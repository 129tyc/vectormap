package com.tyc129.vectormap.struct;

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
    enum RoadAttribute {
        ONEWAY,
        TIME_LIMIT,
        DANGER
    }

    /**
     * 通过等级
     * 表示该道路允许通过的对象分类
     */
    enum PassLevel {
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
    enum TrafficCondition {
        BROKEN,
        CONGESTION,
        NORMAL,
        FREE
    }

    /**
     * 道路等级
     * 影响到缩放时的可见性
     */
    enum RoadLevel {
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
        this.attributes = attributes;
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
