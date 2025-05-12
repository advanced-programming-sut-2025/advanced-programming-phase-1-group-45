package models;

import controllers.MenuController;
import controllers.TradingController;
import managers.UserManager;
//import models.time.GameTimeAndDate;
 import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

    public class Friendship {
        private final User player1;
        private final User player2;
        private int level;
        private int xp;
        private LocalDate lastInteractionDate;
        private final List<Interaction> interactionHistory;
        private boolean isMarried;
        private final List<Gift> pendingGifts = new ArrayList<>();
        private UserManager um;

        public enum InteractionType {
            TALK, TRADE, GIFT, HUG, BOUQUET, MARRIAGE
        }

        public static class Interaction {
            private final InteractionType type;
            private final LocalDateTime timestamp;
            private final String message;

            public Interaction(InteractionType type, String message) {
                this.type = type;
                this.timestamp = LocalDateTime.now();
                this.message = message;
            }
        }

        public Friendship(User player1, User player2) {
            this.player1 = player1;
            this.player2 = player2;
            this.level = 0;
            this.xp = 0;
            this.lastInteractionDate = LocalDate.now();
            this.interactionHistory = new ArrayList<>();
            this.isMarried = false;
        }

        public int getLevel() {
            return level;
        }

        public int getXp() {
            return xp;
        }

        public int getRequiredXp() {
            return 100 * (1 + level);
        }

        public void dailyUpdate() {
            LocalDate today = LocalDate.now();

            if (lastInteractionDate.isBefore(today)) {
                boolean hadInteractionToday = interactionHistory.stream()
                        .anyMatch(i -> i.timestamp.toLocalDate().equals(today));

                if (!hadInteractionToday) {
                    xp -= 10;

                    if (xp < 0) {
                        if (level > 0) {
                            level--;
                            xp = getRequiredXp() - 10;
                        } else {
                            xp = 0;
                        }
                    }
                }
            }
            lastInteractionDate = today;
        }

        public boolean addInteraction(InteractionType type, String message) {
            if (!canInteractToday() || !validateInteraction(type)) {
                return false;
            }

            int xpGain = calculateXpGain(type);
            xp += xpGain;

            interactionHistory.add(new Interaction(type, message));
            checkLevelUp();
            return true;
        }

        private int calculateXpGain(InteractionType type) {
            return switch (type) {
                case TALK -> 20;
                case TRADE -> 30;
                case GIFT -> 50;
                case HUG -> 70;
                case BOUQUET -> 100;
                case MARRIAGE -> 150;
                default -> 0;
            };
        }

        private boolean validateInteraction(InteractionType type) {
            return switch (type) {
                case HUG -> level >= 2;
                case BOUQUET -> level >= 2 && xp >= getRequiredXp();
                case MARRIAGE -> level == 3 && xp >= getRequiredXp();
                default -> true;
            };
        }

        private boolean hasBouquetGift() {
            return interactionHistory.stream()
                    .anyMatch(i -> i.type == InteractionType.BOUQUET);
        }

        public void marry() {
            if (level == 3 && xp >= getRequiredXp()) {
                isMarried = true;
                level = 4;
                mergeFinances();
            }
        }

        private void mergeFinances() {
            double totalMoney = player1.getMoney() + player2.getMoney();
            player1.setMoney(totalMoney);
            player2.setMoney(totalMoney);
        }

        public boolean canInteractToday() {
            return interactionHistory.stream()
                    .noneMatch(i -> i.timestamp.toLocalDate().equals(LocalDate.now()));
        }

        public List<String> getTalkHistory() {
            return interactionHistory.stream()
                    .filter(i -> i.type == InteractionType.TALK)
                    .map(this::formatMessage)
                    .collect(Collectors.toList());
        }

        private String formatMessage(Interaction interaction) {
            return String.format("[%s] %s: %s",
                    interaction.timestamp.format(DateTimeFormatter.ISO_LOCAL_TIME),
                    getSender(interaction),
                    interaction.message);
        }

        private String getSender(Interaction interaction) {
            // Implementation depends on how you track senders
            return "Unknown"; // Replace with actual sender logic
        }

        // Getters for game state inspection
        public boolean isMarried() {
            return isMarried;
        }

        public List<Interaction> getInteractionHistory() {
            return new ArrayList<>(interactionHistory);
        }

        public User getPlayer1() {
            return player1;
        }

        public User getPlayer2() {
            return player2;
        }

        public void handleTradeResult(boolean success) {
            if(success) {
                addInteraction(InteractionType.TRADE, "Successful trade");
                this.xp += 50;
            } else {
                this.xp = Math.max(0, this.xp - 30);
                addInteraction(InteractionType.TRADE, "Failed trade");
            }
            checkLevelUp();
        }

        public void sendGift(String command, MenuController controller) {
            String[] parts = command.split("\\s+");
            String item = null;
            int amount = 1;
            String receiver = null;
            User sender = controller.getCurrentUser();
            for(int i = 0; i < parts.length - 1; i++) {
                if(parts[i].equals("-u")) {receiver = parts[i + 1];}
                if(parts[i].equals("-i")) {item = parts[i + 1];}
                if(parts[i].equals("-a")) {amount = Integer.parseInt(parts[i + 1]);}
            }
            if(item == null || receiver == null || amount <= 0) {
                System.out.println("invalid command");
                return;
            }
            User receiverUser = um.getUser(receiver);
            if(receiverUser == null) {
                System.out.println("user not found");
                return;
            }
//            if(!controller.getCurrentSession().areNextToEachOther(sender.getUsername(), receiver)) {
//                System.out.println("You should be next to the gift receiver");
//                return;
//            }
            if (!canInteractToday()) System.out.println("Daily interaction limit reached");
            if (level < 1) System.out.println("Friendship level too low");
            if (!sender.removeItem(item, amount)) System.out.println("Not enough items");

            Gift gift = new Gift(sender, receiverUser, item, amount);
            sender.addItem(item, -amount);
            receiverUser.addItem(item, amount);
            pendingGifts.add(gift);
            addInteraction(InteractionType.GIFT, "Sent " + amount + " " + item);
            System.out.println("Gift sent! ID: " + gift.getId());
        }

        public String rateGift(String giftId, int rating) {
            Gift gift = pendingGifts.stream()
                    .filter(g -> g.id.equals(giftId))
                    .findFirst()
                    .orElse(null);

            if(gift == null) return "Invalid gift ID";
            if(rating < 1 || rating > 5) return "Invalid rating (1-5)";

            gift.rating = rating;
            int xpChange = 15 + 30 * (3 - rating);
            this.xp += xpChange;
            checkLevelUp();
            return "Gift rated! XP changed by " + xpChange;
        }

        public void processHug(String command, MenuController controller) {
            String[] parts = command.split("\\s+");
            if(parts.length != 2) {
                System.out.println("invalid command format");
            }
            String receiver = parts[1].substring(2);
//            if(controller.getCurrentSession().areNextToEachOther(controller.getCurrentUser().getUsername(), receiver)) {
//                System.out.println("You should be next to the user to hug");
//            }
            if(level >= 2 && validateProximity()) {
                addInteraction(InteractionType.HUG, "Hugged");
                this.xp += 60;
                checkLevelUp();
            }
        }

        public boolean processBouquetExchange() {
            if(level >= 2 && hasBouquet() && validateProximity()) {
                addInteraction(InteractionType.BOUQUET, "Exchanged bouquet");
                checkLevelUp();
                return true;
            }
            return false;
        }

        public String proposeMarriage(String ringType) {
            if(level != 3) return "Friendship level insufficient";
            if(!validateProximity()) return "Not nearby";
            if(!hasRing(ringType)) return "No marriage ring";
            if(!validateGenders()) return "Genders not compatible";

            addInteraction(InteractionType.MARRIAGE, "Marriage proposed");
            return "Marriage proposal sent!";
        }

        private void checkLevelUp() {
            while(xp >= getRequiredXp()) {
                xp -= getRequiredXp();
                if(level < 4) {
                    level++;

                    if(level == 3 && !hasBouquetGift()) {
                        xp = getRequiredXp() - 1;
                        level = 2;
                    }
                }
            }
        }

        private boolean validateProximity() {
            // Implement your proximity check logic
            return true;
        }

        private boolean hasBouquet() {
            return player1.hasItem("Bouquet") || player2.hasItem("Bouquet");
        }

        private boolean hasRing(String ringType) {
            return player1.hasItem(ringType);
        }

        private boolean validateGenders() {
            return !player1.getGender().equals(player2.getGender());
        }

        private boolean canGiveGift(String item, int amount) {
            return player1.getInventoryCount(item) >= amount;
        }
        public static class Gift {
            private final String id;
            private final String item;
            private final int amount;
            private final LocalDateTime sentTime;
            private Integer rating;
            private final User sender;
            private final User receiver;

            public Gift(User sender, User receiver, String item, int amount) {
                this.id = UUID.randomUUID().toString();
                this.sender = sender;
                this.receiver = receiver;
                this.item = item;
                this.amount = amount;
                this.sentTime = LocalDateTime.now();
                this.rating = null;
            }

            // Getters
            public String getId() { return id; }
            public String getItem() { return item; }
            public int getAmount() { return amount; }
            public Integer getRating() { return rating; }
            public User getSender() { return sender; }
            public User getReceiver() { return receiver; }
        }

    }

