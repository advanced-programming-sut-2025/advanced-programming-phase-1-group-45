package com.proj.network;

import org.json.JSONObject;

/**
 * کلاس نگهداری اطلاعات بازیکن در سمت سرور
 */
public class PlayerData {
    private String username;
    private float positionX;
    private float positionY;
    private PlayerDirection direction;
    private boolean isMoving;
    private float energy;
    private float maxEnergy = 5000f;
    private boolean isFainted;

    // اطلاعات مربوط به مزرعه بازیکن
    private String farmName;
    private int money;
    private int level;

    public PlayerData(String username) {
        this.username = username;
        this.positionX = 0;
        this.positionY = 0;
        this.direction = PlayerDirection.DOWN;
        this.isMoving = false;
        this.energy = maxEnergy;
        this.isFainted = false;
        this.money = 500; // مقدار اولیه پول
        this.level = 1;
    }

    /**
     * تبدیل اطلاعات بازیکن به JSON
     */
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("username", username);
        json.put("positionX", positionX);
        json.put("positionY", positionY);
        json.put("direction", direction.toString());
        json.put("isMoving", isMoving);
        json.put("energy", energy);
        json.put("maxEnergy", maxEnergy);
        json.put("isFainted", isFainted);
        json.put("farmName", farmName);
        json.put("money", money);
        json.put("level", level);
        return json;
    }

    /**
     * به‌روزرسانی موقعیت بازیکن
     */
    public void updatePosition(float x, float y, PlayerDirection direction) {
        this.positionX = x;
        this.positionY = y;
        this.direction = direction;
    }

    /**
     * کاهش انرژی بازیکن
     */
    public void useEnergy(float amount) {
        this.energy = Math.max(0, this.energy - amount);
        if (this.energy <= 0 && !isFainted) {
            this.isFainted = true;
        }
    }

    /**
     * بازیابی انرژی بازیکن
     */
    public void restoreEnergy(float amount) {
        this.energy = Math.min(maxEnergy, this.energy + amount);
        if (this.energy > 0 && isFainted) {
            this.isFainted = false;
        }
    }

    /**
     * افزایش پول بازیکن
     */
    public void addMoney(int amount) {
        this.money += amount;
    }

    /**
     * کاهش پول بازیکن
     */
    public boolean spendMoney(int amount) {
        if (this.money >= amount) {
            this.money -= amount;
            return true;
        }
        return false;
    }

    /**
     * افزایش سطح بازیکن
     */
    public void levelUp() {
        this.level++;
        // افزایش حداکثر انرژی با افزایش سطح
        this.maxEnergy += 100;
        this.energy = this.maxEnergy;
    }

    // Getters and setters
    public String getUsername() { return username; }

    public float getPositionX() { return positionX; }
    public void setPositionX(float positionX) { this.positionX = positionX; }

    public float getPositionY() { return positionY; }
    public void setPositionY(float positionY) { this.positionY = positionY; }

    public PlayerDirection getDirection() { return direction; }
    public void setDirection(PlayerDirection direction) { this.direction = direction; }

    public boolean isMoving() { return isMoving; }
    public void setMoving(boolean moving) { isMoving = moving; }

    public float getEnergy() { return energy; }
    public void setEnergy(float energy) { this.energy = energy; }

    public float getMaxEnergy() { return maxEnergy; }

    public boolean isFainted() { return isFainted; }
    public void setFainted(boolean fainted) { isFainted = fainted; }

    public String getFarmName() { return farmName; }
    public void setFarmName(String farmName) { this.farmName = farmName; }

    public int getMoney() { return money; }
    public void setMoney(int money) { this.money = money; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
}
