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
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Shader;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.Landmark;
import com.rebecasarai.mysoulmate.Camera.GraphicOverlay;
import com.rebecasarai.mysoulmate.Models.Heart;
import com.rebecasarai.mysoulmate.R;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Random;

public class FaceGraphic extends GraphicOverlay.Graphic {
    private static final float FACE_POSITION_RADIUS = 10.0f;
    private static final float ID_TEXT_SIZE = 40.0f;
    private static final float ID_Y_OFFSET = 50.0f;
    private static final float ID_X_OFFSET = -50.0f;
    private static final float BOX_STROKE_WIDTH = 5.0f;
    private Context context;
    private int TopX, TopY, LeftX, LeftY, RightX, RightY, BottomX, BottomY;
    private ArrayList<Heart> mHearts;

    Random random = new Random();
    SecureRandom r = new SecureRandom();
    Bitmap scaledBitmap;
    BitmapFactory.Options options;

    private static final int COLOR_CHOICES = Color.RED;

    private Paint mFacePositionPaint;
    private Paint mIdPaint;
    private Paint mBoxPaint;

    private volatile Face mFace;
    private int mFaceId;
    private int heartTopX;
    private Rect recta;
    Paint p = new Paint();




    public FaceGraphic(GraphicOverlay overlay, Context context, ArrayList<Heart> hearts) {
        super(overlay);

        final int selectedColor = COLOR_CHOICES;

        mFacePositionPaint = new Paint();
        mFacePositionPaint.setColor(selectedColor);

        mIdPaint = new Paint();
        mIdPaint.setColor(selectedColor);
        mIdPaint.setTextSize(ID_TEXT_SIZE);

        mBoxPaint = new Paint();
        mBoxPaint.setColor(selectedColor);
        mBoxPaint.setStyle(Paint.Style.STROKE);
        mBoxPaint.setStrokeWidth(BOX_STROKE_WIDTH);


        this.mHearts = hearts;

        this.context = context;
    }

    public void setId(int id) {
        mFaceId = id;
    }


    /**
     * Actualiza la instancia de cara desde la detección del marco más reciente. Invalida el
     *       * porciones relevantes de la superposición para activar un redibujado.
     *      
     */
    public void updateFace(Face face) {
        mFace = face;
        postInvalidate();
    }

