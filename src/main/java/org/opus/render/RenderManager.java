package org.opus.render;

import org.lwjgl.opengl.GL11;
import org.opus.Launcher;
import org.opus.engine.WindowManager;
import org.opus.player.Camera;
import org.opus.player.Raycast;
import org.opus.world.World;

public class RenderManager {

        private final WindowManager window;
        private ChunkRenderer chunkRenderer;
        private BlockHighlight blockHighlight;
        private CrosshairRenderer crosshairRenderer;

        public RenderManager() {
                window = Launcher.getWindow();
        }

        public void init() throws Exception {
                GL11.glEnable(GL11.GL_DEPTH_TEST);
                GL11.glEnable(GL11.GL_CULL_FACE);
                GL11.glCullFace(GL11.GL_BACK);

                chunkRenderer = new ChunkRenderer();
                chunkRenderer.init();
                blockHighlight = new BlockHighlight();
                blockHighlight.init();
                crosshairRenderer = new CrosshairRenderer();
                crosshairRenderer.init();
        }

        public void render(Camera camera, World world, Raycast.Hit target, boolean showCrosshair) {
                clear();

                if (window.isResize()) {
                        GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
                        window.setResize(false);
                }

                chunkRenderer.render(camera, world);
                blockHighlight.render(camera, target);
                if (showCrosshair) {
                        crosshairRenderer.render();
                }
        }

        public void clear() {
                GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        }

        public void cleanup() {
                chunkRenderer.cleanup();
                blockHighlight.cleanup();
                crosshairRenderer.cleanup();
        }
}