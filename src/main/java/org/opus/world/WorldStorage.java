package org.opus.world;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public class WorldStorage {

        private final Path directory;

        public WorldStorage(Path directory) {
                this.directory = directory;
                try {
                        Files.createDirectories(directory);
                } catch (IOException e) {
                        throw new UncheckedIOException(e);
                }
        }

        private Path chunkFile(int cx, int cz) {
                return directory.resolve("c." + cx + "." + cz + ".bin");
        }

        // Retourne true si le chunk a été rempli depuis le disque
        public boolean loadChunk(Chunk chunk) {
                Path file = chunkFile(chunk.getChunkX(), chunk.getChunkZ());
                if (!Files.exists(file)) {
                        return false;
                }
                try (DataInputStream in = new DataInputStream(
                    new InflaterInputStream(new BufferedInputStream(Files.newInputStream(file))))) {
                        int size = in.readInt();
                        if (size != Chunk.VOLUME) {
                                return false;
                        }
                        byte[] data = new byte[size];
                        in.readFully(data);
                        chunk.setBlocks(data);
                        return true;
                } catch (IOException e) {
                        System.err.println("Chunk illisible, il sera régénéré : " + file + " (" + e.getMessage() + ")");
                        return false;
                }
        }

        public void saveChunk(Chunk chunk) {
                Path file = chunkFile(chunk.getChunkX(), chunk.getChunkZ());
                Path temp = file.resolveSibling(file.getFileName() + ".tmp");
                try {
                        try (DataOutputStream out = new DataOutputStream(
                            new DeflaterOutputStream(new BufferedOutputStream(Files.newOutputStream(temp))))) {
                                out.writeInt(Chunk.VOLUME);
                                out.write(chunk.getBlocks());
                        }
                        Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                        System.err.println("Sauvegarde du chunk impossible : " + file + " (" + e.getMessage() + ")");
                }
        }

        // {x, y, z, yaw, pitch} ou null s'il n'y a pas de sauvegarde
        public float[] loadPlayer() {
                Path file = directory.resolve("player.dat");
                if (!Files.exists(file)) {
                        return null;
                }
                try (DataInputStream in = new DataInputStream(new BufferedInputStream(Files.newInputStream(file)))) {
                        float[] data = new float[5];
                        for (int i = 0; i < data.length; i++) {
                                data[i] = in.readFloat();
                        }
                        return data;
                } catch (IOException e) {
                        return null;
                }
        }

        public void savePlayer(float[] data) {
                Path file = directory.resolve("player.dat");
                try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(Files.newOutputStream(file)))) {
                        for (float value : data) {
                                out.writeFloat(value);
                        }
                } catch (IOException e) {
                        System.err.println("Sauvegarde du joueur impossible : " + e.getMessage());
                }
        }
}