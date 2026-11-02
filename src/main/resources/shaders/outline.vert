#version 400 core

layout(location = 0) in vec3 position;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 blockPos;

void main() {
    gl_Position = projectionMatrix * viewMatrix * vec4(position + blockPos, 1.0);
}