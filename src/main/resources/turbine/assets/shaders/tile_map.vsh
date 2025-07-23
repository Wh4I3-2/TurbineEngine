#version 450 core

layout(location = 0) in vec4 position;
layout(location = 1) in vec2 texCoord0;
layout(location = 2) in float tex;

uniform mat4 u_MVP;

out vec2 v_TexCoord;
out float v_TexSelector;

void main() {
    v_TexCoord = texCoord0;
    v_TexSelector = tex;
    gl_Position = u_MVP * position;
}