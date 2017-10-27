package com.tyc129.sample;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.tyc129.vectormap.MapFactory;
import com.tyc129.vectormap.NaviAnalyzer;
import com.tyc129.vectormap.VectorMap;
import com.tyc129.vectormap.struct.MapSrc;
import com.tyc129.vectormap.struct.Path;
import com.tyc129.vectormap.view.MapRender;
import com.tyc129.vectormap.view.RenderUnit;
import com.tyc129.vectormap.view.VectorMapView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = "Sample";

    private VectorMapView vectorMapView;
    private Button reset;
    private TextView startPos;
    private Button currPos;
    private String currPosId;
    private String sId;
    private String eId;
    private TextView endPos;
    private Button runNavi;
    private Button clear;
    private ListView inner;
    private Map<String, String> tags;
    private NaviAnalyzer naviAnalyzer;
    private MapRender render;
    private VectorMap vectorMap;
    private MapFactory factory;
    private Map<MapSrc, List<RenderUnit>> innerMaps;
    private MapSrc currMainMap;
    private List<RenderUnit> currUnits;
    private List<String> names;
    private List<MapSrc> maps;
    private List<List<RenderUnit>> units;
    private ArrayAdapter<String> adapter;
    private int selectPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sId = null;
        eId = null;
        names = new ArrayList<>();
        maps = new ArrayList<>();
        units = new ArrayList<>();
        inner = (ListView) findViewById(R.id.inner);
        adapter = new ArrayAdapter<>(this, R.layout.layout_item);
        inner.setAdapter(adapter);
        inner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != selectPos) {
                    vectorMapView.stopDirectionIndicate();
                    selectPos = i;
                    vectorMapView.setMapRecourse(maps.get(i));
                    naviAnalyzer.setSource(maps.get(i));
                    naviAnalyzer.initialize();
                    render.setCurrentUnits(units.get(i));
                    vectorMapView.mapRefresh();
                    if (i == 0) {
                        inner.setVisibility(View.GONE);
                    }
                }

            }
        });
        vectorMapView = (VectorMapView) findViewById(R.id.vectorMapView);
        startPos = (TextView) findViewById(R.id.startPos);
        endPos = (TextView) findViewById(R.id.endPos);
        runNavi = (Button) findViewById(R.id.runNavi);
        clear = (Button) findViewById(R.id.clear);
        currPos = (Button) findViewById(R.id.currPos);
        vectorMapView.setOnMapActionOccurListener(new VectorMapView.OnMapActionOccurListener() {
            @Override
            public void onClickPoint(String id, float touchX, float touchY) {
                currPosId = id;
                float pos[] = new float[3];
                vectorMap.acquirePosition(id, pos, currMainMap);
                vectorMapView.selectPosition(pos[0], pos[1]);
                Toast.makeText(MainActivity.this, id, Toast.LENGTH_SHORT).show();
                if (tags != null) {
                    if (sId == null) {
                        sId = id;
                        startPos.setText(tags.get(id));
                    } else {
                        eId = id;
                        endPos.setText(tags.get(id));
                    }
                }
                String innerId = vectorMap.getInnerId(currPosId, vectorMap.acquireMainMap());
                if (innerId != null && !innerId.equals("")) {
                    innerMaps = vectorMap.getMaps(innerId);
                    names.clear();
                    maps.clear();
                    units.clear();
                    names.add(currMainMap.getName());
                    maps.add(currMainMap);
                    units.add(currUnits);
                    for (Map.Entry<MapSrc, List<RenderUnit>> e :
                            innerMaps.entrySet()) {
                        names.add(e.getKey().getName());
                        maps.add(e.getKey());
                        units.add(e.getValue());
                    }
                    adapter.clear();
                    adapter.addAll(names);
                    adapter.notifyDataSetChanged();
                    inner.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onClickMap(float touchX, float touchY) {
                Log.v(LOG_TAG, "Click Something!");
                vectorMapView.cancelSelection();
            }

            @Override
            public void onDoubleTapMap() {

            }

            @Override
            public void onTranslateMap(float dx, float dy) {

            }

            @Override
            public void onRotateMap(float deg, float rx, float ry) {

            }

            @Override
            public void onScaleMap(float scale, float sx, float sy) {

            }

            @Override
            public void onTransformStart() {

            }

            @Override
            public void onTransformEnd() {

            }
        });
        reset = (Button) findViewById(R.id.reset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //vectorMapView.mapReset();
                vectorMapView.locateTo(0, 0, 0, 0, vectorMapView.getMinScaleActually(), 0, 500);
            }
        });
        factory = new MapFactory(this, new MapFactory.CallBack() {
            @Override
            public void buildFailed(String msg) {
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG)
                        .show();
            }

            @Override
            public void buildSuccess(VectorMap vectorMap) {
                MainActivity.this.vectorMap = vectorMap;
                tags = vectorMap.getTags();
                naviAnalyzer = new NaviAnalyzer();
                currMainMap = vectorMap.acquireMainMap();
                naviAnalyzer.setSource(vectorMap.acquireMainMap());
                naviAnalyzer.initialize();
                Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                render = new MapRender();
                render.setCurrentUnits(vectorMap.getMainRenderMap());
                currUnits = vectorMap.getMainRenderMap();
                vectorMapView.setMapRecourse(vectorMap.acquireMainMap());
                vectorMapView.setMapRender(render);
                Bitmap arrow = BitmapFactory.decodeStream(getResources().openRawResource(R.drawable.arrow));
                Bitmap holder = BitmapFactory.decodeStream(getResources().openRawResource(R.drawable.placeholder));
                if (arrow != null) {
                    vectorMapView.setDirectionNarrow(arrow);
//                    vectorMapView.startDirectionIndicate(VectorMapView.CompassStyle.INDEPENDENCE,
//                            300, 300);
                } else {
                    Log.e(LOG_TAG, "Can not Create Bitmap");
                }
                if (holder != null)
                    vectorMapView.setPlaceHolder(holder);
                vectorMapView.mapRefresh();
            }
        });
        factory.addBitmapRecourse(R.drawable.landscape);
        factory.addBitmapRecourse(R.drawable.entrance);
        factory.addBitmapRecourse(R.drawable.building);
        factory.addBitmapRecourse(R.drawable.little_circle);
        factory.addBitmapRecourse(R.drawable.toilet);
        factory.addBitmapRecourse(R.drawable.restaurant);
        factory.addCoorRecourse(R.raw.coordinates);
        //factory.addDrawSrcRecourse(R.raw.paint);
        factory.addDrawSrcRecourse(R.raw.zzupaint);
        factory.addMapRecourse(R.raw.zzui211f);
        factory.addMapRecourse(R.raw.zzui212f);
        factory.addMapRecourse(R.raw.zzumap);
        //factory.addTagRecourse(R.raw.tag);
        factory.addTagRecourse(R.raw.zzutag);
