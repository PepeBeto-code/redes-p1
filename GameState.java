import java.util.Arrays;

public class GameState {
    private String[][] board;
    private String nextPlayer;

    public GameState(String[][] board, String nextPlayer) {
        this.board = board;
        this.nextPlayer = nextPlayer;
    }

    public String[][] getBoard() {
        return board;
    }

    public String getNextPlayer() {
        return nextPlayer;
    }

    @Override
    public String toString() {
        return "GameState{" +
                "board=" + Arrays.deepToString(board) +
                ", nextPlayer='" + nextPlayer + '\'' +
                '}';
    }
}
