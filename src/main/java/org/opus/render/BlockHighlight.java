package org.opus.render;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.opus.Launcher;
import org.opus.engine.WindowManager;
import org.opus.player.Camera;
import org.opus.player.Raycast;
import org.opus.utils.Utils;

public class BlockHighlight {

        // Légèrement plus grand que le bloc, pour éviter que les lignes soient cachées par ses faces
        private static final float MIN = -0.003f;
        private static final float MAX = 1.003f;

        private final WindowManager window;
        private final Vector3f pos = new Vector3f();
        private ShaderManager shader;
        private int vao;
        private int vbo;

        public BlockHighlight() {
                window = Launcher.getWindow();
        }

        public void init() throws Exception {
                shader = new ShaderManager();
                shader.createVertexShader(Utils.loadResource("/shaders/outline.vert"));
                shader.createFragmentShader(Utils.loadResource("/shaders/outline.frag"));
                shader.link();
                shader.createUniform("projectionMatrix");
                shader.createUniform("viewMatrix");
                shader.createUniform("blockPos");

                // 12 arêtes : deux coins qui ne diffèrent que d'un seul bit (x, y ou z)
                float[] vertices = new float[24 * 3];
                int n = 0;
                for (int i = 0; i < 8; i++) {
                        for (int bit = 0; bit < 3; bit++) {
                                if ((i & (1 << bit)) != 0) {
                                        continue;
                                }
                                n = putCorner(vertices, n, i);
                                n = putCorner(vertices, n, i | (1 << bit));
                        }
                }

                vao = GL30.glGenVertexArrays();
                GL30.glBindVertexArray(vao);
                vbo = GL15.glGenBuffers();
                GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
                GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertices, GL15.GL_STATIC_DRAW);
                GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);
                GL20.glEnableVertexAttribArray(0);
                GL30.glBindVertexArray(0);
                GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        }

        private static int putCorner(float[] v, int n, int corner) {
                v[n++] = (corner & 1) != 0 ? MAX : MIN;
                v[n++] = (corner & 2) != 0 ? MAX : MIN;
                v[n++] = (corner & 4) != 0 ? MAX : MIN;
                return n;
        }

        public void render(Camera camera, Raycast.Hit hit) {
                if (hit == null) {
                        return;
                }

                shader.bind();
                shader.setUniform("projectionMatrix", window.updateProjectionMatrix());
                shader.setUniform("viewMatrix", ChunkRenderer.createViewMatrix(camera));
                shader.setUniform("blockPos", pos.set(hit.x, hit.y, hit.z));

                GL11.glLineWidth(2f);
                GL30.glBindVertexArray(vao);
                GL11.glDrawArrays(GL11.GL_LINES, 0, 24);
                GL30.glBindVertexArray(0);
                shader.unbind();
        }

        public void cleanup() {
                GL15.glDeleteBuffers(vbo);
                GL30.glDeleteVertexArrays(vao);
                shader.cleanup();
        }
}