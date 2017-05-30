package com.omikronsoft.notepad.utils;

import java.util.Calendar;

/**
 * Created by Dariusz Lelek on 5/30/2017.
 * dariusz.lelek@gmail.com
 */

public class Utils {
    private static Utils instance;

    private final String DATE_SEPARATOR = "/";

    public String getFormattedDate(long timeStamp){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);

        StringBuilder sb = new StringBuilder();
        sb.append(calendar.get(Calendar.DAY_OF_MONTH)).append(DATE_SEPARATOR);
        sb.append(String.format("%02d", calendar.get(Calendar.MONTH))).append(DATE_SEPARATOR);
        sb.append(calendar.get(Calendar.YEAR));

        return sb.toString();
    }

    private Utils(){

    }

    public synchronized static Utils getInstance() {
        if (instance == null) {
            instance = new Utils();
        }
        return instance;
    }
}
