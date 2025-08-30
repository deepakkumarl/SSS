import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.math.BigInteger;
import java.util.*;

public class ShamirReconstruction {
    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(new File("input.json"));

        int n = root.get("keys").get("n").asInt();
        int k = root.get("keys").get("k").asInt();

        List<Integer> xs = new ArrayList<>();
        List<BigInteger> ys = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        Iterator<String> fields = root.fieldNames();
        while(fields.hasNext()) {
            String key = fields.next();
            if(key.equals("keys")) continue;
            JsonNode node = root.get(key);
            int x = Integer.parseInt(key);
            int base = node.get("base").asInt();
            BigInteger y = new BigInteger(node.get("value").asText(), base);
            xs.add(x);
            ys.add(y);
            indices.add(x);
        }

        BigInteger secret = null;
        List<Integer> correctIndices = null;
        List<List<Integer>> combos = combinations(indices, k);
        for(List<Integer> combo : combos) {
            List<Integer> xSubset = new ArrayList<>();
            List<BigInteger> ySubset = new ArrayList<>();
            for(int idx : combo) {
                int pos = indices.indexOf(idx);
                xSubset.add(xs.get(pos));
                ySubset.add(ys.get(pos));
            }
            BigInteger candidate = lagrangeInterpolationAtZero(xSubset, ySubset);
            if(secret == null) {
                secret = candidate;
                correctIndices = combo;
            } else if(!secret.equals(candidate)) {
            }
        }

        List<Integer> wrongShares = new ArrayList<>();
        for(int idx : indices) {
            if(!correctIndices.contains(idx)) wrongShares.add(idx);
        }

        System.out.println("Secret: " + secret);
        System.out.println("Wrong Shares: " + wrongShares);
    }

    static BigInteger lagrangeInterpolationAtZero(List<Integer> xs, List<BigInteger> ys) {
        BigInteger result = BigInteger.ZERO;
        int n = xs.size();
        for(int i = 0; i < n; i++) {
            BigInteger term = ys.get(i);
            for(int j = 0; j < n; j++) {
                if(i == j) continue;
                BigInteger numerator = BigInteger.valueOf(-xs.get(j));
                BigInteger denominator = BigInteger.valueOf(xs.get(i) - xs.get(j));
                term = term.multiply(numerator).divide(denominator);
            }
            result = result.add(term);
        }
        return result;
    }

    static List<List<Integer>> combinations(List<Integer> arr, int k) {
        List<List<Integer>> result = new ArrayList<>();
        backtrack(arr, k, 0, new ArrayList<>(), result);
        return result;
    }

    static void backtrack(List<Integer> arr, int k, int start, List<Integer> curr, List<List<Integer>> result) {
        if(curr.size() == k) {
            result.add(new ArrayList<>(curr));
            return;
        }
        for(int i = start; i < arr.size(); i++) {
            curr.add(arr.get(i));
            backtrack(arr, k, i+1, curr, result);
            curr.remove(curr.size()-1);
        }
    }
}
