package org.touchsoft;

public class IdGen {
    private static int id = 1;

    public static int getId(){
        if (id == Integer.MAX_VALUE) id = 1;
        return id++;
    }
}
