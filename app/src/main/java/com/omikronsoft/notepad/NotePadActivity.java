package com.omikronsoft.notepad;

import android.app.Dialog;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.omikronsoft.notepad.containers.Content;
import com.omikronsoft.notepad.containers.ItemData;
import com.omikronsoft.notepad.containers.Priority;
import com.omikronsoft.notepad.providers.DataProvider;

import java.util.HashMap;
import java.util.Map;

import static com.omikronsoft.notepad.ListItemType.DRAW_ITEM;
import static com.omikronsoft.notepad.ListItemType.NOTE_ITEM;
import static com.omikronsoft.notepad.ListItemType.RECORD_ITEM;
import static com.omikronsoft.notepad.ListItemType.TODO_ITEM;

public class NotePadActivity extends AppCompatActivity {
    private SwipeMenuListView listView;
    private RadioButton rbAll;
    private RadioButton rbMedHigh;
    private RadioButton addNoteRadioLow;
    private RadioButton addNoteRadioHigh;
    private RadioButton addNoteRadioMed;
    private RadioButton addToDoRadioLow;
    private RadioButton addToDoRadioMed;
    private FrameLayout progressLayout;
    private Map<ListItemType, ToggleButton> toggleButtonHolder;
    private Map<ListItemType, SwipeMenuCreator> swipeMenuHolder;
    private Map<ListItemType, SwipeMenuListView.OnMenuItemClickListener> menuListenerHolder;
    private Map<ListItemType, CustomAdapter> listAdapters;
    private DataProvider dataProvider;
    private TextView totalCounter, notePreviewTextView, quote, author, txtNoteTitle, txtNoteContent, txtNoteDialogTitle, txtToDoTitle;
    private ItemData editedItem;
    private String totalCounterPrefix, counterDisplay;
    private Dialog noteContentPreview, addNoteDialog, addToDoDialog, addRecordDialog;
    private Button btnAddNote;

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

        dataProvider = DataProvider.getInstance();

        totalCounterPrefix = res.getString(R.string.total_display_prefix);
        setContentView(R.layout.activity_note_pad);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        progressLayout = (FrameLayout)findViewById(R.id.progress_layout);
        progressLayout.setVisibility(View.INVISIBLE);
        totalCounter = (TextView)findViewById(R.id.txt_total_counter);

        noteContentPreview = new Dialog(this);
        noteContentPreview.requestWindowFeature(Window.FEATURE_NO_TITLE);
        noteContentPreview.setContentView(R.layout.note_preview_layout);
        notePreviewTextView = (TextView) (noteContentPreview).findViewById(R.id.txt_note_preview);

        prepareList();
        prepareSwipeMenus();
        prepareMenuClickListeners();

        prepareAddNoteDialog();
        prepareAddToDoDialog();
        prepareAddRecordDialog();

