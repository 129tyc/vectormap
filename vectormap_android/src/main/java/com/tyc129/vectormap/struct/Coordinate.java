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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Coordinate that = (Coordinate) o;

        if (Float.compare(that.oriX, oriX) != 0) return false;
        if (Float.compare(that.oriY, oriY) != 0) return false;
        if (Float.compare(that.oriZ, oriZ) != 0) return false;
        if (Float.compare(that.rotateDeg, rotateDeg) != 0) return false;
        if (!id.equals(that.id)) return false;
        return postCoordinate != null ? postCoordinate.equals(that.postCoordinate) : that.postCoordinate == null;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + (oriX != +0.0f ? Float.floatToIntBits(oriX) : 0);
        result = 31 * result + (oriY != +0.0f ? Float.floatToIntBits(oriY) : 0);
        result = 31 * result + (oriZ != +0.0f ? Float.floatToIntBits(oriZ) : 0);
        result = 31 * result + (rotateDeg != +0.0f ? Float.floatToIntBits(rotateDeg) : 0);
        result = 31 * result + (postCoordinate != null ? postCoordinate.hashCode() : 0);
        return result;
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
