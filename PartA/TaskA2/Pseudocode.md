# A2 Buffered I/O

For a 50 MB log, reading one byte at a time with `InputStream.read()` performs at least one method call per byte: about 50 MB of calls, excluding internal JVM/JDK operations. The result is poor throughput because repeated calls and possible OS/JDK crossings dominate.

## Buffered redesign
`BufferedInputStream` uses an internal byte array and fills a block in a few large reads. An equivalent can be:

```java
try (InputStream raw = Files.newInputStream(path);
     BufferedInputStream in = new BufferedInputStream(raw, 64 * 1024)) {
    int value;
    while ((value = in.read()) != -1) {
        // Process value.
    }
}
```

A byte array loop is also efficient:

```java
try (InputStream in = Files.newInputStream(path)) {
    byte[] buffer = new byte[64 * 1024];
    int count;
    while ((count = in.read(buffer)) != -1) {
        // Process buffer[0] through buffer[count - 1].
    }
}
```

## When buffering helps little
The benefit is small when the input is already decoded/processed as one complete network or database response, or when the application already reads large blocks and does almost no per-byte method calls. A small, one-time local file may also be dominated by page-cache or startup costs.

## Files.readAllLines()
`Files.readAllLines()` is easiest but loads every line and associated Java `String` objects into memory. Use it for small/moderate files, not a 50 MB log on a memory-limited process. `Files.lines()` also processes lazily and is more memory-efficient, but it should be closed as a stream.

Time complexity is O(n) for all approaches. Extra memory is O(b) for a fixed buffer and O(n) for `readAllLines()`; lazy line streaming is O(l) for the longest retained line.
