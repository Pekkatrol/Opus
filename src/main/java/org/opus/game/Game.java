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
import org.opus.world.World;

import static org.opus.utils.Consts.MOUSE_SENSITIVITY;

public class Game implements ILogic {

        private static final float REACH = 6f;

        private final RenderManager render;
        private final WindowManager window;
        private final Camera camera;
        private final Player player;
        private final Vector3f moveInput;
        private final Vector3f lookDir;
        private final World world;

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
        }

        @Override
        public void init() throws Exception {
                render.init();
                window.setCursorCaptured(true);

                // Terrain de test : 4 x 4 chunks, collines en sinus, herbe / terre / pierre
                for (int cx = -2; cx < 2; cx++) {
                        for (int cz = -2; cz < 2; cz++) {
                                Chunk chunk = world.createChunk(cx, cz);
                                for (int x = 0; x < Chunk.SIZE; x++) {
                                        for (int z = 0; z < Chunk.SIZE; z++) {
                                                int wx = cx * Chunk.SIZE + x;
                                                int wz = cz * Chunk.SIZE + z;
                                                int height = 20 + (int) (4 * Math.sin(wx * 0.1) + 4 * Math.cos(wz * 0.1));
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

                // Apparition sur le bloc le plus haut en (0, 0)
                int y = Chunk.HEIGHT - 1;
                while (y > 0 && !Block.isSolid(world.getBlock(0, y, 0))) {
                        y--;
                }
                player.setSpawn(0.5f, y + 1f, 0.5f);
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
                window.setClearColour(0.47f, 0.73f, 1.0f, 1f);
                render.render(camera, world, target, !inMenu);
        }

        @Override
        public void cleanup() {
                render.cleanup();
        }
}