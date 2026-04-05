package org.example;

public class Main {
    public static void main(String[] args) {
        // Можно запускать так:
        // 1) без аргументов — будет демо
        // 2) с аргументами: x eps maxTerms
        //    например: 0.5 1e-12 100000

        if (args.length == 0) {
            demo();
            return;
        }

        double x = Double.parseDouble(args[0]);
        double eps = (args.length >= 2) ? Double.parseDouble(args[1]) : 1e-12;
        int maxTerms = (args.length >= 3) ? Integer.parseInt(args[2]) : 200000;

        double approx = ArccosSeries.arccos(x, eps, maxTerms);
        double exact = Math.acos(x);

        System.out.printf("x = %.17g%n", x);
        System.out.printf("Series arccos(x) = %.17g%n", approx);
        System.out.printf("Math.acos(x)     = %.17g%n", exact);
        System.out.printf("|diff|           = %.17g%n", Math.abs(approx - exact));
    }

    private static void demo() {
        double[] xs = { -0.9, -0.5, 0.0, 0.5, 0.9 };
        double eps = 1e-12;
        int maxTerms = 200000;

        for (double x : xs) {
            double approx = ArccosSeries.arccos(x, eps, maxTerms);
            double exact = Math.acos(x);
            System.out.printf("x=% .2f  approx=% .15f  exact=% .15f  diff=% .3e%n",
                    x, approx, exact, Math.abs(approx - exact));
        }
    }
}