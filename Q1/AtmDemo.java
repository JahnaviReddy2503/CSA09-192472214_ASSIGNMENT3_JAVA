import java.io.File;
import java.io.FileNotFoundException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class AtmDemo {
    private static double withdraw(double balance, double amount)
            throws InsufficientFundsException {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        if (amount > balance) {
            throw new InsufficientFundsException("Insufficient balance");
        }
        return balance - amount;
    }

    private static void run(String label, String input) {
        System.out.println("[" + label + "]");
        try (Scanner scanner = new Scanner(input)) {
            int account = scanner.nextInt();
            double amount = scanner.nextDouble();
            System.out.printf("Account %d: remaining %.2f%n", account, withdraw(1000, amount));
            if (amount == 0) {
                new Scanner(new File("missing-card-reader.dat")).close();
            }
        } catch (InputMismatchException | NumberFormatException e) {
            System.out.println("Input error: " + e.getClass().getSimpleName());
        } catch (IllegalArgumentException e) {
            System.out.println("Illegal amount: " + e.getMessage());
        } catch (InsufficientFundsException e) {
            System.out.println("Withdrawal failed: " + e.getMessage());
        } catch (FileNotFoundException e) {
            System.out.println("Reader error: " + e.getMessage());
        } finally {
            System.out.println("finally executed");
        }
    }

    public static void main(String[] args) {
        run("valid", "101 250");
        run("letters", "abc 250");
        run("negative", "101 -5");
        run("over balance", "101 1500");
        run("missing reader", "101 0");
    }
}
