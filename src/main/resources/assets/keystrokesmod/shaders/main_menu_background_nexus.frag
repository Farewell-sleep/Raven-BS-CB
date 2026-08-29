#ifdef GL_ES
precision mediump float;
#endif

#extension GL_OES_standard_derivatives : enable

uniform vec2 resolution;
uniform float time;

/*
* Advanced shader with noise, particles, and complex patterns
*/

// Hash function for noise
float hash(vec2 p) {
    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453);
}

// 2D noise function
float noise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    f = f * f * (3.0 - 2.0 * f);
    
    float a = hash(i);
    float b = hash(i + vec2(1.0, 0.0));
    float c = hash(i + vec2(0.0, 1.0));
    float d = hash(i + vec2(1.0, 1.0));
    
    return mix(mix(a, b, f.x), mix(c, d, f.x), f.y);
}

// Fractal noise (octaves)
float fbm(vec2 p) {
    float value = 0.0;
    float amplitude = 0.5;
    float frequency = 1.0;
    
    for (int i = 0; i < 4; i++) {
        value += amplitude * noise(p * frequency);
        frequency *= 2.0;
        amplitude *= 0.5;
    }
    
    return value;
}

// Rotate 2D vector
vec2 rotate2D(vec2 v, float angle) {
    float s = sin(angle);
    float c = cos(angle);
    return vec2(
        v.x * c - v.y * s,
        v.x * s + v.y * c
    );
}

// HSV to RGB conversion
vec3 hsv2rgb(vec3 c) {
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

// Particle field
float particles(vec2 uv, float t) {
    float particleField = 0.0;
    
    // Create multiple particle layers
    for (int i = 0; i < 8; i++) {
        float fi = float(i);
        vec2 offset = vec2(
            sin(t * 0.3 + fi * 0.7) * 0.5,
            cos(t * 0.4 + fi * 0.9) * 0.5
        );
        
        vec2 pPos = vec2(
            fract(offset.x + fi * 0.3) - 0.5,
            fract(offset.y + fi * 0.4) - 0.5
        ) * 2.0;
        
        vec2 dist = uv - pPos;
        float d = length(dist);
        
        // Glowing particles
        particleField += 0.02 / (d * d + 0.01);
    }
    
    return particleField;
}

// Voronoi-like cells
float voronoi(vec2 p, float t) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    
    float minDist = 1.0;
    
    for (int y = -1; y <= 1; y++) {
        for (int x = -1; x <= 1; x++) {
            vec2 neighbor = vec2(float(x), float(y));
            vec2 point = hash(i + neighbor) * vec2(1.0) + neighbor;
            point = 0.5 + 0.5 * sin(t * 0.5 + point * 6.2831);
            
            vec2 diff = neighbor + point - f;
            float dist = length(diff);
            minDist = min(minDist, dist);
        }
    }
    
    return minDist;
}

void main() {
    vec2 uv = gl_FragCoord.xy / resolution.xy;
    vec2 p = (gl_FragCoord.xy * 2.0 - resolution.xy) / resolution.y;
    
    // Animated rotation
    float angle = time * 0.2;
    vec2 rotatedP = rotate2D(p, angle);
    
    // Base noise layer
    vec2 noiseCoord = rotatedP * 2.0 + vec2(time * 0.1, time * 0.15);
    float n = fbm(noiseCoord);
    
    // Voronoi pattern
    float vor = voronoi(rotatedP * 3.0 + vec2(time * 0.2), time);
    vor = smoothstep(0.0, 0.5, vor);
    
    // Particle field
    float particles = particles(rotatedP * 0.8, time);
    
    // Radial gradient
    float dist = length(p);
    float radial = 1.0 - smoothstep(0.0, 1.2, dist);
    
    // Combine patterns
    float pattern1 = n * 0.5 + vor * 0.3 + particles * 0.2;
    float pattern2 = sin(length(rotatedP) * 4.0 - time * 2.0) * 0.5 + 0.5;
    
    // Color calculation using HSV
    float hue = time * 0.1 + pattern1 * 0.3;
    hue = mod(hue, 1.0);
    
    float saturation = 0.7 + 0.2 * pattern2;
    float value = 0.3 + 0.4 * radial + 0.2 * pattern1 + 0.1 * particles;
    
    vec3 hsvColor = vec3(hue, saturation, value);
    vec3 color = hsv2rgb(hsvColor);
    
    // Add glow effect from particles
    color += vec3(0.3, 0.4, 0.6) * particles * 0.5;
    
    // Add voronoi edge highlights
    float vorEdge = 1.0 - smoothstep(0.0, 0.1, vor);
    color += vec3(0.2, 0.3, 0.5) * vorEdge * 0.3;
    
    // Add noise-based detail
    color += vec3(0.1) * (n - 0.5) * 0.5;
    
    // Final adjustments
    color = clamp(color, 0.0, 1.0);
    
    // Add subtle vignette
    float vignette = 1.0 - smoothstep(0.5, 1.5, dist);
    color *= 0.7 + 0.3 * vignette;
    
    gl_FragColor = vec4(color, 1.0);
}

