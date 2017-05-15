package com.tyc129.vectormap.struct;

/**
 * 路径
 * 矢量地图上连接点的有向线段
 * Created by Code on 2017/5/15 0015.
 *
 * @author 谈永成
 * @version 1.0
 */
public abstract class Path {
    private String id;
    private Point startPoint;
    private Point endPoint;
    private String pathData;
    private Coordinate coordinate;

    public Path(String id) {
        this(id, null);
    }

    public Path(String id, Coordinate coordinate) {
        this.id = id;
        this.coordinate = coordinate;
    }

    @Override
    public String toString() {
        return "Path{" +
                "id='" + id + '\'' +
                ", startPoint=" + startPoint.toString() +
                ", endPoint=" + endPoint.toString() +
                ", pathData='" + pathData + '\'' +
                ", coordinate=" + coordinate.toString() +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Point getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(Point startPoint) {
        this.startPoint = startPoint;
    }

    public Point getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(Point endPoint) {
        this.endPoint = endPoint;
    }

    public String getPathData() {
        return pathData;
    }

    public void setPathData(String pathData) {
        this.pathData = pathData;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }
}
