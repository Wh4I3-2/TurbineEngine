#version 450 core

layout(location = 0) in vec4 position;
layout(location = 1) in vec2 texCoord0;

uniform vec2 u_WindowSize;
uniform vec2 u_ViewportSize;
uniform vec2 u_SubPixel;

out vec2 v_TexCoord;

void main() {
    v_TexCoord = texCoord0;
    vec2 posXY = vec2(position.xy * (vec2(1.0) + 1.0 / u_ViewportSize * 2.0) + u_SubPixel / u_ViewportSize * 2.0);
    gl_Position = vec4(posXY, position.zw);
}