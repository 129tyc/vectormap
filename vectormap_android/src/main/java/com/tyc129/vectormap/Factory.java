package com.tyc129.vectormap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ProgressBar;
import com.tyc129.vectormap.resolver.*;
import com.tyc129.vectormap.struct.Coordinate;
import com.tyc129.vectormap.struct.MapSrc;
import com.tyc129.vectormap.struct.Path;
import com.tyc129.vectormap.struct.Point;
import com.tyc129.vectormap.view.DrawSrc;
import com.tyc129.vectormap.view.RenderTranslator;
import com.tyc129.vectormap.view.RenderUnit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 矢量地图工厂
 * 提供异步资源解析、绑定数据和地图元素的平台
 * Created by Code on 2017/5/15 0015.
 *
 * @author 谈永成
 * @version 1.0
 */
public class Factory {

    public interface CallBack {
        void buildFailed(String msg);

        void buildSuccess(VectorMap vectorMap);
    }

    private Context context;
    private boolean isAsync;
    private List<InputStream> mapStreams;
    private Map<String, InputStream> bitmapStreams;
    private InputStream coorStream;
    private InputStream tagStream;
    private InputStream drawSrcStream;
    private ProgressBar progressBar;
    private CallBack callBack;
    private List<Coordinate> coordinates;
    private List<MapSrc> mapSrcs;
    private List<DrawSrc> drawSrcList;
    private Map<String, String> tagsMap;
    private Map<String, Bitmap> bitmapMap;
    private Map<String, List<RenderUnit>> mapRenderUnits;

    public Factory(@NonNull Context context) {
        this.context = context;
        initialize();
    }

    public Factory(@NonNull Context context, @NonNull CallBack callBack) {
        this.context = context;
        this.callBack = callBack;
        initialize();
    }

    private void initialize() {
        mapStreams = new ArrayList<>();
        bitmapStreams = new HashMap<>();
        mapSrcs = new ArrayList<>();
        drawSrcList = new ArrayList<>();
        tagsMap = new HashMap<>();
        bitmapMap = new HashMap<>();
        mapRenderUnits = new HashMap<>();
    }

    public void build() {
        if (callBack == null) {
            return;
        }
        if (coorStream == null ||
                mapStreams.isEmpty() ||
                tagStream == null ||
                drawSrcStream == null) {
            callBack.buildFailed("Recourse Not Ready!");
            return;
        }
        if (isAsync) {
            AsyncBuilder builder = new AsyncBuilder(progressBar);
            builder.execute("");
        } else {
            try {
                if (parseCoordinates() &&
                        parseMaps() &&
                        parseDrawSrcs() &&
                        parseTags() &&
                        parseBitmaps()) {
                    translate2Render();
                    callBack.buildSuccess(new VectorMap(mapSrcs,
                            drawSrcList,
                            mapRenderUnits,
                            coordinates,
                            tagsMap,
                            bitmapMap));
                } else {
                    callBack.buildFailed("Parse Failed!");
                }
            } catch (Exception e) {
                callBack.buildFailed(e.getMessage());
            }
        }
    }

    private boolean translate2Render() {
        RenderTranslator translator = new RenderTranslator(drawSrcList);
        List<Point> points = new ArrayList<>();
        List<Path> paths = new ArrayList<>();
        for (MapSrc e :
                mapSrcs) {
            points.addAll(e.getPoints());
            points.addAll(e.getInterests());
            paths.addAll(e.getRoads());
            paths.addAll(e.getPaths());
            List<RenderUnit> result = translator.translate(points, paths, tagsMap, bitmapMap);
            if (result != null && !result.isEmpty()) {
                mapRenderUnits.put(e.getId(), result);
            }
            paths.clear();
            points.clear();
        }
        return true;
    }

    private boolean parseBitmaps() throws Exception {
        for (Map.Entry<String, InputStream> e :
                bitmapStreams.entrySet()) {
            Bitmap temp = BitmapFactory.decodeStream(e.getValue());
            String id = e.getKey();
            int index = id.indexOf("/");
            if (index > 0) {
                id = id.substring(index + 1, id.length());

            }
            index = id.indexOf(".");
            if (index > 0) {
                id = id.substring(0, index);
            }
            if (temp != null && !id.equals("")) {
                bitmapMap.put(id, temp);
            } else {
                return false;
            }
        }
        return true;
    }

    private boolean parseDrawSrcs() throws Exception {
        Resolver<DrawSrc> drawSrcResolver = new DrawSrcResolver();
        drawSrcResolver.importSource(drawSrcStream);
        drawSrcResolver.initialize();
        if (drawSrcResolver.isReady()) {
            drawSrcResolver.doParse();
            drawSrcList.addAll(drawSrcResolver.getResult());
            return true;
        }
        return false;
    }

