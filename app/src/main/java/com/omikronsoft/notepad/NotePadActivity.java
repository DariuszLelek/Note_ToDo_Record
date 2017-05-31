package com.omikronsoft.notepad;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.omikronsoft.notepad.containers.NoteData;
import com.omikronsoft.notepad.containers.Priority;
import com.omikronsoft.notepad.containers.ToDoData;
import com.omikronsoft.notepad.providers.DataProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.omikronsoft.notepad.ListItemType.TODO_ITEM;

public class NotePadActivity extends AppCompatActivity {
    private SwipeMenuListView listView;
    private CustomAdapter adapter;
    private RadioButton rbAll, rbMedHigh, rbHigh, addLowPriority, addMedPriority, addHighPriority;
    private FrameLayout progressLayout;
    private Map<ListItemType, ToggleButton> toggleButtonHolder;
    private Map<ListItemType, SwipeMenuCreator> swipeMenuHolder;
    private Map<ListItemType, SwipeMenuListView.OnMenuItemClickListener> menuListenerHolder;
    private FrameLayout addItemLayout;
    private TextView addItemTitle, noteContent;
    private LinearLayout mainLayout;
    private NoteData editedNote;

    private Globals globals;
    private Context context;
    private Resources res;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this.getApplicationContext();
        res = getResources();
        globals = Globals.getInstance();

        ApplicationContext.getInstance().init(context);
        SharedPreferences prefs = this.getSharedPreferences(res.getString(R.string.shared_pref_key), Context.MODE_PRIVATE);

        globals.setPrefs(prefs);
        globals.setRes(res);
        globals.loadPrefsData();

        setContentView(R.layout.activity_note_pad);

        progressLayout = (FrameLayout)findViewById(R.id.progress_layout);
        progressLayout.setVisibility(View.INVISIBLE);
        addItemLayout = (FrameLayout)findViewById(R.id.item_add_layout);
        addItemTitle = (TextView)findViewById(R.id.txt_title);
        noteContent = (TextView)findViewById(R.id.txt_note_content);
        addLowPriority = (RadioButton)findViewById(R.id.add_radio_low);
        addMedPriority = (RadioButton)findViewById(R.id.add_radio_med);
        addHighPriority = (RadioButton)findViewById(R.id.add_radio_high);
        mainLayout = (LinearLayout)findViewById(R.id.main_layout);

//        loading = new ProgressDialog(this);
//        loading.setCancelable(true);
//        loading.setMessage("Loading data...");
//        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        prepareList();
        prepareSwipeMenus();
        prepareMenuClickListeners();

