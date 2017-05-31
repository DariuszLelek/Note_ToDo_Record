package com.omikronsoft.notepad.providers;

import android.content.Intent;
import android.content.SharedPreferences;

import com.omikronsoft.notepad.Globals;
import com.omikronsoft.notepad.ListItemType;
import com.omikronsoft.notepad.R;
import com.omikronsoft.notepad.containers.ItemData;
import com.omikronsoft.notepad.containers.NoteData;
import com.omikronsoft.notepad.containers.ToDoData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static com.omikronsoft.notepad.ListItemType.DRAW_ITEM;
import static com.omikronsoft.notepad.ListItemType.NOTE_ITEM;
import static com.omikronsoft.notepad.ListItemType.RECORD_ITEM;
import static com.omikronsoft.notepad.ListItemType.TODO_ITEM;

/**
 * Created by Dariusz Lelek on 5/30/2017.
 * dariusz.lelek@gmail.com
 */

public class DataProvider {
    private static DataProvider instance;
    private Map<String, NoteData> notesData;
    private Map<String, ToDoData> todoData;
    private Map<ListItemType, Map<String, ItemData>> itemData;

    private String noteDataKey, dataSeparator, todoDataKey;
    private Globals globals;

    private DataProvider(){
        notesData = new TreeMap<>();
        todoData = new TreeMap<>();
        itemData = new HashMap<>();

        itemData.put(NOTE_ITEM, new HashMap());
        itemData.put(TODO_ITEM, new HashMap());
        itemData.put(RECORD_ITEM, new HashMap());
        itemData.put(DRAW_ITEM, new HashMap());

        globals = Globals.getInstance();

        noteDataKey = globals.getRes().getString(R.string.note_data_key);
        todoDataKey = globals.getRes().getString(R.string.todo_data_key);

        dataSeparator = globals.getRes().getString(R.string.shared_pref_data_separator);

        loadNotesData();
        loadToDoData();
    }

    public NoteData getNoteData(String title){
        return notesData.get(title);
    }

    public ToDoData getToDoData(String title){
        return todoData.get(title);
    }

    public void addNoteData(NoteData nd){
        notesData.put(nd.getTitle(), nd);
        saveItemData(NOTE_ITEM);
    }

    public void addToDoData(ToDoData tdd){
        todoData.put(tdd.getTitle(), tdd);
        saveItemData(TODO_ITEM);
    }

    public boolean itemExists(String title, ListItemType itemType){
        boolean result = false;

        switch (itemType){
            case NOTE_ITEM:
                result = notesData.containsKey(title);
                break;
            case TODO_ITEM:
                result = todoData.containsKey(title);
                break;
            case RECORD_ITEM:
                break;
            case DRAW_ITEM:
                break;
            default:
                break;
        }

        return result;
    }

    public void updateNote(String oldTitle, NoteData editedNote){
        if(notesData.containsKey(oldTitle)){
            notesData.remove(oldTitle);
            notesData.put(editedNote.getTitle(), editedNote);
        }
        saveItemData(NOTE_ITEM);
    }

    public void deleteItem(ListItemType itemType, String title){
        switch (itemType){
            case NOTE_ITEM:
                if(notesData.containsKey(title)){
                    notesData.remove(title);
                }
                saveItemData(NOTE_ITEM);
                break;
            case TODO_ITEM:
                if(todoData.containsKey(title)){
                    todoData.remove(title);
                }
                saveItemData(TODO_ITEM);
                break;
            case RECORD_ITEM:
                break;
            case DRAW_ITEM:
                break;
            default:
                break;
        }
    }

    public List<String> getTitleList(ListItemType itemType){
        List<String> result = new ArrayList<>();
        int selectedPriority = Globals.getInstance().getSelectedPriority().getValue();

        switch (itemType){
            case NOTE_ITEM:
                for(Map.Entry<String, NoteData> pair : notesData.entrySet()){
                    if(pair.getValue().getPriority() >= selectedPriority){
                        result.add(pair.getKey());
                    }
                }
                break;
            case TODO_ITEM:
                for(Map.Entry<String, ToDoData> pair : todoData.entrySet()){
                    if(pair.getValue().getPriority() >= selectedPriority){
                        result.add(pair.getKey());
                    }
                }
                break;
            case RECORD_ITEM:
                break;
            case DRAW_ITEM:
                break;
            default:
                break;
        }

        Collections.sort(result);
        return result;
    }

