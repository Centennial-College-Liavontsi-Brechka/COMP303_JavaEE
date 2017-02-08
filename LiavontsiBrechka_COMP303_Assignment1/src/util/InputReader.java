package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.StringTokenizer;

public class InputReader {
    private final BufferedReader keypadReader;
    private StringTokenizer tokenizer;

    public InputReader(InputStream in) {
        keypadReader = new BufferedReader(new InputStreamReader(in));
    }

    public int nextInt() {
        return Integer.parseInt(next());
    }

    public long nextLong() {
        return Long.parseLong(next());
    }

    public double nextDouble() {
        return Double.parseDouble(next());
    }

    public BigInteger nextBigInteger() {
        return new BigInteger(next());
    }

    public boolean hasNext() {
        while (tokenizer == null || !tokenizer.hasMoreTokens()) {
            String line = readLine();
            if (line == null) {
                return false;
            }
            tokenizer = new StringTokenizer(line);
        }
        return true;
    }

    public String next() {
        while (tokenizer == null || !tokenizer.hasMoreTokens()) {
            tokenizer = new StringTokenizer(readLine());
        }
        return tokenizer.nextToken();
    }

    public String readLine() {
        String line;
        try {
            line = keypadReader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return line;
    }
}
