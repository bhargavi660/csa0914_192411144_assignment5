# Screenshot Checklist

| Filename | Task | Command or action | Required visible evidence |
|---|---|---|---|
| A1-format.txt | A1 | Open `PartA/TaskA1/RecordLayouts.md` | Delimited and binary layouts |
| B1-pass-store.txt | B1 | `java -cp out assignment5.b1.PassStoreDemo` | P1003 found, P1002 null, route R12 records |
| B1-damaged.txt | B1 | Damaged-data test | 4 valid, 4 damaged/skipped |
| B1-performance.txt | B1 | Run `PassBenchmark` | Actual `performance.csv` table |
| B2-login.txt | B2 | Run `LoginDemo` after MySQL setup | Unsafe crafted login true and safe crafted login false |
| B3-loader.txt | B3 | Run `PassLoader` | Four row-count timings and rollback row count |
| B3-metadata.txt | B3 | Run `PassLoader` | Column names/types and row count |
| B4-migration.txt | B4 | Run `MigrationProgram` | Success/failure counts and metadata |
| B4-tables.txt | B4 | Run verification SQL | Bus-pass and users tables |
| git-status.txt | Git | `git status --short --branch` | Repository and committed files |

No screenshot capture is claimed here. Capture the listed evidence only after the corresponding command has been executed successfully.
