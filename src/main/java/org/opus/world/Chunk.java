package org.opus.world;

public class Chunk {

        public static final int SIZE = 16;
        public static final int HEIGHT = 64;

        private final int chunkX;
        private final int chunkZ;
        private final byte[] blocks = new byte[SIZE * HEIGHT * SIZE];
        private boolean dirty = true;

        public Chunk(int chunkX, int chunkZ) {
                this.chunkX = chunkX;
                this.chunkZ = chunkZ;
        }

        private static int index(int x, int y, int z) {
                return x + SIZE * (z + SIZE * y);
        }

        private static boolean inBounds(int x, int y, int z) {
                return x >= 0 && x < SIZE && y >= 0 && y < HEIGHT && z >= 0 && z < SIZE;
        }

        public byte get(int x, int y, int z) {
                return inBounds(x, y, z) ? blocks[index(x, y, z)] : Block.AIR;
        }

        public void set(int x, int y, int z, byte id) {
                if (inBounds(x, y, z)) {
                        blocks[index(x, y, z)] = id;
                        dirty = true;
                }
        }

        public int getChunkX() {
                return chunkX;
        }

        public int getChunkZ() {
                return chunkZ;
        }

        public int getWorldX() {
                return chunkX * SIZE;
        }

        public int getWorldZ() {
                return chunkZ * SIZE;
        }

        public boolean isDirty() {
                return dirty;
        }

        public void setDirty(boolean dirty) {
                this.dirty = dirty;
        }
}