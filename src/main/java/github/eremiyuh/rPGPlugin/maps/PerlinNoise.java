package github.eremiyuh.rPGPlugin.maps;

import java.util.Random;

public class PerlinNoise {

    private static final int PERMUTATION_SIZE = 256;
    private final int[] permutation;

    public PerlinNoise(int seed) {
        permutation = new int[PERMUTATION_SIZE * 2];
        Random random = new Random(seed);

        // Initialize the permutation table
        for (int i = 0; i < PERMUTATION_SIZE; i++) {
            permutation[i] = i;
        }

        // Shuffle the permutation table
        for (int i = 0; i < PERMUTATION_SIZE; i++) {
            int swap = random.nextInt(PERMUTATION_SIZE);
            int temp = permutation[i];
            permutation[i] = permutation[swap];
            permutation[swap] = temp;
        }

        // Copy the table for wrapping around the values
        System.arraycopy(permutation, 0, permutation, PERMUTATION_SIZE, PERMUTATION_SIZE);
    }

    public double noise(double x, double y) {
        int X = (int) Math.floor(x) & 255;
        int Y = (int) Math.floor(y) & 255;

        x -= Math.floor(x);
        y -= Math.floor(y);

        double u = fade(x);
        double v = fade(y);

        int A = permutation[X] + Y;
        int B = permutation[X + 1] + Y;

        return lerp(v, lerp(u, grad(permutation[A], x, y), grad(permutation[B], x - 1, y)),
                lerp(u, grad(permutation[A + 1], x, y - 1), grad(permutation[B + 1], x - 1, y - 1)));
    }

    private double fade(double t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    private double lerp(double t, double a, double b) {
        return a + t * (b - a);
    }

    private double grad(int hash, double x, double y) {
        int h = hash & 15;
        double u = h < 8 ? x : y;
        double v = h < 4 ? y : (h == 12 || h == 14) ? x : 0;
        return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
    }
}

