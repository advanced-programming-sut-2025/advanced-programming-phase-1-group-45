package com.proj.Model.Animal;

public class Animal {
    private String name;
    private String type;
    private int friendship = 0;
    private boolean isPetToday = false;
    private boolean isFedToday = false;
    private boolean isOutside = false;
    private float x, y;
    private float targetX, targetY;
    private boolean isMoving = false;
    private String product;
    private boolean hasProduct = false;
    private String productQuality = "regular";
    private boolean free = false;
    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    private Direction direction = Direction.DOWN;
    private float stateTime = 0;
    private float lastMoveTime = 0;

    public Animal(String name, String type) {
        this.name = name;
        this.type = type;

        switch (type.toLowerCase()) {
            case "cow":
                this.product = "Cow_Milk";
                break;
            case "chicken":
                this.product = "Egg";
                break;
            case "dinosaur":
                this.product = "Dinosaur_Egg";
                break;
            case "duck":
                this.product = "Duck_Egg";
                break;
            case "goat":
                this.product = "Goat_Milk";
                break;
            case "pig":
                this.product = "Truffle";
                break;
            case "rabbit":
                this.product = "Rabbit_Wool";
                break;
            case "sheep":
                this.product = "Sheep_Wool";
                break;
            default:
                this.product = "Egg";
                break;
        }
    }

    public void pet() {
        if (!isPetToday) {
            friendship += 15;
            if (friendship > 1000) friendship = 1000;
            isPetToday = true;
        }
    }

    public void feed() {
        if (!isFedToday) {
            friendship += 5;
            if (friendship > 1000) friendship = 1000;
            isFedToday = true;
        }
    }

    public void moveTo(float targetX, float targetY) {
        this.targetX = targetX;
        this.targetY = targetY;
        isMoving = true;
    }

    public void update(float delta) {
        stateTime += delta;

        if (isMoving) {
            float dx = targetX - x;
            float dy = targetY - y;
            float distance = (float) Math.sqrt(dx * dx + dy * dy);

            if (distance < 1) {
                x = targetX;
                y = targetY;
                isMoving = false;
                return;
            }

            float speed = 50.0f * delta;
            if (speed > distance) speed = distance;

            float ratio = speed / distance;
            x += dx * ratio;
            y += dy * ratio;

            if (Math.abs(dx) > Math.abs(dy)) {
                direction = dx > 0 ? Direction.RIGHT : Direction.LEFT;
            } else {
                direction = dy > 0 ? Direction.UP : Direction.DOWN;
            }
        }
    }

    public void setOutside(boolean outside) {
        this.isOutside = outside;
        if (outside && !isFedToday) {
            friendship += 8;
            if (friendship > 1000) friendship = 1000;
            isFedToday = true;
        }
    }

    public void produceProduct() {
        if (isFedToday) {
            hasProduct = true;

            float random = (float) Math.random();
            float qualityValue = (friendship / 1000.0f) * (0.5f + 0.5f * random);

            if (qualityValue >= 0.9f) {
                productQuality = "iridium";
                if (type.equalsIgnoreCase("cow")) {
                    product = "Cow_Large_Milk";
                } else if (type.equalsIgnoreCase("goat")) {
                    product = "Goat_Large_Milk";
                } else if (type.equalsIgnoreCase("chicken")) {
                    product = "Large_Egg";
                } else if (type.equalsIgnoreCase("duck")) {
                    product = Math.random() > 0.5 ? "Duck_Egg" : "Duck_Feather";
                }
            } else if (qualityValue >= 0.7f) {
                productQuality = "gold";
            } else if (qualityValue >= 0.5f) {
                productQuality = "silver";
            } else {
                productQuality = "regular";
                if (type.equalsIgnoreCase("cow")) {
                    product = "Cow_Milk";
                } else if (type.equalsIgnoreCase("goat")) {
                    product = "Goat_Milk";
                } else if (type.equalsIgnoreCase("chicken")) {
                    product = "Egg";
                }
            }
        }
    }

    public String collectProduct() {
        if (hasProduct) {
            hasProduct = false;
            String collectedProduct = productQuality + " " + product;
            return collectedProduct;
        } else {
            return null;
        }
    }

    public int getSellPrice() {
        int basePrice = getBasePrice();
        float priceMultiplier = (friendship / 1000.0f) + 0.3f;
        return (int)(basePrice * priceMultiplier);
    }

    private int getBasePrice() {
        switch (type.toLowerCase()) {
            case "cow": return 1500;
            case "chicken": return 800;
            case "sheep": return 1200;
            case "goat": return 1300;
            case "pig": return 1600;
            case "rabbit": return 1000;
            case "duck": return 1100;
            case "dinosaur": return 5000;
            default: return 1000;
        }
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int getFriendship() {
        return friendship;
    }

    public void setFriendship(int friendship) {
        this.friendship = Math.max(0, Math.min(1000, friendship));
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public boolean isOutside() {
        return isOutside;
    }

    public boolean hasProduct() {
        return hasProduct;
    }

    public boolean isPetToday() {
        return isPetToday;
    }

    public boolean isFedToday() {
        return isFedToday;
    }

    public String getProduct() {
        return product;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public boolean isMoving() {
        return isMoving;
    }

    public void setMoving(boolean moving) {
        isMoving = moving;
    }

    public float getStateTime() {
        return stateTime;
    }

    public void setStateTime(float stateTime) {
        this.stateTime = stateTime;
    }

    public float getLastMoveTime() {
        return lastMoveTime;
    }

    public void setLastMoveTime(float lastMoveTime) {
        this.lastMoveTime = lastMoveTime;
    }

    public void resetDailyStatus() {
        isPetToday = false;
        isFedToday = false;

        if (!isFedToday) {
            friendship -= 20;
            if (friendship < 0) friendship = 0;
        }

        if (isOutside) {
            friendship -= 20;
            if (friendship < 0) friendship = 0;
        }

        if (!isPetToday) {
            friendship -= 10;
            if (friendship < 0) friendship = 0;
        }

        produceProduct();
    }


    public boolean isFree() {
        return free;
    }

    public void setFree(boolean free) {
        this.free = free;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setX(float x) {
        this.x = x;
    }
}
