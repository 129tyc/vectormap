package com.tyc129.vectormap.resolver;


import android.util.Log;
import com.tyc129.vectormap.struct.Coordinate;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 坐标系解析器
 * 从文件中解析出坐标系信息
 * Created by Code on 2017/5/15 0015.
 *
 * @author 谈永成
 * @version 1.0
 */
public class CoorResolver
        extends XMLResolver<Coordinate, CoorResolver.ContentHandler> {
    private static final String LOG_TAG = "CoorResolver";
    private List<Coordinate> coordinates;
    private ContentHandler contentHandler;


    @Override
    public void initialize() {
        super.initialize();
        if (coordinates == null) {
            coordinates = new ArrayList<>();
        } else {
            coordinates.clear();
        }
        if (contentHandler == null) {
            contentHandler = new ContentHandler();
        }
        super.setContentHandler(contentHandler);
    }

    @Override
    public boolean isReady() {
        return coordinates != null && super.isReady();
    }

    @Override
    public void destroy() {
        super.destroy();
        if (coordinates != null) {
            coordinates.clear();
            coordinates = null;
        }
    }

    @Override
    public List<Coordinate> getResult() {
        if (coordinates != null) {
            if (coordinates.isEmpty()) {
                return null;
            }
        }
        return coordinates;
    }

    class ContentHandler extends DefaultHandler {
        private static final String LOG_TAG = "CoorHandler";
        String id = null;
        Coordinate postCoor = null;
        float oriX = 0f;
        float oriY = 0f;
        float oriZ = 0f;
        float rotateDeg = 0f;

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
//            Log.i(LOG_TAG, "Parse--->" + localName);
//            System.out.println(localName + "--" + qName);
            if (localName.equals("Coordinate") ||
                    qName.equals("Coordinate")) {
                id = attributes.getValue("id");
                if (id != null && !id.equals("")) {
                    oriX = Float.parseFloat(attributes.getValue("oriX"));
                    oriY = Float.parseFloat(attributes.getValue("oriY"));
                    oriZ = Float.parseFloat(attributes.getValue("oriZ"));
                    rotateDeg = Float.parseFloat(attributes.getValue("rotateDeg"));
                    String postId = attributes.getValue("postCoordinate");
                    if (postId != null && !postId.equals("")) {
                        for (Coordinate e :
                                coordinates) {
                            if (e.getId().equals(postId)) {
                                postCoor = e;
                                break;
                            }
                        }
                    }
                    Coordinate coordinate = new Coordinate(id, postCoor);
                    coordinate.setOriX(oriX);
                    coordinate.setOriY(oriY);
                    coordinate.setOriZ(oriZ);
                    coordinate.setRotateDeg(rotateDeg);
                    coordinates.add(coordinate);
                }

            }
        }
    }
}
