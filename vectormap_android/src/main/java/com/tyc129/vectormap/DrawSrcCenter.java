package com.tyc129.vectormap;

import android.graphics.Bitmap;
import android.graphics.Paint;

import java.util.Map;

/**
 * 绘制资源
 * 需要使用画刷进行绘制的地图元素统一从读取画刷信息
 * 兴趣点和其他标志需要的图标Icon资源页集中在此存放
 * Created by Code on 2017/5/15 0015.
 *
 * @author 谈永成
 * @version 1.0
 */
public class DrawSrcCenter {
    private Map<String, Paint> paintTable;
    private Map<String, Bitmap> iconTable;
}
