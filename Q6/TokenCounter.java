public class TokenCounter {
    private final int[] buffer=new int[5]; private int count,in,out;
    public synchronized void put(int token)throws InterruptedException{while(count==5)wait();buffer[in]=token;in=(in+1)%5;count++;notifyAll();}
    public synchronized int take()throws InterruptedException{while(count==0)wait();int t=buffer[out];out=(out+1)%5;count--;notifyAll();return t;}
    static class Account{final int id;int balance=100;Account(int id){this.id=id;}}
    static void ordered(Account a,Account b,int amount){Account first=a.id<b.id?a:b, second=a.id<b.id?b:a;synchronized(first){synchronized(second){a.balance-=amount;b.balance+=amount;}}}
    public static void main(String[] args)throws Exception{
        TokenCounter c=new TokenCounter();int[] sum={0};
        Thread cook=new Thread(()->{try{for(int i=1;i<=20;i++){c.put(i);Thread.sleep(1);}}catch(InterruptedException e){Thread.currentThread().interrupt();}});
        Thread s1=new Thread(()->take(c,sum,10));Thread s2=new Thread(()->take(c,sum,10));
        cook.start();s1.start();s2.start();cook.join();s1.join();s2.join();System.out.println("Sum of tokens collected: "+sum[0]+" (expected 210)");
        Account a=new Account(1),b=new Account(2);Thread t1=new Thread(()->ordered(a,b,10)),t2=new Thread(()->ordered(b,a,10));t1.start();t2.start();t1.join();t2.join();System.out.println("Ordered transfer balances: "+a.balance+", "+b.balance);
    }
    static void take(TokenCounter c,int[] sum,int n){try{for(int i=0;i<n;i++)synchronized(sum){sum[0]+=c.take();}}catch(InterruptedException e){Thread.currentThread().interrupt();}}
}
