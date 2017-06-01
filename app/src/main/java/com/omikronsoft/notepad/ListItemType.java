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
    NOTE_ITEM(R.drawable.ic_note, R.string.note_data_key, false),
    RECORD_ITEM(R.drawable.ic_record, R.string.record_data_key, false),
    TODO_ITEM(R.drawable.ic_todo_box, R.string.todo_data_key, false),
    DRAW_ITEM(0, R.string.draw_data_key, true);


    public int value;
    public String prefsKey;
    public boolean noPriority;

    ListItemType(int value, int prefsKeyID, boolean noPriority) {
        this.noPriority = noPriority;
        this.value = value;
        this.prefsKey = Globals.getInstance().getRes().getString(prefsKeyID);
    }

    public int getIconResource(){
        return value;
    }

    public String getPrefsKey() {
        return prefsKey;
    }

    public boolean hasPriority(){
        return !noPriority;
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
