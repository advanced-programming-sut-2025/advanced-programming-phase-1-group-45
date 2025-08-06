package com.proj.Control;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.proj.Main;
import com.proj.View.*;
import com.proj.Model.*;
import com.proj.network.client.NetworkEventListener;
import com.proj.network.event.NetworkEvent;
import com.proj.network.lobby.LobbyScreen;

public class LoginMenuController {
    private LoginMenuView view;
    private Skin skin;

    public void setView(LoginMenuView view) {
        this.view = view;
        this.skin = GameAssetManager.getGameAssetManager().getSkin();
    }

    public void handleForgetPasswordButton() {
        if (view.getForgotPasswordButton().isChecked()) {
            Main.getMain().getScreen().dispose();
            Main.getMain().setScreen(new ForgetPasswordView(new ForgetPasswordController(), skin));
        }
    }

    /*public void handleLoginButton() {
        view.getLoginButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String username = view.getUsername().getText();
                String password = view.getPassword().getText();

                if (login(username, password)) {
                    Main.getMain().switchToGameScreen();
                } else {
                    view.getErrorMessage().setText("Invalid credentials!");
                }
            }
        });
    }*/
    public void handleLoginButton() {
        if (view != null) {
            view.getLoginButton().addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    String username = view.getUsername().getText();
                    String password = view.getPassword().getText();

                    Main.getMain().authenticate(username, password);

                    if (login(username, password)) {
//                        // After successful login, go to game screen
//                        // After successful login
                        Main.getMain().setScreen(new LobbyScreen(Main.getMain()));
//                       // Main.getMain().switchToGameScreen();// on github, not a comment
                    }
                }
            });
        }
    }


    /*public void handleLoginButton() {
        if (view.getLoginButton().isChecked()) {
            String username = view.getUsername().getText();
            String password = view.getPassword().getText();

            if (login(username, password)) {
                Main.getMain().getScreen().dispose();
                Main.getMain().setScreen(new PreGameMenuView(new PreGameMenuController(), skin));
            }
        }
    }*/

    private boolean login(String username, String password) {
        if (!Authenticator.existsUsername(username)) {
            view.getErrorMessage().setText("Username not found.");
            return false;
        }
        else {
            User user = Authenticator.authenticate(username, password);

            if (user == null) {
                view.getErrorMessage().setText("Wrong password.");
                return false;
            }
        }

        return true;
    }

}

