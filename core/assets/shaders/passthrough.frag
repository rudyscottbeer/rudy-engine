#ifdef GL_ES
precision mediump float;
#endif 
 
// varying input variables from our vertex shader
varying vec4 v_color;
varying vec2 v_texCoords;

uniform vec3 light;
uniform vec3 attenuation;


const vec2 resolution = vec2(400.0, 240.0);
 
// a special uniform for textures 
uniform sampler2D u_texture;
uniform sampler2D u_normal;
 
void main()
{
  vec2 u_center = light.xy;
  
  vec2 flipY = vec2(u_center.x, resolution.y*3.0 - u_center.y);
  vec2 light_pos = flipY / resolution.xy;
  

  vec2 pixel_pos = gl_FragCoord.xy / resolution.xy;


  float distance = distance(light_pos, pixel_pos);
  vec4 shade_color = v_color;
  shade_color.xyz = shade_color.xyz * (1.0 / (attenuation.x + (attenuation.z * distance * distance)));
  // set the colour for this fragment|pixel
  gl_FragColor = shade_color * texture2D(u_texture, v_texCoords);
}