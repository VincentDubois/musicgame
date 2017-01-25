package fr.univartois.iutlens.mmi.web2.musicgame;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by vincent.dubois on 24/01/17.
 */
public class AbstractSprite implements Sprite {

    public static final int SIZE = 30;
    float x;
    float y;View view;
    private final Paint paint;

    public AbstractSprite(View view, Paint paint) {
        this.x = (float) (Math.random()*view.getWidth());
        this.y =  0;
        this.view = view;
        this.paint= paint;
    }

    @Override
    public void act() {
        y += GameView.PIXEL_SIZE*GameView.SPEED;
        if (y> view.getHeight()) {
            y = 0;
            this.x = (float) (Math.random()*view.getWidth());
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
