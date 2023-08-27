import java.util.List;
import java.util.Random;
/*
 * File: Player.java
 * 
 * Representation of a player for a Reversi game.
 */
public class Player {

    /**
     * REMOVE MENTIONS OF HAS CURRENT TURN and player
     */
    private ReversiBoard board;
    private COLOR color;
    private int playerType;

    public Player(ReversiBoard board, COLOR color){
        this.board = board;
        this.color = color;
        playerType = 0;
    }
    
    public Player(ReversiBoard board, COLOR color, int playerType){
        this.board = board;
        this.color = color;
        this.playerType = playerType;
    }

    /**
     * Chooses how the CPU plays depending on playerType
     * 
     * @param playerType player type, 1 corresponds to random, 2 corresponds to minimax AI
     * @return returns if the move is value
     */
    public boolean cpuMove(int playerType){
        if(playerType == 1){
            return randomMove();
        }
        else if(playerType == 2){
            board.minimax(true, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, board);
            board.cpuMinimax();
            return true;
        }
        else if(playerType == 3){
            board.minimax(true, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, board);
            board.cpuMinimax();
            return true;
        }
        return false;
    }

    /**
     * Lets a player move their piece in the form of (letter)(numer) 
     * Example: a2
     * 
     * @param x character for the x axis, as displayed by the board
     * @param y integer for the y axis, as displayed by the board
     * @return returns if the move is valid, true if true, false otherwise
     */
    public boolean move(char x, int y) {
        boolean check = board.safeMove(((int)x-97), y-1, color.getPiece());
        return check;
    }

    /**
     * Picks a random valid move picks it as a move
     * 
     * @return returns if the move is valid
     */
    public boolean randomMove(){
        List<List<Integer>> moves = board.storeValidMoves(color.getPiece());
        if(moves.size() <= 0){
            return false;
        }
        Random r = new Random();
        int move = r.nextInt(moves.size());
        
        return board.movePiece(moves.get(move).get(0), moves.get(move).get(1), color.getPiece());
    }

    public void validMovesHelper(){
        List<List<Integer>> moves = board.storeValidMoves(color.getPiece());
        System.out.print("[");
        for(List<Integer> list: moves){
            int i = list.get(0) + 97;
            System.out.print("[" + (char)i + ", " + (list.get(1)+1) + "]");
        }
        System.out.print("]\n");
    }

    /**
     * ---------GETTERS----------
     */
    
    public COLOR getColor(){
        return color;
    }

    public int getPlayerType() {
        return playerType;
    }
}
