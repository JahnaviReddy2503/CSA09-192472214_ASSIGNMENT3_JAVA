import java.util.concurrent.atomic.AtomicBoolean;

public class SeatBooking {
    final boolean[] booked;
    int bookingsMade;
    public SeatBooking(int seats){booked=new boolean[seats];}
    void bookUnsafe(int seat){if(!booked[seat]){try{Thread.sleep(1);}catch(InterruptedException e){Thread.currentThread().interrupt();}booked[seat]=true;bookingsMade++;}}
    public synchronized void book(int seat){if(!booked[seat]){booked[seat]=true;bookingsMade++;}}
    void bookBlock(int seat){synchronized(this){if(!booked[seat]){booked[seat]=true;bookingsMade++;}}}
    static int run(int mode) throws InterruptedException {
        SeatBooking s=new SeatBooking(10); Thread[] ts=new Thread[100];
        for(int i=0;i<ts.length;i++){final int seat=i%10;ts[i]=new Thread(()->{if(mode==0)s.bookUnsafe(seat);else if(mode==1)s.book(seat);else s.bookBlock(seat);});ts[i].start();}
        for(Thread t:ts)t.join(); return s.bookingsMade;
    }
    public static void main(String[] args)throws Exception{
        for(int i=1;i<=10;i++)System.out.println("Run "+i+" unsynchronized seats sold: "+run(0));
        System.out.println("Synchronized method seats sold: "+run(1));
        System.out.println("Synchronized block seats sold: "+run(2));
        System.out.println("Atomic alternative: "+atomic());
    }
    static int atomic() throws InterruptedException {
        AtomicBoolean[] seats=new AtomicBoolean[10];for(int i=0;i<10;i++)seats[i]=new AtomicBoolean();
        Thread[] ts=new Thread[100];AtomicBoolean[] a=seats;java.util.concurrent.atomic.AtomicInteger n=new java.util.concurrent.atomic.AtomicInteger();
        for(int i=0;i<100;i++){final int x=i%10;ts[i]=new Thread(()->{if(a[x].compareAndSet(false,true))n.incrementAndGet();});ts[i].start();}
        for(Thread t:ts)t.join();return n.get();
    }
}
