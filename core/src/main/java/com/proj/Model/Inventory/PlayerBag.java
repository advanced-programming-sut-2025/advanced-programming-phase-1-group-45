package com.proj.Model.Inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.proj.Player;

public class PlayerBag {
    private Inventory inventory;
    private Player player;
    private boolean isOpen = false;
    private boolean isVisible = true;

    // تصویر کیف
    private Texture bagTexture;

    // اندازه و موقعیت کیف
    private float scale = 0.7f;
    private float offsetX = -150;
    private float offsetY = -150; // تنظیم شده به -150

    // موقعیت نوار ابزار (ثابت نسبت به صفحه)
    private float toolbarX;
    private float toolbarY;

    // مناطق کلیک برای ابزارها در نوار ابزار
    private int[][] toolbarSlots = {
        {76, 336, 36, 36},  // ابزار 1: {x, y, width, height}
        {112, 336, 36, 36}, // ابزار 2
        {148, 336, 36, 36}, // ابزار 3
        {184, 336, 36, 36}, // ابزار 4
        {220, 336, 36, 36}, // ابزار 5
        {256, 336, 36, 36}  // ابزار 6
    };

    // منطقه کلیک برای دکمه "بدون ابزار"
    private int[] noToolButton = {292, 336, 36, 36}; // دکمه بدون ابزار کنار ابزارهای دیگر

    public PlayerBag(Player player, Inventory inventory) {
        this.player = player;
        this.inventory = inventory;

        // تنظیم موقعیت نوار ابزار
        toolbarX = 10;
        toolbarY = Gdx.graphics.getHeight() - 42;

        // بارگذاری تصویر کیف
        try {
            bagTexture = new Texture(Gdx.files.internal("items/Inventory_Parts.png"));

            if (bagTexture == null) {
                Gdx.app.error("PlayerBag", "Failed to load bag texture");
            } else {
                Gdx.app.log("PlayerBag", "Textures loaded successfully");
            }
        } catch (Exception e) {
            Gdx.app.error("PlayerBag", "Failed to load textures", e);
            e.printStackTrace();
        }
    }

    public void update(float delta) {
        // کلید T در GameScreen کنترل می‌شود
    }

    public void render(SpriteBatch batch, OrthographicCamera camera) {
        try {
            if (!isVisible) return;

            // اگر کیف بسته است، فقط آیتم انتخاب شده را نمایش بده
            if (!isOpen) {
                InventoryItem selectedItem = inventory.getSelectedItem();
                if (selectedItem != null && selectedItem.getTexture() != null) {
                    // نمایش آیتم انتخاب شده در دست بازیکن
                    renderToolInHand(batch, selectedItem);
                }
                return;
            }

            // بررسی وجود تصویر کیف
            if (bagTexture == null) {
                Gdx.app.error("PlayerBag", "Bag texture is null");
                return;
            }

            // محاسبه اندازه کیف با مقیاس
            float bagWidth = bagTexture.getWidth() * scale;
            float bagHeight = bagTexture.getHeight() * scale;

            // محاسبه موقعیت کیف نسبت به بازیکن
            float bagX = player.getPosition().x + offsetX;
            float bagY = player.getPosition().y + offsetY;

            // نمایش کل تصویر کیف
            batch.draw(
                bagTexture,
                bagX,
                bagY,
                bagWidth,
                bagHeight
            );

            // بررسی کلیک موس برای انتخاب آیتم
            checkToolbarClick(camera);
        } catch (Exception e) {
            Gdx.app.error("PlayerBag", "Error in render", e);
            e.printStackTrace();
        }
    }

