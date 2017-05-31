package com.omikronsoft.notepad;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.omikronsoft.notepad.containers.Priority;

import static java.lang.reflect.Array.getInt;

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
    private SharedPreferences prefs;
    private ListItemType selectedListType;

    private Globals(){

    }

    public void loadPrefsData(){
        selectedPriority = Priority.getPriorityByValue(prefs.getInt(res.getString(R.string.selected_priority_key), 0));
        selectedListType = ListItemType.getListTypeByValue(prefs.getInt(res.getString(R.string.selected_list_type_key), 0));
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

    public ListItemType getSelectedListType() {
        return selectedListType;
    }

    public void setSelectedPriority(Priority selectedPriority) {
        this.selectedPriority = selectedPriority;

        SharedPreferences.Editor edit = prefs.edit();
        edit.putInt(res.getString(R.string.selected_priority_key), selectedPriority.getValue());
        edit.apply();
    }

    public void setSelectedListType(ListItemType selectedListType) {
        this.selectedListType = selectedListType;

        SharedPreferences.Editor edit = prefs.edit();
        edit.putInt(res.getString(R.string.selected_list_type_key), selectedPriority.getValue());
        edit.apply();
    }


    public void setPrefs(SharedPreferences prefs) {
        this.prefs = prefs;
    }

    public SharedPreferences getPrefs() {
        return prefs;
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
