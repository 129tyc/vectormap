package com.tyc129.vectormap.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import com.tyc129.vectormap.NaviAnalyzer;
import com.tyc129.vectormap.VectorMap;
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
    public interface OnClickPointListener {
        /**
         * 当地图目标被点击时触发函数
         *
         * @param id 地图目标id
         */
        void onClickPoint(String id);
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
    private static final float CLICK_SENSITIVITY_DEFAULT = 20;
    private boolean isAnimating;
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
    private boolean showTags;
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
     * 地图的最大放比例
     */
    private float maxScale;
    /**
     * 地图的最小缩放比例
     * 不得小于重缩放时按地图填充属性缩放的比例
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
    private Render render;
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
    private OnClickPointListener listener;
    /**
     * 临时Canvas,用于向临时Bitmap绘制地图资源
     */
    private Canvas tempCanvas;
    /**
     * 临时Bitmap,用于保存地图画面,并转绘到主Canvas上
     */
    private Bitmap tempBitmap;
    private MapAnimateListener animateListener;

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
        Log.i(LOG_TAG, "initialize!");
        initView(context, attrs);
    }

    /**
     * 加载地图目标点击监听器
     *
     * @param listener 需加载的监听器
     */
    public void setOnClickPointListener(OnClickPointListener listener) {
        this.listener = listener;
    }

    public void setRender(Render render) {
        this.render = render;
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
        this.mapWidth = (int) this.mapSrc.getWidth();
        this.mapHeight = (int) this.mapSrc.getHeight();
        if (this.mapSrc.getUnit() == MapSrc.MetricUnit.DP) {
            this.mapWidth *= context.getResources().getDisplayMetrics().density;
            this.mapHeight *= context.getResources().getDisplayMetrics().density;
        }
        mapBox.set(0, 0, mapWidth, mapHeight);
//        initVariables();
        // TODO: 2017/5/1 0001 检查是否可能在加载其他地图时发生混乱
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
        initVariables();
        getAttributes(context, attrs);
    }

    /**
     * 初始化特殊变量
     */
    private void initVariables() {
        controlDeg = 0f;
        controlScale = 1f;
        mainMatrix = new Matrix();
        isAnimating = false;
    }

    /**
     * 从View定义的XML中获取属性
     *
     * @param context 定义View的上下文参数
     * @param attrs   属性集
     */
    private void getAttributes(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.VectorMapView);
        try {

            showTags = typedArray.getBoolean(R.styleable.VectorMapView_showTags, false);
            allowTranslate = typedArray.getBoolean(R.styleable.VectorMapView_allowTranslate, false);
            allowScale = typedArray.getBoolean(R.styleable.VectorMapView_allowScale, false);
            allowClick = typedArray.getBoolean(R.styleable.VectorMapView_allowClick, false);
            allowRotate = typedArray.getBoolean(R.styleable.VectorMapView_allowRotate, false);
            allowBreakBoundary = typedArray.getBoolean(R.styleable.VectorMapView_allowBreakBoundary, false);
            mapFillType = Integer.parseInt(typedArray.getString(R.styleable.VectorMapView_mapFillType));
            maxScale = typedArray.getFloat(R.styleable.VectorMapView_maxScale, MAX_SCALE_DEFAULT);
            minScale = typedArray.getFloat(R.styleable.VectorMapView_minScale, MIN_SCALE_DEFAULT);
            scaleOneTap = typedArray.getFloat(R.styleable.VectorMapView_scaleOneTap, SCALE_ONETAP_DEFAULT);
            clickSensitivity = typedArray.getFloat(R.styleable.VectorMapView_clickSensitivity, CLICK_SENSITIVITY_DEFAULT);
            originalX = typedArray.getFloat(R.styleable.VectorMapView_originalPositionX, 0f);
            originalY = typedArray.getFloat(R.styleable.VectorMapView_originalPositionY, 0f);
            originalDeg = typedArray.getFloat(R.styleable.VectorMapView_originalRotate, 0f);
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
        if (mapSrc != null) {
            tempBitmap.eraseColor(0);
            tempCanvas.save();
            tempCanvas.setMatrix(mainMatrix);
            if (isAnimating) {
                render.render2CanvasWithoutTags(tempCanvas, mainMatrix, controlDeg, controlScale);
            } else {
                render.renderAll2Canvas(tempCanvas, mainMatrix, controlDeg, controlScale);
            }
            tempCanvas.restore();
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
        float scaleX = width * (1.0f) / mapWidth;
        float scaleY = height * (1.0f) / mapHeight;
//        float centreX = originalX;
//        float centerY = originalY;
        switch (mapFillType) {
            case FILL_TYPE_MAX: {
                controlScale = Math.max(scaleX, scaleY);
                if (minScale < controlScale) {
                    minScale = controlScale;
                }
                break;
            }
            case FILL_TYPE_MIN: {
                controlScale = Math.min(scaleX, scaleY);
                if (minScale < controlScale) {
                    minScale = controlScale;
                }
                break;
            }
            case FILL_TYPE_DEF: {
                controlScale = Math.min(scaleX, scaleY);
                controlScale += maxScale;
                controlScale /= 2;
                minScale = Math.max(scaleX, scaleY);
                break;
            }
        }
        mainMatrix.postScale(controlScale, controlScale, originalX, originalY);
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
            mapPoint[0] = cerX;
            mapPoint[1] = cerY;
            controlDeg += rDeg;
            mainMatrix.mapPoints(mapPoint);
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
                controlScale * scale < maxScale && controlScale * scale > minScale) {
            controlScale *= scale;
            mapPoint[0] = cerX;
            mapPoint[1] = cerY;
            mainMatrix.mapPoints(mapPoint);
            if (allowBreakBoundary || scale > 1) {
                mainMatrix.postScale(scale, scale, mapPoint[0], mapPoint[1]);
            } else {
                checkBoundaryScale(mainMatrix, scale, cerX, cerY);
            }
            invalidate();
        }
    }

    void mapTranslateAndRotateAnimation(final float xMinus,
                                        final float yMinus,
                                        final float degMinus, float cerX, float cerY,
                                        long duration) {
        boolean needRotate = false;
    }

    void mapTranslateAnimation(final float xMinus, final float yMinus, long duration) {
        mapTranslateAndRotateAnimation(xMinus, yMinus, 0f, 0f, 0f, duration);
    }

    void mapScaleAnimation(final float scale, final float cerX, final float cerY, long duration) {
        if (!isAnimating) {
            isAnimating = true;
            tempMatrix.set(mainMatrix);
            controlScale *= scale;
            ValueAnimator animator;
            if (scale > 1) {
                animator = ValueAnimator.ofFloat(1, scale);
            } else {
                animator = ValueAnimator.ofFloat(scale, 1);
            }
            animator.setDuration(duration);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    float value = (float) valueAnimator.getAnimatedValue();
                    mainMatrix.set(tempMatrix);
                    mainMatrix.postScale(value, value, cerX, cerY);
                    invalidate();
                }
            });
            animator.addListener(animateListener);
            animator.start();
        }
    }

    /**
     * 地图方位重置
     * 不会恢复为XML文件定义的原始样式，而是最小缩放的样式
     * 自带界面重绘
     */
    public void mapReset() {
        mainMatrix.reset();
        controlScale = minScale;
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
            listener.onClickPoint(id);
            return true;
        }
        return false;
    }

    /**
     * 通过id设置地图显示位置
     * <b>不进行相对旋转</b>
     *
     * @param id 点或路径id
     * @return 是否定位成功
     */
    public boolean setViewById(String id) {
        return setViewById(id, 0f);
    }

    /**
     * 通过id和旋转角设置地图显示位置
     *
     * @param id     点或路径id
     * @param rotate 地图旋转角（相对）
     * @return 是否定位成功
     */
    public boolean setViewById(String id, float rotate) {

        return true;
    }

    /**
     * 通过地图点的位置坐标设置地图
     * <b>不进行相对旋转</b>
     *
     * @param vx 地图点的x轴坐标点
     * @param vy 地图点的y轴坐标点
     * @return 是否定位成功
     */
    public boolean setViewByPos(float vx, float vy) {
        return setViewByPos(vx, vy, 0f);
    }

    /**
     * 通过地图点的位置坐标及旋转角设置地图
     *
     * @param vx     地图点的x轴坐标点
     * @param vy     地图点的y轴坐标点
     * @param rotate 旋转角（相对）
     * @return 是否定位成功
     */
    public boolean setViewByPos(float vx, float vy, float rotate) {
        mapPoint[0] = vx;
        mapPoint[1] = vy;
        mainMatrix.mapPoints(mapPoint);
        vx = mapPoint[0];
        vy = mapPoint[1];
        mapPoint[0] = halfWidth;
        mapPoint[1] = halfHeight;
        mainMatrix.mapPoints(mapPoint);
        float xMinus = vx - mapPoint[0];
        float yMinus = vy - mapPoint[1];

        return true;
    }

    void setMapFillType(@MapFillType int mapFillType) {
        this.mapFillType = mapFillType;
    }

    public float getMinScale() {
        return minScale;
    }

    public float getMaxScale() {
        return maxScale;
    }

    public void setMaxScale(float maxScale) {
        this.maxScale = maxScale;
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

    class MapAnimateListener implements Animator.AnimatorListener {

        @Override
        public void onAnimationStart(Animator animator) {
            isAnimating = true;
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            isAnimating = false;
            invalidate();
        }

        @Override
        public void onAnimationCancel(Animator animator) {
            isAnimating = false;
            invalidate();
        }

        @Override
        public void onAnimationRepeat(Animator animator) {
            isAnimating = true;
        }
    }
}
