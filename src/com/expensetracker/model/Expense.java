package com.expensetracker.model;

public class Expense extends Record {

    public Expense(int id, double amount) {
        super(id, amount);
    }

    @Override
    public String getType() {
        return "EXPENSE";
    }
}
