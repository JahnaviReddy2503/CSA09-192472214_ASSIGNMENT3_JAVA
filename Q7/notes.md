# Q7 – Concurrent, Fault-Tolerant Processor

## 1. Problem summary
The generator creates 10,000 score files, including corrupt, out-of-range, and
missing inputs. Each `Callable<Result>` catches its own failures.

## 2. Shared Group Work
`ExecutorService` and `Future<Result>` process all files. A
`ConcurrentLinkedQueue` preserves every result without requiring a shared
mutable counter. The invariant is successes + failures = 10,000.

## Student A
Design: Controller → fixed ExecutorService → Callable task → `Result`; each task
maps missing, invalid, I/O, and range failures to a reason. The queue is
appropriate because each result is appended once and final aggregation is a
linear scan.

## Student B
The program measures pools 1, 2, 4, and 8 on the actual machine and injects
corrupt and missing files. A single failure is returned, never thrown through
the whole run. Exact times are emitted by the run, not fabricated here.

## Joint question
Pool size 2–4 is often a reasonable I/O compromise; more threads can add
context switching and disk contention. Complete collection of one result per
submitted file guarantees a complete summary. A remaining limitation is that
the input directory can change during processing.

## Alternative Concept and Complexity Analysis
`parallelStream()` offers less explicit lifecycle control; one bad unchecked
exception can fail the aggregate unless handled inside the lambda. One-thread
per-file uses O(n) stacks, while the pool uses O(p) worker stacks. Processing is
O(n) tasks and O(n) result storage.
