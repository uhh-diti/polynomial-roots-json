import java.math.BigInteger;
import java.util.*;

public class Main {

    // Convert base-N string to BigInteger
    static BigInteger toDecimal(String value, int base) {
        return new BigInteger(value, base);
    }

    // Evaluate polynomial at x
    static BigInteger evaluate(List<BigInteger> coeff, BigInteger x) {
        BigInteger result = BigInteger.ZERO;
        BigInteger pow = BigInteger.ONE;
        for (BigInteger c : coeff) {
            result = result.add(c.multiply(pow));
            pow = pow.multiply(x);
        }
        return result;
    }

    // Lagrange interpolation
    static List<BigInteger> interpolate(List<BigInteger[]> pts, int degree) {
        int n = pts.size();
        List<BigInteger> coeff = new ArrayList<>(Collections.nCopies(degree+1, BigInteger.ZERO));

        for (int i=0; i<n; i++) {
            List<BigInteger> basis = new ArrayList<>();
            basis.add(BigInteger.ONE);
            BigInteger denom = BigInteger.ONE;

            for (int j=0; j<n; j++) {
                if (i==j) continue;
                List<BigInteger> newBasis = new ArrayList<>(Collections.nCopies(basis.size()+1, BigInteger.ZERO));
                for (int k=0; k<basis.size(); k++) {
                    newBasis.set(k, newBasis.get(k).subtract(basis.get(k).multiply(pts.get(j)[0])));
                    newBasis.set(k+1, newBasis.get(k+1).add(basis.get(k)));
                }
                basis = newBasis;
                denom = denom.multiply(pts.get(i)[0].subtract(pts.get(j)[0]));
            }
            BigInteger scalar = pts.get(i)[1].divide(denom);
            for (int k=0; k<basis.size(); k++) {
                coeff.set(k, coeff.get(k).add(basis.get(k).multiply(scalar)));
            }
        }
        return coeff;
    }

    public static void main(String[] args) {
        // Example JSON data (from your test case)
        String[][] inputs = {
            {"6",  "13444211440455345511"},
            {"15", "aed7015a346d635"},
            {"15", "6aeeb69631c227c"},
            {"16", "e1b5e05623d881f"},
            {"8",  "316034514573652620673"},
            {"3",  "2122212201122002221120200210011020220200"},
            {"3",  "20120221122211000100210021102001201112121"},
            {"6",  "20220554335330240002224253"},
            {"12", "45153788322a1255483"},
            {"7",  "1101613130313526312514143"}
        };

        int n = 10;
        int k = 7; // degree = k-1 = 6

        List<BigInteger[]> points = new ArrayList<>();
        for (int i=0; i<n; i++) {
            int base = Integer.parseInt(inputs[i][0]);
            String value = inputs[i][1];
            BigInteger val = toDecimal(value, base);
            points.add(new BigInteger[]{BigInteger.valueOf(i+1), val});
        }

        // Use first k points for interpolation
        List<BigInteger[]> subset = points.subList(0, k);
        List<BigInteger> poly = interpolate(subset, k-1);

        System.out.println("Recovered polynomial coefficients (constant to x^" + (k-1) + "):");
        for (int i=0; i<poly.size(); i++) {
            System.out.println("a" + i + " = " + poly.get(i));
        }
    }
}
