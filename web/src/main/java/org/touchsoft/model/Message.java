package org.touchsoft.model;

import java.util.Date;

public class Message {
    private String text;
    private final User from;
    private User to;
    private final Date date = new Date();
    private boolean sent = false;

    public Message(String text, User from, User to) {
        this.text = text;
        this.from = from;
        this.to = to;
    }

    public Message(String text, User from) {
        this(text, from, null);
    }




    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public User getFrom() {
        return from;
    }

    public User getTo() {
        return to;
    }

    public void setTo(User to) {
        this.to = to;
    }

    public Date getDate() {
        return date;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }
}
