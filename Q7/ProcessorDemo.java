import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

public class ProcessorDemo {
    record Result(String file, boolean success, String reason) {}

    static void generate(Path dir, int count, int corruptEvery, int missingEvery) throws IOException {
        Files.createDirectories(dir);
        for (int i = 1; i <= count; i++) {
            Path file = dir.resolve(String.format("s%05d.txt", i));
            if (missingEvery > 0 && i % missingEvery == 0) {
                Files.deleteIfExists(file);
            } else if (corruptEvery > 0 && i % corruptEvery == 0) {
                Files.writeString(file, "not-a-number\n");
            } else {
                Files.writeString(file, String.valueOf(i % 101));
            }
        }
    }

    static Result process(Path file) {
        try {
            int score = Integer.parseInt(Files.readString(file).trim());
            if (score < 0 || score > 100)
                return new Result(file.toString(), false, "out-of-range");
            return new Result(file.toString(), true, "success");
        } catch (NoSuchFileException e) {
            return new Result(file.toString(), false, "missing");
        } catch (NumberFormatException e) {
            return new Result(file.toString(), false, "non-numeric");
        } catch (IOException e) {
            return new Result(file.toString(), false, "io-error");
        }
    }

    static List<Result> processWithPool(Path dir, int count, int pool) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(pool);
        List<Future<Result>> futures = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            Path file = dir.resolve(String.format("s%05d.txt", i));
            futures.add(executor.submit(() -> process(file)));
        }
        List<Result> results = new ArrayList<>();
        for (Future<Result> future : futures) results.add(future.get());
        executor.shutdown();
        return results;
    }

    static long timedPool(Path dir, int count, int pool) throws Exception {
        long start = System.nanoTime();
        List<Result> results = processWithPool(dir, count, pool);
        long elapsed = (System.nanoTime() - start) / 1_000_000;
        long failures = results.stream().filter(r -> !r.success()).count();
        System.out.println("Pool " + pool + ": successes=" + (count - failures)
                + ", failures=" + failures + ", timeMs=" + elapsed);
        return elapsed;
    }

    static void summarize(List<Result> results, String label) {
        Map<String, Long> reasons = results.stream().filter(r -> !r.success())
                .collect(Collectors.groupingBy(Result::reason, TreeMap::new, Collectors.counting()));
        long successes = results.stream().filter(Result::success).count();
        System.out.println(label + " successes=" + successes + " failures="
                + (results.size() - successes) + " reasons=" + reasons);
    }

    static void smallVerification() throws Exception {
        Path small = Path.of("small-scores");
        Files.createDirectories(small);
        for (int i = 1; i <= 10; i++) Files.writeString(small.resolve(String.format("s%05d.txt", i)),
                i == 3 ? "bad" : (i == 4 ? "101" : String.valueOf(i)));
        Files.deleteIfExists(small.resolve("s00007.txt"));
        List<Result> results = processWithPool(small, 10, 2);
        summarize(results, "Small set expected successes=7");
    }

    static void parallelStreamRun(Path dir, int count) {
        long start = System.nanoTime();
        List<Result> results = IntStream.rangeClosed(1, count).parallel()
                .mapToObj(i -> process(dir.resolve(String.format("s%05d.txt", i))))
                .toList();
        long ms = (System.nanoTime() - start) / 1_000_000;
        summarize(results, "parallelStream");
        System.out.println("parallelStream timeMs=" + ms);
    }

    public static void main(String[] args) throws Exception {
        int fileCount = 10000, corruptEvery = 50, missingEvery = 75;
        int[] pools = {1, 2, 4, 8};
        try (Scanner input = new Scanner(System.in)) {
            while (input.hasNext()) {
                String[] pair = input.next().split("=");
                if (pair.length != 2) continue;
                if (pair[0].equals("fileCount")) fileCount = Integer.parseInt(pair[1]);
                else if (pair[0].equals("corruptEvery")) corruptEvery = Integer.parseInt(pair[1]);
                else if (pair[0].equals("missingEvery")) missingEvery = Integer.parseInt(pair[1]);
                else if (pair[0].equals("poolSizes"))
                    pools = Arrays.stream(pair[1].split(",")).mapToInt(Integer::parseInt).toArray();
            }
        }
        Path scores = Path.of("scores");
        generate(scores, fileCount, corruptEvery, missingEvery);
        System.out.println("Input fileCount=" + fileCount + " corruptEvery=" + corruptEvery
                + " missingEvery=" + missingEvery);
        for (int pool : pools) {
            long total = 0;
            for (int run = 1; run <= 3; run++) total += timedPool(scores, fileCount, pool);
            System.out.println("Pool " + pool + " averageOf3Ms=" + (total / 3));
        }
        summarize(processWithPool(scores, fileCount, pools[0]), "Final summary");
        smallVerification();
        parallelStreamRun(scores, fileCount);
    }
}
