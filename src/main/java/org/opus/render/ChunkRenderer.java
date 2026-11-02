package org.opus.render;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import org.opus.Launcher;
import org.opus.engine.WindowManager;
import org.opus.player.Camera;
import org.opus.utils.Utils;
import org.opus.world.Chunk;
import org.opus.world.ChunkMesher;
import org.opus.world.World;

import java.util.HashMap;
import java.util.Map;

import static org.opus.utils.Consts.RENDER_DISTANCE;
import static org.opus.utils.Consts.SKY;

public class ChunkRenderer {

        // Limite de nouveaux meshes par image, pour éviter les saccades
        private static final int MAX_NEW_MESHES_PER_FRAME = 4;

        private final WindowManager window;
        private final Map<Chunk, Mesh> meshes = new HashMap<>();
        private final Vector3f offset = new Vector3f();
        private final Vector3f fogColour = new Vector3f(SKY[0], SKY[1], SKY[2]);
        private final float fogEnd = (RENDER_DISTANCE - 1) * Chunk.SIZE;
        private final float fogStart = fogEnd * 0.5f;
        private ShaderManager shader;
        private int texture;

        public ChunkRenderer() {
                window = Launcher.getWindow();
        }

        public void init() throws Exception {
                shader = new ShaderManager();
                shader.createVertexShader(Utils.loadResource("/shaders/block.vert"));
                shader.createFragmentShader(Utils.loadResource("/shaders/block.frag"));
                shader.link();
                shader.createUniform("textureSampler");
                shader.createUniform("projectionMatrix");
                shader.createUniform("viewMatrix");
                shader.createUniform("chunkOffset");
                shader.createUniform("fogColour");
                shader.createUniform("fogStart");
                shader.createUniform("fogEnd");

                texture = TextureLoader.load("/textures/blocks.png");
        }

        public void render(Camera camera, World world) {
                // Libère les meshes des chunks déchargés
                for (Chunk chunk : world.pollRemovedChunks()) {
                        Mesh old = meshes.remove(chunk);
                        if (old != null) {
                                old.cleanup();
                        }
                }

                // Reconstruit les chunks modifiés ou nouveaux, une fois leurs 4 voisins chargés.
                // Un chunk déjà dessiné (modification d'un bloc) est reconstruit tout de suite.
                int budget = MAX_NEW_MESHES_PER_FRAME;
                for (Chunk chunk : world.getChunks()) {
                        if (!chunk.isDirty() || !world.hasAllNeighbours(chunk)) {
                                continue;
                        }
                        boolean hasMesh = meshes.containsKey(chunk);
                        if (!hasMesh) {
                                if (budget == 0) {
                                        continue;
                                }
                                budget--;
                        }

                        Mesh old = meshes.remove(chunk);
                        if (old != null) {
                                old.cleanup();
                        }
                        Mesh mesh = ChunkMesher.build(world, chunk);
                        if (mesh != null) {
                                meshes.put(chunk, mesh);
                        }
                        chunk.setDirty(false);
                }

                shader.bind();
                shader.setUniform("textureSampler", 0);
                shader.setUniform("projectionMatrix", window.updateProjectionMatrix());
                shader.setUniform("viewMatrix", createViewMatrix(camera));
                shader.setUniform("fogColour", fogColour);
                shader.setUniform("fogStart", fogStart);
                shader.setUniform("fogEnd", fogEnd);

                GL13.glActiveTexture(GL13.GL_TEXTURE0);
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);

                for (Map.Entry<Chunk, Mesh> entry : meshes.entrySet()) {
                        Chunk chunk = entry.getKey();
                        Mesh mesh = entry.getValue();
                        offset.set(chunk.getWorldX(), 0, chunk.getWorldZ());
                        shader.setUniform("chunkOffset", offset);
                        GL30.glBindVertexArray(mesh.getVao());
                        GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.getIndexCount(), GL11.GL_UNSIGNED_INT, 0);
                }

                GL30.glBindVertexArray(0);
                shader.unbind();
        }

        static Matrix4f createViewMatrix(Camera camera) {
                Vector3f pos = camera.getPosition();
                Vector3f rot = camera.getRotation();
                return new Matrix4f()
                    .rotateX((float) Math.toRadians(rot.x))
                    .rotateY((float) Math.toRadians(rot.y))
                    .rotateZ((float) Math.toRadians(rot.z))
                    .translate(-pos.x, -pos.y, -pos.z);
        }

        public void cleanup() {
                for (Mesh mesh : meshes.values()) {
                        mesh.cleanup();
                }
                meshes.clear();
                GL11.glDeleteTextures(texture);
                shader.cleanup();
        }
}