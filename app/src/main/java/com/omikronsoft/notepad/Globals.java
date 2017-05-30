package com.omikronsoft.notepad;

import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.omikronsoft.notepad.containers.Priority;

/**
 * Created by Dariusz Lelek on 5/30/2017.
 * dariusz.lelek@gmail.com
 */

public class Globals {
    private static Globals instance;
    private Resources res;
    private DisplayMetrics metrics;
    private float pixelDensity;
    private Priority selectedPriority;

    private Globals(){
        // todo read from prefs
        selectedPriority = Priority.LOW;
    }

    public Resources getRes() {
        return res;
    }

    public void setRes(Resources res) {
        this.res = res;
        metrics = res.getDisplayMetrics();
    }

    public Priority getSelectedPriority() {
        return selectedPriority;
    }

    public void setSelectedPriority(Priority selectedPriority) {
        this.selectedPriority = selectedPriority;
    }

    public int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
    }

    public float getPixelDensity() {
        return metrics.density;
    }

    public synchronized static Globals getInstance() {
        if (instance == null) {
            instance = new Globals();
        }
        return instance;
    }
}
