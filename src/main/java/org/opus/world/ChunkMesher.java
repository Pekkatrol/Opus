package org.opus.world;

import org.opus.render.Mesh;
import org.opus.render.MeshLoader;

import java.util.Arrays;

public class ChunkMesher {

        private static final int ATLAS_COLS = 4;
        private static final int ATLAS_ROWS = 4;
        // Léger retrait dans chaque case, pour que le voisin ne déborde pas
        private static final float INSET = 0.01f;

        // Ordre : +z, -z, +x, -x, +y, -y
        private static final int[][] DIRS = {
            {0, 0, 1}, {0, 0, -1}, {1, 0, 0}, {-1, 0, 0}, {0, 1, 0}, {0, -1, 0}
        };

        // Coins de chaque face (bas-gauche, bas-droite, haut-droite, haut-gauche), vus de l'extérieur
        private static final int[][][] CORNERS = {
            {{0, 0, 1}, {1, 0, 1}, {1, 1, 1}, {0, 1, 1}},
            {{1, 0, 0}, {0, 0, 0}, {0, 1, 0}, {1, 1, 0}},
            {{1, 0, 1}, {1, 0, 0}, {1, 1, 0}, {1, 1, 1}},
            {{0, 0, 0}, {0, 0, 1}, {0, 1, 1}, {0, 1, 0}},
            {{0, 1, 1}, {1, 1, 1}, {1, 1, 0}, {0, 1, 0}},
            {{0, 0, 0}, {1, 0, 0}, {1, 0, 1}, {0, 0, 1}}
        };

        private static final float[] SHADE = {0.8f, 0.8f, 0.6f, 0.6f, 1.0f, 0.5f};
        private static final float[][] UVS = {{0, 1}, {1, 1}, {1, 0}, {0, 0}};

        private static byte neighbour(World world, Chunk chunk, int x, int y, int z) {
                if (x >= 0 && x < Chunk.SIZE && z >= 0 && z < Chunk.SIZE) {
                        return chunk.get(x, y, z);
                }
                return world.getBlock(chunk.getWorldX() + x, y, chunk.getWorldZ() + z);
        }

        public static Mesh build(World world, Chunk chunk) {
                float[] vertices = new float[24 * 1024];
                int[] indices = new int[6 * 1024];
                int v = 0;
                int i = 0;
                int faceCount = 0;

                for (int x = 0; x < Chunk.SIZE; x++) {
                        for (int y = 0; y < Chunk.HEIGHT; y++) {
                                for (int z = 0; z < Chunk.SIZE; z++) {
                                        byte id = chunk.get(x, y, z);
                                        if (!Block.isSolid(id)) {
                                                continue;
                                        }
                                        for (int f = 0; f < 6; f++) {
                                                int nx = x + DIRS[f][0];
                                                int ny = y + DIRS[f][1];
                                                int nz = z + DIRS[f][2];
                                                if (Block.isSolid(neighbour(world, chunk, nx, ny, nz))) {
                                                        continue;
                                                }

                                                if (v + 24 > vertices.length) {
                                                        vertices = Arrays.copyOf(vertices, vertices.length * 2);
                                                }
                                                if (i + 6 > indices.length) {
                                                        indices = Arrays.copyOf(indices, indices.length * 2);
                                                }

                                                int tile = Block.tile(id, f);
                                                int col = tile % ATLAS_COLS;
                                                int row = tile / ATLAS_COLS;

                                                int base = faceCount * 4;
                                                for (int c = 0; c < 4; c++) {
                                                        float texU = (col + INSET + UVS[c][0] * (1 - 2 * INSET)) / ATLAS_COLS;
                                                        float texV = (row + INSET + UVS[c][1] * (1 - 2 * INSET)) / ATLAS_ROWS;
                                                        vertices[v++] = x + CORNERS[f][c][0];
                                                        vertices[v++] = y + CORNERS[f][c][1];
                                                        vertices[v++] = z + CORNERS[f][c][2];
                                                        vertices[v++] = texU;
                                                        vertices[v++] = texV;
                                                        vertices[v++] = SHADE[f];
                                                }
                                                indices[i++] = base;
                                                indices[i++] = base + 1;
                                                indices[i++] = base + 2;
                                                indices[i++] = base;
                                                indices[i++] = base + 2;
                                                indices[i++] = base + 3;
                                                faceCount++;
                                        }
                                }
                        }
                }

                if (faceCount == 0) {
                        return null;
                }
                return MeshLoader.create(Arrays.copyOf(vertices, v), Arrays.copyOf(indices, i));
        }
}