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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Path path = (Path) o;

        return id.equals(path.id) &&
                (startPoint != null ? startPoint.equals(path.startPoint) : path.startPoint == null) &&
                (endPoint != null ? endPoint.equals(path.endPoint) : path.endPoint == null) &&
                (pathData != null ? pathData.equals(path.pathData) : path.pathData == null) &&
                (coordinate != null ? coordinate.equals(path.coordinate) : path.coordinate == null);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + (startPoint != null ? startPoint.hashCode() : 0);
        result = 31 * result + (endPoint != null ? endPoint.hashCode() : 0);
        result = 31 * result + (pathData != null ? pathData.hashCode() : 0);
        result = 31 * result + (coordinate != null ? coordinate.hashCode() : 0);
        return result;
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

    public android.graphics.Path transfer2Render() {
        if (this.startPoint != null &&
                this.endPoint != null &&
                this.pathData != null) {
            android.graphics.Path path = new android.graphics.Path();
            path.moveTo(this.startPoint.getRootPosX(), this.startPoint.getRootPosY());
            if (this.pathData.equals("l")) {
                path.lineTo(this.endPoint.getRootPosX(), this.endPoint.getRootPosY());
            }
            return path;
        } else {
            return null;
        }
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
