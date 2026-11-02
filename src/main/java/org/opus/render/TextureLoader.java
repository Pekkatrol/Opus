package org.opus.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class TextureLoader {

        // Chemin dans le classpath, par exemple "/textures/grassblock.png"
        public static int load(String resourcePath) throws Exception {
                byte[] bytes;
                try (InputStream in = TextureLoader.class.getResourceAsStream(resourcePath)) {
                        if (in == null) {
                                throw new Exception("Texture introuvable : " + resourcePath);
                        }
                        bytes = in.readAllBytes();
                }

                ByteBuffer data = MemoryUtil.memAlloc(bytes.length);
                data.put(bytes).flip();

                ByteBuffer image;
                int width;
                int height;
                try (MemoryStack stack = MemoryStack.stackPush()) {
                        IntBuffer w = stack.mallocInt(1);
                        IntBuffer h = stack.mallocInt(1);
                        IntBuffer c = stack.mallocInt(1);
                        image = STBImage.stbi_load_from_memory(data, w, h, c, 4);
                        if (image == null) {
                                throw new Exception("Texture non chargée : " + STBImage.stbi_failure_reason());
                        }
                        width = w.get(0);
                        height = h.get(0);
                } finally {
                        MemoryUtil.memFree(data);
                }

                int id = GL11.glGenTextures();
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
                GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
                GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0,
                    GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, image);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
                STBImage.stbi_image_free(image);
                return id;
        }
}