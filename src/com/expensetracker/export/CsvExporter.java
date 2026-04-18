package com.expensetracker.export;

import com.expensetracker.model.Transaction;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CsvExporter {

    public void export(List<Transaction> transactions, String filePath) throws IOException {
        FileWriter writer = new FileWriter(filePath);
        writer.write("ID,Type,Amount,Category,Date,Notes\n");
        for (Transaction t : transactions) {
            writer.write(t.getId() + ",");
            writer.write(escapeCsv(t.getType()) + ",");
            writer.write(String.format("%.2f", t.getAmount()) + ",");
            writer.write(escapeCsv(t.getCategory()) + ",");
            writer.write(t.getDate().toString() + ",");
            writer.write(escapeCsv(t.getNotes()) + "\n");
        }
        writer.flush();
        writer.close();
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
