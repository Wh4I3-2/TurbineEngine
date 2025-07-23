#version 450 core

layout(location = 0) in vec4 position;
layout(location = 1) in vec2 texCoord0;

uniform mat4 u_MVP;

uniform ivec2 u_Size;
uniform ivec2 u_Cell;

out vec2 v_TexCoord;

void main() {
    v_TexCoord = vec2((texCoord0 + u_Cell) / u_Size);
    gl_Position = u_MVP * position;
}