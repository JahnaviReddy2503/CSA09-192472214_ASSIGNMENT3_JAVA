import java.util.*;
import java.util.concurrent.*;

public class ThreadDemo {
    static class Station extends Thread {
        private final int amount;
        Station(String name, int amount) { super(name); this.amount = amount; }
        public void run() {
            for (int i = 0; i < amount; i++) {
                try { Thread.sleep(2); }
                catch (InterruptedException e) { Thread.currentThread().interrupt(); return; }
            }
            synchronized (finishOrder) { finishOrder.add(getName()); }
            System.out.println(getName() + " scanned " + amount + " state=" + getState());
        }
    }
    static class RunnableStation implements Runnable {
        private final int amount;
        RunnableStation(int amount) { this.amount = amount; }
        public void run() {
            System.out.println(Thread.currentThread().getName() + " scanned " + amount);
        }
    }
    static final List<String> finishOrder = new ArrayList<>();

    static String oneRun(int[] amounts) throws InterruptedException {
        finishOrder.clear();
        List<Station> stations = new ArrayList<>();
        for (int i = 0; i < amounts.length; i++)
            stations.add(new Station("Station-" + (i + 1), amounts[i]));
        for (Station station : stations) station.start();
        for (Station station : stations) station.join();
        return String.join(" -> ", finishOrder);
    }

    public static void main(String[] args) throws Exception {
        int[] amounts = {120, 95, 150, 80};
        try (Scanner input = new Scanner(System.in)) {
            if (input.hasNextInt()) amounts[0] = input.nextInt();
            if (input.hasNextInt()) amounts[1] = input.nextInt();
            if (input.hasNextInt()) amounts[2] = input.nextInt();
            if (input.hasNextInt()) amounts[3] = input.nextInt();
        }
        Station probe = new Station("Probe", 1);
        System.out.println("Initial state=" + probe.getState());
        probe.start();
        while (probe.isAlive()) System.out.println("Probe alive state=" + probe.getState());
        probe.join();
        System.out.println("Finish order runs:");
        for (int run = 1; run <= 10; run++)
            System.out.println("Run " + run + ": " + oneRun(amounts));
        int total = Arrays.stream(amounts).sum();
        System.out.println("Grand total after join: " + total);
        new Thread(new RunnableStation(2), "Runnable-station").run();
        System.out.println("run() executed on " + Thread.currentThread().getName());
        Station high = new Station("Priority-10", 2);
        high.setPriority(Thread.MAX_PRIORITY);
        Station low = new Station("Priority-1", 2);
        low.setPriority(Thread.MIN_PRIORITY);
        high.start(); low.start(); high.join(); low.join();
        Thread daemon = new Thread(() -> {
            try { Thread.sleep(1000); System.out.println("daemon completed"); }
            catch (InterruptedException ignored) { }
        }, "daemon-station");
        daemon.setDaemon(true);
        daemon.start();
        System.out.println("daemon=" + daemon.isDaemon() + ", main ends while daemonAlive=" + daemon.isAlive());
        long executorNanos = 0;
        for (int repeat = 0; repeat < 3; repeat++) {
            long start = System.nanoTime();
            ExecutorService pool = Executors.newFixedThreadPool(2);
            List<Future<?>> futures = new ArrayList<>();
            for (int amount : amounts)
                futures.add(pool.submit(() -> { for (int i = 0; i < amount; i++) Thread.yield(); }));
            for (Future<?> future : futures) future.get();
            pool.shutdown();
            executorNanos += System.nanoTime() - start;
        }
        System.out.println("Executor fixed-pool average ns=" + executorNanos / 3);
    }
}
