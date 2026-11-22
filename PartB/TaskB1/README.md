# Task B1 - Bus-Pass Text File Store

## Compile
```powershell
javac -d out .\src\*.java
java -cp out assignment5.b1.PassStoreDemo
```

## Test input
`input/passes.txt` contains valid records and damaged records: missing pass ID, wrong field count, nonnumeric amount and blank line.

## Benchmark
```powershell
java -cp out assignment5.b1.PassBenchmark
```
Actual CSV output is written to `measurements/performance.csv`. The benchmark generates 1 MB, 10 MB and 50 MB attendance logs and 1,000, 10,000 and 100,000-pass datasets.

## Search complexity
- Sequential scan: O(q*n) for q searches, O(n) file/stream memory.
- Loading a `HashMap`: O(n) startup, O(1) average lookup, O(n) memory.
