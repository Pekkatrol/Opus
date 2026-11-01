package org.opus.test;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.opus.Launcher;
import org.opus.core.*;
import org.opus.core.entity.*;
import org.opus.core.entity.terrain.Terrain;
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
        private SceneManager sceneManager;
        private Camera camera;
        Vector3f cameraInc;

        public TestGame() {
                render = new RenderManager();
                window = Launcher.getWindow();
                loader = new ObjectLoader();
                camera = new Camera();
                camera.setPosition(0, 4, 0);
                cameraInc = new Vector3f(0, 0, 0);
                sceneManager = new SceneManager(-90);

        }

        @Override
        public void init() throws Exception {
                render.init();

                Model model = loader.loadOBJModel("/models/cube.obj");
                model.setTexture(new Texture(loader.loadTexture("textures/grassblock.png")), 1.0f);

                Terrain terrain = new Terrain(new Vector3f(0, 1, -800), loader, new Material(new Texture(loader.loadTexture("textures/terrain.png")), 0.1f));
                Terrain terrain2 = new Terrain(new Vector3f(-800, 1, -800), loader, new Material(new Texture(loader.loadTexture("textures/flowers.png")), 0.1f));
                sceneManager.addTerrain(terrain);
                sceneManager.addTerrain(terrain2);

                Random rnd = new Random();
                for (int i = 0; i < 2000; i++) {
                        float x = Math.round(rnd.nextFloat() * 800);
                        float z = -Math.round(rnd.nextFloat() * 300);
                        sceneManager.addEntity(new Entity(model, new Vector3f(x, 1.5f, z),
                            new Vector3f(0, 0, 0), 1));
                }

                sceneManager.addEntity(new Entity(model, new Vector3f(0, 1.5f, -2f), new Vector3f(0, 0, 0), 1));

                float lightIntensity = 1.0f;

                // point light
                Vector3f  lightPosition = new Vector3f(-0.5f, -0.5f, -3.2f);
                Vector3f lightColour = new Vector3f(1, 1, 1);
                PointLight pointLight = new PointLight(lightColour, lightPosition, lightIntensity, 0, 0, 1);

                //spot light
                Vector3f coneDir = new Vector3f(0, -50, 0);
                float cutoff = (float) Math.cos(Math.toRadians(25));
                lightIntensity = 60f;
                SpotLight spotLight = new SpotLight(new PointLight(new Vector3f(1f, 0.85f, 0.6f), new Vector3f(10f, 50f, -10f),
                    lightIntensity, 0f, 0f, 0.02f), coneDir, cutoff);

                SpotLight spotLight1 = new SpotLight(new PointLight(new Vector3f(0.6f, 0.8f, 1f), new Vector3f(40f, 50f, -10f),
                    lightIntensity, 0f, 0f, 0.02f), new Vector3f(0, -50, 0), cutoff);

                //directional light
                lightIntensity = 1f;
                lightPosition = new Vector3f(-1, 0, 0);
                lightColour = new Vector3f(1, 1, 1);
                sceneManager.setDirectionalLight(new DirectionalLight(lightColour, lightPosition, lightIntensity));

                sceneManager.setPointLights(new PointLight[]{pointLight});
                sceneManager.setSpotLights( new SpotLight[]{spotLight, spotLight1});
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
                        sceneManager.getPointLights()[0].getPosition().x += 0.1f;
                }

                if (window.isKeyPressed(GLFW.GLFW_KEY_P)) {
                        sceneManager.getPointLights()[0].getPosition().x -= 0.1f;
                }

                float lightPos = sceneManager.getSpotLights()[0].getPointLight().getPosition().z;
                if (window.isKeyPressed(GLFW.GLFW_KEY_N)) {
                        sceneManager.getSpotLights()[0].getPointLight().getPosition().z = lightPos + 0.1f;
                }

                if (window.isKeyPressed(GLFW.GLFW_KEY_M)) {
                        sceneManager.getSpotLights()[0].getPointLight().getPosition().z = lightPos - 0.1f;
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

                sceneManager.incSpotAngle(0.15f);
                if (sceneManager.getSpotAngle() > 4) {
                        sceneManager.setSpotInc(-1);
                } else if (sceneManager.getSpotAngle() <= -4) {
                        sceneManager.setSpotInc(1);
                }

                double spotAngleRad = Math.toRadians(sceneManager.getSpotAngle());
                Vector3f coneDir = sceneManager.getSpotLights()[0].getConeDirection();
                coneDir.x = (float) Math.sin(spotAngleRad);

                coneDir = sceneManager.getSpotLights()[1].getConeDirection();
                coneDir.z = (float) Math.cos(spotAngleRad * 0.15);

                sceneManager.incLightAngle(0.1f);
                if (sceneManager.getLightAngle() > 90) {
                        sceneManager.getDirectionalLight().setIntensity(0);
                        if (sceneManager.getLightAngle() >= 360) {
                                sceneManager.setLightAngle(-90);
                        }
                } else if (sceneManager.getLightAngle() <= -80 || sceneManager.getLightAngle() >= 80) {
                        float factor = 1 - (Math.abs(sceneManager.getLightAngle()) - 80) / 10.0f;
                        sceneManager.getDirectionalLight().setIntensity(factor);
                        sceneManager.getDirectionalLight().getColour().y = Math.max(factor, 0.9f);
                        sceneManager.getDirectionalLight().getColour().z = Math.max(factor, 0.5f);
                } else {
                        sceneManager.getDirectionalLight().setIntensity(1);
                        sceneManager.getDirectionalLight().getColour().x = 1;
                        sceneManager.getDirectionalLight().getColour().y = 1;
                        sceneManager.getDirectionalLight().getColour().z = 1;
                }
                double angRad = Math.toRadians(sceneManager.getLightAngle());
                sceneManager.getDirectionalLight().getDirection().x = (float) Math.sin(angRad);
                sceneManager.getDirectionalLight().getDirection().y = (float) Math.cos(angRad);

                for (Entity entity : sceneManager.getEntities()) {
                        render.processEntity(entity);
                }

                for (Terrain terrain : sceneManager.getTerrains()) {
                        render.processTerrain(terrain);
                }
        }

        @Override
        public void render() {
                float day = Math.max(0, sceneManager.getDirectionalLight().getIntensity());
                window.setClearColour(0.45f * day + 0.02f, 0.70f * day + 0.03f, 0.95f * day + 0.06f, 1f);
                render.render(camera, sceneManager);
        }

        @Override
        public void cleanup() {
                render.cleanup();
                loader.cleanup();
        }
}
