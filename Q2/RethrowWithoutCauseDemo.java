public class RethrowWithoutCauseDemo {
    public static void main(String[] args) {
        try {
            throw new java.io.IOException("Disk quota exceeded");
        } catch (java.io.IOException e) {
            SubmissionStorageException wrapper =
                    new SubmissionStorageException("Could not store answer.pdf", null);
            System.out.println("Cause is null: " + (wrapper.getCause() == null));
            try {
                System.out.println(wrapper.getCause().getMessage());
            } catch (NullPointerException expected) {
                System.out.println("getCause().getMessage() caused NullPointerException");
            }
        }
    }
}
