package org.example.task1;

public final class ArccosSeries {

    private ArccosSeries() {}

    public static double arccos(double x, double eps, int maxTerms) {
        validate(x, eps, maxTerms);

        if (x == 1.0) {
            return 0.0;
        }
        if (x == -1.0) {
            return Math.PI;
        }

        if (x < -0.5) {
            return Math.PI - arccos(-x, eps, maxTerms);
        }

        if (x > 0.5) {
            return arccosNearOne(1.0 - x, eps, maxTerms);
        }

        return arccosMaclaurin(x, eps, maxTerms);
    }

    static double arccosMaclaurin(double x, double eps, int maxTerms) {
        double sum = Math.PI / 2.0;
        double coefficient = 1.0;
        double power = x;

        for (int n = 0; n < maxTerms; n++) {
            double term = coefficient * power;
            sum -= term;

            if (Math.abs(term) < eps) {
                break;
            }

            int next = n + 1;
            double factor = 2.0 * next - 1.0;
            coefficient = coefficient * (factor * factor) / ((2.0 * next) * (2.0 * next + 1.0));
            power *= x * x;
        }

        return sum;
    }

    static double arccosNearOne(double t, double eps, int maxTerms) {
        double scale = Math.sqrt(2.0 * t);
        double sum = 0.0;
        double coefficient = 1.0;
        double power = 1.0;

        for (int n = 0; n < maxTerms; n++) {
            double term = scale * coefficient * power;
            sum += term;

            if (Math.abs(term) < eps) {
                break;
            }

            int next = n + 1;
            double factor = 2.0 * next - 1.0;
            coefficient = coefficient * (factor * factor) / (4.0 * next * (2.0 * next + 1.0));
            power *= t;
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
        if (!(eps > 0.0)) {
            throw new IllegalArgumentException("eps must be > 0");
        }
        if (maxTerms <= 0) {
            throw new IllegalArgumentException("maxTerms must be > 0");
        }
    }
}
