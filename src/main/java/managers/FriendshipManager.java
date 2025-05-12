package managers;

import controllers.MenuController;
import models.*;
import models.User;

import java.sql.SQLOutput;
import java.time.LocalDate;
import java.util.*;

public class FriendshipManager {
    private final Map<String, Integer> friendshipPoints = new HashMap<>();
    private Map<String, LocalDate> lastInteractionDates = new HashMap<>();
    UserManager um;

    public int getPoint(String user1, String user2) {
        return friendshipPoints.getOrDefault(key(user1, user2), 0);
    }

    public int getLevel(String user1, String user2) {
        return getPoint(user1, user2)/200;
    }
    public void addPoint(String user1, String user2, int delta) {
        LocalDate today = LocalDate.now();
        String key = key(user1, user2);
        if (lastInteractionDates.getOrDefault(key, null) != null &&
                lastInteractionDates.get(key).isEqual(today)) {
            return;
        }
        int points = friendshipPoints.getOrDefault(key, 0);
        points += delta;
        points = Math.max(0, Math.min(points, 999));
        friendshipPoints.put(key, points);
        lastInteractionDates.put(key, today);
    }

    private int getLevelsByPoint(int points) {
        return Math.max(0, points / 200);
    }

    public void notInteracted(){
        Set<String> updatedToday = new HashSet<>(lastInteractionDates.keySet());
        LocalDate today = LocalDate.now();
        for (String key : friendshipPoints.keySet()) {
            if(updatedToday.contains(key) && lastInteractionDates.get(key).isEqual(today)) continue;
            int points = friendshipPoints.get(key);
            if(points > 0) {
                points = Math.max(0, points - 10);
                friendshipPoints.put(key, points);
            } else{
                int level = getLevelsByPoint(points);
                if(level > 0) {
                    int newLevel = level - 1;
                    int newXp = (newLevel + 1) * 100 - 10;
                    friendshipPoints.put(key, newXp);
                }
            }
        }
        lastInteractionDates.clear();
    }
    private String key(String user1, String user2) {
        List<String> pair = List.of(user1, user2);
        pair.sort(Comparator.naturalOrder());
        return pair.get(0) + ":" + pair.get(1);
    }
    public Map<String, Integer> getAllFriendships (String user){
        Map<String, Integer> result = new HashMap<>();
        for(Map.Entry<String, Integer> entry : friendshipPoints.entrySet()){
            String[] users = entry.getKey().split(":");
            if(users[0].equals(user)){
                result.put(users[1], entry.getValue());
            } else if(users[1].equals(user)){
                result.put(users[0], entry.getValue());
            }
        }
        return result;
    }

    private void handleTalk(String command, MenuController controller) {
        String[] parts =  command.split("\\s+");
        if(parts.length < 4){
            System.out.println("invalid command format");
        }
        String receiver = parts[1];
        String message = parts[3];
        User receiverUser = um.getUser(receiver);
        if(receiverUser == null){
            System.out.println("user not found");
        }
//        if(!controller.getCurrentSession().areNextToEachOther(controller.getCurrentUser().getUsername(),
//                receiver)){
//            System.out.println("You should be next to the user to talk");
//        }
        addPoint(controller.getCurrentUser().getUsername(), receiver, 20);
        System.out.println("massage sent");
        System.out.println(message);
    }

    public void handleSendGift(String command, MenuController controller) {
        String[] parts = command.split("\\s+");
        String item = null;
        int amount = 1;
        String receiver = null;
        User sender = controller.getCurrentUser();
        for(int i = 0; i < parts.length - 1; i++) {
            if(parts[i].equals("-u")) {receiver = parts[i + 1];}
            if(parts[i].equals("-i")) {item = parts[i + 1];}
            if(parts[i].equals("-a")) {
                try {
                    amount = Integer.parseInt(parts[i + 1]);
                } catch (NumberFormatException e) {
                    System.out.println("invalid amount");
                    return;
                }
            }
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
//        if(!controller.getCurrentSession().areNextToEachOther(sender.getUsername(), receiver)) {
//            System.out.println("You should be next to the gift receiver");
//            return;
//        }
        int level = getLevel(sender.getUsername(), receiver);
        if (level < 1) System.out.println("Friendship level too low");
        if (sender.getInventoryCount(item) < amount) {
            System.out.println("Not enough items");
        }

        sender.addItem(item, -amount);
        receiverUser.addItem(item, amount);
        controller.recordGift(sender.getUsername(), receiver, item, amount);
        System.out.println("Gift sent!");
    }

    private void handleGiftList(MenuController controller) {
        String user = controller.getCurrentUser().getUsername();
        List<GiftLogEntry> logs = controller.getGiftLogs();
        System.out.println("Gift list: ");
        boolean found = false;
        for(GiftLogEntry log : logs) {
            if(log.receiver.equals(user)){
                System.out.printf("number %d | from %s to %s | %s*%d | %s %s\n", log.getId(), log.sender, log.receiver,
                        log.item, log.amount, log.timestamp.toLocalDate(), log.isRated() ? "| rating :" + log.getRating() : "");
                found = true;
            }
        }
        if(!found){
            System.out.println("No gift found");
        }
    }

    private void handleGiftHistory(String command, MenuController controller) {
        String[] parts =  command.split("\\s+");
        if(parts.length != 3 || !parts[1].equals("-u")) {
            System.out.println("invalid command format");
        }
        String target = parts[2];
        System.out.println("gift history: ");
        List<GiftLogEntry> logs = controller.getGiftLogs();
        boolean found = false;
        for(GiftLogEntry log : logs) {
            if(log.sender.equals(target) || log.receiver.equals(target)){
                System.out.printf("number %d | from %s to %s | %s*%d | %s %s\n", log.getId(), log.sender, log.receiver,
                        log.item, log.amount, log.timestamp.toLocalDate(), log.isRated() ? "| rating :" + log.getRating() : "");
                found = true;
            }
        }
        if(!found){
            System.out.println("No gift found");
        }
    }

    private void handleGiftRate(String command, MenuController controller) {
        String[] parts =  command.split("\\s+");
        if(parts.length != 5 || parts[1].equals("-i") || parts[3].equals("-r")) {
            System.out.println("invalid command format");
            return;
        }
        int id, rate;
        try{
            id = Integer.parseInt(parts[2]);
            rate = Integer.parseInt(parts[4]);
        } catch(NumberFormatException e){
            System.out.println("invalid numeric input");
            return;
        }
        if(rate < 1 || rate > 5){
            System.out.println("rating must be between 1 and 5");
            return;
        }
        String user = controller.getCurrentUser().getUsername();
        Optional<GiftLogEntry>  optional = controller.getGiftById(id, user);
        if(optional.isEmpty()){
            System.out.println("gift not found with this id");
        }
        GiftLogEntry log = optional.get();
        if(log.receiver.equals(user)){
            System.out.println("You only can rate gifts that you receive");
            return;
        }
        if(log.isRated()){
            System.out.println("gift is rated before");
            return;
        }
        log.rate(rate);
        int delta = (rate - 3) * 30 + 15;
        addPoint(log.sender, user, delta);
        System.out.println("rating submitted friendshipXP increased" + delta);
    }
}
