package org.opus.core.rendering;

import org.lwjgl.opengl.GL11;
import org.opus.Launcher;
import org.opus.core.Camera;
import org.opus.core.ShaderManager;
import org.opus.core.SpotLight;
import org.opus.core.WindowManager;
import org.opus.core.entity.Entity;
import org.opus.core.entity.terrain.Terrain;
import org.opus.core.lighting.DirectionalLight;
import org.opus.core.lighting.PointLight;

import java.util.ArrayList;
import java.util.List;

import static org.opus.core.utils.Consts.AMBIENT_LIGHT;
import static org.opus.core.utils.Consts.SPECULAR_POWER;

public class RenderManager {

        private final WindowManager window;
        private EntityRenderer entityRenderer;
        private TerrainRenderer terrainRenderer;

        public RenderManager() {
                window = Launcher.getWindow();
        }

        public void init() throws Exception {
                entityRenderer = new EntityRenderer();
                terrainRenderer = new TerrainRenderer();
                entityRenderer.init();
                terrainRenderer.init();

        }

        public static void renderLights(PointLight[] pointLights, SpotLight[] spotLights, DirectionalLight directionalLight, ShaderManager shader) {
                shader.setUniform("ambientLight", AMBIENT_LIGHT);
                shader.setUniform("specularPower", SPECULAR_POWER);
                shader.setUniform("pointLights", pointLights);
                shader.setUniform("spotLights", spotLights);
                shader.setUniform("directionalLight", directionalLight);

        }

        public void render(Camera camera, DirectionalLight directionalLight, PointLight[] pointLights, SpotLight[] spotLights) {
                clear();

                if (window.isResize()) {
                        GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
                        window.setResize(true);
                }

                entityRenderer.render(camera, pointLights, spotLights, directionalLight);
                terrainRenderer.render(camera, pointLights, spotLights, directionalLight);
        }

        public void processEntity(Entity entity) {
                List<Entity> entityList = entityRenderer.getEntities().get(entity.getModel());
                if (entityList != null) {
                        entityList.add(entity);
                } else {
                        List<Entity> newEntityList = new ArrayList<>();
                        newEntityList.add(entity);
                        entityRenderer.getEntities().put(entity.getModel(), newEntityList);
                }
        }

        public void processTerrain(Terrain terrain) {
                terrainRenderer.getTerrains().add(terrain);
        }

        public void clear() {
                GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        }

        public void cleanup() {
                entityRenderer.cleanup();
                terrainRenderer.cleanup();
        }
}
