import java.io.*;import java.nio.file.*;import java.util.*;import java.util.concurrent.*;import java.util.concurrent.atomic.*;

public class ProcessorDemo {
    record Result(String file,boolean success,String reason){}
    static void generate(Path dir)throws IOException{Files.createDirectories(dir);for(int i=1;i<=10000;i++){Path p=dir.resolve(String.format("s%05d.txt",i));if(i%997==0)Files.writeString(p,"bad\n");else if(i%991==0)Files.writeString(p,"101\n");else Files.writeString(p,String.valueOf(i%101));}}
    static Result process(Path p){try{int n=Integer.parseInt(Files.readString(p).trim());if(n<0||n>100)return new Result(p.toString(),false,"out-of-range");return new Result(p.toString(),true,"");}catch(NoSuchFileException e){return new Result(p.toString(),false,"missing");}catch(NumberFormatException|IOException e){return new Result(p.toString(),false,"invalid");}}
    static void run(Path dir,int pool)throws Exception{ExecutorService ex=Executors.newFixedThreadPool(pool);ConcurrentLinkedQueue<Result> results=new ConcurrentLinkedQueue<>();long start=System.nanoTime();List<Future<Result>> fs=new ArrayList<>();for(int i=1;i<=10000;i++){Path p=dir.resolve(String.format("s%05d.txt",i));fs.add(ex.submit(()->process(p)));}for(Future<Result> f:fs)results.add(f.get());ex.shutdown();long ms=(System.nanoTime()-start)/1_000_000;long ok=results.stream().filter(Result::success).count();System.out.println("Pool "+pool+": successes="+ok+", failures="+(results.size()-ok)+", timeMs="+ms);}
    public static void main(String[] args)throws Exception{Path d=Paths.get("scores");generate(d);Files.deleteIfExists(d.resolve("s00007.txt"));for(int p:new int[]{1,2,4,8})run(d,p);}
}
