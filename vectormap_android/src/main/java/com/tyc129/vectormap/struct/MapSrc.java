package com.tyc129.vectormap.struct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tyc129.vectormap.utils.MathUtils.getDistance;

/**
 * 地图
 * 地图元素的集合，包含地图的大小、单位等属性
 * Created by Code on 2017/5/15 0015.
 *
 * @author 谈永成
 * @version 1.0
 */
public class MapSrc {
    public enum MetricUnit {
        PX,
        DP,
    }

    private String id;
    private float width;
    private float height;
    private MetricUnit unit;
    private Coordinate rootCoordinate;
    private List<Road> roads;
    private List<Interest> interests;
    private List<LinkPoint> points;
    private List<LinePath> paths;
    private List<ShapeZone> zones;
    private List<Coordinate> coordinates;
    private Map<String, String> tags;


    public MapSrc(String id) {
        this(id, null);
    }

    public MapSrc(String id, Coordinate rootCoordinate) {
        this.id = id;
        this.rootCoordinate = rootCoordinate;
        roads = new ArrayList<>();
        paths = new ArrayList<>();
        points = new ArrayList<>();
        interests = new ArrayList<>();
        tags = new HashMap<>();
    }

    public void destroy() {
        if (interests != null) {
            interests.clear();
            interests = null;
        }
        if (roads != null) {
            roads.clear();
            roads = null;
        }
        if (tags != null) {
            tags.clear();
            tags = null;
        }
        rootCoordinate = null;
        id = null;
        unit = null;
    }

    /**
     * 通过坐标位置找到重叠对应的点
     * 不支持路径
     *
     * @param x      X轴位置
     * @param y      Y轴位置
     * @param maxDis 判定为重叠的最大距离半径
     * @return 重叠点对应的id, 若有多个点则返回最近的点，若没有重叠返回null
     */
    public String findPointByPos(float x, float y, float maxDis) {
        String id = findPointByPos(x, y, maxDis, interests);
        if (id == null) {
            id = findPointByPos(x, y, maxDis, points);
        }
        return id;
    }

    /**
     * 通过坐标位置找到重叠对应的点
     * 不支持路径
     *
     * @param x      X轴位置
     * @param y      Y轴位置
     * @param maxDis 判定为重叠的最大距离半径
     * @param points 需要查找的点集
     * @return 重叠点对应的id, 若有多个点则返回最近的点，若没有重叠返回null
     */
    String findPointByPos(float x, float y, float maxDis,
                          List<? extends Point> points) {
        double dis;
        float ex;
        float ey;
        double min = maxDis;
        for (Point e :
                points) {
            ex = e.getRootPosX();
            ey = e.getPosX();
            dis = getDistance(ex, ey, x, y);
            if (dis < min) {
                id = e.getId();
                min = dis;
            }
        }
        return id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public MetricUnit getUnit() {
        return unit;
    }

    public void setUnit(MetricUnit unit) {
        this.unit = unit;
    }

    public Coordinate getRootCoordinate() {
        return rootCoordinate;
    }

    public void setRootCoordinate(Coordinate rootCoordinate) {
        this.rootCoordinate = rootCoordinate;
    }

    public List<Road> getRoads() {
        return roads;
    }

    public void setRoads(List<Road> roads) {
        this.roads = roads;
    }

    public List<Interest> getInterests() {
        return interests;
    }

    public void setInterests(List<Interest> interests) {
        this.interests = interests;
    }

    public List<LinkPoint> getPoints() {
        return points;
    }

    public void setPoints(List<LinkPoint> points) {
        this.points = points;
    }

    public List<LinePath> getPaths() {
        return paths;
    }

    public void setPaths(List<LinePath> paths) {
        this.paths = paths;
    }

    public List<ShapeZone> getZones() {
        return zones;
    }

    public void setZones(List<ShapeZone> zones) {
        this.zones = zones;
    }

    public List<Coordinate> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<Coordinate> coordinates) {
        this.coordinates = coordinates;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }
}