    public void saveItemData(ListItemType itemType){
        Set<String> dataSet = new HashSet<>();
        String dataKey = "";

        switch (itemType){
            case NOTE_ITEM:
                dataKey = noteDataKey;
                for(NoteData nd :  notesData.values()){
                    dataSet.add(getNoteDataString(nd));
                }
                break;
            case TODO_ITEM:
                dataKey = todoDataKey;
                for(ToDoData tdd :  todoData.values()){
                    dataSet.add(getToDoDataString(tdd));
                }
                break;
            case RECORD_ITEM:
                break;
            case DRAW_ITEM:
                break;
            default:
                break;
        }

        if(!dataKey.isEmpty()){
            SharedPreferences.Editor edit = globals.getPrefs().edit();
            edit.putStringSet(dataKey, dataSet);
            edit.apply();
        }
    }

    private void loadItemData(){
        for(ListItemType itemType : ListItemType.values()){

        }
    }

    private void loadNotesData(){
        notesData.clear();
        Set<String> noteDataSet = globals.getPrefs().getStringSet(noteDataKey, null);

        if(noteDataSet != null && !noteDataSet.isEmpty()){
            for(String noteDataString : noteDataSet){
                String[] parts = noteDataString.split(dataSeparator, -1);

                NoteData noteData = tryGetNoteData(parts, 4);

                if(noteData != null){
                    notesData.put(noteData.getTitle(), noteData);
                }
            }
        }
    }

    private void loadToDoData(){
        todoData.clear();
        Set<String> todoDataSet = globals.getPrefs().getStringSet(todoDataKey, null);

        if(todoDataSet != null && !todoDataSet.isEmpty()){
            for(String todoDataString : todoDataSet){
                String[] parts = todoDataString.split(dataSeparator, -1);
                ToDoData tdd = tryGetToDoData(parts, 3);

                if(tdd != null){
                    todoData.put(tdd.getTitle(), tdd);
                }
            }
        }
    }


    private ToDoData tryGetToDoData(String[] savedToDoDataParts, int length){
        ToDoData tdd = null;

        if(savedToDoDataParts.length == length){
            boolean dataValid = !savedToDoDataParts[0].isEmpty();
            if(dataValid){
                try{
                    int priority = Integer.parseInt(savedToDoDataParts[1]);
                    long editTime = Long.parseLong(savedToDoDataParts[2]);

                    tdd = new ToDoData(savedToDoDataParts[0], priority, editTime);
                }catch (NumberFormatException e){
                    e.printStackTrace();
                }
            }
        }

        return tdd;
    }

    private NoteData tryGetNoteData(String[] savedNoteDataParts, int length){
        NoteData nd = null;

        if(savedNoteDataParts.length == length){
            boolean dataValid = !savedNoteDataParts[0].isEmpty();
            if(dataValid){
                try{
                    int priority = Integer.parseInt(savedNoteDataParts[1]);
                    long editTime = Long.parseLong(savedNoteDataParts[2]);

                    nd = new NoteData(savedNoteDataParts[0], priority, editTime, savedNoteDataParts[3]);
                }catch (NumberFormatException e){
                    e.printStackTrace();
                }
            }
        }

        return nd;
    }

    private String getNoteDataString(NoteData nd){
        StringBuilder sb = new StringBuilder();
        sb.append(nd.getTitle()).append(dataSeparator).append(nd.getPriority()).append(dataSeparator);
        sb.append(nd.getEditTime()).append(dataSeparator).append(nd.getContent());
        return sb.toString();
    }

    private String getToDoDataString(ToDoData tdd){
        StringBuilder sb = new StringBuilder();
        sb.append(tdd.getTitle()).append(dataSeparator).append(tdd.getPriority()).append(dataSeparator);
        sb.append(tdd.getEditTime());
        return sb.toString();
    }

    public synchronized static DataProvider getInstance() {
        if (instance == null) {
            instance = new DataProvider();
        }
        return instance;
    }
}
