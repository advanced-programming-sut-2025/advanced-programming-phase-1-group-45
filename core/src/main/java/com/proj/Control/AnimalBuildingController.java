package com.proj.Control;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.proj.map.GameMap;
import com.proj.Model.Animal.Animal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnimalBuildingController {
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
    private List<Animal>[] animalsInBarns = new List[100];
    private List<Animal>[] animalsInCoops = new List[100];

    // Interior view state
    private boolean showingInterior = false;
    private boolean showingBarnInterior = false;
    private boolean showingCoopInterior = false;
    private int selectedBuildingIndex = -1;
    private String selectedAnimalType = null;

    private List<Animal> freeAnimals = new ArrayList<>();


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
    private Map<String, Texture> animalTextures = new HashMap<>();
    private Map<String, Texture> productTextures = new HashMap<>();
    private Texture hayHopperTexture;
    private Texture hayHopperFullTexture;

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

            font = new BitmapFont();
            animalNameFont = new BitmapFont();
            animalNameFont.setColor(Color.BLACK);
            layout = new GlyphLayout();
            createWhitePixelTexture();

            // Initialize animal display list
            updateListPosition();
            loadAnimalsToDisplay();

            loadAnimalTextures();
            loadAnimalListTextures();

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
        listWidth = listBackgroundTexture.getWidth() * listScale;
        listHeight = listBackgroundTexture.getHeight() * listScale;



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



    public void update(float delta) {
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
            if (Gdx.input.isKeyJustPressed(Input.Keys.Z)) {
                if (barnCount > 0) {
                    transferAnimalToBuilding(selectedAnimalType, true, 0);
                    selectingBuildingForAnimal = false;
                    selectedAnimalType = null;
                }
                return;
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.X)) {
                if (coopCount > 0) {
                    transferAnimalToBuilding(selectedAnimalType, false, 0);
                    selectingBuildingForAnimal = false;
                    selectedAnimalType = null;
                }
                return;
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                selectedAnimalType = null;
                selectingBuildingForAnimal = false;
            }
        }


        // کنترل‌های داخل ساختمان
        if (showingInterior) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                closeInteriorView();
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.L)) {
            showingAnimalList = !showingAnimalList;
            if (showingAnimalList) {
                updateListPosition();
            }
        }
    }


    private void closeInteriorView() {
        showingInterior = false;
        showingBarnInterior = false;
        showingCoopInterior = false;
        selectedBuildingIndex = -1;
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



    public boolean isPlacingBarn() {
        return isPlacingBarn;
    }

    public boolean isPlacingCoop() {
        return isPlacingCoop;
    }

    public boolean isShowingInterior() {
        return showingInterior;
    }


    public void dispose() {
        if (barnTexture != null) barnTexture.dispose();
        if (coopTexture != null) coopTexture.dispose();
        if (barnInteriorTexture != null) barnInteriorTexture.dispose();
        if (coopInteriorTexture != null) coopInteriorTexture.dispose();
        if (hayHopperTexture != null) hayHopperTexture.dispose();
        if (hayHopperFullTexture != null) hayHopperFullTexture.dispose();
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

    private void renderAnimal(SpriteBatch batch, Animal animal) {
        String animalType = animal.getType().toLowerCase();

        Texture animalTexture = animalListTextures.get(animalType);

        if (animalTexture != null) {
            float scale = 2.0f;  // این مقدار را می‌توانید تنظیم کنید

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
                    animal.getY() - 5);
            }

            // رسم محصول اگر حیوان محصولی داشته باشد
            if (animal.hasProduct()) {
                Texture productTexture = productTextures.get(animal.getProduct());
                if (productTexture != null) {
                    batch.draw(productTexture,
                        animal.getX() + (width/2) - productTexture.getWidth()/2,
                        animal.getY() + height + 5);
                }
            }
        }
    }

}

