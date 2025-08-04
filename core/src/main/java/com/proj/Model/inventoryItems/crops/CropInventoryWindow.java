package com.proj.Model.inventoryItems.crops;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.proj.Model.Inventory.InventoryItem;
import com.proj.Model.Inventory.InventoryManager;
import com.proj.Model.inventoryItems.CropItem;


public class CropInventoryWindow extends Window {
    private final Table contentTable;
    private final Skin skin;
    private final TextButton closeButton;
    private final int COLUMNS = 5;
    private CropItem selectedCrop;
    private TextButton plantButton;


    public CropInventoryWindow(Skin skin) {
        super("Crop Inventory", skin);
        getTitleLabel().setAlignment(Align.center);

        this.skin = skin;

        setModal(false);
        setMovable(false);
        setResizable(false);
        setSize(800 + 100 + 200 , 600 + 100);
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

        Array<CropItem> seeds = getPlayerCrops();
        if (seeds.size == 0) {
            contentTable.add(new Label("No crops available", skin)).row();
            return;
        }

        float iconSize = 80;
        float spacing = 50;

        int rowCount = (int) Math.ceil((double) seeds.size / COLUMNS);
        float windowHeight = 400 + (rowCount * (iconSize + 50));
        setHeight(Math.min(600, windowHeight));
        centerWindow();


        Table rowTable = new Table();
        rowTable.defaults().pad(spacing).center();


        for (int i = 0; i < seeds.size; i++) {
            CropItem seed = seeds.get(i);

            Table seedCell = createCropCell(seed, iconSize);

            rowTable.add(seedCell).size(iconSize + 20, iconSize + 40).pad(spacing);

            if ((i + 1) % COLUMNS == 0 || i == seeds.size - 1) {
                contentTable.add(rowTable).padBottom(20).row(); // فاصله بین ردیف‌ها
                rowTable = new Table();
                rowTable.defaults().pad(spacing).center();

                if (i != seeds.size - 1) {
                    contentTable.row();
                }
            }
        }
    }

    private Table createCropCell(CropItem seed, float iconSize) {
        Table cell = new Table();
        cell.setBackground(skin.getDrawable("background"));
        cell.pad(10);
        // آیکون دانه
        Image icon = new Image(seed.getTexture());
        icon.setScaling(Scaling.fit);
        cell.add(icon).size(iconSize).padTop(5).row();

        String displayName = seed.getName();
        if (displayName.contains(" Crop")) {
            displayName = displayName.substring(0, displayName.indexOf(" Crop"));
        }

        Label nameLabel = new Label(displayName, skin, "default");
        nameLabel.setEllipsis(true);
        nameLabel.setFontScale(0.7f);
        nameLabel.setAlignment(Align.center);
        cell.add(nameLabel).padTop(5).growX().row();

        Label countLabel = new Label(String.valueOf(seed.getQuantity()), skin, "default");
        cell.add(countLabel).padBottom(5);


        return cell;
    }


    public CropItem getSelectedCrop() {
        return selectedCrop;
    }

    private Array<CropItem> getPlayerCrops() {
        Array<CropItem> seeds = new Array<>();
        for (InventoryItem item : InventoryManager.getInstance().getPlayerInventory().getItems().values()) {
            if (item instanceof CropItem && item.getQuantity() > 0) {
                seeds.add((CropItem) item);
            }
        }
        seeds.sort((s1, s2) -> Integer.compare(s2.getQuantity(), s1.getQuantity()));
        return seeds;
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            updateDisplay();
            toFront();
        }
    }
}

