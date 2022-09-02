package com.rudybeer.topdown;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.rudybeer.topdown.entities.Entity;
import com.rudybeer.topdown.lights.StaticLight;
import com.rudybeer.topdown.states.State;

import java.util.ArrayList;


/**
 * Shadow code inspired greatly by mattdesl at https://github.com/mattdesl/lwjgl-basics/wiki
 *
 * I have written a lighting readme to explain the lighting and shadows as well.
 */

public class LightingHandler {

    private static final int MAX_LIGHTS = 20;
    private static float UPSCALE = 1f;

    //Lights to be rendered
    private ArrayList<StaticLight> lights;

    //A temporary vector for runtime assignment
    private Vector3 screenPosition = new Vector3(0,0, 0);
    private Vector3 color = new Vector3(1, 0, 0);

    //A batch to be used for writting to other framebuffers.
    private SpriteBatch shadowBatch;

    /**
     * Shadow Variables
     */
    //Entity list of shadow casters.
    private ArrayList<Entity> shadowCasters;
    //The framebuffer that draws the entity occlusion textures relative to each light
    private FrameBuffer occludersFBO;
    //The framebuffer that takes the occlusion fb and calculates distances to a 1D line
    private FrameBuffer shadowMapFBO;
    //Camera used for shadow framebuffers
    private OrthographicCamera shadowCam;
    //Shader to make 1D shadow map
    private ShaderProgram shadowMapShader;
    //Shader to draw shadows
    private ShaderProgram shadowRenderShader;
    //Occlusion framebuffer texture
    private TextureRegion occluders;
    //1D shadow map texture region
    private TextureRegion shadowMap1d;
    //1D shadow map texture
    private Texture shadowMapTex;



    public LightingHandler(){
        //Init camera, batch, lights, and casters
        this.shadowBatch = new SpriteBatch();
        this.shadowCam = new OrthographicCamera();
        lights = new ArrayList<>();
        shadowCasters = new ArrayList<>();

        //Init framebuffers
        occludersFBO = new FrameBuffer(Pixmap.Format.RGBA8888, 400, 240, false);
        shadowMapFBO = new FrameBuffer(Pixmap.Format.RGBA8888, 400, 1, false);

        //Init shaders
        shadowMapShader = State.createShader(
                Gdx.files.internal("shaders/passthrough.vert").readString(),
                Gdx.files.internal("shaders/shadowMap_n.frag").readString());
        shadowRenderShader = State.createShader(
                Gdx.files.internal("shaders/passthrough.vert").readString(),
                Gdx.files.internal("shaders/shadowRender.frag").readString());

        //Set occluders textureregion to be framebuffer and flip (frames are always upside down)
        occluders = new TextureRegion(occludersFBO.getColorBufferTexture());
        occluders.flip(false, true);

        //Set shadow texture to Shadow map framebuffer
        shadowMapTex = shadowMapFBO.getColorBufferTexture();

        //Set the texture with linear and wrap sampling
        shadowMapTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        shadowMapTex.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        //Set textureregion and flip
        shadowMap1d = new TextureRegion(shadowMapTex);
        shadowMap1d.flip(false, true);


    }

    public LightingHandler( ArrayList<Entity> shadowCasters){
        this();
        this.shadowCasters = shadowCasters;
    }

    public void addLight(StaticLight light){
        lights.add(light);
    }

    public StaticLight getLight(int index){
        return lights.get(index);
    }


    public void renderLights(SpriteBatch batch, CameraHandler cameraHandler){
        ShaderProgram shader = batch.getShader();

        //Set the number of lights in shader
        shader.setUniformi("numLights", lights.size());
        //Render each light
        for (int i = 0; i < lights.size(); i++) {
            StaticLight l = lights.get(i);
            renderLight(l, i, batch, cameraHandler, shader);
        }




    }

