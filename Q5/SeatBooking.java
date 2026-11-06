import java.util.Scanner;
import java.util.concurrent.atomic.*;

public class SeatBooking {
    final boolean[] booked;
    int bookingsMade;
    public SeatBooking(int seats) { booked = new boolean[seats]; }
    void bookUnsafe(int seat) {
        if (!booked[seat]) {
            try { Thread.sleep(1); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            booked[seat] = true;
            bookingsMade++;
        }
    }
    public synchronized void book(int seat) {
        if (!booked[seat]) { booked[seat] = true; bookingsMade++; }
    }
    void bookBlock(int seat) {
        synchronized (this) {
            if (!booked[seat]) { booked[seat] = true; bookingsMade++; }
        }
    }
    static int run(int mode, int seats, int users) throws InterruptedException {
        SeatBooking screen = new SeatBooking(seats);
        Thread[] threads = new Thread[users];
        for (int i = 0; i < users; i++) {
            final int seat = i % seats;
            threads[i] = new Thread(() -> {
                if (mode == 0) screen.bookUnsafe(seat);
                else if (mode == 1) screen.book(seat);
                else screen.bookBlock(seat);
            });
            threads[i].start();
        }
        for (Thread thread : threads) thread.join();
        return screen.bookingsMade;
    }
    static long timed(int mode, int seats, int users) throws InterruptedException {
        long start = System.nanoTime();
        run(mode, seats, users);
        return (System.nanoTime() - start) / 1_000;
    }
    static int atomic(int seats, int users) throws InterruptedException {
        AtomicBoolean[] state = new AtomicBoolean[seats];
        for (int i = 0; i < seats; i++) state[i] = new AtomicBoolean();
        AtomicInteger sold = new AtomicInteger();
        Thread[] threads = new Thread[users];
        for (int i = 0; i < users; i++) {
            final int seat = i % seats;
            threads[i] = new Thread(() -> { if (state[seat].compareAndSet(false, true)) sold.incrementAndGet(); });
            threads[i].start();
        }
        for (Thread thread : threads) thread.join();
        return sold.get();
    }
    public static void main(String[] args) throws Exception {
        int seats = 10, users = 100, bookingsForTiming = 10000;
        try (Scanner input = new Scanner(System.in)) {
            while (input.hasNext()) {
                String[] pair = input.next().split("=");
                if (pair.length != 2) continue;
                int value = Integer.parseInt(pair[1]);
                if (pair[0].equals("seats")) seats = value;
                else if (pair[0].equals("users")) users = value;
                else if (pair[0].equals("bookingsForTiming")) bookingsForTiming = value;
            }
        }
        for (int run = 1; run <= 10; run++)
            System.out.println("Run " + run + " unsynchronized: Seats sold: " + run(0, seats, users));
        System.out.println("Synchronized method: Seats sold: " + run(1, seats, users));
        System.out.println("Synchronized block: Seats sold: " + run(2, seats, users));
        for (int mode = 0; mode < 3; mode++) {
            long total = 0;
            for (int i = 0; i < 3; i++) total += timed(mode, seats, bookingsForTiming);
            System.out.println("Timing mode " + mode + " averageMicros=" + total / 3);
        }
        System.out.println("AtomicBoolean alternative: Seats sold: " + atomic(seats, users));
    }
}
