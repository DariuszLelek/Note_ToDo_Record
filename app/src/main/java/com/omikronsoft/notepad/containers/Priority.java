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

    public static Priority getPriorityByValue(int value){
        Priority result = LOW;
        switch (value){
            case 1:
                result = MEDIUM;
                break;
            case 2:
                result = HIGH;
                break;
            default:
                break;
        }
        return result;
    }
}
