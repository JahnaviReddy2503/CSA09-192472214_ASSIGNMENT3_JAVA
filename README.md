# CSA09 Programming in Java – Assignment 3

**Name:** Jahnavi Reddy  
**Register Number:** 192472214  
**Java:** OpenJDK 17.0.21 LTS

Every question uses the default package. Run commands from the indicated folder:

| Question | Compile and run | Actual result |
|---|---|---|
| Q1 | `javac *.java` then `Get-Content input.txt \| java AtmDemo > output.txt 2>&1` | Five inputs exercised; every case printed `finally executed`. The throw benchmark averaged 38,758,166 ns versus 67,800 ns for error codes. |
| Q2 | `javac *.java` then `java StorageDemo > output.txt 2>&1`; portal scenarios: `Get-Content input.txt \| java ExamPortalDemo > portal-output.txt 2>&1` | Storage saved answer01, reported quota failure and printed a real `Caused by:` chain. Portal covered closed, unregistered, quota, and valid scenarios. |
| Q3 | `javac *.java` then `Get-Content input.txt \| java SalesReportDemo > output.txt 2>&1` | Reports were identical; manual closing was 6/5 and try-with-resources was 6/6. The measured averages were 1,281,133 ns and 1,326,533 ns. |
| Q4 | `javac *.java` then `Get-Content input.txt \| java ThreadDemo > output.txt 2>&1` | Four stations processed 445 scripts; ten finish-order runs, lifecycle states, priority, daemon, and Runnable evidence were captured. |
| Q5 | `javac *.java` then `Get-Content input.txt \| java SeatBooking > output.txt 2>&1` | Unsynchronized runs sold 31–91 seats; both synchronized versions and AtomicBoolean sold exactly 10. |
| Q6 | `javac *.java` then `Get-Content input.txt \| java TokenCounter > output.txt 2>&1` | Deadlock was detected, ordered transfers preserved balances, and the while buffer collected exactly 210. |
| Q7 | `javac *.java` then `Get-Content input.txt \| java ProcessorDemo > output.txt 2>&1` | All 10,000 paths completed: 9,733 successes and 267 handled failures; pool averages were 548/231/150/130 ms and parallelStream took 214 ms. |

`input.txt`, source, compiled classes, notes, and real outputs are retained in
each `Q1`–`Q7` folder. Q7 also generates `scores` and `small-scores`.
