package main.java.model;


import java.math.BigDecimal;
import java.math.RoundingMode;

import static java.lang.Math.PI;

public class Solver {

    private static final int SCALE = 10;
    private static final int ROUNDING_MODE = BigDecimal.ROUND_HALF_DOWN;

    private static final int R = 6371000;
    private static final double g = 9.8;
    private static final double speed = 11200000.0;


    public static String solve(double first, double second) {
        BigDecimal firstTime = BigDecimal.valueOf(first).multiply(BigDecimal.valueOf(86400));
        BigDecimal secondTime = BigDecimal.valueOf(second).multiply(BigDecimal.valueOf(86400));

        BigDecimal firstTerm = BigDecimal.ONE.divide(nthRoot(3, firstTime.pow(2)), 10, RoundingMode.HALF_UP).subtract(BigDecimal.ONE.divide(nthRoot(3, secondTime.pow(2)), 10, RoundingMode.HALF_UP));

        BigDecimal secondTerm = nthRoot(3, BigDecimal.valueOf(PI).pow(2).multiply(BigDecimal.valueOf(R)).divide(BigDecimal.valueOf(16 * g), RoundingMode.HALF_UP));

        BigDecimal result = firstTerm.multiply(secondTerm).multiply(BigDecimal.valueOf(speed)).divide(BigDecimal.valueOf(1000), 5, RoundingMode.HALF_UP);

        result = result.abs().setScale(0, RoundingMode.HALF_UP);

        return result.toString();
    }

    private static BigDecimal nthRoot(final int n, final BigDecimal a) {
        return nthRoot(n, a, BigDecimal.valueOf(.1).movePointLeft(SCALE));
    }

    private static BigDecimal nthRoot(final int n, final BigDecimal a, final BigDecimal p) {
        if (a.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("nth root can only be calculated for positive numbers");
        }
        if (a.equals(BigDecimal.ZERO)) {
            return BigDecimal.ZERO;
        }
        BigDecimal xPrev = a;
        BigDecimal x = a.divide(new BigDecimal(n), SCALE, ROUNDING_MODE);  // starting "guessed" value...
        while (x.subtract(xPrev).abs().compareTo(p) > 0) {
            xPrev = x;
            x = BigDecimal.valueOf(n - 1.0)
                    .multiply(x)
                    .add(a.divide(x.pow(n - 1), SCALE, ROUNDING_MODE))
                    .divide(new BigDecimal(n), SCALE, ROUNDING_MODE);
        }
        return x;
    }
}
