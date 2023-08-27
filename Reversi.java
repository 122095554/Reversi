import java.util.Scanner;
/*
 * File: Reversi.java
 * 
 * Holds the gameloop logic for a user playable version of the game of Reversi
 */
public class Reversi {

    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        gameEngine(sc);
        sc.close();
    }

    public static void gameEngine(Scanner sc){
        //user input prompt
        System.out.println("Welcome to Reversi");
        System.out.println("Please choose your game:");
        System.out.println("1. Small 4x4 Reversi");
        System.out.println("2. Medium 6x6 Reversi");
        System.out.println("3. Standard 8x8 Reversi");
        System.out.print("Please enter your choice: ");
        int num = 0;
        
        //assigns choice to num with error checking
        num = numberCheck(num, 1, 3, sc);  

        //depending on user choice, set board size
        ReversiBoard board = null;
        if(num == 1){
            board = new ReversiBoard(4);
        }
        else if(num == 2){            
            board = new ReversiBoard(6);
        }
        else if(num == 3){
            board = new ReversiBoard(8);
        }
        board.initBoard();

        //user input prompt
        System.out.println("Choose your opponent:");
        System.out.println("1. An agent that plays randomly");
        System.out.println("2. An agent that uses MINIMAX with a-b pruning");
        System.out.println("3. An agent that uses H-MINIMAX with a fixed depth cutoff and a-b pruning");
        System.out.print("Please enter your choice: ");
        int num2 = 0;

        //assigns choice to num2 with error checking
        num2 = numberCheck(num2, 1, 4, sc);

        //assigns the cutoff depth if option 2 was chosen
        int cutoff = -1;
        if(num2 == 3){
            System.out.print("Please enter a cutoff (recommmended 10, -1 for no cutoff): ");
            while (!sc.hasNextInt()) {
                System.out.println("That's not a number!");
                System.out.print("Please enter your choice: ");
                sc.next();
            }
            cutoff = sc.nextInt();
        }

        //if cutoff is negative remove cutoff depth, otherwise set it to the user input
        if(cutoff < 0){
            board.setMaxLevel(Integer.MAX_VALUE);
        }
        else{
            board.setMaxLevel(cutoff);
        }
        

        //user input prompt
        System.out.println("Do you want to play DARK (x) or LIGHT (o)? ");
        while (!sc.hasNext("[xo]")){
            System.out.print("Please choose a valid option: ");
            sc.next();
        }
        String color = sc.next();

        Player p1 = null;
        COLOR otherPlayerColor = null;

        //sets p1 color based on user input
        if(color.charAt(0) == 'x'){
            p1 = new Player(board, COLOR.BLACK);
            otherPlayerColor = COLOR.WHITE;
        }
        else if(color.charAt(0) == 'o'){
            p1 = new Player(board, COLOR.WHITE);
            otherPlayerColor = COLOR.BLACK;
        }

        //sets p2 to the color p1 didnt choose
        Player p2 = null;
        if(num2 == 1){
            p2 = new Player(board, otherPlayerColor, 1);
        }
        else if(num2 == 2){            
            p2 = new Player(board, otherPlayerColor, 2);
        }
        else if(num2 == 3){
            p2 = new Player(board, otherPlayerColor, 3);
        }

        //displays initial state of the board
        board.printBoard();

        //main player interaction loop for the game, black goes first
        boolean quit = false;
        if(p1.getColor() == COLOR.BLACK){
            quit = playerTurnLoop(sc, p1, p2, board, true);
        }
        else{
            quit = playerTurnLoop(sc, p1, p2, board, false);
        }

        if(quit){
            System.out.println("Game Over!");
        }
        else{
            int value = board.valueDiffBlack();
            if(value == 0){
                System.out.println("It's a draw!");
            }
            else if(value < 0){
                System.out.println("White wins!");
            }
            else{
                System.out.println("Black wins!");
            }
        }
    }

    public static int numberCheck(int num, int lowerBound, int upperBound, Scanner sc){
        while (!sc.hasNextInt()) {
            System.out.println("That's not a number!");
            System.out.print("Please enter your choice: ");
            sc.next();
        }
        num = sc.nextInt();
        while (num < lowerBound || num > upperBound){
            System.out.print("Please choose a valid option: ");
            while (!sc.hasNextInt()) {
                System.out.println("That's not a number!");
                System.out.print("Please enter your choice: ");
                sc.next();
            }
            num = sc.nextInt();
        }

        return num;
    }

    public static boolean playerTurnLoop(Scanner sc, Player p1, Player p2, ReversiBoard board, boolean isTurn){
        boolean quit = false;
        if(isTurn){
            while(!board.isGameOver()){
                
                //Player Turn actions
                if(board.findValidMoves(p1.getColor().getPiece()).size() == 0){
                    System.out.println("\nNo moves for " + p1.getColor() + " to play! Skipping to " + p2.getColor() + "'s turn\n");
                    System.out.println("Next to play: " + p2.getColor());
                    System.out.println();
                    System.out.println("I'm picking my move...");
                    p2.cpuMove(p2.getPlayerType());
                    board.printBoard();
                    continue;
                }
                System.out.println("Next to play: " + p1.getColor());
                System.out.println();
                System.out.println("Type \"h\" for help or \"q\" to quit");
                System.out.print("Your move (format: (letter)(number) example: \"a2\"): ");
                while (!sc.hasNext("[a-zA-Z][0-9]|q|h")){
                    System.out.print("Please choose a valid option: ");
                    sc.next();
                }
                String play = sc.next();
                char x = 0;
                int y = 0;
                if(play.length() == 2){
                    x = play.charAt(0);
                    y = Character.getNumericValue(play.charAt(1));
                }
                else{
                    if(play.charAt(0) == 'q'){
                        quit = true;
                        break;
                    }
                    else{
                        p1.validMovesHelper();
                        continue;
                    }
                }
                boolean leave = false;
                boolean move = p1.move(x, y);
                while(!move){
                    System.out.println();
                    System.out.print("Please enter a valid move: ");
                    while (!sc.hasNext("[a-zA-Z][0-9]|q|h")){
                    System.out.print("Please choose a valid option: ");
                        sc.next();
                    }  
                    play = sc.next();
                    if(play.length() == 2){
                        x = play.charAt(0);
                        y = Character.getNumericValue(play.charAt(1));
                        move = p1.move(x, y);
                        System.out.println(move);
                    }
                    else{
                        if(play.charAt(0) == 'q'){
                            quit = true;
                            leave = true;
                            break;
                        }
                        else{
                            p1.validMovesHelper();
                            continue;
                        }
                    }
                }
                if(leave){
                    break;
                }      
                board.printBoard();

                //CPU turn actions
                if(board.findValidMoves(p2.getColor().getPiece()).size() == 0){
                    System.out.println("\nNo moves for " + p2.getColor() + " to play! Skipping to " + p1.getColor() + "'s turn\n");
                }
                else{
                    System.out.println("Next to play: " + p2.getColor());
                    System.out.println();
                    System.out.println("I'm picking my move...");
                    p2.cpuMove(p2.getPlayerType());
                    board.printBoard();
                }
            }
        }
        else{
            while(!board.isGameOver()){
                //CPU turn actions
                if(board.findValidMoves(p2.getColor().getPiece()).size() == 0){
                    System.out.println("\nNo moves for " + p2.getColor() + " to play! Skipping to " + p1.getColor() + "'s turn\n");
                }
                else{
                    System.out.println("Next to play: " + p2.getColor());
                    System.out.println();
                    System.out.println("I'm picking my move...");
                    p2.cpuMove(p2.getPlayerType());
                    board.printBoard();
                }

                //Player Turn actions
                if(board.findValidMoves(p1.getColor().getPiece()).size() == 0){
                    System.out.println("\nNo moves for " + p1.getColor() + " to play! Skipping to " + p2.getColor() + "'s turn\n");
                    System.out.println("Next to play: " + p2.getColor());
                    System.out.println();
                    System.out.println("I'm picking my move...");
                    p2.cpuMove(p2.getPlayerType());
                    board.printBoard();
                    continue;
                }
                System.out.println("Next to play: " + p1.getColor());
                System.out.println();
                System.out.println("Type \"h\" for help or \"q\" to quit");
                System.out.print("Your move (format: (letter)(number) example: \"a2\"): ");
                while (!sc.hasNext("[a-zA-Z][0-9]|q|h")){
                    System.out.print("Please choose a valid option: ");
                    sc.next();
                }
                String play = sc.next();
                char x = 0;
                int y = 0;
                if(play.length() == 2){
                    x = play.charAt(0);
                    y = Character.getNumericValue(play.charAt(1));
                }
                else{
                    if(play.charAt(0) == 'q'){
                        quit = true;
                        break;
                    }
                    else{
                        p1.validMovesHelper();
                        continue;
                    }
                }
                boolean leave = false;
                boolean move = p1.move(x, y);
                while(!move){
                    System.out.println();
                    System.out.print("Please enter a valid move: ");
                    while (!sc.hasNext("[a-zA-Z][0-9]|q|h")){
                        System.out.print("Please choose a valid option: ");
                        sc.next();
                    }  
                    play = sc.next();
                    if(play.length() == 2){
                        x = play.charAt(0);
                        y = Character.getNumericValue(play.charAt(1));
                        move = p1.move(x, y);
                        System.out.println(move);
                    }
                    else{
                        if(play.charAt(0) == 'q'){
                            quit = true;
                            leave = true;
                            break;
                        }
                        else{
                            p1.validMovesHelper();
                            continue;
                        }
                    }
                }
                if(leave){
                    break;
                }
                board.printBoard();
            }
        }
        return quit;
    }
}