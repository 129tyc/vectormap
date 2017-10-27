package com.tyc129.vectormap.view;

import android.view.GestureDetector;
import android.view.MotionEvent;

import static com.tyc129.vectormap.utils.MathUtils.getDecimal;
import static com.tyc129.vectormap.utils.MathUtils.getDistance;

/**
 * 手指动作解析
 * 负责判断手指在屏幕上的动作并对地图进行相应的变换
 * Created by Code on 2017/5/15 0015.
 *
 * @author 谈永成
 * @version 1.0
 */
public class GestureParser extends GestureDetector.SimpleOnGestureListener {
    private static final double RAD2DEG = 180 / Math.PI;
    private static final double CRITICAL_VALUE_SCALE = 0.03;
    private static final double CRITICAL_VALUE_ROTATE = 3;

    private VectorMapView mapView;
    private MotionEvent.PointerCoords coord1;
    private MotionEvent.PointerCoords coord2;
    private VectorMapView.OnMapActionOccurListener listener;
    private double lastScaleMinus = -1;
    private double scaleMinus;
    private double lastRotateDeg = -1;
    private boolean allowRotating = false;
    private boolean isRotating = false;
    private boolean isRotateFirst = true;
    private double rotateDeg;
    private float centreX;
    private float centreY;
    private float tmpX;
    private float tmpY;

    public void setListener(VectorMapView.OnMapActionOccurListener listener) {
        this.listener = listener;
    }

    GestureParser(VectorMapView mapView) {
        if (mapView != null) {
            this.mapView = mapView;
        } else
            throw new NullPointerException("VectorMapView is Null!");
        coord1 = new MotionEvent.PointerCoords();
        coord2 = new MotionEvent.PointerCoords();
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        int fingerCount = e2.getPointerCount();
        if (fingerCount == 1) {
//            单指移动
            mapView.mapTranslate(-distanceX, -distanceY);
        } else if (fingerCount == 2) {
//            得到双指的坐标
            e2.getPointerCoords(0, coord1);
            e2.getPointerCoords(1, coord2);
            centreX = (coord1.x + coord2.x) / 2;
            centreY = (coord1.y + coord2.y) / 2;
//            双指缩放
            scaleMinus = getDistance(coord1.x, coord1.y, coord2.x, coord2.y);
            if (lastScaleMinus > 0) {
                if (!isRotating &&
                        allowRotating &&
                        Math.abs(getDecimal(scaleMinus / lastScaleMinus) - 1) > CRITICAL_VALUE_SCALE) {
                    allowRotating = false;
                }
                mapView.mapScale((float) (scaleMinus / lastScaleMinus), centreX, centreY);
            }
            lastScaleMinus = scaleMinus;
//            双指旋转
            tmpX = coord1.x - coord2.x;
            tmpY = coord1.y - coord2.y;
            rotateDeg = -Math.atan2(tmpX, tmpY);
            rotateDeg *= RAD2DEG;
            if (isRotateFirst) {
                lastRotateDeg = rotateDeg;
                isRotateFirst = false;
            } else {
                if (Math.abs(lastRotateDeg - rotateDeg) > CRITICAL_VALUE_ROTATE) {
                    isRotating = true;
                }
            }
            if (isRotating && allowRotating) {
                mapView.mapRotate((float) (rotateDeg - lastRotateDeg), centreX, centreY);
                lastRotateDeg = rotateDeg;
            }
        }
        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        isRotateFirst = true;
        allowRotating = true;
        isRotating = false;
        lastScaleMinus = -1;
        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        if (mapView.isAllowClick()) {
            if (listener != null)
                listener.onClickMap(e.getX(), e.getY());
            mapView.getIdByPos(e.getX(), e.getY());
        }
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        centreX = e.getX();
        centreY = e.getY();
        mapView.mapScaleAnimation(mapView.getScaleOneTap(), centreX, centreY, 200);
        return true;
    }
}
