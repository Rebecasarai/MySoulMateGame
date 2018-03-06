/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rebecasarai.mysoulmate.Graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;

import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.Landmark;
import com.rebecasarai.mysoulmate.Views.GraphicOverlay;
import com.rebecasarai.mysoulmate.Models.Heart;
import com.rebecasarai.mysoulmate.R;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Random;

/**
 * Clase en la que se dibuja sobre la pantalla al reconocer el rostro de una alma gemela.
 * Se entra en el draw cada por cada frame capturado por la camara, es decir, dependiendo de los fotogramas por segundos establecidos en el builder del dectector.
 *
 */
public class FaceGraphic extends GraphicOverlay.Graphic {
    private Context context;

    private ArrayList<Heart> mHearts;

    private Random random = new Random();
    private SecureRandom r = new SecureRandom();
    private Bitmap scaledBitmap;
    private BitmapFactory.Options options;

    private static final int COLOR_CHOICES = Color.RED;

    private Paint mBitmapPaint = new Paint();
    private Paint mFiltroPaint = new Paint();

    private volatile Face mFace;

    private int alpha = 10;


    public FaceGraphic(GraphicOverlay overlay, Context context, ArrayList<Heart> hearts) {
        super(overlay);
        this.mHearts = hearts;
        this.context = context;
    }



    /**
     * Actualiza la instancia de cara desde la detección del marco más reciente.
     *      
     */
    public void updateFace(Face face) {
        mFace = face;
        postInvalidate();
    }

    /**
     * Dibuja las anotaciones de la cara para la posición en el canvas.
     *     
     */
    @Override
    public void draw(Canvas canvas) {
        Face face = mFace;
        if (face == null) {
            return;
        }

        setBitmap();

        setTonoFiltro(canvas);

        animacionSuperior(canvas);
        animacionLateralDerecho();
        animacionInferior(canvas);
        animacionIzquierda(canvas);

        drawCorazones(canvas);
        corazonesPequeñosRelleno(canvas);
    }


    /**
     * Establece la imagen a mostarr en el filtro. En este caso corazones
     */
    private void setBitmap(){
        options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(), R.drawable.heart, options);

