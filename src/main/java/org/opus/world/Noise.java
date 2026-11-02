package org.opus.world;

import java.util.Random;

// Bruit de Perlin 2D
public class Noise {

        private final int[] perm = new int[512];

        public Noise(long seed) {
                int[] p = new int[256];
                for (int i = 0; i < 256; i++) {
                        p[i] = i;
                }
                Random random = new Random(seed);
                for (int i = 255; i > 0; i--) {
                        int j = random.nextInt(i + 1);
                        int tmp = p[i];
                        p[i] = p[j];
                        p[j] = tmp;
                }
                for (int i = 0; i < 512; i++) {
                        perm[i] = p[i & 255];
                }
        }

        private static float fade(float t) {
                return t * t * t * (t * (t * 6 - 15) + 10);
        }

        private static float lerp(float a, float b, float t) {
                return a + t * (b - a);
        }

        private static float grad(int hash, float x, float y) {
                switch (hash & 7) {
                        case 0: return x + y;
                        case 1: return -x + y;
                        case 2: return x - y;
                        case 3: return -x - y;
                        case 4: return x;
                        case 5: return -x;
                        case 6: return y;
                        default: return -y;
                }
        }

        // Valeur entre -1 et 1 environ
        public float noise(float x, float y) {
                int xi = (int) Math.floor(x);
                int yi = (int) Math.floor(y);
                float xf = x - xi;
                float yf = y - yi;
                int xc = xi & 255;
                int yc = yi & 255;
                float u = fade(xf);
                float v = fade(yf);

                int aa = perm[perm[xc] + yc];
                int ab = perm[perm[xc] + yc + 1];
                int ba = perm[perm[xc + 1] + yc];
                int bb = perm[perm[xc + 1] + yc + 1];

                float x1 = lerp(grad(aa, xf, yf), grad(ba, xf - 1, yf), u);
                float x2 = lerp(grad(ab, xf, yf - 1), grad(bb, xf - 1, yf - 1), u);
                return lerp(x1, x2, v);
        }

        // Plusieurs couches de bruit : grandes collines + petits reliefs
        public float fractal(float x, float y, int octaves, float persistence) {
                float total = 0;
                float amplitude = 1;
                float frequency = 1;
                float max = 0;
                for (int i = 0; i < octaves; i++) {
                        total += noise(x * frequency, y * frequency) * amplitude;
                        max += amplitude;
                        amplitude *= persistence;
                        frequency *= 2;
                }
                return total / max;
        }
}