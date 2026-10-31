package org.opus.test;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.opus.Launcher;
import org.opus.core.ILogic;
import org.opus.core.ObjectLoader;
import org.opus.core.RenderManager;
import org.opus.core.WindowManager;
import org.opus.core.entity.Model;

public class TestGame implements ILogic {

        private int direction = 0;
        private float colour = 0.0f;

        private final RenderManager render;
        private final ObjectLoader loader;
        private final WindowManager window;

        private Model model;

        public TestGame() {
                render = new RenderManager();
                window = Launcher.getWindow();
                loader = new ObjectLoader();
        }

        @Override
        public void init() throws Exception {
                render.init();

                float[] verticies = {
                    -0.5f, 0.5f, 0f,
                    -0.5f, -0.5f, 0f,
                    0.5f, -0.5f, 0f,
                    0.5f, -0.5f, 0f,
                    0.5f, 0.5f, 0f,
                    -0.5f, 0.5f, 0f
                };

                model = loader.loadModel(verticies);
        }

        @Override
        public void input() {
                if (window.isKeyPressed(GLFW.GLFW_KEY_UP)) {
                        direction = 1;
                } else if (window.isKeyPressed(GLFW.GLFW_KEY_DOWN)) {
                        direction = -1;
                } else {
                        direction = 0;
                }
        }

        @Override
        public void update() {
                colour += direction * 0.01f;
                if (colour > 1) {
                        colour = 1.0f;
                } else if (colour <= 0) {
                        colour = 0.0f;
                }
        }

        @Override
        public void render() {
                if (window.isResize()) {
                        GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
                        window.setResize(true);
                }

                window.setClearColour(colour, colour, colour, 0.0f);
                render.render(model);
        }

        @Override
        public void cleanup() {
                render.cleanup();
                loader.cleanup();
        }
}
