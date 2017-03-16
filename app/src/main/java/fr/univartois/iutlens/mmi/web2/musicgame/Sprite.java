package fr.univartois.iutlens.mmi.web2.musicgame;

import android.graphics.Canvas;

/**
 * Interface pour objets dessinés dans GameView
 *
 *
 * Created by vincent.dubois on 24/01/17.
 */
public interface Sprite {

    /**
     * Action à réaliser à chaque tour
     */
    boolean act();

    /**
     * Dessiner le sprite
     *
     * @param canvas
     */
    void draw(Canvas canvas);

    /**
     * Tester si un point est dans le sprite, avec une tolérance donnée
     * @param x
     * @param y
     * @param size taille du point à tester
     * @return vrai si le point touche le sprite
     */
    boolean contains(float x, float y, float size);

}
