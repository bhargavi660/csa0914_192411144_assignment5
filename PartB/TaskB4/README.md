# Task B4 - Migration and Engineering Decision

Set database environment variables, then compile and run:
```powershell
javac -d out -cp ..\..\lib\mysql-connector-j-9.4.0.jar src\*.java
java -cp "out;..\..\lib\mysql-connector-j-9.4.0.jar" assignment5.b4.MigrationProgram
```

The migration validates the text file, batches valid records, uses one transaction and reports success/failure counts. It also reports database and JDBC metadata.
