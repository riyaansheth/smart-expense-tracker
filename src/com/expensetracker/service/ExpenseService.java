package com.expensetracker.service;

import com.expensetracker.dao.TransactionDAO;
import com.expensetracker.dao.TransactionDAOImpl;
import com.expensetracker.model.Transaction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExpenseService {

    private TransactionDAO transactionDAO;

    public ExpenseService() {
        this.transactionDAO = new TransactionDAOImpl();
    }

    public void addExpense(double amount, String category, LocalDate date, int userId) {
        Transaction transaction = new Transaction("EXPENSE", amount, category, date, userId);
        transactionDAO.addTransaction(transaction);
    }

    public void addIncome(double amount, String category, LocalDate date, int userId) {
        Transaction transaction = new Transaction("INCOME", amount, category, date, userId);
        transactionDAO.addTransaction(transaction);
    }

    public void addTransaction(Transaction transaction) {
        transactionDAO.addTransaction(transaction);
    }

    public List<Transaction> getAllTransactions() {
        return transactionDAO.getAllTransactions();
    }

    public Transaction getTransactionById(int id) {
        return transactionDAO.getTransactionById(id);
    }

    public void updateTransaction(Transaction transaction) {
        transactionDAO.updateTransaction(transaction);
    }

    public void deleteTransaction(int id) {
        transactionDAO.deleteTransaction(id);
    }

    public List<Transaction> searchTransactions(String type, String category, String date) {
        return transactionDAO.searchTransactions(type, category, date);
    }

    public double getCurrentBalance() {
        List<Transaction> all = transactionDAO.getAllTransactions();
        double balance = 0.0;
        for (Transaction t : all) {
            if ("INCOME".equals(t.getType())) {
                balance += t.getAmount();
            } else if ("EXPENSE".equals(t.getType())) {
                balance -= t.getAmount();
            }
        }
        return balance;
    }

    public Map<String, Double> getCategoryWiseSummary() {
        return transactionDAO.getCategoryWiseSummary();
    }

    public double getTotalIncome() {
        List<Transaction> all = transactionDAO.getAllTransactions();
        double total = 0.0;
        for (Transaction t : all) {
            if ("INCOME".equals(t.getType())) {
                total += t.getAmount();
            }
        }
        return total;
    }

    public double getTotalExpenses() {
        List<Transaction> all = transactionDAO.getAllTransactions();
        double total = 0.0;
        for (Transaction t : all) {
            if ("EXPENSE".equals(t.getType())) {
                total += t.getAmount();
            }
        }
        return total;
    }

    public List<Transaction> getExpenses() {
        List<Transaction> all = transactionDAO.getAllTransactions();
        List<Transaction> expenses = new ArrayList<>();
        for (Transaction t : all) {
            if ("EXPENSE".equals(t.getType())) {
                expenses.add(t);
            }
        }
        return expenses;
    }

    public List<Transaction> getIncomes() {
        List<Transaction> all = transactionDAO.getAllTransactions();
        List<Transaction> incomes = new ArrayList<>();
        for (Transaction t : all) {
            if ("INCOME".equals(t.getType())) {
                incomes.add(t);
            }
        }
        return incomes;
    }
}
