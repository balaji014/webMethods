# DemoPkg

Utility math services and file integrity utilities for Integration Server.

## Services
- `DemoPkg.services:addTwoNumbers`: adds `num1` and `num2`, returns `result`; wrapped in try/catch with `pub.flow:getLastError`.
- `DemoPkg.services:arithmeticOperation`: inputs `number1`, `number2`, `operator` (`+`, `-`, `*`, `/`); routes to the matching `pub.math` service, returns `result`; unsupported operators and runtime errors flow to the catch block via `lastError`.
- `DemoPkg.services.utils:ReadFile`: loads file content from `filePath`.
- `DemoPkg.services.utils:ComputeHash`: hashes `content` with optional `hashAlgorithm` (default SHA256), returns `hash`.
- `DemoPkg.services.utils:CompareHashes`: compares `expectedHash` vs `actualHash`, returns `match`.
- `DemoPkg.services.utils:VerifyFileIntegrity`: orchestrator that reads file, hashes it, and compares to `expectedHash`, returning `computedHash` and `match`; errors surface via `lastError`.

## Tests
- Arithmetic suite: `resources/tests/setup/arithmeticTestSuite.xml` (cases for add, subtract, multiply, divide, divide-by-zero, invalid-operator). Data files live under `resources/tests/data/arithmetic_*`.
- AddTwoNumbers suite: `resources/tests/setup/wmTestSuite.xml` with inputs/outputs in `resources/tests/data/DemoPkg_services_addTwoNumbers_*`.
- File integrity suite: `resources/tests/setup/fileIntegrityTestSuite.xml` covering all utils and orchestrator; data under `resources/tests/data/verify_*`, `readFile_*`, `computeHash_*`, `compareHashes_*`, and sample files `integrity_file_ok.txt`, `integrity_file_bad.txt`.

Run suites with your preferred wM test runner (e.g., WmTestSuite UI or wmTestRunner) from the package root. Ensure the package is loaded on the target Integration Server before running.
