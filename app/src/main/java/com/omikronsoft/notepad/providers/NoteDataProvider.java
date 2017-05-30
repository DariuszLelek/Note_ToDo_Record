package com.omikronsoft.notepad.providers;

import com.omikronsoft.notepad.Globals;
import com.omikronsoft.notepad.containers.NoteData;
import com.omikronsoft.notepad.containers.Priority;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.R.attr.priority;

/**
 * Created by Dariusz Lelek on 5/30/2017.
 * dariusz.lelek@gmail.com
 */

public class NoteDataProvider {
    private static NoteDataProvider instance;
    private Map<String, NoteData> notesData;

    private NoteDataProvider(){
        notesData = new HashMap<>();

        String[] titles = {"some note", "some other", "some note long 2", "Aaaaa", "sasaaa"};
                //"sasa", "Sasasaaaa", "Saaaaa", "Sasaaaa", "Saaaaa", "Saasass", "SAsasas", "Sasasaa", "Sasaaaa", "Saaaa", "Sasssss", "Asasasas"};
        Priority[] priorities = {Priority.LOW, Priority.MEDIUM, Priority.HIGH, Priority.LOW, Priority.MEDIUM};
                //Priority.LOW, Priority.MEDIUM, Priority.HIGH, Priority.LOW, Priority.MEDIUM, Priority.MEDIUM, Priority.LOW, Priority.MEDIUM, Priority.HIGH, Priority.LOW, Priority.MEDIUM, Priority.HIGH};

        for(int i=0; i<titles.length; i++){
            NoteData nd = new NoteData(titles[i]);
            nd.setPriority(priorities[i]);
            notesData.put(titles[i], nd);
        }
    }

    public boolean noteExists(String title){
        return notesData.containsKey(title);
    }

    public Priority getNotePriority(String title){
        return notesData.get(title).getPriority();
    }

    public long getNoteEditTime(String title){
        return notesData.get(title).getEditTime();
    }

    public NoteData getNoteData(String title){
        return notesData.get(title);
    }

    public void deleteNote(String title){
        if(notesData.containsKey(title)){
            notesData.remove(title);
            // todo trigger save ?
        }
    }

    public List<String> getNotesTitle(){
        int selectedPriority = Globals.getInstance().getSelectedPriority().getValue();
        List<String> result = new ArrayList<>();
        for(Map.Entry<String, NoteData> pair : notesData.entrySet()){
            if(pair.getValue().getPriority().getValue() >= selectedPriority){
                result.add(pair.getKey());
            }
        }
        return result;
    }

    public synchronized static NoteDataProvider getInstance() {
        if (instance == null) {
            instance = new NoteDataProvider();
        }
        return instance;
    }
}
