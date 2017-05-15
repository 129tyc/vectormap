package com.tyc129.vectormap.struct;

/**
 * 点
 * 矢量地图上的最基础结构，用于标定位置，给路径提供端点，展示兴趣热点等
 * Created by Code on 2017/5/15 0015.
 *
 * @author 谈永成
 * @version 1.0
 */
abstract class Point {
    private String id;
    private float posX;
    private float posY;
    private float posZ;
    private Coordinate coordinate;

    Point(String id) {
        this(id, null);
    }

    public Point(String id, Coordinate coordinate) {
        this.id = id;
        this.coordinate = coordinate;
    }

    @Override
    public String toString() {
        return "Point{" +
                "id='" + id + '\'' +
                ", posX=" + posX +
                ", posY=" + posY +
                ", posZ=" + posZ +
                ", coordinate=" + coordinate.toString() +
                '}';
    }

    String getId() {
        return id;
    }

    void setId(String id) {
        this.id = id;
    }

    float getPosX() {
        return posX;
    }

    void setPosX(float posX) {
        this.posX = posX;
    }

    float getPosY() {
        return posY;
    }

    void setPosY(float posY) {
        this.posY = posY;
    }

    public float getPosZ() {
        return posZ;
    }

    public void setPosZ(float posZ) {
        this.posZ = posZ;
    }

    Coordinate getCoordinate() {
        return coordinate;
    }

    void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }
}