    // متد برای رندر کردن ابزار در دست کاراکتر
    private void renderToolInHand(SpriteBatch batch, InventoryItem item) {
        try {
            if (item == null || item.getTexture() == null) return;

            float x = player.getPosition().x;
            float y = player.getPosition().y;

            // تنظیم موقعیت ابزار بر اساس جهت کاراکتر
            switch (player.getDirection()) {
                case UP:
                    x += 16;
                    y += 20;
                    break;
                case DOWN:
                    x -= 16;
                    y -= 16;
                    break;
                case LEFT:
                    x -= 24;
                    break;
                case RIGHT:
                    x += 24;
                    break;
            }

            // نمایش ابزار با اندازه مناسب
            batch.draw(item.getTexture(), x, y, 32, 32);

            // اضافه کردن لاگ برای دیباگ
            Gdx.app.log("PlayerBag", "Drawing tool in hand at: " + x + ", " + y);
        } catch (Exception e) {
            Gdx.app.error("PlayerBag", "Error in renderToolInHand", e);
            e.printStackTrace();
        }
    }

    // متد برای بررسی کلیک موس روی کیف
    private void checkToolbarClick(OrthographicCamera camera) {
        if (Gdx.input.justTouched()) {
            try {
                Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
                camera.unproject(touchPos);

                Gdx.app.log("PlayerBag", "Mouse clicked at: " + touchPos.x + ", " + touchPos.y);

                // محاسبه موقعیت کیف
                float bagX = player.getPosition().x + offsetX;
                float bagY = player.getPosition().y + offsetY;

                // بررسی کلیک روی اسلات‌های ابزار در کیف
                for (int i = 0; i < toolbarSlots.length && i < inventory.getCapacity(); i++) {
                    float toolX = bagX + toolbarSlots[i][0] * scale;
                    float toolY = bagY + toolbarSlots[i][1] * scale;
                    float toolWidth = toolbarSlots[i][2] * scale;
                    float toolHeight = toolbarSlots[i][3] * scale;

                    Gdx.app.log("PlayerBag", "Tool slot " + i + " rect: " + toolX + ", " + toolY + ", " + toolWidth + ", " + toolHeight);

                    if (touchPos.x >= toolX && touchPos.x < toolX + toolWidth &&
                        touchPos.y >= toolY && touchPos.y < toolY + toolHeight) {
                        // انتخاب اسلات
                        inventory.selectSlot(i);
                        Gdx.app.log("PlayerBag", "Selected tool slot: " + i);
                        return;
                    }
                }

                // بررسی کلیک روی دکمه "بدون ابزار"
                float noToolX = bagX + noToolButton[0] * scale;
                float noToolY = bagY + noToolButton[1] * scale;
                float noToolWidth = noToolButton[2] * scale;
                float noToolHeight = noToolButton[3] * scale;

                if (touchPos.x >= noToolX && touchPos.x < noToolX + noToolWidth &&
                    touchPos.y >= noToolY && touchPos.y < noToolY + noToolHeight) {
                    // انتخاب حالت بدون ابزار
                    selectNoTool();
                    Gdx.app.log("PlayerBag", "Selected no tool");
                    return;
                }
            } catch (Exception e) {
                Gdx.app.error("PlayerBag", "Error in checkToolbarClick", e);
                e.printStackTrace();
            }
        }
    }

    // متد جدید برای انتخاب حالت "بدون ابزار"
    public void selectNoTool() {
        // در اینجا باید کلاس Inventory را اصلاح کنیم تا این متد را پشتیبانی کند
        // اما فعلاً می‌توانیم با انتخاب یک اسلات خالی این کار را انجام دهیم
        for (int i = 0; i < inventory.getCapacity(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.selectSlot(i);
                Gdx.app.log("PlayerBag", "Selected empty slot as 'no tool'");
                return;
            }
        }

        // اگر هیچ اسلات خالی پیدا نشد، اسلات اول را انتخاب کنید
        inventory.selectSlot(0);
        Gdx.app.log("PlayerBag", "No empty slot found, selected slot 0");
    }

    public void setVisible(boolean visible) {
        this.isVisible = visible;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void toggleOpen() {
        isOpen = !isOpen;
        Gdx.app.log("PlayerBag", "Toggled inventory: " + (isOpen ? "open" : "closed"));
    }

    public void dispose() {
        try {
            if (bagTexture != null) {
                bagTexture.dispose();
            }
        } catch (Exception e) {
            Gdx.app.error("PlayerBag", "Error in dispose", e);
        }
    }

    public void setScale(float scale) {
        this.scale = scale;
    }
}
