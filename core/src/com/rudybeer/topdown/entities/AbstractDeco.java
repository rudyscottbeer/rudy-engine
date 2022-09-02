package com.rudybeer.topdown.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

public abstract class AbstractDeco implements Entity{
    Texture tex = new Texture(Gdx.files.internal("tree.png"));
    Texture tex_n = new Texture(Gdx.files.internal("tree_n.png"));
    SpriteBatch batch;

    private float x = 0;
    private float y = 0;

    public Vector3 bottom;


    public AbstractDeco(SpriteBatch batch){
        this.batch = batch;
    }

    @Override
    public void render() {
        batch.begin();
        tex_n.bind(1);
        tex.bind(0);
        batch.draw(tex, x, y);
        batch.end();
    }

    @Override
    public void dispose() {
        tex.dispose();
        tex_n.dispose();
    }


    public Vector3 getBottom(){
        return bottom.set(this.x + tex.getWidth()/2, this.y + tex.getHeight()/4, 0f);
    }


}
