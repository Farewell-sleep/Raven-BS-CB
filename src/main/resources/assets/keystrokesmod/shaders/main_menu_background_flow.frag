#ifdef GL_ES
precision mediump float;
#endif

#extension GL_OES_standard_derivatives : enable

uniform vec2 resolution;
uniform float time;

/*
* Animated gradient background with flowing waves
* Smooth color flow using HSV to RGB conversion
*/

// HSV to RGB conversion for smooth color transitions
vec3 hsv2rgb(vec3 c) {
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

void main() {
    vec2 uv = gl_FragCoord.xy / resolution.xy;
    
    // Normalize coordinates to center
    vec2 p = (gl_FragCoord.xy * 2.0 - resolution.xy) / resolution.y;
    
    // Create flowing wave patterns
    float wave1 = sin(p.x * 3.0 + time * 0.5) * 0.5 + 0.5;
    float wave2 = sin(p.y * 2.0 + time * 0.7) * 0.5 + 0.5;
    float wave3 = sin(length(p) * 2.0 - time * 0.6) * 0.5 + 0.5;
    
    // Combine waves for interesting patterns
    float pattern = (wave1 + wave2 + wave3) / 3.0;
    
    // Create radial gradient from center
    float dist = length(p);
    float radial = 1.0 - smoothstep(0.0, 1.5, dist);
    
    // Smooth color flow using HSV - hue cycles smoothly through the spectrum
    float baseHue = time * 0.15; // Slower, smoother hue rotation
    float hueOffset = pattern * 0.3; // Add variation based on pattern
    float hue = mod(baseHue + hueOffset, 1.0);
    
    // Vary saturation and value based on position and waves
    float saturation = 0.6 + 0.3 * wave1;
    float value = 0.4 + 0.4 * radial + 0.2 * wave2;
    
    // Convert HSV to RGB for smooth color flow
    vec3 hsvColor = vec3(hue, saturation, value);
    vec3 finalColor = hsv2rgb(hsvColor);
    
    // Add subtle brightness variation
    finalColor += vec3(0.05) * sin(time * 2.0 + p.x * 2.0 + p.y * 2.0);
    
    // Ensure colors are in valid range
    finalColor = clamp(finalColor, 0.0, 1.0);
    
    gl_FragColor = vec4(finalColor, 1.0);
}

