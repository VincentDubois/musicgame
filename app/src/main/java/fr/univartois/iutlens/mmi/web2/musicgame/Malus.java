package fr.univartois.iutlens.mmi.web2.musicgame;

import android.graphics.Paint;

/**
 * Created by vincent.dubois on 24/01/17.
 */
public class Malus extends AbstractSprite {

    final private static Paint paint = new Paint();
    {
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setColor(0xffff3322);

    }

    public Malus(){
        super(paint);

    }



}
