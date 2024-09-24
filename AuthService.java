import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class AuthService {
    private final String usersFile = "users.txt";
    private Map<String, String> users = new HashMap<>();

    public AuthService() throws IOException {
        loadUsers();
    }

    public Map<String, String> getUsers(){
        return users;
    }

    // Cargar usuarios desde el archivo
    private void loadUsers() throws IOException {
        File file = new File(usersFile);
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(":");
                    if (parts.length == 2) {
                        users.put(parts[0], parts[1]); // Usuario y contraseña cifrada
                    }
                }
            }
        }
    }

    // Registro de usuario
    public boolean register(String username, String password) throws IOException {
        if (users.containsKey(username)) {
            return false; // El usuario ya existe
        }
        users.put(username, hashPassword(password));
        saveUser(username, hashPassword(password));
        return true;
    }

    // Guardar un nuevo usuario en el archivo
    private void saveUser(String username, String password) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(usersFile, true))) {
            bw.write(username + ":" + password);
            bw.newLine();
        }
    }

    // Inicio de sesión
    public boolean login(String username, String password) {
        String hashedPassword = users.get(username);
        System.out.println("user: "+username+" password: "+password+ " sifrado: "+hashedPassword);

        return hashedPassword != null && hashedPassword.equals(hashPassword(password));
    }

    // Método para cifrar la contraseña (simplificado)
    private String hashPassword(String password) {
        return Integer.toString(password.hashCode());
    }
}
