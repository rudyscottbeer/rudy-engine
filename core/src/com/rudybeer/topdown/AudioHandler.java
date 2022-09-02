package com.rudybeer.topdown;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

import java.util.HashMap;
import java.util.Map;

/***
 * The audio handler stores all sounds in a hashmap which can be accessed by their file name.
 */
public class AudioHandler {
    private Map<String, Sound> sounds;


    public AudioHandler(){
        sounds = new HashMap<String, Sound>();
               sounds.put("revolver_shot", Gdx.audio.newSound(Gdx.files.internal("audio/revolver_shot.ogg")));

    }

    /***
     * Plays sound.
     * @param id the filename string of sound to play.
     */
    public void play(String id){
        Sound s = sounds.get(id);
        if(s != null){
            s.play();
        }
    }
}
