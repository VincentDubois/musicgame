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
    private final SpriteSheet spriteSheet;
    private final int n;
    // Coordonnée
    float x;
    float y;
    // vue dans laquelle on s'affiche


    public AbstractSprite( int id,int n, float min, float max) {
        //entre 0 et 3/4
        // margin qui est identique au margin du mur

        this.x = (float) (Math.random()*(max-min))+min;
        //
        this.y =  0;
        this.spriteSheet = SpriteSheet.get(id);
        this.n = n;
    }

    @Override
    public boolean act() {
        y += GameView.PIXEL_SIZE*GameView.SPEED;
        if (y> GameView.HEIGHT){ //sort
            y = 0;
            return true;
        }
        return false;

    }

    @Override
    public void draw(Canvas canvas) {
      spriteSheet.paint(canvas,n,x-spriteSheet.w/2,y-spriteSheet.h/2);
    }

    @Override
    public boolean contains(float x, float y, float size) {

        float move = GameView.PIXEL_SIZE*GameView.SPEED;

        float dx = x-this.x;
        float dy = y-this.y;
//        if (dy>=move) dy -= move;
//        else if (dy >= 0) dy = 0;
//        return dx*dx+dy*dy < (size+spriteSheet.w/2) *(size+spriteSheet.h/2);
        float rayonSprite = spriteSheet.w/2;
        float rayonPlayer = size;

        return dx*dx+dy*dy < ( rayonPlayer + rayonSprite) * ( rayonPlayer + rayonSprite);
    }


}
