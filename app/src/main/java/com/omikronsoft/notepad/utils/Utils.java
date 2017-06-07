package com.omikronsoft.notepad.utils;

import java.io.File;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Dariusz Lelek on 5/30/2017.
 * dariusz.lelek@gmail.com
 */

public class Utils {
    private static Utils instance;

    private final static String DATE_SEPARATOR = "/";
    private final static String TIME_SEPARATOR = ":";

    public String getFormattedDate(long timeStamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);

        return String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) + DATE_SEPARATOR +
                String.format(Locale.ENGLISH, "%02d", calendar.get(Calendar.MONTH) + 1) + DATE_SEPARATOR +
                calendar.get(Calendar.YEAR);
    }

    public String getUpdatedTimer(String timer) {
        String[] timerParts = timer.split(TIME_SEPARATOR);
        int minutes = Integer.parseInt(timerParts[0]);
        int seconds = Integer.parseInt(timerParts[1]);

        seconds++;
        if (seconds == 60) {
            seconds = 0;
            minutes++;
        }

        return (minutes < 10 ? "0" + String.valueOf(minutes) : String.valueOf(minutes)) + TIME_SEPARATOR +
                (seconds < 10 ? "0" + String.valueOf(seconds) : String.valueOf(seconds));
    }

    String getFileName(File file) {
        String name = file.getName();
        int pos = name.lastIndexOf(".");
        if (pos > 0) {
            name = name.substring(0, pos);
        }
        return name;
    }

    private Utils() {

    }

    public synchronized static Utils getInstance() {
        if (instance == null) {
            instance = new Utils();
        }
        return instance;
    }
}
