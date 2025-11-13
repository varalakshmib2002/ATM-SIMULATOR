package atm;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class AccountStore {
    private static final String ACC_FILE = "accounts.txt";
    private Map<String, Account> accounts = new HashMap<>();

    public AccountStore() {
        loadAccounts();
    }

    private void loadAccounts() {
        Path p = Paths.get(ACC_FILE);
        if (!Files.exists(p)) {
            // create sample accounts if file absent
            accounts.put("1001", new Account("1001", "1234", 25000.0));
            accounts.put("1002", new Account("1002", "2345", 15000.0));
            accounts.put("1003", new Account("1003", "3456", 5000.0));
            saveAccounts(); // persist defaults
            return;
        }
        try (BufferedReader br = Files.newBufferedReader(p)) {
            String line;
            while ((line = br.readLine()) != null) {
                Account a = Account.fromRecord(line);
                if (a != null) accounts.put(a.getAccountId(), a);
            }
        } catch (IOException e) {
            System.err.println("Failed to load accounts: " + e.getMessage());
        }
    }

    public synchronized void saveAccounts() {
        Path p = Paths.get(ACC_FILE);
        List<String> lines = new ArrayList<>();
        for (Account a : accounts.values()) lines.add(a.toRecord());
        try {
            Files.write(p, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Failed to save accounts: " + e.getMessage());
        }
    }

    public Account findAccount(String accountId) {
        return accounts.get(accountId);
    }

    public void addAccount(Account account) {
        accounts.put(account.getAccountId(), account);
        saveAccounts();
    }
}
