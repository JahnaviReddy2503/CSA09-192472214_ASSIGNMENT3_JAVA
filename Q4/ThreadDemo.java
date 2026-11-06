import java.util.*;

public class ThreadDemo {
    static class Station extends Thread {
        private final int amount;
        Station(String name,int amount){super(name);this.amount=amount;}
        public void run(){ for(int i=0;i<amount;i++) try{Thread.sleep(2);}catch(InterruptedException e){Thread.currentThread().interrupt();} System.out.println(getName()+" scanned "+amount+" state="+getState()); }
    }
    static class RunnableStation implements Runnable {
        private final int amount;
        RunnableStation(int amount){this.amount=amount;}
        public void run(){System.out.println(Thread.currentThread().getName()+" scanned "+amount);}
    }
    public static void main(String[] args) throws Exception {
        List<Thread> list=Arrays.asList(new Station("Station-1",3),new Station("Station-2",4),new Station("Station-3",2),new Station("Station-4",5));
        System.out.println("Initial state="+list.get(0).getState());
        for(Thread t:list){t.start(); System.out.println(t.getName()+" state="+t.getState());}
        for(Thread t:list)t.join();
        int total=0; for(Thread t:list) total+=((Station)t).amount;
        System.out.println("Grand total after join: "+total);
        new Thread(new RunnableStation(2),"Runnable-station").run();
        System.out.println("run() executed on "+Thread.currentThread().getName());
    }
}
