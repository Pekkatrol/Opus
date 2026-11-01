package org.opus.core.rendering;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.opus.Launcher;
import org.opus.core.Camera;
import org.opus.core.ShaderManager;
import org.opus.core.SpotLight;
import org.opus.core.entity.Entity;
import org.opus.core.entity.Model;
import org.opus.core.lighting.DirectionalLight;
import org.opus.core.lighting.PointLight;
import org.opus.core.utils.Transformation;
import org.opus.core.utils.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.opus.core.utils.Consts.MAX_POINTLIGHTS;
import static org.opus.core.utils.Consts.MAX_SPOTLIGHTS;

public class EntityRenderer implements  IRenderer {
        ShaderManager shader;
        private Map<Model, List<Entity>> entities;

        public EntityRenderer() throws Exception {
                entities = new HashMap<>();
                shader = new ShaderManager();
        }

        @Override
        public void init() throws Exception {
                shader.createVertexShader(Utils.loadResource("/shaders/entity_vertex.vs"));
                shader.createFragmentShader(Utils.loadResource("/shaders/entity_fragment.fs"));
                shader.link();
                shader.createUniform("textureSampler");
                shader.createUniform("transformationMatrix");
                shader.createUniform("projectionMatrix");
                shader.createUniform("viewMatrix");
                shader.createUniform("ambientLight");
                shader.createMaterialUniform("material");
                shader.createUniform("specularPower");
                shader.createDirectionalLightUniform("directionalLight");
                shader.createPointLightListUniform("pointLights", MAX_POINTLIGHTS);
                shader.createSpotLightListUniform("spotLights", MAX_SPOTLIGHTS);
        }

        @Override
        public void render(Camera camera, PointLight[] pointLights, SpotLight[] spotLights, DirectionalLight directionalLight) {
                shader.bind();

                shader.setUniform("projectionMatrix", Launcher.getWindow().updateProjectionMatrix());
                RenderManager.renderLights(pointLights, spotLights, directionalLight, shader);
                for (Model model : entities.keySet()) {
                        bind(model);
                        List<Entity> entityList = entities.get(model);
                        for (Entity entity : entityList) {
                                prepare(entity, camera);
                                GL11.glDrawElements(GL11.GL_TRIANGLES, entity.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
                        }
                        unbind();
                }

                entities.clear();
                shader.unbind();
        }

        @Override
        public void bind(Model model) {
                GL30.glBindVertexArray(model.getId());
                GL20.glEnableVertexAttribArray(0);
                GL20.glEnableVertexAttribArray(1);
                GL20.glEnableVertexAttribArray(2);
                shader.setUniform("material", model.getMaterial());
                GL13.glActiveTexture(GL13.GL_TEXTURE0);
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getId());
        }

        @Override
        public void unbind() {
                GL20.glDisableVertexAttribArray(0);
                GL20.glDisableVertexAttribArray(1);
                GL20.glDisableVertexAttribArray(2);
                GL30.glBindVertexArray(0);
        }

        @Override
        public void prepare(Object entity, Camera camera) {
                shader.setUniform("textureSampler", 0);
                shader.setUniform("transformationMatrix", Transformation.createTransformationMatrix((Entity) entity));
                shader.setUniform("viewMatrix", Transformation.getViewMatrix(camera));
        }

        @Override
        public void cleanup() {
                shader.cleanup();
        }

        public Map<Model, List<Entity>> getEntities() {
                return entities;
        }
}
