package com.omikronsoft.notepad.utils;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import com.omikronsoft.notepad.ApplicationContext;

import java.io.File;
import java.io.IOException;

/**
 * Created by Dariusz Lelek on 6/3/2017.
 * dariusz.lelek@gmail.com
 */

public class RecordingHelper {
    private static RecordingHelper instance;
    private boolean recording;
    private MediaRecorder recorder;
    private String pendingFileName;
    private File pendingRecordFile;
    private MediaPlayer media;


    private RecordingHelper(){
        recording = false;


        pendingFileName = "RECORDING_.m4a";
    }

    public synchronized boolean isRecording() {
        return recording;
    }

    public synchronized void setRecording(boolean recording) {
        this.recording = recording;
    }

    private MediaRecorder getRecorder(){
        MediaRecorder recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.HE_AAC);
            recorder.setAudioEncodingBitRate(48000);
        } else {
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            recorder.setAudioEncodingBitRate(64000);
        }

        recorder.setAudioSamplingRate(16000);

        pendingRecordFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath().toString()
                + "/Voice Recorder/" + pendingFileName);

        pendingRecordFile.getParentFile().mkdirs();
        recorder.setOutputFile(pendingRecordFile.getAbsolutePath());

        return recorder;
    }

    public void playFile(){
        media = new MediaPlayer();
        Uri url = Uri.parse(pendingRecordFile.getAbsolutePath());
        media = MediaPlayer.create(ApplicationContext.get(), url);
        media.seekTo(0);
        media.start();
    }

    public void stopFile(){
        if(media != null){
            media.pause();
        }
    }

    public void deleteFile(){
        stopFile();
        pendingRecordFile.delete();
    }

    public synchronized void startRecording() {
        recorder = getRecorder();
        try {
            recording = true;
            recorder.prepare();
            recorder.start();
        } catch (IOException e) {
            recording = false;
            e.printStackTrace();
        }
    }

    public synchronized void stopRecording() {
        recording = false;
        recorder.stop();
        recorder.release();
    }

    public synchronized static RecordingHelper getInstance() {
        if (instance == null) {
            instance = new RecordingHelper();
        }
        return instance;
    }
}
