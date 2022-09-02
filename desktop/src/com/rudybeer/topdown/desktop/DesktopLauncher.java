package com.rudybeer.topdown.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.rudybeer.topdown.TopDown;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Topdown";
		config.width = 400*3;
		config.height = 240*3;
		config.resizable = false;
		new LwjglApplication(new TopDown(), config);
	}
}
