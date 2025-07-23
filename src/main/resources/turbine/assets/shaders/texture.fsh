#version 450 core

layout(location = 0) out vec4 color;

in vec2 v_TexCoord;

uniform sampler2D u_Sampler;
uniform vec3 u_Modulate;

void main() {
    color = texture(u_Sampler, v_TexCoord);
    color.rgb *= u_Modulate;
}