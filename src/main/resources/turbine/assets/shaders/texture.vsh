#version 450 core

layout(location = 0) in vec4 position;
layout(location = 1) in vec2 texCoord0;

uniform mat4 u_MVP;

out vec2 v_TexCoord;

void main() {
    v_TexCoord = texCoord0;
    gl_Position = u_MVP * position;
}