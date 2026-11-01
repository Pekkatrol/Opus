package org.opus.core.lighting;

import org.joml.Vector3f;

public class PointLight {

        private Vector3f colour;
        private Vector3f position;
        private float intensity;
        private float constant;
        private float linear;
        float exponent;

        public PointLight(Vector3f colour, Vector3f position, float intensity, float constant, float linear, float exponent) {
                this.colour = colour;
                this.position = position;
                this.intensity = intensity;
                this.constant = constant;
                this.linear = linear;
                this.exponent = exponent;
        }

        public PointLight(Vector3f colour,  Vector3f position, float intensity) {
                this(colour, position, intensity, 1, 0, 0);
        }

        public float getExponent() {
                return exponent;
        }

        public float getLinear() {
                return linear;
        }

        public float getConstant() {
                return constant;
        }

        public float getIntensity() {
                return intensity;
        }

        public Vector3f getPosition() {
                return position;
        }

        public Vector3f getColour() {
                return colour;
        }

        public void setPosition(Vector3f position) {
                this.position = position;
        }
}
