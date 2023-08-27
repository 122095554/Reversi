import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * File: ReversiBoard.java
 * 
 * Representation of a board for a game of Reversi. Implements TwoPlayerPlayable that
 * follows the formal model of a two-player, perfect knowledge, zero sum game.
 */
public class ReversiBoard implements TwoPlayerPlayable{
    
    private int gameSize;
    private char playerTurn;
    private char[][] board;
    private ReversiBoard bestMove;

    public static int maxLevel;

    public ReversiBoard(int gameSize){
        this.gameSize = gameSize;
        this.board = new char[gameSize][gameSize];
        maxLevel = 5;
        playerTurn = 'x';
        bestMove = null;
        initBoard();
    }

    public ReversiBoard(int gameSize, char[][] board, char playerChar){
        this.gameSize = gameSize;
        this.board = board;
        this.playerTurn = playerChar;
    }

    /**
     * -------------GAMEPLAY CODE------------------
     */

    public void initBoard(){
        for(int i = 0; i<board.length; i++){
            for(int j = 0; j<board[i].length; j++){
                board[i][j] = 32;
            }
        }
    
        int rightMidLength = gameSize/2;
        int leftMidLength = gameSize/2 - 1;
        int topMidWidth = gameSize/2 - 1;
        int bottomMidWidth = gameSize/2;
    
        board[leftMidLength][bottomMidWidth] = 'x';
        board[leftMidLength][topMidWidth] ='o';
        board[rightMidLength][bottomMidWidth] ='o';
        board[rightMidLength][topMidWidth] = 'x';
    }

    
    public boolean movePiece(int x, int y, char playerChar){
        char otherChar;
        if(playerChar == 'x'){
            otherChar = 'o';
        }
        else{
            otherChar = 'x';
        } 

        int newX = x;
        int newY = y;

        //location already occupied or is off the board
        if(x < 0 || x >= gameSize || y < 0 || y >= gameSize || board[newX][newY] == 'o'|| board[newX][newY] == 'x') {
            return false;
        }

        boolean validPlay = false;

        for(int i = -1; i<2; i++){
            for(int j = -1; j<2; j++){
                boolean passedOtherChar = false;
                boolean flippable = false;
                int directionCount = 1;


                while(newX + (i * directionCount) >= 0 && newX + (i *directionCount) < gameSize && newY + (j * directionCount) >= 0 
                    && newY + (j * directionCount) < gameSize){
                        
                        if(board[newX + (i * directionCount)][newY + (j * directionCount)] == (char)32 || 
                            board[newX + (i * directionCount)][newY + (j * directionCount)] == playerChar && passedOtherChar == false){
                                break;
                        }

                        if(board[newX + (i * directionCount)][newY + (j * directionCount)] == playerChar && passedOtherChar == true){
                            flippable = true;
                            break;
                        }
                        else if(board[newX + (i * directionCount)][newY + (j * directionCount)] == otherChar){
                            passedOtherChar = true;
                            directionCount++;
                        }
                }

                if(flippable){
                    for(int flipCount = 1; flipCount<directionCount; flipCount++){
                        board[newX + (i * flipCount)][newY + (j * flipCount)] = playerChar;
                    }
                    board[newX][newY] = playerChar;
                    validPlay = true;
                }
                
            }
        }
        if(validPlay){
            playerTurn = otherChar;
            return validPlay;
        }
        else{
            return validPlay;
        }
    }

    public boolean safeMove(int x, int y, char playerChar){
        char[][] state = copyGameState(gameSize, board);
        char otherChar;
        if(playerChar == 'x'){
            otherChar = 'o';
        }
        else{
            otherChar = 'x';
        } 

        if(movePiece(x, y, playerChar)){
            return true;
        }
        else{
            board = state;
            printBoard();
            return false;
        }
    }

    public List<ReversiBoard> findValidMoves(char playerChar){
        ArrayList<ReversiBoard> validMovesList = new ArrayList<ReversiBoard>();
        ReversiBoard nextMoveBoard = new ReversiBoard(gameSize, copyGameState(gameSize, board), playerChar);
        
        for(int i = 0; i<gameSize; i++){
            for(int j = 0; j<gameSize; j++){
                if(nextMoveBoard.movePiece(i, j, playerChar)){
                    validMovesList.add(nextMoveBoard);
                    nextMoveBoard = new ReversiBoard(gameSize, copyGameState(gameSize, board), playerChar);
                }
            }
        }
        
        return validMovesList;
    }

