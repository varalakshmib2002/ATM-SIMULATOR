package atm;
import java.util.Scanner;

public class ATMApp {
    private static AccountStore store = new AccountStore();
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=== Welcome to CoreJava ATM Simulator ===");
        while (true) {
            System.out.println("\n1. Login\n2. Exit");
            System.out.print("Select: ");
            String opt = sc.nextLine().trim();
            if ("1".equals(opt)) {
                loginFlow();
            } else if ("2".equals(opt)) {
                System.out.println("Exiting... Saving data.");
                store.saveAccounts();
                break;
            } else {
                System.out.println("Invalid choice.");
            }
        }
        sc.close();
    }

    private static void loginFlow() {
        System.out.print("Enter Account ID: ");
        String id = sc.nextLine().trim();
        System.out.print("Enter PIN: ");
        String pin = sc.nextLine().trim();

        Account acc = store.findAccount(id);
        if (acc == null) {
            System.out.println("Account not found.");
            return;
        }
        if (!acc.getPin().equals(pin)) {
            System.out.println("Invalid PIN.");
            return;
        }
        System.out.println("Login successful. Welcome " + id + "!");
        userMenu(acc);
    }

    private static void userMenu(Account acc) {
        while (true) {
            System.out.println("\n---- Menu ----");
            System.out.println("1. Check Balance");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Mini Statement");
            System.out.println("5. Logout");
            System.out.print("Choose: ");
            String ch = sc.nextLine().trim();
            try {
                switch (ch) {
                    case "1":
                        System.out.printf("Current balance: %.2f%n", acc.getBalance());
                        break;
                    case "2":
                        System.out.print("Enter amount to deposit: ");
                        double d = Double.parseDouble(sc.nextLine().trim());
                        acc.deposit(d);
                        store.saveAccounts();
                        System.out.printf("Deposit successful. New balance: %.2f%n", acc.getBalance());
                        break;
                    case "3":
                        System.out.print("Enter amount to withdraw: ");
                        double w = Double.parseDouble(sc.nextLine().trim());
                        boolean ok = acc.withdraw(w);
                        if (ok) {
                            store.saveAccounts();
                            System.out.printf("Withdraw successful. New balance: %.2f%n", acc.getBalance());
                        } else {
                            System.out.println("Insufficient balance.");
                        }
                        break;
                    case "4":
                        acc.printMiniStatement();
                        break;
                    case "5":
                        System.out.println("Logging out...");
                        return;
                    default:
                        System.out.println("Invalid option.");
                }
            } catch (NumberFormatException nfe) {
                System.out.println("Invalid amount. Please enter numbers only.");
            } catch (IllegalArgumentException iae) {
                System.out.println("Operation error: " + iae.getMessage());
            } catch (Exception e) {
                System.out.println("Unexpected error: " + e.getMessage());
            }
        }
    }
}
