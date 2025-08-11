package com.proj.Control;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.proj.Model.Character;
import com.proj.View.AvatarCreationView;
import com.proj.View.MultiplayerAvatarMenu;

public class MultiPlayerAvatarController {
    private MultiplayerAvatarMenu view;
    private Character character;

    public MultiPlayerAvatarController(Character character) {
        this.character = character;
    }

    public void setView(MultiplayerAvatarMenu view) {
        this.view = view;
        setupEventListeners();
    }

    public void setupEventListeners() {
        for (Actor actor : view.getCharacterGrid().getChildren()) {
            if (actor instanceof ImageButton) {
                actor.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        String characterName = actor.getName();
                        try {
                            Texture newTexture = new Texture(Gdx.files.internal("characters/" + characterName + ".png"));
                            character.setCharacterTexture(newTexture);
                            view.updateCharacterPreview(newTexture);
                        } catch (Exception e) {
                            Gdx.app.error("CONTROLLER", "Failed to load character: " + characterName, e);
                        }
                    }
                });
            }
        }

        for (Actor actor : view.getAnimalGrid().getChildren()) {
            if (actor instanceof ImageButton) {
                actor.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        String animalName = actor.getName();
                        try {
                            Texture newTexture = new Texture(Gdx.files.internal("animals/" + animalName + ".png"));
                            character.setFavoriteAnimal(newTexture);
                            view.updateAnimalPreview(newTexture);
                        } catch (Exception e) {
                            Gdx.app.error("CONTROLLER", "Failed to load animal: " + animalName, e);
                        }
                    }
                });
            }
        }

        view.getNameField().setTextFieldListener((field, c) ->
            character.setName(field.getText()));

        view.getFarmField().setTextFieldListener((field, c) ->
            character.setFarmName(field.getText()));
    }
}
