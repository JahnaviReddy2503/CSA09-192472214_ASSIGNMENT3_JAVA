# CSA09 Programming in Java – Assignment 3

**Name:** Jahnavi Reddy  
**Register Number:** 192472214  
**Java:** OpenJDK 17.0.21 LTS

Every question uses the default package. Run commands from the indicated folder:

| Question | Compile and run | Actual result |
|---|---|---|
| Q1 | `javac *.java` then `Get-Content input.txt \| java AtmDemo > output.txt 2>&1` | Five inputs exercised; every case printed `finally executed`. The throw benchmark averaged 38,758,166 ns versus 67,800 ns for error codes. |
| Q2 | `javac *.java` then `java StorageDemo > storage-output.txt 2>&1`; `java RethrowWithoutCauseDemo > rethrow-output.txt 2>&1`; `Get-Content input.txt \| java ExamPortalDemo > portal-output.txt 2>&1` | Storage printed the exact three required lines; the no-cause demo printed a null cause and its real NullPointerException result; portal scenarios completed. |
| Q3 | `javac *.java` then `Get-Content input.txt \| java SalesReportDemo > output.txt 2>&1` | Reports were identical; manual closing was 6/5 and try-with-resources was 6/6. The measured averages were 1,281,133 ns and 1,326,533 ns. |
| Q4 | `javac *.java` then `Get-Content input.txt \| java ThreadDemo > output.txt 2>&1` | Four stations processed 445 scripts; ten finish-order runs, lifecycle states, priority, daemon, and Runnable evidence were captured. |
| Q5 | `javac *.java` then `Get-Content input.txt \| java SeatBooking > output.txt 2>&1` | Ten unsynchronized runs recorded 31–91 seats; both synchronized versions and AtomicBoolean sold exactly 10, with three timing measurements for 10,000 bookings. |
| Q6 | `javac *.java` then `Get-Content input.txt \| java TokenCounter > output.txt 2>&1` | Deadlock was detected without hanging, ordered transfers preserved balances, the counter stayed within 0–5, and the while→if experiment produced real results. |
| Q7 | `javac *.java` then `Get-Content input.txt \| java ProcessorDemo > output.txt 2>&1` | All 10,000 paths completed: 9,733 successes and 267 handled failures; pool averages were 548/231/150/130 ms and parallelStream took 214 ms. |

`input.txt`, source, compiled classes, notes, and real outputs are retained in
each `Q1`–`Q7` folder. Q7 also generates `scores` and `small-scores`.
