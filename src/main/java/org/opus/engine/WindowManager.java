package org.opus.engine;

import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import static org.opus.utils.Consts.*;

public class WindowManager {


        private final String title;
        private int width;
        private int height;
        private long window;
        private boolean resize;
        private boolean vSync;

        private final Matrix4f projectionMatrix;

        public WindowManager(String title, int width, int height, boolean vSync) {
                this.title = title;
                this.width = width;
                this.height = height;
                this.vSync = vSync;
                projectionMatrix = new Matrix4f();
        }

        public void init() {
                GLFWErrorCallback.createPrint(System.err).set();

                if (!GLFW.glfwInit()) {
                        throw new IllegalStateException("Unable to initialize GLFW");
                }

                GLFW.glfwDefaultWindowHints();
                GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GL11.GL_FALSE);
                GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
                GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2);
                GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
                GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GL11.GL_TRUE);

                boolean maximised = false;

                if (width == 0 || height == 0) {
                        width = 100;
                        height = 100;
                        GLFW.glfwWindowHint(GLFW.GLFW_MAXIMIZED, GLFW.GLFW_TRUE);
                        maximised = true;
                }

                window = GLFW.glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL);
                if (window == MemoryUtil.NULL) {
                        throw new RuntimeException("Failed to create GLFW window");
                }

                GLFW.glfwSetFramebufferSizeCallback(window, (window, width, height) -> {
                        this.width = width;
                        this.height = height;
                        this.setResize(true);
                });

                GLFW.glfwSetKeyCallback(window, (window, key,  scancode, action, mods) -> {
                        if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE) {
                                GLFW.glfwSetWindowShouldClose(window, true);
                        }
                });

                if (maximised) {
                        GLFW.glfwMaximizeWindow(window);
                } else {
                        GLFWVidMode vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
                        GLFW.glfwSetWindowPos(window,
                            (vidMode.width() - width) / 2,
                            (vidMode.height() - height) / 2
                        );
                }

                GLFW.glfwMakeContextCurrent(window);

                if (isvSync()) {
                        GLFW.glfwSwapInterval(1);
                }

                GLFW.glfwShowWindow(window);

                GL.createCapabilities();

                GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
                GL11.glEnable(GL11.GL_DEPTH_TEST);
                GL11.glEnable(GL11.GL_STENCIL_TEST);
                GL11.glEnable(GL11.GL_CULL_FACE);
                GL11.glCullFace(GL11.GL_BACK);
        }

        public void update() {
                GLFW.glfwSwapBuffers(window);
                GLFW.glfwPollEvents();
        }

        public void cleanup() {
                GLFW.glfwDestroyWindow(window);
        }

        public void setClearColour(float r, float g, float b, float a) {
                GL11.glClearColor(r, g, b, a);
        }

        public boolean  isKeyPressed(int keycode) {
                return GLFW.glfwGetKey(window, keycode) == GLFW.GLFW_PRESS;
        }

        public boolean windowShouldClose() {
                return GLFW.glfwWindowShouldClose(window);
        }

        public String getTitle() {
                return title;
        }

        public void setTitle(String title) {
                GLFW.glfwSetWindowTitle(window, title);
        }

        public boolean isResize() {
                return resize;
        }

        public void setResize(boolean resize) {
                this.resize = resize;
        }

        public boolean isvSync() {
                return vSync;
        }

        public int getWidth() {
                return width;
        }

        public int getHeight() {
                return height;
        }

        public long getWindowHandle() {
                return window;
        }

        public Matrix4f getProjectionMatrix() {
                return projectionMatrix;
        }

        public Matrix4f updateProjectionMatrix() {
                float aspectRatio = (float) width / height;
                return projectionMatrix.setPerspective(FOV, aspectRatio, Z_NEAR, Z_FAR);
        }

        public Matrix4f updateProjectionMatrix(Matrix4f matrix, int width, int height) {
                float aspectRatio = (float) width / height;
                return matrix.setPerspective(FOV, aspectRatio, Z_NEAR, Z_FAR);
        }

        public void setCursorCaptured(boolean captured) {
                GLFW.glfwSetInputMode(getWindowHandle(), GLFW.GLFW_CURSOR,
                    captured ? GLFW.GLFW_CURSOR_DISABLED : GLFW.GLFW_CURSOR_CAPTURED);
        }

        private boolean fullscreen = false;
        private int windowedX;
        private int windowedY;
        private int windowedWidth;
        private int windowedHeight;

        public void toggleFullscreen() {
                long handle = getWindowHandle();
                if (!fullscreen) {
                        int[] x = new int[1];
                        int[] y = new int[1];
                        int[] w = new int[1];
                        int[] h = new int[1];
                        GLFW.glfwGetWindowPos(handle, x, y);
                        GLFW.glfwGetWindowSize(handle, w, h);
                        windowedX = x[0];
                        windowedY = y[0];
                        windowedWidth = w[0];
                        windowedHeight = h[0];

                        long monitor = GLFW.glfwGetPrimaryMonitor();
                        GLFWVidMode mode = GLFW.glfwGetVideoMode(monitor);
                        GLFW.glfwSetWindowMonitor(handle, monitor, 0, 0, mode.width(), mode.height(), mode.refreshRate());
                } else {
                        GLFW.glfwSetWindowMonitor(handle, 0L, windowedX, windowedY, windowedWidth, windowedHeight, GLFW.GLFW_DONT_CARE);
                }
                fullscreen = !fullscreen;
                GLFW.glfwSwapInterval(1);
        }
}
