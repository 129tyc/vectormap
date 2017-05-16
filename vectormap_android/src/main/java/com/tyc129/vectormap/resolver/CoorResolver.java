package com.tyc129.vectormap.resolver;


import android.util.Log;
import com.tyc129.vectormap.struct.Coordinate;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 坐标系解析器
 * 从文件中解析出坐标系信息
 * Created by Code on 2017/5/15 0015.
 *
 * @author 谈永成
 * @version 1.0
 */
public class CoorResolver implements Resolver<Coordinate> {
    private static final String LOG_TAG = "CoorResolver";

    private boolean isReady;
    private InputStream inputStream;
    private List<Coordinate> coordinates;
    private XMLReader xmlReader;
    private ContentHandler handler;


    @Override
    public void initialize() {
        if (xmlReader == null) {
            try {
                xmlReader = XMLReaderFactory.createXMLReader();
            } catch (SAXException e) {
                e.printStackTrace();
            }
        }
        if (handler == null) {
            handler = new ContentHandler();
        }
        if (coordinates == null) {
            coordinates = new ArrayList<>();
        } else {
            coordinates.clear();
        }
    }

    @Override
    public boolean importSource(InputStream stream) {
        boolean flag = closeInputStream();
        if (inputStream == null) {
            inputStream = stream;
        }
        return flag;
    }

    @Override
    public boolean isReady() {
        isReady = inputStream != null &
                xmlReader != null &
                handler != null &
                coordinates != null;
        return isReady;
    }

    @Override
    public void doParse() {
        if (isReady) {
            xmlReader.setContentHandler(handler);
            try {
                xmlReader.parse(new InputSource(inputStream));
            } catch (IOException | SAXException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            }
        }
    }

    @Override
    public void destroy() {
        if (xmlReader != null) {
            xmlReader = null;
        }
        if (handler != null) {
            handler = null;
        }
        closeInputStream();
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

    private boolean closeInputStream() {
        if (inputStream != null) {
            try {
                inputStream.close();
                inputStream = null;
            } catch (IOException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                return false;
            }
        }
        return true;
    }

    private class CoorErrorHandler implements ErrorHandler {

        @Override
        public void warning(SAXParseException e) throws SAXException {

        }

        @Override
        public void error(SAXParseException e) throws SAXException {

        }

        @Override
        public void fatalError(SAXParseException e) throws SAXException {

        }
    }

    private class ContentHandler extends DefaultHandler {

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            String id = null;
            Coordinate postCoor = null;
            float oriX = 0f;
            float oriY = 0f;
            float oriZ = 0f;
            float rotateDeg = 0f;
            switch (localName) {
                case "id": {
                    id = attributes.getValue("id");
                    break;
                }
                case "oriX": {
                    oriX = Float.parseFloat(attributes.getValue("oriX"));
                    break;
                }
                case "oriY": {
                    oriY = Float.parseFloat(attributes.getValue("oriY"));
                    break;
                }
                case "oriZ": {
                    oriZ = Float.parseFloat(attributes.getValue("oriZ"));
                    break;
                }
                case "rotateDeg": {
                    rotateDeg = Float.parseFloat(attributes.getValue("rotateDeg"));
                    break;
                }
                case "postCoordinate": {
                    String postId = attributes.getValue("postCoordinate");
                    if (!postId.equals("")) {
                        for (Coordinate e :
                                coordinates) {
                            if (e.getId().equals(postId)) {
                                postCoor = e;
                                break;
                            }
                        }
                    }
                    break;
                }
            }
            if (id != null && !id.equals("")) {
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
