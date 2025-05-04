package models;

public class TradeRequest {
    public enum Type{OFFER, REQUEST}
    public enum Status{PENDING,ACCEPTED,REJECTED}
    private String id;
    private String fromUser;
    private String toUser;
    private Type type;
    private Status status;
    private String item;
    private int amount;
    private double price;
    private String targetItem;
    private int targetAmount;

    public TradeRequest(String id, String fromUSer, String toUser, Type type, String item, int amount,
                        double price, String targetItem, int targetAmount) {
        this.id = id;
        this.fromUser = fromUSer;
        this.toUser = toUser;
        this.type = type;
        this.item = item;
        this.amount = amount;
        this.price = price;
        this.targetItem = targetItem;
        this.targetAmount = targetAmount;
        this.status = status.PENDING;
    }
    public void setStatus(Status status) {this.status = status;}

    public String getId() {return id;}
    public String getFromUser() {return fromUser;}
    public String getToUser() {return toUser;}
    public Type getType() {return type;}
    public Status getStatus() {return status;}
    public String getItem() {return item;}
    public int getAmount() {return amount;}
    public double getPrice() {return price;}
    public String getTargetItem() {return targetItem;}
    public int getTargetAmount() {return targetAmount;}
}
