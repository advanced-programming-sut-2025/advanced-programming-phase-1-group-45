package models;
public class Weather {
    private static models.Enums.Weather currentWeather;
    public static models.Enums.Weather showWeather(){
        return currentWeather;
    }
    public static void updateWeather(){}
}
