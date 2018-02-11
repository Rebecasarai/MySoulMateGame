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
import android.graphics.Paint;
import android.util.Log;

import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.Landmark;
import com.rebecasarai.mysoulmate.Camera.GraphicOverlay;
import com.rebecasarai.mysoulmate.R;

import java.security.SecureRandom;
import java.util.Random;

public class FaceGraphic extends GraphicOverlay.Graphic {
    private static final float FACE_POSITION_RADIUS = 10.0f;
    private static final float ID_TEXT_SIZE = 40.0f;
    private static final float ID_Y_OFFSET = 50.0f;
    private static final float ID_X_OFFSET = -50.0f;
    private static final float BOX_STROKE_WIDTH = 5.0f;
    private Context context;
    private int TopX, TopY, LeftX, LeftY, RightX, RightY, BottomX, BottomY;

    Random random = new Random();

    SecureRandom r = new SecureRandom();


    private static final int COLOR_CHOICES = Color.RED;

    private Paint mFacePositionPaint;
    private Paint mIdPaint;
    private Paint mBoxPaint;

    private volatile Face mFace;
    private int mFaceId;


    public FaceGraphic(GraphicOverlay overlay, Context context) {
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

        // Draws a circle at the position of the detected face, with the face's track id below.
        float x = translateX(face.getPosition().x + face.getWidth() / 2);
        float y = translateY(face.getPosition().y + face.getHeight() / 2);
        canvas.drawCircle(x, y, FACE_POSITION_RADIUS, mFacePositionPaint);
        canvas.drawText("id: " + mFaceId, x + ID_X_OFFSET, y + ID_Y_OFFSET, mIdPaint);
        //canvas.drawText("Es tu alma gemelaa", x - ID_X_OFFSET * 3, y - ID_Y_OFFSET * 3, mIdPaint);


        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(), R.drawable.heart, options);
        //Lo reduzco por 5
        options.inSampleSize = 4;
        options.inJustDecodeBounds = false;
        Bitmap scaledBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.heart, options);

        Paint p = new Paint();
        p.setColor(Color.RED);

        //for (int i = 0; i < 3; i++) {
            TopX = random.nextInt(800);
            TopY = random.nextInt(100);

            LeftX = random.nextInt(100);
            LeftY = random.nextInt(800);

            RightX = randomRango(800,900);
            RightY = random.nextInt(800);

            BottomX = random.nextInt(900);
            BottomY = randomRango(800,900);

        //}

        canvas.drawBitmap(scaledBitmap, TopX, TopY, p);
        canvas.drawBitmap(scaledBitmap, LeftX, LeftY, p);
        canvas.drawBitmap(scaledBitmap, RightX, RightY, p);
        canvas.drawBitmap(scaledBitmap, BottomX, BottomY, p);
        //canvas.drawBitmap(scaledBitmap, 400, 300, p);

        /* float xOffset = scaleX(face.getWidth() / 2.0f);
        float yOffset = scaleY(face.getHeight() / 2.0f);
        float left = x - xOffset;
        float top = y - yOffset;
        float right = x + xOffset;
        float bottom = y + yOffset;*/
        //drawFaceLandmarks(canvas, scale, face, p);
        //canvas.drawRect(left, top, right, bottom, mBoxPaint);
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