//        factory.setAsync(true);
//        ProgressDialog progressDialog = new ProgressDialog(this);
//        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//        factory.setProgressDialog(progressDialog);
        runNavi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (naviAnalyzer != null &&
                        sId != null && eId != null && !sId.equals(eId)) {
                    List<Path> paths = naviAnalyzer.searchRoute(NaviAnalyzer.SearchType.NORMAL, sId, eId);
                    if (paths != null) {
                        List<RenderUnit> units = factory.buildRenderPaths(paths);
                        render.getTempUnits().addAll(units);
                        vectorMapView.mapRefresh();
                    }
                }
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sId != null) {
                    startPos.setText("起点");
                    sId = null;
                }
                if (eId != null) {
                    endPos.setText("终点");
                    eId = null;
                }
                if (render != null && !render.getTempUnits().isEmpty()) {
                    render.getTempUnits().clear();
                    vectorMapView.mapRefresh();
                }
            }
        });
        currPos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currPosId != null) {
                    Toast.makeText(MainActivity.this, "更换到" + currPosId, Toast.LENGTH_SHORT).show();
                    float[] pos = new float[3];
                    if (selectPos > 0) {
                        vectorMap.acquirePosition(currPosId, pos, maps.get(selectPos));
                    } else {
                        vectorMap.acquirePositionForMain(currPosId, pos);
                    }
                    vectorMapView.locateToCenter(pos[0], pos[1], vectorMapView.getMaxScaleActually(),
                            0, 500);
                    vectorMapView.stopDirectionIndicate();
                    if (vectorMapView.startDirectionIndicate(
                            VectorMapView.CompassStyle.INDEPENDENCE, pos[0], pos[1])) {
                        vectorMapView.mapRefresh();
                    }

                }
//                vectorMapView.setMapRecourse(vectorMap.acquireMap("test"));
//                render.setCurrentUnits(vectorMap.getRenderMap("test"));
//                naviAnalyzer.setSource(vectorMap.acquireMap("test"));
//                naviAnalyzer.initialize();
//                vectorMapView.mapRefresh();
            }
        });
        factory.build();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        vectorMapView.stopDirectionIndicate();
    }

}
