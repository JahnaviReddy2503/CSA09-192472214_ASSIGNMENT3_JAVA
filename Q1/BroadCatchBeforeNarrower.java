public class BroadCatchBeforeNarrower {
    /*
     * Uncommenting this method causes:
     * error: exception FileNotFoundException has already been caught
     *
     * catch (Exception e) { }
     * catch (java.io.FileNotFoundException e) { }
     */
    public static void main(String[] args) {
        System.out.println("See notes.md for the real compiler diagnostic and corrected order.");
    }
}
