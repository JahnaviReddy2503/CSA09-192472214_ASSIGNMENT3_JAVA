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

    private static void run(String input) {
        System.out.println("Input: " + input);
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
        try (Scanner input = new Scanner(System.in)) {
            while (input.hasNextLine()) {
                String line = input.nextLine().trim();
                if (!line.isEmpty()) run(line);
            }
        }
        long thrown = 0, returned = 0;
        for (int repeat = 0; repeat < 3; repeat++) {
            long start = System.nanoTime();
            for (int i = 0; i < 100000; i++) {
                try { throw new IllegalArgumentException("bad amount"); }
                catch (IllegalArgumentException ignored) { }
            }
            thrown += System.nanoTime() - start;
            start = System.nanoTime();
            for (int i = 0; i < 100000; i++) {
                if (errorCode(-1) == -1) { }
            }
            returned += System.nanoTime() - start;
        }
        System.out.println("Average 100000 throws ns=" + thrown / 3
                + ", error-code returns ns=" + returned / 3);
    }

    private static int errorCode(double amount) { return amount < 0 ? -1 : 0; }
}
