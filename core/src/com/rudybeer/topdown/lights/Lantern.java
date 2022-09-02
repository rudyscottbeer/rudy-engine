package com.rudybeer.topdown.lights;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

public class Lantern extends StaticLight {
    public Lantern(Vector3 pos, Vector3 attenuation, Color color) {
        super(pos, attenuation, color);
    }

    public Lantern(float x, float y, float z, float constantAtt, float linearAtt, float quadAtt, Color color) {
        super(x, y, z, constantAtt, linearAtt, quadAtt, color);
    }

    public Lantern(float x, float y, float z) {
        super(x, y, z);
    }

    public void update(){
        return;
    }
}
