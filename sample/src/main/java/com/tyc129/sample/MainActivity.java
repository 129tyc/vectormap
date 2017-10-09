package com.tyc129.sample;

import android.app.ProgressDialog;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.tyc129.vectormap.MapFactory;
import com.tyc129.vectormap.NaviAnalyzer;
import com.tyc129.vectormap.VectorMap;
import com.tyc129.vectormap.struct.Interest;
import com.tyc129.vectormap.struct.Path;
import com.tyc129.vectormap.view.MapRender;
import com.tyc129.vectormap.view.RenderUnit;
import com.tyc129.vectormap.view.VectorMapView;

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
    private Map<String, String> tags;
    private NaviAnalyzer naviAnalyzer;
    private MapRender render;
    private VectorMap vectorMap;
    private float currX;
    private float currY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sId = null;
        eId = null;


        vectorMapView = (VectorMapView) findViewById(R.id.vectorMapView);
        startPos = (TextView) findViewById(R.id.startPos);
        endPos = (TextView) findViewById(R.id.endPos);
        runNavi = (Button) findViewById(R.id.runNavi);
        clear = (Button) findViewById(R.id.clear);
        currPos = (Button) findViewById(R.id.currPos);
        vectorMapView.setOnClickPointListener(new VectorMapView.OnClickPointListener() {
            @Override
            public void onClickPoint(String id, float touchX, float touchY) {
                currPosId = id;
                currX = touchX;
                currY = touchY;
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
            }
        });
        reset = (Button) findViewById(R.id.reset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vectorMapView.mapReset();
            }
        });
        final MapFactory factory = new MapFactory(this, new MapFactory.CallBack() {
            @Override
            public void buildFailed(String msg) {
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void buildSuccess(VectorMap vectorMap) {
                MainActivity.this.vectorMap = vectorMap;
                tags = vectorMap.getTags();
                naviAnalyzer = new NaviAnalyzer();
                naviAnalyzer.setSource(vectorMap.acquireMainMap());
                naviAnalyzer.initialize();
                Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                render = new MapRender();
                render.setCurrentUnits(vectorMap.getMainRenderMap());
                vectorMapView.setMapRecourse(vectorMap.acquireMainMap());
                vectorMapView.setMapRender(render);
                Bitmap narrow = BitmapFactory.decodeStream(getResources().openRawResource(R.drawable.narrow));
                if (narrow != null) {
                    vectorMapView.setDirectionNarrow(narrow);
//                    vectorMapView.startDirectionIndicate(VectorMapView.CompassStyle.INDEPENDENCE,
//                            300, 300);
                } else {
                    Log.e(LOG_TAG, "Can not Create Bitmap");
                }
                vectorMapView.mapRefresh();
            }
        });
        factory.addBitmapRecourse(R.drawable.building);
        factory.addBitmapRecourse(R.drawable.little_circle);
        factory.addBitmapRecourse(R.drawable.toilet);
        factory.addCoorRecourse(R.raw.coordinates);
        factory.addDrawSrcRecourse(R.raw.paint);
        factory.addMapRecourse(R.raw.map);
        factory.addTagRecourse(R.raw.tag);
        factory.setAsync(true);
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        factory.setProgressDialog(progressDialog);
        factory.build();
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
                    vectorMap.acquirePositionForMain(currPosId, pos);
                    vectorMapView.stopDirectionIndicate();
                    if (vectorMapView.startDirectionIndicate(
                            VectorMapView.CompassStyle.INDEPENDENCE, pos[0], pos[1])) {
                        vectorMapView.mapRefresh();
                    }

                }
            }
        });
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
