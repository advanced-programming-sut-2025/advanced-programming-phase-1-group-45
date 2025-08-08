package com.proj.View;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.proj.Control.AvatarCreationController;
import com.proj.Model.Character;
import com.proj.Map.farmName;

public class AvatarCreationView implements Screen {
    private Stage stage;
    private Character character;
    private Image characterPreview;
    private TextField nameField;
    private TextField farmField;
    private Table characterGrid;
    private Table animalGrid;
    private Texture bgTexture;
    private Texture overlayTexture;
    private Image animalPreview;

    private Table farmGrid;
    private Image farmPreview;
    private Label farmNameLabel;
    private Label farmDescription;
    private ImageButton[] farmButtons;
    private ImageButton lastSelectedFarmButton;

    public AvatarCreationView(AvatarCreationController controller, Character character, Skin skin) {
        this.character = character;

        debugCheckAssets();

        stage = new Stage(new ScreenViewport());

        try {
            bgTexture = new Texture(Gdx.files.internal("AvatarMenuBackground.jpg"));
            Image bgImage = new Image(bgTexture);
            bgImage.setFillParent(true);
            stage.addActor(bgImage);

            Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pm.setColor(0, 0, 0, 0.7f);
            pm.fill();
            overlayTexture = new Texture(pm);
            pm.dispose();

            Image overlay = new Image(overlayTexture);
            overlay.setFillParent(true);
            stage.addActor(overlay);
        } catch (Exception e) {
            Gdx.app.error("BACKGROUND", "Failed to load background or create overlay", e);
        }

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.pad(30);

        characterPreview = new Image(new TextureRegionDrawable(new TextureRegion(character.getCharacterTexture())));

        animalPreview = new Image(new TextureRegionDrawable(new TextureRegion(character.getFavoriteAnimal())));

        nameField = new TextField(character.getName(), skin);
        farmField = new TextField(character.getFarmName(), skin);

        characterGrid = new Table();
        animalGrid = new Table();
        createCharacterGrid(skin);
        createAnimalGrid(skin);

        mainTable.add(new Label("Select Character:", skin)).colspan(2).padBottom(10).row();
        mainTable.add(characterPreview).size(150, 200).colspan(2).padBottom(20).row();
        mainTable.add(characterGrid).colspan(2).padBottom(20).row();

        mainTable.add(new Label("Favorite Animal:", skin)).colspan(2).padBottom(10).row();
        mainTable.add(animalPreview).size(100, 100).colspan(2).padBottom(10).row(); // ADDED ANIMAL PREVIEW
        mainTable.add(animalGrid).colspan(2).padBottom(20).row();

        mainTable.add(new Label("Your Name:", skin)).right().padRight(10);
        mainTable.add(nameField).left().width(200).padBottom(10).row();

        mainTable.add(new Label("Farm Name:", skin)).right().padRight(10);
        mainTable.add(farmField).left().width(200).padBottom(20).row();

        mainTable.add(new TextButton("Save", skin)).colspan(2).padTop(20).width(200).height(100);

        farmGrid = new Table();
        farmPreview = new Image();
        farmNameLabel = new Label("", skin);
        farmDescription = new Label("", skin);
        farmDescription.setWrap(true);

        createFarmGrid(skin);
        updateFarmSelection(character.getFarmType());

        mainTable.add(new Label("Choose Farm:", skin)).colspan(2).padBottom(10).row();
        mainTable.add(farmGrid).colspan(2).padBottom(10).row();
        mainTable.add(farmPreview).size(120, 120).colspan(2).padBottom(10).row();
        mainTable.add(farmNameLabel).colspan(2).padBottom(5).row();
        mainTable.add(farmDescription).colspan(2).width(400).padBottom(20).row();
        stage.addActor(mainTable);
        controller.setView(this);
    }

    private void createFarmGrid(Skin skin) {
        farmGrid.clear();
        for (farmName farm : farmName.values()) {
            try {
                Texture texture = new Texture(Gdx.files.internal("FarmIcons/" + farm.name() + ".png"));
                ImageButton btn = new ImageButton(new TextureRegionDrawable(new TextureRegion(texture)));
                btn.setName(farm.name());

                farmGrid.add(btn).size(60, 60).pad(5);
            } catch (Exception e) {
                Gdx.app.error("FARM_GRID", "Failed to load icon for: " + farm.name(), e);
            }
        }
    }

    public void updateFarmSelection(farmName farm) {
        try {
            Texture texture = new Texture(Gdx.files.internal("FarmIcons/" + farm.name() + ".png"));
            farmPreview.setDrawable(new TextureRegionDrawable(new TextureRegion(texture)));

            farmNameLabel.setText(farm.getFarmName());
            farmDescription.setText(farm.getDescription());
        } catch (Exception e) {
            Gdx.app.error("FARM_PREVIEW", "Failed to load farm preview", e);
        }
    }

    public Table getFarmGrid() {
        return farmGrid;
    }

    private void debugCheckAssets() {
        String[] filesToCheck = {
            "characters/characters2.png",
            "characters/characters3.png",
            "characters/characters4.png",
            "characters/stand_down-removebg-preview.png",
            "animals/White_Chicken.png",
            "animals/Cat_1.png",
            "AvatarMenuBackground.jpg"
        };

        for (String path : filesToCheck) {
            boolean exists = Gdx.files.internal(path).exists();
            Gdx.app.log("ASSET CHECK", exists ? "✓ " + path : "✗ MISSING: " + path);
        }
    }

