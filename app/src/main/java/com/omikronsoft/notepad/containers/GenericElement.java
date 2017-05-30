package com.omikronsoft.notepad.containers;

import com.omikronsoft.notepad.utils.Utils;

/**
 * Created by Dariusz Lelek on 5/30/2017.
 * dariusz.lelek@gmail.com
 */

public class GenericElement {
    protected String title;
    private long editTime;
    private String editTimeString;

    public GenericElement(String title) {
        this.title = title;

        updateEditTime();
    }

    public String getTitle() {
        return title;
    }

    public long getEditTime() {
        return editTime;
    }

    public String getEditTimeString() {
        return editTimeString;
    }

    protected void setTitle(String title) {
        this.title = title;
        updateEditTime();
    }

    private void updateEditTime(){
        this.editTime = System.currentTimeMillis();
        this.editTimeString = Utils.getInstance().getFormattedDate(editTime);
    }
}
