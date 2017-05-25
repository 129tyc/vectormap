package com.tyc129.vectormap.struct;

/**
 * 连接点
 * 从Point继承，没有特殊实现
 * Created by Code on 2017/5/16 0016.
 *
 * @author 谈永成
 * @version 1.0
 */
public class LinkPoint extends Point {
    LinkPoint(String id) {
        super(id);
    }

    public LinkPoint(String id, Coordinate coordinate) {
        super(id, coordinate);
    }

    public LinkPoint(String id, Coordinate coordinate,
                     float posX, float posY, float posZ) {
        super(id, coordinate);
        this.setPosX(posX);
        this.setPosY(posY);
        this.setPosZ(posZ);
    }
}
