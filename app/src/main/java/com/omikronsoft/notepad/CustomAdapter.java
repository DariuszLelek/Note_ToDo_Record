package com.omikronsoft.notepad;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.omikronsoft.notepad.containers.NoteData;
import com.omikronsoft.notepad.painting.PaintingResources;
import com.omikronsoft.notepad.providers.NoteDataProvider;

import java.util.List;

/**
 * Created by Dariusz Lelek on 5/30/2017.
 * dariusz.lelek@gmail.com
 */

public class CustomAdapter extends BaseAdapter {
    private final List<String> itemList;
    private final Context context;
    private final ListItemType itemType;

    public CustomAdapter(List<String> itemList, Context context, ListItemType itemType) {
        this.itemList = itemList;
        this.context = context;
        this.itemType = itemType;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public String getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.note_list_item, null);
            new ViewHolder(convertView);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        String item = getItem(position);
        holder.tv_title.setText(item);

        switch (itemType) {
            case NOTE_ITEM:
                NoteData nd = NoteDataProvider.getInstance().getNoteData(item);

                holder.iv_icon.setImageResource(itemType.getIconResource());
                holder.iv_icon.setColorFilter(PaintingResources.getInstance().getListIconPaint(nd.getPriority()));
                holder.tv_datetime.setText(nd.getEditTimeString());
                break;
            case RECORD_ITEM:
                break;
        }

        return convertView;
    }

    class ViewHolder {
        ImageView iv_icon;
        TextView tv_title;
        TextView tv_datetime;

        public ViewHolder(View view) {
            iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
            tv_title = (TextView) view.findViewById(R.id.tv_title);
            tv_datetime = (TextView) view.findViewById(R.id.tv_datetime);
            view.setTag(this);
        }
    }
}