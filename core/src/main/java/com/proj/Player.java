package com.proj;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.proj.map.GameMap;

public class Player {
    private Vector2 position;
    private Vector2 targetPosition;
    private float speed = 100f;
    private GameMap gameMap;
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

    private final float WIDTH = 32;
    private final float HEIGHT = 32;

    public Player(GameMap gameMap, float startX, float startY) {
        this.gameMap = gameMap;
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

            Gdx.app.log("Player", "All animation frames loaded successfully");
        } catch (Exception e) {
            Gdx.app.error("Player", "Error loading player animations: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void update(float delta) {
        isMoving = !position.epsilonEquals(targetPosition, 0.5f);

        if (isMoving) {
            frameTime += delta;

            if (frameTime >= FRAME_DURATION) {
                frameTime -= FRAME_DURATION;
                frameIndex = (frameIndex + 1) % 3;
                Gdx.app.log("Player", "Animation frame changed to: " + frameIndex);
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
    }

    public void render(SpriteBatch batch) {
        TextureRegion currentTexture = getCurrentFrame();

        batch.draw(
            currentTexture,
            position.x - WIDTH/2,
            position.y - HEIGHT/2,
            WIDTH,
            HEIGHT
        );
    }

    private TextureRegion getCurrentFrame() {
        switch (currentDirection) {
            case UP: return upFrames[frameIndex];
            case DOWN: return downFrames[frameIndex];
            case LEFT: return leftFrames[frameIndex];
            case RIGHT: return rightFrames[frameIndex];
            default: return downFrames[frameIndex];
        }
    }

    public boolean isMoving() {
        return isMoving;
    }

    public void setDirection(PlayerDirection direction) {
        this.currentDirection = direction;
    }

    public void setTargetPosition(float worldX, float worldY) {
        int tileX = (int) (worldX / 16);
        int tileY = (int) (worldY / 16);

        if (gameMap.isPassable(worldX, worldY)) {
            targetPosition.set(tileX * 16 + 8, tileY * 16 + 8);
            isMoving = true;
            frameIndex = 1;
            frameTime = 0;
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
    }

    private void disposeTextureArray(TextureRegion[] frames) {
        if (frames != null) {
            for (TextureRegion frame : frames) {
                if (frame != null && frame.getTexture() != null) {
                    frame.getTexture().dispose();
                }
            }
        }
    }
}
