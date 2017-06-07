package com.omikronsoft.notepad.painting;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.omikronsoft.notepad.utils.ApplicationContext;
import com.omikronsoft.notepad.R;
import com.omikronsoft.notepad.containers.Priority;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dariusz Lelek on 5/22/2017.
 * dariusz.lelek@gmail.com
 */

public class PaintingResources {
    private static PaintingResources instance;
    private Map<Priority, Integer> listIconPaints;

    private PaintingResources() {
        listIconPaints = new HashMap<>();

        prepareListIconPaints();
    }

    private void prepareListIconPaints() {
        Context context = ApplicationContext.get();

        listIconPaints.put(Priority.LOW, ContextCompat.getColor(context, R.color.list_icon_low_priority));
        listIconPaints.put(Priority.MEDIUM, ContextCompat.getColor(context, R.color.list_icon_medium_priority));
        listIconPaints.put(Priority.HIGH, ContextCompat.getColor(context, R.color.list_icon_high_priority));
    }

    public int getListIconPaint(Priority priority) {
        return listIconPaints.get(priority);
    }

    public synchronized static PaintingResources getInstance() {
        if (instance == null) {
            instance = new PaintingResources();
        }
        return instance;
    }
}
