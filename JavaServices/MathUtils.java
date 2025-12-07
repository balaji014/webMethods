// Basic webMethods Java service: accepts num1, num2 (strings or numeric), and operator; returns result
package com.mycompany.math;

import com.wm.data.IData;
import com.wm.data.IDataCursor;
import com.wm.data.IDataUtil;
import com.wm.app.b2b.server.ServiceException;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class MathUtils {

    /**
     * Java service entry point for webMethods.
     *
     * Inputs (pipeline):
     *   num1     - (String or number) first operand
     *   num2     - (String or number) second operand
     *   operator - (String) one of: +, -, *, /, %, pow, ^, add, sub, mul, div, mod
     *
     * Outputs (pipeline):
     *   result       - (String) operation result
     *   resultType   - (String) 'BigDecimal' or 'Integer' (informational)
     *   status       - 'OK' or 'ERROR'
     *   errorMessage - present only on error
     */
    public static final void calculate(IData pipeline) throws ServiceException {
        IDataCursor cursor = pipeline.getCursor();
        try {
            String sNum1 = IDataUtil.getString(cursor, "num1");
            String sNum2 = IDataUtil.getString(cursor, "num2");
            String operator = IDataUtil.getString(cursor, "operator");

            if (operator == null || operator.trim().isEmpty()) {
                throw new ServiceException("operator is required");
            }
            if (sNum1 == null || sNum1.trim().isEmpty()) {
                throw new ServiceException("num1 is required");
            }
            if (sNum2 == null || sNum2.trim().isEmpty()) {
                throw new ServiceException("num2 is required");
            }

            BigDecimal num1;
            BigDecimal num2;
            try {
                num1 = new BigDecimal(sNum1.trim());
                num2 = new BigDecimal(sNum2.trim());
            } catch (NumberFormatException nfe) {
                throw new ServiceException("Invalid numeric input: " + nfe.getMessage());
            }

            BigDecimal result;
            String op = operator.trim().toLowerCase();

            switch (op) {
                case "+":
                case "add":
                case "plus":
                    result = num1.add(num2);
                    break;
                case "-":
                case "sub":
                case "minus":
                    result = num1.subtract(num2);
                    break;
                case "*":
                case "x":
                case "mul":
                case "multiply":
                    result = num1.multiply(num2);
                    break;
                case "/":
                case "div":
                case "divide":
                    if (num2.compareTo(BigDecimal.ZERO) == 0) {
                        throw new ServiceException("Division by zero");
                    }
                    // Set safe scale for division (10 decimal places); change as needed.
                    result = num1.divide(num2, 10, RoundingMode.HALF_UP);
                    break;
                case "%":
                case "mod":
                case "remainder":
                    if (num2.compareTo(BigDecimal.ZERO) == 0) {
                        throw new ServiceException("Modulo by zero");
                    }
                    result = num1.remainder(num2);
                    break;
                case "pow":
                case "^":
                    // try integer exponent for BigDecimal pow
                    try {
                        int exponent = num2.intValueExact();
                        result = num1.pow(exponent);
                    } catch (ArithmeticException ae) {
                        // if exponent isn't an exact integer, fallback to double pow
                        double d = Math.pow(num1.doubleValue(), num2.doubleValue());
                        result = BigDecimal.valueOf(d);
                    }
                    break;
                default:
                    throw new ServiceException("Unsupported operator: " + operator);
            }

            // normalize result string: drop unnecessary trailing zeros
            String resultStr = stripTrailingZeros(result);

            IDataUtil.put(cursor, "result", resultStr);
            IDataUtil.put(cursor, "resultType", determineResultType(result));
            IDataUtil.put(cursor, "status", "OK");
        } catch (ServiceException se) {
            // put error into pipeline and rethrow so IS can catch/log if needed
            IDataUtil.put(cursor, "status", "ERROR");
            IDataUtil.put(cursor, "errorMessage", se.getMessage());
            throw se;
        } finally {
            cursor.destroy();
        }
    }

    private static String stripTrailingZeros(BigDecimal bd) {
        bd = bd.stripTrailingZeros();
        // ensure plain string (no scientific)
        return bd.toPlainString();
    }

    private static String determineResultType(BigDecimal bd) {
        // If scale <= 0 after stripping, it's effectively integer-like
        bd = bd.stripTrailingZeros();
        if (bd.scale() <= 0) {
            return "Integer";
        } else {
            return "BigDecimal";
        }
    }
}