    public List<List<Integer>> storeValidMoves(char playerChar){
        ArrayList<List<Integer>> validMovesList = new ArrayList<List<Integer>>();
        ReversiBoard nextMoveBoard = new ReversiBoard(gameSize, copyGameState(gameSize, board), playerChar);
        ArrayList<Integer> boardLocation = new ArrayList<Integer>();

        for(int i = 0; i<gameSize; i++){
            for(int j = 0; j<gameSize; j++){
                if(nextMoveBoard.movePiece(i, j, playerChar)){
                    boardLocation.add(i);
                    boardLocation.add(j);
                    validMovesList.add(boardLocation);
                    nextMoveBoard = new ReversiBoard(gameSize, copyGameState(gameSize, board), playerChar);
                    boardLocation = new ArrayList<Integer>();
                }
            }
        }

        return validMovesList;
    }

    
    public static char[][] copyGameState(int size, char[][] currentBoard){
        char[][] copiedBoard = new char[size][size];

        for(int i = 0; i<currentBoard.length; i++){
            for(int j = 0; j<currentBoard[i].length; j++){
                copiedBoard[i][j] = currentBoard[i][j];
            }
        }

        return copiedBoard;
    }

    /**
     * -----------MINIMAX CODE-------------
     */

    /**
     * Recursively implements the H-Minimax algorithm as a variant of state spaced adversarial search to find the best move 
     * for the given player. Includes to alpha beta pruning and a heuristic to find the value of a board at a cutoff state.
     * 
     * @param maxPlayer if the current iteration of the method is true in wanting to maximize the value for the player, 
     *                  minimizing value if false. Initial call should make this true.
     * @param level Keeps track of the current depth of the tree. Inital call should make this 0.
     * @param alpha Holds the value of alpha in the context of alpha-beta pruning. Initial call should make this Integer.Min_Value.
     * @param beta Holds the value of beta in the context of alpha-beta pruning. Initial call should make this Integer.Max_Value.
     * @param board Holds the current board. Initial call should make this the given board.
     * @return Returns the value of the board.
     */
    public int minimax (boolean maxPlayer, int level, int alpha, int beta, ReversiBoard board){
        //reached the cutoff level
        if(level > maxLevel){
            return board.reversiHeuristic(board.playerTurn);
        }

        //holds the list of valid moves
        List<ReversiBoard> children;

        //finds children for the correct color
        if(board.playerTurn == 'x'){
            if(maxPlayer){
                children = board.findValidMoves('x'); 
            }
            else{
                children = board.findValidMoves('o'); 
            }
        }
        else{
            if(maxPlayer){
                children = board.findValidMoves('o'); 
            }
            else{
                children = board.findValidMoves('x'); 
            }
        }
        
        //if no more moves, return value
        if(board.playerTurn == 'x'){
            if(children.size() == 0){
                if(maxPlayer){
                    return board.getValue('x');
                }
                else{
                    return -1 * board.getValue('x');
                }
            }
        }
        else{
            if(children.size() == 0){
                if(maxPlayer){
                    return board.getValue('o');
                }
                else{
                    return -1 * board.getValue('o');
                }
            }
        }
      

        //If the current iteration aims to find max value
        if(maxPlayer){
            int max = Integer.MIN_VALUE;
            int bestMoveLocation = 0;

            //run minimax for each children
            for(int i = 0; i<children.size(); i++){
                int keep = minimax(false, level + 1, alpha, beta, children.get(i));
                max = Math.max(max, keep);
                if(keep > alpha){
                    alpha = keep;
                    bestMoveLocation = i;
                }
                if(beta <= alpha){
                    break;
                }
            }

            //back at top of tree
            if(level == 0){
                bestMove = children.get(bestMoveLocation);
            }

            return max;
        }
        //if current iteration aims to minimize value
        else{
            int min = Integer.MAX_VALUE;
            for(int i = 0; i<children.size(); i++){
                int keep = minimax(true, level + 1, alpha, beta, children.get(i));
                min = Math.min(min, keep);
                if(keep < beta){
                    beta = keep;
                }                
            }
        }
        return 0;
    }

    /**
     * Function that changes the current board to the best move as per minimax()
     */
    public void cpuMinimax(){
        board = bestMove.getBoard();
        if(playerTurn == 'x'){
            playerTurn = 'o';
        }
        else{
            playerTurn = 'x';
        }
    }

    /**
     * -----------STATE OF GAME CODE--------------
     */
    
    /**
     * Returns if the game is over. i.e. when there is no more valid moves
     * @return true if there are no more valid moves, false if there is
     */
    public boolean isGameOver() {
        if(findValidMoves('x').size() == 0 && findValidMoves('o').size() == 0){
            return true;
        }
        else{
            return false;
        }
    }

