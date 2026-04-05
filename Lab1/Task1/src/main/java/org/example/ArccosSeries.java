package org.example;

public final class ArccosSeries {

    private ArccosSeries() {}

    /**
     * arccos(x) через степенной ряд с ускорением сходимости.
     * Работает на x in [-1; 1], eps > 0.
     */
    public static double arccos(double x, double eps, int maxTerms) {
        validate(x, eps, maxTerms);

        // Ускорение: сводим вычисление к arcsin(z), где z in [0; 0.7071...]
        if (x >= 0.0) {
            double z = Math.sqrt((1.0 - x) / 2.0);
            return 2.0 * arcsinSeries(z, eps, maxTerms);
        } else {
            double z = Math.sqrt((1.0 + x) / 2.0);
            return Math.PI - 2.0 * arcsinSeries(z, eps, maxTerms);
        }
    }

    /**
     * arcsin(z) по ряду Маклорена:
     * arcsin(z) = sum_{n=0..inf} c_n * z^(2n+1),
     * c_0 = 1
     * c_n = c_{n-1} * ((2n-1)^2)/((2n)(2n+1))
     */
    static double arcsinSeries(double z, double eps, int maxTerms) {
        double sum = 0.0;

        double c = 1.0;     // c0
        double power = z;   // z^(2*0+1)

        for (int n = 0; n < maxTerms; n++) {
            double term = c * power;
            sum += term;

            if (Math.abs(term) < eps) {
                break;
            }

            int nn = n + 1;
            double num = (2.0 * nn - 1.0);
            c = c * (num * num) / ((2.0 * nn) * (2.0 * nn + 1.0));

            power *= (z * z);
        }

        return sum;
    }

    private static void validate(double x, double eps, int maxTerms) {
        if (Double.isNaN(x) || Double.isInfinite(x)) {
            throw new IllegalArgumentException("x must be finite");
        }
        if (x < -1.0 || x > 1.0) {
            throw new IllegalArgumentException("x must be in [-1; 1]");
        }
        if (!(eps > 0.0)) { // eps <= 0 или NaN
            throw new IllegalArgumentException("eps must be > 0");
        }
        if (maxTerms <= 0) {
            throw new IllegalArgumentException("maxTerms must be > 0");
        }
    }
}