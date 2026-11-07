# CSA09 Programming in Java – Assignment 3

## Joint Report

**Name:** Jahnavi Reddy  
**Register Number:** 192472214  
**Course:** CSA09 Programming in Java  
**Repository:** https://github.com/JahnaviReddy2503/CSA09-192472214_ASSIGNMENT3_JAVA

---

## Q1 – ATM Withdrawal

### Student A
`AtmDemo` demonstrates input, argument, custom checked, and file failures.
The hierarchy is `Throwable`, `Error`, `Exception`, `RuntimeException`, and
checked exceptions including `InsufficientFundsException` and
`FileNotFoundException`. Narrow catches precede broad catches. The real cases
were:

| Input | Catch hit | Finally |
|---|---|---|
| `101 250` | none | yes |
| `abc 250` | multi-catch | yes |
| `101 -5` | IllegalArgumentException | yes |
| `101 1500` | InsufficientFundsException | yes |
| `101 0` | FileNotFoundException | yes |

### Student B
The saved `uncaught-output.txt` stack trace identifies the exception type,
message, and `UncaughtAtmDemo.java:3` throw line. The broader-before-narrower
example records the compiler error that `FileNotFoundException` was already
caught.

### Joint
A single `catch (Exception e)` is suitable at an application boundary for
logging and safe feedback, but loses precise recovery and can hide programming
errors.

### Alternative Concept and Complexity Analysis
`hasNextInt()` with `-1` error codes needs explicit checks. Both approaches are
O(1) time and O(1) space per case. The saved benchmark averaged **38,758,166
ns** for 100,000 throws versus **67,800 ns** for error-code returns.

---

## Q2 – Online Examination Portal

### Student A
The checked hierarchy is:

```text
ExamException
├── ExamWindowClosedException
├── StudentNotRegisteredException
└── SubmissionStorageException
```

The propagation path is `IOException` → `SubmissionStorageException` →
Service → Controller. The controller catches the domain exception and executes
`finally`.

### Student B

| Scenario | Observed result |
|---|---|
| closed window | `ExamWindowClosedException` |
| unregistered student | `StudentNotRegisteredException` |
| large/quota file | `SubmissionStorageException`, cause `Disk quota exceeded` |
| valid file | saved successfully |

The saved portal trace contains `Caused by: java.io.IOException`. The
no-cause demo output is:

```text
Cause is null: true
getCause().getMessage() caused NullPointerException
```

### Joint
`SubmissionStorageException` gives the UI a stable domain message without
exposing storage details while logs retain the original cause.

### Alternative Concept and Complexity Analysis
Using `RuntimeException` would remove mandatory caller catches, while runtime
failures would still occur. Construction is O(1) plus stack depth, and a cause
retains another throwable and stack. The saved averages were **30,521,133 ns**
without causes and **61,387,233 ns** with causes.

---

## Q3 – Daily Sales Report

### Student A
The program reads five stores and one corrupt store. Manual closing counted
`6/5` handles and try-with-resources counted `6/6`; the corrupt file failed
while parsing.

### Student B
The reader wrapper is the resource and must close after every file. The saved
suppressed-exception demonstration reports `suppressed count=1`, and the
try-with-resources close order is `close second` followed by `close first`.

### Joint
Try-with-resources does not make output atomic, close non-`AutoCloseable`
resources, or design retries. Temporary output plus atomic move is needed for a
complete report.

### Alternative Concept and Complexity Analysis
For `f` files, both approaches are O(f) plus bytes read. Correct approaches
use O(1) handles; unsafe no-close code can use O(f). Saved averages were
**1,281,133 ns** manual and **1,326,533 ns** try-with-resources.

---

## Q4 – Thread Creation and Life Cycle

### Student A
Four stations processed `120 + 95 + 150 + 80 = 445`. The observed lifecycle
was `NEW -> RUNNABLE -> TIMED_WAITING -> TERMINATED`; direct `run()` printed
`main`, while `start()` used a worker. `join()` waited for termination.

### Student B

| Runs | Observed finish order |
|---|---|
| 1–10 | Station-4, Station-2, Station-1, Station-3 |

Priority 10 and priority 1 both completed, so priority did not establish
order. The saved daemon evidence recorded `daemon=true` while alive.

### Joint
Scheduling, sleep timing, CPU load, and priority make order nondeterministic.
Correctness relies on joins and synchronization, never print order.

