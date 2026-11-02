#version 400 core

in vec2 fragUv;
in float fragShade;
in float fragDist;

out vec4 fragColour;

uniform sampler2D textureSampler;
uniform vec3 fogColour;
uniform float fogStart;
uniform float fogEnd;

void main() {
    vec4 colour = texture(textureSampler, fragUv);
    float fog = clamp((fragDist - fogStart) / (fogEnd - fogStart), 0.0, 1.0);
    fragColour = vec4(mix(colour.rgb * fragShade, fogColour, fog), colour.a);
}