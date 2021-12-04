package com.cabel.ducatusapp;

public class BudgetItemClass {
    private int itemID;
    private String category;
    private String description;
    private float budget;
    private float activity;
    private float available;
    private String date;
    private int userID;

    public BudgetItemClass() {

    }

    public BudgetItemClass(int itemID, String category, String description, float budget, float activity, float available, String date, int userID) {
        this.itemID = itemID;
        this.category = category;
        this.description = description;
        this.budget = budget;
        this.activity = activity;
        this.available = available;
        this.date = date;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getBudget() {
        return budget;
    }

    public void setBudget(float budget) {
        this.budget = budget;
    }

    public float getActivity() {
        return activity;
    }

    public void setActivity(float activity) {
        this.activity = activity;
    }

    public float getAvailable() {
        return available;
    }

    public void setAvailable(float available) {
        this.available = available;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

}
