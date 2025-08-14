package com.proj.Control;

import com.proj.Database.DatabaseHelper;
import com.proj.Model.ScoreboardEntry;
import java.util.*;

public class ScoreboardController {
    private final DatabaseHelper dbHelper;
    private List<ScoreboardEntry> entries = new ArrayList<>();

    public ScoreboardController(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public void loadScoreboard() {
        String[] usernames = {
            "fati", "arm", "at", "ta", "ty", "yt", "name", "nam", "us", "me",
            "job", "use", "ak", "ka", "ee", "er", "ga", "ha", "PR", "JJ",
            "s2", "s1", "s3", "KK", "PK", "FB", "KV", "JH", "MN", "FQ",
            "RTU", "fatemeB", "sara", "OOO", "arm2", "mi2", "", "HH", "H", "aa"
        };

        entries = new ArrayList<>();
        Random rand = new Random();

        for (String username : usernames) {
            int money = rand.nextInt(10000);
            int quests = rand.nextInt(20);
            int skillLevel = rand.nextInt(10) + 1;
            entries.add(new ScoreboardEntry(username, money, quests, skillLevel));
        }

        Collections.sort(entries, (e1, e2) -> e2.getMoney() - e1.getMoney());
    }

    public List<ScoreboardEntry> getScoreboardEntries() {
        return Collections.unmodifiableList(entries);
    }
}
