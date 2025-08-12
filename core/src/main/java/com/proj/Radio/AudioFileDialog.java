package com.proj.Radio;

import com.badlogic.gdx.Graphics;
import java.awt.Frame;
import java.io.File;
import java.lang.reflect.Method;

public class AudioFileDialog {
    private final java.awt.FileDialog dialog;

    public AudioFileDialog(Graphics graphics) {
        Frame frame = getFrameFromGraphics(graphics);
        dialog = new java.awt.FileDialog(frame);
    }

    private Frame getFrameFromGraphics(Graphics graphics) {
        try {
            Method getWindowMethod = graphics.getClass().getMethod("getWindow");
            Object window = getWindowMethod.invoke(graphics);

            Method getWindowHandleMethod = window.getClass().getMethod("getWindowHandle");
            return (Frame) getWindowHandleMethod.invoke(window);
        } catch (Exception e) {
            return new Frame();
        }
    }

    public void setTitle(String title) {
        dialog.setTitle(title);
    }

    public void setMultipleMode(boolean multiple) {
        dialog.setMultipleMode(multiple);
    }

    public void setFilenameFilter(FilenameFilter filter) {
        dialog.setFilenameFilter((dir, name) ->
            filter.accept(new File(dir, name).getAbsolutePath(), name));
    }

    public void show() {
        dialog.setVisible(true);
    }

    public String[] getFiles() {
        java.io.File[] files = dialog.getFiles();
        if (files != null && files.length > 0) {
            String[] paths = new String[files.length];
            for (int i = 0; i < files.length; i++) {
                paths[i] = files[i].getAbsolutePath();
            }
            return paths;
        }
        return null;
    }

    public interface FilenameFilter {
        boolean accept(String filePath, String name);
    }
}
