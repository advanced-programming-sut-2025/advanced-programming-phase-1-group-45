package models;

public class ArtisanMachine {

    public String name;
    public String inputItem;
    public String outputItem;
    public int remainingTime;
    public boolean isProcessing;

    public ArtisanMachine(String name) {
        this.name = name;
        this.inputItem = null;
        this.outputItem = null;
        this.remainingTime = 0;
        this.isProcessing = false;
    }

    public boolean startProcessing(String inputItem) {
        if (isProcessing) {
            return false;
        }


        String output = getOutputForInput(inputItem);
        if (output == null) {
            return false;
        }

        this.inputItem = inputItem;
        this.outputItem = output;
        this.remainingTime = getProcessingTime();
        this.isProcessing = true;

        return true;
    }


    public String getProduct() {
        if (isProcessing && remainingTime <= 0) {
            String product = outputItem;


            inputItem = null;
            outputItem = null;
            remainingTime = 0;
            isProcessing = false;

            return product;
        }

        return null;
    }


    public void updateTime(int hours) {
        if (isProcessing && remainingTime > 0) {
            remainingTime -= hours;
            if (remainingTime < 0) {
                remainingTime = 0;
            }
        }
    }


    public boolean isProductReady() {
        return isProcessing && remainingTime <= 0;
    }


    private String getOutputForInput(String inputItem) {
        switch (name) {
            case "Keg":
                if (inputItem.equals("Wheat")) return "Beer";
                if (inputItem.equals("Hops")) return "Pale Ale";
                if (inputItem.equals("Fruit")) return "Wine";
                if (inputItem.equals("Vegetable")) return "Juice";
                break;

            case "Preserves Jar":
                if (inputItem.equals("Fruit")) return "Jelly";
                if (inputItem.equals("Vegetable")) return "Pickles";
                break;

            case "Cheese Press":
                if (inputItem.equals("Milk")) return "Cheese";
                if (inputItem.equals("Goat Milk")) return "Goat Cheese";
                break;

            case "Mayonnaise Machine":
                if (inputItem.equals("Egg")) return "Mayonnaise";
                if (inputItem.equals("Duck Egg")) return "Duck Mayonnaise";
                break;

            case "Loom":
                if (inputItem.equals("Wool")) return "Cloth";
                break;

            case "Oil Maker":
                if (inputItem.equals("Truffle")) return "Truffle Oil";
                if (inputItem.equals("Sunflower")) return "Oil";
                if (inputItem.equals("Corn")) return "Oil";
                break;

            case "Furnace":
                if (inputItem.equals("Copper Ore")) return "Copper Bar";
                if (inputItem.equals("Iron Ore")) return "Iron Bar";
                if (inputItem.equals("Gold Ore")) return "Gold Bar";
                if (inputItem.equals("Iridium Ore")) return "Iridium Bar";
                break;

            case "Fish Smoker":
                if (inputItem.contains("Salmon") || inputItem.contains("Trout")) {
                    return "Smoked " + inputItem;
                }
                break;
        }

        return null;
    }


    private int getProcessingTime() {
        switch (name) {
            case "Keg": return 36;
            case "Preserves Jar": return 24;
            case "Cheese Press": return 6;
            case "Mayonnaise Machine": return 3;
            case "Loom": return 4;
            case "Oil Maker": return 6;
            case "Furnace": return 2;
            case "Fish Smoker": return 4;
            default: return 12;
        }
    }


    public String getName() {
        return name;
    }

    public String getInputItem() {
        return inputItem;
    }

    public String getOutputItem() {
        return outputItem;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public boolean isProcessing() {
        return isProcessing;
    }


    public String getStatus() {
        if (!isProcessing) {
            return name + ": Idle";
        } else if (remainingTime > 0) {
            return name + ": Processing " + inputItem + " into " + outputItem + " (" + remainingTime + "h remaining)";
        } else {
            return name + ": Ready to harvest " + outputItem;
        }
    }
}
