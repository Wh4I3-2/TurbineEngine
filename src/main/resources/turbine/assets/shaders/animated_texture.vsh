#version 450 core

layout(location = 0) in vec4 position;
layout(location = 1) in vec2 texCoord0;

uniform int u_Frames;
uniform float u_Time;
uniform float u_FPS;

uniform mat4 u_MVP;

out vec2 v_TexCoord;

void main() {
    float frame = float(int(floor(-u_Time * u_FPS)) % u_Frames);

    v_TexCoord = vec2(texCoord0.x, (texCoord0.y + frame) / float(u_Frames));
    gl_Position = u_MVP * position;
}