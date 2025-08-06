package com.proj.Model.Animal;

public enum ProductQuality {
    REGULAR(1.0f),
    SILVER(1.25f),
    GOLD(1.5f),
    IRIDIUM(2.0f);

    private final float priceMultiplier;

    ProductQuality(float priceMultiplier) {
        this.priceMultiplier = priceMultiplier;
    }

    public float getPriceMultiplier() {
        return priceMultiplier;
    }
}
