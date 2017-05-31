package com.tyc129.vectormap.view;

import android.graphics.Bitmap;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Code on 2017/5/26 0026.
 *
 * @author 谈永成
 * @version 1.0
 */
public class DrawSrc {
    public enum DrawType {
        POINT,
        PATH,
        TAG
    }

    public enum PaintType {
        BITMAP,
        CANVAS
    }

    private DrawType drawType;
    private PaintType paintType;
    private String specifyType;
    private String specifyData;
    private Paint paint;
    private String bitmapId;
    private float scale;
    private float textMargin;

    public DrawSrc() {
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public float getTextMargin() {
        return textMargin;
    }

    public void setTextMargin(float textMargin) {
        this.textMargin = textMargin;
    }

    public DrawType getDrawType() {
        return drawType;
    }

    public void setDrawType(DrawType drawType) {
        this.drawType = drawType;
    }

    public PaintType getPaintType() {
        return paintType;
    }

    public void setPaintType(PaintType paintType) {
        this.paintType = paintType;
    }

    public String getSpecifyType() {
        return specifyType;
    }

    public void setSpecifyType(String specifyType) {
        this.specifyType = specifyType;
    }

    public String getSpecifyData() {
        return specifyData;
    }

    public void setSpecifyData(String specifyData) {
        this.specifyData = specifyData;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public String getBitmapId() {
        return bitmapId;
    }

    public void setBitmapId(String bitmapId) {
        this.bitmapId = bitmapId;
    }
}
