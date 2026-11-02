package org.opus.world;

import java.util.ArrayList;
import java.util.List;

public class ChunkManager {

        private static final int MAX_GENERATED_PER_UPDATE = 2;

        private final World world;
        private final WorldGenerator generator;
        private final int loadRadius;
        private final List<Chunk> toRemove = new ArrayList<>();

        public ChunkManager(World world, WorldGenerator generator, int renderDistance) {
                this.world = world;
                this.generator = generator;
                // Un anneau de plus que ce qui est affiché : un chunk n'est dessiné que si ses 4 voisins existent
                this.loadRadius = renderDistance + 1;
        }

        private static int chunkCoord(float worldCoord) {
                return Math.floorDiv((int) Math.floor(worldCoord), Chunk.SIZE);
        }

        private void generate(int cx, int cz) {
                Chunk chunk = world.createChunk(cx, cz);
                generator.generate(chunk);
        }

        // Charge tout autour d'un point, sans limite (au démarrage)
        public void loadAround(float x, float z) {
                int cx = chunkCoord(x);
                int cz = chunkCoord(z);
                for (int dx = -loadRadius; dx <= loadRadius; dx++) {
                        for (int dz = -loadRadius; dz <= loadRadius; dz++) {
                                if (dx * dx + dz * dz <= loadRadius * loadRadius
                                    && world.getChunk(cx + dx, cz + dz) == null) {
                                        generate(cx + dx, cz + dz);
                                }
                        }
                }
        }

        // À appeler à chaque mise à jour avec la position du joueur
        public void update(float x, float z) {
                int cx = chunkCoord(x);
                int cz = chunkCoord(z);

                // Déchargement des chunks trop loin (une marge évite les allers-retours)
                int unloadRadius = loadRadius + 1;
                toRemove.clear();
                for (Chunk chunk : world.getChunks()) {
                        int dx = chunk.getChunkX() - cx;
                        int dz = chunk.getChunkZ() - cz;
                        if (dx * dx + dz * dz > unloadRadius * unloadRadius) {
                                toRemove.add(chunk);
                        }
                }
                for (Chunk chunk : toRemove) {
                        world.removeChunk(chunk);
                }

                // Chargement : le plus proche d'abord, quelques chunks par mise à jour
                for (int n = 0; n < MAX_GENERATED_PER_UPDATE; n++) {
                        int bestDx = 0;
                        int bestDz = 0;
                        int bestDist = Integer.MAX_VALUE;
                        for (int dx = -loadRadius; dx <= loadRadius; dx++) {
                                for (int dz = -loadRadius; dz <= loadRadius; dz++) {
                                        int dist = dx * dx + dz * dz;
                                        if (dist <= loadRadius * loadRadius && dist < bestDist
                                            && world.getChunk(cx + dx, cz + dz) == null) {
                                                bestDist = dist;
                                                bestDx = dx;
                                                bestDz = dz;
                                        }
                                }
                        }
                        if (bestDist == Integer.MAX_VALUE) {
                                break;
                        }
                        generate(cx + bestDx, cz + bestDz);
                }
        }
}