        prepareRadioGroup();
        prepareToggleButtons();
        prepareAddItemView();
        prepareFloatingActionButton();
    }

    private void prepareAddItemView(){
        (findViewById(R.id.btn_item_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideEditPanel();
            }
        });

        (findViewById(R.id.btn_item_add)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                boolean processed = false;
                String title = addItemTitle.getText().toString();
                int priority = addLowPriority.isChecked() ? 0 : (addMedPriority.isChecked() ? 1 : 2);
                ListItemType itemType = globals.getSelectedListType();

                if (!title.isEmpty()) {
                    switch (itemType) {
                        case NOTE_ITEM:
                            if (editedNote != null) {
                                String oldTitle = editedNote.getTitle();
                                editedNote.setPriority(priority);
                                editedNote.setContent(noteContent.getText().toString());
                                editedNote.setTitle(addItemTitle.getText().toString());
                                editedNote.setEditTime(System.currentTimeMillis());
                                DataProvider.getInstance().updateNote(oldTitle, editedNote);
                                updateListAdapter(ListItemType.NOTE_ITEM);
                                processed = true;
                            } else {
                                if(!DataProvider.getInstance().itemExists(title, ListItemType.NOTE_ITEM)){
                                    NoteData noteData = new NoteData(title, priority, System.currentTimeMillis(), noteContent.getText().toString());
                                    DataProvider.getInstance().addNoteData(noteData);
                                    updateListAdapter(ListItemType.NOTE_ITEM);
                                    processed = true;
                                }
                            }
                            break;
                        case TODO_ITEM:
                            if(!DataProvider.getInstance().itemExists(title, TODO_ITEM)){
                                ToDoData todoData = new ToDoData(title, priority, System.currentTimeMillis());
                                DataProvider.getInstance().addToDoData(todoData);
                                updateListAdapter(TODO_ITEM);
                                processed = true;
                            }
                            break;
                        default:
                            break;
                    }
                }

                if(processed){
                    hideEditPanel();
                }
            }
        });
    }

    private void hideEditPanel(){
        editedNote = null;
        addLowPriority.setChecked(true);
        addItemTitle.setText("");
        noteContent.setText("");
        findViewById(R.id.txt_note_content).setVisibility(View.INVISIBLE);
        ((Button)findViewById(R.id.btn_item_add)).setText("ADD");
        addItemLayout.setVisibility(View.INVISIBLE);
        mainLayout.setVisibility(View.VISIBLE);
        findViewById(R.id.txt_note_content).setVisibility(View.INVISIBLE);
    }

    private void showEditPanel(){
        addItemLayout.setVisibility(View.VISIBLE);
        mainLayout.setVisibility(View.INVISIBLE);

        if(globals.getSelectedListType() == ListItemType.NOTE_ITEM){
            findViewById(R.id.txt_note_content).setVisibility(View.VISIBLE);
        }
    }

    private void prepareFloatingActionButton(){
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.btn_floating_add);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(globals.getSelectedListType() == ListItemType.NOTE_ITEM || globals.getSelectedListType() == TODO_ITEM){
                    showEditPanel();
                }
            }
        });
    }

    private void prepareMenuClickListeners(){
        menuListenerHolder = new HashMap<>();

        SwipeMenuListView.OnMenuItemClickListener noteClickListener = new SwipeMenuListView.OnMenuItemClickListener(){
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                String title = adapter.getItem(position);

                switch (index){
                    case 0:
                        DataProvider.getInstance().deleteItem(ListItemType.NOTE_ITEM, title);
                        updateListAdapter(ListItemType.NOTE_ITEM);
                        break;
                    case 1:
                        NoteData nd = DataProvider.getInstance().getNoteData(title);
                        editedNote = nd;
                        showEditPanel();
                        ((Button)findViewById(R.id.btn_item_add)).setText("OK");

                        int priority = nd.getPriority();
                        switch (priority){
                            case 0:
                                addLowPriority.setChecked(true);
                                break;
                            case 1:
                                addMedPriority.setChecked(true);
                                break;
                            case 2:
                                addHighPriority.setChecked(true);
                                break;
                            default:
                                break;
                        }

                        addItemTitle.setText(nd.getTitle());
                        noteContent.setText(nd.getContent());
                        break;
                }
                return false;
            }
        };

        SwipeMenuListView.OnMenuItemClickListener todoClickListener = new SwipeMenuListView.OnMenuItemClickListener(){
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                String title = adapter.getItem(position);

                switch (index){
                    case 0:
                        DataProvider.getInstance().deleteItem(TODO_ITEM, title);
                        updateListAdapter(TODO_ITEM);
                        break;
                }
                return false;
            }
        };

        SwipeMenuListView.OnMenuItemClickListener recordClickListener = new SwipeMenuListView.OnMenuItemClickListener(){
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                String title = adapter.getItem(position);

                switch (index){
                    case 0:
                        DataProvider.getInstance().deleteItem(TODO_ITEM, title);
                        updateListAdapter(TODO_ITEM);
                        break;
                }
                return false;
            }
        };


        SwipeMenuListView.OnMenuItemClickListener drawClickListener = new SwipeMenuListView.OnMenuItemClickListener(){
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                String title = adapter.getItem(position);

                switch (index){
                    case 0:
                        DataProvider.getInstance().deleteItem(TODO_ITEM, title);
                        updateListAdapter(TODO_ITEM);
                        break;
                }
                return false;
            }
        };

        menuListenerHolder.put(ListItemType.NOTE_ITEM, noteClickListener);
        menuListenerHolder.put(TODO_ITEM, todoClickListener);
        menuListenerHolder.put(ListItemType.RECORD_ITEM, recordClickListener);
        menuListenerHolder.put(ListItemType.DRAW_ITEM, drawClickListener);
    }

    private void prepareSwipeMenus(){
        swipeMenuHolder = new HashMap<>();

        SwipeMenuCreator notesMenu = new SwipeMenuCreator(){
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

        SwipeMenuCreator todoMenu = new SwipeMenuCreator(){
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(context);
                int buttonWidthPx = globals.dp2px(res.getInteger(R.integer.list_menu_button_width_dp));

                deleteItem.setWidth(buttonWidthPx);
                deleteItem.setBackground(new ColorDrawable(ContextCompat.getColor(context, R.color.list_menu_complete_back)));
                deleteItem.setIcon(R.drawable.ic_todo);

                menu.addMenuItem(deleteItem);
            }
        };

        SwipeMenuCreator recordMenu = new SwipeMenuCreator(){
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(context);
                int buttonWidthPx = globals.dp2px(res.getInteger(R.integer.list_menu_button_width_dp));

                deleteItem.setWidth(buttonWidthPx);
                deleteItem.setBackground(new ColorDrawable(ContextCompat.getColor(context, R.color.list_menu_delete_back)));
                deleteItem.setIcon(R.drawable.ic_delete);

                menu.addMenuItem(deleteItem);
            }
        };

        SwipeMenuCreator drawMenu = new SwipeMenuCreator(){
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(context);
                int buttonWidthPx = globals.dp2px(res.getInteger(R.integer.list_menu_button_width_dp));

                deleteItem.setWidth(buttonWidthPx);
                deleteItem.setBackground(new ColorDrawable(ContextCompat.getColor(context, R.color.list_menu_delete_back)));
                deleteItem.setIcon(R.drawable.ic_delete);

                menu.addMenuItem(deleteItem);
            }
        };

        swipeMenuHolder.put(ListItemType.NOTE_ITEM, notesMenu);
        swipeMenuHolder.put(TODO_ITEM, todoMenu);
        swipeMenuHolder.put(ListItemType.RECORD_ITEM, recordMenu);
        swipeMenuHolder.put(ListItemType.DRAW_ITEM, drawMenu);
    }

    private void prepareToggleButtons(){
        toggleButtonHolder = new HashMap<>();

        final CompoundButton.OnCheckedChangeListener ToggleListener = new CompoundButton.OnCheckedChangeListener() {
            boolean avoidRecursions = false;

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(avoidRecursions) return;
                avoidRecursions = true;

                for(Map.Entry<ListItemType, ToggleButton> pair : toggleButtonHolder.entrySet()){
                    ToggleButton toggleButton = pair.getValue();

                    if(buttonView.equals(toggleButton)){
                        ListItemType selectedType = pair.getKey();
                        Globals.getInstance().setSelectedListType(selectedType);
                        buttonView.setChecked(true);
                        updateListAdapter(selectedType);
                        listView.setMenuCreator(swipeMenuHolder.get(selectedType));
                        listView.setOnMenuItemClickListener(menuListenerHolder.get(selectedType));
                    }else{
                        toggleButton.setChecked(false);
                    }
                }

                avoidRecursions = false;
            }
        };

        toggleButtonHolder.put(ListItemType.NOTE_ITEM, (ToggleButton) findViewById(R.id.toggle_notes));
        toggleButtonHolder.put(TODO_ITEM,  (ToggleButton) findViewById(R.id.toggle_todo));
        toggleButtonHolder.put(ListItemType.RECORD_ITEM, (ToggleButton) findViewById(R.id.toggle_records));
        toggleButtonHolder.put(ListItemType.DRAW_ITEM, (ToggleButton) findViewById(R.id.toggle_draw));

        for(ToggleButton toggleButton : toggleButtonHolder.values()){
            toggleButton.setOnCheckedChangeListener(ToggleListener);
        }

        ((ToggleButton) findViewById(R.id.toggle_notes)).setChecked(true);
    }

    private void prepareRadioGroup(){
        final RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radio_group);
        rbAll = (RadioButton) findViewById(R.id.radio_low);
        rbMedHigh = (RadioButton) findViewById(R.id.radio_med);
        rbHigh = (RadioButton) findViewById(R.id.radio_high);

        switch (globals.getSelectedPriority()){
            case LOW:
                rbAll.setChecked(true);
                break;
            case MEDIUM:
                rbMedHigh.setChecked(true);
                break;
            case HIGH:
                rbHigh.setChecked(true);
                break;
            default:
                break;
        }

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

                updateListAdapter(Globals.getInstance().getSelectedListType());
            }
        });
    }

    private void updateListAdapter(ListItemType itemType){
        listView.smoothCloseMenu();
        displayProgressIndicator();
        adapter = new CustomAdapter(DataProvider.getInstance().getTitleList(itemType), context, itemType);
        listView.setAdapter(adapter);
    }

    private void displayProgressIndicator(){
        progressLayout.setVisibility(View.VISIBLE);
        listView.post(new Runnable() {
            @Override
            public void run() {
                progressLayout.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void prepareList(){
        listView = (SwipeMenuListView) findViewById(R.id.items_list);

        listView.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {
            @Override
            public void onSwipeStart(int position) {

            }

            @Override
            public void onSwipeEnd(int position) {

            }
        });

        listView.setSwipeDirection(SwipeMenuListView.DIRECTION_RIGHT);
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
