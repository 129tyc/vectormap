package com.tyc129.vectormap.resolver;

import android.util.Log;
import com.tyc129.vectormap.struct.*;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * 地图解析器
 * 解析地图文件中的数据，包括地图元素和地图本身
 * Created by Code on 2017/5/15 0015.
 *
 * @author 谈永成
 * @version 1.0
 */
public class MapResolver
        extends XMLResolver<MapSrc, MapResolver.ContentHandler> {
    private static final String LOG_TAG = "MapResolver";
    private MapSrc mapSrc;
    private List<MapSrc> mapSrcs;
    private List<Coordinate> coordinates;

    public MapResolver() {
        this(null);
    }

    public MapResolver(List<Coordinate> coordinates) {
        this.coordinates = coordinates;
        ContentHandler contentHandler = new ContentHandler();
        super.setContentHandler(contentHandler);
    }

    @Override
    public void destroy() {
        super.destroy();
        if (mapSrc != null) {
            mapSrc.destroy();
            mapSrc = null;
        }
    }

    @Override
    public List<MapSrc> getResult() {
        if (mapSrc != null) {
            if (mapSrcs == null) {
                mapSrcs = new ArrayList<>();
            } else {
                mapSrcs.clear();
            }
            mapSrcs.add(mapSrc);
            return mapSrcs;
        }
        return null;
    }

    class ContentHandler extends DefaultHandler {
        private List<Road> roads;
        private List<Interest> interests;
        private List<LinePath> paths;
        private List<LinkPoint> points;
        private Coordinate currentCoor;

        @Override
        public void startDocument() throws SAXException {
            roads = new ArrayList<>();
            interests = new ArrayList<>();
            paths = new ArrayList<>();
            points = new ArrayList<>();
            mapSrc = new MapSrc(null);
            currentCoor = null;
        }

        @Override
        public void endDocument() throws SAXException {
            if (mapSrc.getId() == null || mapSrc.getId().equals("")) {
                mapSrc = null;
                roads.clear();
                interests.clear();
                paths.clear();
                points.clear();
            } else {
                mapSrc.setCoordinates(coordinates);
                mapSrc.setPaths(paths);
                mapSrc.setInterests(interests);
                mapSrc.setPoints(points);
                mapSrc.setRoads(roads);
            }
        }

        Point searchPoint(String pointId) {
            if (pointId != null && !pointId.equals("")) {
                if (points != null) {
                    for (Point e :
                            points) {
                        if (e.getId().equals(pointId)) {
                            return e;
                        }
                    }
                }
                if (interests != null) {
                    for (Point e :
                            interests) {
                        if (e.getId().equals(pointId)) {
                            return e;
                        }
                    }
                }
            }
            return null;
        }

        Coordinate searchCoordinate(String coorId) {
            if (coorId != null && !coorId.equals("") && coordinates != null) {
                for (Coordinate e :
                        coordinates) {
                    if (e.getId().equals(coorId)) {
                        return e;
                    }
                }
            }
            return null;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes)
                throws SAXException {
            String tag = (localName == null || localName.equals("")) ?
                    qName : localName;
            String id = attributes.getValue("id");
            Coordinate coordinate = searchCoordinate(attributes.getValue("coordinate"));
            if (id != null && !id.equals("")) {
                switch (tag) {
                    case "LinkPoint": {
                        float posX = Float.parseFloat(attributes.getValue("posX"));
                        float posY = Float.parseFloat(attributes.getValue("posY"));
                        float posZ = Float.parseFloat(attributes.getValue("posZ"));
                        LinkPoint point = new LinkPoint(id, null, posX, posY, posZ);
                        if (coordinate != null) {
                            point.setCoordinate(coordinate);
                        } else {
                            point.setCoordinate(currentCoor);
                        }
                        points.add(point);
                        break;
                    }
                    case "Interest": {
                        Interest interest = new Interest(id, null);
                        float posX = Float.parseFloat(attributes.getValue("posX"));
                        float posY = Float.parseFloat(attributes.getValue("posY"));
                        float posZ = Float.parseFloat(attributes.getValue("posZ"));
                        String typeStr = attributes.getValue("type");
                        String levelStr = attributes.getValue("level");
                        if (typeStr != null) {
                            interest.setType(Interest.InterestType.valueOf(typeStr.toUpperCase()));
                        }
                        if (levelStr != null) {
                            interest.setLevel(Interest.ImportantLevel.valueOf(levelStr.toUpperCase()));
                        }
                        String innerId = attributes.getValue("innerId");
                        interest.setPosX(posX);
                        interest.setPosY(posY);
                        interest.setPosZ(posZ);
                        if (coordinate != null) {
                            interest.setCoordinate(coordinate);
                        } else {
                            interest.setCoordinate(currentCoor);
                        }
                        if (innerId != null && !innerId.equals("")) {
                            interest.setInnerId(innerId);
                        }
                        interests.add(interest);
                        break;
                    }
                    case "LinePath": {
                        Point startPoint = searchPoint(attributes.getValue("startPoint"));
                        Point endPoint = searchPoint(attributes.getValue("endPoint"));
                        String pathData = attributes.getValue("pathData");
                        if (startPoint != null && endPoint != null &&
                                pathData != null && !pathData.equals("")) {
                            LinePath path = new LinePath(id, null);
                            path.setStartPoint(startPoint);
                            path.setEndPoint(endPoint);
                            path.setPathData(pathData);
                            if (coordinate != null) {
                                path.setCoordinate(coordinate);
                            } else {
                                path.setCoordinate(currentCoor);
                            }
                            paths.add(path);
                        }
                        break;
                    }
                    case "Road": {
                        List<Road.RoadAttribute> roadAttributes = new ArrayList<>();
                        Point startPoint = searchPoint(attributes.getValue("startPoint"));
                        Point endPoint = searchPoint(attributes.getValue("endPoint"));
                        String pathData = attributes.getValue("pathData");
                        String roadAttrs = attributes.getValue("attributes");
                        if (roadAttrs != null) {
                            String[] attrs = roadAttrs.split("|");
                            for (String e :
                                    attrs) {
                                roadAttributes.add(Road.RoadAttribute.valueOf(e.toUpperCase()));
                            }
                        }
                        String passLevelStr = attributes.getValue("passLevel");
                        String roadLevelStr = attributes.getValue("roadLevel");
                        String conditionStr = attributes.getValue("condition");

                        if (startPoint != null && endPoint != null &&
                                pathData != null && !pathData.equals("")) {
                            Road road = new Road(id, null);
                            road.setStartPoint(startPoint);
                            road.setEndPoint(endPoint);
                            if (passLevelStr != null) {
                                road.setPassLevel(Road.PassLevel.valueOf(passLevelStr.toUpperCase()));
                            }
                            if (roadLevelStr != null) {
                                road.setRoadLevel(Road.RoadLevel.valueOf(roadLevelStr.toUpperCase()));
                            }
                            if (conditionStr != null) {
                                road.setCondition(Road.TrafficCondition.valueOf(conditionStr.toUpperCase()));
                            }
                            road.setAttributes(roadAttributes);
                            road.setPathData(pathData);
                            if (coordinate != null) {
                                road.setCoordinate(coordinate);
                            } else {
                                road.setCoordinate(currentCoor);
                            }
                            roads.add(road);
                        }
                        break;
                    }
                    case "Group": {
                        if (coordinate != null) {
                            currentCoor = coordinate;
                        }
                        break;
                    }
                    case "VectorMap": {
                        float width = Float.parseFloat(attributes.getValue("width"));
                        float height = Float.parseFloat(attributes.getValue("height"));
                        String unit = attributes.getValue("unit");
                        String name = attributes.getValue("name");
                        mapSrc.setId(id);
                        mapSrc.setWidth(width);
                        mapSrc.setHeight(height);
                        mapSrc.setUnit(MapSrc.MetricUnit.valueOf(unit.toUpperCase()));
                        mapSrc.setName(name);
                        if (coordinate != null) {
                            currentCoor = coordinate;
                        }
                        mapSrc.setRootCoordinate(currentCoor);
                        break;
                    }
                }
            }
        }
    }

}
