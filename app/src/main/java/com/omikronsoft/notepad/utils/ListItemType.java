package com.omikronsoft.notepad.utils;

import com.omikronsoft.notepad.R;

/**
 * Created by Dariusz Lelek on 5/30/2017.
 * dariusz.lelek@gmail.com
 */

public enum ListItemType {
    NOTE_ITEM(R.drawable.ic_note, R.string.note_data_key),
    RECORD_ITEM(R.drawable.ic_record, R.string.record_data_key),
    TODO_ITEM(R.drawable.ic_todo_box, R.string.todo_data_key);


    public int value;
    public String prefsKey;

    ListItemType(int value, int prefsKeyID) {
        this.value = value;
        this.prefsKey = Globals.getInstance().getRes().getString(prefsKeyID);
    }

    public int getIconResource(){
        return value;
    }

    public String getPrefsKey() {
        return prefsKey;
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
            default:
                break;
        }
        return result;
    }
}
