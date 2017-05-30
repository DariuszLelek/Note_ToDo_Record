package com.omikronsoft.notepad.containers;

import com.omikronsoft.notepad.ListItemType;

/**
 * Created by Dariusz Lelek on 5/30/2017.
 * dariusz.lelek@gmail.com
 */

public class NoteData extends GenericElement{
    private Priority priority;
    private String content;
    private ListItemType itemType;

    public NoteData(String title) {
        super(title);
        itemType = ListItemType.NOTE_ITEM;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ListItemType getItemType() {
        return itemType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NoteData noteData = (NoteData) o;

        return title.equals(noteData.title);
    }

    @Override
    public int hashCode() {
        return title.hashCode();
    }
}