        options.inSampleSize = 4;
        options.inJustDecodeBounds = false;
        scaledBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.heart, options);
    }


    /**
     * Metodo con el que se da el tono al filtro de la camara.
     * Se actualiza la opacidad Alpha, el color, el estilo, efecto y forma
     * @param canvas
     */
    private void setTonoFiltro(Canvas canvas){
        mFiltroPaint.setColor(Color.RED);
        mFiltroPaint.setAntiAlias(true);
        mFiltroPaint.setFilterBitmap(true);
        mFiltroPaint.setDither(true);

        ColorFilter filter = new PorterDuffColorFilter(context.getResources().getColor(R.color.colorPrimaryRed), PorterDuff.Mode.SRC_IN);
        mFiltroPaint.setColorFilter(filter);
        mFiltroPaint.setShader(new RadialGradient(canvas.getWidth() / 2, canvas.getHeight() / 2,
                canvas.getHeight() / 3, Color.TRANSPARENT, Color.BLACK, Shader.TileMode.MIRROR));

        Rect recta = new Rect(0, 0, canvas.getWidth()+canvas.getWidth()/2, canvas.getHeight()+canvas.getHeight()/2);

        mFiltroPaint.setStyle(Paint.Style.FILL);

        if(alpha<=80){
            alpha++;
        }

        mFiltroPaint.setAlpha(alpha);
        canvas.drawRect(recta, mFiltroPaint);
    }

    /**
     * Metodo que calcula un numero al azar con un rango de numeros pasados por parametros
     * @param min Numero minimo
     * @param max Numero maximo
     * @return int que representa numero random
     */
    private int randomRango(int min, int max) {
        return r.nextInt(max - min) + min;
    }


    /**
     * Se establece la posicion del corazon de la parte inferior del canvas.
     * Se actualiza la posición tomando la actual y sumando la velocidad.
     * La velocidad es un valor entero que representa los pixeles a moverse.
     * @param canvas en el que dibuja
     */
    private void animacionSuperior(Canvas canvas){

        int heartTopX= mHearts.get(0).getPositionX();

        if(!mHearts.get(0).isDevueltaX()){
            if(heartTopX>canvas.getWidth()){
                mHearts.get(0).setDevueltaX(true);
            }
            mHearts.get(0).setPositionX(heartTopX + mHearts.get(0).getSpeedX());


        }else{
            if(heartTopX<10){
                mHearts.get(0).setDevueltaX(false);
            }
            mHearts.get(0).setPositionX(heartTopX - mHearts.get(0).getSpeedX());
        }



        if(!mHearts.get(0).isDevueltaY()){
            if(mHearts.get(0).getPositionY()>200){
                mHearts.get(0).setDevueltaY(true);
            }
            mHearts.get(0).setPositionY(mHearts.get(0).getPositionY() + mHearts.get(0).getSpeedY());
        }else{
            if(mHearts.get(0).getPositionY()<100){
                mHearts.get(0).setDevueltaY(false);
            }
            mHearts.get(0).setPositionY(mHearts.get(0).getPositionY() - mHearts.get(0).getSpeedY());
        }
    }


    /**
     * Se establece la posicion del corazon de la parte lateral Derecha del canvas.
     * Se actualiza la posición tomando la actual y sumando la velocidad.
     * La velocidad es un valor entero que representa los pixeles a moverse.
     */
    private void animacionLateralDerecho(){
        if(!mHearts.get(1).isDevueltaX()){
            if(mHearts.get(1).getPositionX()>1000){
                mHearts.get(1).setDevueltaX(true);
            }
            mHearts.get(1).setPositionX(mHearts.get(1).getPositionX() + mHearts.get(1).getSpeedX());
        }else{
            if(mHearts.get(1).getPositionX()<900){
                mHearts.get(1).setDevueltaX(false);
            }
            mHearts.get(1).setPositionX(mHearts.get(1).getPositionX() - mHearts.get(1).getSpeedX());
        }

        if(!mHearts.get(1).isDevueltaY()){
            if(mHearts.get(1).getPositionY()>1100){
                mHearts.get(1).setDevueltaY(true);
            }
            mHearts.get(1).setPositionY(mHearts.get(1).getPositionY() + mHearts.get(1).getSpeedY());
        }else{
            if(mHearts.get(1).getPositionY()<200){
                mHearts.get(1).setDevueltaY(false);
            }
            mHearts.get(1).setPositionY(mHearts.get(1).getPositionY() - mHearts.get(1).getSpeedY());
        }
    }

    /**
     * Se establece la posicion del corazon de la parte inferior del canvas.
     * Se actualiza la posición tomando la actual y sumando la velocidad.
     * La velocidad es un valor entero que representa los pixeles a moverse.
     *
     * @param canvas en el que dibuja
     */
    private void animacionInferior(Canvas canvas){
        if(!mHearts.get(2).isDevueltaX()){
            if(mHearts.get(2).getPositionX()>canvas.getWidth()){
                mHearts.get(2).setDevueltaX(true);
            }
            mHearts.get(2).setPositionX(mHearts.get(2).getPositionX() + mHearts.get(2).getSpeedX());


        }else{
            if(mHearts.get(2).getPositionX()<100){
                mHearts.get(2).setDevueltaX(false);
            }
            mHearts.get(2).setPositionX(mHearts.get(2).getPositionX() - mHearts.get(2).getSpeedX());
        }


        if(!mHearts.get(2).isDevueltaY()){
            if(mHearts.get(2).getPositionY()>canvas.getHeight()){
                mHearts.get(2).setDevueltaY(true);
            }
            mHearts.get(2).setPositionY(mHearts.get(2).getPositionY() + mHearts.get(2).getSpeedY());
        }else{
            if(mHearts.get(2).getPositionY()<1050){
                mHearts.get(2).setDevueltaY(false);
            }
            mHearts.get(2).setPositionY(mHearts.get(2).getPositionY() - mHearts.get(2).getSpeedY());
        }
    }

    /**
     * Se establece la posicion del corazon de la parte lateral izqierda del canvas.
     * Se actualiza la posición tomando la actual y sumando la velocidad.
     * La velocidad es un valor entero que representa los pixeles a moverse.
     *
     * @param canvas en el que dibuja
     */
    private void animacionIzquierda(Canvas canvas){
        if(!mHearts.get(3).isDevueltaX()){
            if(mHearts.get(3).getPositionX()>180){
                mHearts.get(3).setDevueltaX(true);
            }
            mHearts.get(3).setPositionX(mHearts.get(3).getPositionX() + mHearts.get(3).getSpeedX());
        }else{
            if(mHearts.get(3).getPositionX()<10){
                mHearts.get(3).setDevueltaX(false);
            }
            mHearts.get(3).setPositionX(mHearts.get(3).getPositionX() - mHearts.get(3).getSpeedX());
        }

        if(!mHearts.get(3).isDevueltaY()){
            if(mHearts.get(3).getPositionY()>canvas.getHeight()){
                mHearts.get(3).setDevueltaY(true);
            }
            mHearts.get(3).setPositionY(mHearts.get(3).getPositionY() + mHearts.get(3).getSpeedY());
        }else{
            if(mHearts.get(3).getPositionY()<200){
                mHearts.get(3).setDevueltaY(false);
            }
            mHearts.get(3).setPositionY(mHearts.get(3).getPositionY() - mHearts.get(3).getSpeedY());
        }
    }


    /**
     * Diuja los corazones grandes en el canvas
     * @param canvas
     */
    private void drawCorazones(Canvas canvas){
        canvas.drawBitmap(scaledBitmap, mHearts.get(0).getPositionX(), mHearts.get(0).getPositionY(), mBitmapPaint);
        canvas.drawBitmap(scaledBitmap, mHearts.get(1).getPositionX(), mHearts.get(1).getPositionY(), mBitmapPaint);
        canvas.drawBitmap(scaledBitmap, mHearts.get(2).getPositionX(), mHearts.get(2).getPositionY(), mBitmapPaint);
        canvas.drawBitmap(scaledBitmap, mHearts.get(3).getPositionX(), mHearts.get(3).getPositionY(), mBitmapPaint);
    }


    /**
     * Dibuja los corazones pequeños con movimientos al azar en el canvas
     * @param canvas
     */
    private void corazonesPequeñosRelleno(Canvas canvas){

        options.inSampleSize=6;
        scaledBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.heart, options);
        //Herat TOP
        canvas.drawBitmap(scaledBitmap, random.nextInt(canvas.getWidth()-100), random.nextInt(200), mBitmapPaint);

        //Heart Right
        canvas.drawBitmap(scaledBitmap, randomRango(canvas.getWidth()-300,canvas.getWidth()), random.nextInt(canvas.getHeight()-200), mBitmapPaint);

        //Heart Bottom
        canvas.drawBitmap(scaledBitmap, random.nextInt(canvas.getWidth()-100), randomRango(1300,1500), mBitmapPaint);

        //Heart left
        canvas.drawBitmap(scaledBitmap, randomRango(50, 100), random.nextInt(canvas.getHeight()), mBitmapPaint);
    }



}


