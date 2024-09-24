public class Game {
    private char[][] board = new char[3][3];
    private char currentPlayer = 'X';
    private static final String SAVE_FILE_PATH = "game_state.json";

    public String[][] convertCharArrayToStringArray(char[][] charArray) {
        int rows = charArray.length;
        int cols = charArray[0].length;
        String[][] stringArray = new String[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                stringArray[i][j] = String.valueOf(charArray[i][j]);
            }
        }
        return stringArray;
    }


    public Game() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = ' ';
            }
        }
        loadGame();
    }

    public boolean makeMove(String  player,int row, int col) {
        if (row < 0 || col < 0 || row > 2 || col > 2 || board[row][col] != ' ') {
            return false;
        }

        board[row][col] = player.charAt(0);
        currentPlayer = player.equals("X") ? 'O' : 'X';
        saveGame(); // Guardar el estado después de cada movimiento
        return true;
    }

    public char[][] getBoard() {
        return board;
    }

    public char getCurrentPlayer() {
        return currentPlayer;
    }

    public boolean checkWinner() {
        for (int i = 0; i < 3; i++) {
            if (board[i][0] != ' ' && board[i][0] == board[i][1] && board[i][1] == board[i][2]) return true;
            if (board[0][i] != ' ' && board[0][i] == board[1][i] && board[1][i] == board[2][i]) return true;
        }
        if (board[0][0] != ' ' && board[0][0] == board[1][1] && board[1][1] == board[2][2]) return true;
        if (board[0][2] != ' ' && board[0][2] == board[1][1] && board[1][1] == board[2][0]) return true;
        return false;
    }

    // Método para obtener el estado del tablero
    public String getBoardState() {
        StringBuilder sb = new StringBuilder();
        String lineSeparator = System.lineSeparator();  // Obtener el separador de líneas adecuado
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                sb.append(board[i][j]);
                if (j < 2) sb.append("|");
            }
            sb.append(lineSeparator);  // Usar separador de línea adecuado
        }
        return sb.toString();
    }

    // Guardar el estado del juego
    public void saveGame() {
        GameState gameState = new GameState(convertCharArrayToStringArray(this.board), String.valueOf(this.currentPlayer));
        GamePersistence.saveGame(gameState, SAVE_FILE_PATH);
    }

    public char[][] convertStringArrayToCharArray(String[][] stringArray) {
        int rows = stringArray.length;
        int cols = stringArray[0].length;
        char[][] charArray = new char[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                charArray[i][j] = stringArray[i][j].charAt(0);
            }
        }
        return charArray;
    }

    // Cargar el estado del juego
    public void loadGame() {
        GameState gameState = GamePersistence.loadGame(SAVE_FILE_PATH);
        if (gameState != null) {
            this.board = convertStringArrayToCharArray(gameState.getBoard());
            this.currentPlayer = gameState.getNextPlayer().charAt(0);
        }
    }

}
