import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    private static List<ClientHandler> clients = new CopyOnWriteArrayList<>();
    private static AuthService authService;
    private static CookieManager cookies;
    private static Game game;


    public static void main(String[] args) throws IOException {
        authService = new AuthService();
        cookies = new CookieManager();
        game = new Game();
        ServerSocket serverSocket = new ServerSocket(12345);
        System.out.println("Servidor iniciado en el puerto 12345...");

        while (true) {
            Socket clientSocket = serverSocket.accept();

            // Crear un nuevo manejador para el cliente y agregarlo a la lista
            ClientHandler clientHandler = new ClientHandler(clientSocket);
            clients.add(clientHandler);

            // Iniciar el hilo del cliente
            clientHandler.start();
        }
    }

    // Método para enviar el mensaje a todos los clientes excepto al que envió la jugada
    private static void broadcastMessage(JsonObject message, Socket excludeSocket) {
        // Iterar sobre todos los clientes
        for (ClientHandler client : clients) {
            if (client.getClientSocket() != excludeSocket) {
                client.sendMessage(message);
            }
        }
    }



    static class ClientHandler extends Thread {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        private Gson gson = new Gson();


        public ClientHandler(Socket socket) throws IOException {
            this.socket = socket;
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);
        }

        @Override
        public void run() {
            try {
               // out.println("Bienvenido al servidor de Tic-Tac-Toe!");
                String message;

                while ((message = in.readLine()) != null) {


                    JsonObject request = gson.fromJson(message, JsonObject.class);
                    String action = request.get("action").getAsString();
                    JsonObject data = request.getAsJsonObject("data");

                    if (action.equals("login")) {
                        handleLogin(data);
                    } else if (action.equals("register")) {
                        handleRegister(data);
                    } else if (action.equals("makeMove")) {
                        String sessionId = data.get("gameId").getAsString();
                        if (cookies.validateSession(sessionId)) {
                            handleMove(data);
                        }else{
                            out.println("Id invalido, inicia sesion");
                        }
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // Si el cliente se desconecta, lo eliminamos de la lista de clientes conectados
                closeConnection();
            }
        }

        // Método para obtener el socket del cliente
        public Socket getClientSocket() {
            return this.socket;
        }

        private void handleLogin(JsonObject data) {
            String username = data.get("username").getAsString();
            String password = data.get("password").getAsString();

            JsonObject response = new JsonObject();

            if (authService.login(username, password)) {
                String sessionId = cookies.createSession(username);

                response.addProperty("status", "success");
                JsonObject dataResponse = new JsonObject();
                dataResponse.addProperty("sessionId", sessionId);
                dataResponse.addProperty("message", "Registro Exitoso");
                response.add("data", dataResponse);
            } else {
                response.addProperty("status", "error");
                JsonObject dataResponse = new JsonObject();
                dataResponse.addProperty("message", "Credenciales incorrectas.");
                response.add("data", dataResponse);
            }
            out.println(gson.toJson(response));
        }

        private void handleRegister(JsonObject data) throws IOException {
            String username = data.get("username").getAsString();
            String password = data.get("password").getAsString();

            JsonObject response = new JsonObject();

            if (authService.register(username, password) || authService.getUsers().containsKey(username)) {
                response.addProperty("status", "success");
                JsonObject dataResponse = new JsonObject();
                if (authService.getUsers().containsKey(username)){
                    dataResponse.addProperty("message", "El usuario estaba previamente registrado");
                }else {
                    dataResponse.addProperty("message", "Usuario registrado correctamente.");
                }
                response.add("data", dataResponse);
            } else {
                response.addProperty("status", "error");
                JsonObject dataResponse = new JsonObject();
                dataResponse.addProperty("message", "Registro Invalido");
                response.add("data", dataResponse);
            }
            out.println(gson.toJson(response));
        }

        private void handleMove(JsonObject data) {
            String player = data.get("player").getAsString();
            JsonArray position = data.getAsJsonArray("position");
            int row = position.get(0).getAsInt();
            int col = position.get(1).getAsInt();

            JsonObject response = new JsonObject();

            System.out.println("Game Current: "+game.getCurrentPlayer()+" Client: "+player.charAt(0));

            if (game.getCurrentPlayer() == player.charAt(0)){
                if (game.makeMove(player, row, col)){

                    response.addProperty("status", "success");
                    JsonObject dataResponse = new JsonObject();
                    dataResponse.addProperty("board", game.getBoardState());
                    dataResponse.addProperty("nextPlayer", game.getCurrentPlayer());
                    dataResponse.addProperty("winner", game.checkWinner());
                    response.add("data", dataResponse);

                    // Crear el mensaje para enviar a los otros clientes
                    JsonObject message = new JsonObject();
                    message.addProperty("status", "updateBoard");
                    message.add("data", dataResponse);

                    broadcastMessage(message, this.socket);
                }else{
                    response.addProperty("status", "error");
                    JsonObject dataResponse = new JsonObject();
                    dataResponse.addProperty("message", "Jugada inválida. Intenta de nuevo.");
                    response.add("data", dataResponse);
                }
            }else{
                response.addProperty("status", "error");
                JsonObject dataResponse = new JsonObject();
                dataResponse.addProperty("message", "Espera tu turno");
                response.add("data", dataResponse);
            }


            out.println(gson.toJson(response));
        }

        // Método para enviar un mensaje al cliente actual
        public void sendMessage(JsonObject response) {
            out.println(gson.toJson(response));
        }

        public void closeConnection() {
            try {
                clients.remove(this); // Eliminamos el cliente de la lista
                if (this.socket != null) this.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
