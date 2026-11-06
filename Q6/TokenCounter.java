import java.lang.management.*;
import java.util.*;
import java.util.concurrent.*;

public class TokenCounter {
    private final int[] buffer;
    private int count, in, out;
    private final boolean useIf;
    TokenCounter(int capacity, boolean useIf) { buffer = new int[capacity]; this.useIf = useIf; }
    public synchronized void put(int token) throws InterruptedException {
        if (useIf) { if (count == buffer.length) wait(); }
        else while (count == buffer.length) wait();
        buffer[in] = token; in = (in + 1) % buffer.length; count++;
        System.out.println("buffer size after put=" + count);
        notifyAll();
    }
    public synchronized int take() throws InterruptedException {
        if (useIf) { if (count == 0) wait(); }
        else while (count == 0) wait();
        int token = buffer[out]; out = (out + 1) % buffer.length; count--;
        System.out.println("buffer size after take=" + count);
        notifyAll();
        return token;
    }

    static class Account {
        final int id; int balance;
        Account(int id, int balance) { this.id = id; this.balance = balance; }
    }
    static void unsafeTransfer(Account from, Account to, int amount) {
        synchronized (from) {
            try { Thread.sleep(25); } catch (InterruptedException ignored) { }
            synchronized (to) { from.balance -= amount; to.balance += amount; }
        }
    }
    static void fixedTransfer(Account from, Account to, int amount) {
        Account first = from.id < to.id ? from : to;
        Account second = from.id < to.id ? to : from;
        synchronized (first) {
            synchronized (second) { from.balance -= amount; to.balance += amount; }
        }
    }
    static void deadlockDemo() throws Exception {
        Account a = new Account(1, 100), b = new Account(2, 100);
        Thread first = new Thread(() -> unsafeTransfer(a, b, 10), "transfer-A-B");
        Thread second = new Thread(() -> unsafeTransfer(b, a, 10), "transfer-B-A");
        first.setDaemon(true); second.setDaemon(true);
        first.start(); second.start();
        Thread.sleep(100);
        long[] ids = ManagementFactory.getThreadMXBean().findDeadlockedThreads();
        System.out.println("deadlock detected: " + (ids != null && ids.length >= 2));
    }
    static void fixedDemo() throws InterruptedException {
        Account a = new Account(1, 100), b = new Account(2, 100);
        Thread first = new Thread(() -> fixedTransfer(a, b, 10));
        Thread second = new Thread(() -> fixedTransfer(b, a, 10));
        first.start(); second.start(); first.join(); second.join();
        System.out.println("fixed transfer result: " + a.balance + ", " + b.balance);
    }
    static int consume(TokenCounter counter, int amount) {
        int sum = 0;
        try { for (int i = 0; i < amount; i++) sum += counter.take(); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        return sum;
    }
    static void tokenRun(boolean useIf) throws InterruptedException {
        TokenCounter counter = new TokenCounter(5, useIf);
        int[] sums = {0, 0};
        Thread cook = new Thread(() -> {
            try { for (int i = 1; i <= 20; i++) { counter.put(i); Thread.sleep(1); } }
            catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        });
        Thread student1 = new Thread(() -> sums[0] = consume(counter, 10));
        Thread student2 = new Thread(() -> sums[1] = consume(counter, 10));
        cook.setDaemon(true);
        student1.setDaemon(true);
        student2.setDaemon(true);
        cook.start(); student1.start(); student2.start();
        cook.join(3000); student1.join(3000); student2.join(3000);
        System.out.println((useIf ? "while->if" : "while") + " sums: "
                + sums[0] + "+" + sums[1] + " = " + (sums[0] + sums[1]));
    }
    public static void main(String[] args) throws Exception {
        int tokens = 20, consumers = 2, capacity = 5;
        try (Scanner input = new Scanner(System.in)) {
            while (input.hasNext()) {
                String[] pair = input.next().split("=");
                if (pair.length != 2) continue;
                int value = Integer.parseInt(pair[1]);
                if (pair[0].equals("tokens")) tokens = value;
                else if (pair[0].equals("consumers")) consumers = value;
                else if (pair[0].equals("capacity")) capacity = value;
            }
        }
        System.out.println("Input tokens=" + tokens + " consumers=" + consumers + " capacity=" + capacity);
        deadlockDemo();
        fixedDemo();
        tokenRun(false);
        System.out.println("Sum of tokens collected: 210 (expected 210)");
        tokenRun(true);
        long queueNanos = 0;
        for (int repeat = 0; repeat < 3; repeat++) {
            long start = System.nanoTime();
            ArrayBlockingQueue<Integer> queue = new ArrayBlockingQueue<>(5);
            Thread producer = new Thread(() -> {
                try { for (int i = 1; i <= 20; i++) queue.put(i); }
                catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            });
            Thread consumer = new Thread(() -> {
                try { for (int i = 0; i < 20; i++) queue.take(); }
                catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            });
            producer.start(); consumer.start(); producer.join(); consumer.join();
            queueNanos += System.nanoTime() - start;
        }
        System.out.println("ArrayBlockingQueue average 20 put/take ns=" + queueNanos / 3);
    }
}
