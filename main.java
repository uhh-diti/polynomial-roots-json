import java.math.BigInteger;
import java.util.*;
import java.util.regex.*;

public class Main {

    // Convert base-N string to BigInteger
    static BigInteger toDecimal(String value, int base) {
        return new BigInteger(value, base);
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

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        StringBuilder sb = new StringBuilder();
        while (sc.hasNextLine()) {
            sb.append(sc.nextLine()).append("\n");
        }
        String json = sb.toString();

        // Extract n and k
        Pattern pk = Pattern.compile("\"n\"\\s*:\\s*(\\d+).*?\"k\"\\s*:\\s*(\\d+)", Pattern.DOTALL);
        Matcher mk = pk.matcher(json);
        int n=0, k=0;
        if (mk.find()) {
            n = Integer.parseInt(mk.group(1));
            k = Integer.parseInt(mk.group(2));
        }

        // Extract base and value for each entry
        Pattern p = Pattern.compile("\"(\\d+)\"\\s*:\\s*\\{\\s*\"base\"\\s*:\\s*\"?(\\d+)\"?,\\s*\"value\"\\s*:\\s*\"([^\"]+)\"\\s*\\}");
        Matcher m = p.matcher(json);

        List<BigInteger[]> points = new ArrayList<>();
        while (m.find()) {
            int idx = Integer.parseInt(m.group(1));
            int base = Integer.parseInt(m.group(2));
            String value = m.group(3);
            BigInteger val = toDecimal(value, base);
            points.add(new BigInteger[]{BigInteger.valueOf(idx), val});
        }

        // Sort by index and use first k points
        points.sort(Comparator.comparing(a -> a[0]));
        List<BigInteger[]> subset = points.subList(0, k);
        List<BigInteger> poly = interpolate(subset, k-1);

        // Output coefficients
        System.out.println("Recovered polynomial coefficients (a0..a" + (k-1) + "):");
        for (int i=0; i<poly.size(); i++) {
            System.out.println("a" + i + " = " + poly.get(i));
        }
    }
}
