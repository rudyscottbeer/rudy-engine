package com.rudybeer.topdown;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.rudybeer.topdown.entities.Player;
import com.rudybeer.topdown.states.World;


/***
 * A temporary input handler to be reworked later,
 * as such it is commented lowly and is fairly self-explanatory
 */
public class InputHandler implements InputProcessor {
    private World world;
    private Player player;

    public InputHandler(World world){
        this.world = world;
        this.player = world.getPlayer();
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode){
            case Input.Keys.W:
            player.setMoveUp(true);
                break;
            case Input.Keys.S:
                player.setMoveDown(true);
                break;
            case Input.Keys.A:
                player.setMoveLeft(true);
                break;
            case Input.Keys.D:
                player.setMoveRight(true);
                break;
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode){
            case Input.Keys.W:
                player.setMoveUp(false);
                break;
            case Input.Keys.S:
                player.setMoveDown(false);
                break;
            case Input.Keys.A:
                player.setMoveLeft(false);
                break;
            case Input.Keys.D:
                player.setMoveRight(false);
                break;
        }
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }


}
