package org.opus.render;

import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

public class Mesh {

        private final int vao;
        private final int vbo;
        private final int ebo;
        private final int indexCount;

        public Mesh(int vao, int vbo, int ebo, int indexCount) {
                this.vao = vao;
                this.vbo = vbo;
                this.ebo = ebo;
                this.indexCount = indexCount;
        }

        public int getVao() {
                return vao;
        }

        public int getIndexCount() {
                return indexCount;
        }

        public void cleanup() {
                GL15.glDeleteBuffers(vbo);
                GL15.glDeleteBuffers(ebo);
                GL30.glDeleteVertexArrays(vao);
        }
}