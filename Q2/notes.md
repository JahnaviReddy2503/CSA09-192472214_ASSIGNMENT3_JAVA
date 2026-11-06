# Q2 – Online Examination Portal

## 1. Problem summary
The controller, service, and storage layers propagate meaningful checked
exceptions. Storage wraps `IOException` while preserving it as the cause.

## 2. Shared Group Work
`ExamException` is the checked root; its three subclasses model recoverable
exam-domain failures. The controller is the UI boundary and always runs
`finally`.

## Student A
```text
ExamException
├── ExamWindowClosedException
├── StudentNotRegisteredException
└── SubmissionStorageException
```
Checked exceptions force callers to acknowledge failures that can be recovered
from. An invalid argument would be an unchecked `IllegalArgumentException`.
The path is Storage IOException → SubmissionStorageException → Service →
Controller. The student sees the wrapper message, while logs can inspect cause.

## Student B
`ExamPortalDemo` runs registered/open, unregistered, closed-window, and quota
scenarios. `StorageDemo` produces the required save/error/cause messages (plus
the controller's request-completion line). Without passing `e` to the wrapper
constructor, `getCause()` is null and `getCause().getMessage()` throws a
`NullPointerException`; the chained stack trace would otherwise contain
`Caused by: java.io.IOException`.

## Joint question
The domain exception prevents storage implementation details leaking into the
UI and gives callers a stable recovery and user-facing message.

## Alternative Concept and Complexity Analysis
Making the hierarchy extend `RuntimeException` removes `throws` declarations
and compiler-enforced handling, but does not remove runtime failure. Each
exception is O(1) space plus stack frames; a cause adds another throwable and
its frames. Benchmark with `System.nanoTime()` on the target JVM rather than
inventing timings.
