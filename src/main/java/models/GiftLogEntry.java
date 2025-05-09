package models;

import java.time.LocalDateTime;

public class GiftLogEntry {
    public String sender;
    public String receiver;
    public String item;
    public int amount;
    public LocalDateTime timestamp;
    private Integer rating = null;
    private int id;
    public GiftLogEntry(int id, String sender, String receiver, String item, int amount) {
        this.sender = sender;
        this.receiver = receiver;
        this.item = item;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public boolean isRated() {
        return rating != null;
    }

    public void rate(int rating) {
        this.rating = rating;
    }

    public int getRating(){
        return rating != null ? rating : 0;
    }
}