    private void createCharacterGrid(Skin skin) {
        characterGrid.clear();
        for (String characterName : Character.CHARACTER_OPTIONS) {
            try {
                Texture texture = new Texture(Gdx.files.internal("characters/" + characterName + ".png"));
                ImageButton btn = new ImageButton(new TextureRegionDrawable(new TextureRegion(texture)));
                btn.setName(characterName);
                characterGrid.add(btn).size(80, 100).pad(5);
            } catch (Exception e) {
                Gdx.app.error("CHARACTER GRID", "Failed to load: " + characterName, e);
            }
        }
    }

    private void createAnimalGrid(Skin skin) {
        animalGrid.clear();
        for (String animal : Character.ANIMAL_OPTIONS) {
            try {
                Texture texture = new Texture(Gdx.files.internal("animals/" + animal + ".png"));
                ImageButton btn = new ImageButton(new TextureRegionDrawable(new TextureRegion(texture)));
                btn.setName(animal);
                animalGrid.add(btn).size(64).pad(5);
            } catch (Exception e) {
                Gdx.app.error("ANIMAL GRID", "Failed to load: " + animal, e);
            }
        }
    }

    public void updateCharacterPreview(Texture texture) {
        characterPreview.setDrawable(new TextureRegionDrawable(new TextureRegion(texture)));
    }

    public void updateAnimalPreview(Texture texture) {
        animalPreview.setDrawable(new TextureRegionDrawable(new TextureRegion(texture)));
    }

    public Stage getStage() { return stage; }
    public TextField getNameField() { return nameField; }
    public TextField getFarmField() { return farmField; }
    public Table getCharacterGrid() { return characterGrid; }
    public Table getAnimalGrid() { return animalGrid; }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        if (bgTexture != null) bgTexture.dispose();
        if (overlayTexture != null) overlayTexture.dispose();
        stage.dispose();
    }

    private void buildFarmSection(Table table, Skin skin) {
        Image farmPreview = new Image();
        Label farmNameLabel = new Label("", skin);
        Label farmDescription = new Label("", skin);
        farmDescription.setWrap(true);

        Table farmGrid = new Table();
        for (final farmName farm : farmName.values()) {
            try {
                final Texture texture = new Texture(Gdx.files.internal("FarmIcons/" + farm.name() + ".png"));
                ImageButton btn = new ImageButton(new TextureRegionDrawable(new TextureRegion(texture)));
                btn.setName(farm.name());

                btn.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        farmPreview.setDrawable(new TextureRegionDrawable(new TextureRegion(texture)));
                        farmNameLabel.setText(farm.getFarmName());
                        farmDescription.setText(farm.getDescription());

                        character.setFarmType(farm);

                        for (Actor actor : farmGrid.getChildren()) {
                            actor.setColor(Color.WHITE);
                        }
                        btn.setColor(Color.GOLD); // Highlight

                        Gdx.app.log("DEBUG", "Selected: " + farm.name());
                    }
                });

                farmGrid.add(btn).size(80, 80).pad(5);

            } catch (Exception e) {
                Gdx.app.error("ERROR", "Failed to load: " + farm.name(), e);
            }
        }

        if (farmName.values().length > 0) {
            farmName firstFarm = farmName.values()[0];
            Texture texture = new Texture(Gdx.files.internal("FarmIcons/" + firstFarm.name() + ".png"));
            farmPreview.setDrawable(new TextureRegionDrawable(new TextureRegion(texture)));
            farmNameLabel.setText(firstFarm.getFarmName());
            farmDescription.setText(firstFarm.getDescription());
            character.setFarmType(firstFarm);
        }

        table.add(new Label("Choose Farm:", skin)).colspan(2).row();
        table.add(farmGrid).colspan(2).row();
        table.add(farmPreview).size(120, 120).colspan(2).row();
        table.add(farmNameLabel).colspan(2).row();
        table.add(farmDescription).colspan(2).width(400).row();
    }

    private void resetFarmButtonColors() {
        if (farmButtons != null) {
            for (ImageButton btn : farmButtons) {
                if (btn != null) {
                    btn.setColor(Color.WHITE);
                }
            }
        }
    }

    private void updateFarmSelection(farmName farm, Image preview, Label nameLabel, Label descLabel) {
        try {
            Texture texture = new Texture(Gdx.files.internal("FarmIcons/" + farm.name() + ".png"));
            preview.setDrawable(new TextureRegionDrawable(new TextureRegion(texture)));
            nameLabel.setText(farm.getFarmName());
            descLabel.setText(farm.getDescription());
        } catch (Exception e) {
            Gdx.app.error("FARM_PREVIEW", "Failed to load: " + farm.name(), e);
        }
    }

    private void updateFarmPreview(Image preview, Label nameLabel, Label descLabel, farmName farm) {
        try {
            Texture texture = new Texture(Gdx.files.internal("FarmIcons/" + farm.name() + ".png"));
            preview.setDrawable(new TextureRegionDrawable(new TextureRegion(texture)));
            nameLabel.setText(farm.getFarmName());
            descLabel.setText(farm.getDescription());
        } catch (Exception e) {
            Gdx.app.error("FARM_PREVIEW", "Failed to load preview for: " + farm.name(), e);
        }
    }
}
