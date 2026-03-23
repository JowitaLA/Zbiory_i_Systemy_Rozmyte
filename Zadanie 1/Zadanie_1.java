public class Zadanie_1 {

    // ZBIÓR TRÓJKĄTNY
    static class TriangleSet {
        double a, b, c;

        TriangleSet(double a, double b, double c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }

        double membership(double x) {
            if (x <= a || x >= c) return 0;
            if (x == b) return 1;
            if (x > a && x < b) return (x - a) / (b - a);
            return (c - x) / (c - b);
        }
    }

    // ZBIÓR TRAPEZOWY
    static class TrapezoidSet {
        double a, b, c, d;

        TrapezoidSet(double a, double b, double c, double d) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
        }

        double membership(double x) {
            if (x <= a || x >= d) return 0;
            if (x >= b && x <= c) return 1;
            if (x > a && x < b) return (x - a) / (b - a);
            return (d - x) / (d - c);
        }
    }

    // ZBIÓR GAUSSOWSKI
    static class GaussianSet {
        double mean, sigma;

        GaussianSet(double mean, double sigma) {
            this.mean = mean;
            this.sigma = sigma;
        }
        
        double membership(double x) {
            return Math.exp( -Math.pow(x - mean, 2) / (2 * sigma * sigma) );
        }
    }


    // GŁÓWNY KOD
    public static void main(String[] args) {

        double x = 5; // Przykładowa wartość wejściowa

        // Tworzenie zbiorów rozmytych
        TriangleSet triangle = new TriangleSet(2, 6, 10);
        TrapezoidSet trapezoid = new TrapezoidSet(0, 7, 9, 12);
        GaussianSet gaussian = new GaussianSet(7, 1.5);

        // Wyniki
        System.out.println("x = " + x);
        System.out.println("Trójkątny: " + triangle.membership(x));
        System.out.println("Trapezowy: " + trapezoid.membership(x));
        System.out.println("Gaussowski: " + gaussian.membership(x));
    }
}
