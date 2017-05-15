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
    private Map<String,String> tags;
}
