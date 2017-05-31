package com.tyc129.vectormap.utils;

import com.tyc129.vectormap.struct.Point;

/**
 * 地图处理用到的数学函数
 * Created by Code on 2017/5/30 0030.
 *
 * @author 谈永成
 * @version 1.0
 */
public class MathUtils {

    public static double getDistance(Point p1, Point p2) {
        return getDistance(p1.getRootPosX(), p1.getRootPosY(), p2.getRootPosX(), p2.getRootPosY());
    }

    public static double getDistance(float x1, float y1, float x2, float y2) {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    public static double getDecimal(double num) {
        if (num > 1 || num < -1) {
            num = 1 / num;
        }
        return num;
    }
}
