#version 400 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 uv;
layout(location = 2) in float shade;

out vec2 fragUv;
out float fragShade;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 chunkOffset;

void main() {
    gl_Position = projectionMatrix * viewMatrix * vec4(position + chunkOffset, 1.0);
    fragUv = uv;
    fragShade = shade;
}