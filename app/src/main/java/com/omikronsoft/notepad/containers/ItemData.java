package com.omikronsoft.notepad.containers;

import com.omikronsoft.notepad.ListItemType;
import com.omikronsoft.notepad.utils.Utils;

/**
 * Created by Dariusz Lelek on 6/1/2017.
 * dariusz.lelek@gmail.com
 */

public class ItemData {
    private String title;
    private ListItemType itemType;
    private int priority;
    private long editTime;
    private Content content;
    private String formattedEditTime;

    public ItemData(ListItemType itemType, String title, int priority, long editTime) {
        this.itemType = itemType;
        this.title = title;
        this.priority = priority;
        setEditTime(editTime);
    }

    public ListItemType getItemType() {
        return itemType;
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
        this.formattedEditTime = Utils.getInstance().getFormattedDate(editTime);
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }
}
