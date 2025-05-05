package managers;

import java.nio.file.*;
import java.util.*;
import java.io.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import models.TradeRequest;
import models.User;
import managers.UserManager;

public class TradeManager {
    private final Map<String, List<TradeRequest>> trades = new HashMap<>();
    private final Path storage = Paths.get("trades.json");
    private final Gson gson = new Gson();
    private UserManager um = new UserManager();

    public TradeManager(UserManager um) {
        this.um = um;
        load();
    }

    public String creatTrades(String fromUser, String toUser, String typeStr, String amountStr, String priceStr,
                              String item, String targetItem, String targetAmountStr) {
        User sender = um.getUser(fromUser);
        User receiver = um.getUser(toUser);
        if(receiver == null) return "targetUser " + toUser + " not found!";
        TradeRequest.Type type;
        try{
            type = TradeRequest.Type.valueOf(typeStr);
        } catch(Exception e) {return "invalid trading type";}

        int amount;
        try{amount = Integer.parseInt(amountStr);}
        catch(NumberFormatException e) {return "invalid amount format";}
        if (amount <= 0) return "amount must be greater than 0";

        Double price = null;
        Integer targetAmount = null;
        if(type == TradeRequest.Type.OFFER){
            if (priceStr == null ) return "You should specify price for offer trading.";
            try{price = Double.parseDouble(priceStr);}
            catch(NumberFormatException e) {return "invalid price format";}
            if( price < 0) return "price cannot be negative.";
            if (item.equals("money")){
                if (sender.getMoney() < amount * price){
                    return "You do not have enough money for trading.";
                }  else {
                    if (sender.getInventoryCount(item) < amount){
                        return "Insufficient inventory for trading.";
                    }
                }
            }
        } else {
            if (targetAmountStr == null || targetItem == null)
                return "You should specify targetAmount and targetItem for trading.";
            try{targetAmount = Integer.parseInt(targetAmountStr);}
            catch(NumberFormatException e) {return "invalid amount format";}
            if (targetAmount <= 0) return "amount must be greater than 0";
            if (targetItem.equals("money")){
                if (sender.getMoney() < amount){
                    return "You do not have enough money to exchange.";
                } else{
                    if (sender.getInventoryCount(item) < amount) {
                        return "Insufficient inventory for trading.";
                    }
                }
            }

        }
        String id = UUID.randomUUID().toString();
        TradeRequest request = new TradeRequest(id, fromUser, toUser, type, item, amount, price, targetItem,
                targetAmount);
        trades.computeIfAbsent(toUser, k -> new ArrayList<>()).add(request);
        save();
        return "Your request has been sent with id:" + id + ".";
    }

    public List<TradeRequest> getPendingTradesList(String username) {
        return trades.getOrDefault(username, Collections.emptyList());
    }

    public String respond(String username, String id, boolean accept){
        List<TradeRequest> list = trades.get(username);
        if (list == null)return "You do not have any trades.";
        for (TradeRequest t : list) {
            if (t.getId().equals(id) && t.getStatus() == TradeRequest.Status.PENDING){
                if (accept){
                    User sender = um.getUser(t.getFromUser());
                    User receiver = um.getUser(t.getToUser());
                    if (t.getType() == TradeRequest.Type.OFFER){
                        double total = t.getAmount() * t.getPrice();
                        if (receiver.getMoney() < total)
                            return " customer does not have enough money.";
                        receiver.addMoney(-total);
                        sender.addMoney(total);
                        sender.addItem(t.getItem(), -t.getAmount());
                        receiver.addItem(t.getItem(), t.getAmount());
                    } else{
                        if (receiver.getInventoryCount(t.getItem()) < t.getAmount())
                            return "Insufficient inventory for trading.";
                        if (t.getTargetItem().equals("money")){
                            double amount = t.getTargetAmount();
                            if(sender.getMoney() < amount)
                                return "You do not have enough money to exchange.";
                            sender.addItem(t.getItem(), t.getAmount());
                            receiver.addItem(t.getItem(), -t.getAmount());
                            sender.addMoney(-amount);
                            receiver.addMoney(amount);
                        } else {
                            if(sender.getInventoryCount(t.getItem()) < t.getAmount())
                                return "Insufficient inventory for exchanging.";
                            sender.addItem(t.getItem(), t.getAmount());
                            receiver.addItem(t.getItem(), -t.getAmount());
                            sender.addItem(t.getTargetItem(), -t.getTargetAmount());
                            receiver.addItem(t.getTargetItem(), t.getTargetAmount());
                        }
                    }
                    t.setStatus(TradeRequest.Status.ACCEPTED);
                    save();
                    return "Request accepted successfully.";
                } else {
                    t.setStatus(TradeRequest.Status.REJECTED);
                    save();
                    return "Request rejected.";
                }
            }
        }
        return "request not found or accepted before.";
    }

    public List<TradeRequest> history(String username) {
        List<TradeRequest> all = new ArrayList<>();
        for (List<TradeRequest> list : trades.values()) {
            for (TradeRequest tr : list) {
                if (tr.getFromUser().equals(username) || tr.getToUser().equals(username)) {
                    all.add(tr);
                }
            }
        }
        return all;
    }

    private void load() {
        try {
            if (Files.exists(storage)) {
                var type = new TypeToken<Map<String,List<TradeRequest>>>() {}.getType();
                Map<String, List<TradeRequest>> t = gson.fromJson(Files.readString(storage), type);
                if (t != null) trades.putAll(t);
            }
        } catch(IOException ignored) {}
    }

    private void save() {
        try (Writer w = Files.newBufferedWriter(storage)) {
            gson.toJson(new ArrayList<>(trades.values()), w);
        } catch(IOException ignored) {}
    }

}
