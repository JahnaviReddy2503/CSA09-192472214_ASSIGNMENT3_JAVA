# Q6 – Deadlock and Inter-Thread Communication

## 1. Problem summary
Part A demonstrates a bounded deadlock and detects it with
`ThreadMXBean.findDeadlockedThreads()`, then fixes transfers with account ordering.
Part B implements a capacity-five producer-consumer buffer.

## 2. Shared Group Work
Input is `tokens=20 consumers=2 capacity=5`. The while version logged sizes
between 0 and 1 in this run and printed the required sum 210.

## Student A
`wait()` is called while synchronized and in a while loop because wakeups can be
spurious and another waiter may consume the condition first. `notifyAll()` wakes
all producers/consumers; `sleep()` keeps a lock, whereas `wait()` releases it.
The output's buffer-size log demonstrates the guarded range 0..1, within the
capacity-five bound. The real while-to-if experiment used one consumer and
produced `while->if sums: 210 = 210`, also staying within 0..5.

## Student B
The deadlock pair locks A then B and B then A. Coffman conditions: mutual
exclusion, hold-and-wait, no preemption, and circular wait. Sorting locks by
account id breaks circular wait; output reported `deadlock detected: true` and
the fixed balances remained `100, 100`.

## Joint answer
Both cases involve contention over shared state. Lock ordering prevents cycles;
condition loops and notifications ensure producer-consumer progress.

## Alternative Concept and Complexity Analysis
`ArrayBlockingQueue<Integer>(5)` encapsulates the buffer with O(1) put/take and
O(5) space. Its measured three-run average for 20 puts/takes was **4,619,166 ns**.
`tryLock(timeout)` can provide bounded transfer waiting and is less error-prone
than hand-written monitor protocols.
