package org.opus.core;

import org.lwjgl.opengl.GL20;

public class ShaderManager {

        private final int programeID;
        private int vertexShaderID;
        private int fragmentShaderID;

        public ShaderManager() throws Exception {
                programeID = GL20.glCreateProgram();
                if (programeID == 0) {
                        throw new Exception("Could not create shader");
                }
        }

        public void createVertexShader(String shaderCode) throws Exception {
                vertexShaderID = createShader(shaderCode, GL20.GL_VERTEX_SHADER);
        }

        public void createFragmentShader(String shaderCode) throws Exception {
                fragmentShaderID = createShader(shaderCode, GL20.GL_FRAGMENT_SHADER);
        }

        public int createShader(String shaderCode, int shaderType) throws Exception {
                int shaderID = GL20.glCreateShader(shaderType);
                if (shaderID == 0) {
                        throw new Exception("Error creating shader. Type : " + shaderType);
                }

                GL20.glShaderSource(shaderID, shaderCode);
                GL20.glCompileShader(shaderID);

                if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == 0) {
                        throw new Exception("Error compiling shader code: TYPE: " + shaderType + " Info " + GL20.glGetShaderInfoLog(shaderID, 1024));
                }

                GL20.glAttachShader(programeID, shaderID);

                return shaderID;
        }

        public void link() throws Exception {
                GL20.glLinkProgram(programeID);
                if (GL20.glGetProgrami(programeID, GL20.GL_LINK_STATUS) == 0) {
                        throw new Exception("Error linking shader code  Info " + GL20.glGetProgramInfoLog(programeID, 1024));
                }

                if (vertexShaderID != 0) {
                        GL20.glDetachShader(programeID , vertexShaderID);
                }

                if (fragmentShaderID != 0) {
                        GL20.glDetachShader(programeID , fragmentShaderID);
                }

                GL20.glValidateProgram(programeID);
                if (GL20.glGetProgrami(programeID, GL20.GL_VALIDATE_STATUS) == 0) {
                        throw new Exception("Enable to validate shader code: " + GL20.glGetProgramInfoLog(programeID, 1024));
                }
        }

        public void bind() {
                GL20.glUseProgram(programeID);
        }

        public void unbind() {
                GL20.glUseProgram(0);
        }

        public void cleanup() {
                unbind();
                if (programeID != 0) {
                        GL20.glDeleteProgram(programeID);
                }
        }
}
