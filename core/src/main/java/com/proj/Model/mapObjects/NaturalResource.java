package com.proj.Model.mapObjects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.proj.Model.Inventory.InventoryItem;
import com.proj.Map.LandObject;

import java.awt.*;

public class NaturalResource extends InventoryItem implements LandObject {
    private String id;
    private String name;
    private TextureRegion textureRegion;
    private Point position;

    public NaturalResource(String id, String name,
                           TextureRegion texture,
                           boolean stackable, int maxStackSize) {
        super(id, name, texture, stackable, maxStackSize);
        this.id = id;
        this.name = name;
        this.textureRegion = texture;
    }

    @Override
    public String getId() {
        return id;
    }

    public TextureRegion getTextureRegion() {
        return textureRegion;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public void setTextureRegion(TextureRegion textureRegion) {
        this.textureRegion = textureRegion;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void use() {

    }

}
