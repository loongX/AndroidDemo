package com.googu.a30809.wave;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by 30809 on 2018/6/4.
 */

public class WaveView extends View {
    String TAG = "WaveView";
    private  AttributeSet attrs;
    //View的宽高
    private int width;
    private int height;
    //View的画笔
    private Paint wavePaint;
    private Paint textPaint;
    private Paint circlePaint;

    Paint mPaint ;

    Bitmap bgBitmap;
    private int backgroundId;//背景图片的资源ID

    //波浪的路径
    private Path path;
    //sin曲线的长度：一个周期长度
    private int cycle = 160;
    //每次平移的长度，为四分之一个周期
    private int translateX = cycle / 4;
    //sin曲线振幅的高度
    private int waveHeight = 10;
    //sin曲线的起点坐标
    private Point startPoint;
    //当前波浪的进度
    private int progress = 0;
    //当前波浪的速度
    private int waveSpeech = 150;
    //是否启用了自动增长进度
    private boolean isAutoIncrease = false;

    public WaveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.attrs = attrs;

        //无命名空间测试(未添加到attrs)
        //获取的属性值为字符串
        String testAttr=attrs.getAttributeValue(null,"testAttr");
        Log.d(TAG, "MyToggleButton: testAttr= "+testAttr);

        //获取自定义属性
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.WaveView);
        int count = ta.getIndexCount();
        for (int i = 0; i < count; i++) {
            //获取某个属性的ID
            int itemId = ta.getIndex(i);
            switch (itemId) {
                case R.styleable.WaveView_wv_background:
                    backgroundId = ta.getResourceId(itemId, -1);
                    Log.d(TAG, "MyToggleButton: backgroundId= "+backgroundId);
                    if (backgroundId==-1){
                        throw new RuntimeException("please set the imgRec of background");
                    }
                    bgBitmap= BitmapFactory.decodeResource(getResources(), backgroundId);
                    break;
            }
        }

        initPaint(context);
    }

    public WaveView(Context context) {
        super(context);
        initPaint(context);
    }

    public WaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);

        //无命名空间测试(未添加到attrs)
        //获取的属性值为字符串
        String testAttr=attrs.getAttributeValue(null,"testAttr");
        Log.d(TAG, "MyToggleButton: testAttr= "+testAttr);

        //获取自定义属性
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.WaveView);
        int count = ta.getIndexCount();
        for (int i = 0; i < count; i++) {
            //获取某个属性的ID
            int itemId = ta.getIndex(i);
            switch (itemId) {
                case R.styleable.WaveView_wv_background:
                    backgroundId = ta.getResourceId(itemId, -1);
                    Log.d(TAG, "MyToggleButton: backgroundId= "+backgroundId);
                    if (backgroundId==-1){
                        throw new RuntimeException("please set the imgRec of background");
                    }
                    bgBitmap= BitmapFactory.decodeResource(getResources(), backgroundId);
                    break;
            }
        }

        initPaint(context);
    }



    private void initPaint(Context context) {
        path = new Path();

        wavePaint = new Paint();
        wavePaint.setAntiAlias(true);
        wavePaint.setStyle(Paint.Style.FILL);
        wavePaint.setColor(Color.parseColor("#1998FA"));

        circlePaint = new Paint();
        circlePaint.setStrokeWidth(5);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.parseColor("#1998FA"));

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(50);
        textPaint.setColor(Color.BLUE);

        mPaint = new Paint();
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //由于是一个圆形，所以取的值是宽高的最小值
        width = measureSize(400, widthMeasureSpec);
        height = measureSize(400, heightMeasureSpec);
        width = Math.min(width, height);
        height = Math.min(width, height);
        setMeasuredDimension(width, height);
        //初始化起点，为屏幕外的一个周期
        startPoint = new Point(-cycle * 4, 0);
    }

    /**
     * 测量宽高
     *
     * @param defaultSize
     * @param measureSpec
     * @return
     */
    private int measureSize(int defaultSize, int measureSpec) {
        int result = defaultSize;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        switch (mode) {
            case MeasureSpec.UNSPECIFIED:
                result = defaultSize;
                break;
            case MeasureSpec.AT_MOST:
            case MeasureSpec.EXACTLY:
                result = size;
                break;
        }
        return result;
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(bgBitmap, 0, 0, mPaint); //画背景图片
        //设置内间距
        setPadding(20, 20, 20, 20);
        //裁剪画布为圆形
        clipCicle(canvas);
        //绘制圆形边框
        drawCicleBorder(canvas);
        //绘制波浪区域
        drawWavePath(canvas);
        //绘制进度文字
        drawProcessText(canvas);

        //自动增长进度
        if (isAutoIncrease) {
            if (progress >= 100) {
                progress = 0;
            } else {
                progress++;
            }
        }
//        Bitmap mBackGround  = ((BitmapDrawable) this.getResources().getDrawable(R.drawable.background)).getBitmap(); //获取背景图片


        //更新UI
        postInvalidateDelayed(waveSpeech);
    }

    /**
     * 裁剪画布为圆形
     *
     * @param canvas
     */
    private void clipCicle(Canvas canvas) {
        Path circlePath = new Path();
        circlePath.addCircle(width / 2, height / 2, width / 2, Path.Direction.CW);
        canvas.clipPath(circlePath);

    }

    /**
     * 绘制圆形边框
     *
     * @param canvas
     */
    private void drawCicleBorder(Canvas canvas) {
        canvas.drawPaint(circlePaint);
        canvas.drawCircle(width / 2, height / 2, width / 2, circlePaint);
    }

    /**
     * 绘制波浪区域
     *
     * @param canvas
     */
    private void drawWavePath(Canvas canvas) {
        //根据进度改变起点坐标的y值
        startPoint.y = (int) ((1 - (progress / 100.0)) * (height / 2 + width / 2));
        //移动区域起点
        path.moveTo(startPoint.x, startPoint.y);
        int j = 1;
        //循环绘制正弦曲线区域，循环两个周期
        for (int i = 1; i <= 8; i++) {
            if (i % 2 == 0) {
                //波峰
                path.quadTo(startPoint.x + (cycle * j), startPoint.y + waveHeight,
                        startPoint.x + (cycle * 2) * i, startPoint.y);
            } else {
                //波谷
                path.quadTo(startPoint.x + (cycle * j), startPoint.y - waveHeight,
                        startPoint.x + (cycle * 2) * i, startPoint.y);
            }
            j += 2;
        }
        //绘制封闭的区域
        path.lineTo(width, height);//右下角
        path.lineTo(startPoint.x, height);//左下角
        path.lineTo(startPoint.x, startPoint.y);//起点
        path.close();
        //绘制区域
        canvas.drawPath(path, wavePaint);
        path.reset();
        //一开始的起点是在-160，160 = 40 + 40 + 40 + 40，走完一个周期则回到原点
        if (startPoint.x + translateX >= 0) {
            startPoint.x = -cycle * 4;
        } else {
            startPoint.x += translateX;
        }
    }

    /**
     * 绘制进度文字
     *
     * @param canvas
     */
    private void drawProcessText(Canvas canvas) {
        //画布的大小
        Rect targetRect = new Rect(0, 0, width, height);
        Paint.FontMetricsInt fontMetrics = textPaint.getFontMetricsInt();
        int baseline = (targetRect.bottom + targetRect.top - fontMetrics.bottom - fontMetrics.top) / 2;
        // 下面这行是实现水平居中，drawText对应改为传入targetRect.centerX()
        textPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(progress + "%", targetRect.centerX(), baseline, textPaint);
    }

    /**
     * 开启自动增长
     */
    public void startIncrease() {
        isAutoIncrease = true;
        invalidate();
    }

    /**
     * 设置当前进度
     *
     * @param progress 进度
     */
    public void setProgress(int progress) {
        if (progress > 100 || progress < 0) {
            throw new RuntimeException(getClass().getName() + "请设置[0,100]之间的值");
        }

        this.progress = progress;
        invalidate();
    }

    /**
     * 通过动画设置当前进度
     *
     * @param targetProcess 进度 <=100
     * @param duration      动画时长
     */
    public void setProgress(final int targetProcess, int duration) {
        if (progress > 100 || progress < 0) {
            throw new RuntimeException(getClass().getName() + "请设置[0,100]之间的值");
        }

        ValueAnimator progressAnimator = ValueAnimator.ofInt(progress, targetProcess);
        progressAnimator.setDuration(duration);
        progressAnimator.setTarget(progress);
        progressAnimator.setInterpolator(new LinearInterpolator());
        progressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setProgress((Integer) animation.getAnimatedValue());
            }
        });
        progressAnimator.start();
    }

    /**
     * 获取当前进度
     *
     * @return
     */
    public int getProgress() {
        return progress;
    }


}
