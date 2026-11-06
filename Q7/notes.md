# Q7 – Concurrent Fault-Tolerant Processor

## 1. Problem summary
The generator creates 10,000 score paths with corrupt and missing inputs.
Callable tasks return a `Result` instead of allowing one bad file to abort the run.

## 2. Shared Group Work
The run used `fileCount=10000`, `corruptEvery=50`, `missingEvery=75`, and pools
1, 2, 4, 8. `ConcurrentLinkedQueue` stores one result per submitted path.

## Student A
```text
Controller -> ExecutorService -> Callable<Result> -> process(file)
                                      |
                              ConcurrentLinkedQueue
```
Pseudocode: submit one callable per expected filename; each catches missing,
non-numeric, range, and I/O errors; collect every Future; aggregate by reason.
The queue suits append-only concurrent result collection.

## Student B

| Pool | Average of 3 runs (ms) | Successes | Failures |
|---:|---:|---:|---:|
| 1 | 548 | 9,733 | 267 |
| 2 | 231 | 9,733 | 267 |
| 4 | 150 | 9,733 | 267 |
| 8 | 130 | 9,733 | 267 |

Final reasons were `missing=133`, `non-numeric=134`; success + failure =
`9733 + 267 = 10000`. The ten-file hand check expected seven successes and
observed seven, with one each missing, non-numeric, and out-of-range. No failure
escaped task handling.

## Joint answer
The shared design meets the measurable requirement: every expected path produces
one result and no task failure aborts the run.

## Stage 4 – Joint
Pool 4 is a reasonable I/O compromise for this run; more workers can cause disk
contention and context switching. One result per submitted path makes the
summary complete. A limitation is that files can change while processing.

## Alternative Concept and Complexity Analysis
`parallelStream()` also returned 9,733 successes and 267 failures and measured
**214 ms**. Its lambda still catches errors through `process`. A one-thread-per-
file design would use O(n) stacks, versus O(p) worker stacks for a fixed pool;
processing and result storage are O(n).
