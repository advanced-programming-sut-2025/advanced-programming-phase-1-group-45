package com.proj.Control;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.proj.Main;
import com.proj.View.*;
import com.proj.Model.*;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.graphics.Color;

//help
public class SignupMenuController {
    private SignupMenuView view;
    private Skin skin;

    public void setView(SignupMenuView view) {
        this.view = view;
        this.skin = GameAssetManager.getGameAssetManager().getSkin();
    }

    /*public void handleGuestButton() {
        if (view != null) {
            view.getGuestButton().addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Main.getMain().getScreen().dispose();
                    Main.getMain().setScreen(new PreGameMenuView(new PreGameMenuController(), skin));
                }
            });
        }
    }*/
    public void handleGuestButton() {
        if (view != null) {
            view.getGuestButton().addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    // Go directly to game screen for guests
                    Main.getMain().switchToGameScreen();
                }
            });
        }
    }


    /*public void handleSignupMenuButton() {
        if (view != null) {
            view.getSignUpButton().addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    String username = view.getUsername().getText();
                    String password = view.getPassword().getText();
                    String securityAnswer = view.getSecurityQuestion().getText();

                    if (signup(username, password, securityAnswer)) {
                        Main.getMain().getScreen().dispose();
                        Main.getMain().setScreen(new LoginMenuView(new LoginMenuController(), skin));
                    }
                }
            });
        }
    }*/

    public void handleSignupMenuButton() {
        if (view != null) {
            view.getSignUpButton().addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    String username = view.getUsername().getText();
                    String password = view.getPassword().getText();
                    String securityAnswer = view.getSecurityQuestion().getText();

                    if (signup(username, password, securityAnswer)) {
                        // Switch directly to login screen after successful registration
                        Main.getMain().setScreen(new LoginMenuView(
                            new LoginMenuController(),
                            skin
                        ));
                    }
                }
            });
        }
    }


    public boolean signup(String username, String password, String securityAnswer) {
        if (!Authenticator.isUsernameUnique(username)) {
            view.getErrorMessage().setText("Username is already taken!");
            return false;
        }
        else if (!Authenticator.isPasswordStrong(password)) {
            view.getErrorMessage().setText("Password is weak!");
            return false;
        }
        else {
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setSecurityAnswer(securityAnswer);

            App.addUser(user);
            return true;
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
}
