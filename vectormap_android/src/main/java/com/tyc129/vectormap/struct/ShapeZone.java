package com.tyc129.vectormap.struct;

/**
 * 形状区域
 * 从Zone继承，没有特殊实现
 * Created by Code on 2017/5/16 0016.
 *
 * @author 谈永成
 * @version 1.0
 */
public class ShapeZone extends Zone {
    public ShapeZone(String id) {
        super(id);
    }

    public ShapeZone(String id, Coordinate coordinate) {
        super(id, coordinate);
    }
}
