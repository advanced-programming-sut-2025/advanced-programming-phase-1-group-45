package com.proj.Control;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.proj.Main;
import com.proj.View.*;
import com.proj.Model.*;
import com.proj.network.lobby.LobbyScreen;
import com.proj.Database.DatabaseHelper;
import com.proj.network.client.NetworkEventListener;
import com.proj.network.event.NetworkEvent;

public class LoginMenuController {
    private LoginMenuView view;
    private Skin skin;
    private final DatabaseHelper dbHelper = new DatabaseHelper();


    public void setView(LoginMenuView view) {
        this.view = view;
        this.skin = GameAssetManager.getGameAssetManager().getSkin();
        dbHelper.connect();
    }

    public void handleForgetPasswordButton() {
        // Fixed: Use proper click listener instead of isChecked()
        view.getForgotPasswordButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Main.getMain().getScreen().dispose();
                Main.getMain().setScreen(new ForgetPasswordView(new ForgetPasswordController(), skin));
            }
        });
    }

    // In LoginMenuController
    public void handleLoginButton() {
        view.getLoginButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String identifier = view.getUsername().getText();
                String password = view.getPassword().getText();

                User authenticatedUser = dbHelper.authenticateUser(identifier, password);
                if (authenticatedUser != null) {
                    // Proceed to game screen with authenticated user
                    Main.getMain().setScreen(new LobbyScreen(Main.getMain()));
                    //Main.getMain().setScreen(new MainMenuView(new MainMenuController(), skin));
                } else {
                    view.getErrorMessage().setText("Invalid credentials");
                }
            }
        });
    }
}
//Main.getMain().setScreen(new LobbyScreen(Main.getMain()));



