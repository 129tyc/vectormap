package com.tyc129.vectormap.struct;

/**
 * 兴趣热点
 * 代表地图上的商户、建筑、重要位置等
 * Created by Code on 2017/5/15 0015.
 *
 * @author 谈永成
 * @version 1.0
 */
class Interest extends Point {
    Interest(String id) {
        super(id);
    }

    public Interest(String id, Coordinate coordinate) {
        super(id, coordinate);
    }

    enum InterestType {

    }

    enum ImportantLevel {

    }

    private InterestType type;
    private ImportantLevel level;
    private String nextLevelId;


}
