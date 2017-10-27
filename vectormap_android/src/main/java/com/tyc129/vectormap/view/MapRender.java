package com.tyc129.vectormap.view;

import android.graphics.*;
import android.util.SparseArray;
import com.tyc129.vectormap.view.RenderUnit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 渲染器
 * 负责显示矢量地图的元素
 * Created by Code on 2017/5/15 0015.
 *
 * @author 谈永成
 * @version 1.0
 */
public class MapRender {
    private static final String LOG_TAG = "MapRender";

    private Matrix tempMatrix;
    private float cerX;
    private float cerY;
    private float[] mapPoint;

    private List<RenderUnit> currentUnits;
    private SparseArray<List<RenderUnit>> renderLayers;
    private List<RenderUnit> tempUnits;

    public MapRender() {
        mapPoint = new float[2];
        tempMatrix = new Matrix();
        tempUnits = new ArrayList<>();
    }

    public void setCurrentUnits(List<RenderUnit> currentUnits) {
        this.currentUnits = currentUnits;
    }

    public List<RenderUnit> getTempUnits() {
        return tempUnits;
    }

    /**
     * 绘制指南针类型的图像
     * 适用于具有独立的方向，需要进行变换才可以绘制的Bitmap
     *
     * @param canvas        绘制的画板
     * @param matrix        正在使用的矩阵
     * @param bitmap        需要绘制的位图
     * @param directionDeg  位图方向
     * @param controlDeg    原矩阵旋转角度
     * @param cerX          旋转中心点X轴位置
     * @param cerY          旋转中心点Y轴位置
     * @param bitmapHWidth  位图宽的一半
     * @param bitmapHHeight 位图长的一半
     */
    void renderBitmap2Canvas(Canvas canvas, Matrix matrix, Bitmap bitmap,
                             float directionDeg, float controlDeg, float cerX, float cerY, float scale,
                             float bitmapHWidth, float bitmapHHeight) {
        scale = 1 / scale;
        canvas.save();
        tempMatrix.set(matrix);
        mapPoint[0] = cerX;
        mapPoint[1] = cerY;
        matrix.mapPoints(mapPoint);
        tempMatrix.postRotate(directionDeg - controlDeg, mapPoint[0], mapPoint[1]);
        tempMatrix.postScale(scale, scale, mapPoint[0], mapPoint[1]);
        canvas.setMatrix(tempMatrix);
        canvas.drawBitmap(bitmap, cerX - bitmapHWidth,
                cerY - bitmapHHeight, null);
        canvas.restore();
    }

    private void render(List<RenderUnit> renderUnits, Canvas canvas, Matrix matrix, float rDeg, float scale,
                        boolean allowPaths, boolean allowPoints, boolean allowTags) {
        canvas.save();
        scale = 1 / scale;
        for (int i = 0; i < renderUnits.size(); i++) {
            RenderUnit e = renderUnits.get(i);
            switch (e.getRenderBody()) {
                case POINT: {
                    if (allowPoints) {
                        tempMatrix.set(matrix);
                        cerX = e.getPoint().x;
                        cerY = e.getPoint().y;
                        mapPoint[0] = cerX;
                        mapPoint[1] = cerY;
                        matrix.mapPoints(mapPoint);
                        tempMatrix.postRotate(-rDeg, mapPoint[0], mapPoint[1]);
                        tempMatrix.postScale(scale, scale, mapPoint[0], mapPoint[1]);
                        canvas.setMatrix(tempMatrix);
                        if (e.getBitmap() != null)
                            canvas.drawBitmap(e.getBitmap(),
                                    cerX - e.getHalfBitmapWidth(), cerY - e.getHalfBitmapHeight(), null);
                    }
                    break;
                }
                case TAG: {
                    if (allowTags) {
                        tempMatrix.set(matrix);
                        cerX = e.getPoint().x;
                        cerY = e.getPoint().y;
                        mapPoint[0] = cerX;
                        mapPoint[1] = cerY;
                        matrix.mapPoints(mapPoint);
                        tempMatrix.postRotate(-rDeg, mapPoint[0], mapPoint[1]);
                        tempMatrix.postScale(scale, scale, mapPoint[0], mapPoint[1]);
                        canvas.setMatrix(tempMatrix);
                        canvas.drawText(e.getTagText(),
                                cerX, cerY + e.getTextMargin(),
                                e.getPaint());
                    }
                    break;
                }
                case PATH: {
                    if (allowPaths) {
                        canvas.drawPath(e.getPath(), e.getPaint());
                    }
                    break;
                }
            }
        }
        canvas.restore();
    }

    void render2Canvas(Canvas canvas, Matrix matrix, float rDeg, float scale,
                       boolean allowPaths, boolean allowPoints, boolean allowTags) {
        if (canvas != null && currentUnits != null) {
            render(currentUnits, canvas, matrix, rDeg, scale, allowPaths, allowPoints, allowTags);
            if (!tempUnits.isEmpty()) {
                render(tempUnits, canvas, matrix, rDeg, scale, allowPaths, allowPoints, allowTags);
            }
        }
    }

    void renderAll2Canvas(Canvas canvas, Matrix matrix, float rDeg, float scale) {
        render2Canvas(canvas, matrix, rDeg, scale,
                true, true, true);
    }

    void render2CanvasWithoutTags(Canvas canvas, Matrix matrix, float rDeg, float scale) {
        render2Canvas(canvas, matrix, rDeg, scale,
                true, true, false);
    }
}
