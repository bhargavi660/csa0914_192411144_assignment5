# Task B3 - JDBC Performance

Set `DB_URL`, `DB_USER` and `DB_PASSWORD` to the local assignment database.

Compile and run:
```powershell
javac -d out -cp ..\..\lib\mysql-connector-j-9.4.0.jar src\*.java
java -cp "out;..\..\lib\mysql-connector-j-9.4.0.jar" assignment5.b3.PassLoader
```

The program measures Statement, PreparedStatement and batch insertion for 10, 1,000, 10,000 and 100,000 rows. It also prints ResultSet metadata, verifies a row count and runs a controlled transaction rollback.
