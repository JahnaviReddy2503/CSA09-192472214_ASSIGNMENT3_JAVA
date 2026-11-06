# Q1 – ATM Withdrawal

## 1. Problem summary
`AtmDemo` reads one case per line from `input.txt`, validates an account and
amount, and demonstrates input, argument, custom checked, and file failures.

## 2. Shared Group Work
The program uses `try`, multi-catch, ordered catches, and `finally`. The real
run handled five cases and captured the result in `output.txt`.

## Student A
```text
Throwable
├── Error (StackOverflowError)
└── Exception
    ├── RuntimeException (InputMismatchException, IllegalArgumentException)
    └── checked (InsufficientFundsException, FileNotFoundException)
```

| Failure point | Exception | Checked? | Catch |
|---|---|---|---|
| letters | InputMismatchException | unchecked | multi-catch |
| negative amount | IllegalArgumentException | unchecked | argument catch |
| amount over balance | InsufficientFundsException | checked | custom catch |
| missing card reader | FileNotFoundException | checked | file catch |

Narrow catches precede broad catches; otherwise the compiler reports
`exception FileNotFoundException has already been caught`. `finally` runs after
normal completion or a handled exception, except for abnormal JVM termination.

## Student B

| Input | Catch hit | Finally |
|---|---|---|
| `101 250` | none | yes |
| `abc 250` | multi-catch | yes |
| `101 -5` | IllegalArgumentException | yes |
| `101 1500` | InsufficientFundsException | yes |
| `101 0` | FileNotFoundException | yes |

`uncaught-output.txt` is a real stack trace: the exception header identifies the
type/message, and `UncaughtAtmDemo.java:3` identifies the throw line. The
broader-before-narrower example is in `BroadCatchBeforeNarrower.java` comments.

## Joint answer
A single `catch (Exception e)` is acceptable at an application boundary for
logging and safe feedback, but loses precise recovery and can hide programming
errors.

## Alternative Concept and Complexity Analysis
`hasNextInt()` plus `-1` error codes needs explicit checks at every input point;
both approaches are O(1) time and O(1) space per case. The captured three-run
benchmark averaged **38,758,166 ns** for 100,000 throws versus **67,800 ns** for
100,000 error-code returns. This measures this JVM run, not a universal value.
