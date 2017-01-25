package fr.univartois.iutlens.mmi.web2.musicgame;

import android.graphics.Canvas;

/**
 * Created by vincent.dubois on 24/01/17.
 */
public interface Sprite {

    void act();

    void draw(Canvas canvas);

    boolean contains(float x, float y, float size);
}
