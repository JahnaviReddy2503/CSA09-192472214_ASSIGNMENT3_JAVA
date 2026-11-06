# Q4 – Thread Creation and Life Cycle

## 1. Problem summary
Four stations read counts from `input.txt`, using a `Thread` subclass. A
Runnable station, joins, states, priorities, and a daemon experiment are included.

## 2. Shared Group Work
The real run processed `120 + 95 + 150 + 80 = 445`; the grand total appears only
after all joins. `output.txt` contains ten finish-order runs.

## Student A
`Thread` is convenient when the worker is itself a thread; `Runnable` separates
work from execution and preserves inheritance. Lifecycle is:
`NEW -> RUNNABLE -> TIMED_WAITING -> TERMINATED`. The probe output observed all
running states and the `run()` call printed `main`; `start()` used a worker.
`join()` blocks the main thread until each station terminates.

## Student B

| Runs | Observed finish order |
|---|---|
| 1–10 | Station-4, Station-2, Station-1, Station-3 |

This one machine/input produced the same order ten times; it is not guaranteed.
Priority-10 and priority-1 both completed, so priority did not establish order.
The daemon output recorded `daemon=true` while it was alive; the JVM may end
without waiting for it.

## Joint answer
Scheduling, sleep timing, CPU load, and priorities make order nondeterministic.
Correctness therefore relies on joins/synchronization, never print order.

## Alternative Concept and Complexity Analysis
A fixed `ExecutorService` bounds worker count to `p`; work is approximately
`n/p` batches and memory is O(p) stacks. The captured three-run executor
alternative averaged **2,320,600 ns** for the four station tasks.
