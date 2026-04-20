package com.expensetracker.controller;

import com.expensetracker.export.CsvExporter;
import com.expensetracker.model.Transaction;
import com.expensetracker.service.ExpenseService;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainController {

    @FXML private BorderPane rootPane;

    @FXML private TextField amountField;
    @FXML private TextField categoryField;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> typeCombo;
    @FXML private TextField notesField;

    @FXML private TextField searchCategoryField;
    @FXML private ComboBox<String> searchTypeCombo;
    @FXML private DatePicker fromDatePicker;
    @FXML private DatePicker toDatePicker;

    @FXML private TableView<Transaction> transactionTable;
    @FXML private TableColumn<Transaction, Integer> colId;
    @FXML private TableColumn<Transaction, String> colType;
    @FXML private TableColumn<Transaction, Double> colAmount;
    @FXML private TableColumn<Transaction, String> colCategory;
    @FXML private TableColumn<Transaction, String> colDate;
    @FXML private TableColumn<Transaction, String> colNotes;

    @FXML private Label balanceLabel;
    @FXML private Label totalIncomeLabel;
    @FXML private Label totalExpenseLabel;
    @FXML private Label countLabel;

    @FXML private VBox summaryBox;

    @FXML private HBox topBar;
    @FXML private VBox leftPanel;
    @FXML private VBox centerContainer;
    @FXML private VBox searchBar;
    @FXML private VBox rightPanel;

    @FXML private ComboBox<String> monthCombo;
    @FXML private ComboBox<Integer> yearCombo;
    @FXML private Label monthlyIncomeLabel;
    @FXML private Label monthlyExpenseLabel;
    @FXML private Label monthlyBalanceLabel;

    @FXML private Button darkModeButton;

    private ExpenseService expenseService;
    private CsvExporter csvExporter;
    private Transaction selectedTransaction;
    private boolean darkMode = false;

    private static final String LIGHT_STYLE = "-fx-background-color: #f0f2f5;";
    private static final String DARK_STYLE  = "-fx-background-color: #1a1a2e;";

    @FXML
    public void initialize() {
        expenseService = new ExpenseService();
        csvExporter = new CsvExporter();

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
        colNotes.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNotes()));

        colType.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                setStyle("INCOME".equals(item)
                        ? "-fx-text-fill: #27ae60; -fx-font-weight: bold;"
                        : "-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            }
        });

        colAmount.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("%.2f", item));
            }
        });

        colId.setSortable(true);
        colAmount.setSortable(true);
        colDate.setSortable(true);
        colCategory.setSortable(true);
        colType.setSortable(true);
        transactionTable.getSortOrder().add(colDate);

        transactionTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedTransaction = newVal;
                populateForm(newVal);
            }
        });

        ObservableList<String> months = FXCollections.observableArrayList();
        for (Month m : Month.values()) {
            months.add(m.getDisplayName(TextStyle.FULL, Locale.ENGLISH));
        }
        monthCombo.setItems(months);
        monthCombo.getSelectionModel().select(LocalDate.now().getMonthValue() - 1);

        ObservableList<Integer> years = FXCollections.observableArrayList();
        int currentYear = LocalDate.now().getYear();
        for (int y = currentYear - 5; y <= currentYear + 1; y++) years.add(y);
        yearCombo.setItems(years);
        yearCombo.getSelectionModel().select(Integer.valueOf(currentYear));

        datePicker.setValue(LocalDate.now());
        refreshTable();
        refreshBalance();
        refreshSummary();
        refreshMonthly();
        applyThemeStyles();
    }

    @FXML
    public void handleAddExpense() {
        String amountText = amountField.getText().trim();
        String category = categoryField.getText().trim();
        LocalDate date = datePicker.getValue();
        String notes = notesField.getText().trim();

        if (amountText.isEmpty() || category.isEmpty() || date == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Amount, Category and Date are required.");
            return;
        }
        try {
            double amount = Double.parseDouble(amountText);
            if (amount <= 0) { showAlert(Alert.AlertType.WARNING, "Validation Error", "Amount must be greater than zero."); return; }
            expenseService.addExpense(amount, category, date, notes, 1);
            clearForm();
            refreshAll();
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
        String notes = notesField.getText().trim();

        if (amountText.isEmpty() || category.isEmpty() || date == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Amount, Category and Date are required.");
            return;
        }
        try {
            double amount = Double.parseDouble(amountText);
            if (amount <= 0) { showAlert(Alert.AlertType.WARNING, "Validation Error", "Amount must be greater than zero."); return; }
            expenseService.addIncome(amount, category, date, notes, 1);
            clearForm();
            refreshAll();
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
        String notes = notesField.getText().trim();

        if (amountText.isEmpty() || category.isEmpty() || date == null || type == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill in all required fields.");
            return;
        }
        try {
            double amount = Double.parseDouble(amountText);
            if (amount <= 0) { showAlert(Alert.AlertType.WARNING, "Validation Error", "Amount must be greater than zero."); return; }
            selectedTransaction.setAmount(amount);
            selectedTransaction.setCategory(category);
            selectedTransaction.setDate(date);
            selectedTransaction.setType(type);
            selectedTransaction.setNotes(notes);
            expenseService.updateTransaction(selectedTransaction);
            clearForm();
            selectedTransaction = null;
            refreshAll();
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
                refreshAll();
                showAlert(Alert.AlertType.INFORMATION, "Deleted", "Transaction deleted successfully.");
            }
        });
    }

    @FXML
    public void handleSearch() {
        String type = searchTypeCombo.getValue();
        String category = searchCategoryField.getText().trim();
        String from = fromDatePicker.getValue() != null ? fromDatePicker.getValue().toString() : "";
        String to = toDatePicker.getValue() != null ? toDatePicker.getValue().toString() : "";

        List<Transaction> results = expenseService.searchTransactionsWithRange(type, category, from, to);
        ObservableList<Transaction> data = FXCollections.observableArrayList(results);
        SortedList<Transaction> sorted = new SortedList<>(data);
        sorted.comparatorProperty().bind(transactionTable.comparatorProperty());
        transactionTable.setItems(sorted);
    }

    @FXML
    public void handleClearSearch() {
        searchCategoryField.clear();
        searchTypeCombo.getSelectionModel().selectFirst();
        fromDatePicker.setValue(null);
        toDatePicker.setValue(null);
        refreshTable();
    }

    @FXML
    public void handleExportCsv() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Transactions to CSV");
        fileChooser.setInitialFileName("transactions.csv");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showSaveDialog(rootPane.getScene().getWindow());
        if (file != null) {
            try {
                List<Transaction> all = expenseService.getAllTransactions();
                csvExporter.export(all, file.getAbsolutePath());
                showAlert(Alert.AlertType.INFORMATION, "Export Successful",
                        "Exported " + all.size() + " transactions to:\n" + file.getAbsolutePath());
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Export Failed", "Could not write file: " + e.getMessage());
            }
        }
    }

    @FXML
    public void handleRefreshMonthly() {
        refreshMonthly();
    }

    @FXML
    public void handleToggleDarkMode() {
        darkMode = !darkMode;
        darkModeButton.setText(darkMode ? "☀️  Light Mode" : "🌙  Dark Mode");
        applyThemeStyles();
    }

    private void applyThemeStyles() {
        rootPane.setStyle(darkMode ? DARK_STYLE : LIGHT_STYLE);
        topBar.setStyle("-fx-background-color: #1a1a2e; -fx-padding: 12 24 12 24;");
        leftPanel.setStyle(darkMode
                ? "-fx-background-color: #2b2e3b; -fx-border-color: #3a3f54; -fx-border-width: 0 1 0 0;"
                : "-fx-background-color: #ffffff; -fx-border-color: #dde1e7; -fx-border-width: 0 1 0 0;");
        centerContainer.setStyle(darkMode
                ? "-fx-background-color: #1f2430;"
                : "-fx-background-color: #f0f2f5;");
        searchBar.setStyle(darkMode
                ? "-fx-background-color: #2b2e3b; -fx-border-color: #3a3f54; -fx-border-width: 0 0 1 0; -fx-padding: 10 16 10 16;"
                : "-fx-background-color: #ffffff; -fx-border-color: #dde1e7; -fx-border-width: 0 0 1 0; -fx-padding: 10 16 10 16;");
        rightPanel.setStyle(darkMode
                ? "-fx-background-color: #2b2e3b; -fx-border-color: #3a3f54; -fx-border-width: 0 0 0 1;"
                : "-fx-background-color: #ffffff; -fx-border-color: #dde1e7; -fx-border-width: 0 0 0 1;");
        transactionTable.setStyle(darkMode
                ? "-fx-background-color: #2b2e3b; -fx-border-color: #3a3f54; -fx-border-radius: 6; -fx-background-radius: 6;"
                : "-fx-background-color: white; -fx-border-color: #dde1e7; -fx-border-radius: 6; -fx-background-radius: 6;");

        String fieldStyle = darkMode
                ? "-fx-background-color: #2a2d3e; -fx-text-fill: white; -fx-border-color: #3a3f54; -fx-border-radius: 4; -fx-background-radius: 4; -fx-padding: 6 10;"
                : "-fx-background-color: #f8f9fa; -fx-text-fill: black; -fx-border-color: #ced4da; -fx-border-radius: 4; -fx-background-radius: 4; -fx-padding: 6 10;";
        amountField.setStyle(fieldStyle);
        categoryField.setStyle(fieldStyle);
        notesField.setStyle(fieldStyle);
        searchCategoryField.setStyle(fieldStyle);
        typeCombo.setStyle(fieldStyle);
        searchTypeCombo.setStyle(fieldStyle);
        monthCombo.setStyle(fieldStyle);
        yearCombo.setStyle(fieldStyle);
        datePicker.setStyle(darkMode
                ? "-fx-background-color: #2a2d3e; -fx-text-fill: white;"
                : "");
        fromDatePicker.setStyle(darkMode
                ? "-fx-background-color: #2a2d3e; -fx-text-fill: white;"
                : "");
        toDatePicker.setStyle(darkMode
                ? "-fx-background-color: #2a2d3e; -fx-text-fill: white;"
                : "");
    }

    private void refreshTable() {
        List<Transaction> all = expenseService.getAllTransactions();
        ObservableList<Transaction> data = FXCollections.observableArrayList(all);
        SortedList<Transaction> sorted = new SortedList<>(data);
        sorted.comparatorProperty().bind(transactionTable.comparatorProperty());
        transactionTable.setItems(sorted);
    }

    private void refreshBalance() {
        double balance = expenseService.getCurrentBalance();
        double income = expenseService.getTotalIncome();
        double expenses = expenseService.getTotalExpenses();
        int total = expenseService.getTotalCount();
        int incomeCount = expenseService.getCountByType("INCOME");
        int expenseCount = expenseService.getCountByType("EXPENSE");

        balanceLabel.setText(String.format("Balance: ₹ %.2f", balance));
        totalIncomeLabel.setText(String.format("Income: ₹ %.2f (%d)", income, incomeCount));
        totalExpenseLabel.setText(String.format("Expenses: ₹ %.2f (%d)", expenses, expenseCount));
        countLabel.setText("Total Transactions: " + total);

        balanceLabel.setStyle(balance >= 0
                ? "-fx-text-fill: #27ae60; -fx-font-size: 16px; -fx-font-weight: bold;"
                : "-fx-text-fill: #e74c3c; -fx-font-size: 16px; -fx-font-weight: bold;");
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
        double grandTotal = summary.values().stream().mapToDouble(Double::doubleValue).sum();
        summary.forEach((category, total) -> {
            double pct = grandTotal > 0 ? (total / grandTotal) * 100 : 0;
            VBox entry = new VBox(2);
            Label nameLabel = new Label(category);
            nameLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
            Label amtLabel = new Label(String.format("₹ %.2f  (%.1f%%)", total, pct));
            amtLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #e74c3c;");
            ProgressBar bar = new ProgressBar(pct / 100);
            bar.setMaxWidth(Double.MAX_VALUE);
            bar.setStyle("-fx-accent: #e74c3c;");
            entry.getChildren().addAll(nameLabel, amtLabel, bar);
            summaryBox.getChildren().add(entry);
            summaryBox.getChildren().add(new Separator());
        });
    }

    private void refreshMonthly() {
        String monthName = monthCombo.getValue();
        Integer year = yearCombo.getValue();
        if (monthName == null || year == null) return;

        int month = Month.valueOf(monthName.toUpperCase()).getValue();
        Map<String, Double> data = expenseService.getMonthlySummary(year, month);

        double income = data.getOrDefault("INCOME", 0.0);
        double expense = data.getOrDefault("EXPENSE", 0.0);
        double balance = income - expense;

        monthlyIncomeLabel.setText(String.format("Income:   ₹ %.2f", income));
        monthlyExpenseLabel.setText(String.format("Expenses: ₹ %.2f", expense));
        monthlyBalanceLabel.setText(String.format("Net:      ₹ %.2f", balance));
        monthlyBalanceLabel.setStyle(balance >= 0
                ? "-fx-text-fill: #27ae60; -fx-font-weight: bold;"
                : "-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
    }

    private void refreshAll() {
        refreshTable();
        refreshBalance();
        refreshSummary();
        refreshMonthly();
    }

    private void populateForm(Transaction t) {
        amountField.setText(String.valueOf(t.getAmount()));
        categoryField.setText(t.getCategory());
        datePicker.setValue(t.getDate());
        typeCombo.setValue(t.getType());
        notesField.setText(t.getNotes());
    }

    private void clearForm() {
        amountField.clear();
        categoryField.clear();
        notesField.clear();
        datePicker.setValue(LocalDate.now());
        typeCombo.getSelectionModel().selectFirst();
        selectedTransaction = null;
        transactionTable.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
