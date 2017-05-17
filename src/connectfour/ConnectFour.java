package connectfour;

import static org.fusesource.jansi.Ansi.*;
import static org.fusesource.jansi.Ansi.Color.*;
import org.fusesource.jansi.AnsiConsole;

/**
 * This class allows the playing of a virtual game of "Connect Four"
 * This updated version adds colour to the output.
 * Note that this version uses the jansi library which should work by default 
 * on Linux but on windows requires "jansi.dll" to be found in an appropriate 
 * environment path. (Untested on Apple OSX)
 * @author Stephen Whitely P308730
 * @version 2
 */
public class ConnectFour {
    private int[][] board;
    private int turn;
    private int width = 7, height = 6;
    private boolean gameOver;
    // boolean to check if jansi.dll can be loaded
    private static Boolean jansiOkay;
    // colours
    private static Color boardColour = YELLOW;
    private static Color numberColour = GREEN;
    private static Color playerOneColour = RED;
    private static Color playerTwoColour = CYAN;
    private static Color defaultColour = WHITE;
    /**
     * Constructor to create a new instance of a game.<br>
     * This default constructor will randomly select the first player.
     */
    public ConnectFour() {
        board = new int[width][height];
        testJansi();
        startNewGame();
    }
    /**
     * Constructor to create a new instance of a game.<br>
     * This constructor allows you to select the first player.
     * @param firstTurn 0 or 1 - which player goes first.
     */
    public ConnectFour(int firstTurn) {
        board = new int[width][height];
        testJansi();
        startNewGame(firstTurn);
    }
    /**
     * Private helper method to see if jansi libary can be loaded
     */
    private void testJansi() {
        try {
            // test to see if jansi library can be loaded, if not fall back to
            // not coloured output. Note that with each method that uses the 
            // library that the AnsiConsole is installed and uninstalled because
            // currently the program has no exit point so it can't cleanly 
            // unistall it on termination.
             AnsiConsole.systemInstall();
             AnsiConsole.systemUninstall();
             jansiOkay = true;
        } catch (UnsatisfiedLinkError e) {
            System.out.println("Jansi library not loaded. No colour on output");
            //System.out.println(e);
            jansiOkay = false;
        }
    }
    /**
     * Start a new game with a randomly selected first player.
     */
    public final void startNewGame() {
        clearBoard();
        if (Math.random() < 0.5) {
            turn = 0;
        } else {
            turn = 1;
        }
        gameOver = false;
        System.out.println("NEW GAME");
        displayBoard();
    }
    /**
     * Start a new game with a user selected first player.
     * @param firstTurn 0 or 1 - which player goes first
     */
    public final void startNewGame(int firstTurn) {
        clearBoard();
        // if firstTurn isn't valid set to Player 1 (0)
        if (firstTurn < 0 && firstTurn > 1) firstTurn = 0;
        turn = firstTurn;
        gameOver = false;
        System.out.println("NEW GAME");
        displayBoard();
    }
    /**
     * Private helper function to clear the board for a new game.
     */
    private void clearBoard() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                board[j][i] = -1;
            }
        }
    }
    /**
     * Display the board by printing it out to the console.
     */
    public void displayBoard() {
        if (jansiOkay) {
            AnsiConsole.systemInstall();
            System.out.println(ansi().bg(BLACK));
            System.out.println(ansi().fg(numberColour) + "  1 2 3 4 5 6 7  ");
            System.out.println(ansi().fg(boardColour) + "_________________");
        } else {
            System.out.println("  1 2 3 4 5 6 7");
            System.out.println("_________________");
        }
        for (int i = height-1; i >= 0; i--) {
            System.out.print("||");
            for (int j = 0; j < width; j++) {
                if (board[j][i] == 0) {
                    if (jansiOkay) {
                        System.out.print(ansi().fg(playerOneColour).a("X"));
                        System.out.print(ansi().fg(boardColour).a("|"));
                    } else {
                        System.out.print("X|");
                    }
                } else if (board[j][i] == 1) {
                    if (jansiOkay) {
                        System.out.print(ansi().fg(playerTwoColour).a("O"));
                        System.out.print(ansi().fg(boardColour).a("|"));
                    } else {
                        System.out.print("O|");
                    }
                } else {
                 System.out.print("_|");
                }
            }
            System.out.println("|");
        }
        if (jansiOkay) {
            System.out.println("TTTTTTTTTTTTTTTTT" + ansi().fg(defaultColour));
            AnsiConsole.systemUninstall();
        } else {
            System.out.println("TTTTTTTTTTTTTTTTT");
        }
    }
    /**
     * Get which players turn it is. Returns 0 for Player 1, 1 for Player 2 or
     * -1 for no player (eg - game has ended).
     * @return An int representing the player whose turn it is <br>
     * 0 - player 1<br>
     * 1 - player 2<br>
     * -1 - neither player
     */
    public int getTurn() {
        return turn;
    }
    /**
     * This method plays a move in the game.
     * @param player the player making the move
     * @param play the column to place the player token
     * @return true if the move was played, false if the move was rejected
     */
    public boolean playMove(int player, int play){
        if (player != turn) {
            System.out.println("It is not your turn.");
            return false;
        }
        if (play < 1 || play > width) {
            System.out.println("Not a valid move.");
            return false;
        }
        if (jansiOkay) AnsiConsole.systemInstall();
        for (int i = 0; i < height; i++) {
            if (board[play - 1][i] == -1) {
                board[play - 1][i] = player;
                if (jansiOkay) {
                    System.out.println(ansi().fg((turn == 0)?playerOneColour:playerTwoColour) +
                            "\n\nPlayer " + (turn + 1) + ansi().fg(defaultColour) + 
                            " placed their token in " + 
                            ansi().fg(numberColour) + "column " + play +
                            ansi().fg(defaultColour));
                } else {
                    System.out.println("\n\nPlayer " + (turn + 1) + 
                            " placed their token in column " + play);
                }
                turn = (turn + 1) % 2;
                displayBoard();
                int winner = checkWinner();
                if (winner == -2) {
                    System.out.println("GAME OVER!\nDrawn game.");
                    gameOver = true;
                    turn = -1;
                } else if (winner != -1) {
                    if (jansiOkay) {
                        System.out.println("GAME OVER!\nWinner is " + 
                                ansi().fg((winner == 0)?playerOneColour:playerTwoColour) +
                                "Player " + (winner + 1) + ansi().fg(defaultColour));
                    } else {
                        System.out.println("GAME OVER!\nWinner is Player " 
                                + (winner + 1));
                    }
                    gameOver = true;
                    turn = -1;
                }
                if (jansiOkay) AnsiConsole.systemUninstall();
                return true;
            }
        }
        System.out.println("That column is full.");
        if (jansiOkay) AnsiConsole.systemUninstall();
        return false;
    }
    /**
     * Checks to see if the game has ended.
     * @return true if the game is over
     */
    public boolean isGameOver() {
        return gameOver;
    }
    /**
     * Private helper function to determine if the board contains a winning play
     * @return 0 if player 1 wins, 1 if player 2 wins, -1 for no winner yet,
     * -2 for a draw
     */
    private int checkWinner() {
        // set to negative one for no winner
        int winner = -1;
        // check for draw
        boolean draw = true;
        for (int i = 0; i < width; i++) {
            if (board[i][height - 1] == -1) {
               draw = false;
               break;
            }
        }
        if (draw) {
            return -2;
        }
        // check horizontal
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width - 3; j++)
                if (board[j][i] == board[j+1][i] &&
                        board[j][i] == board[j+2][i] &&
                        board[j][i] == board[j+3][i]) {
                    winner = board[j][i];
                    if (winner != -1) {
                        //System.out.println("HORIZONTAL " + i + " " + j);
                        return winner;
                    }
                }
        }
        // check vertical
        for (int i = 0; i < height - 3; i++) {
            for (int j = 0; j < width; j++)
                if (board[j][i] == board[j][i+1] &&
                        board[j][i] == board[j][i+2] &&
                        board[j][i] == board[j][i+3]) {
                    winner = board[j][i];
                    if (winner != -1) {
                        //System.out.println("VERTICAL " + i + " " + j);
                        return winner;
                    }
                }
        }
        // check diagonal one
        for (int i = 0; i < height - 3; i++) {
            for (int j = 0; j < width - 3; j++)
                if (board[j][i] == board[j+1][i+1] &&
                        board[j][i] == board[j+2][i+2] &&
                        board[j][i] == board[j+3][i+3]) {
                    winner = board[j][i];
                    if (winner != -1) {
                        //System.out.println("DIAGONAL 1 " + i + " " + j);
                        return winner;
                    }
                }
        }
        // check diagonal two
        for (int i = 0; i < height - 3; i++) {
            for (int j = 3; j < width; j++)
                if (board[j][i] == board[j-1][i+1] &&
                        board[j][i] == board[j-2][i+2] &&
                        board[j][i] == board[j-3][i+3]) {
                    winner = board[j][i];
                    if (winner != -1) {
                        //System.out.println("DIAGONAL 2 " + i + " " + j);
                        return winner;
                    }
                }
        }
        return winner;
    }

    /**
     * The main method for this class is simply a test where the computer 
     * randomly plays out until the game ends by win or draw.
     * @param args the command line arguments don't do anything
     */
    public static void main(String[] args) {
        ConnectFour c4 = new ConnectFour();
        // play out randomly until game over
        while(!c4.isGameOver()) {
            c4.playMove(c4.getTurn(), (int)(Math.random() * 7 + 1));
        }
        // manual test for drawn game
        /*
        c4.playMove(0, 1);
        c4.playMove(1, 2);
        c4.playMove(0, 1);
        c4.playMove(1, 2);
        c4.playMove(0, 1);
        c4.playMove(1, 2);
        c4.playMove(0, 3);
        c4.playMove(1, 4);
        c4.playMove(0, 3);
        c4.playMove(1, 4);
        c4.playMove(0, 3);
        c4.playMove(1, 4);
        c4.playMove(0, 5);
        c4.playMove(1, 6);
        c4.playMove(0, 5);
        c4.playMove(1, 6);
        c4.playMove(0, 5);
        c4.playMove(1, 6);
        c4.playMove(0, 2);
        c4.playMove(1, 1);
        c4.playMove(0, 2);
        c4.playMove(1, 1);
        c4.playMove(0, 2);
        c4.playMove(1, 1);
        c4.playMove(0, 4);
        c4.playMove(1, 3);
        c4.playMove(0, 4);
        c4.playMove(1, 3);
        c4.playMove(0, 4);
        c4.playMove(1, 3);
        c4.playMove(0, 6);
        c4.playMove(1, 5);
        c4.playMove(0, 6);
        c4.playMove(1, 5);
        c4.playMove(0, 6);
        c4.playMove(1, 5);
        c4.playMove(0, 7);
        c4.playMove(1, 7);
        c4.playMove(0, 7);
        c4.playMove(1, 7);
        c4.playMove(0, 7);
        c4.playMove(1, 7);
        */
        
        
        
        
    }
    
}
