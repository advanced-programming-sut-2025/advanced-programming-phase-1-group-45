package com.proj.Model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;
import com.proj.Map.farmName;

public class Character implements Disposable {
    private Texture characterTexture;
    private Texture favoriteAnimal;
    private String name;
    private String farmName;
    private farmName farmType;

    public static final String[] CHARACTER_OPTIONS = {
        "characters2", "characters3", "characters4", "stand_down-removebg-preview"
    };

    public static final String[] ANIMAL_OPTIONS = {
        "White_Chicken", "Cat_1"
    };

    public Character() {
        try {
            this.characterTexture = new Texture(Gdx.files.internal("characters/characters2.png"));
            this.favoriteAnimal = new Texture(Gdx.files.internal("animals/White_Chicken.png"));
            //this.farmType = farmName.standard;
        } catch (Exception e) {
            Gdx.app.error("CHARACTER", "Failed to load default textures", e);
            throw new RuntimeException("Initial texture loading failed", e);
        }
    }

    public farmName getFarmType() {
        return farmType;
    }

    public void setFarmType(farmName farmType) {
        this.farmType = farmType;
    }

    public Texture getFavoriteAnimal() { return favoriteAnimal; }
    public void setFavoriteAnimal(Texture favoriteAnimal) {
        if (this.favoriteAnimal != null) this.favoriteAnimal.dispose();
        this.favoriteAnimal = favoriteAnimal;
    }
    public void setCharacterTexture(Texture characterTexture) {
        if (this.characterTexture != null) this.characterTexture.dispose();
        this.characterTexture = characterTexture;
    }
    public Texture getCharacterTexture() { return characterTexture; }


    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getFarmName() { return farmName; }
    public void setFarmName(String farmName) { this.farmName = farmName; }

    @Override
    public void dispose() {
        if (characterTexture != null) characterTexture.dispose();
        if (favoriteAnimal != null) favoriteAnimal.dispose();
    }
}
