package org.opus.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class World {

        private final Map<Long, Chunk> chunks = new HashMap<>();
        private final List<Chunk> removedChunks = new ArrayList<>();

        private static long key(int cx, int cz) {
                return ((long) cx << 32) | (cz & 0xFFFFFFFFL);
        }

        public Chunk getChunk(int cx, int cz) {
                return chunks.get(key(cx, cz));
        }

        public Chunk createChunk(int cx, int cz) {
                Chunk chunk = new Chunk(cx, cz);
                chunks.put(key(cx, cz), chunk);
                // Les voisins doivent redessiner leurs faces de bord
                markDirty(cx - 1, cz);
                markDirty(cx + 1, cz);
                markDirty(cx, cz - 1);
                markDirty(cx, cz + 1);
                return chunk;
        }

        public void removeChunk(Chunk chunk) {
                if (chunks.remove(key(chunk.getChunkX(), chunk.getChunkZ())) != null) {
                        removedChunks.add(chunk);
                }
        }

        // Chunks déchargés depuis le dernier appel (le rendu libère leurs meshes)
        public List<Chunk> pollRemovedChunks() {
                if (removedChunks.isEmpty()) {
                        return Collections.emptyList();
                }
                List<Chunk> result = new ArrayList<>(removedChunks);
                removedChunks.clear();
                return result;
        }

        public boolean hasAllNeighbours(Chunk chunk) {
                int cx = chunk.getChunkX();
                int cz = chunk.getChunkZ();
                return getChunk(cx - 1, cz) != null && getChunk(cx + 1, cz) != null
                    && getChunk(cx, cz - 1) != null && getChunk(cx, cz + 1) != null;
        }

        // Coordonnées du monde
        public byte getBlock(int x, int y, int z) {
                if (y < 0 || y >= Chunk.HEIGHT) {
                        return Block.AIR;
                }
                Chunk chunk = getChunk(Math.floorDiv(x, Chunk.SIZE), Math.floorDiv(z, Chunk.SIZE));
                if (chunk == null) {
                        return Block.AIR;
                }
                return chunk.get(Math.floorMod(x, Chunk.SIZE), y, Math.floorMod(z, Chunk.SIZE));
        }

        // Coordonnées du monde
        public void setBlock(int x, int y, int z, byte id) {
                if (y < 0 || y >= Chunk.HEIGHT) {
                        return;
                }
                int cx = Math.floorDiv(x, Chunk.SIZE);
                int cz = Math.floorDiv(z, Chunk.SIZE);
                Chunk chunk = getChunk(cx, cz);
                if (chunk == null) {
                        return;
                }

                int lx = Math.floorMod(x, Chunk.SIZE);
                int lz = Math.floorMod(z, Chunk.SIZE);
                chunk.set(lx, y, lz, id);
                chunk.setModified(true);

                // Bloc au bord : le chunk voisin doit être redessiné aussi
                if (lx == 0) {
                        markDirty(cx - 1, cz);
                }
                if (lx == Chunk.SIZE - 1) {
                        markDirty(cx + 1, cz);
                }
                if (lz == 0) {
                        markDirty(cx, cz - 1);
                }
                if (lz == Chunk.SIZE - 1) {
                        markDirty(cx, cz + 1);
                }
        }

        private void markDirty(int cx, int cz) {
                Chunk chunk = getChunk(cx, cz);
                if (chunk != null) {
                        chunk.setDirty(true);
                }
        }

        public Collection<Chunk> getChunks() {
                return chunks.values();
        }
}