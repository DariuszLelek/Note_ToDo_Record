package com.omikronsoft.notepad.containers;

/**
 * Created by Dariusz Lelek on 5/30/2017.
 * dariusz.lelek@gmail.com
 */

public enum Priority {
    LOW(0), MEDIUM(1), HIGH(2);

    int value;

    Priority(int value){
        this.value = value;
    }

    public int getValue(){
        return value;
    }
}
