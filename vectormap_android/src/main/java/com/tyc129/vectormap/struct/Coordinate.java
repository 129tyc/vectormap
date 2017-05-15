package com.tyc129.vectormap.struct;

/**
 * 矢量地图坐标系
 * 是地图数据的位置基准
 * Created by Code on 2017/5/15 0015.
 *
 * @author 谈永成
 * @version 1.0
 */
class Coordinate {
    private String id;
    private float oriX;
    private float oriY;
    private float oriZ;
    private float rotateDeg;
    private Coordinate rootCoordinate;

    Coordinate(String id) {
        this(id, null);
    }

    Coordinate(String id, Coordinate rootCoordinate) {
        this.id = id;
        this.rootCoordinate = rootCoordinate;
    }

    @Override
    public String toString() {
        return "Coordinate{" +
                "id='" + id + '\'' +
                ", oriX=" + oriX +
                ", oriY=" + oriY +
                ", oriZ=" + oriZ +
                ", rotateDeg=" + rotateDeg +
                ", rootCoordinate=" + rootCoordinate +
                '}';
    }

    String getId() {
        return id;
    }

    void setId(String id) {
        this.id = id;
    }

    float getOriX() {
        return oriX;
    }

    void setOriX(float oriX) {
        this.oriX = oriX;
    }

    float getOriY() {
        return oriY;
    }

    void setOriY(float oriY) {
        this.oriY = oriY;
    }

    float getOriZ() {
        return oriZ;
    }

    void setOriZ(float oriZ) {
        this.oriZ = oriZ;
    }

    float getRotateDeg() {
        return rotateDeg;
    }

    void setRotateDeg(float rotateDeg) {
        this.rotateDeg = rotateDeg;
    }

    public Coordinate getRootCoordinate() {
        return rootCoordinate;
    }

    public void setRootCoordinate(Coordinate rootCoordinate) {
        this.rootCoordinate = rootCoordinate;
    }
}
