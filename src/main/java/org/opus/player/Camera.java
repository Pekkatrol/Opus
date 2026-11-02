package org.opus.player;

import org.joml.Vector3f;

public class Camera {

        private Vector3f position;
        private Vector3f rotation;

        public Camera() {
                position = new Vector3f(0, 0, 0);
                rotation= new Vector3f(0, 0, 0);
        }

        public Camera(Vector3f position, Vector3f rotation) {
                this.rotation = rotation;
                this.position = position;
        }

        public void movePosition(float x, float y, float z) {
                float yaw = (float) Math.toRadians(rotation.y);

                if (z != 0) {
                        position.x += Math.sin(yaw) * -z;
                        position.z += Math.cos(yaw) * z;
                }
                if (x != 0) {
                        position.x += Math.cos(yaw) * x;
                        position.z += Math.sin(yaw) * x;
                }
                position.y += y;
        }

        public void setPosition(float x, float y, float z) {
                this.position.x = x;
                this.position.y = y;
                this.position.z = z;
        }

        public void setRotation(float x, float y, float z) {
                this.rotation.x = x;
                this.rotation.y = y;
                this.rotation.z = z;
        }

        public void moveRotation(float x, float y, float z) {
                rotation.x = Math.max(-90f, Math.min(90f, rotation.x + x));
                rotation.y = (rotation.y + y) % 360f;
                rotation.z += z;
        }

        public Vector3f getPosition() {
                return position;
        }

        public Vector3f getRotation() {
                return rotation;
        }
}
