package org.touchsoft.model;

public class User {
    private String nick;
    private String password;

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick.trim();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof User)) return false;

        User user = (User) obj;
        return this.nick.equals(user.nick) && this.password.equals(user.password);
    }
}
