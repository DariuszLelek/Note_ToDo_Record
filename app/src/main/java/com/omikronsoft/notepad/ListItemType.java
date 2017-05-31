package com.omikronsoft.notepad;

import com.omikronsoft.notepad.containers.Priority;

import static com.omikronsoft.notepad.containers.Priority.HIGH;
import static com.omikronsoft.notepad.containers.Priority.LOW;
import static com.omikronsoft.notepad.containers.Priority.MEDIUM;

/**
 * Created by Dariusz Lelek on 5/30/2017.
 * dariusz.lelek@gmail.com
 */

public enum ListItemType {
    NOTE_ITEM(R.drawable.ic_note),
    RECORD_ITEM(R.drawable.ic_record),
    TODO_ITEM(R.drawable.ic_todo),
    DRAW_ITEM(0);


    public int value;

    ListItemType(int value) {
        this.value = value;
    }

    public int getIconResource(){
        return value;
    }

    public static ListItemType getListTypeByValue(int value){
        ListItemType result = NOTE_ITEM;
        switch (value){
            case 1:
                result = RECORD_ITEM;
                break;
            case 2:
                result = TODO_ITEM;
                break;
            case 3:
                result = DRAW_ITEM;
            default:
                break;
        }
        return result;
    }
}
