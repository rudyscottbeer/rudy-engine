package com.rudybeer.topdown.entities;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public interface Entity {

    //Used to display an entity
    void render();
    // Disposes of the resources used in an entity
    void dispose();

    //Returns a Vector3 of the entity in world coordinates
    Vector3 getPosition();

    //Gets the bottom of the entity for the sake of lighting.
    Vector3 getBottom();

    //Returns the occlusion texture for the sake of shadow casting
    Texture getOcclusionTexture();




}
