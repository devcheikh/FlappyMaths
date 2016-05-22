package com.betcheg.flappymaths;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by root on 22/05/16.
 */

public class Game extends LinearLayout {


    protected double SCREEN_WIDTH = 0;
    protected double SCREEN_HEIGHT = 0;
    private LinkedList<Sprite> sprites;
    private Bird bird;
    private Background background;
    private Pipe pipe;
    private Block block;
    private TextCalculus texteCalculus;
    private Score score;
    private boolean scorePasEncoreCompte = false;

    private int random;
    private int random2;
    private int random3;
    private int random4;
    private int result;

    private int transparency;
    private boolean gameOver = false;

    private boolean screenPressed = false;

    Context c;




    public Game(Context c, int h, int w){

        super(c);
        sprites = new LinkedList<Sprite>();
        this.c = (MainActivity) c;
        SCREEN_WIDTH = w;
        SCREEN_HEIGHT = h;

        /* Game graphics */
        background = new Background(c, (int) SCREEN_WIDTH , (int) SCREEN_HEIGHT);
        sprites.add(background);
        pipe = new Pipe(c, (int) SCREEN_WIDTH , (int) SCREEN_HEIGHT);
        sprites.add(pipe);
        block = new Block(c, (int) SCREEN_WIDTH , (int) SCREEN_HEIGHT, (int) SCREEN_HEIGHT);
        sprites.add(block);
        bird = new Bird(c, (int) SCREEN_WIDTH , (int) SCREEN_HEIGHT);
        sprites.add(bird);
        texteCalculus = new TextCalculus(c, (int) SCREEN_WIDTH , (int) SCREEN_HEIGHT);
        sprites.add(texteCalculus);
        score = new Score(c, (int) SCREEN_WIDTH , (int) SCREEN_HEIGHT);
        sprites.add(score);

        setWillNotDraw(false); // Will call onDraw();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if(event.getAction() == MotionEvent.ACTION_DOWN && screenPressed == false && !gameOver)
        {
            screenPressed = true;
            // The bird should not jump higher than screen size + jump size
            if(bird.getY() > -bird.getHeight()) bird.jump();
        }

        else if(event.getAction() == MotionEvent.ACTION_UP) {
            this.screenPressed = false;
        }

        return true;
    }


    protected void onDraw(Canvas canvas)
    {
        if (block.getNewLevel()){
            // Assigner couleur, nombre etc..
            // Assigner taille
            texteCalculus.setFini(false);
            scorePasEncoreCompte=true;
            if (score.getScore() >= 3 ) block.setNumbers(3);

            generateRandomExpression();
            generateBlockPlacement();
            generateBlockColors();

            // Let's start the new level
            block.setNewLevel(false);
        }

        if(block.getX() <= SCREEN_WIDTH * 0.80) texteCalculus.setFini(true);



        Iterator<Sprite> iterator = sprites.iterator();
        while (iterator.hasNext()) {
            Sprite sprite = iterator.next();
            sprite.onDraw(canvas);
        }

        if(!gameOver) {
            if (bird.getX() + bird.getWidth() >= block.getX() && bird.getX() <= block.getX() + block.getWidth()) {
                if (!(bird.getY() >= block.getYnoCollision() - ((int) 0.33 * bird.getHeight()) && bird.getY() < block.getYnoCollision() + block.getHeight())) {
                    gameOver = true;
                    transparency = 255;
                    block.stop();
                    pipe.stop();
                }
            }
            if (bird.getX() >= block.getX() + block.getWidth() && scorePasEncoreCompte) {
                score.setScore(score.getScore() + 1);
                scorePasEncoreCompte = false;
            }
        }
        else {
            canvas.drawColor(Color.argb(transparency, 255, 255, 255));
            transparency -=20;
            if(transparency < 0) transparency = 0;
            if (bird.getY() > SCREEN_HEIGHT){
                gameOver();
            }
        }
        invalidate(); // Will loop onDraw
    }

    public void generateRandomExpression(){
        random = (int) (Math.random()* 20); // First number (0,1,2,...,20)
        random2 = (int) (Math.random()* 1); // Artithmetic expression (+, -, *, /)
        random3 = (int) (Math.random()* 5); // Second number

        result = texteCalculus.setExpression(random, random2, random3);

        random = (int) (Math.random()* block.getNumbers());

        // Random false number
        random2 = (int) (Math.random()* 9) +1;
        while(random2 == result){
            random2 = (int) (Math.random()* 9) +1;
        }

        // Random false number 2
        random3 = (int) (Math.random()* 9) +1;
        while(random3 == result || random3 == random2){
            random3 = (int) (Math.random()* 9) +1;
        }

        if (random%block.getNumbers() == 0 ) {
            random = (int) Math.random()*2;
            random = random*2 - 1;
            random4 = (int) Math.random()*2;
            random4 = random4*2 - 1;
            block.setValues(result, result + random*random2, result + random4*random3);
        }
        else if (random%block.getNumbers() == 1 ) {
            random = (int) Math.random()*2;
            random = random*2 - 1;
            random4 = (int) Math.random()*2;
            random4 = random4*2 - 1;
            block.setValues(result + random*random2,result, result + random4*random3);
        }
        else {
            random = (int) Math.random()*2;
            random = random*2 - 1;
            random4 = (int) Math.random()*2;
            random4 = random4*2 - 1;
            block.setValues(result + random*random2, result + random4*random3, result);
        }
        block.setGoodValue(result);
    }

    public void generateBlockColors(){
        random = (int) (Math.random()* 7);
        random2 = (int) (Math.random()* 7);
        while(random2 == random){
            random2 = (int) (Math.random()* 7);
        }
        random3 = (int) (Math.random()* 7);
        while(random3 == random || random3 == random2){
            random3 = (int) (Math.random()* 7);
        }
        block.setColors(random, random2, random3);
    }
    public void generateBlockPlacement() {
        random = (int) (Math.random()* (SCREEN_HEIGHT*0.25));
        pipe.setSize(random, (int) (SCREEN_HEIGHT * 0.25 - random));
        block.setHeight((int) (SCREEN_HEIGHT - (SCREEN_HEIGHT * 0.25)) / block.getNumbers());
        block.setStartY(random);
    }


    public void gameOver(){
        bird.setCurrentHeight((int) (SCREEN_HEIGHT / 2));
        bird.jump();
        score.setScore(0);
        block.resetX();
        block.setNumbers(2);
        pipe.resetX();
        block.setNewLevel(true);
        block.restart();
        pipe.restart();
        gameOver = false;
    }
}
