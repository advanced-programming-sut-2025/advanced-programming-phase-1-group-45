package com.proj.Control;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.proj.Main;
import com.proj.Model.GameAssetManager;
import com.proj.View.AvatarCreationView;
import com.proj.View.ChangeInfoMenuView;
import com.proj.View.MainMenuView;
import com.proj.View.MultiPlayerMainMenu;

public class MultiPlayerMainMenuController {
    private MultiPlayerMainMenu view;
    private Skin skin;

    public void setView(MultiPlayerMainMenu view) {
        this.view = view;
        this.skin = GameAssetManager.getGameAssetManager().getSkin();
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
                Main.getMain().setScreen(new ChangeInfoMenuView(
                    new ChangeInfoController(), // Controller will use Session.getDatabaseHelper()
                    skin
                ));
            }
        });
    }

    public void handleLoadGameButton() {
        view.getLoadGameButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
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
