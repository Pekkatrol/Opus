#version 400 core

in vec2 fragUv;
in float fragShade;

out vec4 fragColour;

uniform sampler2D textureSampler;

void main() {
    vec4 colour = texture(textureSampler, fragUv);
    fragColour = vec4(colour.rgb * fragShade, colour.a);
}