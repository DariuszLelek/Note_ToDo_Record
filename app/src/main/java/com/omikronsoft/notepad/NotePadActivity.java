package com.omikronsoft.notepad;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.drawable.DrawableWrapper;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.omikronsoft.notepad.containers.Priority;
import com.omikronsoft.notepad.providers.NoteDataProvider;

import java.util.List;

public class NotePadActivity extends AppCompatActivity {
    private SwipeMenuListView notesListView;
    private List<String> notes;
    private CustomAdapter adapter;
    private RadioButton rbAll, rbMedHigh, rbHigh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Context context = getApplicationContext();
        ApplicationContext.getInstance().init(context);
        final Resources res = getResources();
        final Globals globals = Globals.getInstance();

        globals.setRes(res);

        setContentView(R.layout.activity_note_pad);

        TabHost tabHost = (TabHost)findViewById(R.id.host1);
        tabHost.setup();

        TabHost.TabSpec spec = tabHost.newTabSpec("Notes");
        spec.setContent(R.id.tab_notes);
        spec.setIndicator("Notes");
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec("Records");
        spec.setContent(R.id.tab_records);
        spec.setIndicator("Records");
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec("Drawings");
        spec.setContent(R.id.tab_drawings);
        spec.setIndicator("Drawings");
        tabHost.addTab(spec);

        notesListView = (SwipeMenuListView) findViewById(R.id.notes_list);
       // adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, NotesControl.getInstance().getNotesList());
        adapter = new CustomAdapter(NoteDataProvider.getInstance().getNotesTitle(), context, ListItemType.NOTE_ITEM);
        notesListView.setAdapter(adapter);

        SwipeMenuCreator creator = new SwipeMenuCreator(){
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem openItem = new SwipeMenuItem(context);
                SwipeMenuItem deleteItem = new SwipeMenuItem(context);
                int buttonWidthPx = globals.dp2px(res.getInteger(R.integer.list_menu_button_width_dp));
                int txtSize = res.getInteger(R.integer.list_menu_button_txt_size_dp);

                deleteItem.setWidth(buttonWidthPx);
                deleteItem.setBackground(new ColorDrawable(ContextCompat.getColor(context, R.color.list_menu_delete_back)));
                deleteItem.setIcon(R.drawable.ic_delete);

                openItem.setBackground(new ColorDrawable(ContextCompat.getColor(context, R.color.list_menu_edit_back)));
                openItem.setTitle("Edit");
                openItem.setWidth(buttonWidthPx);
                openItem.setTitleSize(txtSize);
                openItem.setTitleColor(Color.WHITE);

                menu.addMenuItem(deleteItem);
                menu.addMenuItem(openItem);
            }
        };

        notesListView.setMenuCreator(creator);

        notesListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener(){
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                String noteTitle = adapter.getItem(position);

                switch (index){
                    case 0:
                        notesListView.smoothCloseMenu();
                        NoteDataProvider.getInstance().deleteNote(noteTitle);
                        adapter = new CustomAdapter(NoteDataProvider.getInstance().getNotesTitle(), context, ListItemType.NOTE_ITEM);
                        notesListView.setAdapter(adapter);
                        break;
                    case 1:
                        // edit
                        break;
                }
                return false;
            }
        });

        notesListView.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {
            @Override
            public void onSwipeStart(int position) {

            }

            @Override
            public void onSwipeEnd(int position) {

            }
        });

        notesListView.setSwipeDirection(SwipeMenuListView.DIRECTION_RIGHT);

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radio_group);
        rbAll = (RadioButton) findViewById(R.id.radio_low);
        rbMedHigh = (RadioButton) findViewById(R.id.radio_med);
        rbHigh = (RadioButton) findViewById(R.id.radio_high);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(rbAll.isChecked()){
                    globals.setSelectedPriority(Priority.LOW);
                }else if(rbMedHigh.isChecked()){
                    globals.setSelectedPriority(Priority.MEDIUM);
                }else{
                    globals.setSelectedPriority(Priority.HIGH);
                }

                adapter = new CustomAdapter(NoteDataProvider.getInstance().getNotesTitle(), context, ListItemType.NOTE_ITEM);
                notesListView.setAdapter(adapter);
            }
        });

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}
