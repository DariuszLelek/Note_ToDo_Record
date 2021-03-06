package com.omikronsoft.notepad.utils;

import android.media.MediaPlayer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dariusz Lelek on 5/27/2017.
 * dariusz.lelek@gmail.com
 */

public class AudioPlayer {
    private static AudioPlayer instance;
    private final List<MediaPlayer> activeMedia;

    private AudioPlayer() {
        activeMedia = new ArrayList<>();
    }

    public void playWithOffset(MediaPlayer player, int offset) {
        if (!activeMedia.contains(player)) {
            activeMedia.add(player);
        }

        if (player.isPlaying()) {
            player.pause();
        }

        player.seekTo(offset);
        player.start();
    }

    public void stopAll() {
        for (MediaPlayer player : activeMedia) {
            if (player.isPlaying()) {
                player.pause();
                player.seekTo(0);
            }
        }
    }

    public void stopMedia(MediaPlayer media) {
        if (media != null && media.isPlaying()) {
            media.pause();
        }
    }

    public synchronized static AudioPlayer getInstance() {
        if (instance == null) {
            instance = new AudioPlayer();
        }
        return instance;
    }
}
