/*
 * File: COLOR.java
 * 
 * Representation of a board for a game of a player's color as in a board game.
 */
public enum COLOR{
    BLACK('x', "BLACK"),
    WHITE('o', "WHITE");

    private char piece;
    private String name;
    
    COLOR(char c, String label) {
        piece = c;
        name = label;
    }

    public char getPiece() {
        return piece;
    }

    public String toString(){
        return name;
    }
}