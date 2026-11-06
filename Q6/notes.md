# Q6 – Deadlock and Inter-Thread Communication

## 1. Problem summary
The bounded token buffer uses `wait`/`notifyAll` and a while guard. Transfers
use a consistent account-id order to prevent circular waiting.

## 2. Shared Group Work
Twenty tokens are produced and consumed by two students; the expected sum is
210. The fixed transfer implementation is bounded and cannot hang.

## Student A
`wait()` must be inside synchronized code and in a loop because wakeups can be
spurious and another consumer may win the condition. `notifyAll()` safely wakes
all waiters; `sleep()` keeps a lock, while `wait()` releases it.

## Student B
The deadlock pair is A→B and B→A when each locks its first account first.
The Coffman conditions are mutual exclusion, hold-and-wait, no preemption, and
circular wait. Ordering breaks circular wait. A real deadlock demo should be
bounded with `ThreadMXBean.findDeadlockedThreads()` and interrupted/terminated
after reporting.

## Joint question
Both problems require progress under contention. Lock ordering prevents cycles;
the buffer's condition loops and notifications prevent consumers/producers from
proceeding in an invalid state.

## Alternative Concept and Complexity Analysis
`ArrayBlockingQueue<Integer>(5)` encapsulates the buffer safely. `tryLock` with a
timeout avoids waiting forever. Each operation is O(1), space is O(5), and these
library designs are less error-prone.
