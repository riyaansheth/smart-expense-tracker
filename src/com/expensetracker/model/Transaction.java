package com.expensetracker.model;

import java.time.LocalDate;

public class Transaction {

    private int id;
    private String type;
    private double amount;
    private String category;
    private LocalDate date;
    private int userId;

    public Transaction() {
    }

    public Transaction(int id, String type, double amount, String category, LocalDate date, int userId) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.userId = userId;
    }

    public Transaction(String type, double amount, String category, LocalDate date, int userId) {
        this.type = type;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Transaction{id=" + id + ", type=" + type + ", amount=" + amount
                + ", category=" + category + ", date=" + date + ", userId=" + userId + "}";
    }
}
