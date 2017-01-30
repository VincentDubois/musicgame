package fr.univartois.iutlens.mmi.web2.musicgame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.Vector;


/**
 * Classe affichant le jeu.
 */
public class GameView extends View implements View.OnTouchListener {


    public static final int WIDTH = 1080;
    public static final int HEIGHT = 1920;
    public static final RectF VIRTUAL_SCREEN = new RectF(0, 0, WIDTH, HEIGHT);

    // Nombre de lignes affichées à chaque tour
    public static final int SPEED = 1;
    // Taille en pixels de la taille de chaque ligne
    public static final int PIXEL_SIZE = 20;

    public static final int LINES = HEIGHT / PIXEL_SIZE;


    public static final int TURN_DELAY_MILLIS = 25;

    // Position des éléments du mur
    Vector<Float> wall = null;
    // Liste des sprites (bonus/malus) à afficher
    Vector<Sprite> sprite = null;

    private Paint wallPaint;

    // Gestion du timer
    private Handler handler;
    private boolean running = false;

    // dernière pôsition du joueur
    private PointF last = new PointF();

    private Matrix transform;
    private Matrix inverseTransform;
    private float[] pos = new float[2];


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
        // On instancie les vecteurs
        wall =  new Vector<>();
        sprite = new Vector<>();

        // Caractéristiques des bords
        wallPaint = new Paint();
        wallPaint.setAntiAlias(true);
        wallPaint.setColor(0xff224499 );
        wallPaint.setStyle(Paint.Style.STROKE);
        wallPaint.setStrokeWidth(PIXEL_SIZE+1);
        wallPaint.setStrokeCap(Paint.Cap.ROUND);

        //On prépare le timer
        handler = new Handler();

        // On écoute les évènements.
        setOnTouchListener(this);

        createGame();
    }

    // Démarrer l'animation
    private void startTimer(){
        if (running == true) return;
        running = true;
        reStartTimer();
    }

    // Demande d'une nouveau tour de jeu
    private synchronized void reStartTimer(){
        handler.removeCallbacks(null);
        handler.postDelayed(new Runnable(){
            @Override
            public void run(){
                if (running) reStartTimer();
                update();
            }
        }, TURN_DELAY_MILLIS);
    }

    // Arrêt de l'animation
    private  void stopTimer(){
        running = false;
        handler.removeCallbacks(null);
    }



    // Quand la taille du jeu est connue
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w == 0 || h == 0) return;

        transform = new Matrix();
        RectF screen = new RectF(0,0,w,h);
        transform.setRectToRect(VIRTUAL_SCREEN,screen, Matrix.ScaleToFit.CENTER);

        inverseTransform = new Matrix();
        transform.invert(inverseTransform);

    }

    private void createGame() {
        createWall();
    }

    private void createWall() {
        wall.add((float) Math.random());
        updateWall(LINES);
    }

    /**
     * Ajoute le nombre de lignes indiquées au mur
     * Si le mur dépasse la taille de la fenêtre, on supprime les anciennes lignes
     * @param nb
     */
    private void updateWall(int nb) {
        for(int i = 0; i< nb; ++i) {
            float last = wall.lastElement(); // On repart de la dernière position
            last += (Math.random() - 0.5f) * 0.2f; // On ajoute une petite valeur, positive ou négative
            if (last < 0) last = 0; // On reste entre 0 et 1
            if (last > 1) last = 1;
            wall.add(last);
            if (wall.size() > LINES) wall.remove(0); // On retire une ligne si nécessaire
        }
    }

    /**
     * Gère un tour de jeu
     */
    private void update(){
        updateWall(SPEED); // On déplace les murs
        updateSprite(); // On déplace les sprites

        int i = 0;
        while (i< sprite.size()){ // On supprime les sprites touchés par le joueur
            Sprite s = sprite.elementAt(i);
            if (s.contains(last.x,last.y,20)){
                sprite.remove(i);
            } else ++i;
        }

        invalidate(); // On demande le rafraîchissement de l'écran
    }


    /**
     * Gestion d'un tour pour les sprites :
     *
     * ajout aléatoire d'un sprite
     * déplacement de tous les sprites
     */
    private void updateSprite() {
        if (sprite.size()< 10 && Math.random()< 0.05)
            sprite.add(Math.random() > 0.5f ? new Bonus() :  new Malus());


        for(Sprite s : sprite){
            s.act();
        }
    }

    /**
     * Réalise le dessin du jeu
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(0xff99aaee);

        if (wall == null) return; // Si le jeu n'est pas initialisé, on ne fait rien

        // On sauvegarde la transformation actuelle et on applique la notre
        canvas.save();
        canvas.concat(transform);

        // Affichage des murs
        for(int i = 0; i < wall.size(); ++i){
            canvas.drawLine(0,HEIGHT-i* PIXEL_SIZE - PIXEL_SIZE,
                    0.3f*wall.get(i)*WIDTH,HEIGHT-i* PIXEL_SIZE - PIXEL_SIZE,wallPaint);

            canvas.drawLine((0.7f+0.3f*wall.get(i))*WIDTH,HEIGHT-i* PIXEL_SIZE - PIXEL_SIZE,
                    WIDTH,HEIGHT-i* PIXEL_SIZE - PIXEL_SIZE,wallPaint);
        }

        //Affichage des sprites
        for(Sprite s : sprite){
            s.draw(canvas);
        }

        canvas.restore(); // On restore la transformation d'origine
    }

    @Override
    public boolean onTouch(View v, MotionEvent motionEvent) {
        boolean used = true;

        int action = motionEvent.getActionMasked();
        int ndx = motionEvent.getActionIndex();
        if (action == MotionEvent.ACTION_DOWN){
            // On vient de toucher l'écran, on démarre l'animation
            startTimer();

        } else if (action == MotionEvent.ACTION_UP){
            // On ne touche plus l'écran, arrêter l'animation
            stopTimer();
        }
        // Mise à jour de la position. Il faut appliquer la matrice
        // de transformation inverse, ce qui nécessite de placer le point
        // dans un float[]
        pos[0] = motionEvent.getX(ndx);
        pos[1] = motionEvent.getY(ndx);

        inverseTransform.mapPoints(pos);

        last.set(pos[0], pos[1]);

        return used;
    }
}
