package com.expensetracker.controller;

import com.expensetracker.model.Transaction;
import com.expensetracker.service.ExpenseService;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class MainController {

    @FXML private TextField amountField;
    @FXML private TextField categoryField;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> typeCombo;

    @FXML private TextField searchCategoryField;
    @FXML private ComboBox<String> searchTypeCombo;
    @FXML private DatePicker searchDatePicker;

    @FXML private TableView<Transaction> transactionTable;
    @FXML private TableColumn<Transaction, Integer> colId;
    @FXML private TableColumn<Transaction, String> colType;
    @FXML private TableColumn<Transaction, Double> colAmount;
    @FXML private TableColumn<Transaction, String> colCategory;
    @FXML private TableColumn<Transaction, String> colDate;

    @FXML private Label balanceLabel;
    @FXML private Label totalIncomeLabel;
    @FXML private Label totalExpenseLabel;
    @FXML private VBox summaryBox;

    private ExpenseService expenseService;
    private Transaction selectedTransaction;

    @FXML
    public void initialize() {
        expenseService = new ExpenseService();

        typeCombo.setItems(FXCollections.observableArrayList("EXPENSE", "INCOME"));
        typeCombo.getSelectionModel().selectFirst();

        searchTypeCombo.setItems(FXCollections.observableArrayList("ALL", "EXPENSE", "INCOME"));
        searchTypeCombo.getSelectionModel().selectFirst();

        colId.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getId()).asObject());
        colType.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getType()));
        colAmount.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getAmount()).asObject());
        colCategory.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCategory()));
        colDate.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getDate() != null ? data.getValue().getDate().toString() : ""));

        colType.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if ("INCOME".equals(item)) {
                        setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    }
                }
            }
        });

        colAmount.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f", item));
                }
            }
        });

        transactionTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedTransaction = newVal;
                populateForm(newVal);
            }
        });

        datePicker.setValue(LocalDate.now());
        refreshTable();
        refreshBalance();
        refreshSummary();
    }

    @FXML
    public void handleAddExpense() {
        String amountText = amountField.getText().trim();
        String category = categoryField.getText().trim();
        LocalDate date = datePicker.getValue();

        if (amountText.isEmpty() || category.isEmpty() || date == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill in all fields.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);
            if (amount <= 0) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Amount must be greater than zero.");
                return;
            }
            expenseService.addExpense(amount, category, date, 1);
            clearForm();
            refreshTable();
            refreshBalance();
            refreshSummary();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Expense added successfully.");
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Please enter a valid numeric amount.");
        }
    }

    @FXML
    public void handleAddIncome() {
        String amountText = amountField.getText().trim();
        String category = categoryField.getText().trim();
        LocalDate date = datePicker.getValue();

        if (amountText.isEmpty() || category.isEmpty() || date == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill in all fields.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);
            if (amount <= 0) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Amount must be greater than zero.");
                return;
            }
            expenseService.addIncome(amount, category, date, 1);
            clearForm();
            refreshTable();
            refreshBalance();
            refreshSummary();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Income added successfully.");
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Please enter a valid numeric amount.");
        }
    }

    @FXML
    public void handleUpdate() {
        if (selectedTransaction == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a transaction to update.");
            return;
        }

        String amountText = amountField.getText().trim();
        String category = categoryField.getText().trim();
        LocalDate date = datePicker.getValue();
        String type = typeCombo.getValue();

        if (amountText.isEmpty() || category.isEmpty() || date == null || type == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill in all fields.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);
            if (amount <= 0) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Amount must be greater than zero.");
                return;
            }
            selectedTransaction.setAmount(amount);
            selectedTransaction.setCategory(category);
            selectedTransaction.setDate(date);
            selectedTransaction.setType(type);
            expenseService.updateTransaction(selectedTransaction);
            clearForm();
            selectedTransaction = null;
            refreshTable();
            refreshBalance();
            refreshSummary();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Transaction updated successfully.");
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Please enter a valid numeric amount.");
        }
    }

    @FXML
    public void handleDelete() {
        if (selectedTransaction == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a transaction to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to delete this transaction?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                expenseService.deleteTransaction(selectedTransaction.getId());
                clearForm();
                selectedTransaction = null;
                refreshTable();
                refreshBalance();
                refreshSummary();
                showAlert(Alert.AlertType.INFORMATION, "Deleted", "Transaction deleted successfully.");
            }
        });
    }

    @FXML
    public void handleSearch() {
        String type = searchTypeCombo.getValue();
        String category = searchCategoryField.getText().trim();
        String date = searchDatePicker.getValue() != null ? searchDatePicker.getValue().toString() : "";

        List<Transaction> results = expenseService.searchTransactions(type, category, date);
        ObservableList<Transaction> data = FXCollections.observableArrayList(results);
        transactionTable.setItems(data);
    }

    @FXML
    public void handleClearSearch() {
        searchCategoryField.clear();
        searchTypeCombo.getSelectionModel().selectFirst();
        searchDatePicker.setValue(null);
        refreshTable();
    }

    private void refreshTable() {
        List<Transaction> all = expenseService.getAllTransactions();
        ObservableList<Transaction> data = FXCollections.observableArrayList(all);
        transactionTable.setItems(data);
    }

    private void refreshBalance() {
        double balance = expenseService.getCurrentBalance();
        double income = expenseService.getTotalIncome();
        double expenses = expenseService.getTotalExpenses();

        balanceLabel.setText(String.format("Balance: ₹ %.2f", balance));
        totalIncomeLabel.setText(String.format("Total Income: ₹ %.2f", income));
        totalExpenseLabel.setText(String.format("Total Expenses: ₹ %.2f", expenses));

        if (balance >= 0) {
            balanceLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 16px; -fx-font-weight: bold;");
        } else {
            balanceLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 16px; -fx-font-weight: bold;");
        }
    }

    private void refreshSummary() {
        summaryBox.getChildren().clear();
        Map<String, Double> summary = expenseService.getCategoryWiseSummary();
        if (summary.isEmpty()) {
            Label empty = new Label("No expense data available.");
            empty.setStyle("-fx-text-fill: #888888;");
            summaryBox.getChildren().add(empty);
            return;
        }
        summary.forEach((category, total) -> {
            Label entry = new Label(String.format("%-20s  ₹ %.2f", category, total));
            entry.setStyle("-fx-font-family: 'Monospaced'; -fx-font-size: 13px;");
            summaryBox.getChildren().add(entry);
        });
    }

    private void populateForm(Transaction t) {
        amountField.setText(String.valueOf(t.getAmount()));
        categoryField.setText(t.getCategory());
        datePicker.setValue(t.getDate());
        typeCombo.setValue(t.getType());
    }

    private void clearForm() {
        amountField.clear();
        categoryField.clear();
        datePicker.setValue(LocalDate.now());
        typeCombo.getSelectionModel().selectFirst();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
