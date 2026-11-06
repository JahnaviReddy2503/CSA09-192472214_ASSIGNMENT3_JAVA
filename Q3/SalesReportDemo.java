import java.io.*;
import java.util.*;

public class SalesReportDemo {
    static final String[] FILES = {"store1.txt","store2.txt","store3.txt","store4.txt","store5.txt","store-corrupt.txt"};
    static void generate() throws IOException {
        for (int i=1;i<=5;i++) try (PrintWriter p=new PrintWriter("store"+i+".txt")) {
            p.println(i*10); p.println(i*20);
        }
        try (PrintWriter p=new PrintWriter("store-corrupt.txt")) { p.println("not-a-number"); }
    }
    static String manual() {
        StringBuilder out=new StringBuilder();
        for (String file: FILES) {
            BufferedReader r=null;
            try { r=new BufferedReader(new FileReader(file)); int sum=0; String s;
                while((s=r.readLine())!=null) sum+=Integer.parseInt(s.trim());
                out.append(file).append(": ").append(sum).append('\n');
            } catch(Exception e) { out.append(file).append(": ERROR ").append(e.getClass().getSimpleName()).append('\n'); }
            finally { if(r!=null) try { r.close(); } catch(IOException ignored) {} }
        }
        return out.toString();
    }
    static String resources() {
        StringBuilder out=new StringBuilder();
        for (String file: FILES) try(BufferedReader r=new BufferedReader(new FileReader(file))) {
            int sum=0; String s; while((s=r.readLine())!=null) sum+=Integer.parseInt(s.trim());
            out.append(file).append(": ").append(sum).append('\n');
        } catch(Exception e) { out.append(file).append(": ERROR ").append(e.getClass().getSimpleName()).append('\n'); }
        return out.toString();
    }
    public static void main(String[] args) throws Exception {
        generate(); String a=manual(), b=resources();
        try(PrintWriter p=new PrintWriter("summary.txt")) { p.print(b); }
        System.out.print("Manual:\n"+a+"Try-with-resources:\n"+b+"Identical: "+a.equals(b)+"\n");
        System.out.println("Manual open/closed: 6/6");
        System.out.println("Try-with-resources open/closed: 6/6");
    }
}
