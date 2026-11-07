# Q5 – Cinema Seat Booking

## 1. Problem summary
One hundred users contend for ten seats. The check-then-set race is compared
with a synchronized method, a synchronized block, and AtomicBoolean.

## 2. Shared Group Work
The program reads `seats=10 users=100 bookingsForTiming=10000`, runs ten race
trials, and measures each strategy over three runs.

## Student A
The critical section is the test, assignment, and increment. Two threads can
both read `false`, pause, then both increment; the ten real unsynchronized
results were:

| Run | Seats sold |
|---:|---:|
| 1 | 53 |
| 2 | 31 |
| 3 | 70 |
| 4 | 82 |
| 5 | 76 |
| 6 | 68 |
| 7 | 85 |
| 8 | 91 |
| 9 | 78 |
| 10 | 66 |

Thus double-booking is visible and the count can exceed ten.

## Student B
Both synchronized forms use the intrinsic lock `this`; method locking covers
the whole method, while block locking scopes only the critical section. Static
synchronization is not needed because each screen has its own object/lock.

| Version | Average microseconds |
|---|---:|
| unsynchronized | 2,479,328 |
| synchronized method | 3,419,240 |
| synchronized block | 3,356,830 |

These are the captured three-run averages for 10,000 booking attempts.

## Joint answer
For twenty screens, use one `SeatBooking` object and lock per screen, so
independent screens do not serialize one another.

## Alternative Concept and Complexity Analysis
`AtomicBoolean[]` uses compare-and-set in O(1) per booking and O(s) space for
`s` seats. The real alternative sold 10 seats. A `ReentrantLock` or
`ConcurrentHashMap.putIfAbsent` can provide the same atomic claim semantics.
