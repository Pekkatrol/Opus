package org.opus.world;

public class WorldGenerator {

        private static final int BASE_HEIGHT = 26;
        private static final float AMPLITUDE = 20f;
        private static final float SCALE = 0.008f;

        private final Noise noise;

        public WorldGenerator(long seed) {
                noise = new Noise(seed);
        }

        public void generate(Chunk chunk) {
                for (int x = 0; x < Chunk.SIZE; x++) {
                        for (int z = 0; z < Chunk.SIZE; z++) {
                                int wx = chunk.getWorldX() + x;
                                int wz = chunk.getWorldZ() + z;

                                float n = noise.fractal(wx * SCALE, wz * SCALE, 4, 0.5f);
                                int height = BASE_HEIGHT + (int) (n * AMPLITUDE);
                                height = Math.max(2, Math.min(Chunk.HEIGHT - 1, height));

                                for (int y = 0; y < height; y++) {
                                        byte id;
                                        if (y == height - 1) {
                                                id = Block.GRASS;
                                        } else if (y >= height - 4) {
                                                id = Block.DIRT;
                                        } else {
                                                id = Block.STONE;
                                        }
                                        chunk.set(x, y, z, id);
                                }
                        }
                }
        }
}