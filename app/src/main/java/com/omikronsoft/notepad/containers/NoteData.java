package com.omikronsoft.notepad.containers;

import com.omikronsoft.notepad.utils.Utils;

/**
 * Created by Dariusz Lelek on 5/30/2017.
 * dariusz.lelek@gmail.com
 */

public class NoteData{
    private String title;
    private int priority;
    private long editTime;
    private String content;
    private String formattedEditTime;

    public NoteData(String title) {
        this.title = title;
    }

    public NoteData(String title, int priority, Long editTime, String content) {
        this.title = title;
        this.priority = priority;
        this.editTime = editTime;
        this.content = content;

        updateFormattedEditTime();
    }

    private void updateFormattedEditTime(){
        formattedEditTime = Utils.getInstance().getFormattedDate(editTime);
    }

    public String getTitle() {
        return title;
    }

    public int getPriority() {
        return priority;
    }

    public long getEditTime() {
        return editTime;
    }

    public String getContent() {
        return content;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getFormattedEditTime() {
        return formattedEditTime;
    }

    public void setEditTime(long editTime) {
        this.editTime = editTime;

        updateFormattedEditTime();
    }

    public void setContent(String content) {
        this.content = content;
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
