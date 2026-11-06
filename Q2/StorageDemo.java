public class StorageDemo {
    public static void main(String[] args) {
        new ExamPortalDemo.Controller().handle("A", true, true, "answer01.pdf");
        new ExamPortalDemo.Controller().handle("A", true, true, "quota-answer02.pdf");
    }
}
