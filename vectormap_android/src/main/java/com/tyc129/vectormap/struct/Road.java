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
    enum RoadAttribute {

    }

    enum RoadLevel {

    }

    private List<RoadAttribute> attributes;
    private RoadLevel roadLevel;

    public Road(String id) {
        super(id);
    }

    public Road(String id, Coordinate coordinate) {
        super(id, coordinate);
    }
}
