package com.cabel.ducatusapp;

public class BudgetItemClass {
    private int itemID;
    private String category;
    private int budget;
    private int activity;
    private int available;
    private String description  ;
    private int userID;

    public BudgetItemClass() {

    }

    public BudgetItemClass(int itemID, String category, int budget, int activity, int available, String description, int userID) {
        this.itemID = itemID;
        this.category = category;
        this.budget = budget;
        this.activity = activity;
        this.available = available;
        this.description = description;
        this.userID = userID;
    }

    public int getItemID() {
        return itemID;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getBudget() {
        return budget;
    }

    public void setBudget(int budget) {
        this.budget = budget;
    }

    public int getActivity() {
        return activity;
    }

    public void setActivity(int activity) {
        this.activity = activity;
    }

    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

}
