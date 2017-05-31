package com.omikronsoft.notepad.containers;

import com.omikronsoft.notepad.utils.Utils;

/**
 * Created by Dariusz Lelek on 5/31/2017.
 * dariusz.lelek@gmail.com
 */

public class ToDoData {
    private String title;
    private int priority;
    private long editTime;
    private String formattedEditTime;

    public ToDoData(String title, int priority, long editTime) {
        this.title = title;
        this.priority = priority;
        this.editTime = editTime;

        updateFormattedEditTime();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public long getEditTime() {
        return editTime;
    }

    public void setEditTime(long editTime) {
        this.editTime = editTime;
    }

    private void updateFormattedEditTime(){
        formattedEditTime = Utils.getInstance().getFormattedDate(editTime);
    }

    public String getFormattedEditTime() {
        return formattedEditTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ToDoData toDoData = (ToDoData) o;

        return title.equals(toDoData.title);

    }

    @Override
    public int hashCode() {
        return title.hashCode();
    }
}
