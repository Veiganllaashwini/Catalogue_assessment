import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Shamirs_Secret_Sharing_algorithm {

    public static void main(String[] args) throws IOException {
        // Load the JSON input file for the two test cases
        JSONObject testCase1 = new JSONObject(Files.readString(new File("testcase1.json").toPath()));
        JSONObject testCase2 = new JSONObject(Files.readString(new File("testcase2.json").toPath()));

        // Process each test case
        System.out.println("Secret for Test Case 1: " + findSecret(testCase1));
        System.out.println("Secret for Test Case 2: " + findSecret(testCase2));
    }

    private static BigInteger findSecret(JSONObject testCase) {
        // Read the values of n and k
        int n = testCase.getJSONObject("keys").getInt("n");
        int k = testCase.getJSONObject("keys").getInt("k");

        // Decode the roots
        List<int[]> roots = new ArrayList<>(); // List of (x, y) pairs
        for (int i = 1; i <= n; i++) {
            if (testCase.has(String.valueOf(i))) {
                JSONObject root = testCase.getJSONObject(String.valueOf(i));
                int x = i;
                BigInteger y = decode(root.getString("value"), root.getInt("base"));
                roots.add(new int[]{x, y.intValue()}); // Store y as an int (if it fits)
            }
        }

        // Use the first k roots for Lagrange interpolation
        return calculateConstantTerm(roots.subList(0, k));
    }

    private static BigInteger decode(String value, int base) {
        // Decode the value considering the base using BigInteger
        return new BigInteger(value, base); // Use BigInteger for arbitrary precision
    }

    private static BigInteger calculateConstantTerm(List<int[]> points) {
        BigInteger constant = BigInteger.ZERO; // Initialize constant as BigInteger.ZERO
        int k = points.size();

        // Apply Lagrange interpolation formula
        for (int i = 0; i < k; i++) {
            int[] p1 = points.get(i);
            int x_i = p1[0];
            BigInteger y_i = BigInteger.valueOf(p1[1]);

            // Compute the Lagrange basis polynomial L_i(0)
            BigInteger term = y_i;
            for (int j = 0; j < k; j++) {
                if (i != j) {
                    int[] p2 = points.get(j);
                    int x_j = p2[0];
                    term = term.multiply(BigInteger.valueOf(x_j))
                            .divide(BigInteger.valueOf(x_j - x_i));
                }
            }

            // Add the term to the constant
            constant = constant.add(term);
        }

        return constant;
    }
}
