package com.proj;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.proj.Control.WorldController;

public class Player {
    private Vector2 position;
    private Vector2 targetPosition;
    private float speed = 100f;
    private WorldController worldController;
    private Rectangle boundingBox;
    private PlayerDirection currentDirection = PlayerDirection.DOWN;
    private boolean isMoving = false;

    private int frameIndex = 0;
    private float frameTime = 0;
    private final float FRAME_DURATION = 0.15f;

    private TextureRegion[] upFrames;
    private TextureRegion[] downFrames;
    private TextureRegion[] leftFrames;
    private TextureRegion[] rightFrames;

    private float maxEnergy = 5000f;
    private float currentEnergy = maxEnergy;
    private boolean isFainted = false;
    private boolean isFainting = false;
    private float faintAnimationTimer = 0f;
    private static final float FAINT_DURATION = 2.0f;
    private static final float ENERGY_COST_PER_TILE = 5f;

    private TextureRegion faintFrame;
    private TextureRegion faintFinalFrame;

    private final float WIDTH = 32;
    private final float HEIGHT = 32;


    private float eatingAnimationTimer = 0f;
    private static final float EATING_ANIM_DURATION = 0.5f;
    private boolean isEating = false;

    private com.proj.Model.Cooking.Buff activeBuff;
    private float buffRemainingTime;

    public Player(WorldController worldController, float startX, float startY) {
        this.worldController = worldController;
        this.position = new Vector2(startX, startY);
        this.targetPosition = new Vector2(startX, startY);
        this.boundingBox = new Rectangle(position.x - WIDTH/4, position.y - HEIGHT/4, WIDTH/2, HEIGHT/2);
        loadAnimations();
    }

    private void loadAnimations() {
        try {
            upFrames = new TextureRegion[3];
            upFrames[0] = new TextureRegion(new Texture(Gdx.files.internal("character/stand_up-removebg-preview.png")));
            upFrames[1] = new TextureRegion(new Texture(Gdx.files.internal("character/walk_up-removebg-preview.png")));
            upFrames[2] = new TextureRegion(new Texture(Gdx.files.internal("character/run_up-removebg-preview.png")));

            downFrames = new TextureRegion[3];
            downFrames[0] = new TextureRegion(new Texture(Gdx.files.internal("character/stand_down-removebg-preview.png")));
            downFrames[1] = new TextureRegion(new Texture(Gdx.files.internal("character/walk_down-removebg-preview.png")));
            downFrames[2] = new TextureRegion(new Texture(Gdx.files.internal("character/run_down-removebg-preview.png")));

            leftFrames = new TextureRegion[3];
            leftFrames[0] = new TextureRegion(new Texture(Gdx.files.internal("character/stand_left-removebg-preview.png")));
            leftFrames[1] = new TextureRegion(new Texture(Gdx.files.internal("character/walk_left-removebg-preview.png")));
            leftFrames[2] = new TextureRegion(new Texture(Gdx.files.internal("character/run_left-removebg-preview.png")));

            rightFrames = new TextureRegion[3];
            rightFrames[0] = new TextureRegion(new Texture(Gdx.files.internal("character/stand_right-removebg-preview.png")));
            rightFrames[1] = new TextureRegion(new Texture(Gdx.files.internal("character/walk_right-removebg-preview.png")));
            rightFrames[2] = new TextureRegion(new Texture(Gdx.files.internal("character/run_right-removebg-preview.png")));

            faintFrame = new TextureRegion(new Texture(Gdx.files.internal("characters/stand_down-removebg-preview.png")));
            faintFinalFrame = new TextureRegion(new Texture(Gdx.files.internal("characters/passed_out.png")));

            Gdx.app.log("Player", "All animation frames loaded successfully");
        } catch (Exception e) {
            Gdx.app.error("Player", "Error loading player animations: " + e.getMessage());
            e.printStackTrace();

            Pixmap pixmap = new Pixmap(16, 16, Pixmap.Format.RGBA8888);
            pixmap.setColor(1, 0, 0, 1);
            pixmap.fill();

            Texture fallback = new Texture(pixmap);
            pixmap.dispose();

            faintFrame = new TextureRegion(fallback);
            faintFinalFrame = new TextureRegion(fallback);

            downFrames = new TextureRegion[3];
            for (int i = 0; i < 3; i++) {
                downFrames[i] = faintFrame;
            }
            upFrames = downFrames;
            leftFrames = downFrames;
            rightFrames = downFrames;
        }
    }

    public void update(float delta) {
        if (isFainting) {
            faintAnimationTimer += delta;
            if (faintAnimationTimer >= FAINT_DURATION) {
                isFainting = false;
                isFainted = true;
            }
            return;
        }

        if (isFainted) return;

        isMoving = !position.epsilonEquals(targetPosition, 0.5f);

        if (isMoving) {
            frameTime += delta;

            if (frameTime >= FRAME_DURATION) {
                frameTime -= FRAME_DURATION;
                frameIndex = (frameIndex + 1) % 3;
            }

            Vector2 direction = new Vector2(targetPosition).sub(position).nor();
            position.add(direction.scl(speed * delta));
            boundingBox.setPosition(position.x - WIDTH/4, position.y - HEIGHT/4);

            if (position.dst2(targetPosition) < 1f) {
                position.set(targetPosition);
                isMoving = false;
                frameIndex = 0;
            }
        } else {
            frameIndex = 0;
        }

        if (isEating) {
            eatingAnimationTimer += delta;
            if (eatingAnimationTimer >= EATING_ANIM_DURATION) {
                isEating = false;
                eatingAnimationTimer = 0;
            }
        }

        if (activeBuff != null) {
            buffRemainingTime -= delta;
            if (buffRemainingTime <= 0) {
                activeBuff = null;
                Gdx.app.log("Player", "Buff expired.");
            }
        }
    }

