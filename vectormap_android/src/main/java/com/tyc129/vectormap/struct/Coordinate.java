package com.tyc129.vectormap.struct;

/**
 * 矢量地图坐标系
 * 是地图数据的位置基准
 * Created by Code on 2017/5/15 0015.
 *
 * @author 谈永成
 * @version 1.0
 */
public class Coordinate {
    private String id;
    private float oriX;
    private float oriY;
    private float oriZ;
    private float rotateDeg;
    private Coordinate postCoordinate;

    public Coordinate(String id) {
        this(id, null);
    }

    public Coordinate(String id, Coordinate postCoordinate) {
        this.id = id;
        this.postCoordinate = postCoordinate;
    }

    @Override
    public String toString() {
        return "Coordinate{" +
                "id='" + id + '\'' +
                ", oriX=" + oriX +
                ", oriY=" + oriY +
                ", oriZ=" + oriZ +
                ", rotateDeg=" + rotateDeg +
                ", postCoordinate=" + postCoordinate +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getOriX() {
        return oriX;
    }

    public void setOriX(float oriX) {
        this.oriX = oriX;
    }

    public float getOriY() {
        return oriY;
    }

    public void setOriY(float oriY) {
        this.oriY = oriY;
    }

    public float getOriZ() {
        return oriZ;
    }

    public void setOriZ(float oriZ) {
        this.oriZ = oriZ;
    }

    public float getRotateDeg() {
        return rotateDeg;
    }

    public void setRotateDeg(float rotateDeg) {
        this.rotateDeg = rotateDeg;
    }

    public Coordinate getPostCoordinate() {
        return postCoordinate;
    }

    public void setPostCoordinate(Coordinate postCoordinate) {
        this.postCoordinate = postCoordinate;
    }
}
