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

    // تصویر نوار ابزار بالای صفحه
    private Texture toolbarTexture;

    // اندازه و موقعیت کیف
    private float scale = 0.7f;
    private float offsetX = -300;
    private float offsetY = -150;

    // موقعیت نوار ابزار
    private float toolbarX = 10;
    private float toolbarY = 400;
    private float toolbarScale = 1.0f;

    // مناطق کلیک برای ابزارها در نوار ابزار (بر اساس تصویر بازی)
    private int[][] toolbarSlots = {
        {10, 400, 32, 32},   // ابزار 1 (داس)
        {46, 400, 32, 32},   // ابزار 2 (آبپاش)
        {82, 400, 32, 32},   // ابزار 3 (تبر)
        {118, 400, 32, 32},  // ابزار 4 (کلنگ)
        {154, 400, 32, 32},  // ابزار 5 (بیل)
        {190, 400, 32, 32}   // ابزار 6 (چوب ماهیگیری)
    };

    public PlayerBag(Player player, Inventory inventory) {
        this.player = player;
        this.inventory = inventory;

        // بارگذاری تصویر کیف
        try {
            bagTexture = new Texture(Gdx.files.internal("items/Inventory_Parts.png"));
            // بارگذاری تصویر نوار ابزار (همان تصویر کیف را استفاده می‌کنیم و بخش مورد نظر را برش می‌دهیم)
            toolbarTexture = new Texture(Gdx.files.internal("items/Inventory_Parts.png"));
        } catch (Exception e) {
            Gdx.app.error("PlayerBag", "Failed to load textures", e);
        }
    }

    public void update(float delta) {
        // کلید T در GameScreen کنترل می‌شود
    }

    public void render(SpriteBatch batch, OrthographicCamera camera) {
        try {
            if (!isVisible) return;

            // نمایش نوار ابزار بالای صفحه (همیشه نمایش داده می‌شود)
            renderToolbar(batch);

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

    // متد جدید برای رندر کردن نوار ابزار بالای صفحه
    private void renderToolbar(SpriteBatch batch) {
        // برش قسمت نوار ابزار از تصویر اصلی (فرض می‌کنیم در مختصات 0,0 با عرض 240 و ارتفاع 32 قرار دارد)
        TextureRegion toolbarRegion = new TextureRegion(toolbarTexture, 0, 0, 240, 32);

        // نمایش نوار ابزار در بالای صفحه
        batch.draw(toolbarRegion, toolbarX, toolbarY, 240 * toolbarScale, 32 * toolbarScale);

        // نمایش آیتم‌ها در نوار ابزار
        for (int i = 0; i < 6 && i < inventory.getCapacity(); i++) {
            InventoryItem item = inventory.getItem(i);
            if (item != null && item.getTexture() != null) {
                float itemX = toolbarX + (i * 36) * toolbarScale + 4 * toolbarScale;
                float itemY = toolbarY + 4 * toolbarScale;
                batch.draw(item.getTexture(), itemX, itemY, 24 * toolbarScale, 24 * toolbarScale);
            }
        }

        // نمایش هایلایت برای اسلات انتخاب شده
        int selectedSlot = inventory.getSelectedSlot();
        if (selectedSlot >= 0 && selectedSlot < 6) {
            // فرض می‌کنیم هایلایت در مختصات 240,0 با عرض 36 و ارتفاع 32 قرار دارد
            TextureRegion highlightRegion = new TextureRegion(toolbarTexture, 240, 0, 36, 32);
            float highlightX = toolbarX + (selectedSlot * 36) * toolbarScale;
            batch.draw(highlightRegion, highlightX, toolbarY, 36 * toolbarScale, 32 * toolbarScale);
        }
    }

    // متد جدید برای رندر کردن ابزار در دست کاراکتر
    private void renderToolInHand(SpriteBatch batch, InventoryItem item) {
        float x = player.getPosition().x;
        float y = player.getPosition().y;

        // تنظیم موقعیت ابزار بر اساس جهت کاراکتر
        switch (player.getDirection()) {
            case UP:
                x += 16; // کمی به راست
                y += 20; // بالاتر
                break;
            case DOWN:
                x -= 16; // کمی به چپ
                y -= 16; // پایین‌تر
                break;
            case LEFT:
                x -= 24; // سمت چپ
                break;
            case RIGHT:
                x += 24; // سمت راست
                break;
        }

        // نمایش ابزار با اندازه مناسب
        batch.draw(item.getTexture(), x, y, 32, 32);

        // اضافه کردن لاگ برای دیباگ
        Gdx.app.log("PlayerBag", "Drawing tool in hand at: " + x + ", " + y);
    }

    // متد جدید برای بررسی کلیک روی نوار ابزار
    private void checkToolbarClick(OrthographicCamera camera) {
        if (Gdx.input.justTouched()) {
            Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);

            // بررسی کلیک روی اسلات‌های نوار ابزار
            for (int i = 0; i < toolbarSlots.length && i < inventory.getCapacity(); i++) {
                float slotX = toolbarSlots[i][0] * toolbarScale;
                float slotY = toolbarSlots[i][1] * toolbarScale;
                float slotWidth = toolbarSlots[i][2] * toolbarScale;
                float slotHeight = toolbarSlots[i][3] * toolbarScale;

                if (touchPos.x >= slotX && touchPos.x < slotX + slotWidth &&
                    touchPos.y >= slotY && touchPos.y < slotY + slotHeight) {
                    // انتخاب اسلات
                    inventory.selectSlot(i);
                    Gdx.app.log("PlayerBag", "Selected toolbar slot: " + i);
                    break;
                }
            }
        }
    }

    public void setVisible(boolean visible) {
        this.isVisible = visible;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void toggleOpen() {
        isOpen = !isOpen;
    }

    public void dispose() {
        if (bagTexture != null) {
            bagTexture.dispose();
        }
        if (toolbarTexture != null && toolbarTexture != bagTexture) {
            toolbarTexture.dispose();
        }
    }

    public void setScale(float scale) {
        this.scale = scale;
    }
}