    public void render(SpriteBatch batch) {
        if (isFainted) {
            batch.draw(faintFinalFrame, position.x - WIDTH/2, position.y - HEIGHT/2, WIDTH, HEIGHT);
            return;
        }
        if (isFainting) {
            batch.draw(faintFrame, position.x - WIDTH/2, position.y - HEIGHT/2, WIDTH, HEIGHT);
            return;
        }
        TextureRegion currentTexture = getCurrentFrame();
        batch.draw(currentTexture, position.x - WIDTH/2, position.y - HEIGHT/2, WIDTH, HEIGHT);
    }

    private TextureRegion getCurrentFrame() {
        switch (currentDirection) {
            case UP:
                if (upFrames != null && upFrames.length > frameIndex)
                    return upFrames[frameIndex];
            case DOWN:
                if (downFrames != null && downFrames.length > frameIndex)
                    return downFrames[frameIndex];
            case LEFT:
                if (leftFrames != null && leftFrames.length > frameIndex)
                    return leftFrames[frameIndex];
            case RIGHT:
                if (rightFrames != null && rightFrames.length > frameIndex)
                    return rightFrames[frameIndex];
            default:
                if (downFrames != null && downFrames.length > frameIndex)
                    return downFrames[frameIndex];
        }
        return null;
    }

    public boolean isMoving() {
        return isMoving;
    }

    public void setDirection(PlayerDirection direction) {
        this.currentDirection = direction;
    }

    public void setTargetPosition(float worldX, float worldY) {
        if (isFainted || isFainting) return;

        int tileX = (int) (worldX / 16);
        int tileY = (int) (worldY / 16);

        if (worldController.isPassable(worldX, worldY) && currentEnergy >= ENERGY_COST_PER_TILE) {
            currentEnergy -= ENERGY_COST_PER_TILE;
            targetPosition.set(tileX * 16 + 8, tileY * 16 + 8);
            isMoving = true;
            frameIndex = 1;
            frameTime = 0;
        } else if (currentEnergy < ENERGY_COST_PER_TILE) {
            startFainting();
        }
    }

    public Vector2 getPosition() {
        return position;
    }

    public Rectangle getBoundingBox() {
        return boundingBox;
    }

    public boolean collidesWith(Rectangle otherBounds) {
        return boundingBox.overlaps(otherBounds);
    }

    public void dispose() {
        disposeTextureArray(upFrames);
        disposeTextureArray(downFrames);
        disposeTextureArray(leftFrames);
        disposeTextureArray(rightFrames);

        disposeTextureRegion(faintFrame);
        disposeTextureRegion(faintFinalFrame);
    }

    private void disposeTextureArray(TextureRegion[] frames) {
        if (frames != null) {
            for (TextureRegion frame : frames) {
                disposeTextureRegion(frame);
            }
        }
    }

    private void disposeTextureRegion(TextureRegion region) {
        if (region != null && region.getTexture() != null) {
            region.getTexture().dispose();
        }
    }

    private void startFainting() {
        isFainting = true;
        faintAnimationTimer = 0f;
        isMoving = false;
        targetPosition.set(position.x, position.y);
    }

    public void restoreEnergy(float amount) {
        currentEnergy = Math.min(currentEnergy + amount, maxEnergy);
    }

    public void resetEnergy() {
        currentEnergy = maxEnergy;
        isFainted = false;
    }

    public boolean isFainted() {
        return isFainted;
    }

    public boolean isFainting() {
        return isFainting;
    }

    public void useEnergy(float amount) {
        currentEnergy = Math.max(0, currentEnergy - amount);
        if (currentEnergy <= 0 && !isFainted && !isFainting) {
            startFainting();
        }
    }

    public PlayerDirection getDirection() {
        return currentDirection;
    }

    public void setPosition(float worldX, float worldY) {
        position.set(worldX, worldY);
    }

    public float getCurrentEnergy() {
        return currentEnergy;
    }

    public float getMaxEnergy() {
        return maxEnergy;
    }



    public void startEatingAnimation() {
        isEating = true;
        eatingAnimationTimer = 0;
    }

    public void applyBuff(com.proj.Model.Cooking.Buff buff) {
        this.activeBuff = buff;
        this.buffRemainingTime = buff.durationHours * 3600;
        Gdx.app.log("Player", "Buff applied: " + buff.effect + " for " + buff.durationHours + " hours.");

        if (buff.effect.contains("Max Energy")) {
            try {
                // فرض می‌کنیم فرمت "Max Energy +X" است
                int energyIncrease = Integer.parseInt(buff.effect.split("\\+")[1].trim());
                this.maxEnergy += energyIncrease;
                this.currentEnergy = Math.min(this.currentEnergy + energyIncrease, this.maxEnergy); // انرژی فعلی هم افزایش یابد
                Gdx.app.log("Player", "Max Energy increased by: " + energyIncrease);
            } catch (NumberFormatException e) {
                Gdx.app.error("Player", "Failed to parse Max Energy buff value: " + buff.effect);
            }
        }
        // می‌توانید باف‌های دیگر را نیز اینجا مدیریت کنید (مثلاً Farming, Foraging, Fishing, Mining)
    }

    public com.proj.Model.Cooking.Buff getActiveBuff() {
        return activeBuff;
    }

    public float getBuffRemainingTime() {
        return buffRemainingTime;
    }

    public boolean isEating() {
        return isEating;
    }

}
