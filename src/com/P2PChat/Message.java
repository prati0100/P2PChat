package com.P2PChat;

/**
 * Describes a message that we receive. Contains information about the sender, and the message String
 * @author Pratyush Yadav
 * @author Rahul Rai
 * @author Prateek
 * @author Raunaq Singh
 * @author Nandan Pai
 */
public class Message {
    private String msg;
    private Sender mSender;

    /**
     * Create a new Message
     * @param msg The String that we receive or send
     * @param mSender Details about the person we send to
     */
    public Message(String msg, Sender mSender) {
        this.msg = msg;
        this.mSender = mSender;
    }

    public String getMessage() {
        return msg;
    }

    public void setMessage(String m) {
        msg = m;
    }

    public Sender getSender() {
        return mSender;
    }

    @Override
    public String toString() {
        return mSender.getName() + ": " + msg;
    }
}
