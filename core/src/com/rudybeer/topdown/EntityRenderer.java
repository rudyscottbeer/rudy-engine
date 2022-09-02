package com.rudybeer.topdown;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.rudybeer.topdown.entities.Entity;

import java.util.ArrayList;
import java.util.Comparator;

/***
 * Entities are stored in an arraylist for quicker access and sorting,
 * There is no id system at the moment, so entity retrieval must be handled outside of renderer.
 */
public class EntityRenderer {

    private Texture test = new Texture(Gdx.files.internal("player.png"));



    private ArrayList<Entity> entities;

    private Sortbyy sort = new Sortbyy();

    //Temporary vector to be assigned at runtime
    private Vector3 currBottom = new Vector3();
    private Vector3 camLocale = new Vector3();

    public EntityRenderer(){
        entities = new ArrayList<>();
    }

    public void addEntity(Entity entity){
        entities.add(entity);
    }

    public void render(SpriteBatch batch, CameraHandler cam){
        ShaderProgram shader = batch.getShader();
        //Sort entities by bottom y to z-render properly (whether entity is in front or behind)
        entities.sort(sort);

        //Center camera position
        Vector3 cVec = cam.getCamera().position;

        //Offsets are essentially the bottom left corner of the camera
        float xOffset = (cVec.x - cam.getViewportWidth()/2);
        float yOffset = (cVec.y - cam.getViewportHeight()/2);



        //For each entity, use it's render function and set it's bottom in shader.
        for (int i = 0; i < entities.size(); i++) {
            batch.begin();
            Entity currEntity = entities.get(i);
            //TODO: *3 location
            shader.setUniformf("bottom",
                    currBottom.set((currEntity.getBottom().x - xOffset)*3,
                            (currEntity.getBottom().y - yOffset)*3,
                            currEntity.getBottom().z));
            entities.get(i).render();
            batch.end();
        }
    }

    public void dispose(){
        for (int i = 0; i < entities.size(); i++) {
            entities.get(i).dispose();
        }
    }

    public ArrayList<Entity> getEntities(){
        return entities;
    }

}

class Sortbyy implements Comparator<Entity> {

    // Sorting in ascending order of bottom y.
    public int compare(Entity a, Entity b)
    {
        return (int)(b.getBottom().y - a.getBottom().y);
    }
}
