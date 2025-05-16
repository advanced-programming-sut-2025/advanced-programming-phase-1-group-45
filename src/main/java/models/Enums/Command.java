package models.Enums;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum Command {
    TIME("^\\s*time\\s*$"),
    DATE("^date\\s*$"),
    Weather("^\\s*weather\\s*$"),
    dateTime("^datetime\\s*$"),
    DayOfWeek("^\\s*day\\s+of\\s+the\\s+week\\s*$"),
    CheatTime("^\\s*cheat\\s+advance\\s+time\\s+(?<hour>\\d+)h\\s*$"),
    CheatDate("^\\s*cheat\\s+advance\\s+date\\s+(?<day>\\d+)d\\s*$"),
    Season("^\\s*season\\s*$"),
    weather("^\\s*weather\\s*$"),
    WeatherForecast("^\\s*weather\\s+forecast\\s*$"),
    CheatWeather("^\\s*cheat\\s+weather\\s+set\\s+(?<weather>\\S+)\\s*$"),
    ToolEquip("^\\s*tool\\s+equip\\s+(?<toolName>\\S+)\\s*$"),
    toolShowCurrent("^\\s*tools\\s+show\\s+current\\s*$"),
    toolsShow("^\\s*tools\\s+show\\s+available\\s*$"),
    toolsUpgrade("^\\s*tools\\s+upgrade\\s+(?<toolName>\\S+)\\s*$"),
    toolUse("^\\s*tools\\s+use\\s+-d\\s+(?<direction>\\S+)\\s*$");

    private final String pattern;

    Command(String pattern) {
        this.pattern = pattern;
    }

    public Matcher getMatcher(String input) {
        java.util.regex.Matcher matcher = Pattern.compile(this.pattern).matcher(input);
        if (matcher.matches()) {
            return matcher;
        }
        return null;
    }
}
