package com.proj.Model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.proj.Model.inventoryItems.crops.CropData;
import com.proj.Model.inventoryItems.crops.CropRegistry;


public class CropInfoWindow extends Window {
    private final Table contentTable;
    private final Skin skin;
    private final TextButton closeButton;
    private final int COLUMNS = 5;
    private CropData selectedCrop;
    private TextButton plantButton;


    public CropInfoWindow(Skin skin) {
        super("Crop Info", skin);
        getTitleLabel().setAlignment(Align.center);
        this.skin = skin;
        setModal(false);
        setMovable(false);
        setResizable(false);
        setSize(800 + 100 + 200, 600 + 100);
        centerWindow();

        contentTable = new Table();
        contentTable.top().left();
        contentTable.pad(10);

        ScrollPane scrollPane = new ScrollPane(contentTable, skin);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollBarPositions(false, true);

        closeButton = new TextButton("Close", skin);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setVisible(false);
            }
        });

        add(scrollPane).grow().pad(10).row();
        add(closeButton).padBottom(10).padRight(10).right();

        updateDisplay();
    }

    public void centerWindow() {
        setPosition(
            (Gdx.graphics.getWidth() - getWidth()) / 2,
            (Gdx.graphics.getHeight() - getHeight()) / 2
        );
    }

    public void updateDisplay() {
        contentTable.clear();

        Array<CropData> crops = getPlayerCrops();
        if (crops.size == 0) {
            contentTable.add(new Label("No crops available", skin)).row();
            return;
        }

        float iconSize = 80;
        float spacing = 50;

        int rowCount = (int) Math.ceil((double) crops.size / COLUMNS);
        float windowHeight = 400 + (rowCount * (iconSize + 50));
        setHeight(Math.min(600, windowHeight));
        centerWindow();


        Table rowTable = new Table();
        rowTable.defaults().pad(spacing).center();


        for (int i = 0; i < crops.size; i++) {
            CropData crop = crops.get(i);

            Table cropCell = createCropCell(crop, iconSize);

            rowTable.add(cropCell).size(iconSize + 20, iconSize + 40).pad(spacing);

            if ((i + 1) % COLUMNS == 0 || i == crops.size - 1) {
                contentTable.add(rowTable).padBottom(20).row(); // فاصله بین ردیف‌ها
                rowTable = new Table();
                rowTable.defaults().pad(spacing).center();

                if (i != crops.size - 1) {
                    contentTable.row();
                }
            }
        }
    }

    private Table createCropCell(CropData crop, float iconSize) {
        Table cell = new Table();
        cell.setBackground(skin.getDrawable("background"));
        cell.pad(10);
        Image icon = new Image(GameAssetManager.getGameAssetManager().getTexture(crop.getProductTexturePath()));
        icon.setScaling(Scaling.fit);
        cell.add(icon).size(iconSize).padTop(5).row();

        String displayName = crop.getName();
        if (displayName.contains(" Crop")) {
            displayName = displayName.substring(0, displayName.indexOf(" Crop"));
        }
        cell.pad(10);

        Label nameLabel = new Label(displayName, skin, "default");
        nameLabel.setEllipsis(true);
        nameLabel.setFontScale(0.7f);
        nameLabel.setAlignment(Align.center);
        cell.add(nameLabel).padTop(5).growX().row();

        cell.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selectCrop(crop);
            }
        });

        return cell;
    }


    public CropData getSelectedCrop() {
        return selectedCrop;
    }

    private Array<CropData> getPlayerCrops() {
        Array<CropData> crops = new Array<>();
        for (CropData item : CropRegistry.getInstance().getAllCropData()) {
            crops.add((CropData) item);
        }
        return crops;
    }


    private void selectCrop(CropData cropItem) {
        selectedCrop = cropItem;
        new CropDetailDialog(cropItem, skin).show(getStage());

    }

    public CropData getSelectedCropData() {
        return selectedCrop;
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            updateDisplay();
            toFront();
        }
    }

    private class CropDetailDialog extends Dialog {
        private final CropData cropItem;

        public CropDetailDialog(CropData cropItem, Skin skin) {
            super("", skin);
            this.cropItem = cropItem;
            buildDialog();
        }

        private void buildDialog() {
            pad(20);
            getContentTable().defaults().pad(10);

            getTitleLabel().setText(cropItem.getName());
            getTitleLabel().setAlignment(Align.center);

            StringBuilder stage = new StringBuilder();
            for (int i = 0; i < cropItem.getGrowthStages().length; i++) {
                stage.append(cropItem.getGrowthStages()[i] + ", ");
            }
            stage.setLength(stage.length() - 2);
            getContentTable().add(new Label("Growth Stages: " + stage.toString(), skin)).left().row();
            getContentTable().add(new Label("Total Time: " + cropItem.getTotalGrowthTime() + " days", skin)).left().row();

            String harvestType = cropItem.isOneTimeHarvest() ? "Single Harvest" : "Regrows every " + cropItem.getRegrowthTime() + " days";
            getContentTable().add(new Label("Harvest: " + harvestType, skin)).left().row();

            stage = new StringBuilder();
            for (int i = 0; i < cropItem.getSeason().size; i++) {
                stage.append(cropItem.getSeason().get(i) + ", ");
            }
            stage.setLength(stage.length() - 2);

            getContentTable().add(new Label("Season: " + stage, skin)).left().row();
            getContentTable().add(new Label("Base Price: " + cropItem.getBaseSellPrice() + " $", skin)).left().row();
            getContentTable().add(new Label("Edible: " + (cropItem.isEdible() ? "Yes" : "No"), skin)).left().row();
            if (cropItem.isEdible()) {
                getContentTable().add(new Label("Energy: " + cropItem.getEnergy(), skin)).left().row();
            }
            TextButton closeButton = new TextButton("Close", skin);
            closeButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    hide();
                }
            });

            getButtonTable().defaults().pad(10).minWidth(100);
            getButtonTable().add(plantButton);
            getButtonTable().add(closeButton);
        }

        @Override
        public float getPrefWidth() {
            return 700;
        }

        @Override
        public float getPrefHeight() {
            return 700;
        }
    }
}


