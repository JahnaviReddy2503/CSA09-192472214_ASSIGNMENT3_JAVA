# Q3 – Daily Sales Report

## 1. Problem summary
The program reads the five stores and one corrupt store listed in `input.txt`.
Manual closing and try-with-resources produce identical reports in `summary.txt`.

## 2. Shared Group Work
`CountingReader` tracks opened/closed handles, logs the failing filename, compares
both report strings in code, and demonstrates suppressed exceptions.

## Student A
Breakpoints: constructor/open, each `readLine`, `parseInt`, catch, and close.
The corrupt file `store-corrupt.txt` fails at parsing. Real counts were manual
`6/5` (one leaked handle) and try-with-resources `6/6`; logs name that file.

## Student B
The `FileReader` wrapped by `BufferedReader`/`CountingReader` is the resource and
must close after each file. Manual code leaks when parsing throws before its
explicit close. Try-with-resources closes automatically in reverse declaration
order; the demo prints `close second` then `close first`. The suppressed demo
reports `suppressed count=1`.

## Joint answer
Try-with-resources does not make an output atomic, close non-`AutoCloseable`
resources, or design retries. Temporary output plus atomic move is needed for a
complete report.

## Alternative Concept and Complexity Analysis
For `f` files, both approaches are O(f) plus bytes read. Correct manual finally
and try-with-resources hold O(1) handles; unsafe no-close code can hold O(f).
The captured three-run averages were manual **1,281,133 ns** and
try-with-resources **1,326,533 ns**.
