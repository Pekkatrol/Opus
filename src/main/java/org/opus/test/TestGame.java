package org.opus.test;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.opus.Launcher;
import org.opus.core.*;
import org.opus.core.entity.Model;
import org.opus.core.entity.Texture;

import static org.opus.core.utils.Consts.CAMERA_STEP;
import static org.opus.core.utils.Consts.MOUSE_SENSITIVITY;

public class TestGame implements ILogic {


        private final RenderManager render;
        private final ObjectLoader loader;
        private final WindowManager window;

        private Entity entity;
        private Camera camera;
        Vector3f cameraInc;

        public TestGame() {
                render = new RenderManager();
                window = Launcher.getWindow();
                loader = new ObjectLoader();
                camera = new Camera();
                cameraInc = new Vector3f(0, 0, 0);
        }

        @Override
        public void init() throws Exception {
                render.init();

                Model model = loader.loadOBJModel("/models/bunny.obj");
                model.setTexture(new Texture(loader.loadTexture("textures/grassblock.png")), 1.0f);
                entity = new Entity(model, new Vector3f(0, 0, -5), new Vector3f(0, 0, 0), 1);
        }

        @Override
        public void input() {
                cameraInc.set(0, 0, 0);
                if (window.isKeyPressed(GLFW.GLFW_KEY_W)) {
                        cameraInc.z = -1;
                }
                if (window.isKeyPressed(GLFW.GLFW_KEY_S)) {
                        cameraInc.z = 1;
                }

                if (window.isKeyPressed(GLFW.GLFW_KEY_A)) {
                        cameraInc.x = -1;
                }
                if (window.isKeyPressed(GLFW.GLFW_KEY_D)) {
                        cameraInc.x = 1;
                }

                if (window.isKeyPressed(GLFW.GLFW_KEY_Z)) {
                        cameraInc.y = -1;
                }
                if (window.isKeyPressed(GLFW.GLFW_KEY_X)) {
                        cameraInc.y = 1;
                }
        }

        @Override
        public void update(MouseInput mouseInput) {
                camera.movePosition(
                    cameraInc.x * CAMERA_STEP,
                    cameraInc.y * CAMERA_STEP,
                    cameraInc.z * CAMERA_STEP);

                if (mouseInput.isRightButtonPress()) {
                        Vector2f rotVec = mouseInput.getDispVec();
                        camera.moveRotation(
                            rotVec.x * MOUSE_SENSITIVITY,
                            rotVec.y * MOUSE_SENSITIVITY,
                            0);
                }

                entity.incRotation(0.0f, 0.25f, 0.0f);
        }

        @Override
        public void render() {
                render.render(entity, camera);
        }

        @Override
        public void cleanup() {
                render.cleanup();
                loader.cleanup();
        }
}
