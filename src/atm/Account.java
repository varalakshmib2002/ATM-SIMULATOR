package atm;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Account {
    private String accountId;
    private String pin;
    private double balance;
    private static final DateTimeFormatter TF = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public Account(String accountId, String pin, double balance) {
        this.accountId = accountId;
        this.pin = pin;
        this.balance = balance;
    }

    public String getAccountId() { return accountId; }
    public String getPin() { return pin; }

    public synchronized double getBalance() { return balance; }

    public synchronized void deposit(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Deposit must be positive");
        balance += amount;
        recordTransaction("DEPOSIT", amount);
    }

    public synchronized boolean withdraw(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Withdraw must be positive");
        if (amount > balance) return false;
        balance -= amount;
        recordTransaction("WITHDRAW", amount);
        return true;
    }

    private void recordTransaction(String type, double amount) {
        String filename = "txn_" + accountId + ".txt";
        String line = String.format("%s - %s - %.2f - Balance: %.2f%n",
                LocalDateTime.now().format(TF), type, amount, balance);
        try {
            Files.write(Paths.get(filename), line.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Failed to record transaction: " + e.getMessage());
        }
    }

    public void printMiniStatement() {
        String filename = "txn_" + accountId + ".txt";
        Path p = Paths.get(filename);
        System.out.println("----- Mini Statement for " + accountId + " -----");
        if (!Files.exists(p)) {
            System.out.println("No transactions found.");
            return;
        }
        try (BufferedReader br = Files.newBufferedReader(p)) {
            String line;
            int count = 0;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                count++;
            }
            if (count == 0) System.out.println("No transactions found.");
        } catch (IOException e) {
            System.out.println("Error reading transactions: " + e.getMessage());
        }
        System.out.println("----------------------------------------");
    }

    // Serialize account to CSV line for accounts.txt
    public String toRecord() {
        return accountId + "," + pin + "," + balance;
    }

    // factory to parse line
    public static Account fromRecord(String recordLine) {
        String[] parts = recordLine.split(",");
        if (parts.length != 3) return null;
        String id = parts[0].trim();
        String pin = parts[1].trim();
        double bal = Double.parseDouble(parts[2].trim());
        return new Account(id, pin, bal);
    }
}