    private void renderLight(StaticLight l, int id, SpriteBatch batch, CameraHandler cameraHandler, ShaderProgram shader){

        /**
         * An important note. The most complicated aspect of this system is mediating
         * between the various different coordinate systems. A more in-depth write up can be found,
         * in the lighting readme, but in brief:
         * there is the camera's coordinate system, which operates in a 400x240 viewport,
         * then a screen coordinate system, which is 1200x720 currently, but
         * should in the future support various screen sizes.
         * You will find various *3 todos in this project,
         * which signal a place to rework a translation between cam and screen coords.
         * To get the proper world position, you must get its initial coordinates, subtract the current
         * bottom left of the camera, and multiply by 3.
         */




        //Get relative light coords
        screenPosition.set(l.getPosition());

        //Center camera position
        Vector3 cVec = cameraHandler.getCamera().position;

        //Offsets are essentially the bottom left corner of the camera
        float xOffset = (cVec.x - cameraHandler.getViewportWidth()/2);
        float yOffset = (cVec.y - cameraHandler.getViewportHeight()/2);

        //The screen position of the light.
        Vector3 pVec =
                screenPosition.set(
                        //TODO: *3 Location
                        (screenPosition.x - xOffset)*3,
                        (screenPosition.y - yOffset)*3,
                        1);


        //Assign this light's properties
        batch.getShader().setUniformf("lights[" + id + "].lightPosition", pVec);
        batch.getShader().setUniformf("lights[" + id + "].lightColor",
                color.set(l.getColor().r, l.getColor().g, l.getColor().b));
        batch.getShader().setUniformf("lights[" + id + "].attenuation", l.getAttenuation());

        //Bind the occluder framebuffer for this light
        occludersFBO.begin();

        //clear the FBO
        Gdx.gl.glClearColor(0f,0f,0f,0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //set camera at light
        shadowCam.setToOrtho(false, cameraHandler.getViewportWidth(), cameraHandler.getViewportHeight());
        //TODO: *3 Location
        shadowCam.position.set(pVec.x/3, pVec.y/3, pVec.z);
        shadowCam.update();

        //set batch to draw occluders
        shadowBatch.setProjectionMatrix(shadowCam.combined);
        shadowBatch.setShader(null);
        shadowBatch.begin();

        //Get each shadow caster and draw it's occlusion texture
        for (int i = 0; i < shadowCasters.size(); i++) {
            Entity e = shadowCasters.get(i);
            //This does not need a *3, as the draw function handles translation for us
            shadowBatch.draw(e.getOcclusionTexture(),
                    (e.getPosition().x - xOffset),
                    (e.getPosition().y - yOffset)
            );
        }
        shadowBatch.end();
        occludersFBO.end();



        //Now build 1d shadow map from the previous occlusion framebuffer
        shadowMapFBO.begin();

        //Clear buffer
        Gdx.gl.glClearColor(0f,0f,0f,0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //Set the shadow map shader
        shadowBatch.setShader(shadowMapShader);
        shadowBatch.begin();
        shadowMapShader.setUniformf("resolution",
                cameraHandler.getViewportWidth(), cameraHandler.getViewportHeight());

        //reset projection matrix to framebuffer size
        shadowCam.setToOrtho(false, shadowMapFBO.getWidth(), shadowMapFBO.getHeight());
        shadowBatch.setProjectionMatrix(shadowCam.combined);

        //draw occluders texture to 1d shadow map framebuffer
        shadowBatch.draw(occluders, 0, 0, occluders.getRegionWidth(), shadowMapFBO.getHeight());

        shadowBatch.end();
        shadowMapFBO.end();

        /**
         * Render shadow layer
         */


        //reset projection matrix to screen
        shadowCam.setToOrtho(false);
        shadowBatch.setProjectionMatrix(shadowCam.combined);

        //set the shader which actually draws the light/shadow
        shadowBatch.setShader(shadowRenderShader);
        shadowBatch.begin();

        shadowRenderShader.setUniformf("resolution", 400, 240);
        shadowRenderShader.setUniformf("softShadows", 0f);



        //draw centered on light position
        //TODO: *3 Location
        shadowBatch.draw(shadowMap1d.getTexture(), pVec.x-1200/2f, pVec.y-720/2f, 1200, 720);

        //flush the batch before swapping shaders
        shadowBatch.end();
    }
}
