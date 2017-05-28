package com.tyc129.vectormap.struct;

/**
 * 点
 * 矢量地图上的最基础结构，用于标定位置，给路径提供端点，展示兴趣热点等
 * Created by Code on 2017/5/15 0015.
 *
 * @author 谈永成
 * @version 1.0
 */
public abstract class Point {
    private String id;
    private float posX;
    private float posY;
    private float posZ;
    private float rootPosX;
    private float rootPosY;
    private float rootPosZ;
    private Coordinate coordinate;

    Point(String id) {
        this(id, null);
    }

    public Point(String id, Coordinate coordinate) {
        this.id = id;
        setCoordinate(coordinate);
    }

    @Override
    public String toString() {
        return "Point{" +
                "id='" + id + '\'' +
                ", posX=" + posX +
                ", posY=" + posY +
                ", posZ=" + posZ +
                ", rootPosX=" + rootPosX +
                ", rootPosY=" + rootPosY +
                ", rootPosZ=" + rootPosZ +
                ", coordinate=" + coordinate.toString() +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getRootPosX() {
        return rootPosX;
    }

    public float getRootPosY() {
        return rootPosY;
    }

    public float getRootPosZ() {
        return rootPosZ;
    }

    public float getPosX() {
        return posX;
    }

    private void calculateRootPos() {
        rootPosX = posX;
        rootPosY = posY;
        rootPosZ = posZ;
        if (coordinate != null) {
            rootPosX += coordinate.getOriX();
            rootPosY += coordinate.getOriY();
            rootPosZ += coordinate.getOriZ();
            Coordinate temp = coordinate;
            while (temp.getPostCoordinate() != null) {
                temp = temp.getPostCoordinate();
                rootPosX += temp.getOriX();
                rootPosY += temp.getOriY();
                rootPosZ += temp.getOriZ();
            }
        }
    }

    public void setPosX(float posX) {
        this.posX = posX;
        calculateRootPos();
    }

    public float getPosY() {
        return posY;
    }

    public void setPosY(float posY) {
        this.posY = posY;
        calculateRootPos();
    }

    public float getPosZ() {
        return posZ;
    }


    public void setPosZ(float posZ) {
        this.posZ = posZ;
        calculateRootPos();
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
        calculateRootPos();
    }
}
