package com.tyc129.vectormap.struct;

/**
 * 线条路径
 * 从Path继承，没有特殊实现
 * Created by Code on 2017/5/16 0016.
 *
 * @author 谈永成
 * @version 1.0
 */
public class LinePath extends Path {
    public LinePath(String id) {
        super(id);
    }

    public LinePath(String id, Coordinate coordinate) {
        super(id, coordinate);
    }
}
