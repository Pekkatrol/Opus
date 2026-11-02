package org.opus.world;

public final class Block {

        public static final byte AIR = 0;
        public static final byte GRASS = 1;
        public static final byte DIRT = 2;
        public static final byte STONE = 3;

        // Case de l'atlas pour chaque face : +z, -z, +x, -x, +y, -y
        private static final int[][] TILES = {
            {0, 0, 0, 0, 0, 0},
            {1, 1, 1, 1, 0, 2},
            {2, 2, 2, 2, 2, 2},
            {3, 3, 3, 3, 3, 3}
        };

        private Block() {
        }

        public static boolean isSolid(byte id) {
                return id != AIR;
        }

        public static int tile(byte id, int face) {
                return TILES[id][face];
        }
}