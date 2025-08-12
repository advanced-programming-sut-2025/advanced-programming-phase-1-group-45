package com.proj.Radio;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

public class TextInputDialog extends Dialog {
    private TextField inputField;
    private InputListener listener;

    public TextInputDialog(String title, String message, Skin skin) {
        super(title, skin);
        text(message);
        inputField = new TextField("", skin);
        getContentTable().add(inputField).padTop(10).fillX();
        button("OK", true);
        button("Cancel", false);
    }

    @Override
    protected void result(Object object) {
        if ((Boolean) object && listener != null) {
            listener.input(inputField.getText());
        }
    }

    public void setListener(InputListener listener) {
        this.listener = listener;
    }

    public interface InputListener {
        void input(String text);
    }
}
