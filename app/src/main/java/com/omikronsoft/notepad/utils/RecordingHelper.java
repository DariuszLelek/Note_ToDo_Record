package com.omikronsoft.notepad.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import com.omikronsoft.notepad.ApplicationContext;
import com.omikronsoft.notepad.Globals;
import com.omikronsoft.notepad.R;

import java.io.File;
import java.io.IOException;

/**
 * Created by Dariusz Lelek on 6/3/2017.
 * dariusz.lelek@gmail.com
 */

public class RecordingHelper {
    private static RecordingHelper instance;
    private MediaRecorder recorder;
    private String pendingFileName, fileExtension, fileNamePrefix;
    private File pendingRecordFile, saveFolder;
    private MediaPlayer pendingMediaPlayer;


    private final String FILE_NAME_SEPARATOR = "_";


    private RecordingHelper(){
        saveFolder = ApplicationContext.get().getExternalFilesDir(Context.STORAGE_SERVICE);
        fileNamePrefix = Globals.getInstance().getRes().getString(R.string.record_file_name_prefix);
        fileExtension = Globals.getInstance().getRes().getString(R.string.record_file_extension);
    }

    public void prepareRecordFile(){
        pendingFileName = fileNamePrefix + FILE_NAME_SEPARATOR + Globals.getInstance().getPendingRecFileNum() + fileExtension;
        pendingRecordFile = new File(saveFolder.getAbsolutePath() + "/" + pendingFileName);

        if(pendingRecordFile.exists()){
            pendingRecordFile.delete();
        }

        pendingMediaPlayer = null;
    }

    public File getRecordFile(String fileName){
        return new File(saveFolder.getAbsolutePath() + "/" + fileName + fileExtension);
    }

    public void changePendingFileName(String newName){
        File newFile = new File(pendingRecordFile.getParent() + "/" + newName + fileExtension);
        if(!pendingRecordFile.renameTo(newFile)){
            // todo add logger
        }
    }

    public MediaPlayer getPendingMediaPlayer() {
        return pendingMediaPlayer;
    }

    public String getRecordFileName(){
        return Utils.getInstance().getFileName(pendingRecordFile);
    }

    public synchronized boolean isRecording() {
        return recorder != null;
    }

    private MediaRecorder getRecorder(){
        MediaRecorder recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.HE_AAC);
            recorder.setAudioEncodingBitRate(48000);
        } else {
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            recorder.setAudioEncodingBitRate(64000);
        }

        recorder.setAudioSamplingRate(16000);
        recorder.setOutputFile(pendingRecordFile.getAbsolutePath());

        return recorder;
    }

    public void stopFile(){
        if(pendingMediaPlayer != null){
            pendingMediaPlayer.pause();
        }
    }

    public boolean isRecordEmpty(){
        return pendingMediaPlayer == null || pendingMediaPlayer.getDuration() == 0;
    }

    public void deleteFile(){
        stopFile();
        if(pendingRecordFile.delete()){
            pendingMediaPlayer = null;
        }
    }

    public synchronized void startRecording() {
        recorder = getRecorder();

        try {
            recorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        recorder.start();
    }

    public synchronized void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;

        if(pendingMediaPlayer == null){
            Uri url = Uri.parse(pendingRecordFile.getAbsolutePath());
            pendingMediaPlayer = MediaPlayer.create(ApplicationContext.get(), url);
        }
    }

    public synchronized static RecordingHelper getInstance() {
        if (instance == null) {
            instance = new RecordingHelper();
        }
        return instance;
    }
}
