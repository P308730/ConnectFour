package connectfour;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * This class acts as a server to play the ConnectFour game. <br>
 * Player 1 runs the server code and Player 2 runs the client code.<br>
 * Both server and client maintain their own ConnectFour object.
 * @author Stephen Whitely P308730
 */
public class ConnectFourServer {
    private static int socket = 1234;
    private static DataOutputStream outStream;
    private static BufferedReader inStream;
    private static ConnectFour c4;
    /**
     * The main method runs the server for Player 1 to play and for Player 2 to
     * connect to via the ConnectFourClient class.
     * @param args do nothing
     */
    public static void main(String[] args) {
        // try (with resource) to create ServerSocket
        try (ServerSocket serverSocket = new ServerSocket(socket)) {
            System.out.println("Server is listening on port #"
                    + serverSocket.getLocalPort());
            // try (with resource) to wait and listen on port
            try (Socket clientSocket = serverSocket.accept()) {
                // get client name and port
                String clientHostName = clientSocket.getInetAddress().getHostName();
                int clientPortNumber = clientSocket.getLocalPort();
                System.out.println("Connected from " + clientHostName
                        + " on port #" + clientPortNumber);
                // create input stream
                inStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                // create output stream
                outStream = new DataOutputStream(clientSocket.getOutputStream());
                // scanner for user input from console
                Scanner sc = new Scanner(System.in);
                // instantiate the ConnectFour object
                c4 = new ConnectFour();
                // communicate with client and play game
                sendMessage("" + c4.getTurn());
                while (true) {
                    if (c4.isGameOver()) {
                        c4.startNewGame();
                        sendMessage("" + c4.getTurn());
                        continue;
                    }
                    if (c4.getTurn() == 1) {
                        String message = inStream.readLine();
                        try {
                            int command = Integer.parseInt(message);
                            if (command > 0 && command < 8) {
                                c4.playMove(1, command);
                            } else {
                                continue;
                            }
                        } catch (NumberFormatException nfe) {
                            continue;
                        }
                    }
                    if (c4.getTurn() == 0) {
                        System.out.println("Enter your next move.");
                        String userInput = sc.nextLine();
                        if (userInput.length() > 0) {
                            try {
                                int command = Integer.parseInt(userInput);
                                if (command > 0 && command < 8) {
                                    c4.playMove(0, command);
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
                }
                // close the streams
                //inStream.close();
                //outStream.close();
            }

        } catch (IOException e) {
            // print error to error stream
            System.err.println("IOException occurred " + e);
            System.out.println("IO error occured. Server exiting.");
        }

    }
    /**
     * A private method for the server to send a message to the client.
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
