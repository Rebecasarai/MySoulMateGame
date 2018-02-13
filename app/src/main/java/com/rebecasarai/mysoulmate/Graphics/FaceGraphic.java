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
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
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

    private static final int COLOR_CHOICES = Color.RED;

    private Paint mFacePositionPaint;
    private Paint mIdPaint;
    private Paint mBoxPaint;

    private volatile Face mFace;
    private int mFaceId;
    private int heartTopX;




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

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(), R.drawable.heart, options);
        //Lo reduzco por 5
        options.inSampleSize = 4;
        options.inJustDecodeBounds = false;
        Bitmap scaledBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.heart, options);

        Paint p = new Paint();
        Paint paintCameraEntera = new Paint();
        p.setColor(Color.RED);
        p.setAntiAlias(true);
        p.setFilterBitmap(true);
        p.setDither(true);

        paintCameraEntera.setColorFilter(new PorterDuffColorFilter(context.getResources().getColor(R.color.colorPrimaryDark), PorterDuff.Mode.MULTIPLY));


        if(LeftX>100||LeftY>800){

        }
        if(RightX>1000||RightY>1200){
        }
        if(BottomX>800||BottomX>1300) {
        }
            //TopX = mSharedPref.getInt("startingTopX", 1);

            // TopX = random.nextInt(800);
            TopY = random.nextInt(100);

            LeftX = random.nextInt(100);
            LeftY = random.nextInt(800);

            RightX = randomRango(900, 1000);
            RightY = random.nextInt(1200);

            BottomX = random.nextInt(800);
            BottomY = randomRango(1200, 1300);

       Rect r = new Rect(canvas.getWidth(), canvas.getHeight()-canvas.getHeight(), canvas.getWidth(), canvas.getHeight());

        paintCameraEntera.setStyle(Paint.Style.FILL);
        paintCameraEntera.setAlpha(100);
        //paintCameraEntera.setColor(Color.RED);
        canvas.drawRect(r, paintCameraEntera);

       // canvas.drawBitmap(scaledBitmap, TopX, TopY, p);
       // canvas.drawBitmap(scaledBitmap, LeftX, LeftY, p);
        //canvas.drawBitmap(scaledBitmap, RightX, RightY, p);

        Heart heartTop = mHearts.get(0);
        Heart heartRight = mHearts.get(1);
        Heart heartBottom = mHearts.get(2);
        Heart heartLeft = mHearts.get(3);

        heartTopX= heartTop.getPositionX();


            if(heartRight.getPositionX()>=1000){

                heartRight.setPositionX(heartRight.getPositionX() - heartRight.getSpeedX());
            }
            if(heartRight.getPositionX()<=900){
                heartRight.setPositionX(heartRight.getPositionX() + heartRight.getSpeedX());
            }

            heartRight.setPositionX(heartRight.getPositionX() - heartRight.getSpeedX());
            heartRight.setPositionY(heartRight.getPositionY() + heartRight.getSpeedY());


        //------------TOP


            if(!heartTop.isDevueltaX()){
                if(heartTopX>1100){
                    heartTop.setDevueltaX(true);
                }
                heartTop.setPositionX(heartTopX + heartTop.getSpeedX());
            }else{
                if(heartTopX<40){
                heartTop.setDevueltaX(false);
                }
                heartTop.setPositionX(heartTopX - heartTop.getSpeedX());
            }

            if(!heartTop.isDevueltaY()){
                if(heartTop.getPositionY()>200){
                    heartTop.setDevueltaY(true);
                }
                heartTop.setPositionY(heartTop.getPositionY() + heartTop.getSpeedY());
            }else{
                if(heartTop.getPositionY()<40){
                    heartTop.setDevueltaY(false);
                }
                heartTop.setPositionY(heartTop.getPositionY() - heartTop.getSpeedY());
            }

        //------------TOP

        if(heartBottom.getPositionX()>1100){
            heartBottom.setPositionX(heartTop.getPositionX() - heartTop.getSpeedX());
        }
        if(heartBottom.getPositionX()<5){
            heartBottom.setPositionX(heartBottom.getPositionX() + heartBottom.getSpeedX());
        }
        heartBottom.setPositionX(heartBottom.getPositionX() + heartBottom.getSpeedX());



        canvas.drawBitmap(scaledBitmap, heartTop.getPositionX(), heartTop.getPositionY(), p);
        canvas.drawBitmap(scaledBitmap, heartRight.getPositionX(), heartRight.getPositionY() + heartRight.getSpeedY(), p);
        canvas.drawBitmap(scaledBitmap, heartBottom.getPositionX(), heartBottom.getPositionY(), p);
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

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
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


