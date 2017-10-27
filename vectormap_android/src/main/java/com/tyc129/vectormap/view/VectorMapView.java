package com.tyc129.vectormap.view;

import android.animation.Animator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import com.tyc129.vectormap.struct.MapSrc;
import com.tyc129.vectormap_android.R;

/**
 * 矢量地图显示模块
 * 面向用户使其可以操控
 * Created by Code on 2017/5/15 0015.
 *
 * @author 谈永成
 * @version 1.0
 */
public class VectorMapView extends View {
    /**
     * 地图目标点击监听器接口
     */
    public interface OnMapActionOccurListener {
        /**
         * 当地图目标被点击时触发函数
         *
         * @param id     地图目标id
         * @param touchX 触摸点X轴位置
         * @param touchY 触摸点Y轴位置
         */
        void onClickPoint(String id, float touchX, float touchY);

        void onClickMap(float touchX, float touchY);

        void onDoubleTapMap();

        void onTranslateMap(float dx, float dy);

        void onRotateMap(float deg, float rx, float ry);

        void onScaleMap(float scale, float sx, float sy);

        void onTransformStart();

        void onTransformEnd();
    }

    public enum CompassStyle {
        WHOLE,
        INDEPENDENCE
    }

    /**
     * 默认填充
     */
    static final int FILL_TYPE_DEF = 0;
    /**
     * 最小填充
     */
    static final int FILL_TYPE_MIN = 1;
    /**
     * 最大填充
     */
    static final int FILL_TYPE_MAX = 2;
    /**
     * Log标签
     */
    private static final String LOG_TAG = "VectorMapView";
    /**
     * 默认最大缩放比
     */
    private static final float MAX_SCALE_DEFAULT = 3;
    /**
     * 默认最小缩放比
     */
    private static final float MIN_SCALE_DEFAULT = 0;
    /**
     * 一次放大的比例默认值
     */
    private static final float SCALE_ONETAP_DEFAULT = 1;
    /**
     * 默认点击敏感度
     */
    private static final float CLICK_SENSITIVITY_DEFAULT = 36;
    /**
     * 默认地图填充类型
     */
    private static final int MAP_FILL_TYPE_DEFAULT = 0;
    /**
     * 是否处于动画中
     */
    private boolean isTransforming;
    /**
     * 允许地图旋转
     */
    private boolean allowRotate;
    /**
     * 允许地图移动
     */
    private boolean allowTranslate;
    /**
     * 是否显示标签
     */
    private boolean allowShowTags;
    /**
     * 允许地图缩放
     */
    private boolean allowScale;
    /**
     * 允许点击地图目标
     */
    private boolean allowClick;
    /**
     * 允许突破边界
     * 即不进行边界检查
     */
    private boolean allowBreakBoundary;
    /**
     * 地图宽
     */
    private int mapWidth;
    /**
     * 地图高
     */
    private int mapHeight;
    /**
     * 视窗的宽
     */
    private int viewPortWidth;
    /**
     * 视窗的高
     */
    private int viewPortHeight;
    /**
     * 地图填充类型，一定程度上决定了最小缩放比
     */
    private int mapFillType;
    /**
     * 原始地图X轴位置（X轴位移）
     */
    private float originalX;
    /**
     * 原始地图Y轴位置（Y轴位移）
     */
    private float originalY;
    /**
     * 原始旋转角
     */
    private float originalDeg;
    /**
     * 控制缩放比
     * 为绝对缩放比例
     * 负责保持地图中图标或标签大小不变
     */
    private float controlScale;
    /**
     * 控制旋转角
     * 为绝对旋转角度
     * 负责保持地图中图标或标签方向不变
     */
    private float controlDeg;
    /**
     * View的一半宽
     */
    private float halfWidth;
    /**
     * View的一半高
     */
    private float halfHeight;
    /**
     * 地图的实际最大缩放比例
     */
    private float maxScaleActually;
    /**
     * 地图的最大缩放比例
     */
    private float maxScale;
    /**
     * 地图的实际最小缩放比例
     * 不得小于重缩放时按地图填充属性缩放的比例
     */
    private float minScaleActually;
    /**
     * 地图的最小缩放比例
     */
    private float minScale;
    /**
     * 一次放大的比例
     */
    private float scaleOneTap;
    /**
     * 点击敏感度
     * 以手指点击中心为圆心，点击敏感度为半径的圆算作触碰区域
     * 在点击地图目标时使用
     */
    private float clickSensitivity;
    /**
     * 坐标映射数组
     */
    private float[] mapPoint;
    /**
     * 实例化View的上下文参数
     */
    private Context context;
    /**
     * 矢量地图资源
     */
    private MapSrc mapSrc;
    /**
     * 地图渲染器，负责渲染地图数据
     */
    private MapRender mapRender;
    /**
     * 主地图矩阵，负责主地图显示
     */
    private Matrix mainMatrix;
    /**
     * 临时矩阵，负责临时替代主地图矩阵进行矩阵运算
     */
    private Matrix tempMatrix;
    /**
     * 手势解析器，负责解析手势动作并产生地图变化
     */
    private GestureParser gestureParser;
    /**
     * 手势检测器，负责地图Touch事件的解析
     */
    private GestureDetector gestureDetector;
    /**
     * 包含地图的矩形，负责边界检查
     */
    private RectF mapBox;
    /**
     * 包含地图显示模块View的矩形，负责边界检查
     */
    private RectF viewBox;
    /**
     * 临时矩形，负责边界检查时的映射
     */
    private RectF tmpRectF;
    /**
     * 地图目标点击监听器，负责回调
     */
    private OnMapActionOccurListener listener;
    /**
     * 临时Canvas,用于向临时Bitmap绘制地图资源
     */
    private Canvas tempCanvas;
    /**
     * 临时Bitmap,用于保存地图画面,并转绘到主Canvas上
     */
    private Bitmap tempBitmap;
    private MapAnimateListener animateListener;
    private Bitmap currentNarrow;
    private float narrowHWidth;
    private float narrowHHeight;
    private SensorManager manager;
    private CompassListener compassListener;
    private Sensor aSensor;
    private Sensor mSensor;
    private float[] aValues;
    private float[] mValues;
    private float[] oValues;
    private float narrowPosX;
    private float narrowPosY;
    private CompassStyle compassStyle;
    private boolean isIndicating;
    private int currentDirection;
    //private Paint backPaint;
    private ValueAnimator tfAnimator;

