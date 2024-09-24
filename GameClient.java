import java.io.*;
import java.net.*;
import java.util.Scanner;
import com.google.gson.*;

class GameStatus {
    public boolean gameOn;
    public boolean win;

    char currenPlayer = 'Y';


    public GameStatus(boolean gameOn, boolean win ) {
        this.gameOn = gameOn;
        this.win = win;
    }
}


public class GameClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;
    private static String sessionId = null;

    public static void main(String[] args) {

        if (args.length < 3) {
            System.err.println("Por favor proporciona el nombre de usuario, la contraseña y el símbolo del jugador (X o O).");
            return;
        }

        String username = args[0];
        String password = args[1];
        String playerSymbol = args[2];
        char currenPlayer = 'Y';

        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)) {

            Gson gson = new Gson();

            // Register
            JsonObject requestR = new JsonObject();
            requestR.addProperty("action", "register");
            JsonObject dataR = new JsonObject();
            dataR.addProperty("username", username);
            dataR.addProperty("password", password);
            requestR.add("data", dataR);
            out.println(gson.toJson(requestR));

            // Leer respuesta de Register
            String response = in.readLine();

            if (response != null) {
                JsonObject jsonResponseR = gson.fromJson(response, JsonObject.class);
                String statusR = jsonResponseR.get("status").getAsString();

                if (statusR.equals("success")) {

                    System.out.println(jsonResponseR.getAsJsonObject("data").get("message").getAsString());

                    // Login
                    JsonObject request = new JsonObject();
                    request.addProperty("action", "login");
                    JsonObject data = new JsonObject();
                    data.addProperty("username", username);
                    data.addProperty("password", password);
                    request.add("data", data);
                    out.println(gson.toJson(request));

                    // Leer respuesta de login
                    response = in.readLine();
                    if (response != null) {
                        JsonObject jsonResponse = gson.fromJson(response, JsonObject.class);
                        String status = jsonResponse.get("status").getAsString();

                        if (status.equals("success")) {
                            JsonObject dataRL = jsonResponse.getAsJsonObject("data");
                            sessionId = dataRL.get("sessionId").getAsString();
                            System.out.println("Inicio de sesión exitoso. sessionId: " + sessionId+"\n");
                            System.out.println("");
                            System.out.println("Bienvenido al servidor de Tic-Tac-Toe!"+"\n");

                            GameStatus statusGame = new GameStatus(false, false);

                            new Thread(() -> {
                                try {
                                    String serverMessage;
                                    while ((serverMessage = in.readLine()) != null) {
                                        JsonObject jsonResponseM = gson.fromJson(serverMessage, JsonObject.class);
                                        String action = jsonResponseM.get("status").getAsString();

                                        if (action.equals("updateBoard")) {
                                            // Actualizar el tablero del juego en el cliente
                                            JsonObject dataM = jsonResponseM.getAsJsonObject("data");
                                            String board = dataM.get("board").getAsString();
                                            String nextPlayer = dataM.get("nextPlayer").getAsString();

                                            System.out.println("Tablero actualizado: \n" + board);
                                            System.out.println("Siguiente jugador: " + nextPlayer+"\n");
                                        }

                                        if (action.equals("success")){
                                            // Movimiento exitoso
                                            JsonObject dataM = jsonResponseM.getAsJsonObject("data");
                                            String board = dataM.get("board").getAsString();
                                            String nextPlayer = dataM.get("nextPlayer").getAsString();
                                            statusGame.currenPlayer = nextPlayer.equals("X") ? 'O' : 'X';
                                            statusGame.gameOn = dataM.get("winner").getAsBoolean();

                                            System.out.println("Tablero actual:");
                                            System.out.println(board);
                                            System.out.println("Siguiente jugador: " + nextPlayer+"\n");

                                            // Verificar si hay un ganador
                                            if (statusGame.gameOn) {
                                                statusGame.win = true;
                                                System.out.println("¡El ganador es: " + statusGame.currenPlayer + "!");
                                            }
                                        }

                                        if (action.equals("error")){
                                            // Error en el movimiento
                                            String errorMessage = jsonResponseM.getAsJsonObject("data").get("message").getAsString();
                                            System.out.println("Error: " + errorMessage+"\n");
                                        }

                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }).start();

                            // Comenzar el ciclo de juego
                            while (!statusGame.gameOn) {
                                // Pedir posición al jugador
                                System.out.println("Introduce la fila (0-2) y columna (0-2) para tu movimiento:");
                                int row = scanner.nextInt();
                                int col = scanner.nextInt();

                                // Crear la solicitud para hacer el movimiento
                                JsonObject moveRequest = new JsonObject();
                                moveRequest.addProperty("action", "makeMove");
                                JsonObject moveData = new JsonObject();
                                moveData.addProperty("gameId", sessionId);
                                moveData.addProperty("player", playerSymbol);
                                JsonArray position = new JsonArray();
                                position.add(row);
                                position.add(col);
                                moveData.add("position", position);
                                moveRequest.add("data", moveData);

                                // Enviar el movimiento al servidor
                                out.println(gson.toJson(moveRequest));
                            }
                            if (!statusGame.win)
                            System.out.println("El juego ha terminado en empate." );
                        } else {
                            System.out.println("Error en el inicio de sesión: " + jsonResponse.getAsJsonObject("data").get("message").getAsString()+"\n");
                        }
                    }

                }else{
                    System.out.println("Error en el Registro: " + jsonResponseR.getAsJsonObject("data").get("message").getAsString()+"\n");
                }
            }

            String serverMessage;
            while ((serverMessage = in.readLine()) != null) {
                JsonObject jsonResponseM = gson.fromJson(serverMessage, JsonObject.class);
                String action = jsonResponseM.get("status").getAsString();

                if (action.equals("updateBoard")) {
                    // Actualizar el tablero del juego en el cliente
                    JsonObject dataM = jsonResponseM.getAsJsonObject("data");
                    String board = dataM.get("board").getAsString();
                    String nextPlayer = dataM.get("nextPlayer").getAsString();

                    System.out.println("Tablero actualizado: \n" + board);
                    System.out.println("Siguiente jugador: " + nextPlayer + "\n");
                }
            }

        } catch (IOException e) {
            System.err.println("Error de E/S: " + e.getMessage());
        }
    }
}
