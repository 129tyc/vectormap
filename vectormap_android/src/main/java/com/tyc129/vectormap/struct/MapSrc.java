package com.tyc129.vectormap.struct;

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
    enum MetricUnit {
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
    private Map<String, String> tags;

    public MapSrc(String id) {
        this(id, null);
    }

    public MapSrc(String id, Coordinate rootCoordinate) {
        this.id = id;
        this.rootCoordinate = rootCoordinate;
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

    public Map<String, String> getTags() {
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }
}
