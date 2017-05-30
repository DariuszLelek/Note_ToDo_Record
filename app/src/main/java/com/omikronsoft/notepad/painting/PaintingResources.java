package com.omikronsoft.notepad.painting;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;

import com.omikronsoft.notepad.ApplicationContext;
import com.omikronsoft.notepad.Globals;
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
    private Map<PaintingResource, Paint> cachedPaints;
    private Map<Transparency, Paint> bitmapPaints;
    private Map<Priority, Integer> listIconPaints;
    private Resources res;

    private PaintingResources() {
        cachedPaints = new HashMap<>();
        bitmapPaints = new HashMap<>();
        listIconPaints = new HashMap<>();

        res = Globals.getInstance().getRes();

        prepareListIconPaints();
    }

    private void prepareListIconPaints(){
        Context context = ApplicationContext.get();

        listIconPaints.put(Priority.LOW, ContextCompat.getColor(context, R.color.list_icon_low_priority));
        listIconPaints.put(Priority.MEDIUM, ContextCompat.getColor(context, R.color.list_icon_medium_priority));
        listIconPaints.put(Priority.HIGH, ContextCompat.getColor(context, R.color.list_icon_high_priority));
        //listIconPaints.put(Priority.NONE, Color.BLACK);
    }

    public Paint getBitmapPaint(Transparency transparency) {
        Paint paint;
        if (bitmapPaints.containsKey(transparency)) {
            paint = bitmapPaints.get(transparency);
        } else {
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setFilterBitmap(true);
            paint.setDither(true);
            paint.setAlpha(transparency.value);
            bitmapPaints.put(transparency, paint);
        }
        return paint;
    }

    public Paint getTextPaintCenter(int spSize, int color, Transparency trans) {
        Paint paint;
        PaintingResource pr = new PaintingResource.Builder().color(color).width(spSize).transparency(trans.value).build();
        if (cachedPaints.containsKey(pr)) {
            paint = cachedPaints.get(pr);
        } else {
            paint = new Paint();
            paint.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spSize, res.getDisplayMetrics()));
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(color);
            paint.setAlpha(trans.value);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setAntiAlias(true);
            cachedPaints.put(pr, paint);
        }

        if (paint.getTextAlign() != Paint.Align.CENTER) {
            paint.setTextAlign(Paint.Align.CENTER);
        }
        return paint;
    }

    public Paint getFillPaint(int color, Transparency trans) {
        Paint paint;
        PaintingResource pr = new PaintingResource.Builder().color(color).transparency(trans.value).build();
        if (cachedPaints.containsKey(pr)) {
            paint = cachedPaints.get(pr);
        } else {
            paint = new Paint();
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(color);
            paint.setAlpha(trans.value);
            paint.setAntiAlias(true);
            cachedPaints.put(pr, paint);
        }
        return paint;
    }

    public Paint getStrokePaint(int width, int color, Transparency trans, boolean densityWidth) {
        Paint paint;
        PaintingResource pr = new PaintingResource.Builder().color(color).transparency(trans.value).width(width).build();
        if (cachedPaints.containsKey(pr)) {
            paint = cachedPaints.get(pr);
        } else {
            paint = new Paint();
            paint.setStrokeWidth(densityWidth ? width * Globals.getInstance().getPixelDensity() : width);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(color);
            paint.setAlpha(trans.value);
            paint.setAntiAlias(true);
            cachedPaints.put(pr, paint);
        }
        return paint;
    }

    public int getListIconPaint(Priority priority){
        return listIconPaints.get(priority);
    }

    public synchronized static PaintingResources getInstance() {
        if (instance == null) {
            instance = new PaintingResources();
        }
        return instance;
    }
}