### Alternative Concept and Complexity Analysis
A fixed `ExecutorService` bounds workers to `p`; work is approximately `n/p`
batches and memory is O(p) stacks. The saved three-run executor average was
**2,320,600 ns**.

---

## Q5 – Cinema Seat Booking

### Student A
The check, assignment, and increment form the critical section. Ten saved
unsynchronized runs showed the race:

| Run | Seats sold |
|---:|---:|
| 1 | 59 |
| 2 | 12 |
| 3 | 12 |
| 4 | 22 |
| 5 | 18 |
| 6 | 25 |
| 7 | 22 |
| 8 | 24 |
| 9 | 28 |
| 10 | 24 |

### Student B
The synchronized method locks the whole method, while the synchronized block
locks only the critical section. Both sold exactly 10. Timing for 10,000
bookings was:

| Version | Average microseconds |
|---|---:|
| unsynchronized | 2,479,328 |
| synchronized method | 3,419,240 |
| synchronized block | 3,356,830 |

### Joint
For twenty screens, use one `SeatBooking` object and lock per screen so
independent screens do not serialize one another.

### Alternative Concept and Complexity Analysis
`AtomicBoolean[]` uses compare-and-set in O(1) per booking and O(s) space for
`s` seats; the saved alternative sold 10 seats. `ReentrantLock` or
`ConcurrentHashMap.putIfAbsent` provides similar atomic claim semantics.

---

## Q6 – Deadlock and Inter-Thread Communication

### Student A
`wait()` is called while synchronized and guarded by a loop because wakeups
can be spurious and another waiter may consume the condition. `notifyAll()`
wakes producers and consumers; `sleep()` keeps a lock while `wait()` releases
it. The saved buffer sizes stayed between 0 and 1, within capacity 5. The
real `while→if` experiment printed `while->if sums: 210 = 210`.

### Student B
The deadlock pair locks A then B and B then A, satisfying mutual exclusion,
hold-and-wait, no preemption, and circular wait. Account-id lock ordering
breaks circular wait. The saved output reports `deadlock detected: true` and
fixed balances `100, 100`.

### Joint
Lock ordering prevents transfer cycles; condition loops and notifications
ensure producer-consumer progress.

### Alternative Concept and Complexity Analysis
`ArrayBlockingQueue<Integer>(5)` provides O(1) put/take and O(5) space. Its
saved three-run average for 20 puts/takes was **4,619,166 ns**.
`tryLock(timeout)` can provide bounded transfer waiting.

---

## Q7 – Concurrent Fault-Tolerant Processor

### Student A
The design is `Controller -> ExecutorService -> Callable<Result> ->
process(file) -> ConcurrentLinkedQueue`. One callable is submitted per
expected filename; each task catches missing, non-numeric, range, and I/O
errors and returns one result.

### Student B

| Pool | Average of 3 runs (ms) | Successes | Failures |
|---:|---:|---:|---:|
| 1 | 548 | 9,733 | 267 |
| 2 | 231 | 9,733 | 267 |
| 4 | 150 | 9,733 | 267 |
| 8 | 130 | 9,733 | 267 |

Final reasons were `missing=133` and `non-numeric=134`. The ten-file hand
check found seven successes and one each missing, non-numeric, and out-of-range.
The saved stack/error output shows no failure escaped task handling.

### Joint
Every expected path produces one result, so one bad file cannot abort the
complete summary.

### Alternative Concept and Complexity Analysis
`parallelStream()` returned 9,733 successes and 267 failures in **214 ms**.
Processing and result storage are O(n), with O(p) worker stacks for a fixed
pool; one-thread-per-file would use O(n) stacks.

---

## Individual Contribution Record

| Question | Student A – Primary Responsibility | Student B – Primary Responsibility | Joint |
|---|---|---|---|
| Q1 | Throwable hierarchy and catch ordering | Test matrix and uncaught trace | Single-catch trade-off and alternative |
| Q2 | Hierarchy and propagation design | Cause chaining and scenarios | Meaningful UI-facing exception |
| Q3 | Leak debugging and logging | Resource inventory and suppressed exceptions | Limits of try-with-resources |
| Q4 | Thread/Runnable lifecycle | Repeated ordering and priorities | Scheduler independence |
| Q5 | Race reproduction and critical section | Timing and lock comparison | Multi-screen lock strategy |
| Q6 | wait/notify producer-consumer | Deadlock conditions and ordering | Progress guarantees |
| Q7 | ExecutorService design | Measurements and fault injection | Correct complete summary |
