public class StorageDemo {
    public static void main(String[] args) {
        ExamPortalDemo.Controller controller = new ExamPortalDemo.Controller();
        controller.handle("A", true, true, "answer01.pdf");
        try {
            new ExamPortalDemo.Storage().save("answer02.pdf");
        } catch (SubmissionStorageException e) {
            System.out.println("Error: " + e.getMessage());
            System.out.println("Cause: " + e.getCause().getMessage());
            e.printStackTrace(System.out);
        }
    }
}
