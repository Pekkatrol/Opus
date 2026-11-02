#version 400 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 uv;
layout(location = 2) in float shade;

out vec2 fragUv;
out float fragShade;
out float fragDist;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 chunkOffset;

void main() {
    vec4 viewPos = viewMatrix * vec4(position + chunkOffset, 1.0);
    gl_Position = projectionMatrix * viewPos;
    fragUv = uv;
    fragShade = shade;
    fragDist = length(viewPos.xyz);
}