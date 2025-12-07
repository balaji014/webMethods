# DemoPkg

Utility math services for Integration Server.

## Services
- `DemoPkg.services:addTwoNumbers`: adds `num1` and `num2`, returns `result`; wrapped in try/catch with `pub.flow:getLastError`.
- `DemoPkg.services:arithmeticOperation`: inputs `number1`, `number2`, `operator` (`+`, `-`, `*`, `/`); routes to the matching `pub.math` service, returns `result`; unsupported operators and runtime errors flow to the catch block via `lastError`.

## Tests
- Arithmetic suite: `resources/tests/setup/arithmeticTestSuite.xml` (cases for add, subtract, multiply, divide, divide-by-zero, invalid-operator). Data files live under `resources/tests/data/arithmetic_*`.
- AddTwoNumbers suite: `resources/tests/setup/wmTestSuite.xml` with inputs/outputs in `resources/tests/data/DemoPkg_services_addTwoNumbers_*`.

Run suites with your preferred wM test runner (e.g., WmTestSuite UI or wmTestRunner) from the package root. Ensure the package is loaded on the target Integration Server before running.
