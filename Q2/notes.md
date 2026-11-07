# Q2 – Online Examination Portal

## 1. Problem summary
Controller, Service, and Storage layers propagate meaningful checked exceptions.
Storage wraps `IOException` as `SubmissionStorageException` and preserves its cause.

## 2. Shared Group Work
`ExamPortalDemo` reads four scenarios from `input.txt`; `StorageDemo` produces
exactly the required save/error/cause lines.

## Student A
```text
ExamException
├── ExamWindowClosedException
├── StudentNotRegisteredException
└── SubmissionStorageException
```
These are checked because callers can recover or report them. A programming
precondition such as a null argument would normally be unchecked. The path is
Storage `IOException` → `SubmissionStorageException` → Service → Controller;
Controller catches it and `finally` prints `Request finished`.

## Student B

| Scenario | Observed result |
|---|---|
| closed window | `ExamWindowClosedException` |
| unregistered student | `StudentNotRegisteredException` |
| large/quota file | `SubmissionStorageException`, cause `Disk quota exceeded` |
| valid file | saved successfully |

The portal output contains `Caused by: java.io.IOException`. The separate
`RethrowWithoutCauseDemo` prints `Cause is null: true`; without a cause, a
`Cause:` line would print no underlying message, and calling
`getCause().getMessage()` produces the observed `NullPointerException`.

## Joint answer
`SubmissionStorageException` gives the UI a stable domain message without
exposing storage implementation details, while logs retain the original cause.

## Alternative Concept and Complexity Analysis
If the classes extended `RuntimeException`, `throws ExamException` declarations
and mandatory caller catches could be removed; runtime failures would still
occur. Construction is O(1) plus stack depth, and a cause retains another
throwable/stack. The real portal benchmark averaged **30,521,133 ns** for
100,000 no-cause exceptions and **61,387,233 ns** with causes.
