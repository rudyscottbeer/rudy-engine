package com.rudybeer.topdown.lights;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

/***
 * A light structure to be used in conjuction with Lighting Renderer
 */
public class StaticLight extends Light{

    public StaticLight(Vector3 pos, Vector3 attenuation, Color color){
        super(pos, attenuation, color);
    }

    public StaticLight(float x, float y, float z, float constantAtt, float linearAtt, float quadAtt, Color color){
        super(new Vector3(x , y, z), new Vector3(constantAtt, linearAtt, quadAtt), color);
    }

    public StaticLight(float x, float y, float z){
        super(new Vector3(x , y, z), new Vector3(0, 0f, 1f), Color.WHITE);
    }

    public void update(){
        return;
    }


}
