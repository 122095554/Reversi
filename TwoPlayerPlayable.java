import java.util.List;
/*
 * File: TwoPlayerPlayable.java
 * 
 * Holds the functionality of a formal model of a two-player, perfect knowledge, zero sum game.
 */
public interface TwoPlayerPlayable {
    //Initial State, how the game is set up
    public void initBoard();
    //Player, defines which player has the move
    public char hasCurrentTurn();
    //Actions, returns legal moves
    public List<ReversiBoard> findValidMoves(char playerChar);
    //Transition model, defines the results of a move
    public boolean movePiece(int x, int y, char playerChar);
    //Terminal test, determines if the game is over
    public boolean isGameOver();
    //Utility function, dertimes the numberic value of a terminal state
    public int getValue(char playerChar);
}
