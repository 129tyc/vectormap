package com.tyc129.vectormap.view;

import android.graphics.*;
import android.graphics.Path;
import android.graphics.Point;
import com.tyc129.vectormap.struct.*;

/**
 * 绘制单元
 * 是渲染器直接接触到最小渲染单元，负责存储最直接的绘制数据
 * Created by Code on 2017/5/27 0027.
 *
 * @author 谈永成
 * @version 1.0
 */
public class RenderUnit {
    public enum RenderType {
        BITMAP,
        PAINT
    }

    public enum RenderBody {
        PATH,
        POINT,
        TAG
    }

    private RenderBody renderBody;
    private RenderType renderType;
    private Bitmap bitmap;
    private String bitmapId;
    private Paint paint;
    private Path path;
    private PointF point;
    private float floor;
    private float hBitmapWidth;
    private float hBitmapHeight;
    private float textMargin;
    private String tagText;


    public RenderBody getRenderBody() {
        return renderBody;
    }

    public void setRenderBody(RenderBody renderBody) {
        this.renderBody = renderBody;
    }

    public RenderType getRenderType() {
        return renderType;
    }

    public void setRenderType(RenderType renderType) {
        this.renderType = renderType;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        if (this.bitmap != null) {
            hBitmapWidth = this.bitmap.getWidth() >> 1;
            hBitmapHeight = this.bitmap.getHeight() >> 1;
        }
    }

    public String getBitmapId() {
        return bitmapId;
    }

    public void setBitmapId(String bitmapId) {
        this.bitmapId = bitmapId;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public PointF getPoint() {
        return point;
    }

    public void setPoint(PointF point) {
        this.point = point;
    }

    public float getFloor() {
        return floor;
    }

    public void setFloor(float floor) {
        this.floor = floor;
    }

    public float getHalfBitmapWidth() {
        return hBitmapWidth;
    }

    public float getHalfBitmapHeight() {
        return hBitmapHeight;
    }

    public float getTextMargin() {
        return textMargin;
    }

    public void setTextMargin(float textMargin) {
        this.textMargin = textMargin;
    }

    public String getTagText() {
        return tagText;
    }

    public void setTagText(String tagText) {
        this.tagText = tagText;
    }
}
