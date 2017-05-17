package com.tyc129.vectormap.resolver;

import com.tyc129.vectormap.struct.*;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
            mapSrc.destory();
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

        private Attributes attributes;
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
            } else {
                mapSrc.setCoordinates(coordinates);
                mapSrc.setPaths(paths);
                mapSrc.setInterests(interests);
                mapSrc.setPoints(points);
                mapSrc.setRoads(roads);
            }
        }

        void handleInterest() {

        }

        void handlePoint() {

        }

        void handleRoad() {

        }

        void handleLinePath() {

        }

        void searchCoordinate(String coorId) {
            if (coordinates != null) {
                for (Coordinate e :
                        coordinates) {
                    if (e.getId().equals(coorId)) {
                        currentCoor = e;
                    }
                }
            }
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            String tag = (localName == null || localName.equals("")) ?
                    qName : localName;
            switch (tag) {
                case "Interest": {
                    break;
                }
                case "LinkPoint": {
                    break;
                }
                case "Road": {
                    break;
                }
                case "LinePath": {
                    break;
                }
                case "Group": {
                    break;
                }
                case "VectorMap": {
                    String id = attributes.getValue("id");
                    float width = Float.parseFloat(attributes.getValue("width"));
                    float height = Float.parseFloat(attributes.getValue("height"));
                    String unit = attributes.getValue("unit");
                    String rootCoordinate = attributes.getValue("rootCoordinate");
                    mapSrc.setId(id);
                    mapSrc.setWidth(width);
                    mapSrc.setHeight(height);
                    mapSrc.setUnit(MapSrc.MetricUnit.valueOf(unit));
                    searchCoordinate(rootCoordinate);
                    mapSrc.setRootCoordinate(currentCoor);
                    break;
                }

            }
        }
    }

}
