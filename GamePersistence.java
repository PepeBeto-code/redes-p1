import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;

public class GamePersistence {

    public static void saveGame(GameState gameState, String filePath) {
        Gson gson = new Gson();
        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(gameState, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static GameState loadGame(String filePath) {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(filePath)) {
            return gson.fromJson(reader, GameState.class);
        } catch (FileNotFoundException e) {
            // Si el archivo no existe, devolvemos null e imprimimos un mensaje
            System.out.println("Archivo no encontrado, se iniciará un nuevo juego.");
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
