package connectfour;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * This class acts as the client to play the ConnectFour game with the 
 * ConnectFourServer. <br>
 * Player 1 runs the server code and Player 2 runs the client code.<br>
 * Both server and client maintain their own ConnectFour object.
 * @author Stephen Whitely
 */
public class ConnectFourClient {
    static ConnectFour c4;
    
    static Socket socket;
    static DataOutputStream outStream;
    static BufferedReader inStream;
    /**
     * The main method of this class runs the client for Player 2 to play when
     * connecting to another player running the ConnectFourServer.
     * @param args user can add two arguments for host and port or leave blank
     * to use defaults
     */
    public static void main(String[] args) {
        //c4 = new ConnectFour(-1);
        String host;
        int port;
        // check that user has put in args for server host and port
        if (args.length == 0) {
            System.out.println("Using default host 'localhost' and port# 1234");
            host = "localhost";
            port = 1234;
        } else {

            if (args.length != 2) {
                System.out.println("Incorrect arguments used.");
                System.out.println("Usage: java squarenumber.SquareNumberClient hostName port#");
                System.exit(1);
            }
            // get server host and port
            host = args[0];
            port = Integer.valueOf(args[1]).intValue();
        }
        // try to create sockets and data streams
        try {
            socket = new Socket(host, port);
            inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outStream = new DataOutputStream(socket.getOutputStream());
        } catch (UnknownHostException e) {
            System.out.println("Error connecting. Check host and port.\n" + e);
            System.exit(1);
        } catch (IOException e) {
            System.out.println(e);
            System.exit(1);
        }
        // scanner for user input from console
        Scanner sc = new Scanner(System.in);
        // loop to communicate with server
        while (true) {
            try {
                if (c4 == null || c4.isGameOver()) {
                    String message = inStream.readLine();
                    try {
                        int command = Integer.parseInt(message);
                        if (command == 0 || command == 1) {
                            if (c4 == null) {
                                c4 = new ConnectFour(command);
                            } else {
                                c4.startNewGame(command);
                            }
                        } else {
                            continue;
                        }
                    } catch (NumberFormatException nfe) {
                        System.out.println(nfe.toString());
                        continue;
                    }
                }
                if (c4.getTurn() == 0) {
                    String message = inStream.readLine();
                    try {
                        int command = Integer.parseInt(message);
                        if (command > 0 && command < 8) {
                            c4.playMove(0, command);
                        } else {
                            continue;
                        }
                    } catch (NumberFormatException nfe) {
                        continue;
                    }
                }
                if (c4.getTurn() == 1) {
                    System.out.println("Enter your next move.");
                    String userInput = sc.nextLine();
                    if (userInput.length() > 0) {
                        try {
                            int command = Integer.parseInt(userInput);
                            if (command > 0 && command < 8) {
                                c4.playMove(1, command);
                                sendMessage(userInput);
                            } else {
                                continue;
                            }
                        } catch (NumberFormatException nfe) {
                            System.out.println("Please enter a number from 1 to 7");
                            continue;
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println(e);
                System.out.println("IO error occured. Cient exiting.");
                break;
            }
        }
        // close sockets and streams
        try {
            socket.close();
            inStream.close();
            outStream.close();
        } catch (IOException e) {
            System.out.println(e);
            System.out.println("Oh no. Error closing sockets!");
        }
    }
    /**
     * A private method for the client to send a message to the server.
     * @param message the message to send on the output stream
     * @throws IOException 
     */
    private static void sendMessage(String message) throws IOException {
        outStream.writeBytes(message);
        outStream.write(13);
        outStream.write(10);
        outStream.flush();

    }
}
