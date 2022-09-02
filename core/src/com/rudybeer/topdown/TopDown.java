package com.rudybeer.topdown;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.rudybeer.topdown.entities.Player;
import com.rudybeer.topdown.states.World;

public class TopDown extends ApplicationAdapter {
	World world;

	
	@Override
	public void create () {
		world = new World();
		InputHandler inputHandler = new InputHandler(world);
		Gdx.input.setInputProcessor(inputHandler);
	}

	@Override
	public void render () {
		world.render();
	}



	
	@Override
	public void dispose () {
		world.dispose();
	}
}
