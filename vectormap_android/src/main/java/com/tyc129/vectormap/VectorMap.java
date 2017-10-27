package com.tyc129.vectormap;


import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.ArrayMap;
import com.tyc129.vectormap.struct.Coordinate;
import com.tyc129.vectormap.struct.Interest;
import com.tyc129.vectormap.struct.MapSrc;
import com.tyc129.vectormap.struct.Point;
import com.tyc129.vectormap.view.DrawSrc;
import com.tyc129.vectormap.view.RenderUnit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 矢量地图主类
 * Created by Code on 2017/5/15 0015.
 *
 * @author 谈永成
 * @version 1.0
 */
public class VectorMap {

    private List<MapSrc> mapSrcs;
    private List<DrawSrc> drawSrcList;
    private Map<String, List<RenderUnit>> mapRenderUnits;
    private List<Coordinate> coordinates;
    private Map<String, String> tagsMap;
    private Map<String, Bitmap> bitmapMap;
    private MapSrc mainMapSrc;

    VectorMap(List<MapSrc> mapSrcs, List<DrawSrc> drawSrcList,
              Map<String, List<RenderUnit>> mapRenderUnits,
              List<Coordinate> coordinates,
              Map<String, String> tagsMap,
              Map<String, Bitmap> bitmapMap) {
        this.mapSrcs = mapSrcs;
        this.drawSrcList = drawSrcList;
        this.mapRenderUnits = mapRenderUnits;
        this.coordinates = coordinates;
        this.tagsMap = tagsMap;
        this.bitmapMap = bitmapMap;

        for (MapSrc e :
                mapSrcs) {
            if (e.getId().contains("main")) {
                mainMapSrc = e;
                break;
            }
        }
    }

    public void clearBitmaps() {
        for (Map.Entry<String, Bitmap> e :
                bitmapMap.entrySet()) {
            if (e.getValue() != null) {
                e.getValue().recycle();
            }
        }
    }

    public Map<String, String> getTags() {
        return tagsMap;
    }

    public void destroy() {
        if (this.mapSrcs != null) {
            for (MapSrc e :
                    mapSrcs) {
                e.destroy();
            }
            this.mapSrcs.clear();
        }
        if (this.coordinates != null) {
            this.coordinates.clear();
        }
        clearBitmaps();
        if (this.drawSrcList != null) {
            drawSrcList.clear();
        }
        if (tagsMap != null) {
            tagsMap.clear();
        }
        if (mapRenderUnits != null) {
            for (Map.Entry<String, List<RenderUnit>> e :
                    mapRenderUnits.entrySet()) {
                e.getValue().clear();
            }
        }

    }

    public boolean acquirePositionForMain(@NonNull String pointId,@NonNull float[] pos) {
        return acquirePosition(pointId, pos, mainMapSrc);
    }

    public boolean acquirePosition(@NonNull String pointId, @NonNull float[] pos, @NonNull MapSrc mapSrc) {
        if (pos.length >= 3) {
            List<Point> points = new ArrayList<>();
            points.addAll(mapSrc.getInterests());
            points.addAll(mapSrc.getPoints());
            for (Point e :
                    points) {
                if (e.getId().equals(pointId)) {
                    pos[0] = e.getRootPosX();
                    pos[1] = e.getRootPosY();
                    pos[2] = e.getPosZ();
                    return true;
                }
            }
        }
        return false;
    }

    public MapSrc acquireMap(@NonNull String id) {
        for (MapSrc e :
                mapSrcs) {
            if (e.getId().equals(id)) {
                return e;
            }
        }
        return null;
    }

    public MapSrc acquireMainMap() {
        return mainMapSrc;
    }

    public List<RenderUnit> getRenderMap(@NonNull String id) {
        if (id != null && mapRenderUnits.containsKey(id)) {
            return mapRenderUnits.get(id);
        }
        return null;
    }

    public List<RenderUnit> getMainRenderMap() {
        if (mainMapSrc != null) {
            return getRenderMap(mainMapSrc.getId());
        }
        return null;
    }

    public String getInnerId(@NonNull String id, @NonNull MapSrc mapSrc) {
        String result = null;
        for (Interest e :
                mapSrc.getInterests()) {
            if (e.getId().equals(id)) {
                result = e.getInnerId();
                break;
            }
        }
        return result;
    }

    public Map<MapSrc, List<RenderUnit>> getMaps(@NonNull String id) {
        Map<MapSrc, List<RenderUnit>> mapSrcListMap = new HashMap<>();
        for (MapSrc e :
                mapSrcs) {
            if (e.getId().contains(id)) {
                mapSrcListMap.put(e, mapRenderUnits.get(e.getId()));
            }
        }
        return mapSrcListMap;
    }
}
