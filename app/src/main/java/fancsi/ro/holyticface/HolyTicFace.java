/*
 * Copyright (C) 2014 The Android Open Source Project
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

package fancsi.ro.holyticface;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.view.SurfaceHolder;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Analog watch face with a ticking second hand. In ambient mode, the second hand isn't
 * shown. On devices with low-bit ambient mode, the hands are drawn without anti-aliasing in ambient
 * mode. The watch face is drawn with less contrast in mute mode.
 */
public class HolyTicFace extends CanvasWatchFaceService {

    @Override
    public Engine onCreateEngine() {
        return new Engine();
    }


    private class Engine extends CanvasWatchFaceService.Engine {
        private final String[] HOURS = new String[]{
                "Twelve",
                "One",
                "Two",
                "Three",
                "Four",
                "Five",
                "Six",
                "Seven",
                "Eight",
                "Nine",
                "Ten",
                "Eleven",
        };

        private final String[] MINUTES = new String[]{
                "O'Clock",
                "One",
                "Two",
                "Three",
                "Four",
                "Five",
                "Six",
                "Seven",
                "Eight",
                "Nine",
                "Ten",

                "Eleven",
                "Twelve",
                "Thirteen",
                "Fourteen",
                "Fifteen",
                "Sixteen",
                "Seventeen",
                "Eighteen",
                "Nineteen",
                "Twenty",

                "Twenty One",
                "Twenty Two",
                "Twenty Three",
                "Twenty Four",
                "Twenty Five",
                "Twenty Six",
                "Twenty Seven",
                "Twenty Eight",
                "Twenty Nine",
                "Thirty",

                "Thirty One",
                "Thirty Two",
                "Thirty Three",
                "Thirty Four",
                "Thirty Five",
                "Thirty Six",
                "Thirty Seven",
                "Thirty Eight",
                "Thirty Nine",
                "Forty",

                "Forty One",
                "Forty Two",
                "Forty Three",
                "Forty Four",
                "Forty Five",
                "Forty Six",
                "Forty Seven",
                "Forty Eight",
                "Forty Nine",
                "Fifty",

                "Fifty One",
                "Fifty Two",
                "Fifty Three",
                "Fifty Four",
                "Fifty Five",
                "Fifty Six",
                "Fifty Seven",
                "Fifty Eight",
                "Fifty Nine",
        };


        private Calendar mCalendar;
        private final BroadcastReceiver mTimeZoneReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mCalendar.setTimeZone(TimeZone.getDefault());
                invalidate();
            }
        };
        private boolean mRegisteredTimeZoneReceiver = false;
        private boolean mMuteMode;
        private float mCenterX;
        private float mCenterY;

        private Paint mBackgroundPaint;
        private Bitmap mBackgroundBitmap;
        private boolean mAmbient;
        private boolean mLowBitAmbient;
        private boolean mBurnInProtection;

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            setWatchFaceStyle(new WatchFaceStyle.Builder(HolyTicFace.this)
                    .setCardPeekMode(WatchFaceStyle.PEEK_MODE_SHORT)
                    .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
                    .setShowSystemUiTime(false)
                    .build());

            mBackgroundPaint = new Paint();
            mBackgroundPaint.setColor(Color.BLACK);
