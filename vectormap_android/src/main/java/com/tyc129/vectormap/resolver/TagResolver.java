package com.tyc129.vectormap.resolver;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 标签解析器
 * 将标签和id相对应
 * Created by Code on 2017/5/15 0015.
 *
 * @author 谈永成
 * @version 1.0
 */
public class TagResolver implements Resolver<Map<String, String>> {
    private static final String LOG_TAG = "TagResolver";

    Map<String, String> tagMap;
    private boolean isReady;
    private InputStream inputStream;
    List<Map<String, String>> mapList;

    @Override
    public void initialize() {
        isReady = false;
        if (tagMap == null) {
            tagMap = new HashMap<>();
        } else {
            tagMap.clear();

        }
        if (mapList == null) {
            mapList = new ArrayList<>();
        } else {
            mapList.clear();
        }

    }

    @Override
    public boolean importSource(InputStream stream) {
        if (inputStream == null) {
            inputStream = stream;
            return true;
        }
        return false;

    }

    @Override
    public boolean isReady() {
        isReady = inputStream != null & tagMap != null & mapList != null;
        return isReady;
    }

    @Override
    public void doParse() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String tempStr;
        String[] mapEntry;
        while ((tempStr = reader.readLine()) != null) {
            mapEntry = tempStr.split(",");
            if (mapEntry.length == 2) {
                tagMap.put(mapEntry[0], mapEntry[1]);
            }
        }
    }

    @Override
    public void destroy() {
        if (tagMap != null) {
            tagMap.clear();
            tagMap = null;
        }
        if (mapList != null) {
            mapList.clear();
            mapList = null;
        }
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            } finally {
                inputStream = null;
            }
        }
        isReady = false;
    }

    @Override
    public List<Map<String, String>> getResult() {
        if (tagMap != null) {
            mapList.clear();
            mapList.add(tagMap);
            return mapList;
        }
        return null;
    }
}
