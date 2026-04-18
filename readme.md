# Expense Tracker v2 — Java + JavaFX + MySQL

## New Features Added

- **Notes field** — optional extra detail on every transaction
- **Date range filter** — From / To date pickers in the search bar
- **Sortable columns** — click any column header to sort the table
- **Transaction count** — income count + expense count shown in the top bar
- **CSV Export** — exports all transactions to a .csv file via a file-save dialog
- **Monthly Summary** — pick any month + year and see income, expenses, net for that period
- **Category % breakdown** — progress bars and percentages in the category summary panel
- **Dark mode toggle** — button in the top-right corner switches the theme

---

## Setup (same as before)

### Step 1 — Run the DB script
```bash
mysql -u root -p < db/setup.sql
```
The script is safe to re-run — it uses `IF NOT EXISTS` and `ADD COLUMN IF NOT EXISTS`.

### Step 2 — Set your password
Edit `src/com/expensetracker/db/DBConnection.java`:
```java
private static final String PASSWORD = "your_password_here";
```

### Step 3 — Place dependencies in lib/
```
lib/javafx-sdk-21/          ← JavaFX 21 SDK from gluonhq.com
lib/mysql-connector.jar     ← MySQL Connector/J from dev.mysql.com
```

### Step 4 — Compile
```bash
javac \
  --module-path lib/javafx-sdk-21/lib \
  --add-modules javafx.controls,javafx.fxml \
  -cp "lib/mysql-connector.jar" \
  -d out \
  $(find src -name "*.java")
```

### Step 5 — Copy resources
```bash
cp -r resources/* out/
```

### Step 6 — Run
```bash
java \
  --module-path lib/javafx-sdk-21/lib \
  --add-modules javafx.controls,javafx.fxml \
  -cp "out:lib/mysql-connector.jar" \
  com.expensetracker.MainApp
```

---

## Troubleshooting

| Error | Fix |
|---|---|
| `Access denied` | Wrong password in DBConnection.java |
| `Unknown database` | Re-run db/setup.sql |
| `ClassNotFoundException` | mysql-connector.jar missing from lib/ |
| `Location is not set` | Run `cp -r resources/* out/` after compiling |
| JavaFX modules not found | Check `--module-path` points to `lib/javafx-sdk-21/lib` (inner lib folder) |
| `Unknown column 'notes'` | Re-run db/setup.sql — it adds the notes column |

---

## Project Structure

```
ExpenseTracker/
├── src/com/expensetracker/
│   ├── MainApp.java
│   ├── model/
│   │   ├── Record.java
│   │   ├── Expense.java
│   │   ├── Income.java
│   │   ├── Transaction.java       ← now includes notes field
│   │   └── User.java
│   ├── dao/
│   │   ├── TransactionDAO.java    ← new: range search, monthly, count methods
│   │   └── TransactionDAOImpl.java
│   ├── service/
│   │   └── ExpenseService.java    ← new: monthly summary, counts, overloaded add methods
│   ├── db/
│   │   └── DBConnection.java
│   ├── export/
│   │   └── CsvExporter.java       ← NEW
│   └── controller/
│       └── MainController.java    ← all new features wired in
├── resources/com/expensetracker/
│   └── main.fxml                  ← rebuilt with all new UI panels
├── db/
│   └── setup.sql
└── README.md
```
