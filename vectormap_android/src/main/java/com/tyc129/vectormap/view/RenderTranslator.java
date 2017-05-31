package com.tyc129.vectormap.view;


import android.graphics.*;
import android.util.Log;
import com.tyc129.vectormap.struct.*;
import com.tyc129.vectormap.struct.Path;
import com.tyc129.vectormap.struct.Point;

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

    private List<DrawSrc> drawSrcList;
    private List<AttributesUnit> attributesCache;

    public RenderTranslator() {
        this(null);
    }

    public RenderTranslator(List<DrawSrc> drawSrcs) {
        attributesCache = new ArrayList<>();
        setDrawSrcList(drawSrcs);
    }

    public List<DrawSrc> getDrawSrcList() {
        return drawSrcList;
    }

    public void setDrawSrcList(List<DrawSrc> drawSrcList) {
        if (drawSrcList != null) {
            this.drawSrcList = drawSrcList;
            refreshAttributesCache();
        }
    }

    private void refreshAttributesCache() {
        for (DrawSrc e :
                drawSrcList) {

            AttributesUnit attributesUnit = new AttributesUnit();
            attributesUnit.type = e.getSpecifyType();
            if (e.getSpecifyData() != null) {
                String[] temp = e.getSpecifyData().split(" ");
                for (String attr :
                        temp) {
                    String[] temp2 = attr.split(",");
                    if (temp2.length > 1) {
                        for (int i = 1; i < temp2.length; i++) {
                            attributesUnit.attributes.add(temp2[i].toUpperCase());
                        }
                    }
                }
            }
            attributesCache.add(attributesUnit);
        }
    }

    public List<RenderUnit> translatePaths(List<Path> paths) {
        List<RenderUnit> units = new ArrayList<>();
        for (Path e :
                paths) {
            RenderUnit unit = new RenderUnit();
            if (e instanceof Road) {
                Road temp = (Road) e;
                List<String> attributes = new ArrayList<>();
                if (temp.getPassLevel() != null) {
                    attributes.add(temp.getPassLevel().toString());
                }
                if (temp.getRoadLevel() != null) {
                    attributes.add(temp.getRoadLevel().toString());
                }
                if (temp.getAttributes() != null) {
                    for (Road.RoadAttribute attr :
                            temp.getAttributes()) {
                        attributes.add(attr.toString());
                    }
                }
                int i;
                int size = attributesCache.size();
                for (i = 0; i < size; i++) {
                    if (attributesCache.get(i).type.equals("Road") &&
                            attributesCache.get(i).attributes.containsAll(attributes)) {
                        break;
                    }
                }
                unit.setRenderBody(RenderUnit.RenderBody.PATH);
                DrawSrc drawSrc = drawSrcList.get(i);
                switch (drawSrc.getPaintType()) {
                    case BITMAP:
                        unit.setRenderType(RenderUnit.RenderType.BITMAP);
                        break;
                    case CANVAS:
                        unit.setRenderType(RenderUnit.RenderType.PAINT);
                        unit.setPaint(drawSrc.getPaint());
                        break;
                }
                unit.setPath(e.transfer2Render());
            } else {
                LinePath temp = (LinePath) e;
                DrawSrc drawSrc = null;
                if (temp.getPathAttribute() != null) {
                    int i;
                    int size = attributesCache.size();
                    for (i = 0; i < size; i++) {
                        if (attributesCache.get(i).type.equals("LinePath") &&
                                attributesCache.get(i).attributes.contains(temp.getPathAttribute().toString())) {
                            break;
                        }
                    }
                    drawSrc = drawSrcList.get(i);
                } else {
                    for (DrawSrc drawE :
                            drawSrcList) {
                        if (drawE.getSpecifyType().equals("LinePath") &&
                                drawE.getSpecifyData() == null) {
                            drawSrc = drawE;
                            break;
                        }
                    }
                }
                unit.setPaint(drawSrc.getPaint());
                unit.setPath(temp.transfer2Render());
                unit.setRenderBody(RenderUnit.RenderBody.PATH);
                unit.setRenderType(RenderUnit.RenderType.PAINT);
            }
            units.add(unit);
        }
        return units;
    }

    public List<RenderUnit> translatePoints(List<Point> points,
                                            Map<String, Bitmap> bitmapMap) {
        List<RenderUnit> units = new ArrayList<>();
        for (Point e :
                points) {
            RenderUnit unit = new RenderUnit();
            unit.setPoint(new PointF(e.getRootPosX(), e.getRootPosY()));
            unit.setFloor(e.getRootPosZ());
            if (e instanceof Interest) {
                Interest temp = (Interest) e;
                List<String> attributes = new ArrayList<>();
                if (temp.getType() != null) {
                    attributes.add(temp.getType().toString());
                }
                if (temp.getLevel() != null) {
                    attributes.add(temp.getLevel().toString());
                }
                int i;
                int size = attributesCache.size();
                for (i = 0; i < size; i++) {
                    if (attributesCache.get(i).type.equals("Interest") &&
                            attributesCache.get(i).attributes.containsAll(attributes)) {
                        break;
                    }
                }
                unit.setRenderBody(RenderUnit.RenderBody.POINT);
                DrawSrc drawSrc = drawSrcList.get(i);
                switch (drawSrc.getPaintType()) {
                    case BITMAP:
                        unit.setRenderType(RenderUnit.RenderType.BITMAP);
                        if (bitmapMap.containsKey(drawSrc.getBitmapId())) {
                            unit.setBitmapId(drawSrc.getBitmapId());
                            unit.setBitmap(bitmapMap.get(drawSrc.getBitmapId()));
                        }
                        break;
                    case CANVAS:
                        unit.setRenderType(RenderUnit.RenderType.PAINT);
                        break;
                }
            } else {
                unit.setRenderBody(RenderUnit.RenderBody.POINT);
                unit.setRenderType(RenderUnit.RenderType.BITMAP);
                for (DrawSrc drawE :
                        drawSrcList) {
                    if (drawE.getSpecifyType().equals("LinkPoint")) {
                        unit.setBitmapId(drawE.getBitmapId());
                        unit.setBitmap(bitmapMap.get(drawE.getBitmapId()));
                        break;
                    }
                }
            }
            units.add(unit);
        }
        return units;
    }

    public List<RenderUnit> translateTags(Map<String, String> tagsMap, List<Point> points) {
        List<RenderUnit> units = new ArrayList<>();
        Paint paint = null;
        float margin = 100;
        for (DrawSrc e :
                drawSrcList) {
            if (e.getDrawType() == DrawSrc.DrawType.TAG) {
                paint = e.getPaint();
                margin = e.getTextMargin();
                break;
            }
        }
        for (Map.Entry<String, String> tag :
                tagsMap.entrySet()) {
            RenderUnit unit = new RenderUnit();
            unit.setPaint(paint);
            unit.setRenderBody(RenderUnit.RenderBody.TAG);
            unit.setRenderType(RenderUnit.RenderType.PAINT);
            unit.setTagText(tag.getValue());
            unit.setTextMargin(margin);
            String id = tag.getKey();
            for (Point e :
                    points) {
                if (e.getId().equals(id)) {
                    unit.setPoint(new PointF(e.getRootPosX(), e.getRootPosY()));
                    unit.setFloor(e.getRootPosZ());
                    break;
                }
            }
            units.add(unit);
        }
        return units;
    }

    public List<RenderUnit> translate(List<Point> points,
                                      List<Path> paths,
                                      Map<String, String> tagsMap,
                                      Map<String, Bitmap> bitmapMap) {
        List<RenderUnit> renderUnits = new ArrayList<>();
        renderUnits.addAll(this.translatePaths(paths));
        renderUnits.addAll(this.translatePoints(points, bitmapMap));
        renderUnits.addAll(this.translateTags(tagsMap, points));
        return renderUnits;
    }

    class AttributesUnit {
        String type;
        List<String> attributes;

        AttributesUnit() {
            attributes = new ArrayList<>();
        }
    }
}
