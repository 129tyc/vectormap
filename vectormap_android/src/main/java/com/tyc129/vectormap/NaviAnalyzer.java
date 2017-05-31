package com.tyc129.vectormap;

import android.util.Log;
import com.tyc129.vectormap.struct.*;

import java.util.*;

import static com.tyc129.vectormap.utils.MathUtils.getDistance;


/**
 * 导航分析中心
 * 计算地图中点之间的导航路径
 * Created by Code on 2017/5/15 0015.
 *
 * @author 谈永成
 * @version 1.0
 */
public class NaviAnalyzer {
    public enum SearchType {
        NORMAL,
        WALK,
        NON_MOTOR,
        MOTOR
    }

    private static final String LOG_TAG = "NaviAnalyzer";
    private MapSrc mapSrc;
    private List<NaviUnit> units;
    private Queue<SearchNode> queue;

    public NaviAnalyzer() {
        queue = new LinkedList<>();
    }

    public void initialize() {
        if (units == null) {
            units = new ArrayList<>();
        } else {
            units.clear();
        }
        List<Path> allPaths = new ArrayList<>();
        List<Point> allPoints = new ArrayList<>();
        allPaths.addAll(mapSrc.getRoads());
        allPaths.addAll(mapSrc.getPaths());
        allPoints.addAll(mapSrc.getInterests());
        allPoints.addAll(mapSrc.getPoints());
        boolean[] checkList = new boolean[allPoints.size()];
        for (int i = 0; i < checkList.length; i++) {
            checkList[i] = false;
        }
        for (Path e :
                allPaths) {
            Point sp = e.getStartPoint();
            Point ep = e.getEndPoint();
            NaviUnit sUnit;
            NaviUnit eUnit;
            checkList[allPoints.indexOf(sp)] = true;
            checkList[allPoints.indexOf(ep)] = true;
            int sPos = pointExistInUnits(sp);
            int ePos = pointExistInUnits(ep);
            if (sPos < 0) {
                sUnit = new NaviUnit();
                sUnit.point = sp;
                units.add(sUnit);
            } else {
                sUnit = units.get(sPos);
            }
            if (ePos < 0) {
                eUnit = new NaviUnit();
                eUnit.point = ep;
                units.add(eUnit);
            } else {
                eUnit = units.get(ePos);
            }
            NaviPath naviPath = new NaviPath();
            naviPath.unit = eUnit;
            naviPath.pData = e.getPathData();
            naviPath.distance = (float) getDistance(sUnit.point, eUnit.point);
            if (e instanceof Road) {
                Road temp = (Road) e;
                switch (temp.getPassLevel()) {
                    case NORMAL:
                        naviPath.types.add(SearchType.NORMAL);
                    case VEHICLE:
                        naviPath.types.add(SearchType.MOTOR);
                    case NON_MOTOR:
                        naviPath.types.add(SearchType.NON_MOTOR);
                    case PEOPLE:
                        naviPath.types.add(SearchType.WALK);
                    case PROHIBIT:
                        break;
                }
                if (!temp.getAttributes().contains(Road.RoadAttribute.ONEWAY)) {
                    NaviPath tempPath = new NaviPath(naviPath);
                    tempPath.unit = sUnit;
                    eUnit.outPaths.add(tempPath);
                }
            } else {
                naviPath.types.add(SearchType.NORMAL);
                naviPath.types.add(SearchType.MOTOR);
                naviPath.types.add(SearchType.NON_MOTOR);
                naviPath.types.add(SearchType.WALK);
                NaviPath tempPath = new NaviPath(naviPath);
                tempPath.unit = sUnit;
                eUnit.outPaths.add(tempPath);
            }
            sUnit.outPaths.add(naviPath);
        }
    }

