package com.proj.Control;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.proj.Main;
import com.proj.View.MainMenuView;

public class MainMenuController {
    private MainMenuView view;

    public void setView(MainMenuView view) {
        this.view = view;
    }

    public void handleProfileButton() {
        view.getProfileButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                com.proj.Model.Character newCharacter = new com.proj.Model.Character();
                AvatarCreationController controller = new AvatarCreationController(newCharacter);

                Main.getMain().setScreen(new AvatarCreationView(controller, newCharacter, skin));
                System.out.println("Profile button clicked");
            }
        });
    }

    public void handleNewGameButton() {
        view.getNewGameButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //Main.getMain().switchToGameScreen();// kar nemikoneeeee
                Main.getMain().setScreen(new ChangeInfoMenuView(new ChangeInfoController(), skin));
            }
        });
    }

    public void handleLoadGameButton() {
        view.getLoadGameButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Placeholder: Load game logic
                System.out.println("Load last game clicked");
            }
        });
    }

    public void handleExitButton() {
        view.getExitButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
    }
}
