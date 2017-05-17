package com.tyc129.vectormap.struct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        interests = new ArrayList<>();
        tags = new HashMap<>();
    }

    public void destory() {
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
