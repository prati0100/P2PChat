import com.P2PChat.Sender;
import com.P2PChat.networking.ConnectionManager;

import java.io.IOException;
import java.util.Scanner;


/**
 * This class has the main() method, and handles the start of the program
 * @author Pratyush Yadav
 * @author Rahul Rai
 * @author Prateek
 * @author Raunaq Singh
 * @author Nandan Pai
 */

public class Driver {
    /**
     * Text to prompt the user for input
     */
    public static final String TERMINAL_PROMPT = "Welcome!\n" +
            "1. Connect to a person\n" +
            "2. Wait for someone to connect\n" +
            "3. Exit\n" +
            "Select what to do: ";

    /**
     * Entry point of the application
     * @param args The command line arguments passed. We don't use them here though
     */
    public static void main(String[] args){
        prompt();
    }

    /**
     * Draws the terminal prompt, and loops through to provide the menu to the user
     */

    public static void prompt() {
        ConnectionManager connectionManager = null;
        Scanner s = new Scanner(System.in);

        while(true) {
            System.out.print(TERMINAL_PROMPT);
            int n = s.nextInt();
            try {
                switch (n) {
                    case 1: {
                        Sender sender = Sender.createSenderFromStdin();
                        connectionManager = new ConnectionManager(sender);
                        connectionManager.connect();
                        System.out.println("Connected! Start sending messages now!");
                        break;
                    }

                    case 2: {
                        connectionManager = new ConnectionManager();
                        System.out.println("Your IP address is " + ConnectionManager.getMyInetAddress());
                        System.out.println("Waiting for connection...");
                        connectionManager.waitForConnection();
                        System.out.println("Connected! Start conversation now!");
                        break;
                    }

                    case 3: {
                        System.exit(0);
                    }

                    default: {
                        System.out.println("Error! Incorrect input, try again");
                        break;
                    }
                }

                if(connectionManager != null) {
                    connectionManager.getMessageReceiver().join();
                    connectionManager.getMessageSender().join();
                }
                System.out.println("Connection Terminated!");
            } catch (IOException | InterruptedException e) { //If any of these exception is raised, disconnect and redraw prompt
                System.out.println("Connection error! Disconnecting...");
                if(connectionManager != null) {
                    connectionManager.disconnect();
                }
            }
        }
    }
}
