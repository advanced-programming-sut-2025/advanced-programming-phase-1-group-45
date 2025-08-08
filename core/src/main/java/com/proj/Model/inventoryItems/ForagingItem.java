package com.proj.Model.inventoryItems;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.proj.Model.Inventory.InventoryItem;
import com.proj.Map.LandObject;
import com.proj.Map.Season;

import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ForagingItem extends InventoryItem implements LandObject {
    private String name;
    private Set<Season> season;
    private int baseSellPrice;
    private int energy;
    private TextureRegion texture;
    private Point position; // موقعیت در نقشه (به صورت tile coordinates)

    public ForagingItem(String name, Season[] seasons, int baseSellPrice, int energy, TextureRegion texture) {
        super("foragingCrop", name, texture, true, 5);
        this.name = name;
        this.season = new HashSet<>(Arrays.asList(seasons));
        this.baseSellPrice = baseSellPrice;
        this.energy = energy;
        this.texture = texture;
    }

    // Getters
    public String getName() {
        return name;
    }

    public Set<Season> getSeason() {
        return season;
    }

    public int getBaseSellPrice() {
        return baseSellPrice;
    }

    public int getEnergy() {
        return energy;
    }

    public TextureRegion getTexture() {
        return texture;
    }

    @Override
    public void use() {

    }

    public Point getPosition() {
        return position;
    }

    // Setters
    public void setPosition(Point position) {
        this.position = position;
    }

    public void setTexture(TextureRegion texture) {
        this.texture = texture;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public void setBaseSellPrice(int baseSellPrice) {
        this.baseSellPrice = baseSellPrice;
    }

    public void setSeason(Set<Season> season) {
        this.season = season;
    }

    public void setName(String name) {
        this.name = name;
    }


}
