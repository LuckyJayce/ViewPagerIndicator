/*
 * Copyright 2015 chenupt
 * Copyright 2015 LuckyJayce
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.shizhefei.view.indicator.slidebar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

public class SpringBar extends View implements ScrollBar {
    private int tabWidth;
    private Paint paint;
    private Path path;
    private Point foot;
    private Point head;
    private float radiusMax;
    private float radiusMin;
    private float radiusOffset;
    private float acceleration = 0.5f;
    private float headMoveOffset = 0.6f;
    private float footMoveOffset = 1 - headMoveOffset;
    private float maxRadiusPercent;
    private float minRadiusPercent;

    public SpringBar(Context context, int springColor) {
        this(context, springColor, 0.9f, 0.35f);
    }

    public SpringBar(Context context, int springColor, float maxRadiusPercent, float minRadiusPercent) {
        super(context);
        this.maxRadiusPercent = maxRadiusPercent;
        this.minRadiusPercent = minRadiusPercent;
        foot = new Point();
        head = new Point();
        path = new Path();
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(1);
        paint.setColor(springColor);
    }

    @Override
    public int getHeight(int tabHeight) {
        int half = tabHeight / 2;
        foot.setY(half);
        head.setY(half);
        radiusMax = half * maxRadiusPercent;
        radiusMin = half * minRadiusPercent;
        radiusOffset = radiusMax - radiusMin;
        return tabHeight;
    }

    @Override
    public int getWidth(int tabWidth) {
        this.tabWidth = tabWidth;
        if (positionOffset < 0.02f || positionOffset > 0.98f) {
            onPageScrolled(0, 0, 0);
        }
        return 2 * tabWidth;
    }

    @Override
    public View getSlideView() {
        return this;
    }

    @Override
    public Gravity getGravity() {
        return Gravity.CENTENT_BACKGROUND;
    }

    private float positionOffset;

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        this.positionOffset = positionOffset;
        if (positionOffset < 0.02f || positionOffset > 0.98f) {
            head.setX(getOffsetX(0));
            foot.setX(getOffsetX(0));
            head.setRadius(radiusMax);
            foot.setRadius(radiusMax);
        } else {
            float radiusOffsetHead = 0.5f;
            if (positionOffset < radiusOffsetHead) {
                head.setRadius(radiusMin);
            } else {
                head.setRadius(((positionOffset - radiusOffsetHead) / (1 - radiusOffsetHead) * radiusOffset + radiusMin));
            }
            float radiusOffsetFoot = 0.5f;
            if (positionOffset < radiusOffsetFoot) {
                foot.setRadius((1 - positionOffset / radiusOffsetFoot) * radiusOffset + radiusMin);
            } else {
                foot.setRadius(radiusMin);
            }
            float headX = 0f;
            if (positionOffset > headMoveOffset) {
                float positionOffsetTemp = (positionOffset - headMoveOffset) / (1 - headMoveOffset);
                headX = (float) ((Math.atan(positionOffsetTemp * acceleration * 2 - acceleration) + (Math.atan(acceleration))) / (2 * (Math
                        .atan(acceleration))));
            }
            // x
            head.setX(getOffsetX(positionOffset) - headX * getPositionDistance(position));
            float footX = 1f;
            if (positionOffset < footMoveOffset) {
                float positionOffsetTemp = positionOffset / footMoveOffset;
                footX = (float) ((Math.atan(positionOffsetTemp * acceleration * 2 - acceleration) + (Math.atan(acceleration))) / (2 * (Math
                        .atan(acceleration))));
            }
            foot.setX(getOffsetX(positionOffset) - footX * getPositionDistance(position));
        }
    }

    private float getOffsetX(float positionOffset) {
        return 2 * tabWidth - tabWidth / 4 - tabWidth * (1 - positionOffset) + tabWidth / 4.0f;
    }

    private float getPositionDistance(int position) {
        return tabWidth;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        makePath();
        canvas.drawColor(Color.TRANSPARENT);
        // canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.OVERLAY);
        canvas.drawPath(path, paint);
        canvas.drawCircle(head.getX(), head.getY(), head.getRadius(), paint);
        canvas.drawCircle(foot.getX(), foot.getY(), foot.getRadius(), paint);
        super.onDraw(canvas);
    }

    private void makePath() {
        float headOffsetX = (float) (foot.getRadius() * Math.sin(Math.atan((head.getY() - foot.getY()) / (head.getX() - foot.getX()))));
        float headOffsetY = (float) (foot.getRadius() * Math.cos(Math.atan((head.getY() - foot.getY()) / (head.getX() - foot.getX()))));

        float footOffsetX = (float) (head.getRadius() * Math.sin(Math.atan((head.getY() - foot.getY()) / (head.getX() - foot.getX()))));
        float footOffsetY = (float) (head.getRadius() * Math.cos(Math.atan((head.getY() - foot.getY()) / (head.getX() - foot.getX()))));

        float x1 = foot.getX() - headOffsetX;
        float y1 = foot.getY() + headOffsetY;

        float x2 = foot.getX() + headOffsetX;
        float y2 = foot.getY() - headOffsetY;

        float x3 = head.getX() - footOffsetX;
        float y3 = head.getY() + footOffsetY;

        float x4 = head.getX() + footOffsetX;
        float y4 = head.getY() - footOffsetY;

        float anchorX = (head.getX() + foot.getX()) / 2;
        float anchorY = (head.getY() + foot.getY()) / 2;

        path.reset();
        path.moveTo(x1, y1);
        path.quadTo(anchorX, anchorY, x3, y3);
        path.lineTo(x4, y4);
        path.quadTo(anchorX, anchorY, x2, y2);
        path.lineTo(x1, y1);
    }

    private class Point {

        private float x;
        private float y;
        private float radius;

        public float getX() {
            return x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }

        public float getRadius() {
            return radius;
        }

        public void setRadius(float radius) {
            this.radius = radius;
        }

    }
}
