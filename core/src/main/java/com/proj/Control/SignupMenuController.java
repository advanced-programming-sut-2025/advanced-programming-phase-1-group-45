package com.proj.Control;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.proj.Database.DatabaseHelper;
import com.proj.GameScreen;
import com.proj.Main;
import com.proj.View.*;
import com.proj.Model.*;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.graphics.Color;
import com.proj.map.farmName;

public class SignupMenuController {
    private SignupMenuView view;
    private Skin skin;
    private final DatabaseHelper dbHelper = new DatabaseHelper();

    public void setView(SignupMenuView view) {
        this.view = view;
        this.skin = GameAssetManager.getGameAssetManager().getSkin();
        dbHelper.connect();
    }
    public void handleGuestButton() {
        if (view != null) {
            view.getGuestButton().addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    // Go directly to game screen for guests
//                    Main.getMain().setScreen(new MainMenuView(new MainMenuController(), GameAssetManager.getGameAssetManager().getSkin()));
               Main.getMain().setScreen(new GameScreen(farmName.BEACH));
                }
            });
        }
    }

    public void handleSignupMenuButton() {
        if (view != null) {
            view.getSignUpButton().addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    String username = view.getUsername().getText();
                    String password = view.getPassword().getText();
                    String securityAnswer = view.getSecurityQuestion().getText();
                    // NEW FIELDS
                    String email = view.getEmailField().getText();
                    String nickname = view.getNicknameField().getText();
                    String gender = view.getGenderSelectBox().getSelected();

                    // UPDATED METHOD CALL
                    if (signup(username, password, securityAnswer, email, nickname, gender)) {
                        Main.getMain().setScreen(new LoginMenuView(
                            new LoginMenuController(),
                            skin
                        ));
                    }
                }
            });
        }
    }


    public boolean signup(String username, String password, String securityAnswer,
                          String email, String nickname, String gender) {
        // Add user to database
        boolean success = dbHelper.addUser(username, password, securityAnswer,
            email, nickname, gender);

        if (success) {
            Gdx.app.log("Signup", "User registered successfully: " + username);
            return true;
        } else {
            view.getErrorMessage().setText("Registration failed! Check logs for details.");
            return false;
        }
    }

    public void handleGeneratePasswordButton() {
        if (view != null && view.getGeneratePasswordButton() != null) {
            view.getGeneratePasswordButton().addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    String randomPassword = PasswordGenerator.generateRandomPassword();

                    view.getPassword().setText(randomPassword);

                    view.getPassword().setPasswordMode(false);

                    view.getPassword().setColor(Color.GREEN);

                    Timer.schedule(new Timer.Task() {
                        @Override
                        public void run() {
                            view.getPassword().setPasswordMode(true);
                            view.getPassword().setColor(Color.WHITE);
                        }
                    }, 3);
                }
            });
        }
    }
    public void handleLoginButton() {
        if (view != null && view.getLoginButton() != null) {
            view.getLoginButton().addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Main.getMain().setScreen(new LoginMenuView(new LoginMenuController(), skin));
                }
            });
        }
    }
}
