public class StorageDemo {
    public static void main(String[] args) {
        try {
            new ExamPortalDemo.Storage().save("answer01.pdf");
        } catch (SubmissionStorageException e) {
            throw new AssertionError(e);
        }
        try {
            new ExamPortalDemo.Storage().save("answer02.pdf");
        } catch (SubmissionStorageException e) {
            System.out.println("Error: " + e.getMessage());
            System.out.println("Cause: " + e.getCause().getMessage());
        }
    }
}
