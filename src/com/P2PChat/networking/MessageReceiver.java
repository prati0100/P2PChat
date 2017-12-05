package com.P2PChat.networking;

import com.P2PChat.Message;
import com.P2PChat.Sender;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * This is the thread that works in the background and helps in receiving messages by reading input from the socket
 * and then printing the message to the terminal.
 * @author Pratyush Yadav
 */

public class MessageReceiver extends Thread {
    private Socket inSocket;
    private int inPort;
    private boolean done;
    private List<Message> messageList;

    /**
     * The default port for incoming communication
     */
    public static final int defaultInPort = 9990;

    /**
     * Create a MessageReceiver with the default port.
     * @param inSocket The socket over which we will communicate.
     * @throws IOException
     * @throws InterruptedException
     */
    public MessageReceiver(Socket inSocket) throws IOException, InterruptedException {
        this(inSocket, defaultInPort);
    }

    /**
     * Create a MessageReceiver with the specified port
     * @param inSocket The socket over which we communicate
     * @param inPort The port over which we look for input
     * @throws IOException
     * @throws InterruptedException
     */
    public MessageReceiver(Socket inSocket, int inPort) throws IOException, InterruptedException {
        super();
        this.inPort = inPort;
        this.done = false;
        this.messageList = new LinkedList<>();
        this.inSocket = inSocket;
    }

    public Socket getInSocket() {
        return inSocket;
    }

    public int getInPort() {
        return inPort;
    }

    /**
     * If we want to stop the thread, set done to true. The thread will only stop when it wakes up from
     * readLine().
     * @param done Set whether the thread is done its job or not.
     */
    public void setDone(boolean done) {
        this.done = done;
    }

    public boolean hasNextMessage() {
        return messageList.size() != 0;
    }

    public Message getNextMessage() {
        return messageList.remove(0);
    }

    public void addMessage(Message m) {
        messageList.add(m);
    }

    /**
     * Run in the background and keep reading input from the Socket, and printing it on the screen
     */
    @Override
    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(inSocket.getInputStream());
            BufferedReader br = new BufferedReader(isr);

            String senderName = Sender.getCurrentSender().getName();

            while(true) {
                //If someone (usually the connection manager) sets done, stop execution
                if(done) {
                    break;
                }

                if(inSocket.isClosed()) {
                    break;
                }

                //This is for the terminal output, to signify we are typing the message
                System.out.print("You: ");

                String msg;
                try {
                    msg = br.readLine();
                } catch (NoSuchElementException nsee) {
                    msg = null;

                }
                if(msg == null) {
                    inSocket.close();
                    break;
                }
                addMessage(new Message(msg, Sender.getCurrentSender()));
                Message message = getNextMessage();

                //Prints a bunch of backspaces before printing the message. This is to remove the
                //"You:" that is already there. It will get reprinted when the loop goes through another iteration
                System.out.print("\b\b\b\b\b\b");

                System.out.println(senderName + ": " + message.getMessage());
            }

            System.out.println();

        }
        catch (IOException ioe) {
            System.out.println("Connection Error!");
        }
    }
}
