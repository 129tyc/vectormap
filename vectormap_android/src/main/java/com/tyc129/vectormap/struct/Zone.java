package com.tyc129.vectormap.struct;

import java.util.LinkedList;
import java.util.List;

/**
 * 矢量区域
 * 地图上装饰性<b>非功能</b>的区域定义
 * Created by Code on 2017/5/15 0015.
 *
 * @author 谈永成
 * @version 1.0
 */
public abstract class Zone {
    enum ZoneAttribute {

    }

    private String id;
    private List<Path> borders;
    private List<ZoneAttribute> attributes;
    private Coordinate coordinate;

    public Zone(String id) {
        this(id, null);
    }

    public Zone(String id, Coordinate coordinate) {
        this.id = id;
        this.coordinate = coordinate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Path> getBorders() {
        return borders;
    }

    public void setBorders(List<Path> borders) {
        if (this.borders != null) {
            this.borders.clear();
        } else {
            this.borders = new LinkedList<>();
        }
        this.borders.addAll(borders);
    }

    public List<ZoneAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<ZoneAttribute> attributes) {
        if (this.attributes != null) {
            this.attributes.clear();
        } else {
            this.attributes = new LinkedList<>();
        }
        this.attributes.addAll(attributes);
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }
}
