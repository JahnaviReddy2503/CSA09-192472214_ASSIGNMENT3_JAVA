# Q5 – Cinema Seat Booking

## 1. Problem summary
One hundred threads attempt ten seats. The unsafe check-then-set deliberately
contains a yield; synchronized method, synchronized block, and CAS alternatives
make the operation atomic.

## 2. Shared Group Work
`bookUnsafe`, `book`, `bookBlock`, and an `AtomicBoolean[]` implementation are
measured in one unattended program.

## Student A
The critical section is the test and the corresponding set/increment. Two
threads can both observe false before either sets true, causing double counting.

## Student B
Method-level locking and block locking both use this object's intrinsic lock;
the block can limit the locked region. Static synchronization is unnecessary
because each screen has its own `SeatBooking`; static locking would serialize
all screens.

## Joint question
For twenty screens, use one object/lock per screen so unrelated bookings proceed
in parallel.

## Alternative Concept and Complexity Analysis
CAS gives O(1) time and O(s) space for `s` seats, without blocking a monitor.
`ReentrantLock` is another option; `putIfAbsent` is suitable when the seat map
itself is concurrent.
