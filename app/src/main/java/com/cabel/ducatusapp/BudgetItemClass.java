package com.cabel.ducatusapp;

public class BudgetItemClass {
    private int itemID;
    private String itemName;
    private int budgetAmount;
    private int budgetMoneySpent;
    private int budgetAvailableMoney;
    private String budgetDescription;
    private int userID;

    public BudgetItemClass() {

    }

    public BudgetItemClass(int itemID, String itemName, int budgetAmount, int budgetMoneySpent, int budgetAvailableMoney, String budgetDescription, int userID) {
        this.itemID = itemID;
        this.itemName = itemName;
        this.budgetAmount = budgetAmount;
        this.budgetMoneySpent = budgetMoneySpent;
        this.budgetAvailableMoney = budgetAvailableMoney;
        this.budgetDescription = budgetDescription;
        this.userID = userID;
    }

    public int getItemID() {
        return itemID;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getBudgetAmount() {
        return budgetAmount;
    }

    public void setBudgetAmount(int budgetAmount) {
        this.budgetAmount = budgetAmount;
    }

    public int getBudgetMoneySpent() {
        return budgetMoneySpent;
    }

    public void setBudgetMoneySpent(int budgetMoneySpent) {
        this.budgetMoneySpent = budgetMoneySpent;
    }

    public int getBudgetAvailableMoney() {
        return budgetAvailableMoney;
    }

    public void setBudgetAvailableMoney(int budgetAvailableMoney) {
        this.budgetAvailableMoney = budgetAvailableMoney;
    }

    public String getBudgetDescription() {
        return budgetDescription;
    }

    public void setBudgetDescription(String budgetDescription) {
        this.budgetDescription = budgetDescription;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

}