    /**
     * Heurisitc function that calculates the value of the current board state, 
     * in favor of the given player. Counts number of pieces and weighs corners highly
     * 
     * @param playerChar given player character to calculate in favor of
     * @return returns the value of the board
     */
    public int reversiHeuristic(char playerChar){
        char otherChar;
        if(playerChar == 'x'){
            otherChar = 'o';
        }
        else{
            otherChar = 'x';
        } 
        //corner piece is valued, number of captured vs enemy captures
        int val = 0;
        if(board[0][0] == playerChar || board[gameSize-1][gameSize-1] == playerChar|| 
            board[gameSize-1][0] == playerChar || board[0][gameSize-1] == playerChar){
                if(board[0][0] == playerChar){
                    val++;
                }
                else if(board[0][0] == otherChar){
                    val--;
                }
                if(board[gameSize-1][gameSize-1] == playerChar){
                    val++;
                }
                else if(board[gameSize-1][gameSize-1] == otherChar){
                    val--;
                }
                if(board[gameSize-1][0] == playerChar){
                    val++;
                }
                else if(board[gameSize-1][0] == otherChar){
                    val--;
                }
                if(board[0][gameSize-1] == playerChar){
                    val++;
                }
                else if(board[0][gameSize-1] == otherChar){
                    val--;
                }
            if(playerChar == 'x'){
                return valueDiffBlack() + val*gameSize;
            }
            else{
                return valueDiffWhite() + val*gameSize;
            }
        }
        else{
            if(playerChar == 'x'){
                return valueDiffBlack();
            }
            else{
                return valueDiffWhite();
            }
        }
    }

    //Gets the difference in number of pieces in favor of black
    public int valueDiffBlack(){
        return getValue('x') - getValue('o');
    }
        
    //Gets the difference in number of pieces in favor of white
    public int valueDiffWhite(){
        return getValue('o') - getValue('x');
    }


    /**
     * Finds the value of the board in favor the the given player character,
     * does this by counting the number of pieces of that color on the board
     * 
     * @param playerChar piece that will have its value calculated
     * @return returns the number of pieces of given playerChar
     */
    public int getValue(char playerChar) {
        int val = 0;
        for(int i = 0; i<board.length; i++){
            for(int j = 0; j<board[i].length; j++){
                if(board[i][j] == playerChar){
                    val++;
                }
            }
        }
        return val;
    }

    /**
     * -----------BOARD PRINTING CODE--------------
     */


    /**
     * Prints out the board with formatted border
     */
    public void printBoard(){
        printHorizontalBorder();
        printBoardState();
        printHorizontalBorder();
    }

    /**
     * Helper funtion for printBoard() that prints the alphabetical borders
     */
    public void printHorizontalBorder(){
        System.out.print("  ");
        for(int i = 97; i<board.length + 97; i++){
            if(i == board.length+96){
                System.out.print((char)i);
            }
            else{
                System.out.print((char)i + "| ");
            }
        }
        System.out.println();
    }

    /**
     * Helper function for printBoard() that prints the current board state
     */
    public void printBoardState(){
        printLine();
        for(int i = 0; i<board.length; i++){
            for(int j = 0; j<board[i].length; j++){
                if(j == 0){
                    System.out.print((i + 1) + " ");
                    System.out.print(board[j][i] + "| ");
                }
                else if(j == board[i].length - 1){
                    System.out.print(board[j][i] + " ");
                    System.out.print((i + 1) + " ");
                }
                else{
                    System.out.print(board[j][i] + "| ");
                }                
            }
            System.out.println();
            printLine();
        }
    }

    /**
     * Helper method for printBoardState() that prints out the horizontal lines
     */
    public void printLine(){
        System.out.print(" ");
        for(int i = 0; i<gameSize; i++){
            System.out.print("___");
        }
        System.out.println();
    }

     /**
     * -----------GETTERS AND SETTERS--------------
     */

    public int getGameSize() {
        return gameSize;
    }

    public ReversiBoard getBestMove() {
        return bestMove;
    }
    
    private char[][] getBoard() {
        return board;
    }

    public static int getMaxLevel() {
        return maxLevel;
    }
    
    public void setMaxLevel(int maxLevel) {
        ReversiBoard.maxLevel = maxLevel;
    }

    public char hasCurrentTurn() {
        return playerTurn;
    }

    public void setPlayerTurn(char playerTurn) {
        this.playerTurn = playerTurn;
    }

    @Override
    public String toString(){
        return Arrays.deepToString(board);
    }
}