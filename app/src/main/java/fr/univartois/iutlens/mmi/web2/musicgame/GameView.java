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

import java.util.Iterator;
import java.util.Vector;


/**
 * Classe affichant le jeu.
 */
public class GameView extends View implements View.OnTouchListener {


    public static final int WIDTH = 1080;
    public static final int HEIGHT = 1920;
    public static final int Y_JOUEUR = HEIGHT - 300;
    public static final RectF VIRTUAL_SCREEN = new RectF(0, 0, WIDTH, HEIGHT);

    // Nombre de lignes affichées à chaque tour
    public static final int SPEED = 2;
    // Taille en pixels de la taille de chaque ligne
    public static final int PIXEL_SIZE = 20;

    public static final int LINES = HEIGHT / PIXEL_SIZE;
    public static final int LINES_JOUEUR = Y_JOUEUR / PIXEL_SIZE;
    
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
    private SpriteSheet spriteSheet;
    private Paint scorePaint;
    private int scoreNb = 0;


    public GameView(Context context) {
        super(context);
        init(context,null, 0);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs, 0);
    }

    public GameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context,attrs, defStyle);
    }


    private void init(Context context, AttributeSet attrs, int defStyle) {
        SpriteSheet.register(context,R.drawable.ronds,2,1);
        SpriteSheet.register(context,R.drawable.cursor,1,1);
        SpriteSheet.register(context,R.drawable.fond1,1,1);
        SpriteSheet.register(context,R.drawable.fond2,1,1);
        SpriteSheet.register(context,R.drawable.fond3,1,1);
        SpriteSheet.register(context,R.drawable.fond4,1,1);
        SpriteSheet.register(context,R.drawable.fond5,1,1);
        SpriteSheet.register(context,R.drawable.fond6,1,1);
        SpriteSheet.register(context,R.drawable.sprite_bonus,2,1);
        SpriteSheet.register(context,R.drawable.sprite_malus,2,1);

        spriteSheet = SpriteSheet.get(R.drawable.cursor);

        // On instancie les vecteurs
        wall =  new Vector<>();
        sprite = new Vector<>();

        // Caractéristiques des bords
        wallPaint = new Paint();
        wallPaint.setAntiAlias(true);
        wallPaint.setColor(0xff000000 );
        wallPaint.setStyle(Paint.Style.STROKE);
        wallPaint.setStrokeWidth(PIXEL_SIZE+1);
        wallPaint.setStrokeCap(Paint.Cap.ROUND);
        //COLOR TXT SCORE
        scorePaint = new Paint();
//        scorePaint.setAntiAlias(true);
        scorePaint.setStyle(Paint.Style.STROKE);
        scorePaint.setColor(0xffffffff );

        scorePaint.setStrokeWidth(3);
        scorePaint.setTextSize(48);

        //On prépare le timer
        handler = new Handler();

        // On écoute les évènements.
        setOnTouchListener(this);

        createGame();
    }

    // Démarrer l'animation
    private synchronized void startTimer(){
        if (running == true) return;
        running = true;
        reStartTimer();
    }

    // Demande d'une nouveau tour de jeu
    private synchronized void reStartTimer(){
        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(new Runnable(){
            @Override
            public void run(){
                if (running) {
                    reStartTimer();
                    update();
                }
            }
        }, TURN_DELAY_MILLIS);
    }

    // Arrêt de l'animation
    private synchronized void stopTimer(){
        handler.removeCallbacksAndMessages(null);
        running = false;
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
            last += (Math.random() - 0.5f) * 0.1f; // On ajoute une petite valeur, positive ou négative
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
            if (s.contains(last.x,last.y,30)){
                sprite.remove(i);
                if (s instanceof Bonus){scoreNb += 1;}
                else{scoreNb -= 5;}
            } else ++i;
        }

        checkWall();

        invalidate(); // On demande le rafraîchissement de l'écran
    }


    /**
     * Gestion d'un tour pour les sprites :
     *
     * ajout aléatoire d'un sprite
     * déplacement de tous les sprites
     */
    private void updateSprite() {
        if (sprite.size()< 10 && Math.random()< 0.05){ // 5% d'ajouter un sprite si miuns de 10 sprites.
            float minFFF = getStartXWall((wall.size()-1));
            float maxFFF = (minFFF+3*WIDTH/4);
            sprite.add(Math.random() > 0.5f ? new Bonus(minFFF,maxFFF) :  new Malus(minFFF,maxFFF)); // 50% Bonus / 50% Malus
        }


        Iterator<Sprite> it = sprite.iterator();
        while(it.hasNext()){

            Sprite s = it.next();
            if (s.act()) it.remove();
        }

    }

    /**
     * Réalise le dessin du jeu
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(0xff333333);


        if (wall == null) return; // Si le jeu n'est pas initialisé, on ne fait rien

        // On sauvegarde la transformation actuelle et on applique la notre
        canvas.save();
        canvas.concat(transform);

        //affichage fond
        SpriteSheet.get(R.drawable.fond1).paint(canvas,0,0f,0f);
        SpriteSheet.get(R.drawable.fond2).paint(canvas,0,0f,320f);
        SpriteSheet.get(R.drawable.fond3).paint(canvas,0,0f,320*2f);
        SpriteSheet.get(R.drawable.fond4).paint(canvas,0,0f,320*3f);
        SpriteSheet.get(R.drawable.fond5).paint(canvas,0,0f,320*4f);
        SpriteSheet.get(R.drawable.fond6).paint(canvas,0,0f,320*5f);
        // Affichage des murs
        for(int i = 0; i < wall.size(); ++i){
            /*
            canvas.drawLine(0,HEIGHT-i* PIXEL_SIZE - PIXEL_SIZE,
                    0.3f*wall.get(i)*WIDTH,HEIGHT-i* PIXEL_SIZE - PIXEL_SIZE,wallPaint);

            canvas.drawLine((0.7f+0.3f*wall.get(i))*WIDTH,HEIGHT-i* PIXEL_SIZE - PIXEL_SIZE,
                    WIDTH,HEIGHT-i* PIXEL_SIZE - PIXEL_SIZE,wallPaint);
             */
            canvas.drawLine(getStartXWall(i), HEIGHT-i* PIXEL_SIZE - PIXEL_SIZE,
                    getStartXWall(i) +(WIDTH*3/4), HEIGHT-i* PIXEL_SIZE - PIXEL_SIZE, wallPaint);
        }

        //Affichage des sprites
        for(Sprite s : sprite){
            s.draw(canvas);
        }

        // Affichage de la div du doigt
        canvas.drawLine(0,Y_JOUEUR, WIDTH,Y_JOUEUR,wallPaint);
        spriteSheet.paint(canvas,0,last.x- spriteSheet.w/2,Y_JOUEUR- spriteSheet.h/2);

        //



        canvas.restore(); // On restore la transformation d'origine

        canvas.drawText("Score : "+scoreNb, 20, 50, scorePaint);
    }

    private float getStartXWall(int line) {
        return 0.3f*wall.get(line)*WIDTH;
    }

    public void checkWall() {
//        Y_JOUEUR
        float x_joueur = last.x;
        float x_wall = getStartXWall(LINES_JOUEUR);

        if ( x_wall > x_joueur ) {
            scoreNb--;
        } else if ( x_wall + (WIDTH * 3/4) < x_joueur) {
            scoreNb--;
        }

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
            //stopTimer();
        }
        // Mise à jour de la position. Il faut appliquer la matrice
        // de transformation inverse, ce qui nécessite de placer le point
        // dans un float[]
        pos[0] = motionEvent.getX(ndx);
        pos[1] = motionEvent.getY(ndx);

        inverseTransform.mapPoints(pos);
        pos[1]= Y_JOUEUR;

        last.set(pos[0], pos[1]);

        return used;
    }
}