    private boolean isSelecting;
    private Bitmap placeHolder;
    private float currSelectX;
    private float currSelectY;
    private float holderPaintOffsetX;
    private float holderPaintOffsetY;

    @IntDef({
            FILL_TYPE_DEF,
            FILL_TYPE_MAX,
            FILL_TYPE_MIN
    })
    @interface MapFillType {
    }

    public VectorMapView(Context context) {
        this(context, null);
    }

    public VectorMapView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VectorMapView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
        Log.v(LOG_TAG, "map view initialized!");
    }

    public void setPlaceHolder(Bitmap placeHolder) {
        this.placeHolder = placeHolder;
        if (this.placeHolder != null) {
            holderPaintOffsetX = placeHolder.getWidth() >> 1;
            holderPaintOffsetY = placeHolder.getHeight();
        }
    }

    public void selectPosition(float x, float y) {
        if (isSelecting)
            cancelSelection();
        if (placeHolder == null || x < 0 || y < 0)
            return;
        isSelecting = true;
        currSelectX = x;
        currSelectY = y;
        invalidate();
    }

    public void cancelSelection() {
        isSelecting = false;
        invalidate();
    }

    public void setDirectionNarrow(Bitmap currentNarrow) {
        this.currentNarrow = currentNarrow;
        if (this.currentNarrow != null) {
            narrowHWidth = currentNarrow.getWidth() >> 1;
            narrowHHeight = currentNarrow.getHeight() >> 1;
        }
    }

    public boolean startDirectionIndicate(CompassStyle compassStyle, float cerX, float cerY) {
        if (manager != null &&
                currentNarrow != null &&
                compassStyle != null &&
                !isIndicating) {
            mapPoint[0] = cerX;
            mapPoint[1] = cerY;
            mainMatrix.mapPoints(mapPoint);
            mainMatrix.mapRect(tmpRectF, mapBox);
            if (tmpRectF.contains(mapPoint[0], mapPoint[1])) {
                this.compassStyle = compassStyle;
                isIndicating = true;
                narrowPosX = cerX;
                narrowPosY = cerY;
                manager.registerListener(compassListener, aSensor, SensorManager.SENSOR_DELAY_NORMAL);
                manager.registerListener(compassListener, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
                return true;
            }
        }
        return false;
    }

    public void stopDirectionIndicate() {
        if (manager != null) {
            manager.unregisterListener(compassListener);
        }
        isIndicating = false;
    }


    private void calculateOrientation() {
        float[] r = new float[9];
        SensorManager.getRotationMatrix(r, null, aValues, mValues);
        SensorManager.getOrientation(r, oValues);
        int nextDeg = Math.round((float) Math.toDegrees(oValues[0]));
        if ((nextDeg ^ currentDirection) < 0) {
            nextDeg = nextDeg < 0 ? 360 + nextDeg : 360 - nextDeg;
        }
        if (Math.abs(currentDirection - nextDeg) >= 3) {
            if (this.compassStyle == CompassStyle.WHOLE) {
                mapRotate(nextDeg - currentDirection, narrowPosX, narrowPosY);
            }
            currentDirection = nextDeg;
            invalidate();
        }
    }

    /**
     * 加载地图目标点击监听器
     *
     * @param listener 需加载的监听器
     */
    public void setOnMapActionOccurListener(OnMapActionOccurListener listener) {
        Log.v(LOG_TAG, "map view set listener");
        this.listener = listener;
        if (gestureParser != null)
            gestureParser.setListener(listener);
    }

    public void setMapRender(MapRender mapRender) {
        this.mapRender = mapRender;
    }

    /**
     * 将地图资源加载至View中
     * <b>需手动重绘界面</b>
     *
     * @param mapSrc 待加载的矢量地图资源
     */
    public void setMapRecourse(MapSrc mapSrc) {
        if (mapSrc != null) {
            this.mapSrc = mapSrc;
        } else
            throw new NullPointerException("VectorMap is NULL!");
        Log.v(LOG_TAG, "set map recourse");
        if (isTransforming && tfAnimator != null && tfAnimator.isRunning()) {
            tfAnimator.cancel();
        }
        initVariables();
        this.mapWidth = (int) this.mapSrc.getWidth();
        this.mapHeight = (int) this.mapSrc.getHeight();
        if (this.mapSrc.getUnit() == MapSrc.MetricUnit.DP) {
            this.mapWidth *= context.getResources().getDisplayMetrics().density;
            this.mapHeight *= context.getResources().getDisplayMetrics().density;
        }
        mapBox.set(0, 0, mapWidth, mapHeight);
        if (viewPortHeight > 0 && viewPortWidth > 0) {
            rescaleMatrix(viewPortWidth, viewPortHeight);
        }
    }

    /**
     * 初始化View
     * 包含成员域初始化、属性获取和特殊变量初始化
     *
     * @param context 定义View的上下文参数
     * @param attrs   属性集
     */
    private void initView(Context context, AttributeSet attrs) {
        this.context = context;
        tempCanvas = new Canvas();
        tempMatrix = new Matrix();
        viewBox = new RectF();
        mapBox = new RectF();
        tmpRectF = new RectF();
        animateListener = new MapAnimateListener();
        gestureParser = new GestureParser(this);
        gestureDetector = new GestureDetector(getContext(), gestureParser);
        mapSrc = null;
        originalX = 0f;
        originalY = 0f;
        originalDeg = 0f;
        halfWidth = 0f;
        halfHeight = 0f;
        mapPoint = new float[2];
        isIndicating = false;
        //backPaint = new Paint();
        if (this.context != null && !isInEditMode()) {
            manager = (SensorManager) this.context.getSystemService(Context.SENSOR_SERVICE);
            if (manager != null) {
                aSensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                mSensor = manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
                compassListener = new CompassListener();
                aValues = new float[3];
                mValues = new float[3];
                oValues = new float[3];
            }
        }
        initVariables();
        getAttributes(context, attrs);
    }

    /**
     * 初始化特殊变量
     */
    private void initVariables() {
        minScaleActually = minScale;
        maxScaleActually = maxScale;
        controlDeg = 0f;
        controlScale = 1f;
        mainMatrix = new Matrix();
        isTransforming = false;
        tfAnimator = null;
    }

    /**
     * 从View定义的XML中获取属性
     *
     * @param context 定义View的上下文参数
     * @param attrs   属性集
     */
    private void getAttributes(Context context, AttributeSet attrs) {
        Log.v(LOG_TAG, "set attributes");
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.VectorMapView);
        try {
            allowShowTags = typedArray.getBoolean(R.styleable.VectorMapView_showTags, false);
            allowTranslate = typedArray.getBoolean(R.styleable.VectorMapView_allowTranslate, false);
            allowScale = typedArray.getBoolean(R.styleable.VectorMapView_allowScale, false);
            allowClick = typedArray.getBoolean(R.styleable.VectorMapView_allowClick, false);
            allowRotate = typedArray.getBoolean(R.styleable.VectorMapView_allowRotate, false);
            allowBreakBoundary = typedArray.getBoolean(R.styleable.VectorMapView_allowBreakBoundary, false);
            mapFillType = typedArray.getInt(R.styleable.VectorMapView_mapFillType, MAP_FILL_TYPE_DEFAULT);
            maxScale = typedArray.getFloat(R.styleable.VectorMapView_maxScale, MAX_SCALE_DEFAULT);
            minScale = typedArray.getFloat(R.styleable.VectorMapView_minScale, MIN_SCALE_DEFAULT);
            scaleOneTap = typedArray.getFloat(R.styleable.VectorMapView_scaleOneTap, SCALE_ONETAP_DEFAULT);
            clickSensitivity = typedArray.getFloat(R.styleable.VectorMapView_clickSensitivity, CLICK_SENSITIVITY_DEFAULT);
            originalX = typedArray.getFloat(R.styleable.VectorMapView_originalPositionX, 0f);
            originalY = typedArray.getFloat(R.styleable.VectorMapView_originalPositionY, 0f);
            originalDeg = typedArray.getFloat(R.styleable.VectorMapView_originalRotate, 0f);
            maxScaleActually = maxScale;
            minScaleActually = minScale;

            controlDeg += originalDeg;

            mapRotate(originalDeg, originalX, originalY);
            mapTranslate(originalX, originalY);

        } finally {
            if (typedArray != null) {
                typedArray.recycle();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        gestureDetector.onTouchEvent(event);
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mapRender != null) {
            tempBitmap.eraseColor(0);
            //tempCanvas.save();
            tempCanvas.setMatrix(mainMatrix);
            if (isTransforming) {
                mapRender.render2CanvasWithoutTags(tempCanvas, mainMatrix, controlDeg, controlScale);
            } else {
                mapRender.renderAll2Canvas(tempCanvas, mainMatrix, controlDeg, controlScale);
                if (isIndicating && this.compassStyle == CompassStyle.INDEPENDENCE) {
                    mapRender.renderBitmap2Canvas(tempCanvas, mainMatrix, currentNarrow,
                            currentDirection, controlDeg + 45, narrowPosX, narrowPosY,
                            controlScale, narrowHWidth, narrowHHeight);
                }
                if (isSelecting) {
                    mapRender.renderBitmap2Canvas(tempCanvas, mainMatrix, placeHolder,
                            0, controlDeg, currSelectX, currSelectY, controlScale,
                            holderPaintOffsetX, holderPaintOffsetY);
                }
            }
            //tempCanvas.restore();
            //canvas.drawBitmap(tempBitmap, 0, 0, backPaint);
            canvas.drawBitmap(tempBitmap, 0, 0, null);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewPortWidth = w - getPaddingLeft() - getPaddingRight();
        viewPortHeight = h - getPaddingTop() - getPaddingBottom();
        halfWidth = viewPortWidth >> 1;
        halfHeight = viewPortHeight >> 1;
        if (tempBitmap != null && !tempBitmap.isRecycled()) {
            tempBitmap.recycle();
        }
        tempBitmap = Bitmap.createBitmap(viewPortWidth, viewPortHeight, Bitmap.Config.ARGB_8888);
        tempCanvas.setBitmap(tempBitmap);
        viewBox.set(0, 0, viewPortWidth, viewPortHeight);
        if (mapSrc != null) {
            rescaleMatrix(viewPortWidth, viewPortHeight);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(widthSize, heightSize);
    }

    /**
     * 边界检查移动
     *
     * @param matrix 当前矩阵
     * @param x      X轴移动距离
     * @param y      Y轴移动距离
     */
    private void checkBoundaryTranslate(Matrix matrix, float x, float y) {
        matrix.postTranslate(x, 0);
        matrix.mapRect(tmpRectF, mapBox);
        if (!tmpRectF.contains(viewBox)) {
            matrix.postTranslate(-x, 0);
        }
        matrix.postTranslate(0, y);
        matrix.mapRect(tmpRectF, mapBox);
        if (!tmpRectF.contains(viewBox)) {
            matrix.postTranslate(0, -y);
        }
    }

    /**
     * 边界检查缩放
     *
     * @param matrix 当前矩阵
     * @param scale  需缩放比例（相对量）
     * @param x      缩放中心点X轴位置
     * @param y      缩放中心点轴位置
     */
    private void checkBoundaryScale(Matrix matrix, float scale, float x, float y) {
        tempMatrix.set(matrix);
        tempMatrix.postScale(scale, scale, x, y);
        tempMatrix.mapRect(tmpRectF, mapBox);
        if (!tmpRectF.contains(viewBox)) {
            matrix.mapRect(tmpRectF, mapBox);
            x = tmpRectF.right - viewBox.right < viewBox.left - tmpRectF.left ?
                    tmpRectF.right : tmpRectF.left;
            y = viewBox.top - tmpRectF.top < tmpRectF.bottom - viewBox.bottom ?
                    tmpRectF.top : tmpRectF.bottom;
            matrix.postScale(scale, scale, x, y);
        } else {
            matrix.set(tempMatrix);
        }
    }

    /**
     * 重缩放矩阵
     * 根据填充类型将地图缩放至合适尺寸
     * 若属性中定义的最小比例小于填充时缩放的比例
     * 则将最小比例设为填充时缩放的比例
     * 原最小比例无效
     *
     * @param width  显示的View宽度
     * @param height 显示的View高度
     */
    private void rescaleMatrix(int width, int height) {
        Log.v(LOG_TAG, "rescale map--->" +
                width + "," + height);
        float scaleX = width * (1.0f) / mapWidth;
        float scaleY = height * (1.0f) / mapHeight;
        float tempScale = controlScale;
        switch (mapFillType) {
            case FILL_TYPE_MAX: {
                controlScale = Math.max(scaleX, scaleY);
                if (minScaleActually < controlScale) {
                    minScaleActually = controlScale;
                }
                break;
            }
            case FILL_TYPE_MIN: {
                controlScale = Math.min(scaleX, scaleY);
                if (minScaleActually < controlScale) {
                    minScaleActually = controlScale;
                }
                break;
            }
            case FILL_TYPE_DEF: {
                controlScale = Math.min(scaleX, scaleY);
                controlScale += maxScaleActually;
                controlScale /= 2;
                minScaleActually = Math.max(scaleX, scaleY);
                break;
            }
        }
        mainMatrix.postScale(controlScale / tempScale, controlScale / tempScale, originalX, originalY);
    }

    /**
     * 地图刷新
     * 重绘界面
     */
    public void mapRefresh() {
        invalidate();
    }

    /**
     * 地图移动
     * 自带界面重绘
     *
     * @param tx X轴移动距离
     * @param ty Y轴移动距离
     */
    public void mapTranslate(float tx, float ty) {
        if (allowTranslate) {
            if (listener != null)
                listener.onTranslateMap(tx, ty);
            if (allowBreakBoundary) {
                mainMatrix.postTranslate(tx, ty);
            } else {
                checkBoundaryTranslate(mainMatrix, tx, ty);
            }
            invalidate();
        }
    }

    /**
     * 地图旋转
     * 自带界面重绘
     *
     * @param rDeg 旋转角度（°）
     * @param cerX 旋转中心点X轴位置（相对于屏幕）
     * @param cerY 旋转中心点Y轴位置（相对于屏幕）
     */
    public void mapRotate(float rDeg, float cerX, float cerY) {
        if (allowRotate) {
            // TODO: 2017/5/1 0001 需要添加边界检测函数
            if (listener != null)
                listener.onRotateMap(rDeg, cerX, cerY);
            mapPoint[0] = cerX;
            mapPoint[1] = cerY;
            controlDeg += rDeg;
            mainMatrix.postRotate(rDeg, mapPoint[0], mapPoint[1]);
            invalidate();
        }

    }

    /**
     * 地图旋转，默认中心点为地图中央
     * 自带界面重绘
     *
     * @param rDeg 旋转角度（°）
     */
    public void mapRotate(float rDeg) {
        mapRotate(rDeg, halfWidth, halfHeight);
    }

    /**
     * 地图缩放，默认中心点为屏幕中央
     * 自带界面重绘
     *
     * @param scale 缩放比例
     */
    public void mapScale(float scale) {
        mapScale(scale, halfWidth, halfHeight);
    }

    /**
     * 地图缩放
     * 包含缩放边界控制和缩放比例限制
     * 自带界面重绘
     *
     * @param scale 缩放比例
     * @param cerX  缩放中心点X轴位置（相对于屏幕坐标）
     * @param cerY  缩放中心点Y轴位置（相对于屏幕坐标）
     */
    public void mapScale(float scale, float cerX, float cerY) {
        if (allowScale &&
                controlScale * scale < maxScaleActually && controlScale * scale > minScaleActually) {
            if (listener != null)
                listener.onScaleMap(scale, cerX, cerY);
            controlScale *= scale;
            mapPoint[0] = cerX;
            mapPoint[1] = cerY;
            if (allowBreakBoundary || scale > 1) {
                mainMatrix.postScale(scale, scale, mapPoint[0], mapPoint[1]);
            } else {
                checkBoundaryScale(mainMatrix, scale, cerX, cerY);
            }
            invalidate();
        }
    }

    public void transformAnimation(float xMinus, float yMinus,
                                   float scale, final float sCerX, final float sCerY,
                                   float rotate, final float rCerX, final float rCerY,
                                   long duration) {
        if (!isTransforming && allowTranslate && allowScale && allowRotate &&
                !(rotate == 0 && scale == 1 && xMinus == 0 && yMinus == 0)) {
            final float tempDeg = controlDeg;
            final float tempScale = controlScale;
            isTransforming = true;
            tempMatrix.set(mainMatrix);
            if (controlScale * scale > maxScaleActually) {
                scale = maxScaleActually / controlScale;
            } else if (controlScale * scale < minScaleActually) {
                scale = minScaleActually / controlScale;
            }
            if (rotate > 180) {
                rotate -= 360;
            } else if (rotate < -180)
                rotate += 360;
            //controlDeg += rotate;
            //controlScale *= scale;
            tfAnimator = ValueAnimator.ofObject(new TransformEvaluator(),
                    new TransformF().set(0f, 0f, 1f, 0f),
                    new TransformF().set(xMinus, yMinus, scale, rotate));
            tfAnimator.setDuration(duration);
            tfAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            tfAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    controlDeg = tempDeg;
                    controlScale = tempScale;
                    mainMatrix.set(tempMatrix);
                    TransformF f = (TransformF) valueAnimator.getAnimatedValue();
                    controlDeg += f.rotateDeg;
                    controlScale *= f.scale;
                    mainMatrix.postTranslate(f.x, f.y);
                    mainMatrix.postScale(f.scale, f.scale, sCerX, sCerY);
                    mainMatrix.postRotate(f.rotateDeg, rCerX, rCerY);
                    invalidate();
                }
            });
            tfAnimator.addListener(animateListener);
            tfAnimator.start();
        } else {
            if (tfAnimator != null && tfAnimator.isRunning()) {
                tfAnimator.cancel();
            }
        }
    }

    public void mapRotateAnimation(final float degMinus, final float cerX, final float cerY, long duration) {
        transformAnimation(0, 0, 1, 0, 0, degMinus, cerX, cerY, duration);
    }

    public void mapTranslateAnimation(final float xMinus, final float yMinus, long duration) {
        transformAnimation(xMinus, yMinus, 1, 0, 0, 0, 0, 0, duration);
    }

    public void mapScaleAnimation(final float scale, final float cerX, final float cerY, long duration) {
        transformAnimation(0, 0, scale, cerX, cerY, 0, 0, 0, duration);
    }

    /**
     * 地图方位重置
     * 不会恢复为XML文件定义的原始样式，而是最小缩放的样式
     * 自带界面重绘
     */
    public void mapReset() {
        mainMatrix.reset();
        controlScale = 1;
        controlDeg = 0f;
        rescaleMatrix(viewPortWidth, viewPortHeight);
        invalidate();
    }

    /**
     * 将点击屏幕的坐标修正为地图上的坐标
     * 并找到重叠对应的兴趣点
     *
     * @param orgX 原始坐标X
     * @param orgY 原始坐标Y
     * @return 找到对应点返回真，未找到或未设置监听器返回假
     */
    boolean getIdByPos(float orgX, float orgY) {
        mapPoint[0] = 0;
        mapPoint[1] = 0;
        tempMatrix.set(mainMatrix);
        tempMatrix.postRotate(-controlDeg, orgX, orgY);
        tempMatrix.mapPoints(mapPoint);
        mapPoint[0] = orgX - mapPoint[0];
        mapPoint[1] = orgY - mapPoint[1];
        mapPoint[0] /= controlScale;
        mapPoint[1] /= controlScale;
        String id = this.mapSrc.findPointByPos(mapPoint[0], mapPoint[1], clickSensitivity);
        if (id != null && listener != null) {
            listener.onClickPoint(id, orgX, orgY);
            return true;
        }
        return false;
    }

    /**
     * 将地图的某点定位到屏幕中央并放大到相应比例
     * <b>自带动画</b>
     *
     * @param x     地图上点的X轴位置
     * @param y     地图上点的Y轴位置
     * @param scale 缩放比例
     */
    public void locateToCenter(float x, float y, float scale, float deg, long duration) {
        locateTo(x, y, halfWidth, halfHeight, scale, deg, duration);
    }

    /**
     * 将地图的某点定位到屏幕某位置并放大到相应比例
     * <b>自带动画</b>
     *
     * @param x     地图上点的X轴位置
     * @param y     地图上点的Y轴位置
     * @param sx    屏幕上点的X轴位置
     * @param sy    屏幕上点的Y轴位置
     * @param scale 缩放比例
     */
    public void locateTo(float x, float y,
                         float sx, float sy, float scale, float deg, long duration) {
        float ms = scale / controlScale;
        float mr = deg - controlDeg;
        mapPoint[0] = x;
        mapPoint[1] = y;
        mainMatrix.mapPoints(mapPoint);
        float mx = sx - mapPoint[0];
        float my = sy - mapPoint[1];
        transformAnimation(mx, my, ms, sx, sy, mr, sx, sy, duration);
    }

    void setMapFillType(@MapFillType int mapFillType) {
        this.mapFillType = mapFillType;
    }

    public float getMinScaleActually() {
        return minScaleActually;
    }

    public float getCurrRotation() {
        return controlDeg;
    }

    public float getMaxScaleActually() {
        return maxScaleActually;
    }

    public void setMaxScaleActually(float maxScaleActually) {
        this.maxScaleActually = maxScaleActually;
    }

    public boolean isAllowRotate() {
        return allowRotate;
    }

    public void setAllowRotate(boolean allowRotate) {
        this.allowRotate = allowRotate;
    }

    public boolean isAllowTranslate() {
        return allowTranslate;
    }

    public void setAllowTranslate(boolean allowTranslate) {
        this.allowTranslate = allowTranslate;
    }

    public boolean isAllowScale() {
        return allowScale;
    }

    public void setAllowScale(boolean allowScale) {
        this.allowScale = allowScale;
    }

    public boolean isAllowClick() {
        return allowClick;
    }

    public void setAllowClick(boolean allowClick) {
        this.allowClick = allowClick;
    }

    public float getScaleOneTap() {
        return scaleOneTap;
    }

    public void setScaleOneTap(float scaleOneTap) {
        this.scaleOneTap = scaleOneTap;
    }

    private class MapAnimateListener implements Animator.AnimatorListener {

        @Override
        public void onAnimationStart(Animator animator) {
            if (listener != null)
                listener.onTransformStart();
            isTransforming = true;
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            if (listener != null)
                listener.onTransformEnd();
            isTransforming = false;
            invalidate();
        }

        @Override
        public void onAnimationCancel(Animator animator) {
            if (listener != null)
                listener.onTransformEnd();
            isTransforming = false;
            invalidate();
        }

        @Override
        public void onAnimationRepeat(Animator animator) {
            if (listener != null)
                listener.onTransformStart();
            isTransforming = true;
        }
    }

    private class TransformF {
        private float x;
        private float y;
        private float scale;
        private float rotateDeg;

        TransformF() {
        }

        TransformF set(TransformF transformF) {
            setX(transformF.x);
            setY(transformF.y);
            setScale(transformF.scale);
            setRotateDeg(transformF.rotateDeg);
            return this;
        }

        public float getRotateDeg() {
            return rotateDeg;
        }

        TransformF setRotateDeg(float rotateDeg) {
            this.rotateDeg = rotateDeg;
            return this;
        }

        TransformF set(float x, float y) {
            setX(x);
            setY(y);
            return this;
        }

        TransformF set(float x, float y, float scale) {
            setX(x);
            setY(y);
            setScale(scale);
            return this;
        }

        TransformF set(float x, float y, float scale, float rotateDeg) {
            setX(x);
            setY(y);
            setScale(scale);
            setRotateDeg(rotateDeg);
            return this;
        }

        float getX() {
            return x;
        }

        TransformF setX(float x) {
            this.x = x;
            return this;
        }

        float getY() {
            return y;
        }

        TransformF setY(float y) {
            this.y = y;
            return this;
        }

        float getScale() {
            return scale;
        }

        TransformF setScale(float scale) {
            this.scale = scale;
            return this;
        }
    }

    private class TransformEvaluator implements TypeEvaluator<TransformF> {
        TransformF f = new TransformF();

        @Override
        public TransformF evaluate(float v, TransformF transformF, TransformF t1) {
            float x = transformF.x;
            float x1 = t1.x;
            float y = transformF.y;
            float y1 = t1.y;
            float s = transformF.scale;
            float s1 = t1.scale;
            float r = transformF.rotateDeg;
            float r1 = t1.rotateDeg;
            f.set(x + (x1 - x) * v, y + (y1 - y) * v, s + (s1 - s) * v, r + (r1 - r) * v);
            return f;
        }
    }

    private class CompassListener implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            switch (sensorEvent.sensor.getType()) {
                case Sensor.TYPE_MAGNETIC_FIELD:
                    mValues = sensorEvent.values;
                    break;
                case Sensor.TYPE_ACCELEROMETER:
                    aValues = sensorEvent.values;
                    break;
            }
            calculateOrientation();
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    }
}