    /**
     * Dibuja las anotaciones de la cara para la posición en el lienzo suministrado.
     *     
     */
    @Override
    public void draw(Canvas canvas) {
        Face face = mFace;
        if (face == null) {
            return;
        }

        double viewWidth = canvas.getWidth();
        double viewHeight = canvas.getHeight();
        //double imageWidth = mBitmap.getWidth();
        //double imageHeight = mBitmap.getHeight();
        double scale = Math.min(viewWidth / face.getPosition().x + face.getWidth(), viewHeight / face.getPosition().y + face.getHeight());
        Log.v("scale", "" + scale);

        //Punto medio de la cara
        float x = translateX(face.getPosition().x + face.getWidth() / 2);
        float y = translateY(face.getPosition().y + face.getHeight() / 2);
        //canvas.drawCircle(x, y, FACE_POSITION_RADIUS, mFacePositionPaint);
        //canvas.drawText("id: " + mFaceId, x + ID_X_OFFSET, y + ID_Y_OFFSET, mIdPaint);
        //canvas.drawText("Es tu alma gemelaa", x - ID_X_OFFSET * 3, y - ID_Y_OFFSET * 3, mIdPaint);

        options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(), R.drawable.heart, options);
        //Lo reduzco por 5
        options.inSampleSize = 4;
        options.inJustDecodeBounds = false;
        scaledBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.heart, options);


        Paint paintCameraEntera = new Paint();
        paintCameraEntera.setColor(Color.RED);
        paintCameraEntera.setAntiAlias(true);
        paintCameraEntera.setFilterBitmap(true);
        paintCameraEntera.setDither(true);

        ColorFilter filter = new PorterDuffColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
        paintCameraEntera.setColorFilter(filter);
        //paintCameraEntera.setColorFilter(new PorterDuffColorFilter(context.getResources().getColor(R.color.colorPrimaryRed), PorterDuff.Mode.MULTIPLY));
        //paintCameraEntera.setColorFilter(Color.YELLOW, PorterDuff.Mode.DARKEN);

        //paintCameraEntera.setShader(new LinearGradient(0, 0, 0, canvas.getHeight(), Color.RED, Color.WHITE, Shader.TileMode.MIRROR));

        recta = new Rect(canvas.getWidth(), canvas.getHeight()-canvas.getHeight(), canvas.getWidth(), canvas.getHeight());

        paintCameraEntera.setStyle(Paint.Style.FILL);
        //paintCameraEntera.setAlpha(50);
        canvas.drawRect(recta, paintCameraEntera);
        //Heart mHearts.get(0) = mHearts.get(0);
        //Heart mHearts.get(1) = mHearts.get(1);
        //Heart mHearts.get(2) = mHearts.get(2);
        //Heart mHearts.get(3) = mHearts.get(3);

        heartTopX= mHearts.get(0).getPositionX();
        //------------TOP--------//

            if(!mHearts.get(0).isDevueltaX()){
                if(heartTopX>1100){
                    mHearts.get(0).setDevueltaX(true);
                }
                mHearts.get(0).setPositionX(heartTopX + mHearts.get(0).getSpeedX());
            }else{
                if(heartTopX<40){
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

        //------------------- RIGTH---------------//

        if(!mHearts.get(1).isDevueltaX()){
            if(mHearts.get(1).getPositionX()>1100){
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
            if(mHearts.get(1).getPositionY()>1200){
                mHearts.get(1).setDevueltaY(true);
            }
            mHearts.get(1).setPositionY(mHearts.get(1).getPositionY() + mHearts.get(1).getSpeedY());
        }else{
            if(mHearts.get(1).getPositionY()<200){
                mHearts.get(1).setDevueltaY(false);
            }
            mHearts.get(1).setPositionY(mHearts.get(1).getPositionY() - mHearts.get(1).getSpeedY());
        }

        //------------------- BOTTOM ---------------//

        if(!mHearts.get(2).isDevueltaX()){
            if(mHearts.get(2).getPositionX()>1100){
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
            if(mHearts.get(2).getPositionY()>1300){
                mHearts.get(2).setDevueltaY(true);
            }
            mHearts.get(2).setPositionY(mHearts.get(2).getPositionY() + mHearts.get(2).getSpeedY());
        }else{
            if(mHearts.get(2).getPositionY()<1050){
                mHearts.get(2).setDevueltaY(false);
            }
            mHearts.get(2).setPositionY(mHearts.get(2).getPositionY() - mHearts.get(2).getSpeedY());
        }

        //------------------- Left -----------------//

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
            if(mHearts.get(3).getPositionY()>1300){
                mHearts.get(3).setDevueltaY(true);
            }
            mHearts.get(3).setPositionY(mHearts.get(3).getPositionY() + mHearts.get(3).getSpeedY());
        }else{
            if(mHearts.get(3).getPositionY()<200){
                mHearts.get(3).setDevueltaY(false);
            }
            mHearts.get(3).setPositionY(mHearts.get(3).getPositionY() - mHearts.get(3).getSpeedY());
        }
        canvas.drawBitmap(scaledBitmap, mHearts.get(0).getPositionX(), mHearts.get(0).getPositionY(), p);
        canvas.drawBitmap(scaledBitmap, mHearts.get(1).getPositionX(), mHearts.get(1).getPositionY(), p);
        canvas.drawBitmap(scaledBitmap, mHearts.get(2).getPositionX(), mHearts.get(2).getPositionY(), p);
        canvas.drawBitmap(scaledBitmap, mHearts.get(3).getPositionX(), mHearts.get(3).getPositionY(), p);


        options.inSampleSize=6;
        scaledBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.heart, options);

        canvas.drawBitmap(scaledBitmap, random.nextInt(canvas.getWidth()-100), random.nextInt(200), p);
        canvas.drawBitmap(scaledBitmap, randomRango(1200,1300), random.nextInt(canvas.getHeight()-200), p);
        canvas.drawBitmap(scaledBitmap, random.nextInt(canvas.getWidth()-100), randomRango(1300,1500), p);
        canvas.drawBitmap(scaledBitmap, randomRango(50, 100), random.nextInt(canvas.getHeight()), p);

    }


    public void mayorQue(int punto,int pos ){

    }


    public int randomRango(int min, int max) {
        return r.nextInt(max - min) + min;
    }

    /**
     * Optimiza tamaño del bitmap
     *
     * @param realImage
     * @param maxImageSize
     * @param filter
     * @return
     */
    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                                   boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        return Bitmap.createScaledBitmap(realImage, width,
                height, filter);
    }

    /**
     * Dibuja los landmarks
     *
     * @param canvas
     * @param scale
     * @param face
     * @param p
     */
    private void drawFaceLandmarks(Canvas canvas, double scale, Face face, Paint p) {
        p.setColor(Color.GREEN);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(5);

        for (Landmark landmark : face.getLandmarks()) {
            int cx = (int) (landmark.getPosition().x * scale);
            int cy = (int) (landmark.getPosition().y * scale);
            canvas.drawCircle(cx, cy, 5, p);
        }


    }


}


