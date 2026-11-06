import java.io.*;
import java.nio.file.*;
import java.util.*;

public class SalesReportDemo {
    static int opened;
    static int closed;

    static class CountingReader extends Reader {
        private final BufferedReader delegate;
        CountingReader(String file) throws IOException {
            delegate = new BufferedReader(new FileReader(file));
            opened++;
        }
        public int read(char[] c, int o, int l) throws IOException { return delegate.read(c, o, l); }
        public void close() throws IOException { if (delegate != null) { delegate.close(); closed++; } }
        String readLine() throws IOException { return delegate.readLine(); }
    }

    static void generate() throws IOException {
        for (int i = 1; i <= 5; i++) {
            try (PrintWriter p = new PrintWriter("store" + i + ".txt")) {
                p.println(i * 10);
                p.println(i * 20);
            }
        }
        try (PrintWriter p = new PrintWriter("store-corrupt.txt")) { p.println("not-a-number"); }
    }

    static List<String> filesFromInput() throws IOException {
        List<String> files = Files.readAllLines(Path.of("input.txt"));
        files.removeIf(String::isBlank);
        return files;
    }

    static String manual(List<String> files) {
        StringBuilder out = new StringBuilder();
        for (String file : files) {
            CountingReader reader = null;
            try {
                reader = new CountingReader(file);
                int sum = 0;
                String line;
                while ((line = reader.readLine()) != null) sum += Integer.parseInt(line.trim());
                out.append(file).append(": ").append(sum).append('\n');
                reader.close();
            } catch (Exception e) {
                out.append(file).append(": ERROR ").append(e.getClass().getSimpleName()).append('\n');
                System.out.println("LOG manual failure file=" + file + " reason=" + e.getClass().getSimpleName());
                // This intentionally models the common manual-close leak on failure.
            }
        }
        return out.toString();
    }

    static String resources(List<String> files) {
        StringBuilder out = new StringBuilder();
        for (String file : files) {
            try (CountingReader reader = new CountingReader(file)) {
                int sum = 0;
                String line;
                while ((line = reader.readLine()) != null) sum += Integer.parseInt(line.trim());
                out.append(file).append(": ").append(sum).append('\n');
            } catch (Exception e) {
                out.append(file).append(": ERROR ").append(e.getClass().getSimpleName()).append('\n');
                System.out.println("LOG try-with-resources failure file=" + file
                        + " reason=" + e.getClass().getSimpleName());
            }
        }
        return out.toString();
    }

    static void closeOrderDemo() {
        class Resource implements AutoCloseable {
            private final String name;
            Resource(String name) { this.name = name; }
            public void close() { System.out.println("close " + name); }
        }
        try (Resource first = new Resource("first"); Resource second = new Resource("second")) {
            System.out.println("resources opened");
        }
    }

    static void suppressedDemo() {
        class FailingResource implements AutoCloseable {
            public void close() throws IOException { throw new IOException("close failure"); }
        }
        try (FailingResource ignored = new FailingResource()) {
            throw new IOException("body failure");
        } catch (IOException e) {
            System.out.println("suppressed count=" + e.getSuppressed().length);
        }
    }

    public static void main(String[] args) throws Exception {
        generate();
        List<String> files = filesFromInput();
        opened = closed = 0;
        String manualReport = manual(files);
        System.out.println("Manual report:\n" + manualReport.trim());
        System.out.println("Manual open/closed: " + opened + "/" + closed);
        int manualOpen = opened, manualClosed = closed;
        opened = closed = 0;
        String resourceReport = resources(files);
        System.out.println("Try-with-resources report:\n" + resourceReport.trim());
        System.out.println("Try-with-resources open/closed: " + opened + "/" + closed);
        System.out.println("Reports identical: " + manualReport.equals(resourceReport));
        try (PrintWriter p = new PrintWriter("summary.txt")) { p.print(resourceReport); }
        System.out.println("Manual leaked handles on failing input: " + (manualOpen - manualClosed));
        long manualNanos = 0, resourceNanos = 0;
        for (int i = 0; i < 3; i++) {
            long start = System.nanoTime(); manual(files); manualNanos += System.nanoTime() - start;
            start = System.nanoTime(); resources(files); resourceNanos += System.nanoTime() - start;
        }
        System.out.println("Average report time ns: manual=" + manualNanos / 3
                + ", try-with-resources=" + resourceNanos / 3);
        closeOrderDemo();
        suppressedDemo();
    }
}
