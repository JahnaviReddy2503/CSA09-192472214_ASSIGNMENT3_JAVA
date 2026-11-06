class ExamException extends Exception {
    ExamException(String message) { super(message); }
    ExamException(String message, Throwable cause) { super(message, cause); }
}
class ExamWindowClosedException extends ExamException {
    ExamWindowClosedException(String message) { super(message); }
}
class StudentNotRegisteredException extends ExamException {
    StudentNotRegisteredException(String message) { super(message); }
}
class SubmissionStorageException extends ExamException {
    SubmissionStorageException(String message, Throwable cause) { super(message, cause); }
}

public class ExamPortalDemo {
    static class Storage {
        void save(String file) throws SubmissionStorageException {
            try {
                if (file.contains("quota")) throw new java.io.IOException("Disk quota exceeded");
                System.out.println("Saved " + file);
            } catch (java.io.IOException e) {
                throw new SubmissionStorageException("Could not store " + file, e);
            }
        }
    }
    static class Service {
        private final Storage storage = new Storage();
        void submit(String student, boolean registered, boolean open, String file)
                throws ExamException {
            if (!registered) throw new StudentNotRegisteredException("Student not registered");
            if (!open) throw new ExamWindowClosedException("Exam window is closed");
            storage.save(file);
        }
    }
    static class Controller {
        void handle(String student, boolean registered, boolean open, String file) {
            try {
                new Service().submit(student, registered, open, file);
            } catch (ExamException e) {
                System.out.println("Error: " + e.getMessage());
                if (e.getCause() != null) System.out.println("Cause: " + e.getCause().getMessage());
            } finally {
                System.out.println("Request finished");
            }
        }
    }
    public static void main(String[] args) {
        Controller c = new Controller();
        c.handle("A", true, true, "answer01.pdf");
        c.handle("B", false, true, "answer02.pdf");
        c.handle("C", true, false, "answer03.pdf");
        c.handle("D", true, true, "quota-answer04.pdf");
    }
}
