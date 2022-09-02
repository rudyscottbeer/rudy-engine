package com.rudybeer.topdown.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.rudybeer.topdown.entities.Entity;

public class Tree implements Entity {
    Texture tree = new Texture(Gdx.files.internal("tree.png"));
    Texture tree_n = new Texture(Gdx.files.internal("tree_n.png"));
    Texture tree_o = new Texture(Gdx.files.internal("tree_o.png"));
    SpriteBatch batch;

    public float x = 0;
    public float y = 100;
    private Vector3 position = new Vector3();
    private Vector3 bottom = new Vector3();


    public Tree(SpriteBatch batch){
    this.batch = batch;
    }

    @Override
    public void render() {
        bottom.set(this.x + tree.getWidth()/2, this.y + tree.getHeight()/4, 0f);
        //batch.begin();

        tree_n.bind(1);
        tree.bind(0);
        batch.draw(tree, x, y);
        //batch.end();
    }

    @Override
    public void dispose() {
        tree.dispose();
        tree_n.dispose();
        tree_o.dispose();
    }

    public Vector3 getPosition(){
        return position.set(this.x, this.y, 0);
    }


    public Vector3 getBottom(){
        return bottom;
    }

    public Texture getOcclusionTexture(){
        return tree_o;
    }
}
