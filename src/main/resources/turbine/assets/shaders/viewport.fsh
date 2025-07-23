#version 450 core

layout(location = 0) out vec4 color;

uniform sampler2D u_Sampler;
uniform float u_Time;
uniform vec2 u_WindowSize;
uniform vec2 u_ViewportSize;

in vec2 v_TexCoord;

float random (vec2 st) {
    return fract(sin(dot(st.xy,
    vec2(12.9898,78.233)))
    * 43758.5453123);
}
float noise(vec2 x) {
    vec2 p = floor(x);
    vec2 f = fract(x);
    f = f*f*(3.0-2.0*f);
    float a = random(p);
    float b = random(p+vec2(1.0,.0));
    float c = random(p+vec2(.0,1.));
    float d = random(p+vec2(1.0,1.));
    return mix(mix( a, b,f.x), mix( c, d,f.x),f.y);
}

const mat2 mtx = mat2( 0.80,  0.60, -0.60,  0.80 );

float fbm4(vec2 p) {
    float f = 0.0;

    f += 0.5000*(-1.0+2.0*noise(p)); p = mtx*p*2.02;
    f += 0.2500*(-1.0+2.0*noise(p)); p = mtx*p*2.03;
    f += 0.1250*(-1.0+2.0*noise(p)); p = mtx*p*2.01;
    f += 0.0625*(-1.0+2.0*noise(p));

    return f/0.9375;
}

vec2 fbm42(vec2 p) {
    return vec2( fbm4(p+vec2(1.0)), fbm4(p+vec2(6.2)) );
}
vec2 sampleAt(vec2 q, float speed) {
    float jacked_time = u_Time*speed;
    const vec2 scale = vec2(0.11,0.13);
    q += 0.5*sin(scale*jacked_time + length(q)*0.5);
    vec2 o = fbm42(q);
    o += 0.01*sin(scale*jacked_time*length(o));
    return o;
}

vec3 rgb2hsv(vec3 rgb) {
    vec4 p = (rgb.g < rgb.b) ? vec4(rgb.bg, -1., 2. / 3.) : vec4(rgb.gb, 0., -1. / 3.);
    vec4 q = (rgb.r < p.x) ? vec4(p.xyw, rgb.r) : vec4(rgb.r, p.yzx);
    float c = q.x - min(q.w, q.y);
    float h = abs((q.w - q.y) / (6. * c + 1e-10) + q.z);
    vec3 hcv = vec3(h, c, q.x);
    float s = hcv.y / (hcv.z + 1e-10);
    return vec3(hcv.x, s, hcv.z);
}

vec3 hsv2rgb(vec3 c) {
    vec3 rgb = clamp(abs(mod(c.x * 6.0 + vec3(0.0, 4.0, 2.0), 6.0) - 3.0) - 1.0, 0.0, 1.0);
    return c.z * mix(vec3(1.0), rgb, c.y);
}

vec3 invert(vec3 col) {
    return vec3(1.0 - col.r, 1.0 - col.g, 1.0 - col.b);
}

vec3 colA(vec2 uv) {
    float v = random(round(sampleAt(uv * 0.01, 1.0) + vec2(u_Time * 0.1, u_Time * 0.1) + uv * 0.01) * 0.00001);
    vec3 col = vec3(0.325, 0.071, 0.149);
    vec3 hsv = rgb2hsv(mix(col, invert(col), v) * 0.4);
    float h = float(int((hsv.x + u_Time * 0.03)*200.0) % 200) / 200.0;
    return hsv2rgb(vec3(h, 0.3, hsv.z * 0.5));
}
vec3 colB(vec2 uv) {
    float v = random(round(sampleAt(uv * 0.01 + 0.53425, -1.0) - vec2(u_Time * 0.1, u_Time * 0.1) + uv * 0.01) * 0.00001);
    vec3 col = vec3(0.325, 0.071, 0.149);
    vec3 hsv = rgb2hsv(mix(col, invert(col), v) * 0.4);
    float h = float(int((hsv.x + 0.1 + u_Time * 0.03)*200.0) % 200) / 200.0;
    return hsv2rgb(vec3(h, 0.3, hsv.z * 0.5));
}

vec4 background() {
    float ratio = u_WindowSize.x / u_ViewportSize.x;

    vec2 pos = floor(gl_FragCoord.xy / ratio) * ratio;

    int x1 = int(floor((pos.x - u_Time * 0.5) / 32.0));
    int y1 = int(floor((pos.y - u_Time * 0.5) / 32.0));
    bool x2 = bool(int(floor((pos.x + u_Time * 0.5 * 20.0 * (float(y1 % 2) - 0.5) * 2.0) / 32.0)) % 2);
    bool y2 = bool(int(floor((pos.y + u_Time * 0.5 * 20.0 * (float(x1 % 2) - 0.5) * 2.0) / 32.0)) % 2);

    return vec4(mix(colA(pos), colB(pos), float(x2 ^^ y2)), 1.0);
}

void main() {
    vec4 viewportColor = texture(u_Sampler, v_TexCoord);
    color = mix(background(), viewportColor, viewportColor.a);
}