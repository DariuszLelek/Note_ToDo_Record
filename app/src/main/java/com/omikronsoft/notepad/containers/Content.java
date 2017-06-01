package com.omikronsoft.notepad.containers;

import android.graphics.Bitmap;
import android.media.MediaPlayer;

/**
 * Created by Dariusz Lelek on 6/1/2017.
 * dariusz.lelek@gmail.com
 */

public class Content {
    private String noteContent;
    private MediaPlayer recordContent;
    private Bitmap drawContent;

    public Content() {
    }

    public String getNoteContent() {
        return noteContent;
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
    }

    public MediaPlayer getRecordContent() {
        return recordContent;
    }

    public void setRecordContent(MediaPlayer recordContent) {
        this.recordContent = recordContent;
    }

    public Bitmap getDrawContent() {
        return drawContent;
    }

    public void setDrawContent(Bitmap drawContent) {
        this.drawContent = drawContent;
    }
}
