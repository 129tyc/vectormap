package com.tyc129.vectormap.resolver;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import com.tyc129.vectormap.view.DrawSrc;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 绘制资源解析器
 * 从文件中解析地图绘制信息，包括画刷和位图Id
 * Created by Code on 2017/5/15 0015.
 *
 * @author 谈永成
 * @version 1.0
 */
public class DrawSrcResolver
        extends XMLResolver<DrawSrc, DrawSrcResolver.ContentHandler> {
    private final static String LOG_TAG = "DrawSrcResolver";
    private List<DrawSrc> drawSrcList;
    private ContentHandler contentHandler;

    public DrawSrcResolver() {
        contentHandler = new ContentHandler();
        super.setContentHandler(contentHandler);
    }

    @Override
    public void initialize() {
        super.initialize();
        if (drawSrcList == null) {
            drawSrcList = new ArrayList<>();
        } else {
            drawSrcList.clear();
        }
    }

    @Override
    public boolean isReady() {
        return drawSrcList != null & super.isReady();
    }

    @Override
    public void destroy() {
        super.destroy();
        if (drawSrcList != null) {
            drawSrcList.clear();
            drawSrcList = null;
        }
        if (contentHandler != null) {
            contentHandler = null;
        }
    }

    @Override
    public List<DrawSrc> getResult() {
        if (drawSrcList != null && !drawSrcList.isEmpty()) {
            return drawSrcList;
        }
        return null;
    }

    class ContentHandler extends DefaultHandler {

        private Paint parsePaint(Attributes attributes) {
            Paint paint = new Paint();
            String color = attributes.getValue("paintColor");
            String strokeWidth = attributes.getValue("strokeWidth");
            String alpha = attributes.getValue("alpha");
            String style = attributes.getValue("style");
            String typeFace = attributes.getValue("typeFace");
            String textAlign = attributes.getValue("textAlign");
            String textSize = attributes.getValue("textSize");
            if (typeFace != null) {
                switch (typeFace) {
                    case "default":
                    default:
                        paint.setTypeface(Typeface.DEFAULT);
                        break;
                }
            }
            if (textAlign != null) {
                paint.setTextAlign(Paint.Align.valueOf(textAlign.toUpperCase()));
            }
            if (textSize != null) {
                paint.setTextSize(Float.parseFloat(textSize));
            }
            if (color != null) {
                paint.setColor(Color.parseColor(color.toUpperCase()));
            }
            if (strokeWidth != null) {
                paint.setStrokeWidth(Float.parseFloat(strokeWidth));
            }
            if (alpha != null) {
                paint.setAlpha(Integer.parseInt(alpha));
            }
            if (style != null) {
                paint.setStyle(Paint.Style.valueOf(style.toUpperCase()));
            }
            return paint;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            String tag = (localName == null || localName.equals("")) ?
                    qName : localName;
            if (!tag.equals("MapPaint")) {
                DrawSrc src = new DrawSrc();
                String paintType = attributes.getValue("paintType");
                String specifyType = attributes.getValue("specifyType");
                String specifyData = attributes.getValue("specifyData");
                String scale = attributes.getValue("scale");
                String textMargin = attributes.getValue("textMargin");
                src.setDrawType(DrawSrc.DrawType.valueOf(tag.toUpperCase()));
                if (scale != null) {
                    src.setScale(Float.parseFloat(scale));
                }
                if (textMargin != null) {
                    src.setTextMargin(Float.parseFloat(textMargin));
                }
                if (paintType != null) {
                    src.setPaintType(DrawSrc.PaintType.valueOf(paintType.toUpperCase()));
                }
                src.setSpecifyType(specifyType);
                if (specifyData != null && !specifyData.equals("")) {
                    src.setSpecifyData(specifyData);
                }
                switch (src.getPaintType()) {
                    case BITMAP: {
                        src.setBitmapId(attributes.getValue("src"));
                        break;
                    }
                    case CANVAS: {
                        src.setPaint(parsePaint(attributes));
                        break;
                    }
                }
                drawSrcList.add(src);
            }

        }
    }
}
