# Programming in Java - Assignment 5 (CSA09 - CO5)

**Student:** Bhargavi N.  
**Register Number:** 192411144  
**Course:** Programming in Java (CSA09)  
**Theme:** Data Persistence Engineering - Files and Databases  
**Repository:** CSA09-192411144-Assignment5

## Environment
- Java: OpenJDK/Oracle Java 26.0.2.1
- `javac`: 26.0.2.1
- Operating system: Windows 11
- MySQL: MySQL 8.0 service installed, currently stopped and not startable by the unprivileged VS Code process
- JDBC connector: `lib/mysql-connector-j-9.4.0.jar`

## Project structure
The project contains `PartA/` design tasks, `PartB/` executable Java tasks, `database/` SQL scripts, `screenshots/`, `report/`, `lib/` and task-specific input/output/measurement folders.

## MySQL setup
1. Start the `MySQL80` service using a Windows administrator terminal:
   `net start MySQL80`
2. Set environment variables in the assignment terminal:
   ```powershell
   $env:DB_URL="jdbc:mysql://localhost:3306/bus_pass_assignment5"
   $env:DB_USER="root"
   $env:DB_PASSWORD="<local-password>"
   ```
3. Run the SQL scripts in numeric order.

Credentials are read from environment variables. No real password is stored in source code or SQL.

## Compile and run
Task B1:
```powershell
javac -d PartB\TaskB1\out (Get-ChildItem PartB\TaskB1\src\*.java).FullName
java -cp PartB\TaskB1\out assignment5.b1.PassStoreDemo PartB\TaskB1\input\passes.txt
java -cp PartB\TaskB1\out assignment5.b1.PassBenchmark PartB\TaskB1\measurements
```

Task B2:
```powershell
javac -d PartB\TaskB2\out -cp lib\mysql-connector-j-9.4.0.jar (Get-ChildItem PartB\TaskB2\src\*.java).FullName
java -cp "PartB\TaskB2\out;lib\mysql-connector-j-9.4.0.jar" assignment5.b2.LoginDemo
java -cp PartB\TaskB2\out assignment5.b2.DamagedDataTest PartB\TaskB1\input\passes.txt
```

Tasks B3 and B4 use the same connector classpath with their `PassLoader` and `MigrationProgram` main classes.

## Actual verification status
- Java 26.0.2.1 detected.
- Task B1 compiled and ran successfully.
- `P1003` found with its complete record.
- Damaged `P1002` returned `null`.
- Route R12 listing preserved valid records and skipped damaged records.
- Damaged-data test found 4 valid and 4 damaged/skipped records.
- Task B2 damaged-data test passed.
- Task B2 JDBC login could not run because MySQL was stopped and the process lacked permission to start it.
- Tasks B3 and B4 compiled successfully but could not execute JDBC operations until MySQL is running and credentials are confirmed.

## Actual B1 performance measurements
The output is in `PartB/TaskB1/measurements/performance.csv`.

| Test | Size/count | Method | Time |
|---|---:|---|---:|
| Line count | 1 MB | unbuffered | 2895 ms |
| Line count | 1 MB | buffered | 32 ms |
| Line count | 10 MB | unbuffered | 42900 ms |
| Line count | 10 MB | buffered | 52 ms |
| Line count | 50 MB | unbuffered | 179624 ms |
| Line count | 50 MB | buffered | 164 ms |
| Pass search | 1000 | scan | 25 ms |
| Pass search | 10000 | scan | 48 ms |
| Pass search | 100000 | scan | 141 ms |
