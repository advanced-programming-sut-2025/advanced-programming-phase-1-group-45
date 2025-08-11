package com.proj.Model.inventoryItems.fertilizer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.proj.Control.WorldController;
import com.proj.Model.Inventory.InventoryItem;
import com.proj.Model.Inventory.InventoryManager;
import com.proj.Model.inventoryItems.seeds.ItemRegistry;

public class FertilizeWindow extends Window {
    private final Table contentTable;
    private final Skin skin;
    private final TextButton closeButton;
    private static final int COLUMNS = 4;
    private FertilizerSelectionListener selectionListener;
    private InventoryItem selectedFertilizer;
    private TextButton applyButton;

    public FertilizeWindow(Skin skin, FertilizerSelectionListener listener) {
        super("Fertilizers", skin);
        this.skin = skin;
        this.selectionListener = listener;

        setModal(false);
        setMovable(false);
        setResizable(false);
        setSize(1000, 600);
        centerWindow();

        contentTable = new Table();
        contentTable.top().left().pad(10);

        ScrollPane scroll = new ScrollPane(contentTable, skin);
        scroll.setScrollingDisabled(true, false);
        scroll.setFadeScrollBars(false);
        scroll.setScrollBarPositions(false, true);

        closeButton = new TextButton("Close", skin);
        closeButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setVisible(false);
            }
        });

        add(scroll).grow().pad(10).row();
        add(closeButton).pad(10).right().row();

        updateDisplay();
    }

    /** Center window on the screen */
    public void centerWindow() {
        setPosition(
            (Gdx.graphics.getWidth() - getWidth()) / 2f,
            (Gdx.graphics.getHeight() - getHeight()) / 2f
        );
    }

    /** Rebuild the grid of fertilizer items */
    public void updateDisplay() {
        contentTable.clear();
        Array<InventoryItem> fertilizers = new Array<>();//= getPlayerFertilizers();
        fertilizers.add(ItemRegistry.getInstance().get("Basic_Fertilizer"));
        fertilizers.add(ItemRegistry.getInstance().get("Deluxe_Fertilizer"));
        if (fertilizers.size == 0) {
            contentTable.add(new Label("No fertilizers available", skin)).row();
            return;
        }

        float iconSize = 80f;
        float spacing = 120f;

        int rows = (int)Math.ceil(fertilizers.size / (float)COLUMNS);
        float neededHeight = 100 + rows * (iconSize + spacing);
        setHeight(Math.min(Gdx.graphics.getHeight() - 100, neededHeight));
        centerWindow();

        Table row = new Table();
        row.defaults().pad(spacing).center();

        for (int i = 0; i < fertilizers.size; i++) {
            InventoryItem fertilizer1 = fertilizers.get(i);
            row.add(makeFertilizerCell(fertilizer1, iconSize))
                .size(iconSize + 20, iconSize + 60);

            if ((i + 1) % COLUMNS == 0 || i == fertilizers.size - 1) {
                contentTable.add(row).padBottom(20).row();
                row = new Table();
                row.defaults().pad(spacing).center();
            }
        }
    }

    /** Create one cell with image, name, count and tap listener */
    private Table makeFertilizerCell(InventoryItem fertilizer, float iconSize) {
        Table cell = new Table(skin);
        cell.setBackground(skin.getDrawable("background"));
        cell.pad(5);

        TextureRegion tex = fertilizer.getTexture();
        Image img = new Image(new TextureRegionDrawable(tex));
        img.setScaling(Scaling.fit);
        cell.add(img).size(iconSize).row();

        Label name = new Label(fertilizer.getName(), skin);
        name.setFontScale(0.8f);
        name.setEllipsis(true);
        cell.add(name).padTop(5).row();

        Label qty = new Label(String.valueOf(fertilizer.getQuantity()), skin);
        cell.add(qty).padBottom(5).row();

        cell.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selectFertilizer(fertilizer);
            }
        });

        return cell;
    }

    /** Handle fertilizer selection and callback */
    private void selectFertilizer(InventoryItem fertilizer) {
        selectedFertilizer = fertilizer;
        if (selectionListener != null && selectionListener.onFertilizerSelected(fertilizer)) {
            setVisible(false);
        }
        selectedFertilizer = null;
    }

    /** Gather all fertilizer items in player inventory */
    private Array<InventoryItem> getPlayerFertilizers() {
        Array<InventoryItem> list = new Array<>();
        for (InventoryItem item : InventoryManager.getInstance()
            .getPlayerInventory()
            .getItems()
            .values()) {
            if (item instanceof BasicFertilizer || item instanceof DeluxeFertilizer) {
                if (item.getQuantity() > 0) list.add(item);
            }
        }
        list.sort((a, b) -> Integer.compare(b.getQuantity(), a.getQuantity()));
        return list;
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            updateDisplay();
            pack();        // ← مهم: ابعاد و چیدمان را بروزرسانی می‌کند
            toFront();
        }
    }


    /** Expose the last manual selection if needed */
    public InventoryItem getSelectedFertilizer() {
        return selectedFertilizer;
    }
}
