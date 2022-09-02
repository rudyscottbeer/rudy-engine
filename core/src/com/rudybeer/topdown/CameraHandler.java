package com.rudybeer.topdown;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.rudybeer.topdown.entities.Entity;
import com.rudybeer.topdown.states.State;

public class CameraHandler {
    private OrthographicCamera camera;

    private Vector3 mouse_position = new Vector3(0,0,0);
    private Vector3 targetPosition = new Vector3(0,0,0);

    private static final float VIEWPORT_WIDTH = 400;
    private static final float VIEWPORT_HEIGHT = 240;
    private static final int POSITION_ROUND = 10000;

    //Bound variables
    private float boundHeight = -1;
    private float boundWidth = -1;

    //Following Variables
    private static final float FOLLOW_SPEED = 0.2f;
    private Entity target = null;
    private State state;


    public CameraHandler(State state){
        this.state = state;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, VIEWPORT_WIDTH, VIEWPORT_HEIGHT);

    }

    public void update(){

        //If there is a following target, smoothly follow that target based on FOLLOW_SPEED
        if(target != null){
            camera.position.interpolate(targetPosition.set(target.getPosition().x, target.getPosition().y, 0), FOLLOW_SPEED, Interpolation.smooth);
            //camera.position.set(targetPosition);
        }

        //If bound is not -1, then clamp the position.
        if(boundWidth != -1) {
            camera.position.x = MathUtils.clamp(camera.position.x, VIEWPORT_WIDTH / 2, boundWidth - VIEWPORT_WIDTH / 2);
        }
        if(boundHeight != -1) {
            camera.position.y = MathUtils.clamp(camera.position.y, VIEWPORT_HEIGHT / 2, boundHeight - VIEWPORT_HEIGHT / 2);
        }

        //Rounds camera position so there is not flickering on sides.
        camera.position.x = Math.round(camera.position.x * POSITION_ROUND) / POSITION_ROUND;
        camera.position.y = Math.round(camera.position.y * POSITION_ROUND) / POSITION_ROUND;

        camera.update();

        //Set and unproject mouse coordinates from screen
        mouse_position.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mouse_position);
        state.setMousePos(mouse_position);

    }

    /***
     * Camera following
     * @param target the entity to follow
     */
    public void setFollow(Entity target){
        this.target = target;
    }

    /***
     * Bound camera
     * @param boundWidth The width bound, -1 if not to be bound
     * @param boundHeight The height bound, -1 if not to be bound
     */
    public void setBound(float boundWidth, float boundHeight){
        this.boundWidth = boundWidth;
        this.boundHeight = boundHeight;
    }

    public float getViewportWidth(){
        return VIEWPORT_WIDTH;

    }

    public float getViewportHeight(){
        return VIEWPORT_HEIGHT;
    }

    public OrthographicCamera getCamera(){
        return camera;
    }

}
