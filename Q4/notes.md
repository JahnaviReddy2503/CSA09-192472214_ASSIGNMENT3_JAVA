# Q4 – Thread Creation and Life Cycle

## 1. Problem summary
Four stations process batches using a `Thread` subclass. A second station uses
`Runnable`, and `join()` ensures the grand total is printed last.

## 2. Shared Group Work
The program uses `start`, `sleep`, `join`, `isAlive`-equivalent lifecycle
observations, and prints thread names and states.

## Student A
`Thread` is convenient when the class is itself a thread; `Runnable` preserves
single inheritance and separates work from execution. Lifecycle:
`NEW -> RUNNABLE -> TIMED_WAITING (sleep) -> TERMINATED`. Calling `run()`
directly stays on `main`; `start()` creates a new execution path. `join()` blocks
main until each station terminates.

## Student B
Completion order varies across repeated runs because scheduling and sleep timing
vary. Priorities are hints, not ordering guarantees. A daemon station may be
terminated when all non-daemon work ends, so it must not be used for required
output.

## Joint question
Thread scheduling is nondeterministic; correctness must depend on synchronization
and joins, not output order.

## Alternative Concept and Complexity Analysis
An `ExecutorService` fixed pool bounds threads. For `n` scripts and `p` workers,
work is approximately `n/p` batches, with O(p) thread memory instead of one
stack per station/task.
