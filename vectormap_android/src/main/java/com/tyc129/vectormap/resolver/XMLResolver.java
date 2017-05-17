package com.tyc129.vectormap.resolver;

import android.util.Log;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Code on 2017/5/17 0017.
 *
 * @author 谈永成
 * @version 1.0
 */
public abstract class XMLResolver<T, H extends DefaultHandler>
        implements Resolver<T> {
    private static String LOG_TAG = "XMLReader";
    private boolean isReady;
    private InputStream inputStream;
    private XMLReader reader;
    private ErrorHandler errorHandler;
    private H contentHandler;

    @Override
    public void initialize() {
        isReady = false;
        if (reader == null) {
            try {
                SAXParserFactory factory = SAXParserFactory.newInstance();
                SAXParser parser = factory.newSAXParser();
                reader = parser.getXMLReader();
            } catch (SAXException | ParserConfigurationException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            }
        }
        if (errorHandler == null) {
            errorHandler = new ErrorHandler();
        }

    }

    @Override
    public void destroy() {
        if (reader != null) {
            reader = null;
        }
        if (errorHandler != null) {
            errorHandler = null;
        }
        tryCloseInputStream();
    }

    private boolean tryCloseInputStream() {
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

    public void setContentHandler(H contentHandler) {
        this.contentHandler = contentHandler;
    }

    @Override
    public boolean importSource(InputStream stream) {
        boolean flag = tryCloseInputStream();
        if (inputStream == null) {
            inputStream = stream;
        }
        return flag;
    }

    @Override
    public void doParse() {
        if (isReady()) {
            reader.setContentHandler(contentHandler);
            reader.setErrorHandler(errorHandler);
            try {
                reader.parse(new InputSource(inputStream));
            } catch (IOException | SAXException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            }
        }
    }

    @Override
    public boolean isReady() {
        isReady = inputStream != null &
                contentHandler != null &
                errorHandler != null &
                reader != null;
        return isReady;
    }

    private class ErrorHandler implements org.xml.sax.ErrorHandler {

        @Override
        public void warning(SAXParseException e) throws SAXException {
            Log.i(LOG_TAG, e.getMessage());
        }

        @Override
        public void error(SAXParseException e) throws SAXException {
            Log.e(LOG_TAG, e.getMessage(), e);
        }

        @Override
        public void fatalError(SAXParseException e) throws SAXException {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
    }
}