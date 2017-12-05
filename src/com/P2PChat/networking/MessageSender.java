package com.P2PChat.networking;

import com.P2PChat.Message;
import com.P2PChat.Sender;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**0
 * This is the thread that works in the background and helps in sending messages by reading input from terminal
 * and then sending that information over the network.
 * @author Pratyush Yadav
 */

public class MessageSender extends Thread {
    private Socket outSocket;
    private int outPort;
    private boolean done;
    private List<Message> messageList;

    /**
     * The default port for outgoing communication
     */
    public static final int defaultOutPort = 9990;

    /**
     * Create a MessageSender with the default port
     * @param outSocket The socket over which we will communicate
     * @throws InterruptedException
     * @throws IOException
     */
    public MessageSender(Socket outSocket) throws InterruptedException, IOException {
        this(outSocket, defaultOutPort);
    }

    /**
     * Create a MessageSender with the specified port
     * @param outSocket The socket over which we communicate
     * @param outPort The port for communication
     * @throws InterruptedException
     * @throws IOException
     */
    public MessageSender(Socket outSocket, int outPort) throws InterruptedException, IOException {
        super();
        this.outPort = defaultOutPort;
        this.done = false;
        this.messageList = new LinkedList<>();
        this.outSocket = outSocket;
    }

    public Socket getOutSocket() {
        return outSocket;
    }

    public int getOutPort() {
        return outPort;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    //Maintain a FIFO ordering
    public Message getNextMessage() {
        return messageList.remove(0);
    }

    public void addMessage(Message nextMessage) {
        messageList.add(nextMessage);
    }

    public boolean hasNextMessage() {
        return messageList.size() != 0;
    }

    /**
     * Run in the background, and read messages from terminal and send them over to the socket
     */
    @Override
    public void run() {
        try {
            //Set up IO streams for communication with the socket
            OutputStreamWriter osw = new OutputStreamWriter(outSocket.getOutputStream());
            PrintWriter pw = new PrintWriter(osw);
            Scanner s = new Scanner(System.in); //For reading messages from terminal
            while(true) {
                if(done) {
                    return;
                }

                if(outSocket.isClosed()) {
                    break;
                }

                String msg = s.nextLine();
                addMessage(new Message(msg, Sender.getCurrentSender()));
                if(hasNextMessage()) {
                    pw.println(getNextMessage().getMessage());
                    pw.flush(); //Flush the buffer, just to make sure
                    System.out.print("You: "); //After we hit enter, we need to reprint "You: " otherwise the line becomes empty
                }
            }
            System.out.println();
        }
        catch (IOException ioe) {
            System.out.println("Connection Error!");
        }
    }
}
