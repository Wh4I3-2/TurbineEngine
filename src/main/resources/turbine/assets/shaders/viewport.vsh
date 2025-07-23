#version 450 core

layout(location = 0) in vec4 position;
layout(location = 1) in vec2 texCoord0;

out vec2 v_TexCoord;

void main() {
    v_TexCoord = texCoord0;
    gl_Position = position;
}