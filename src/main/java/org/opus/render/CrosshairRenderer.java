package org.opus.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.opus.Launcher;
import org.opus.engine.WindowManager;
import org.opus.utils.Utils;

public class CrosshairRenderer {

        private static final float LENGTH = 0.025f;
        private static final float THICKNESS = 0.003f;

        private final WindowManager window;
        private ShaderManager shader;
        private int vao;
        private int vbo;
        private int vertexCount;

        public CrosshairRenderer() {
                window = Launcher.getWindow();
        }

        public void init() throws Exception {
                shader = new ShaderManager();
                shader.createVertexShader(Utils.loadResource("/shaders/hud.vert"));
                shader.createFragmentShader(Utils.loadResource("/shaders/hud.frag"));
                shader.link();
                shader.createUniform("aspect");

                // Deux rectangles : une barre horizontale et une barre verticale
                float[] vertices = new float[24];
                int n = 0;
                n = rect(vertices, n, -LENGTH, -THICKNESS, LENGTH, THICKNESS);
                n = rect(vertices, n, -THICKNESS, -LENGTH, THICKNESS, LENGTH);
                vertexCount = n / 2;

                vao = GL30.glGenVertexArrays();
                GL30.glBindVertexArray(vao);
                vbo = GL15.glGenBuffers();
                GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
                GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertices, GL15.GL_STATIC_DRAW);
                GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 0, 0);
                GL20.glEnableVertexAttribArray(0);
                GL30.glBindVertexArray(0);
                GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        }

        private static int rect(float[] v, int n, float x0, float y0, float x1, float y1) {
                v[n++] = x0; v[n++] = y0;
                v[n++] = x1; v[n++] = y0;
                v[n++] = x1; v[n++] = y1;
                v[n++] = x0; v[n++] = y0;
                v[n++] = x1; v[n++] = y1;
                v[n++] = x0; v[n++] = y1;
                return n;
        }

        public void render() {
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                GL11.glDisable(GL11.GL_CULL_FACE);
                GL11.glEnable(GL11.GL_BLEND);
                // Inverse la couleur du fond : le viseur reste visible partout
                GL11.glBlendFunc(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ZERO);

                shader.bind();
                shader.setUniform("aspect", (float) window.getWidth() / window.getHeight());
                GL30.glBindVertexArray(vao);
                GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vertexCount);
                GL30.glBindVertexArray(0);
                shader.unbind();

                GL11.glDisable(GL11.GL_BLEND);
                GL11.glEnable(GL11.GL_CULL_FACE);
                GL11.glEnable(GL11.GL_DEPTH_TEST);
        }

        public void cleanup() {
                GL15.glDeleteBuffers(vbo);
                GL30.glDeleteVertexArrays(vao);
                shader.cleanup();
        }
}