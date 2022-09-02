package com.rudybeer.topdown.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.rudybeer.topdown.*;
import com.rudybeer.topdown.entities.Player;
import com.rudybeer.topdown.entities.Tree;
import com.rudybeer.topdown.lights.StaticLight;

import java.util.LinkedList;

public class World extends State{

    public static float SPEED = 1f;

    //Rendering
    private Player player;
    private SpriteBatch batch;
    private TiledMap map;
    private ExtendedOrthogonalMapRenderer mapRenderer;
    private EntityRenderer entityRenderer;

    private state stateID = state.WORLD;

    //Handlers
    private CameraHandler cameraHandler;
    private AudioHandler audioHandler;
    private LightingHandler lightingHandler;


    private Vector3 mousePosition = new Vector3(0,0,0);
    private Vector3 mousePositionUnproj = new Vector3(0,0, 0);
    private StaticLight mouseLight = new StaticLight(mousePositionUnproj, new Vector3(0f, 0f, 0.5f), Color.OLIVE);



    //Shaders
    ShaderProgram shader;

    //Map Properties
    private int[] foregroundLayers;
    private int[] backgroundLayers;

    private float lightIntensity = 0.7f;
    private float lightDropoff = 1f;

    public Texture currentTexture;

    public float delta;



    public World(){

        //Make pedantic false so unused variables aren't removed
        ShaderProgram.pedantic = false;

        //Initializing default shader shader
        shader = createShader(
                Gdx.files.internal("shaders/passthrough.vert").readString(),
                Gdx.files.internal("shaders/passthrough_n.frag").readString());


        //Initializing Spritebatch
        this.batch = new SpriteBatch();
        batch.setShader(shader);

        //Init Audio System
        audioHandler = new AudioHandler();

        //Init entity rendering system
        entityRenderer = new EntityRenderer();

        //Create player and add to entity system
        this.player = new Player(audioHandler, batch);
        entityRenderer.addEntity(player);

        //Create tree and add to entity system
        entityRenderer.addEntity(new Tree(batch));

        //Init camera system and set to follow player with the map bounds.
        cameraHandler = new CameraHandler(this);
        cameraHandler.setFollow(player);
        cameraHandler.setBound(32*15, 32*15);

        //Init lighting system and add light at mouse
        lightingHandler = new LightingHandler(entityRenderer.getEntities());
        lightingHandler.addLight(mouseLight);

        //Init tilemap
        map = new TmxMapLoader().load("maps/swamp_map.tmx");
        foregroundLayers = findForegroundLayers(map);
        backgroundLayers = findBackgroundLayers(map);
        mapRenderer = new ExtendedOrthogonalMapRenderer(map, batch);
    }

    /***
     * Helper finds layers marked as foreground in Tiled
     * @param map Tile map to be searched
     * @return An int array representing foreground layers
     */
    public static int[] findForegroundLayers(TiledMap map){
        LinkedList<Integer> foreground = new LinkedList<Integer>();
        int layers = map.getLayers().getCount();
        for (int i = 0; i < layers; i++) {
            if(map.getLayers().get(i).getProperties().get("Foreground", Boolean.class)){
                foreground.add(i);
            }
        }
    return integerToInt(foreground);

    }


    /***
     * Helper finds layers marked as background in Tiled
     * @param map Tile map to be searched
     * @return An int array representing background layers
     */
    public static int[] findBackgroundLayers(TiledMap map){
        LinkedList<Integer> background = new LinkedList<Integer>();
        int layers = map.getLayers().getCount();
        for (int i = 0; i < layers; i++) {
            if(!map.getLayers().get(i).getProperties().get("Foreground", Boolean.class)){
                background.add(i);
            }
        }
        return integerToInt(background);
    }


    /**
     * @param ls Integer linked list
     * @return a primative int array
     */
    private static int[] integerToInt(LinkedList<Integer> ls){
        Integer[] l = new Integer[ls.size()];
        l = ls.toArray(l);
        int[] ints = new int[l.length];
        for (int i = 0; i < l.length; i++) {
            ints[i] = l[i].intValue();
        }
        return ints;
    }


    public void render(){

        //Adds a light at mouse location on click
        if(Gdx.input.justTouched())
            lightingHandler.addLight(new StaticLight(mousePosition.x, mousePosition.y, 0));

        //Update light-at-mouse location
        mouseLight.setPosition(mousePosition);

        /*
        Set the texture int of normal textures to 1.
        OpenGL uses int 'channels' to pass textures into a shader.
        [See lighting readme for more information]
         */
        shader.setUniformi("u_normal", 1);


        //Clear buffer to .
        ScreenUtils.clear(0, 0, 0, 1);


        //Set the spritebatch to Camera Handler camera.
        batch.setProjectionMatrix(cameraHandler.getCamera().combined);


        //Set the view of the map render to Camera Handler camera.
        mapRenderer.setView(cameraHandler.getCamera());


        //Render background layers first so they are below everything else
        mapRenderer.render(backgroundLayers);


        //Render lights and shadow above background but below entities
        lightingHandler.renderLights(batch, cameraHandler);


        //Render all entities
        entityRenderer.render(batch, cameraHandler);


        //Render foreground layers above all else.
        mapRenderer.render(foregroundLayers);


        //Update the camera
        cameraHandler.update();




    }

    public void dispose(){
        batch.dispose();
        entityRenderer.dispose();
        shader.dispose();
    }

    public Player getPlayer(){
        return player;
    }

    public void setMousePos(Vector3 mousePos){
        mousePosition = mousePos;
        mousePositionUnproj.set(Gdx.input.getX(), Gdx.input.getY(), 1);
        player.setMousePos(mousePos);
    }
}
