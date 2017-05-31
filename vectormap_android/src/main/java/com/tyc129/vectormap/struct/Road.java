package com.tyc129.vectormap.struct;

import java.util.ArrayList;
import java.util.List;

/**
 * 道路类
 * 定义了矢量地图的道路属性
 * Created by Code on 2017/5/15 0015.
 *
 * @author 谈永成
 * @version 1.0
 */
public class Road extends Path {
    /**
     * 道路属性
     * 可多选，对道路的补充描述
     */
    public enum RoadAttribute {
        ONEWAY,
        TIME_LIMIT,
        DANGER
    }

    /**
     * 通过等级
     * 表示该道路允许通过的对象分类
     */
    public enum PassLevel {
        NORMAL,
        VECHILE,
        NON_MOTOR,
        PEOPLE,
        PROHIBIT
    }

    /**
     * 可通行情况
     * 道路是否拥堵或通畅
     */
    public enum TrafficCondition {
        BROKEN,
        CONGESTION,
        NORMAL,
        FREE
    }

    /**
     * 道路等级
     * 影响到缩放时的可见性
     */
    public enum RoadLevel {
        USERSET,
        FAST,
        MAIN,
        SECOND,
        BRANCH,
        LANE,
        TRAIL
    }

    private List<RoadAttribute> attributes;
    private RoadLevel roadLevel;
    private TrafficCondition condition;
    private PassLevel passLevel;

    public Road(String id) {
        super(id);
    }

    public Road(String id, Coordinate coordinate) {
        super(id, coordinate);
        attributes = new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Road road = (Road) o;

        return (attributes != null ? attributes.equals(road.attributes) : road.attributes == null) &&
                roadLevel == road.roadLevel &&
                condition == road.condition &&
                passLevel == road.passLevel;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (attributes != null ? attributes.hashCode() : 0);
        result = 31 * result + (roadLevel != null ? roadLevel.hashCode() : 0);
        result = 31 * result + (condition != null ? condition.hashCode() : 0);
        result = 31 * result + (passLevel != null ? passLevel.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Road{" +
                "attributes=" + attributes +
                ", roadLevel=" + roadLevel +
                ", condition=" + condition +
                ", passLevel=" + passLevel +
                '}';
    }

    public List<RoadAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<RoadAttribute> attributes) {
        if (attributes != null) {
            this.attributes.addAll(attributes);
        }
    }

    public RoadLevel getRoadLevel() {
        return roadLevel;
    }

    public void setRoadLevel(RoadLevel roadLevel) {
        this.roadLevel = roadLevel;
    }

    public TrafficCondition getCondition() {
        return condition;
    }

    public void setCondition(TrafficCondition condition) {
        this.condition = condition;
    }

    public PassLevel getPassLevel() {
        return passLevel;
    }

    public void setPassLevel(PassLevel passLevel) {
        this.passLevel = passLevel;
    }
}
