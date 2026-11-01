package org.opus.test;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.opus.Launcher;
import org.opus.core.*;
import org.opus.core.entity.Entity;
import org.opus.core.entity.Model;
import org.opus.core.entity.Texture;
import org.opus.core.lighting.DirectionalLight;
import org.opus.core.lighting.PointLight;
import org.opus.core.rendering.RenderManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.opus.core.utils.Consts.CAMERA_STEP;
import static org.opus.core.utils.Consts.MOUSE_SENSITIVITY;

public class TestGame implements ILogic {


        private final RenderManager render;
        private final ObjectLoader loader;
        private final WindowManager window;

        private List<Entity> entities;
        private Camera camera;
        Vector3f cameraInc;

        private float lightAngle;
        private DirectionalLight directionalLight;
        private PointLight[] pointLights;
        private SpotLight[] spotLights;

        public TestGame() {
                render = new RenderManager();
                window = Launcher.getWindow();
                loader = new ObjectLoader();
                camera = new Camera();
                cameraInc = new Vector3f(0, 0, 0);
                lightAngle = -90;

        }

        @Override
        public void init() throws Exception {
                render.init();

                Model model = loader.loadOBJModel("/models/cube.obj");
                model.setTexture(new Texture(loader.loadTexture("textures/grassblock.png")), 1.0f);

                entities = new ArrayList<>();
                Random rnd = new Random();
                for (int i = 0; i < 200; i++) {
                        float x = rnd.nextFloat() * 100 - 50;
                        float y = rnd.nextFloat() * 100 - 50;
                        float z = rnd.nextFloat() * -300;
                        entities.add(new Entity(model, new Vector3f(x, y, z),
                            new Vector3f(rnd.nextFloat() * 180, rnd.nextFloat() * 180, 0), 1));
                }

                entities.add(new Entity(model, new Vector3f(0, 0, -2f), new Vector3f(0, 0, 0), 1));

                float lightIntensity = 1.0f;

                // point light
                Vector3f  lightPosition = new Vector3f(-0.5f, -0.5f, -3.2f);
                Vector3f lightColour = new Vector3f(1, 1, 1);
                PointLight pointLight = new PointLight(lightColour, lightPosition, lightIntensity, 0, 0, 1);

                //spot light
                Vector3f coneDir = new Vector3f(0, 0, 1);
                float cutoff = (float) Math.cos(Math.toRadians(180));
                SpotLight spotLight = new SpotLight(new PointLight(lightColour, new Vector3f(0, 0, 1f), lightIntensity, 0, 0, 1), coneDir, cutoff);

                SpotLight spotLight1 = new SpotLight(pointLight, coneDir, cutoff);
                spotLight1.getPointLight().setPosition(new Vector3f(0.5f, 0.5f, -3.6f));
                //directional light
                lightPosition = new Vector3f(-1, -10, 0);
                lightColour = new Vector3f(1, 1, 1);
                directionalLight = new DirectionalLight(lightColour, lightPosition, lightIntensity);

                pointLights = new PointLight[]{pointLight};
                spotLights = new SpotLight[]{spotLight, spotLight1};
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

                if (window.isKeyPressed(GLFW.GLFW_KEY_O)) {
                       pointLights[0].getPosition().x += 0.1f;
                }

                if (window.isKeyPressed(GLFW.GLFW_KEY_P)) {
                        pointLights[0].getPosition().x -= 0.1f;
                }

                float lightPos = spotLights[0].getPointLight().getPosition().z;
                if (window.isKeyPressed(GLFW.GLFW_KEY_N)) {
                        spotLights[0].getPointLight().getPosition().z = lightPos + 0.1f;
                }

                if (window.isKeyPressed(GLFW.GLFW_KEY_M)) {
                        spotLights[0].getPointLight().getPosition().z = lightPos - 0.1f;
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

                //entity.incRotation(0.0f, 0.25f, 0.0f);

                lightAngle += 0.5f;
                if (lightAngle > 90) {
                        directionalLight.setIntensity(0);
                        if (lightAngle >= 360) {
                                lightAngle = -90;
                        }
                } else if (lightAngle <= -90 || lightAngle >= 80) {
                        float factor = 1 - (Math.abs(lightAngle) - 80) / 10.0f;
                        directionalLight.setIntensity(factor);
                        directionalLight.getColour().y = Math.max(factor, 0.9f);
                        directionalLight.getColour().z = Math.max(factor, 0.5f);
                } else {
                        directionalLight.setIntensity(1);
                        directionalLight.getColour().x = 1;
                        directionalLight.getColour().y = 1;
                        directionalLight.getColour().z = 1;
                }
                double angRad = Math.toRadians(lightAngle);
                directionalLight.getDirection().x = (float) Math.sin(angRad);
                directionalLight.getDirection().y = (float) Math.cos(angRad);

                for (Entity entity : entities) {
                        render.processEntity(entity);
                }
        }

        @Override
        public void render() {
                render.render(camera, directionalLight, pointLights, spotLights);
        }

        @Override
        public void cleanup() {
                render.cleanup();
                loader.cleanup();
        }
}
