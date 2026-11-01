package org.opus.core.rendering;

import org.opus.core.Camera;
import org.opus.core.SpotLight;
import org.opus.core.entity.Model;
import org.opus.core.lighting.DirectionalLight;
import org.opus.core.lighting.PointLight;

public interface IRenderer<T> {

        public void init() throws Exception;

        public void render(Camera camera, PointLight[] pointLights, SpotLight[] spotLights, DirectionalLight directionalLight);

        abstract void bind(Model model);

        public void unbind();

        public void prepare(T t, Camera camera);

        public void cleanup();
}
