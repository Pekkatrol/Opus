package org.opus.player;

import org.joml.Vector3f;
import org.opus.world.Block;
import org.opus.world.World;

public class Raycast {

        public static class Hit {
                // Bloc touché
                public final int x, y, z;
                // Dernier bloc vide traversé (là où on pose)
                public final int px, py, pz;

                public Hit(int x, int y, int z, int px, int py, int pz) {
                        this.x = x;
                        this.y = y;
                        this.z = z;
                        this.px = px;
                        this.py = py;
                        this.pz = pz;
                }
        }

        // Algorithme DDA (Amanatides et Woo) : avance de case en case le long du rayon
        public static Hit cast(World world, Vector3f origin, Vector3f dir, float maxDistance) {
                int x = (int) Math.floor(origin.x);
                int y = (int) Math.floor(origin.y);
                int z = (int) Math.floor(origin.z);

                int stepX = dir.x > 0 ? 1 : -1;
                int stepY = dir.y > 0 ? 1 : -1;
                int stepZ = dir.z > 0 ? 1 : -1;

                float tDeltaX = dir.x != 0 ? Math.abs(1f / dir.x) : Float.POSITIVE_INFINITY;
                float tDeltaY = dir.y != 0 ? Math.abs(1f / dir.y) : Float.POSITIVE_INFINITY;
                float tDeltaZ = dir.z != 0 ? Math.abs(1f / dir.z) : Float.POSITIVE_INFINITY;

                float tMaxX = dir.x > 0 ? (x + 1 - origin.x) / dir.x
                    : dir.x < 0 ? (origin.x - x) / -dir.x : Float.POSITIVE_INFINITY;
                float tMaxY = dir.y > 0 ? (y + 1 - origin.y) / dir.y
                    : dir.y < 0 ? (origin.y - y) / -dir.y : Float.POSITIVE_INFINITY;
                float tMaxZ = dir.z > 0 ? (z + 1 - origin.z) / dir.z
                    : dir.z < 0 ? (origin.z - z) / -dir.z : Float.POSITIVE_INFINITY;

                int px = x;
                int py = y;
                int pz = z;
                float t = 0;

                while (t <= maxDistance) {
                        if (Block.isSolid(world.getBlock(x, y, z))) {
                                return new Hit(x, y, z, px, py, pz);
                        }

                        px = x;
                        py = y;
                        pz = z;

                        if (tMaxX < tMaxY && tMaxX < tMaxZ) {
                                x += stepX;
                                t = tMaxX;
                                tMaxX += tDeltaX;
                        } else if (tMaxY < tMaxZ) {
                                y += stepY;
                                t = tMaxY;
                                tMaxY += tDeltaY;
                        } else {
                                z += stepZ;
                                t = tMaxZ;
                                tMaxZ += tDeltaZ;
                        }
                }
                return null;
        }
}