#ifdef GL_ES
precision mediump float;
#endif

#define PI 3.14

// varying input variables from our vertex shader
varying vec4 v_color;
varying vec2 v_texCoords;

uniform vec3 light;
uniform vec3 lightColor;
uniform vec3 attenuation;

struct lightData{
vec3 lightPosition;
vec3 lightColor;
vec3 attenuation;
};
const int maxLights = 20;

uniform int numLights;
uniform vec3 bottom;
uniform lightData lights[maxLights];


const vec2 resolution = vec2(400.0, 240.0);
 
// a special uniform for textures 
uniform sampler2D u_texture;
uniform sampler2D u_normal;
uniform sampler2D u_shadow;

float approx(float f, float amount){
  if(amount != -1.0){
    return floor(f * amount)/amount;
  }else{
    return f;
  }
}

vec3 approxVec(vec3 v, float a){
  if(a == -1.0){
    return v;
  }else {
    return
    vec3(floor(v.x * a)/a,
    floor(v.y * a)/a,
    floor(v.z * a)/a);
  }
}

//sample from the distance map
float samp(vec2 coord, float r) {
  return step(r, texture2D(u_shadow, coord).r);
}
 
void main()
{


  vec4 color = texture2D(u_texture, v_texCoords.st);
  vec3 nColor = texture2D(u_normal, v_texCoords.st).rgb;
  
  vec3 normal = normalize(nColor * 2.0 - 1.0);
  vec3 result = vec3(0.0, 0.0, 0.0);

  float d = 100.0;
  vec3 deltaBottom = vec3(0.0, 0.0, 0.0);

  for(int i = 0; i < numLights; i++){
    lightData currentLight = lights[i];

    vec3 flipLight =
    vec3(currentLight.lightPosition.x,
    currentLight.lightPosition.y,
    currentLight.lightPosition.z);



    vec3 deltaPos = vec3((flipLight.xy - gl_FragCoord.xy)/resolution.xy, flipLight.z);



      vec3 lightDir = normalize(deltaPos);

      float lambert = clamp(dot(normal, lightDir), 0.0, 1.0);


      d = sqrt(dot(deltaPos, deltaPos));



      float att =
      1.0 / (currentLight.attenuation.x + (currentLight.attenuation.y * d) + (currentLight.attenuation.z*d*d));
    if( bottom.z == -1.0){
      result += (currentLight.lightColor.rgb * approx(lambert, -1.0)) * approx(att, -1.0);
    }else{
      result += (currentLight.lightColor.rgb * approx(lambert, -1.0)) * approx(att, -1.0)
      *  (1.0 / (1.0 + exp(-1.0 * (bottom.y - flipLight.y)/25.0)));
    }

  }


  result *= color.rgb;
  gl_FragColor = v_color * vec4(approxVec(result, -1.0), color.a);



  //Debug code
  // Show light center
  //if(d <= 1.001) gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);
  //if(center <= 0.15) gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);
  //Show normal maps
  //gl_FragColor = vec4(nColor, color.a);
  //if(Coord.x == gl_FragCoord.x) gl_FragColor = vec4(1.0, 0.0, 1.0, 1.0);
  //if(gl_FragCoord.y == bottom.y ) gl_FragColor = vec4(1.0, 0.0, 1.0, 1.0);

}