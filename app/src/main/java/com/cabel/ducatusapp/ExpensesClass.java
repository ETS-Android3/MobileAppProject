package com.cabel.ducatusapp;

public class ExpensesClass {
    private int expenseID;
    private String category;
    private String description;
    private float amount;
    private String date;
    private int userID;
    private int itemID;

    public ExpensesClass() {

    }

    public ExpensesClass(int expenseID, String category, String description, float amount, String date, int userID, int itemID) {
        this.expenseID = expenseID;
        this.category = category;
        this.description = description;
        this.amount = amount;
        this.date = date;
        this.userID = userID;
        this.itemID = itemID;
    }

    public int getExpenseID() {
        return expenseID;
    }

    public void setExpenseID(int expenseID) {
        this.expenseID = expenseID;
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

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
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

    public int getItemID() {
        return itemID;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }
}
