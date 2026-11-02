package org.opus.engine;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.opus.Launcher;

import static org.opus.utils.Consts.TITLE;

public class EngineManager {

        public static final long NANOSECOND = 1000000000L;
        public static final int UPS = 60;
        private static final double UPDATE_STEP = 1.0 / UPS;
        private static final double MAX_ACCUMULATED = 0.25;

        private static int fps;

        private boolean isRunning;

        private WindowManager window;
        private MouseInput mouseInput;
        private GLFWErrorCallback errorCallback;
        private ILogic gameLogic;

        private void init() throws Exception {
                GLFW.glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
                window = Launcher.getWindow();
                gameLogic = Launcher.getGame();
                mouseInput = new MouseInput();
                window.init();
                gameLogic.init();
                mouseInput.init();
        }

        public void start() throws Exception {
                init();
                if (isRunning) {
                        return;
                }
                run();
        }

        public void run() {
                this.isRunning = true;
                int frames = 0;
                long frameCounter = 0;
                long lastTime = System.nanoTime();
                double accumulator = 0;

                while (isRunning) {
                        long startTime = System.nanoTime();
                        long passedTime = startTime - lastTime;
                        lastTime = startTime;

                        accumulator += passedTime / (double) NANOSECOND;
                        frameCounter += passedTime;

                        if (accumulator > MAX_ACCUMULATED) {
                                accumulator = MAX_ACCUMULATED;
                        }

                        if (window.windowShouldClose()) {
                                stop();
                        }

                        while (accumulator >= UPDATE_STEP) {
                                input();
                                update();
                                accumulator -= UPDATE_STEP;
                        }

                        render();
                        frames++;

                        if (frameCounter >= NANOSECOND) {
                                setFps(frames);
                                window.setTitle(TITLE + " --- FPS: " + getFps());
                                frames = 0;
                                frameCounter = 0;
                        }
                }
                cleanup();
        }

        private void stop() {
                if (!isRunning) {
                        return;
                }
                isRunning = false;
        }

        private void input() {
                mouseInput.input();
                gameLogic.input();
        }

        private void render() {
                gameLogic.render();
                window.update();
        }

        private void update() {
                gameLogic.update(mouseInput);
        }

        private void cleanup() {
                gameLogic.cleanup();
                window.cleanup();
                errorCallback.free();
                GLFW.glfwTerminate();
        }

        public static int getFps() {
                return fps;
        }

        public static void setFps(int fps) {
                EngineManager.fps = fps;
        }
}