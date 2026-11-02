package org.opus.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class MeshLoader {

        // Sommets entrelacés : x, y, z, u, v, ombrage
        public static Mesh create(float[] vertices, int[] indices) {
                int vao = GL30.glGenVertexArrays();
                GL30.glBindVertexArray(vao);

                int vbo = GL15.glGenBuffers();
                GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
                GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertices, GL15.GL_STATIC_DRAW);

                int ebo = GL15.glGenBuffers();
                GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ebo);
                GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indices, GL15.GL_STATIC_DRAW);

                int stride = 6 * Float.BYTES;
                GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, stride, 0);
                GL20.glEnableVertexAttribArray(0);
                GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, stride, 3L * Float.BYTES);
                GL20.glEnableVertexAttribArray(1);
                GL20.glVertexAttribPointer(2, 1, GL11.GL_FLOAT, false, stride, 5L * Float.BYTES);
                GL20.glEnableVertexAttribArray(2);

                GL30.glBindVertexArray(0);
                GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

                return new Mesh(vao, vbo, ebo, indices.length);
        }
}