//            mBackgroundBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg);


            mCalendar = Calendar.getInstance();
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
        }

        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);
            mLowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);
            mBurnInProtection = properties.getBoolean(PROPERTY_BURN_IN_PROTECTION, false);
        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
            invalidate();
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            mAmbient = inAmbientMode;

            invalidate();
        }

        @Override
        public void onInterruptionFilterChanged(int interruptionFilter) {
            super.onInterruptionFilterChanged(interruptionFilter);
            boolean inMuteMode = (interruptionFilter == WatchFaceService.INTERRUPTION_FILTER_NONE);

            /* Dim display in mute mode. */
            if (mMuteMode != inMuteMode) {
                mMuteMode = inMuteMode;
                invalidate();
            }
        }

        private Rect textBounds = new Rect();

        private void drawTextCentred(Canvas canvas, Paint paint, String text, float cx, float cy) {
            paint.getTextBounds(text, 0, text.length(), textBounds);
            canvas.drawText(text, cx - textBounds.exactCenterX(), cy - textBounds.exactCenterY(), paint);
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);

            /*
             * Find the coordinates of the center point on the screen, and ignore the window
             * insets, so that, on round watches with a "chin", the watch face is centered on the
             * entire screen, not just the usable portion.
             */
            mCenterX = width / 2f;
            mCenterY = height / 2f;


            /* Scale loaded background image (more efficient) if surface dimensions change. */
//            float scale = ((float) width) / (float) mBackgroundBitmap.getWidth();
//
//            mBackgroundBitmap = Bitmap.createScaledBitmap(mBackgroundBitmap,
//                    (int) (mBackgroundBitmap.getWidth() * scale),
//                    (int) (mBackgroundBitmap.getHeight() * scale), true);

        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            long now = System.currentTimeMillis();
            mCalendar.setTimeInMillis(now);
            Paint hoursPaint = new Paint();
            hoursPaint.setStyle(Paint.Style.FILL);
            hoursPaint.setTextSize(50f);
            Typeface november = Typeface.createFromAsset(getAssets(), "november-regular.ttf");
            hoursPaint.setTypeface(november);

            Paint minutesPaint = new Paint(hoursPaint);


            Paint defaultTextPaint = new Paint(hoursPaint);
            defaultTextPaint.setColor(Color.WHITE);
            defaultTextPaint.setTextSize(40f);


            if (mAmbient && (mLowBitAmbient || mBurnInProtection)) {
                canvas.drawColor(Color.BLACK);
                hoursPaint.setColor(Color.WHITE);
                minutesPaint.setColor(Color.WHITE);
            } else if (mAmbient) {
                canvas.drawColor(Color.BLACK);
                hoursPaint.setColor(Color.WHITE);
                minutesPaint.setColor(Color.WHITE);
            } else {
                canvas.drawColor(Color.BLACK);
//                canvas.drawBitmap(mBackgroundBitmap, 0, 0, mBackgroundPaint);
                hoursPaint.setColor(Color.parseColor("#50D5D3"));
                minutesPaint.setColor(Color.parseColor("#5D84E1"));
            }


            final int minutes = mCalendar.get(Calendar.MINUTE);
            final int hours = mCalendar.get(Calendar.HOUR);


            drawTextCentred(canvas, defaultTextPaint, "HOLY SHIT", mCenterX, 20+65);
            drawTextCentred(canvas, defaultTextPaint, "IT'S ALREADY", mCenterX, 20+105);
            drawTextCentred(canvas, hoursPaint, HOURS[hours].toUpperCase(), mCenterX, 20+mCenterY - 45);
            drawTextCentred(canvas, defaultTextPaint, "FUCKING", mCenterX, 20+mCenterY);
            drawTextCentred(canvas, minutesPaint, MINUTES[minutes].toUpperCase(), mCenterX, 20+mCenterY + 45);
            drawTextCentred(canvas, defaultTextPaint, "MOTHERFUCKER", mCenterX, 20+290);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);

            if (visible) {
                registerReceiver();
                /* Update time zone in case it changed while we weren't visible. */
                mCalendar.setTimeZone(TimeZone.getDefault());
                invalidate();
            } else {
                unregisterReceiver();
            }
        }

        private void registerReceiver() {
            if (mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = true;
            IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
            HolyTicFace.this.registerReceiver(mTimeZoneReceiver, filter);
        }

        private void unregisterReceiver() {
            if (!mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = false;
            HolyTicFace.this.unregisterReceiver(mTimeZoneReceiver);
        }

    }
}
