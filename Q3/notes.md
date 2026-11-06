# Q3 – Daily Sales Report Generator

## 1. Problem summary
Six input files are read, one containing corrupt data. Manual closing and
try-with-resources produce the same report in `summary.txt`.

## 2. Shared Group Work
`SalesReportDemo` generates inputs, runs both versions, compares their strings,
and writes the resource-safe report. A corrupt line is reported rather than
aborting the complete run.

## Student A
Breakpoints belong at file-open, each `readLine`, parse, catch, and close. A
failure between open and `finally` close is the leak point in a version that
does not close in `finally`; this implementation's `finally` closes every
opened reader. Logging can record `file` in the catch block.

## Student B
Every `FileReader` and `BufferedReader` must close. Try-with-resources closes
resources in reverse declaration order, even during exceptions. Two
AutoCloseables can print from `close()` to demonstrate this; close failures are
available through `getSuppressed()`.

## Joint question
Try-with-resources does not make an incomplete output atomic, close
non-AutoCloseable resources, or design retry/recovery logic. Use a temporary
file and atomic move for complete reports.

## Alternative Concept and Complexity Analysis
For `f` files, both approaches are O(f) (plus file bytes). Correct manual
`finally` has at most one reader open; an unsafe no-close variant can hold O(f)
handles. Try-with-resources is clearer and guarantees closure.
