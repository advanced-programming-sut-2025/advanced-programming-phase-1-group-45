package com.proj;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.proj.Control.WorldController;
import com.proj.Model.Inventory.Inventory;
import com.proj.Model.Inventory.InventoryItem;
import com.proj.network.client.GameEventListener;
import com.proj.network.event.GameEvent;
import com.proj.network.event.NetworkEvent;

public class Player implements GameEventListener {
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

    private int money;
    private final Inventory inventory;
    private Main game;

    private TextureRegion faintFrame;
    private TextureRegion faintFinalFrame;

    private final float WIDTH = 32;
    private final float HEIGHT = 32;


    private float eatingAnimationTimer = 0f;
    private static final float EATING_ANIM_DURATION = 0.5f;
    private boolean isEating = false;



    private TextureRegion eatingTexture;
    private float eatingDisplayTimer = 0f;
    private static final float EATING_DISPLAY_DURATION = 3.0f;

    public Player(WorldController worldController, float startX, float startY) {
        this.worldController = worldController;
        this.position = new Vector2(startX, startY);
        this.targetPosition = new Vector2(startX, startY);
        this.money = 1000;
        this.inventory = new Inventory(24);
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
            if (eatingDisplayTimer > 0f) {
                eatingDisplayTimer -= delta;
                if (eatingDisplayTimer <= 0f) {
                    eatingTexture = null;
                }
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
        if (eatingTexture != null) {
            float sx = position.x - WIDTH/2;
            float sy = position.y + HEIGHT/2; // in front of player (adjust as needed)
            batch.draw(eatingTexture, sx, sy, 24, 24);
        }
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
            Main.getMain().getGameClient().sendPlayerNewPosition(worldX, worldY, WorldController.getInstance().getGameMap().getMapName());
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

    public boolean isEating() {
        return isEating;
    }

      public int getMoney() {
        return money;
    }

    public void addMoney(int amount) {
        money += amount;
    }

    public Inventory getInventory() {
        return inventory;
    }

    /*public int getInventoryCount(String itemId) {
        int count = 0;
        for (InventoryItem item : inventory.getItems()) {
            if (item.getId().equals(itemId)) {
                count += item.getQuantity();
            }
        }
        return count;
    }*/

    public void addItem(String itemId, int quantity) {

    }

    public Main getGame() {
        return game;
    }

    @Override
    public void handleGameEvent(GameEvent event) {

    }

    @Override
    public void handleNetworkEvent(NetworkEvent event) {

    }


    public void startEatingAnimation(TextureRegion foodTexture) {
        this.isEating = true;
        this.eatingAnimationTimer = 0f;
        this.eatingDisplayTimer = EATING_DISPLAY_DURATION;
        this.eatingTexture = foodTexture;
    }

}