        prepareRadioGroup();
        prepareToggleButtons();
        prepareFloatingActionButton();
    }

    private void prepareAddNoteDialog(){
        addNoteDialog = new Dialog(this);
        addNoteDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        addNoteDialog.setContentView(R.layout.add_note_layout);

        txtNoteDialogTitle = (TextView) (addNoteDialog).findViewById(R.id.txt_note_dialog);
        txtNoteTitle = (TextView) (addNoteDialog).findViewById(R.id.txt_note_title);
        txtNoteContent =(TextView) (addNoteDialog).findViewById(R.id.txt_note_content);
        btnAddNote = (Button)((addNoteDialog).findViewById(R.id.btn_add_note));
        Button btnCancelAddNote = (Button) ((addNoteDialog).findViewById(R.id.btn_cancel_add_note));

        addNoteRadioHigh = (RadioButton)((addNoteDialog).findViewById(R.id.add_item_radio_high));
        addNoteRadioMed = (RadioButton)((addNoteDialog).findViewById(R.id.add_item_radio_med));
        addNoteRadioLow = (RadioButton)((addNoteDialog).findViewById(R.id.add_item_radio_low));

        btnCancelAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNoteDialog.hide();
                editedItem = null;
            }
        });

        btnAddNote.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String title = txtNoteTitle.getText().toString();
                int priority = addNoteRadioLow.isChecked() ? 0 : (addNoteRadioMed.isChecked() ? 1 : 2);

                if (!title.isEmpty()) {
                    if(editedItem != null){
                        String oldTitle = editedItem.getTitle();
                        editedItem.setPriority(priority);
                        editedItem.setTitle(txtNoteTitle.getText().toString());
                        editedItem.setEditTime(System.currentTimeMillis());
                        editedItem.getContent().setNoteContent(txtNoteContent.getText().toString());
                        DataProvider.getInstance().updateItemData(oldTitle, editedItem);
                        editedItem = null;
                        hideDialogAndRefreshDisplay(addNoteDialog, NOTE_ITEM);
                    }else{
                        if(!DataProvider.getInstance().itemExists(title, NOTE_ITEM)){
                            ItemData id = new ItemData(NOTE_ITEM, title, priority, System.currentTimeMillis());
                            Content content = new Content();
                            content.setNoteContent(txtNoteContent.getText().toString());
                            id.setContent(content);
                            DataProvider.getInstance().addItemData(id);
                            hideDialogAndRefreshDisplay(addNoteDialog, NOTE_ITEM);
                        }
                    }
                }
            }
        });
    }

    private void prepareAddToDoDialog(){
        addToDoDialog = new Dialog(this);
        addToDoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        addToDoDialog.setContentView(R.layout.add_todo_layout);

        txtToDoTitle = (TextView) (addToDoDialog).findViewById(R.id.txt_todo_title);

        addToDoRadioLow = (RadioButton)((addToDoDialog).findViewById(R.id.add_item_radio_low));
        addToDoRadioMed = (RadioButton)((addToDoDialog).findViewById(R.id.add_item_radio_med));

        quote = (TextView) (addToDoDialog).findViewById(R.id.txt_quote);
        author = (TextView) (addToDoDialog).findViewById(R.id.txt_author);

        (addToDoDialog).findViewById(R.id.btn_cancel_add_todo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToDoDialog.hide();
            }
        });

        (addToDoDialog).findViewById(R.id.btn_add_todo).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String title = txtToDoTitle.getText().toString();
                int priority = addToDoRadioLow.isChecked() ? 0 : (addToDoRadioMed.isChecked() ? 1 : 2);

                if (!title.isEmpty()) {
                    if (!DataProvider.getInstance().itemExists(title, TODO_ITEM)) {
                        ItemData id = new ItemData(TODO_ITEM, title, priority, System.currentTimeMillis());
                        id.setContent(new Content());
                        DataProvider.getInstance().addItemData(id);
                        hideDialogAndRefreshDisplay(addToDoDialog, TODO_ITEM);
                    }
                }
            }
        });
    }

    private void prepareAddRecordDialog(){
        addRecordDialog = new Dialog(this);
        addRecordDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        addRecordDialog.setContentView(R.layout.add_record_layout);
    }

    private void hideDialogAndRefreshDisplay(Dialog dialog, ListItemType itemType) {
        dialog.hide();
        updateListAdapter(itemType);
    }

    private void setDataToDoDialog(){
        int rnd = QuoteProvider.getRandomInt();
        quote.setText(QuoteProvider.getQuote(rnd));
        author.setText(QuoteProvider.getAuthor(rnd));

        txtToDoTitle.setText("");
        addToDoRadioLow.setChecked(true);
    }

    private void setDataNoteDialog(ItemData itemData){
        if(itemData != null){
            txtNoteDialogTitle.setText(res.getString(R.string.add_note_title_edit));
            btnAddNote.setText(res.getString(R.string.add_note_button_edit));

            switch (itemData.getPriority()){
                case 0:
                    addNoteRadioLow.setChecked(true);
                    break;
                case 1:
                    addNoteRadioMed.setChecked(true);
                    break;
                case 2:
                    addNoteRadioHigh.setChecked(true);
                    break;
                default:
                    break;
            }
            txtNoteTitle.setText(itemData.getTitle());
            txtNoteContent.setText(itemData.getContent().getNoteContent());
        }else{
            txtNoteDialogTitle.setText(res.getString(R.string.add_note_title_new));
            btnAddNote.setText(res.getString(R.string.add_note_button_add));

            addNoteRadioLow.setChecked(true);
            txtNoteTitle.setText("");
            txtNoteContent.setText("");
        }
    }

    private void prepareFloatingActionButton(){
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.btn_floating_add);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (globals.getSelectedListType()){
                    case DRAW_ITEM:
                        break;
                    case NOTE_ITEM:
                        setDataNoteDialog(null);
                        addNoteDialog.show();
                        break;
                    case RECORD_ITEM:
                        addRecordDialog.show();
                        break;
                    case TODO_ITEM:
                        setDataToDoDialog();
                        addToDoDialog.show();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void prepareMenuClickListeners(){
        menuListenerHolder = new HashMap<>();

        SwipeMenuListView.OnMenuItemClickListener noteClickListener = new SwipeMenuListView.OnMenuItemClickListener(){
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                String title = listAdapters.get(NOTE_ITEM).getItem(position);

                switch (index){
                    case 0:
                        DataProvider.getInstance().deleteItem(NOTE_ITEM, title);
                        updateListAdapter(NOTE_ITEM);
                        break;
                    case 1:
                        editedItem = DataProvider.getInstance().getItemData(NOTE_ITEM, title);
                        setDataNoteDialog(editedItem);
                        addNoteDialog.show();
                        break;
                }
                return false;
            }
        };

        SwipeMenuListView.OnMenuItemClickListener todoClickListener = new SwipeMenuListView.OnMenuItemClickListener(){
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                String title = listAdapters.get(TODO_ITEM).getItem(position);

                switch (index){
                    case 1:
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
                String title = listAdapters.get(RECORD_ITEM).getItem(position);

                switch (index){
                    case 0:
                        DataProvider.getInstance().deleteItem(RECORD_ITEM, title);
                        updateListAdapter(RECORD_ITEM);
                        break;
                }
                return false;
            }
        };


        SwipeMenuListView.OnMenuItemClickListener drawClickListener = new SwipeMenuListView.OnMenuItemClickListener(){
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                String title = listAdapters.get(DRAW_ITEM).getItem(position);

                switch (index){
                    case 0:
                        DataProvider.getInstance().deleteItem(DRAW_ITEM, title);
                        updateListAdapter(DRAW_ITEM);
                        break;
                }
                return false;
            }
        };

        menuListenerHolder.put(NOTE_ITEM, noteClickListener);
        menuListenerHolder.put(TODO_ITEM, todoClickListener);
        menuListenerHolder.put(RECORD_ITEM, recordClickListener);
        menuListenerHolder.put(DRAW_ITEM, drawClickListener);
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
                SwipeMenuItem completeItem = new SwipeMenuItem(context);
                int buttonWidthPx = globals.dp2px(res.getInteger(R.integer.list_menu_button_width_dp));

                deleteItem.setWidth(buttonWidthPx);
                deleteItem.setBackground(new ColorDrawable(ContextCompat.getColor(context, R.color.list_menu_delete_back)));
                deleteItem.setIcon(R.drawable.ic_delete);

                completeItem.setWidth(buttonWidthPx);
                completeItem.setBackground(new ColorDrawable(ContextCompat.getColor(context, R.color.list_menu_complete_back)));
                completeItem.setIcon(R.drawable.ic_todo);

                menu.addMenuItem(deleteItem);
                menu.addMenuItem(completeItem);
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

        swipeMenuHolder.put(NOTE_ITEM, notesMenu);
        swipeMenuHolder.put(TODO_ITEM, todoMenu);
        swipeMenuHolder.put(RECORD_ITEM, recordMenu);
        swipeMenuHolder.put(DRAW_ITEM, drawMenu);
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
                        totalCounter.setText(counterDisplay);
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
        toggleButtonHolder.put(RECORD_ITEM, (ToggleButton) findViewById(R.id.toggle_records));
        toggleButtonHolder.put(DRAW_ITEM, (ToggleButton) findViewById(R.id.toggle_draw));

        for(ToggleButton toggleButton : toggleButtonHolder.values()){
            toggleButton.setOnCheckedChangeListener(ToggleListener);
        }

        ((ToggleButton) findViewById(R.id.toggle_notes)).setChecked(true);
    }

    private void prepareRadioGroup(){
        final RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radio_group);
        rbAll = (RadioButton) findViewById(R.id.radio_low);
        rbMedHigh = (RadioButton) findViewById(R.id.radio_med);

        switch (globals.getSelectedPriority()){
            case LOW:
                rbAll.setChecked(true);
                break;
            case MEDIUM:
                rbMedHigh.setChecked(true);
                break;
            case HIGH:
                ((RadioButton) findViewById(R.id.radio_high)).setChecked(true);
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

        CustomAdapter adapter = listAdapters.get(itemType);
        adapter.updateList(DataProvider.getInstance().getTitleListByPriority(itemType));
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        counterDisplay = totalCounterPrefix + " " + DataProvider.getInstance().getTotalItems(itemType);
        totalCounter.setText(counterDisplay);
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

        listAdapters = new HashMap<>();
        listAdapters.put(NOTE_ITEM, new CustomAdapter(dataProvider.getTitleListByPriority(NOTE_ITEM), context, NOTE_ITEM));
        listAdapters.put(TODO_ITEM, new CustomAdapter(dataProvider.getTitleListByPriority(TODO_ITEM), context, TODO_ITEM));
        listAdapters.put(RECORD_ITEM, new CustomAdapter(dataProvider.getTitleListByPriority(RECORD_ITEM), context, RECORD_ITEM));
        listAdapters.put(DRAW_ITEM, new CustomAdapter(dataProvider.getTitleListByPriority(DRAW_ITEM), context, DRAW_ITEM));

        listView.setAdapter(listAdapters.get(globals.getSelectedListType()));

        listView.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {
            @Override
            public void onSwipeStart(int position) {

            }

            @Override
            public void onSwipeEnd(int position) {

            }
        });

        listView.setSwipeDirection(SwipeMenuListView.DIRECTION_RIGHT);
        listView.setLongClickable(true);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(globals.getSelectedListType() == NOTE_ITEM){
                    String title = (String)listView.getItemAtPosition(position);
                    ItemData itemData = DataProvider.getInstance().getItemData(NOTE_ITEM, title);

                    if(itemData != null && itemData.getContent() != null){
                        noteContentPreview.show();

                        if(notePreviewTextView != null) {
                            String noteContent = itemData.getContent().getNoteContent();
                            if(noteContent.isEmpty()){
                                noteContent = res.getString(R.string.empty_note_content);
                            }
                            notePreviewTextView.setText(noteContent);
                        }
                    }
                }
                return false;
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
