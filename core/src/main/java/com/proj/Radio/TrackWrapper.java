package com.proj.Radio;

import com.badlogic.gdx.audio.Music;
import java.io.File;

public class TrackWrapper {
    private final Music music;
    private final File file;

    public TrackWrapper(Music music, File file) {
        this.music = music;
        this.file = file;
    }

    public Music getMusic() {
        return music;
    }

    public File getFile() {
        return file;
    }

    public String getFileName() {
        return file.getName();
    }
}
