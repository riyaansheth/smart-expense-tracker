package com.expensetracker.dao;

import com.expensetracker.model.Transaction;

import java.util.List;
import java.util.Map;

public interface TransactionDAO {

    void addTransaction(Transaction transaction);

    List<Transaction> getAllTransactions();

    Transaction getTransactionById(int id);

    void updateTransaction(Transaction transaction);

    void deleteTransaction(int id);

    List<Transaction> searchTransactions(String type, String category, String date);

    Map<String, Double> getCategoryWiseSummary();
}
