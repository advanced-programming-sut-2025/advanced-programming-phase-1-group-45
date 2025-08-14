package com.proj.Control;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;
import com.proj.map.GameMap;
import com.proj.Model.Animal.Animal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnimalBuildingController {
    // Building textures
    private Texture barnTexture;
    private Texture coopTexture;
    private Texture barnInteriorTexture;
    private Texture coopInteriorTexture;
    private Texture listBackgroundTexture;

    // Building placement state
    private boolean isPlacingBarn = false;
    private boolean isPlacingCoop = false;
    private float barnX = 0;
    private float barnY = 0;
    private float coopX = 0;
    private float coopY = 0;
    private final float MOVEMENT_SPEED = 5.0f;

    // Placed buildings
    private float[] placedBarnsX = new float[100];
    private float[] placedBarnsY = new float[100];
    private int barnCount = 0;
    private float[] placedCoopsX = new float[100];
    private float[] placedCoopsY = new float[100];
    private int coopCount = 0;
    private int selectedListIndex = -1; // شاخص حیوان انتخاب شده در لیست
    private boolean highlightSelection = false; // برای نمایش انتخاب فعلی
    private boolean waitingForBuildingSelection = false;
    // Animals in buildings
    private List<Animal>[] animalsInBarns = new List[100];
    private List<Animal>[] animalsInCoops = new List[100];

    // Interior view state
    private boolean showingInterior = false;
    private boolean showingBarnInterior = false;
    private boolean showingCoopInterior = false;
    private int selectedBuildingIndex = -1;
    private String selectedAnimalType = null;

    private Animal selectedAnimal = null;
    private boolean showingAnimalMenu = false;

    // Free animals
    private List<Animal> freeAnimals = new ArrayList<>();
    private static final float DIRECTION_CHANGE_INTERVAL = 2.0f;
    private static final float MAX_DISTANCE = 5 * 16; // Assuming tileSize is 16

    // Animal animations
    private Map<String, Animation<TextureRegion>> walkUpAnimations = new HashMap<>();
    private Map<String, Animation<TextureRegion>> walkDownAnimations = new HashMap<>();
    private Map<String, Animation<TextureRegion>> walkLeftAnimations = new HashMap<>();
    private Map<String, Animation<TextureRegion>> walkRightAnimations = new HashMap<>();
    private Map<String, Animation<TextureRegion>> petAnimations = new HashMap<>();
    private Map<String, Animation<TextureRegion>> feedAnimations = new HashMap<>();
    private Map<String, TextureRegion> idleFrames = new HashMap<>();
    private static final float FRAME_DURATION = 0.15f;

    // Rendering
    private ShapeRenderer shapeRenderer;
    private float interiorX;
    private float interiorY;
    private float interiorScale = 0f;

    // UI components
    private BitmapFont font;
    private BitmapFont animalNameFont;
    private GlyphLayout layout;
    private Texture whitePixelTexture;

    // Animal list
    private boolean showingAnimalList = false;
    private List<AnimalDisplayData> animalsToDisplay = new ArrayList<>();
    private float listX;
    private float listY;
    private float listWidth;
    private float listHeight;
    private static final int SLOTS_PER_ROW = 4;
    private static final int TOTAL_SLOTS = 8;
    private Map<String, Texture> animalListTextures = new HashMap<>();
    private float listScale = 0.2f; // Scale for smaller list
    private float cameraX = 0;
    private float cameraY = 0;
    public boolean selectingBuildingForAnimal = false;
    // Textures
    private Map<String, Texture> animalTextures = new HashMap<>();
    private Map<String, Texture> productTextures = new HashMap<>();
    private Texture hayHopperTexture;
    private Texture hayHopperFullTexture;

    // Animation state
    private Animal feedingAnimal = null;
    private float feedingTime = 0;
    private static final float FEEDING_DURATION = 2.0f;
    private Animal pettingAnimal = null;
    private float pettingTime = 0;
    private static final float PETTING_DURATION = 2.0f;

    public static class AnimalDisplayData {
        public String name;

        public AnimalDisplayData(String name) {
            this.name = name;
        }
    }


    public void updateCameraPosition(float x, float y) {
        this.cameraX = x;
        this.cameraY = y;
        // اگر لیست نمایش داده می‌شود، موقعیت آن را نیز به‌روز کنید
        if (showingAnimalList) {
            updateListPosition();
        }
    }

    public AnimalBuildingController(GameMap gameMap) {
        try {
            // Load textures
            barnTexture = new Texture(Gdx.files.internal("assets/Animals/Barn/Barn.png"));
            coopTexture = new Texture(Gdx.files.internal("assets/Animals/Coop/Coop.png"));
            barnInteriorTexture = new Texture(Gdx.files.internal("assets/Animals/Barn/barninside.png"));
            coopInteriorTexture = new Texture(Gdx.files.internal("assets/Animals/Coop/coopinside.png"));
            hayHopperTexture = new Texture(Gdx.files.internal("assets/Animals/Hay_Hopper.png"));
            hayHopperFullTexture = new Texture(Gdx.files.internal("assets/Animals/Hay_Hopper_Full.png"));
            listBackgroundTexture = new Texture(Gdx.files.internal("assets/Animals/list.png"));

            // Initialize animal lists
            for (int i = 0; i < 100; i++) {
                animalsInBarns[i] = new ArrayList<>();
                animalsInCoops[i] = new ArrayList<>();
            }

            // Initialize rendering tools
            shapeRenderer = new ShapeRenderer();
            font = new BitmapFont();
            animalNameFont = new BitmapFont();
            animalNameFont.setColor(Color.BLACK);
            layout = new GlyphLayout();
            createWhitePixelTexture();

            // Initialize animal display list
            updateListPosition();
            loadAnimalsToDisplay();

            // Load animal textures and animations
            loadAnimalTextures();
            loadAnimalListTextures();
            loadAnimalAnimations();

        } catch (Exception e) {
            Gdx.app.error("AnimalBuildingController", "Error loading textures", e);
        }
    }

    private void loadAnimalListTextures() {
        try {
            animalListTextures.put("chicken", new Texture(Gdx.files.internal("assets/TAKI/Chicken.png")));
            animalListTextures.put("cow", new Texture(Gdx.files.internal("assets/TAKI/Cow.png")));
            animalListTextures.put("sheep", new Texture(Gdx.files.internal("assets/TAKI/Sheep.png")));
            animalListTextures.put("goat", new Texture(Gdx.files.internal("assets/TAKI/Goat.png")));
            animalListTextures.put("pig", new Texture(Gdx.files.internal("assets/TAKI/Pig.png")));
            animalListTextures.put("duck", new Texture(Gdx.files.internal("assets/TAKI/Duck.png")));
            animalListTextures.put("rabbit", new Texture(Gdx.files.internal("assets/TAKI/Rabbit.png")));
            animalListTextures.put("dinosaur", new Texture(Gdx.files.internal("assets/TAKI/Dinosaur.png")));
        } catch (Exception e) {
            Gdx.app.error("AnimalBuildingController", "Error loading animal list textures", e);
        }
    }

    private void updateListPosition() {
        // Scale the list to make it smaller
        listWidth = listBackgroundTexture.getWidth() * listScale;
        listHeight = listBackgroundTexture.getHeight() * listScale;

        // Position the list relative to camera position
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        // نمایش لیست در گوشه بالا سمت راست دوربین
        listX = cameraX-100 ; // تنظیم فاصله از مرکز دوربین
        listY = cameraY-200 ; // تنظیم فاصله از مرکز دوربین
    }


    private void loadAnimalsToDisplay() {
        animalsToDisplay = new ArrayList<>();
        String[] animalNames = {
            "Chicken", "Duck", "Rabbit", "Dinosaur",
            "Cow", "Goat", "Sheep", "Pig"
        };

        for (String name : animalNames) {
            animalsToDisplay.add(new AnimalDisplayData(name));
        }
    }

    private void createWhitePixelTexture() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(1, 1, 1, 1);
        pixmap.fill();
        whitePixelTexture = new Texture(pixmap);
        pixmap.dispose();
    }

    private void loadAnimalTextures() {
        animalTextures.put("cow", new Texture(Gdx.files.internal("assets/Animals/Sprites/Cow.png")));
        animalTextures.put("chicken", new Texture(Gdx.files.internal("assets/Animals/Sprites/Chicken.png")));
        animalTextures.put("sheep", new Texture(Gdx.files.internal("assets/Animals/Sprites/Sheep.png")));
        animalTextures.put("goat", new Texture(Gdx.files.internal("assets/Animals/Sprites/Goat.png")));
        animalTextures.put("pig", new Texture(Gdx.files.internal("assets/Animals/Sprites/Pig.png")));
        animalTextures.put("duck", new Texture(Gdx.files.internal("assets/Animals/Sprites/Duck.png")));
        animalTextures.put("rabbit", new Texture(Gdx.files.internal("assets/Animals/Sprites/Rabbit.png")));
        animalTextures.put("dinosaur", new Texture(Gdx.files.internal("assets/Animals/Sprites/Dinosaur.png")));

        productTextures.put("Cow_Milk", new Texture(Gdx.files.internal("assets/Animals/AnimalProducts/Cow_Milk.png")));
        productTextures.put("Cow_Large_Milk", new Texture(Gdx.files.internal("assets/Animals/AnimalProducts/Cow_Large_Milk.png")));
        productTextures.put("Egg", new Texture(Gdx.files.internal("assets/Animals/AnimalProducts/Egg.png")));
        productTextures.put("Large_Egg", new Texture(Gdx.files.internal("assets/Animals/AnimalProducts/Large_Egg.png")));
        productTextures.put("Duck_Egg", new Texture(Gdx.files.internal("assets/Animals/AnimalProducts/Duck_Egg.png")));
        productTextures.put("Duck_Feather", new Texture(Gdx.files.internal("assets/Animals/AnimalProducts/Duck_Feather.png")));
        productTextures.put("Goat_Milk", new Texture(Gdx.files.internal("assets/Animals/AnimalProducts/Goat_Milk.png")));
        productTextures.put("Goat_Large_Milk", new Texture(Gdx.files.internal("assets/Animals/AnimalProducts/Goat_Large_Milk.png")));
        productTextures.put("Sheep_Wool", new Texture(Gdx.files.internal("assets/Animals/AnimalProducts/Sheep_Wool.png")));
        productTextures.put("Rabbit_Wool", new Texture(Gdx.files.internal("assets/Animals/AnimalProducts/Rabbit_Wool.png")));
        productTextures.put("Truffle", new Texture(Gdx.files.internal("assets/Animals/AnimalProducts/Truffle.png")));
        productTextures.put("Dinosaur_Egg", new Texture(Gdx.files.internal("assets/Animals/AnimalProducts/Dinosaur_Egg.png")));
    }

    private void loadAnimalAnimations() {
        String[] animalTypes = {"cow", "chicken", "sheep", "goat", "pig", "duck", "rabbit", "dinosaur"};

        for (String animalType : animalTypes) {
            Texture spriteSheet = animalTextures.get(animalType);

            if (spriteSheet != null) {
                int cols = 4; // تعداد ستون‌ها همیشه 4 است
                int rows;

                // تنظیم تعداد ردیف‌ها براساس نوع حیوان
                if (animalType.equals("rabbit") || animalType.equals("chicken") || animalType.equals("dinosaur")) {
                    rows = 7;
                } else if (animalType.equals("duck")) {
                    rows = 14;
                } else {
                    rows = 4; // برای گاو، گوسفند، بز و خوک
                }

                int frameWidth = spriteSheet.getWidth() / cols;
                int frameHeight = spriteSheet.getHeight() / rows;

                // تقسیم اسپرایت‌شیت به فریم‌ها
                TextureRegion[][] tmp = TextureRegion.split(spriteSheet, frameWidth, frameHeight);

                // ذخیره فریم ایستا
                idleFrames.put(animalType, tmp[0][0]);

                // ساخت انیمیشن‌ها براساس نوع حیوان
                switch (animalType) {
                    case "cow":
                    case "goat":
                    case "sheep":
                    case "pig":
                        // ردیف اول: حرکت به پایین
                        walkDownAnimations.put(animalType, createAnimation(tmp[0], 0, 4));
                        // ردیف دوم: حرکت به چپ/راست
                        walkLeftAnimations.put(animalType, createFlippedAnimation(tmp[1], 0, 4));
                        walkRightAnimations.put(animalType, createAnimation(tmp[1], 0, 4));
                        // ردیف سوم: حرکت به بالا
                        walkUpAnimations.put(animalType, createAnimation(tmp[2], 0, 4));
                        // ردیف چهارم: ناز کردن
                        petAnimations.put(animalType, createAnimation(tmp[3], 0, 4));
                        break;

                    case "rabbit":
                    case "dinosaur":
                        walkDownAnimations.put(animalType, createAnimation(tmp[0], 0, 4));
                        walkLeftAnimations.put(animalType, createFlippedAnimation(tmp[1], 0, 4));
                        walkRightAnimations.put(animalType, createAnimation(tmp[1], 0, 4));
                        walkUpAnimations.put(animalType, createAnimation(tmp[2], 0, 4));
                        // ردیف پنجم: ناز کردن
                        petAnimations.put(animalType, createAnimation(tmp[4], 0, 4));
                        break;

                    case "chicken":
                        walkLeftAnimations.put(animalType, createAnimation(tmp[0], 0, 4));
                        walkRightAnimations.put(animalType, createAnimation(tmp[1], 0, 4));
                        walkUpAnimations.put(animalType, createAnimation(tmp[2], 0, 4));
                        walkDownAnimations.put(animalType, createAnimation(tmp[3], 0, 4));
                        // ردیف پنجم: ناز کردن
                        petAnimations.put(animalType, createAnimation(tmp[4], 0, 4));
                        break;

                    case "duck":
                        walkLeftAnimations.put(animalType, createAnimation(tmp[0], 0, 4));
                        walkRightAnimations.put(animalType, createFlippedAnimation(tmp[0], 0, 4));
                        walkUpAnimations.put(animalType, createAnimation(tmp[2], 0, 4));
                        walkDownAnimations.put(animalType, createAnimation(tmp[3], 0, 4));
                        // ردیف دوازدهم: ناز کردن
                        petAnimations.put(animalType, createAnimation(tmp[12], 0, 4));
                        break;
                }
            }
        }
    }

    private Animation<TextureRegion> createAnimation(TextureRegion[] frames, int startIndex, int frameCount) {
        Array<TextureRegion> animationFrames = new Array<>(TextureRegion.class);
        for (int i = startIndex; i < startIndex + frameCount && i < frames.length; i++) {
            animationFrames.add(frames[i]);
        }
        return new Animation<>(FRAME_DURATION, animationFrames, Animation.PlayMode.LOOP);
    }

    private Animation<TextureRegion> createFlippedAnimation(TextureRegion[] frames, int startIndex, int frameCount) {
        Array<TextureRegion> animationFrames = new Array<>(TextureRegion.class);
        for (int i = startIndex; i < startIndex + frameCount && i < frames.length; i++) {
            TextureRegion region = new TextureRegion(frames[i]);
            region.flip(true, false); // برعکس کردن افقی
            animationFrames.add(region);
        }
        return new Animation<>(FRAME_DURATION, animationFrames, Animation.PlayMode.LOOP);
    }




    public void update(float delta) {
        // کنترل‌های جابجایی ساختمان‌ها
        if (isPlacingBarn) {
            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                barnY += MOVEMENT_SPEED;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                barnY -= MOVEMENT_SPEED;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                barnX -= MOVEMENT_SPEED;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                barnX += MOVEMENT_SPEED;
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                placeBarn();
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                isPlacingBarn = false;
            }
        }

        if (isPlacingCoop) {
            // کنترل‌های مشابه برای قفس
            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                coopY += MOVEMENT_SPEED;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                coopY -= MOVEMENT_SPEED;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                coopX -= MOVEMENT_SPEED;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                coopX += MOVEMENT_SPEED;
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                placeCoop();
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                isPlacingCoop = false;
            }
        }

        // کنترل لیست حیوانات - این قسمت را اصلاح می‌کنیم
        if (showingAnimalList) {
            // انتخاب حیوان با کلیدهای عددی ۱ تا ۸
            for (int i = 0; i < Math.min(TOTAL_SLOTS, animalsToDisplay.size()); i++) {
                if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1 + i) ||
                    Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_1 + i)) {
                    selectedListIndex = i;
                    highlightSelection = true;

                    // اصلاح این قسمت - مقداردهی مستقیم نوع حیوان
                    AnimalDisplayData data = animalsToDisplay.get(i);
                    selectedAnimalType = data.name.toLowerCase();
                    selectingBuildingForAnimal = true;

                    break;
                }
            }
        }

        if (selectedAnimalType != null && selectingBuildingForAnimal) {
            // انتخاب طویله با کلید Z
            if (Gdx.input.isKeyJustPressed(Input.Keys.Z)) {
                if (barnCount > 0) {
                    transferAnimalToBuilding(selectedAnimalType, true, 0);
                    selectingBuildingForAnimal = false;
                    selectedAnimalType = null;
                } else {
                    System.out.println("هیچ طویله‌ای وجود ندارد!");
                }
                return;
            }

            // انتخاب قفس با کلید X
            if (Gdx.input.isKeyJustPressed(Input.Keys.X)) {
                if (coopCount > 0) {
                    transferAnimalToBuilding(selectedAnimalType, false, 0);
                    selectingBuildingForAnimal = false;
                    selectedAnimalType = null;
                } else {
                    System.out.println("هیچ قفسی وجود ندارد!");
                }
                return;
            }

            // لغو انتخاب با ESCAPE
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                selectedAnimalType = null;
                selectingBuildingForAnimal = false;
                System.out.println("انتخاب حیوان لغو شد.");
            }
        }


        // کنترل‌های داخل ساختمان
        if (showingInterior) {
            // کلیدهای تعامل با حیوان انتخاب شده
            if (selectedAnimal != null) {
                if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
                    startPetting(selectedAnimal);
                }
                if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
                    startFeeding(selectedAnimal);
                }
                if (Gdx.input.isKeyJustPressed(Input.Keys.O)) {
                    selectedAnimal.setOutside(!selectedAnimal.isOutside());
                }
                if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
                    if (selectedAnimal.hasProduct()) {
                        selectedAnimal.collectProduct();
                    }
                }
                if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
                    sellAnimal(selectedAnimal);
                    selectedAnimal = null;
                    showingAnimalMenu = false;
                }
            }

            // خروج از نمای داخلی با ESCAPE
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                closeInteriorView();
            }
        }

        // نمایش/مخفی کردن لیست حیوانات با کلید L
        if (Gdx.input.isKeyJustPressed(Input.Keys.L)) {
            showingAnimalList = !showingAnimalList;
            if (showingAnimalList) {
                updateListPosition();
            }
        }

        if (showingInterior && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            float mouseX = Gdx.input.getX();
            float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY(); // تبدیل به مختصات Y بالا

            List<Animal> animals = showingBarnInterior ?
                animalsInBarns[selectedBuildingIndex] :
                animalsInCoops[selectedBuildingIndex];

            // بررسی کلیک روی هر حیوان
            for (Animal animal : animals) {
                if (isClickOnAnimal(animal, mouseX, mouseY)) {
                    System.out.println("کلیک روی حیوان: " + animal.getName());
                    selectedAnimal = animal;
                    showingAnimalMenu = true;
                    break;
                }
            }
        }


        if (showingAnimalMenu && selectedAnimal != null && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            float mouseX = Gdx.input.getX();
            float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
            handleAnimalMenuClick(mouseX, mouseY);
        }
    }




    private void chooseRandomDirection(Animal animal) {
        int direction = (int)(Math.random() * 5);
        Animal.Direction newDirection;

        switch (direction) {
            case 0: newDirection = Animal.Direction.UP; break;
            case 1: newDirection = Animal.Direction.DOWN; break;
            case 2: newDirection = Animal.Direction.LEFT; break;
            case 3: newDirection = Animal.Direction.RIGHT; break;
            default:
                animal.setMoving(false);
                return;
        }

        animal.setDirection(newDirection);
        animal.setMoving(true);
    }

    private void updateAnimalPosition(Animal animal, float delta) {
        float currentX = animal.getX();
        float currentY = animal.getY();
        float nextX = currentX;
        float nextY = currentY;
        float moveAmount = 80f * delta;

        switch (animal.getDirection()) {
            case UP: nextY += moveAmount; break;
            case DOWN: nextY -= moveAmount; break;
            case LEFT: nextX -= moveAmount; break;
            case RIGHT: nextX += moveAmount; break;
        }

        float homeX = currentX;
        float homeY = currentY;
        float distanceFromHome = (float) Math.sqrt(Math.pow(nextX - homeX, 2) + Math.pow(nextY - homeY, 2));

        if (distanceFromHome <= MAX_DISTANCE && canMoveTo(nextX, nextY)) {
            animal.setX(nextX);
            animal.setY(nextY);
        } else {
            animal.setMoving(false);
            chooseRandomDirection(animal);
        }
    }

    private boolean canMoveTo(float x, float y) {
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        return x >= 0 && y >= 0 && x + 64 <= screenWidth && y + 64 <= screenHeight;
    }

    public void render(SpriteBatch batch) {
        if (showingInterior) {
            renderInterior(batch);
        } else {
            for (int i = 0; i < barnCount; i++) {
                batch.draw(barnTexture, placedBarnsX[i], placedBarnsY[i]);
            }

            for (int i = 0; i < coopCount; i++) {
                batch.draw(coopTexture, placedCoopsX[i], placedCoopsY[i]);
            }

            if (isPlacingBarn) {
                batch.setColor(1, 1, 1, 0.7f);
                batch.draw(barnTexture, barnX, barnY);
                batch.setColor(1, 1, 1, 1);
            }

            if (isPlacingCoop) {
                batch.setColor(1, 1, 1, 0.7f);
                batch.draw(coopTexture, coopX, coopY);
                batch.setColor(1, 1, 1, 1);
            }

            // Render free animals
            for (Animal animal : freeAnimals) {
                if (animal.isFree()) {
                    renderAnimal(batch, animal);
                }
            }
        }

        if (showingAnimalList) {
            renderAnimalList(batch);
        }
    }

    private void renderFeedingAnimation(SpriteBatch batch) {
        if (feedingAnimal != null) {
            String animalType = feedingAnimal.getType().toLowerCase();
            Texture animalTexture = animalTextures.get(animalType);

            if (animalTexture != null) {
                Animation<TextureRegion> feedAnimation = feedAnimations.get(animalType);
                TextureRegion currentFrame = feedAnimation != null ?
                    feedAnimation.getKeyFrame(feedingTime, false) :
                    idleFrames.get(animalType);

                batch.draw(currentFrame, feedingAnimal.getX(), feedingAnimal.getY());

                float hopperX = feedingAnimal.getX() + currentFrame.getRegionWidth()/2 - hayHopperTexture.getWidth()/2;
                float hopperY = feedingAnimal.getY() - hayHopperTexture.getHeight();

                Texture hopperTexture = feedingTime < FEEDING_DURATION/2 ?
                    hayHopperFullTexture : hayHopperTexture;
                batch.draw(hopperTexture, hopperX, hopperY);
            }
        }
    }

    private void renderPettingAnimation(SpriteBatch batch) {
        if (pettingAnimal != null) {
            String animalType = pettingAnimal.getType().toLowerCase();
            Animation<TextureRegion> petAnimation = petAnimations.get(animalType);

            if (petAnimation != null) {
                TextureRegion currentFrame = petAnimation.getKeyFrame(pettingTime, false);
                batch.setColor(1, 1, 1, 0.8f + 0.2f * (float)Math.sin(pettingTime * 10));
                batch.draw(currentFrame, pettingAnimal.getX(), pettingAnimal.getY());
                batch.setColor(1, 1, 1, 1);
            }
        }
    }


    private void renderAnimalList(SpriteBatch batch) {
        batch.draw(listBackgroundTexture, listX, listY, listWidth, listHeight);

        float slotWidth = listWidth / SLOTS_PER_ROW;
        float slotHeight = listHeight / 2;

        for (int i = 0; i < TOTAL_SLOTS; i++) {
            int row = i / SLOTS_PER_ROW;
            int col = i % SLOTS_PER_ROW;

            float slotX = listX + col * slotWidth;
            float slotY = listY + (1 - row) * slotHeight;

            if (i < animalsToDisplay.size()) {
                // نمایش قاب برجسته برای حیوان انتخاب شده
                if (highlightSelection && selectedListIndex == i) {
                    batch.setColor(1, 0.8f, 0.2f, 0.7f); // رنگ زرد برای انتخاب
                    batch.draw(whitePixelTexture, slotX, slotY, slotWidth, slotHeight);
                    batch.setColor(1, 1, 1, 1);
                }

                String animalName = animalsToDisplay.get(i).name;
                Texture animalTexture = animalListTextures.get(animalName.toLowerCase());

                if (animalTexture != null) {
                    float imageMargin = slotWidth * 0.1f;
                    float imageWidth = slotWidth - 2 * imageMargin;
                    float imageHeight = slotHeight * 0.45f;
                    float imageX = slotX + imageMargin;
                    float imageY = slotY + slotHeight * 0.42f;

                    batch.draw(animalTexture, imageX, imageY, imageWidth, imageHeight);
                }

                // نمایش شماره کلید برای انتخاب
                font.setColor(1, 1, 1, 1);
                String keyNumber = String.valueOf(i + 1);
                layout.setText(font, keyNumber);
                font.draw(batch, keyNumber, slotX + 10, slotY + slotHeight - 10);

                // نمایش نام حیوان
                layout.setText(animalNameFont, animalName);
                float textX = slotX + (slotWidth - layout.width) / 2;
                float textY = slotY + slotHeight - 30;

                animalNameFont.draw(batch, animalName, textX, textY);
            }
        }

        // دکمه بستن
        renderButton(batch, "Close", listX + listWidth - 60, listY + 10, 50, 20);
    }



    public void selectAnimalFromList(String animalType) {
        this.selectedAnimalType = animalType;
        showingAnimalList = false;
        highlightSelection = false;
        selectedListIndex = -1;
        selectingBuildingForAnimal = true; // فعال کردن حالت انتخاب ساختمان
        waitingForBuildingSelection = true;
        System.out.println("حیوان " + animalType + " انتخاب شد. برای قرار دادن در طویله کلید B و برای قفس کلید K را فشار دهید.");
    }


    private void placeSelectedAnimalInBuilding(boolean isBarn, int buildingIndex) {
        if (selectedAnimalType == null) return;

        // ایجاد حیوان جدید با نوع انتخاب شده
        String animalName = selectedAnimalType + "_" + (int)(Math.random() * 1000);
        Animal newAnimal = new Animal(animalName, selectedAnimalType);

        // تعیین موقعیت تصادفی برای حیوان در ساختمان
        float x = 100 + (float)(Math.random() * 300);
        float y = 100 + (float)(Math.random() * 300);
        newAnimal.setPosition(x, y);

        // قرار دادن حیوان در ساختمان
        if (isBarn) {
            if (buildingIndex < barnCount) {
                animalsInBarns[buildingIndex].add(newAnimal);
                System.out.println(selectedAnimalType + " با نام " + animalName + " در طویله قرار گرفت.");
            }
        } else {
            if (buildingIndex < coopCount) {
                animalsInCoops[buildingIndex].add(newAnimal);
                System.out.println(selectedAnimalType + " با نام " + animalName + " در قفس قرار گرفت.");
            }
        }
    }



    // متد برای چک کردن وضعیت انتظار برای انتخاب ساختمان
    public boolean isWaitingForBuildingSelection() {
        return waitingForBuildingSelection && selectedAnimalType != null;
    }

    private void renderAnimalMenu(SpriteBatch batch) {
        float menuWidth = 450;
        float menuHeight = 300;
        float menuX = selectedAnimal.getX() + 60;
        float menuY = selectedAnimal.getY() + 60;
        float padding = 30;
        float lineSpacing = 40;

        // اطمینان از اینکه منو داخل صفحه باشه
        if (menuX + menuWidth > Gdx.graphics.getWidth()) {
            menuX = Gdx.graphics.getWidth() - menuWidth - 10;
        }
        if (menuY + menuHeight > Gdx.graphics.getHeight()) {
            menuY = Gdx.graphics.getHeight() - menuHeight - 10;
        }

        // پس‌زمینه منو
        batch.setColor(0.15f, 0.15f, 0.15f, 0.9f);
        batch.draw(whitePixelTexture, menuX, menuY, menuWidth, menuHeight);
        batch.setColor(1, 1, 1, 1);

        if (font != null) {
            font.setColor(1, 1, 1, 1);
            float textX = menuX + padding;
            float textY = menuY + menuHeight - padding;

            // اطلاعات حیوان
            String[] info = {
                selectedAnimal.getName() + " (" + selectedAnimal.getType() + ")",
                "Friendship: " + selectedAnimal.getFriendship(),
                "Fed today: " + (selectedAnimal.isFedToday() ? "Yes" : "No"),
                "Petted today: " + (selectedAnimal.isPetToday() ? "Yes" : "No"),
                "Outside: " + (selectedAnimal.isOutside() ? "Yes" : "No"),
                "Has product: " + (selectedAnimal.hasProduct() ? "Yes" : "No")
            };

            for (String line : info) {
                font.draw(batch, line, textX, textY);
                textY -= lineSpacing;
            }

            // دکمه‌ها (چیدمان به صورت دو ستون)
            float btnWidth = 100;
            float btnHeight = 25;
            float btnStartY = menuY + padding;
            float btnSpacing = 8;

            renderButton(batch, "Pet (P)", textX, btnStartY, btnWidth, btnHeight);
            renderButton(batch, "Feed (F)", textX + btnWidth + btnSpacing, btnStartY, btnWidth, btnHeight);

            btnStartY += btnHeight + btnSpacing;
            renderButton(batch,
                selectedAnimal.isOutside() ? "Bring Inside (O)" : "Take Outside (O)",
                textX, btnStartY, menuWidth - (2 * padding), btnHeight);

            if (selectedAnimal.hasProduct()) {
                btnStartY += btnHeight + btnSpacing;
                renderButton(batch, "Collect (C)", textX, btnStartY, btnWidth, btnHeight);
                renderButton(batch, "Sell (S)", textX + btnWidth + btnSpacing, btnStartY, btnWidth, btnHeight);
            } else {
                btnStartY += btnHeight + btnSpacing;
                renderButton(batch, "Sell (S)", textX, btnStartY, btnWidth, btnHeight);
            }
        }
    }



    private void renderButton(SpriteBatch batch, String text, float x, float y, float width, float height) {
        batch.setColor(0.4f, 0.4f, 0.4f, 1);
        batch.draw(whitePixelTexture, x, y, width, height);

        batch.setColor(0.6f, 0.6f, 0.6f, 1);
        batch.draw(whitePixelTexture, x, y, width, 1);
        batch.draw(whitePixelTexture, x, y, 1, height);
        batch.draw(whitePixelTexture, x + width - 1, y, 1, height);
        batch.draw(whitePixelTexture, x, y + height - 1, width, 1);

        if (font != null) {
            font.setColor(1, 1, 1, 1);
            layout.setText(font, text);
            float textX = x + (width - layout.width) / 2;
            float textY = y + (height + layout.height) / 2;
            font.draw(batch, text, textX, textY);
        }

        batch.setColor(1, 1, 1, 1);
    }

    private void handleAnimalMenuClick(float mouseX, float mouseY) {
        float menuWidth = 200;
        float menuHeight = 150;
        float menuX = selectedAnimal.getX() + 50;
        float menuY = selectedAnimal.getY() + 50;

        // Adjust menu position if it would go off screen
        if (menuX + menuWidth > Gdx.graphics.getWidth()) {
            menuX = Gdx.graphics.getWidth() - menuWidth - 10;
        }
        if (menuY + menuHeight > Gdx.graphics.getHeight()) {
            menuY = Gdx.graphics.getHeight() - menuHeight - 10;
        }

        // Check if buttons are clicked
        if (isClickOnButton(mouseX, mouseY, menuX + 10, menuY + 10, 80, 20)) {
            startPetting(selectedAnimal);
        } else if (isClickOnButton(mouseX, mouseY, menuX + 110, menuY + 10, 80, 20)) {
            startFeeding(selectedAnimal);
        } else if (isClickOnButton(mouseX, mouseY, menuX + 10, menuY + 35, 180, 20)) {
            selectedAnimal.setOutside(!selectedAnimal.isOutside());
        } else if (selectedAnimal.hasProduct() && isClickOnButton(mouseX, mouseY, menuX + 10, menuY + 60, 80, 20)) {
            selectedAnimal.collectProduct();
        } else if (isClickOnButton(mouseX, mouseY, menuX + 110, menuY + 60, 80, 20)) {
            sellAnimal(selectedAnimal);
            selectedAnimal = null;
            showingAnimalMenu = false;
        }
    }

    private boolean isClickOnButton(float mouseX, float mouseY, float buttonX, float buttonY, float buttonWidth, float buttonHeight) {
        return mouseX >= buttonX && mouseX <= buttonX + buttonWidth &&
            mouseY >= buttonY && mouseY <= buttonY + buttonHeight;
    }


    private boolean isClickOnBuilding(float buildingX, float buildingY, float width, float height, float mouseX, float mouseY) {
        // افزایش ناحیه کلیک برای راحتی کاربر
        float hitboxExpansion = 30.0f;

        boolean result = mouseX >= (buildingX - hitboxExpansion) &&
            mouseX <= (buildingX + width + hitboxExpansion) &&
            mouseY >= (buildingY - hitboxExpansion) &&
            mouseY <= (buildingY + height + hitboxExpansion);

        return result;
    }


    private boolean isClickOnAnimal(Animal animal, float mouseX, float mouseY) {
        // از تصاویر TAKI برای تشخیص کلیک استفاده می‌کنیم
        Texture texture = animalListTextures.get(animal.getType().toLowerCase());
        if (texture == null) return false;

        // مقیاس بزرگتر برای نمایش حیوان
        float scale = 2.0f;  // همان مقیاسی که در renderAnimal استفاده می‌شود
        float width = texture.getWidth() * scale;
        float height = texture.getHeight() * scale;

        // افزایش ناحیه کلیک برای راحتی کاربر
        float hitboxExpansion = 50.0f;

        boolean result = mouseX >= (animal.getX() - hitboxExpansion) &&
            mouseX <= (animal.getX() + width + hitboxExpansion) &&
            mouseY >= (animal.getY() - hitboxExpansion) &&
            mouseY <= (animal.getY() + height + hitboxExpansion);

        if (result) {
            System.out.println("کلیک روی حیوان " + animal.getName() + " تشخیص داده شد.");
            System.out.println("موقعیت حیوان: X=" + animal.getX() + ", Y=" + animal.getY());
            System.out.println("موقعیت کلیک: X=" + mouseX + ", Y=" + mouseY);
            System.out.println("ابعاد حیوان: W=" + width + ", H=" + height);
        }

        return result;
    }



    private boolean isInInteriorBounds(float x, float y) {
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        Texture interiorTexture = showingBarnInterior ? barnInteriorTexture : coopInteriorTexture;
        float scale = Math.min(screenWidth / interiorTexture.getWidth(), screenHeight / interiorTexture.getHeight()) * interiorScale;
        float scaledWidth = interiorTexture.getWidth() * scale;
        float scaledHeight = interiorTexture.getHeight() * scale;
        float interiorX = (screenWidth - scaledWidth) / 2;
        float interiorY = (screenHeight - scaledHeight) / 2;

        return x >= interiorX && x <= interiorX + scaledWidth &&
            y >= interiorY && y <= interiorY + scaledHeight;
    }

    public void startPlacingBarn(float x, float y) {
        isPlacingBarn = true;
        isPlacingCoop = false;
        barnX = x;
        barnY = y;
    }

    public void startPlacingCoop(float x, float y) {
        isPlacingCoop = true;
        isPlacingBarn = false;
        coopX = x;
        coopY = y;
    }

    private void placeBarn() {
        if (barnCount < placedBarnsX.length) {
            placedBarnsX[barnCount] = barnX;
            placedBarnsY[barnCount] = barnY;
            barnCount++;
            isPlacingBarn = false;
        }
    }

    private void placeCoop() {
        if (coopCount < placedCoopsX.length) {
            placedCoopsX[coopCount] = coopX;
            placedCoopsY[coopCount] = coopY;
            coopCount++;
            isPlacingCoop = false;
        }
    }

    public void addAnimalToBuilding(Animal animal, boolean isBarn, int index) {
        if (isBarn) {
            if (index >= 0 && index < barnCount) {
                animalsInBarns[index].add(animal);
                animal.setFree(false);
            }
        } else {
            if (index >= 0 && index < coopCount) {
                animalsInCoops[index].add(animal);
                animal.setFree(false);
            }
        }
    }

    private void removeAnimalFromBuilding(Animal animal) {
        if (showingBarnInterior && selectedBuildingIndex >= 0) {
            animalsInBarns[selectedBuildingIndex].remove(animal);
        } else if (showingCoopInterior && selectedBuildingIndex >= 0) {
            animalsInCoops[selectedBuildingIndex].remove(animal);
        }
        animal.setFree(true);
        freeAnimals.add(animal);
    }

    public void startPetting(Animal animal) {
        pettingAnimal = animal;
        pettingTime = 0;
    }

    public void startFeeding(Animal animal) {
        feedingAnimal = animal;
        feedingTime = 0;
    }

    public void sellAnimal(Animal animal) {
        // Implement selling logic here
        if (showingBarnInterior && selectedBuildingIndex >= 0) {
            animalsInBarns[selectedBuildingIndex].remove(animal);
        } else if (showingCoopInterior && selectedBuildingIndex >= 0) {
            animalsInCoops[selectedBuildingIndex].remove(animal);
        }
        freeAnimals.remove(animal);
    }

    public AnimalDisplayData getAnimalAt(float screenX, float screenY) {
        if (!showingAnimalList) return null;

        float slotWidth = listWidth / SLOTS_PER_ROW;
        float slotHeight = listHeight / 2;

        float touchX = screenX;
        float touchY = Gdx.graphics.getHeight() - screenY;

        if (touchX >= listX && touchX <= listX + listWidth &&
            touchY >= listY && touchY <= listY + listHeight) {

            int col = (int)((touchX - listX) / slotWidth);
            int row = 1 - (int)((touchY - listY) / slotHeight);
            int index = row * SLOTS_PER_ROW + col;

            if (index >= 0 && index < animalsToDisplay.size()) {
                return animalsToDisplay.get(index);
            }
        }
        return null;
    }


    public boolean isPlacingBarn() {
        return isPlacingBarn;
    }

    public boolean isPlacingCoop() {
        return isPlacingCoop;
    }

    public boolean isShowingInterior() {
        return showingInterior;
    }

    public boolean isShowingBarnInterior() {
        return showingBarnInterior;
    }

    public boolean isShowingCoopInterior() {
        return showingCoopInterior;
    }

    public void dispose() {
        if (barnTexture != null) barnTexture.dispose();
        if (coopTexture != null) coopTexture.dispose();
        if (barnInteriorTexture != null) barnInteriorTexture.dispose();
        if (coopInteriorTexture != null) coopInteriorTexture.dispose();
        if (hayHopperTexture != null) hayHopperTexture.dispose();
        if (hayHopperFullTexture != null) hayHopperFullTexture.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (font != null) font.dispose();
        if (animalNameFont != null) animalNameFont.dispose();
        if (whitePixelTexture != null) whitePixelTexture.dispose();
        if (listBackgroundTexture != null) listBackgroundTexture.dispose();

        for (Texture texture : animalTextures.values()) {
            if (texture != null) texture.dispose();
        }

        for (Texture texture : productTextures.values()) {
            if (texture != null) texture.dispose();
        }

        for (Texture texture : animalListTextures.values()) {
            if (texture != null) texture.dispose();
        }
    }

    public boolean isShowingAnimalList() {
        return showingAnimalList;
    }


    private void renderInterior(SpriteBatch batch) {
        // استفاده از کد اصلی شما برای رندر داخل ساختمان
        Texture interiorTexture = showingBarnInterior ? barnInteriorTexture : coopInteriorTexture;

        // ذخیره ماتریس پروجکشن فعلی
        Matrix4 originalMatrix = batch.getProjectionMatrix();

        // تنظیم ماتریس پروجکشن جدید برای نمایش کامل عکس
        Matrix4 screenMatrix = new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setProjectionMatrix(screenMatrix);

        // محاسبه ابعاد متناسب با حفظ نسبت تصویر
        float imageWidth = interiorTexture.getWidth();
        float imageHeight = interiorTexture.getHeight();
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        float scale = Math.min(screenWidth / imageWidth, screenHeight / imageHeight);
        float scaledWidth = imageWidth * scale;
        float scaledHeight = imageHeight * scale;

        // محاسبه موقعیت برای نمایش در مرکز صفحه
        float x = (screenWidth - scaledWidth) / 2;
        float y = (screenHeight - scaledHeight) / 2;

        // نمایش تصویر با ابعاد محاسبه شده
        batch.draw(interiorTexture, x, y, scaledWidth, scaledHeight);

        // بازگرداندن ماتریس پروجکشن اصلی
        batch.setProjectionMatrix(originalMatrix);

        // رندر حیوانات داخل ساختمان
        List<Animal> animals = showingBarnInterior ?
            animalsInBarns[selectedBuildingIndex] :
            animalsInCoops[selectedBuildingIndex];

        for (Animal animal : animals) {
            renderAnimal(batch, animal);
        }

        // نمایش منوی حیوان اگر حیوانی انتخاب شده باشد
        if (showingAnimalMenu && selectedAnimal != null) {
            renderAnimalMenu(batch);
        }
    }

    public void transferAnimalToBuilding(String animalType, boolean isBarn, int buildingIndex) {
        // ایجاد یک حیوان جدید با نوع انتخاب شده
        String animalName = animalType + "_" + (int)(Math.random() * 1000);
        Animal newAnimal = new Animal(animalName, animalType);

        // تنظیم موقعیت تصادفی برای حیوان در ساختمان
        float x = 100 + (float)(Math.random() * 200);
        float y = 100 + (float)(Math.random() * 200);
        newAnimal.setPosition(x, y);

        // مقداردهی جهت اولیه و وضعیت حیوان
        newAnimal.setDirection(Animal.Direction.DOWN);
        newAnimal.setMoving(false);
        newAnimal.setFree(false);

        // اضافه کردن حیوان به ساختمان مناسب
        if (isBarn) {
            if (buildingIndex < barnCount) {
                animalsInBarns[buildingIndex].add(newAnimal);
                System.out.println(animalType + " با نام " + animalName + " به طویله اضافه شد.");
            }
        } else {
            if (buildingIndex < coopCount) {
                animalsInCoops[buildingIndex].add(newAnimal);
                System.out.println(animalType + " با نام " + animalName + " به قفس اضافه شد.");
            }
        }

        // نمایش داخل ساختمان بعد از قرار دادن حیوان
        showingInterior = true;
        showingBarnInterior = isBarn;
        showingCoopInterior = !isBarn;
        selectedBuildingIndex = buildingIndex;

        // پاک کردن حالت انتخاب
        selectedAnimalType = null;
        selectingBuildingForAnimal = false;
    }

    // در کلاس AnimalBuildingController
    public boolean handleClick(float mouseX, float mouseY) {
        if (!showingInterior) {
            return false;
        }

        // بررسی کلیک روی حیوانات داخل ساختمان
        List<Animal> animals = showingBarnInterior ?
            animalsInBarns[selectedBuildingIndex] :
            animalsInCoops[selectedBuildingIndex];

        for (Animal animal : animals) {
            if (isClickOnAnimal(animal, mouseX, mouseY)) {
                System.out.println("کلیک روی حیوان: " + animal.getName());
                selectedAnimal = animal;
                showingAnimalMenu = true;
                return true;
            }
        }

        return false;
    }


    private void renderAnimal(SpriteBatch batch, Animal animal) {
        String animalType = animal.getType().toLowerCase();

        // از تصاویر تکی TAKI استفاده می‌کنیم
        Texture animalTexture = animalListTextures.get(animalType);

        if (animalTexture != null) {
            // نمایش نشانگر انتخاب برای حیوان انتخاب شده
            if (selectedAnimal == animal) {
                batch.setColor(1, 1, 0, 0.5f); // رنگ زرد نیمه شفاف
                float scale = 2.0f;
                float width = animalTexture.getWidth() * scale;
                float height = animalTexture.getHeight() * scale;
                batch.draw(whitePixelTexture,
                    animal.getX() - 5,
                    animal.getY() - 5,
                    width + 10,
                    height + 10);
                batch.setColor(1, 1, 1, 1); // بازگشت به رنگ عادی
            }

            // مقیاس بزرگتر برای نمایش حیوان
            float scale = 2.0f;  // این مقدار را می‌توانید تنظیم کنید

            // محاسبه ابعاد جدید
            float width = animalTexture.getWidth() * scale;
            float height = animalTexture.getHeight() * scale;

            // رسم حیوان با سایز بزرگتر
            batch.draw(animalTexture, animal.getX(), animal.getY(), width, height);

            // رسم نام حیوان بالای آن
            if (font != null) {
                font.setColor(1, 1, 1, 1);
                layout.setText(font, animal.getName());
                font.draw(batch, animal.getName(),
                    animal.getX() + (width/2) - layout.width/2,
                    animal.getY() + height + 15);
            }

            // رسم محصول اگر حیوان محصولی داشته باشد
            if (animal.hasProduct()) {
                Texture productTexture = productTextures.get(animal.getProduct());
                if (productTexture != null) {
                    batch.draw(productTexture,
                        animal.getX() + (width/2) - productTexture.getWidth()/2,
                        animal.getY() + height + 25);
                }
            }
        }
    }

    /**
     * نمایش داخل ساختمان
     * @param isBarn آیا ساختمان طویله است؟
     * @param index شماره ساختمان
     */
    public void showBuildingInterior(boolean isBarn, int index) {
        showingInterior = true;
        showingBarnInterior = isBarn;
        showingCoopInterior = !isBarn;
        selectedBuildingIndex = index;
        selectedAnimal = null;
        showingAnimalMenu = false;

        // اطمینان از اینکه لیست حیوانات موجود است
        List<Animal> animals = isBarn ? animalsInBarns[index] : animalsInCoops[index];

        System.out.println("نمایش داخل " + (isBarn ? "طویله" : "قفس") + " شماره " + (index + 1));
        System.out.println("تعداد حیوانات داخل: " + animals.size());

        // اگر هیچ حیوانی در ساختمان نباشد، یک حیوان نمونه اضافه کنیم
        if (animals.isEmpty()) {
            // اضافه کردن یک حیوان نمونه متناسب با نوع ساختمان
            String animalType = isBarn ? "cow" : "chicken";
            String animalName = animalType + "_" + (int)(Math.random() * 1000);
            Animal newAnimal = new Animal(animalName, animalType);

            // تنظیم موقعیت تصادفی برای حیوان در ساختمان
            float x = 100 + (float)(Math.random() * 200);
            float y = 100 + (float)(Math.random() * 200);
            newAnimal.setPosition(x, y);

            // اضافه کردن حیوان به ساختمان
            animals.add(newAnimal);

            System.out.println("یک " + animalType + " با نام " + animalName + " به ساختمان اضافه شد.");
        }
    }



    public void closeInteriorView() {
        showingInterior = false;
        showingBarnInterior = false;
        showingCoopInterior = false;
        selectedBuildingIndex = -1;
        showingAnimalMenu = false;
        selectedAnimal = null;
    }

    public void setShowingAnimalList(boolean showing) {
        showingAnimalList = showing;
    }

    public void cancelPlacement() {
        isPlacingBarn = false;
        isPlacingCoop = false;
    }

    /**
     * بررسی وجود حداقل یک طویله در نقشه
     */
    public boolean hasBarn() {
        return barnCount > 0;
    }

    /**
     * بررسی وجود حداقل یک قفس در نقشه
     */
    public boolean hasCoop() {
        return coopCount > 0;
    }

    /**
     * نمایش نزدیک‌ترین طویله به موقعیت داده شده
     */
    public void showNearestBarn(float x, float y) {
        if (barnCount == 0) {
            System.out.println("هیچ طویله‌ای در نقشه وجود ندارد!");
            return;
        }

        // پیدا کردن نزدیک‌ترین طویله
        int nearestBarnIndex = 0;
        float minDistance = Float.MAX_VALUE;

        for (int i = 0; i < barnCount; i++) {
            float distance = calculateDistance(x, y, placedBarnsX[i], placedBarnsY[i]);
            if (distance < minDistance) {
                minDistance = distance;
                nearestBarnIndex = i;
            }
        }

        // نمایش داخل نزدیک‌ترین طویله
        showBuildingInterior(true, nearestBarnIndex);
    }

    /**
     * نمایش نزدیک‌ترین قفس به موقعیت داده شده
     */
    public void showNearestCoop(float x, float y) {
        if (coopCount == 0) {
            System.out.println("هیچ قفسی در نقشه وجود ندارد!");
            return;
        }

        // پیدا کردن نزدیک‌ترین قفس
        int nearestCoopIndex = 0;
        float minDistance = Float.MAX_VALUE;

        for (int i = 0; i < coopCount; i++) {
            float distance = calculateDistance(x, y, placedCoopsX[i], placedCoopsY[i]);
            if (distance < minDistance) {
                minDistance = distance;
                nearestCoopIndex = i;
            }
        }

        // نمایش داخل نزدیک‌ترین قفس
        showBuildingInterior(false, nearestCoopIndex);
    }

    private float calculateDistance(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }



}
