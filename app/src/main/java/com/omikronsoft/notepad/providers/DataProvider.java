package com.omikronsoft.notepad.providers;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;

import com.omikronsoft.notepad.ApplicationContext;
import com.omikronsoft.notepad.Globals;
import com.omikronsoft.notepad.ListItemType;
import com.omikronsoft.notepad.R;
import com.omikronsoft.notepad.containers.Content;
import com.omikronsoft.notepad.containers.ItemData;
import com.omikronsoft.notepad.containers.Priority;
import com.omikronsoft.notepad.utils.RecordingHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.omikronsoft.notepad.ListItemType.NOTE_ITEM;
import static com.omikronsoft.notepad.ListItemType.RECORD_ITEM;
import static com.omikronsoft.notepad.ListItemType.TODO_ITEM;

/**
 * Created by Dariusz Lelek on 5/30/2017.
 * dariusz.lelek@gmail.com
 */

public class DataProvider {
    private static DataProvider instance;
    private Map<ListItemType, Map<String, ItemData>> itemData;

    private String dataSeparator, noteContentPrefix;
    private Globals globals;

    private DataProvider(){
        globals = Globals.getInstance();
        itemData = new HashMap<>();
        dataSeparator = globals.getRes().getString(R.string.shared_pref_data_separator);
        noteContentPrefix = globals.getRes().getString(R.string.note_content_key_prefix);

        itemData.put(NOTE_ITEM, new HashMap<String, ItemData>());
        itemData.put(TODO_ITEM, new HashMap<String, ItemData>());
        itemData.put(RECORD_ITEM, new HashMap<String, ItemData>());

        loadItemData();
    }

    public int getTotalItems(ListItemType itemType){
        return itemData.get(itemType).size();
    }


    public void addItemData(ItemData id){
        itemData.get(id.getItemType()).put(id.getTitle(), id);
        saveItemData(id.getItemType());
    }

    public boolean itemExists(String title, ListItemType itemType){
        return itemData.get(itemType).containsKey(title);
    }

    public void updateItemData(String oldItemTitle, ItemData id){
        ListItemType itemType = id.getItemType();
        itemData.get(itemType).remove(oldItemTitle);
        itemData.get(itemType).put(id.getTitle(), id);
        saveItemData(itemType);
    }

    public void deleteItem(ListItemType itemType, String title){
        itemData.get(itemType).remove(title);
        saveItemData(itemType);
    }

    public ItemData getItemData(ListItemType itemType, String title){
        return itemData.get(itemType).get(title);
    }

    public List<String> getTitleListByPriority(ListItemType itemType){
        List<String> result = new ArrayList<>();
        int selectedPriority = Globals.getInstance().getSelectedPriority().getValue();

        for(Map.Entry<String, ItemData> pair : itemData.get(itemType).entrySet()){
            if(pair.getValue().getPriority() >= selectedPriority){
                result.add(pair.getKey());
            }
        }

        Collections.sort(result);
        return result;
    }

    public void saveItemData(ListItemType itemType){
        Set<String> dataSet = new HashSet<>();
        String dataKey = itemType.getPrefsKey();
        SharedPreferences.Editor edit = globals.getPrefs().edit();

        for(ItemData id : itemData.get(itemType).values()){
            dataSet.add(getItemDataString(id));
            saveContent(edit, id);
        }

        edit.putStringSet(dataKey, dataSet);
        edit.apply();
    }

    private String getItemDataString(ItemData id){
        return id.getTitle() + dataSeparator + id.getPriority() + dataSeparator + id.getEditTime();
    }

    private void loadItemData(){
        for(ListItemType itemType : ListItemType.values()){

            String prefsKey = itemType.getPrefsKey();
            Set<String> savedDataSet = globals.getPrefs().getStringSet(prefsKey, null);

            if(savedDataSet != null && !savedDataSet.isEmpty()){
                for(String dataString : savedDataSet){
                    String[] parts = dataString.split(dataSeparator, -1);

                    if(parts.length == ItemData.SAVE_DATA_LENGTH){
                        String title = parts[0];
                        if(!title.isEmpty()){
                            try{
                                int priority = Integer.parseInt(parts[1]);
                                long editTime = Long.parseLong(parts[2]);
                                Content content = getContent(itemType, title);

                                if(content != null){
                                    ItemData id = new ItemData(itemType, title, priority, editTime);
                                    id.setContent(content);
                                    itemData.get(itemType).put(title, id);
                                }
                            }catch (NumberFormatException e){
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }

    private void saveContent(SharedPreferences.Editor edit, ItemData id){
        switch (id.getItemType()){
            case NOTE_ITEM:
                edit.putString(noteContentPrefix+id.getTitle(), id.getContent().getNoteContent());
                break;
            case RECORD_ITEM:
                // files are saved at record time when dialog is displayed
                break;
            default:
            case TODO_ITEM:
                break;
        }
    }

    private Content getContent(ListItemType itemType, String title){
        Content content = null;

        switch (itemType){
            case NOTE_ITEM:
                content = new Content();
                content.setNoteContent(globals.getPrefs().getString(noteContentPrefix+title, ""));
                break;
            case RECORD_ITEM:
                File recordFile = RecordingHelper.getInstance().getRecordFile(title);
                if(recordFile != null){
                    Uri url = Uri.parse(recordFile.getAbsolutePath());
                    MediaPlayer media = MediaPlayer.create(ApplicationContext.get(), url);
                    if(media != null){
                        content = new Content();
                        content.setRecordContent(media);
                    }
                }
                break;
            default:
            case TODO_ITEM:
                content = new Content();
                break;
        }

        return content;
    }

    public synchronized static DataProvider getInstance() {
        if (instance == null) {
            instance = new DataProvider();
        }
        return instance;
    }
}
