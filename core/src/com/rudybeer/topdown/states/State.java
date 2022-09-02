package com.rudybeer.topdown.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.rudybeer.topdown.AudioHandler;
import com.rudybeer.topdown.CameraHandler;
import com.rudybeer.topdown.entities.Player;

public abstract class State {
    private AudioHandler audioHandler;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private CameraHandler cameraHandler;
    public enum state {
        WORLD
    }

    public state stateID;

    public State(){
        this.batch = new SpriteBatch();
        this.cameraHandler = new CameraHandler(this);
        this.audioHandler = new AudioHandler();
    }

    public abstract void setMousePos(Vector3 mousePos);

    /***
     * A helper for shader creation.
     * @param vert A string vertex shader (not the file locale)
     * @param frag A string fragment shader (not the file locale)
     * @return A shader program from the frag and vert strings.
     */
    public static ShaderProgram createShader(String vert, String frag) {
        ShaderProgram prog = new ShaderProgram(vert, frag);
        if (!prog.isCompiled())
            throw new GdxRuntimeException("could not compile "+ vert + " or " + frag + ": " + prog.getLog());
        if (prog.getLog().length() != 0)
            Gdx.app.log("GpuShadows", prog.getLog());
        return prog;
    }
}
