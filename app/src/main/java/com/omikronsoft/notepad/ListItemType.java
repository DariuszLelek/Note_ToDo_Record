package com.omikronsoft.notepad;

/**
 * Created by Dariusz Lelek on 5/30/2017.
 * dariusz.lelek@gmail.com
 */

public enum ListItemType {
    NOTE_ITEM(R.drawable.ic_note),
    RECORD_ITEM(R.drawable.ic_record);

    public int value;

    ListItemType(int value) {
        this.value = value;
    }

    public int getIconResource(){
        return value;
    }
}
