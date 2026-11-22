# Task B2 - SQL Injection and Damaged Input

Set database environment variables before running:
```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/bus_pass_assignment5"
$env:DB_USER="root"
$env:DB_PASSWORD="<your-local-password>"
```

Compile and run:
```powershell
javac -d out -cp ..\..\lib\mysql-connector-j-9.4.0.jar src\*.java
java -cp "out;..\..\lib\mysql-connector-j-9.4.0.jar" assignment5.b2.LoginDemo
java -cp out assignment5.b2.DamagedDataTest
```

The unsafe login demonstrator uses string-built SQL only to teach the vulnerability. It must be used only against the local test database. The safe version uses `PreparedStatement`.