    private boolean parseTags() throws Exception {
        Resolver<Map<String, String>> tagResolver = new TagResolver();
        tagResolver.importSource(tagStream);
        tagResolver.initialize();
        if (tagResolver.isReady()) {
            tagResolver.doParse();
            tagsMap = tagResolver.getResult().get(0);
            return true;
        }
        return false;
    }

    private boolean parseCoordinates() throws Exception {
        Resolver<Coordinate> coordinateResolver = new CoorResolver();
        coordinateResolver.importSource(coorStream);
        coordinateResolver.initialize();
        if (coordinateResolver.isReady()) {
            coordinateResolver.doParse();
            coordinates = coordinateResolver.getResult();
            return true;
        }
        return false;

    }

    private boolean parseMaps() throws Exception {
        Resolver<MapSrc> mapSrcResolver = new MapResolver(coordinates);
        for (InputStream e :
                mapStreams) {
            mapSrcResolver.importSource(e);
            mapSrcResolver.initialize();
            if (mapSrcResolver.isReady()) {
                mapSrcResolver.doParse();
                mapSrcs.addAll(mapSrcResolver.getResult());
            }
        }
        return true;
    }

    public void setCallBack(@NonNull CallBack callBack) {
        this.callBack = callBack;
    }

    public boolean isAsync() {
        return isAsync;
    }

    public void setAsync(boolean async) {
        isAsync = async;
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public void addMapRecourse(@NonNull List<InputStream> mapStreams) {
        this.mapStreams.addAll(mapStreams);
    }

    public void addMapRecourse(@NonNull int recourseId) {
        this.mapStreams.add(context.getResources().openRawResource(recourseId));
    }

    public void addMapRecourse(@NonNull InputStream mapStreams) {
        this.mapStreams.add(mapStreams);
    }

    public void addCoorRecourse(@NonNull int recourseId) {
        this.coorStream = context.getResources().openRawResource(recourseId);
    }

    public void addCoorRecourse(@NonNull InputStream coorStream) {
        this.coorStream = coorStream;
    }

    public void addTagRecourse(@NonNull int recourseId) {
        this.tagStream = context.getResources().openRawResource(recourseId);
    }

    public void addTagRecourse(@NonNull InputStream tagStream) {
        this.tagStream = tagStream;
    }

    public void addDrawSrcRecourse(@NonNull int recourseId) {
        this.drawSrcStream = context.getResources().openRawResource(recourseId);
    }

    public void addDrawSrcRecourse(@NonNull InputStream drawSrcStream) {
        this.drawSrcStream = drawSrcStream;
    }

    public void addBitmapRecourse(@NonNull int recourseId) {
        bitmapStreams.put(context.getResources().getResourceName(recourseId),
                context.getResources().openRawResource(recourseId));
    }

    public void addBitmapRecourse(@NonNull File bitmapFile) throws FileNotFoundException {
        bitmapStreams.put(bitmapFile.getName(), new FileInputStream(bitmapFile));
    }

    class AsyncBuilder extends AsyncTask<String, Integer, String> {
        private ProgressBar bar;

        public AsyncBuilder(ProgressBar bar) {
            this.bar = bar;
        }

        @Override
        protected String doInBackground(String... strings) {
            if (callBack == null) {
                return null;
            }
            if (coorStream == null ||
                    mapStreams.isEmpty() ||
                    tagStream == null ||
                    drawSrcStream == null) {
                callBack.buildFailed("Recourse Not Ready!");
                return null;
            }
            String error = null;
            try {
                if (!parseCoordinates()) {
                    error = "Coordinate Build ERROR!";
                } else {
                    publishProgress(10);
                }
                if (error != null && !parseMaps()) {
                    error = "Maps Build ERROR!";
                } else {
                    publishProgress(30);
                }
                if (error != null && !parseDrawSrcs()) {
                    error = "DrawSrc Build ERROR!";
                } else {
                    publishProgress(50);
                }
                if (error != null && !parseTags()) {
                    error = "Tags Build ERROR!";
                } else {
                    publishProgress(70);
                }
                if (error != null && !parseBitmaps()) {
                    error = "Bitmaps Build ERROR!";
                } else {
                    publishProgress(90);
                }
                if (error == null) {
                    translate2Render();
                    publishProgress(100);
                    callBack.buildSuccess(new VectorMap(mapSrcs,
                            drawSrcList,
                            mapRenderUnits,
                            coordinates,
                            tagsMap,
                            bitmapMap));
                } else {
                    callBack.buildFailed(error);
                }
            } catch (Exception e) {
                callBack.buildFailed(e.getMessage());
            }
            return "";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if (bar != null) {
                bar.setProgress(values[0]);
            }
        }
    }
}