    public List<Path> searchRoute(SearchType type, String sId, String eId) {
        SearchNode minCost = null;
        if (units != null) {
            queue.clear();
            int sPos = searchPointInUnits(sId);
            int ePos = searchPointInUnits(eId);
            SearchNode node = new SearchNode(units.size());
            node.posList.add(sPos);
            node.traverseList[sPos] = true;
            try {
                pushNode(node, type);
                while (!queue.isEmpty()) {
                    SearchNode temp = queue.remove();
                    if (temp.posList.get(temp.posList.size() - 1) == ePos) {
                        if (minCost == null) {
                            minCost = temp;
                        } else if (minCost.cost > temp.cost) {
                            minCost.destroy();
                            minCost = temp;
                        } else {
                            temp.destroy();
                        }
                    } else {
                        pushNode(temp, type);
                    }
                }
            } catch (CloneNotSupportedException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            }
        }
        if (minCost != null) {
            List<Path> paths = new ArrayList<>();
            Point sp;
            Point ep;
            int size = minCost.posList.size();
            for (int i = 0; i < size - 1; i++) {
                sp = units.get(minCost.posList.get(i)).point;
                ep = units.get(minCost.posList.get(i + 1)).point;
                LinePath e = new LinePath(UUID.randomUUID().toString().replace("-", ""));
                e.setStartPoint(sp);
                e.setEndPoint(ep);
                e.setPathData(minCost.pathDataList.get(i));
                e.setPathAttribute(LinePath.PathAttribute.NAVI);
                paths.add(e);
            }
            minCost.destroy();
            return paths;
        }
        return null;
    }

    private void pushNode(SearchNode searchNode, SearchType type)
            throws CloneNotSupportedException {
        NaviUnit unit = units.get(searchNode.posList.get(searchNode.posList.size() - 1));
        for (NaviPath e :
                unit.outPaths) {
            if (e.types.contains(type)) {
                int uPos = searchUnitInUnits(e.unit);
                if (!searchNode.traverseList[uPos]) {
                    SearchNode temp = (SearchNode) searchNode.clone();
                    temp.cost += e.distance;
                    temp.posList.add(uPos);
                    temp.pathDataList.add(e.pData);
                    temp.traverseList[uPos] = true;
                    queue.add(temp);
                }
            }
        }
    }

    private int searchUnitInUnits(NaviUnit unit) {
        int size = units.size();
        for (int i = 0; i < size; i++) {
            if (units.get(i) == unit) {
                return i;
            }
        }
        return -1;
    }

    private int searchPointInUnits(String id) {
        int size = units.size();
        for (int i = 0; i < size; i++) {
            if (units.get(i).point.getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    private int pointExistInUnits(Point p) {
        int size = units.size();
        for (int i = 0; i < size; i++) {
            if (p == units.get(i).point) {
                return i;
            }
        }
        return -1;
    }

    public void setSource(MapSrc mapSrc) {
        this.mapSrc = mapSrc;
    }

    class SearchNode {
        List<Integer> posList;
        List<String> pathDataList;
        boolean[] traverseList;
        float cost;

        SearchNode(int size) {
            posList = new ArrayList<>();
            pathDataList = new ArrayList<>();
            traverseList = new boolean[size];
            for (int i = 0; i < size; i++) {
                traverseList[i] = false;
            }
            cost = 0f;
        }

        void destroy() {
            pathDataList.clear();
            posList.clear();
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            SearchNode searchNode = new SearchNode(traverseList.length);
            searchNode.posList.addAll(this.posList);
            searchNode.pathDataList.addAll(this.pathDataList);
            searchNode.traverseList = this.traverseList.clone();
            searchNode.cost = this.cost;
            return searchNode;
        }
    }

    class NaviUnit {
        Point point;
        List<NaviPath> outPaths;

        NaviUnit() {
            outPaths = new ArrayList<>();
        }
    }

    class NaviPath {
        NaviUnit unit;
        List<SearchType> types;
        float distance;
        String pData;

        NaviPath() {
            types = new ArrayList<>();
        }

        NaviPath(NaviPath naviPath) {
            this();
            this.unit = naviPath.unit;
            this.types.addAll(naviPath.types);
            this.distance = naviPath.distance;
            this.pData = naviPath.pData;
        }
    }
}
