package com.proj.network.chat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.proj.Main;
import com.proj.Model.GameAssetManager;
import com.proj.network.client.ChatListener;
import com.proj.network.event.NetworkEvent;
import com.proj.network.message.JsonBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChatSystem {
    private final Stage stage;
    private final Skin skin;
    private final Main main;

    private Window chatWindow;
    private TextArea chatHistory;
    private TextField messageField;
    private ImageButton sendButton;
    private SelectBox<String> recipientSelect;
    private CheckBox privateCheckbox;

    private boolean isVisible = false;
    private final List<String> onlinePlayers = new ArrayList<>();
    private final List<ChatMessage> messages = new ArrayList<>();

    public ChatSystem(Main main, Stage stage) {
        this.main = main;
        this.stage = stage;
        this.skin = GameAssetManager.getGameAssetManager().getStardewSkin();
//        main.getGameClient().addChatListener(this);
        createUI();
        setupListeners();
    }

    private void createUI() {
        chatWindow = new Window("Chat", skin);
        chatWindow.setSize(600, 400);
        chatWindow.setPosition(20,20);
        chatWindow.setMovable(true);
        chatWindow.setResizable(true);

        chatHistory = new TextArea("", skin);
        chatHistory.setDisabled(true);

        messageField = new TextField("", skin);

        Texture sendIcon = new Texture("assets/send_icon.png");
        TextureRegion chatR = new TextureRegion(sendIcon);

        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.imageUp = new TextureRegionDrawable(chatR);

        sendButton = new ImageButton(style);
        sendButton.setSize(64, 64);

        recipientSelect = new SelectBox<>(skin);
        recipientSelect.setItems("Everyone");

        privateCheckbox = new CheckBox(" Private", skin);

        Table contentTable = new Table();
        contentTable.add(chatHistory).colspan(3).expand().fill().pad(5).row();

        Table inputTable = new Table();
        inputTable.add(privateCheckbox).left().padRight(45);
        inputTable.add(recipientSelect).width(300).padRight(5);
        contentTable.add(inputTable).colspan(3).left().padBottom(5).row();

        contentTable.add(messageField).expandX().fillX().pad(5);
        contentTable.add(sendButton).pad(5).right();

        chatWindow.add(contentTable).expand().fill();

        chatWindow.setVisible(false);
        stage.addActor(chatWindow);
    }

    private void setupListeners() {
        sendButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sendMessage();
            }
        });

        privateCheckbox.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                recipientSelect.setDisabled(!privateCheckbox.isChecked());
            }
        });

        messageField.setTextFieldListener((textField, c) -> {
            if (c == '\n') {
                sendMessage();
               // event.cancel();
            }
        });
    }

    public void toggle() {
        isVisible = !isVisible;
        chatWindow.setVisible(isVisible);
        chatWindow.center();
        if (isVisible) {
            stage.setKeyboardFocus(messageField);
        }
    }

    public void show() {
        isVisible = true;
        chatWindow.setVisible(true);
        stage.setKeyboardFocus(messageField);
    }

    public void hide() {
        isVisible = false;
        chatWindow.setVisible(false);
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void sendMessage() {
        String message = messageField.getText().trim();
        if (message.isEmpty()) {
            return;
        }

        boolean isPrivate = privateCheckbox.isChecked();
        String recipient = isPrivate ? recipientSelect.getSelected() : null;

        if (isPrivate && "Everyone".equals(recipient)) {
            addSystemMessage("Cannot send private message to Everyone");
            return;
        }

        if (main.getGameClient() != null && main.getGameClient().isConnected()) {
            main.getGameClient().sendChatMessage(message, isPrivate, recipient);

            if (isPrivate) {
                addMessage("You to " + recipient + " (private): " + message);
            } else {

                // The server will broadcast this back to all clients including us
            }

            // Clear the message field
            messageField.setText("");
            stage.setKeyboardFocus(messageField);
        } else {
            addSystemMessage("Not connected to server");
        }
    }

    public void receiveMessage(String sender, String message, boolean isPrivate) {
        String prefix = isPrivate ? "[Private] " : "";
        String formattedMessage = prefix + sender + ": " + message;
        addMessage(formattedMessage);
    }

    public void addSystemMessage(String message) {
        addMessage("[System] " + message);
    }

    private void addMessage(String message) {
        messages.add(new ChatMessage(message));
        updateChatHistory();
    }

    private void updateChatHistory() {
        StringBuilder sb = new StringBuilder();

        // Show last 50 messages at most
        int startIndex = Math.max(0, messages.size() - 50);

        for (int i = startIndex; i < messages.size(); i++) {
            if (i > startIndex) {
                sb.append("\n");
            }
            sb.append(messages.get(i).getMessage());
        }

        chatHistory.setText(sb.toString());
        chatHistory.setCursorPosition(chatHistory.getText().length());
    }

    public void updateOnlinePlayers(List<String> players) {
        onlinePlayers.clear();
        onlinePlayers.add("Everyone");
        onlinePlayers.addAll(players);
        System.err.println(players.size() + " players online");

        String currentSelection = recipientSelect.getSelected();
        recipientSelect.setItems(onlinePlayers.toArray(new String[0]));

        // Try to restore previous selection
        if (onlinePlayers.contains(currentSelection)) {
            recipientSelect.setSelected(currentSelection);
        } else {
            recipientSelect.setSelected("Everyone");
        }
    }

    private static class ChatMessage {
        private final String message;
        private final long timestamp;

        public ChatMessage(String message) {
            this.message = message;
            this.timestamp = System.currentTimeMillis();
        }

        public String getMessage() {
            return message;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }
}
