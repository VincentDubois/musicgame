package fr.univartois.iutlens.mmi.web2.musicgame;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by vincent.dubois on 24/01/17.
 *
 * Classe abstraite gérant les fonctions communes à tous les sprites.
 */
public class AbstractSprite implements Sprite {

    // taille en pixel du sprite
    public static final int SIZE = 30;
    // Coordonnée
    float x;
    float y;
    // vue dans laquelle on s'affiche

    private final Paint paint;

    public AbstractSprite(Paint paint) {
        this.x = (float) (Math.random()*GameView.WIDTH);
        this.y =  0;
        this.paint= paint;
    }

    @Override
    public void act() {
        y += GameView.PIXEL_SIZE*GameView.SPEED;
        if (y> GameView.HEIGHT) {
            y = 0;
            this.x = (float) (Math.random()*GameView.WIDTH);
        };

    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawCircle(x,y,SIZE,paint);
    }

    @Override
    public boolean contains(float x, float y, float size) {

        float move = GameView.PIXEL_SIZE*GameView.SPEED;

        float dx = x-this.x;
        float dy = y-this.y;
//        if (dy>=move) dy -= move;
//        else if (dy >= 0) dy = 0;
        return dx*dx+dy*dy < (size+SIZE) *(size+SIZE);

    }
}
