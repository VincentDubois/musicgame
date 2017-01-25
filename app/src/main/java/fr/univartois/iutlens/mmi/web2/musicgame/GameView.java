package fr.univartois.iutlens.mmi.web2.musicgame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.Vector;


/**
 * TODO: document your custom view class.
 */
public class GameView extends View implements View.OnTouchListener {

    public static final int SPEED = 1;
    public static final int PIXEL_SIZE = 20;
    Vector<Float> wall = null;

    Vector<Sprite> sprite;

    private Paint wallPaint;
    private Handler handler;
    private boolean running = false;
    private float lastX;
    private float lastY;


    public GameView(Context context) {
        super(context);
        init(null, 0);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public GameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }


    private void init(AttributeSet attrs, int defStyle) {
        wall =  new Vector<>();
        sprite = new Vector<>();

        wallPaint = new Paint();
        wallPaint.setAntiAlias(true);
        wallPaint.setColor(0xff224499 );
        wallPaint.setStyle(Paint.Style.STROKE);
        wallPaint.setStrokeWidth(PIXEL_SIZE+1);
        wallPaint.setStrokeCap(Paint.Cap.ROUND);

        handler = new Handler();

        setOnTouchListener(this);

    }


    private void startTimer(){
        if (running == true) return;
        running = true;
        reStartTimer();
    }

    private synchronized void reStartTimer(){
        handler.removeCallbacks(null);
        handler.postDelayed(new Runnable(){
            @Override
            public void run(){
                if (running) reStartTimer();
                update();
            }
        }, 25);
    }

    private  void stopTimer(){
        running = false;
        handler.removeCallbacks(null);
    }




    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w == 0 || h == 0) return;
        createGame();
    }


    private void createGame() {
        createWall();

    }

    private void createWall() {
        wall.add((float) Math.random());
        updateWall(this.getHeight()/ PIXEL_SIZE);
    }

    private void updateWall(int nb) {
        for(int i = 0; i< nb; ++i) {
            float last = wall.lastElement();
            last += (Math.random() - 0.5f) * 0.2f;
            if (last < 0) last = 0;
            if (last > 1) last = 1;
            wall.add(last);
            if (wall.size() > this.getHeight() / PIXEL_SIZE) wall.remove(0);
        }
    }

    private void update(){
        updateWall(SPEED);
        updateSprite();

        int i = 0;
        while (i< sprite.size()){
            Sprite s = sprite.elementAt(i);
            if (s.contains(lastX,lastY,20)){
                sprite.remove(i);
            } else ++i;
        }

        invalidate();
    }

    private void updateSprite() {
        if (sprite.size()< 10 && Math.random()< 0.05)
            sprite.add(Math.random() > 0.5f ? new Bonus(this) :  new Malus(this));


        for(Sprite s : sprite){
            s.act();
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(0xff99aaee);

        if (wall == null) return;

        for(int i = 0; i < wall.size(); ++i){
            canvas.drawLine(0,getHeight()-i* PIXEL_SIZE - PIXEL_SIZE,
                    0.3f*wall.get(i)*getWidth(),getHeight()-i* PIXEL_SIZE - PIXEL_SIZE,wallPaint);

            canvas.drawLine((0.7f+0.3f*wall.get(i))*getWidth(),getHeight()-i* PIXEL_SIZE - PIXEL_SIZE,
                    getWidth(),getHeight()-i* PIXEL_SIZE - PIXEL_SIZE,wallPaint);
        }

        for(Sprite s : sprite){
            s.draw(canvas);
        }
    }


    @Override
    public boolean onTouch(View v, MotionEvent motionEvent) {
        boolean used = true;

        int action = motionEvent.getActionMasked();
        int ndx = motionEvent.getActionIndex();
        if (action == MotionEvent.ACTION_DOWN){

            startTimer();

        } else if (action == MotionEvent.ACTION_UP){
            stopTimer();
        }
        lastX = motionEvent.getX(ndx);
        lastY = motionEvent.getY(ndx);

        return used;
    }
}
