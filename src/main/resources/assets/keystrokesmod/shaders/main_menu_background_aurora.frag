#ifdef GL_ES
precision mediump float;
#endif

#extension GL_OES_standard_derivatives : enable

uniform vec2 resolution;
uniform float time;

/*
* Aurora Shader - Beautiful raymarched 3D scene with dynamic colors
* Features: Smooth color transitions, enhanced lighting, atmospheric effects
*/

// Rotation matrix helper
mat2 m(float a) {
    float c = cos(a), s = sin(a);
    return mat2(c, -s, s, c);
}

// Enhanced distance field with more complex patterns
float map(vec3 p) {
    // Smooth rotation animations
    p.xz *= m(time * 0.3);
    p.xy *= m(time * 0.15);
    
    // Create flowing, organic patterns
    vec3 q = p * 2.0 + time * 0.5;
    float wave1 = sin(q.x + sin(q.z + sin(q.y))) * 0.5;
    float wave2 = sin(q.y * 1.3 + time * 0.7) * 0.3;
    float wave3 = cos(q.z * 1.1 - time * 0.5) * 0.2;
    
    // Combine waves for complex surface
    float pattern = wave1 + wave2 + wave3;
    
    // Create distance field with smooth falloff
    float dist = length(p + vec3(sin(time * 0.6) * 0.3, cos(time * 0.4) * 0.2, 0.0));
    float field = dist * log(dist + 1.0) + pattern - 1.2;
    
    return field;
}

// HSV to RGB conversion for smooth color transitions
vec3 hsv2rgb(vec3 c) {
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

// Calculate normal for lighting
vec3 calcNormal(vec3 p) {
    const float eps = 0.01;
    vec3 n = vec3(
        map(p + vec3(eps, 0.0, 0.0)) - map(p - vec3(eps, 0.0, 0.0)),
        map(p + vec3(0.0, eps, 0.0)) - map(p - vec3(0.0, eps, 0.0)),
        map(p + vec3(0.0, 0.0, eps)) - map(p - vec3(0.0, 0.0, eps))
    );
    return normalize(n);
}

void main() {
    vec2 uv = gl_FragCoord.xy / resolution.xy;
    vec2 a = gl_FragCoord.xy / resolution.y - vec2(0.9, 0.5);
    
    // Ray direction
    vec3 rd = normalize(vec3(a, -1.0));
    
    // Ray origin
    vec3 ro = vec3(0.0, 0.0, 4.0);
    
    // Accumulated color
    vec3 cl = vec3(0.0);
    float d = 2.5;
    
    // Enhanced raymarching with more iterations for better quality
    for (int i = 0; i <= 6; i++) {
        vec3 p = ro + rd * d;
        float rz = map(p);
        
        // Calculate surface normal for lighting
        vec3 n = calcNormal(p);
        
        // Enhanced lighting calculation
        vec3 lightDir = normalize(vec3(1.0, 2.0, -1.0) + vec3(sin(time * 0.3), cos(time * 0.4), 0.0) * 0.5);
        float diff = max(dot(n, lightDir), 0.0);
        float spec = pow(max(dot(reflect(-lightDir, n), -rd), 0.0), 32.0);
        
        // Fresnel effect for edge glow
        float fresnel = pow(1.0 - max(dot(n, -rd), 0.0), 2.0);
        
        // Distance-based fog
        float fog = exp(-d * 0.15);
        
        // Dynamic color based on position and time
        float hue = time * 0.1 + d * 0.05 + rz * 0.1;
        hue = mod(hue, 1.0);
        
        // Vary saturation and brightness based on depth and lighting
        float saturation = 0.7 + 0.2 * diff;
        float value = 0.3 + 0.4 * diff + 0.2 * fresnel + 0.1 * spec;
        
        // Convert to RGB
        vec3 baseColor = hsv2rgb(vec3(hue, saturation, value));
        
        // Apply lighting
        vec3 lightColor = vec3(1.0, 0.9, 0.8) * diff + vec3(0.5, 0.6, 0.9) * spec * 0.5;
        vec3 surfaceColor = baseColor * lightColor;
        
        // Add fresnel glow
        surfaceColor += vec3(0.3, 0.4, 0.6) * fresnel * 0.3;
        
        // Surface detection with smooth falloff
        float surface = smoothstep(2.5, 0.0, rz);
        float edge = clamp((rz - map(p + n * 0.1)) * 0.5, -0.1, 1.0);
        
        // Accumulate color with fog
        cl = mix(cl, surfaceColor, surface * fog * (0.6 + edge * 0.4));
        
        // Step forward
        d += min(rz, 1.0) * 0.8;
        
        // Early exit if we've gone too far
        if (d > 20.0) break;
    }
    
    // Add atmospheric perspective
    float dist = length(a);
    float atmosphere = 1.0 - smoothstep(0.5, 1.5, dist);
    
    // Background gradient
    float bgHue = time * 0.08;
    vec3 bgColor = hsv2rgb(vec3(mod(bgHue, 1.0), 0.4, 0.15));
    cl = mix(bgColor, cl, atmosphere);
    
    // Add subtle vignette
    float vignette = 1.0 - smoothstep(0.6, 1.2, dist);
    cl *= 0.8 + 0.2 * vignette;
    
    // Add subtle color grading
    cl = pow(cl, vec3(0.95)); // Slight gamma adjustment
    
    // Ensure colors are in valid range
    cl = clamp(cl, 0.0, 1.0);
    
    gl_FragColor = vec4(cl, 1.0);
}

