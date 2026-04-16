# Expense Tracker — Java + JavaFX + MySQL

A desktop Expense Tracker application following layered architecture, the DAO design pattern, and all core OOP principles.

---

## Prerequisites

- macOS (Apple Silicon or Intel)
- Java 21
- JavaFX 21 SDK
- MySQL 8.x
- MySQL Connector/J 8.x JAR

---

## Step 1 — Install Java 17

```bash
brew install openjdk@17
echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 17)' >> ~/.zshrc
source ~/.zshrc
java -version
```

---

## Step 2 — Install MySQL

```bash
brew install mysql
brew services start mysql
mysql_secure_installation
```

---

## Step 3 — Create the Database

```bash
mysql -u root -p < db/setup.sql
```

---

## Step 4 — Download JavaFX 26 SDK

1. Go to https://gluonhq.com/products/javafx/
2. Download **JavaFX 26 SDK** for macOS (choose aarch64 for Apple Silicon, x64 for Intel)
3. Extract and place the folder at:

```
ExpenseTracker/lib/javafx-sdk-26/
```
  
Confirm the path contains:
```
lib/javafx-sdk-26/lib/javafx.controls.jar
lib/javafx-sdk-26/lib/javafx.fxml.jar
```

---

## Step 5 — Download MySQL Connector/J

1. Go to https://dev.mysql.com/downloads/connector/j/
2. Download the **Platform Independent** ZIP
3. Extract and copy `mysql-connector-j-8.x.x.jar` to:

```
ExpenseTracker/lib/mysql-connector-j-8.x.x.jar
```

Rename it to `mysql-connector.jar` for simplicity, or adjust the commands below.

---

## Step 6 — Configure Database Password

Edit `src/com/expensetracker/db/DBConnection.java`:

```java
private static final String PASSWORD = "your_mysql_root_password";
```

---

## Step 7 — Compile

From the `ExpenseTracker/` root directory:

```bash
javac \
  --module-path lib/javafx-sdk-26/lib \
  --add-modules javafx.controls,javafx.fxml \
  -cp "lib/mysql-connector.jar" \
  -d out \
  $(find src -name "*.java")
```

---

## Step 8 — Copy FXML Resources

```bash
cp -r resources/* out/
```

---

## Step 9 — Run

```bash
java \
  --module-path lib/javafx-sdk-26/lib \
  --add-modules javafx.controls,javafx.fxml \
  -cp "out:lib/mysql-connector.jar" \
  com.expensetracker.MainApp
```

---

## Troubleshooting

| Issue | Fix |
|---|---|
| `SQLException: Access denied` | Verify the PASSWORD in DBConnection.java |
| `ClassNotFoundException: com.mysql.cj.jdbc.Driver` | MySQL Connector JAR is missing from classpath |
| `Location is not set` (FXML) | Ensure `cp -r resources/* out/` was run after compile |
| JavaFX modules not found | Confirm the `--module-path` points to the correct `lib/` subfolder of the JavaFX SDK |
| Apple Silicon: `Unsupported arch` | Download the aarch64 (ARM) JavaFX SDK, not the x64 one |

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
│   │   ├── Transaction.java
│   │   └── User.java
│   ├── dao/
│   │   ├── TransactionDAO.java
│   │   └── TransactionDAOImpl.java
│   ├── service/
│   │   └── ExpenseService.java
│   ├── db/
│   │   └── DBConnection.java
│   └── controller/
│       └── MainController.java
├── resources/com/expensetracker/
│   └── main.fxml
├── db/
│   └── setup.sql
├── lib/
│   ├── javafx-sdk-26/
│   └── mysql-connector.jar
└── README.md
```
