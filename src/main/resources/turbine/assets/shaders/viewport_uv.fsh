#version 450 core

layout(location = 0) out vec4 color;
uniform vec2 u_ViewportSize;
uniform vec2 u_ViewportOffset;
uniform float u_Time;

void main() {
    vec2 viewportUV = (gl_FragCoord.xy - u_ViewportOffset) / u_ViewportSize;
    color = vec4(viewportUV.x, viewportUV.y, (sin(u_Time * 2.0) + 1.0) / 2.0, 1.0);
}