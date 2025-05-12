package models;

import models.Enums.NPCCharacters;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class NPC {
    private final NPCCharacters character;
    private int friendshipPoints;
    private LocalDate lastInteractionDate;
    private final Map<NPCCharacters.DialogueCondition, String> dialogues = new HashMap<>();
    private final List<NPCCharacters.Quest> activeQuests = new ArrayList<>();

    public NPC(NPCCharacters character) {
        this.character = character;
        initializeDialogues();
    }

    private void initializeDialogues() {
        dialogues.clear(); // Clear any default dialogues

        switch(character) {
            case SEBASTIAN:
                dialogues.put(NPCCharacters.DialogueCondition.DEFAULT, "برنامه نویسی میکنم...");
                dialogues.put(NPCCharacters.DialogueCondition.RAINY, "بارون برای کدنویسی عالیه");
                dialogues.put(NPCCharacters.DialogueCondition.FESTIVAL, "جشن امروز عالیه!");
                break;

            case ABIGAIL:
                dialogues.put(NPCCharacters.DialogueCondition.DEFAULT, "یه ماجراجویی جدید داریم؟");
                dialogues.put(NPCCharacters.DialogueCondition.MORNING, "صبحانه چی خوردی؟");
                break;

            case HARVEY:
                dialogues.put(NPCCharacters.DialogueCondition.DEFAULT, "امروز چطوری احساس میکنی؟");
                dialogues.put(NPCCharacters.DialogueCondition.AFTERNOON, "ناهار خوردی؟");
                break;

            default:
                dialogues.put(NPCCharacters.DialogueCondition.DEFAULT, "سلام! چطوری؟");
                break;
        }
    }

    // New helper methods
    private boolean isFirstInteractionToday() {
        return lastInteractionDate == null ||
                lastInteractionDate.isBefore(LocalDate.now());
    }

    private void updateLastInteraction() {
        lastInteractionDate = LocalDate.now();
    }

    private String getDialogue() {
        if (isRaining()) return dialogues.get(NPCCharacters.DialogueCondition.RAINY);
        if (isMorning()) return dialogues.get(NPCCharacters.DialogueCondition.MORNING);
        return dialogues.get(NPCCharacters.DialogueCondition.DEFAULT);
    }

    private boolean isRaining() {
        // Implement your weather system check here
        return false;
    }

    private boolean isMorning() {
        return LocalTime.now().isBefore(LocalTime.NOON);
    }

//    public String interact(Player player) {
//        if (isFirstInteractionToday()) {
//            friendshipPoints += 20;
//            updateLastInteraction();
//        }
//        return getDialogue();
//    }

    // Getters and setters
    public int getFriendshipPoints() {
        return friendshipPoints;
    }

    public int getFriendshipLevel() {
        return friendshipPoints / 200;
    }

    public List<NPCCharacters.Quest> getActiveQuests() {
        return activeQuests;
    }

    public void addQuest(NPCCharacters.Quest quest) {
        activeQuests.add(quest);
    }
}