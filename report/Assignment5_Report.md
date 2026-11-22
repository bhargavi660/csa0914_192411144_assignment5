# CSA09 CO5 Assignment 5 Report

## Cover Page
- Student Name: Bhargavi N.
- Register Number: 192411144
- Course: Programming in Java (CSA09)
- Assignment: Assignment 5 - Data Persistence Engineering: Files and Databases
- Repository: CSA09-192411144-Assignment5

## 1. Assignment Overview
The project demonstrates text-file persistence, buffered I/O, DAO design, serialization, JDBC transactions, SQL injection prevention, bulk loading, migration and engineering evaluation.

## 2. Part A
### A1 Bus-Pass File Format
The text format uses seven pipe-separated fields. Validity date is an ISO-style date and amount is decimal text. Binary format uses `DataOutputStream` and UTF fields plus a double. Damaged text lines are counted and skipped. Missing files throw clear I/O errors. Permanent writes should use temporary files and atomic replacement.

### A2 Buffered I/O
One-byte reads are not efficient for 50 MB logs. Fixed 64 KB buffering or byte-array reads reduce method and OS-related overhead. `readAllLines()` is easiest but memory-intensive: O(n) memory.

### A3 DAO
`Dao<T,K>` declares insert, find-by-ID, find-all, update and delete. `StudentDao` uses `PreparedStatement` for caller values and try-with-resources for resources. The register number is a stable primary key.

### A4 Serialization
Name and register number may be serialized. Password and database connection are transient. Java serialization preserves object graphs but is Java-specific and sensitive to schema changes. `serialVersionUID` stabilizes compatibility; deserialization of untrusted input is unsafe.

### A5 Transactions
Both hostel debit and canteen credit are placed under `setAutoCommit(false)`. Commit occurs only after both updates; any failure rolls back and restores the original auto-commit setting. Unsafe string-concatenated login SQL is vulnerable to injected quotes.

## 3. Part B
### B1 Bus-Pass Store
`PassStore` implements add, find and route listing. Its parser requires exactly seven fields, skips blank lines, rejects missing IDs and nonnumeric amounts, and returns `null` for a damaged or absent ID.

**Actual test result**
- P1003 was found.
- P1002 returned null.
- Valid route R12 records were listed.
- 4 valid records and 4 damaged/skipped cases were observed.

**Search alternatives**
- Scan: O(n) per search; q searches O(q*n).
- HashMap startup index: O(n) startup and O(1) average lookup; O(n) memory.

**Actual performance**
| Test | Size/count | Method | Time |
|---|---:|---|---:|
| Line count | 1 MB | unbuffered | 2895 ms |
| Line count | 1 MB | buffered | 32 ms |
| Line count | 10 MB | unbuffered | 42900 ms |
| Line count | 10 MB | buffered | 52 ms |
| Line count | 50 MB | unbuffered | 179624 ms |
| Line count | 50 MB | buffered | 164 ms |
| Search | 1000 records | scan | 25 ms |
| Search | 10000 records | scan | 48 ms |
| Search | 100000 records | scan | 141 ms |

These values are actual measurements from this machine and should not be extrapolated as laboratory guarantees.

### B2 Login and Adversarial Input
`LoginDemo` separates unsafe string SQL from safe `PreparedStatement` SQL. The safe query treats the crafted input as one literal value. The damaged-data test independently verifies field-count, amount and blank-line handling.

MySQL login execution is pending because MySQL80 is stopped and this process cannot start it without elevation.

### B3 JDBC
`PassLoader` tests Statement, PreparedStatement and PreparedStatement batches at 10, 1,000, 10,000 and 100,000 rows. Batch processing is transaction-managed and uses 1,000-row batches. The class also demonstrates metadata, ResultSet verification and rollback after a controlled failure.

**Measured database timings:** pending because MySQL is unavailable.

The implementation must not claim database measurements that were not executed.

### B4 Migration
`MigrationProgram` reads and validates the text file, batches valid records and executes one transaction. Invalid rows are counted, and DatabaseMetaData reports product, version, driver and table names.

## 4. Database Design
`bus_pass` uses `pass_id` as primary key. `idx_bus_pass_route` and `idx_bus_pass_register` support route and register searches. `users` has a unique username and is only a harmless local test table.

## 5. Engineering Comparison
| Option | Concurrency | Search | Consistency | Security | Scalability | Backup |
|---|---|---|---|---|---|---|
| Text | Poor | Sequential/index | Weak | Limited | Limited | File copy |
| Java serialization | Poor | Java object graph | Weak | Unsafe for untrusted data | Weak | File copy |
| One MySQL database | Strong | Indexed SQL | Strong transactions | JDBC/ACL support | Strong | Engine backup/recovery |
| Separate serialized campus files | Very poor | Per-file | Divergent | Weak | Poor | Many copies |

For 12 campuses and 100 times the current volume, a shared relational database is recommended. It supports concurrent counters, indexed searches, transactions, centralized security and central backup. Network availability and database security must be managed with least-privilege accounts.

## 6. SQL Injection and Transactions
`PreparedStatement` with parameter binding prevents the quoted-login injection by treating input as data. Prepared batches and a transaction are recommended for nightly imports. One-pass counter issuance should use a prepared insert for the individual row, with duplicate/validation checks.

## 7. Screenshot Placeholders
Use exact names in `screenshots/SCREENSHOT_CHECKLIST.md`.

## 8. Conclusion
The file store and independent Java tests execute successfully. Database and JDBC execution remain manual pending a MySQL 8.0 administrator start and successful credential validation.
