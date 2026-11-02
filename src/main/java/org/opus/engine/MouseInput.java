package org.opus.engine;

import org.joml.Vector2d;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import org.opus.Launcher;

public class MouseInput {

        private final Vector2d previousPos;
        private final Vector2d currentPos;
        private final Vector2f dispVec;
        private boolean leftButtonPress = false;
        private boolean rightButtonPress = false;
        private boolean leftClick = false;
        private boolean rightClick = false;
        // Mises à jour à ignorer après un changement de mode du curseur
        private int skipUpdates = 2;

        public MouseInput() {
                previousPos = new Vector2d(0, 0);
                currentPos = new Vector2d(0, 0);
                dispVec = new Vector2f();
        }

        public void init() {
                long handle = Launcher.getWindow().getWindowHandle();

                GLFW.glfwSetMouseButtonCallback(handle, (window, button, action, mods) -> {
                        if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
                                leftButtonPress = action != GLFW.GLFW_RELEASE;
                                if (action == GLFW.GLFW_PRESS) {
                                        leftClick = true;
                                }
                        }
                        if (button == GLFW.GLFW_MOUSE_BUTTON_2) {
                                rightButtonPress = action != GLFW.GLFW_RELEASE;
                                if (action == GLFW.GLFW_PRESS) {
                                        rightClick = true;
                                }
                        }
                });

                GLFW.glfwSetCursorPosCallback(handle, (window, xpos, ypos) -> {
                        currentPos.x = xpos;
                        currentPos.y = ypos;
                });

                syncPosition();
        }

        // Aligne la position courante et précédente sur la vraie position du curseur
        private void syncPosition() {
                double[] x = new double[1];
                double[] y = new double[1];
                GLFW.glfwGetCursorPos(Launcher.getWindow().getWindowHandle(), x, y);
                currentPos.set(x[0], y[0]);
                previousPos.set(x[0], y[0]);
        }

        public void input() {
                dispVec.set(0, 0);

                if (skipUpdates > 0) {
                        skipUpdates--;
                        previousPos.set(currentPos);
                        return;
                }

                // Rotation autour de X = mouvement vertical, autour de Y = mouvement horizontal
                dispVec.x = (float) (currentPos.y - previousPos.y);
                dispVec.y = (float) (currentPos.x - previousPos.x);
                previousPos.set(currentPos);
        }

        public void reset() {
                syncPosition();
                skipUpdates = 2;
        }

        public boolean isRightButtonPress() {
                return rightButtonPress;
        }

        public boolean isLeftButtonPress() {
                return leftButtonPress;
        }

        public Vector2f getDispVec() {
                return dispVec;
        }

        // Retourne true une seule fois par clic
        public boolean consumeLeftClick() {
                boolean result = leftClick;
                leftClick = false;
                return result;
        }

        public boolean consumeRightClick() {
                boolean result = rightClick;
                rightClick = false;
                return result;
        }
}