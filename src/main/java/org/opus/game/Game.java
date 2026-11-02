package org.opus.game;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.opus.Launcher;
import org.opus.engine.ILogic;
import org.opus.engine.MouseInput;
import org.opus.engine.WindowManager;
import org.opus.player.Camera;
import org.opus.player.Player;
import org.opus.player.Raycast;
import org.opus.render.RenderManager;
import org.opus.world.Block;
import org.opus.world.Chunk;
import org.opus.world.ChunkManager;
import org.opus.world.World;
import org.opus.world.WorldGenerator;
import org.opus.world.WorldStorage;

import java.nio.file.Path;

import static org.opus.utils.Consts.MOUSE_SENSITIVITY;
import static org.opus.utils.Consts.RENDER_DISTANCE;
import static org.opus.utils.Consts.SKY;

public class Game implements ILogic {

        private static final float REACH = 6f;
        private static final long SEED = 20261102L;

        private final RenderManager render;
        private final WindowManager window;
        private final Camera camera;
        private final Player player;
        private final Vector3f moveInput;
        private final Vector3f lookDir;
        private final World world;
        private final WorldStorage storage;
        private final ChunkManager chunkManager;

        private Raycast.Hit target;
        private byte selectedBlock = Block.GRASS;
        private boolean jump = false;
        private boolean inMenu = false;
        private boolean escapeWasPressed = false;
        private boolean flyKeyWasPressed = false;
        private boolean fullscreenKeyWasPressed = false;

        public Game() {
                render = new RenderManager();
                window = Launcher.getWindow();
                camera = new Camera();
                player = new Player();
                moveInput = new Vector3f(0, 0, 0);
                lookDir = new Vector3f();
                world = new World();
                storage = new WorldStorage(Path.of("saves", "world-" + SEED));
                chunkManager = new ChunkManager(world, new WorldGenerator(SEED), storage, RENDER_DISTANCE);
        }

        @Override
        public void init() throws Exception {
                render.init();
                window.setCursorCaptured(true);

                // {x, y, z, yaw, pitch} de la dernière partie, ou null
                float[] saved = storage.loadPlayer();
                float startX = saved != null ? saved[0] : 0.5f;
                float startZ = saved != null ? saved[2] : 0.5f;

                // Charge (ou génère) le monde autour du point de départ
                chunkManager.loadAround(startX, startZ);

                if (saved != null) {
                        player.setSpawn(saved[0], saved[1], saved[2]);
                        Vector3f rot = camera.getRotation();
                        camera.moveRotation(saved[4] - rot.x, saved[3] - rot.y, 0);
                } else {
                        // Première partie : apparition sur le bloc le plus haut en (0, 0)
                        int y = Chunk.HEIGHT - 1;
                        while (y > 0 && !Block.isSolid(world.getBlock(0, y, 0))) {
                                y--;
                        }
                        player.setSpawn(0.5f, y + 1f, 0.5f);
                }
                syncCamera();
        }

        @Override
        public void input() {
                moveInput.set(0, 0, 0);
                jump = false;
                if (inMenu) {
                        return;
                }
                if (window.isKeyPressed(GLFW.GLFW_KEY_W)) {
                        moveInput.z = -1;
                }
                if (window.isKeyPressed(GLFW.GLFW_KEY_S)) {
                        moveInput.z = 1;
                }
                if (window.isKeyPressed(GLFW.GLFW_KEY_A)) {
                        moveInput.x = -1;
                }
                if (window.isKeyPressed(GLFW.GLFW_KEY_D)) {
                        moveInput.x = 1;
                }
                if (window.isKeyPressed(GLFW.GLFW_KEY_SPACE)) {
                        jump = true;
                        moveInput.y = 1;
                }
                if (window.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT)) {
                        moveInput.y = -1;
                }
        }

        @Override
        public void update(MouseInput mouseInput) {
                boolean escapePressed = window.isKeyPressed(GLFW.GLFW_KEY_ESCAPE);
                if (escapePressed && !escapeWasPressed) {
                        inMenu = !inMenu;
                        window.setCursorCaptured(!inMenu);
                        mouseInput.reset();
                }
                escapeWasPressed = escapePressed;

                boolean fullscreenKeyPressed = window.isKeyPressed(GLFW.GLFW_KEY_F11);
                if (fullscreenKeyPressed && !fullscreenKeyWasPressed) {
                        window.toggleFullscreen();
                        mouseInput.reset();
                }
                fullscreenKeyWasPressed = fullscreenKeyPressed;

                boolean flyKeyPressed = window.isKeyPressed(GLFW.GLFW_KEY_F);
                if (flyKeyPressed && !flyKeyWasPressed && !inMenu) {
                        player.toggleFlying();
                }
                flyKeyWasPressed = flyKeyPressed;

                // Choix du bloc à poser : 1 herbe, 2 terre, 3 pierre
                if (window.isKeyPressed(GLFW.GLFW_KEY_1)) {
                        selectedBlock = Block.GRASS;
                } else if (window.isKeyPressed(GLFW.GLFW_KEY_2)) {
                        selectedBlock = Block.DIRT;
                } else if (window.isKeyPressed(GLFW.GLFW_KEY_3)) {
                        selectedBlock = Block.STONE;
                }

                // Toujours consommer les clics, pour ne pas en garder un en attente
                boolean leftClick = mouseInput.consumeLeftClick();
                boolean rightClick = mouseInput.consumeRightClick();

                if (!inMenu) {
                        Vector2f rotVec = mouseInput.getDispVec();
                        camera.moveRotation(
                            rotVec.x * MOUSE_SENSITIVITY,
                            rotVec.y * MOUSE_SENSITIVITY,
                            0);
                }

                player.update(world, camera.getRotation().y, moveInput.x, moveInput.z, moveInput.y, jump);
                syncCamera();

                // Charge et décharge les chunks selon la position du joueur
                chunkManager.update(player.getPosition().x, player.getPosition().z);

                target = null;
                if (inMenu) {
                        return;
                }

                // Direction du regard
                Vector3f rot = camera.getRotation();
                lookDir.set(0, 0, -1)
                    .rotateX((float) Math.toRadians(-rot.x))
                    .rotateY((float) Math.toRadians(-rot.y));

                target = Raycast.cast(world, camera.getPosition(), lookDir, REACH);
                if (target == null) {
                        return;
                }

                if (leftClick) {
                        world.setBlock(target.x, target.y, target.z, Block.AIR);
                } else if (rightClick && !player.intersectsBlock(target.px, target.py, target.pz)) {
                        world.setBlock(target.px, target.py, target.pz, selectedBlock);
                }
        }

        // La caméra suit les yeux du joueur
        private void syncCamera() {
                Vector3f pos = player.getPosition();
                camera.setPosition(pos.x, pos.y + Player.EYE_HEIGHT, pos.z);
        }

        @Override
        public void render() {
                window.setClearColour(SKY[0], SKY[1], SKY[2], 1f);
                render.render(camera, world, target, !inMenu);
        }

        @Override
        public void cleanup() {
                // Sauvegarde du monde et du joueur avant de tout libérer
                chunkManager.saveAll();
                Vector3f pos = player.getPosition();
                Vector3f rot = camera.getRotation();
                storage.savePlayer(new float[]{pos.x, pos.y, pos.z, rot.y, rot.x});

                render.cleanup();
        }
}