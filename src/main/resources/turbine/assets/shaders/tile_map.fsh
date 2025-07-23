#version 450 core

layout(location = 0) out vec4 color;

uniform sampler2D u_Tex0;
uniform vec3 u_Modulate;

in vec2 v_TexCoord;
in float v_TexSelector;

void main() {
    vec4 col0 = texture(u_Tex0, v_TexCoord);

    color = col0;
    color.rgb *= u_Modulate;
}