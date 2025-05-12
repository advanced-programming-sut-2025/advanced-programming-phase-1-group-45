package models.Enums;

import java.util.List;
import java.util.Map;

public enum NPCCharacters {

    SEBASTIAN(
            "Sebastian",
            List.of("Wool", "Pumpkin Pie", "Pizza"),
            List.of(
                    new Quest("Deliver Pumpkin Pie", Map.of("Pumpkin Pie", 1), Map.of("Gold", 5000)),
                    new Quest("Deliver 150 Stone", Map.of("Stone", 150), Map.of("Quartz", 50))
            ),
            new Location(15, 23, "Mountain")
    ),

    ABIGAIL(
            "Abigail",
            List.of("Stone", "Iron Ore", "Coffee"),
            List.of(
                    new Quest("Deliver Pumpkin", Map.of("Pumpkin", 1), Map.of("Gold", 500)),
                    new Quest("Deliver 50 Wheat", Map.of("Wheat", 50), Map.of("Iridium Sprinkler", 1))
            ),
            new Location(32, 10, "Town Square")
    ),
    HARVEY(
            "Harvey",
            List.of("Coffee", "Pickles", "Wine"),
            List.of(
                    new Quest("Deliver 1 Salmon",
                            Map.of("Salmon", 1),
                            Map.of("Friendship Level", 1)
                    ),
                    new Quest("Deliver 5 Wine Bottles",
                            Map.of("Wine", 5),
                            Map.of("Salad", 5)
                    )
            ),
            new Location(28, 15, "Clinic")
    ),

    LEAH(
            "Leah",
            List.of("Salad", "Grapes", "Wine"),
            List.of(
                    new Quest("Deliver 12 Random Plants",
                            Map.of("Any Plant", 12),
                            Map.of("Gold Coin", 750)
                    ),
                    new Quest("Deluxe Scarecrow Blueprint",
                            Map.of("Deluxe Scarecrow", 3),
                            Map.of("Wood", 200)
                    )
            ),
            new Location(12, 8, "Forest Cottage")
    ),

    ROBIN(
            "Robin",
            List.of("Spaghetti", "Wood", "Iron Bar"),
            List.of(
                    new Quest("Deliver 10 Iron Bars",
                            Map.of("Iron Bar", 10),
                            Map.of("Bee House", 3)
                    ),
                    new Quest("Deliver 1000 Wood",
                            Map.of("Wood", 1000),
                            Map.of("Gold Coin", 25000)
                    )
            ),
            new Location(35, 42, "Carpenter Workshop")
    );

    private final String name;
    private final List<String> favoriteItems;
    private final List<Quest> quests;
    private final Location homeLocation;

    NPCCharacters(String name, List<String> favoriteItems, List<Quest> quests, Location homeLocation) {
        this.name = name;
        this.favoriteItems = favoriteItems;
        this.quests = quests;
        this.homeLocation = homeLocation;
    }

    // Getters
    public String getName() {
        return name;
    }

    public List<String> getFavoriteItems() {
        return favoriteItems;
    }

    public List<Quest> getQuests() {
        return quests;
    }

    public Location getHomeLocation() {
        return homeLocation;
    }

    // Nested Quest class
    public static class Quest {
        private final String title;
        private final Map<String, Integer> requirements;
        private final Map<String, Integer> rewards;

        public Quest(String title, Map<String, Integer> requirements, Map<String, Integer> rewards) {
            this.title = title;
            this.requirements = requirements;
            this.rewards = rewards;
        }

        public String getTitle() {
            return title;
        }

        public Map<String, Integer> getRequirements() {
            return requirements;
        }

        public Map<String, Integer> getRewards() {
            return rewards;
        }
    }

    // Nested Location class
    public static class Location {
        private final int x;
        private final int y;
        private final String mapName;

        public Location(int x, int y, String mapName) {
            this.x = x;
            this.y = y;
            this.mapName = mapName;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public String getMapName() {
            return mapName;
        }
    }

    public enum DialogueCondition {
        DEFAULT, MORNING, AFTERNOON, EVENING,
        RAINY, FESTIVAL, HIGH_FRIENDSHIP
    }

}