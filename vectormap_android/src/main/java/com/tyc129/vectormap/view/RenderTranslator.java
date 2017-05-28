package com.tyc129.vectormap.view;


import android.graphics.Bitmap;
import android.graphics.Paint;
import com.tyc129.vectormap.Render;
import com.tyc129.vectormap.struct.MapSrc;
import com.tyc129.vectormap.struct.Point;
import com.tyc129.vectormap.view.RenderUnit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 渲染翻译器
 * 将原始地图数据与绘图资源处理为绘制单元
 * Created by Code on 2017/5/15 0015.
 *
 * @author 谈永成
 * @version 1.0
 */
public class RenderTranslator {
    private static final String LOG_TAG = "RenderTranslator";

    private List<DrawSrc> drawSrcs;
    private List<RenderUnit> units;

    private MapSrc mapSrc;
    private Map<String, String> tagsMap;
    private Map<String, Bitmap> bitmapMap;


    public void setSource(MapSrc mapSrc) {
        this.mapSrc = mapSrc;
    }

    public void setSource(Map<String, String> tagsMap) {
        this.tagsMap = tagsMap;
    }

    public void setDrawStyles(List<DrawSrc> drawSrcs) {
        this.drawSrcs = drawSrcs;
    }

    public List<RenderUnit> translate() {
        if (units == null) {
            units = new ArrayList<>();
        } else {
            units.clear();
        }
        for (DrawSrc e :
                drawSrcs) {
            Paint paint;
            Bitmap bitmap;
            RenderUnit.RenderType type;
            RenderUnit.RenderBody body;
            switch (e.getPaintType()) {
                case CANVAS: {
                    paint = e.getPaint();
                    type = RenderUnit.RenderType.PAINT;
                    break;
                }
                case BITMAP: {
                    String id = e.getBitmapId();
                    if (bitmapMap.containsKey(id)) {
                        bitmap = bitmapMap.get(id);
                    }
                    break;
                }
            }
            switch (e.getDrawType()) {
                case POINT: {
                    break;
                }
            }

        }
        return units;
    }
}
