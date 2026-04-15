package com.expensetracker.model;

public class Income extends Record {

    public Income(int id, double amount) {
        super(id, amount);
    }

    @Override
    public String getType() {
        return "INCOME";
    }
}
