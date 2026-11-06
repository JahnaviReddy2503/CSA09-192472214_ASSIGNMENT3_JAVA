# Q1 – ATM Withdrawal

## 1. Problem summary
The ATM validates an account and amount, then withdraws from a fixed balance. It
illustrates input, argument, custom checked, and file exceptions.

## 2. Shared Group Work
`AtmDemo` uses try-with-resources, multi-catch, ordered catches, and `finally`.
The custom checked exception is in `InsufficientFundsException.java`.

## Student A
```text
Throwable
├── Error (StackOverflowError)
└── Exception
    ├── RuntimeException (InputMismatchException, IllegalArgumentException)
    └── checked (InsufficientFundsException, FileNotFoundException)
```

| Failure | Type | Checked? | Catch |
|---|---|---|---|
| letters | InputMismatchException | No | multi-catch |
| negative | IllegalArgumentException | No | argument catch |
| over balance | InsufficientFundsException | Yes | custom catch |
| missing reader | FileNotFoundException | Yes | file catch |

Specific catches precede broad catches because a superclass catch would make a
later subclass catch unreachable. `finally` executes after success or handled
failure (apart from abnormal JVM termination).

## Student B

| Input | Catch block | Finally |
|---|---|---|
| `101 250` | none | yes |
| `abc 250` | multi-catch | yes |
| `101 -5` | IllegalArgumentException | yes |
| `101 1500` | custom | yes |
| `101 0` | FileNotFoundException | yes |

An uncaught exception would print its class, message, and stack frames from the
throw site back through the caller. A broad `catch (Exception e)` before a
narrow catch produces the compiler error `exception ... has already been caught`.

## Joint question
One `Exception` catch is acceptable at an application boundary for logging and
safe user feedback, but it loses precise recovery and can hide programming bugs.

## Alternative Concept and Complexity Analysis
`hasNextInt()` and explicit error codes avoid throwing but require checks at every
failure point. Both approaches are O(1) per withdrawal and O(1) space. Exception
construction captures a stack trace and is measurably more expensive than a
return code; timings are intentionally not hard-coded and can be collected with
`System.nanoTime()` on the target machine.
