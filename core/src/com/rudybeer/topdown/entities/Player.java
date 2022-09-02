package com.rudybeer.topdown.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.g3d.utils.TextureBinder;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.rudybeer.topdown.AudioHandler;
import org.w3c.dom.Text;

public class Player implements Entity{
    private Vector3 mousePosition = new Vector3(0,0,0);


    private static final float REVOLVER_SPIN_SPEED = 1500;
    private static final float REVOLVER_KICKBACK_ANGLE = 45;
    private static final float REVOLVER_KICKBACK_SPEED = 1500;
    float stateTime;


    private Sprite body;
    private Animation<TextureRegion> idleBody;
    private Sprite arm;
    private Sprite revolver;

    private Sprite body_n;
    private Animation<TextureRegion> idleBody_n;
    private Sprite arm_n;
    private Sprite revolver_n;

    public float x;
    public float y;
    private Vector3 position = new Vector3();
    private Vector3 bottom = new Vector3();


    private float armRotate;


    /*
    The Section for movement variables
     */
    private boolean up;
    private boolean down;
    private boolean left;
    private boolean right;

    //Revolver rotation
    boolean isRevolverSpin = false;
    enum Kick {
            KICK_UP,
            KICK_DOWN,
            NO_KICK
    };
    Kick revolverKick = Kick.NO_KICK;
    private float revolverRotate;


    //Passed Handlers and Other
    AudioHandler audioHandler;
    SpriteBatch batch;
    OrthographicCamera cam;

    RenderContext context;

    private TextureAtlas textureAtlas;
    private TextureAtlas normalAtlas;

    private Texture nBlank = new Texture(Gdx.files.internal("tilesets/textures/swamp_water_n_p.png"));
    private Texture player_o = new Texture(Gdx.files.internal("gunslinger_o.png"));

    public static int PLAYER_SPEED = 100;

    public Player(AudioHandler audioHandler, SpriteBatch batch){
        //This section is a great reference for using texture atlas

        this.audioHandler = audioHandler;
        this.batch = batch;


        textureAtlas = new TextureAtlas(Gdx.files.internal("atlas/gunslinger_pack.atlas"));
        normalAtlas = new TextureAtlas(Gdx.files.internal("atlas/gunslinger_pack_n.atlas"));

        //Body Idle Animation
        idleBody = new Animation<TextureRegion>(0.1f, textureAtlas.findRegions("gunslinger"), Animation.PlayMode.LOOP);
        idleBody_n = new Animation<TextureRegion>(0.1f, normalAtlas.findRegions("gunslinger"), Animation.PlayMode.LOOP);


        arm = textureAtlas.createSprite("gunslingerarm");
        revolver = textureAtlas.createSprite("revolver");

        arm_n = normalAtlas.createSprite("gunslingerarm");
        revolver_n = normalAtlas.createSprite("revolver");

        arm.setOrigin(8, 17);
        //hand at 4,8
        revolver.setOrigin(4, 8);

        //Init variables
        stateTime = 0f;



    }

    public void render(){

       // batch.begin();


        stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion bodyFrame = idleBody.getKeyFrame(stateTime, true);
        TextureRegion bodyFrame_n = idleBody_n.getKeyFrame(stateTime, true);
        bottom.set(this.x + bodyFrame.getRegionWidth()/2, this.y, 0);

        //Bind the normal map and diffuse textures
        // You only need to do this once thanks to the texture atlas!!!!

        bodyFrame_n.getTexture().bind(1);
        bodyFrame.getTexture().bind(0);


        //Set player to stored coordinates
        batch.draw(bodyFrame, x, y);
        arm.setPosition(x + 15, y + 30);
        arm.setRotation(armRotate);
        revolver.setPosition(arm.getX() + MathUtils.cosDeg(armRotate)*20 + 4, arm.getY() + MathUtils.sinDeg(armRotate)*20 + 8);


        //Animate revolver code
        if(Gdx.input.isKeyJustPressed(Input.Keys.R)) isRevolverSpin = true;

        //For stylish revolver spin
        if(isRevolverSpin){
            revolverRotate-= REVOLVER_SPIN_SPEED*Gdx.graphics.getDeltaTime();
            if(revolverRotate >= 360 || revolverRotate <= -360) {
                isRevolverSpin=false;
                revolverRotate=0;
            }
        }

        //For shot kickback
        if(Gdx.input.justTouched()) {
            audioHandler.play("revolver_shot");
            revolverKick = Kick.KICK_UP;
        }
        switch (revolverKick){
            case NO_KICK:
                break;

            case KICK_UP:
                revolverRotate += REVOLVER_KICKBACK_SPEED*Gdx.graphics.getDeltaTime();
                if(revolverRotate >= REVOLVER_KICKBACK_ANGLE) {
                    revolverKick = Kick.KICK_DOWN;
                }
                break;
            case KICK_DOWN:

                revolverRotate -= REVOLVER_KICKBACK_SPEED*Gdx.graphics.getDeltaTime();
                if(revolverRotate <= 0) {
                    revolverKick = Kick.NO_KICK;
                    revolverRotate = 0;
                }
                break;
        }

        revolver.setRotation(armRotate + revolverRotate);



        arm.draw(batch);

        revolver.draw(batch);


        //Handle user input
        if(up) y+=PLAYER_SPEED*Gdx.graphics.getDeltaTime();
        if(down) y-=PLAYER_SPEED*Gdx.graphics.getDeltaTime();
        if(right) x+=PLAYER_SPEED*Gdx.graphics.getDeltaTime();
        if(left) x-=PLAYER_SPEED*Gdx.graphics.getDeltaTime();

        //Point to mouse math
        armRotate = MathUtils.atan2(mousePosition.y - (arm.getY() + arm.getOriginY()), mousePosition.x - (arm.getX() + arm.getOriginX())) * (180/MathUtils.PI);
        if(armRotate < 0)
        {
            armRotate = 360 - (-armRotate);
        }

        //batch.end();
    }

    public void setMoveUp(boolean mov){
    up = mov;
    }
    public void setMoveDown(boolean mov){
        down = mov;
    }
    public void setMoveLeft(boolean mov){
        left = mov;
    }
    public void setMoveRight(boolean mov){
        right = mov;
    }


    public void dispose(){
        textureAtlas.dispose();
    }

    public void setMousePos(Vector3 mousePos){
        mousePosition = mousePos;
    }

    public Vector3 getPosition(){
        return position.set(this.x, this.y, 0);
    }

    public Vector3 getBottom(){
        return bottom;
    }

    public Texture getOcclusionTexture(){
        return player_o;
    }





}
