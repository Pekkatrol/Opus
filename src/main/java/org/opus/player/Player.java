package org.opus.player;

import org.joml.Vector3f;
import org.opus.engine.EngineManager;
import org.opus.world.Block;
import org.opus.world.World;

public class Player {

        public static final float EYE_HEIGHT = 1.62f;

        private static final float HALF_WIDTH = 0.3f;
        private static final float HEIGHT = 1.8f;
        private static final float EPS = 0.0001f;

        private static final float DT = 1f / EngineManager.UPS;
        private static final float WALK_SPEED = 4.5f;      // blocs par seconde
        private static final float FLY_SPEED = 10f;        // blocs par seconde
        private static final float GRAVITY = 32f;          // blocs par seconde²
        private static final float JUMP_SPEED = 9f;        // blocs par seconde
        private static final float MAX_FALL_SPEED = 50f;   // reste < 1 bloc par mise à jour (pas de traversée)
        private static final float VOID_Y = -30f;

        // Position des pieds
        private final Vector3f position = new Vector3f();
        private final Vector3f spawn = new Vector3f();
        private float velocityY = 0;
        private boolean onGround = false;
        private boolean flying = false;

        public void setSpawn(float x, float y, float z) {
                spawn.set(x, y, z);
                respawn();
        }

        public void respawn() {
                position.set(spawn);
                velocityY = 0;
        }

        // strafe : -1 gauche, +1 droite / forward : -1 avancer, +1 reculer (comme la caméra)
        // vertical : +1 monter, -1 descendre (en vol uniquement)
        public void update(World world, float yawDegrees, float strafe, float forward, float vertical, boolean jump) {
                float yaw = (float) Math.toRadians(yawDegrees);

                float dirX = (float) (Math.sin(yaw) * -forward + Math.cos(yaw) * strafe);
                float dirZ = (float) (Math.cos(yaw) * forward + Math.sin(yaw) * strafe);
                float length = (float) Math.sqrt(dirX * dirX + dirZ * dirZ);
                if (length > 1f) {
                        dirX /= length;
                        dirZ /= length;
                }

                float speed = (flying ? FLY_SPEED : WALK_SPEED) * DT;
                float dx = dirX * speed;
                float dz = dirZ * speed;

                if (flying) {
                        velocityY = vertical * FLY_SPEED;
                } else {
                        if (jump && onGround) {
                                velocityY = JUMP_SPEED;
                        }
                        velocityY = Math.max(velocityY - GRAVITY * DT, -MAX_FALL_SPEED);
                }

                // Recalculé par moveY : vrai seulement si on touche le sol à cette mise à jour
                onGround = false;
                moveX(world, dx);
                moveZ(world, dz);
                moveY(world, velocityY * DT);

                if (position.y < VOID_Y) {
                        respawn();
                }
        }

        private void moveX(World world, float dx) {
                if (dx == 0) {
                        return;
                }
                position.x += dx;
                if (collides(world)) {
                        position.x = dx > 0
                            ? (float) Math.floor(position.x + HALF_WIDTH) - HALF_WIDTH - EPS
                            : (float) Math.floor(position.x - HALF_WIDTH) + 1 + HALF_WIDTH + EPS;
                }
        }

        private void moveZ(World world, float dz) {
                if (dz == 0) {
                        return;
                }
                position.z += dz;
                if (collides(world)) {
                        position.z = dz > 0
                            ? (float) Math.floor(position.z + HALF_WIDTH) - HALF_WIDTH - EPS
                            : (float) Math.floor(position.z - HALF_WIDTH) + 1 + HALF_WIDTH + EPS;
                }
        }

        private void moveY(World world, float dy) {
                if (dy == 0) {
                        return;
                }
                position.y += dy;
                if (collides(world)) {
                        if (dy > 0) {
                                position.y = (float) Math.floor(position.y + HEIGHT) - HEIGHT - EPS;
                        } else {
                                position.y = (float) Math.floor(position.y) + 1 + EPS;
                                onGround = true;
                        }
                        velocityY = 0;
                }
        }

        // Vrai si la boîte du joueur chevauche un bloc plein
        private boolean collides(World world) {
                int minX = (int) Math.floor(position.x - HALF_WIDTH);
                int maxX = (int) Math.floor(position.x + HALF_WIDTH);
                int minY = (int) Math.floor(position.y);
                int maxY = (int) Math.floor(position.y + HEIGHT);
                int minZ = (int) Math.floor(position.z - HALF_WIDTH);
                int maxZ = (int) Math.floor(position.z + HALF_WIDTH);

                for (int x = minX; x <= maxX; x++) {
                        for (int y = minY; y <= maxY; y++) {
                                for (int z = minZ; z <= maxZ; z++) {
                                        if (Block.isSolid(world.getBlock(x, y, z))) {
                                                return true;
                                        }
                                }
                        }
                }
                return false;
        }

        // Pour refuser de poser un bloc dans le joueur
        public boolean intersectsBlock(int bx, int by, int bz) {
                return bx < position.x + HALF_WIDTH && bx + 1 > position.x - HALF_WIDTH
                    && by < position.y + HEIGHT && by + 1 > position.y
                    && bz < position.z + HALF_WIDTH && bz + 1 > position.z - HALF_WIDTH;
        }

        public void toggleFlying() {
                flying = !flying;
                velocityY = 0;
        }

        public boolean isFlying() {
                return flying;
        }

        public Vector3f getPosition() {
                return position;
        }
}