package com.bwie.xuexinxin;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.zip.Deflater;

/**
 * date:2018/11/5
 * author:薛鑫欣(吧啦吧啦)
 * function:
 */
public class LuckyPan extends SurfaceView implements SurfaceHolder.Callback,Runnable {
    private SurfaceHolder mHolder;
    private Thread mThread;
    private Canvas mCanvas;
    private boolean isRunning;
    public MyListener mMyListener;
    //开始设置字体
    private String[] mTexts=new String[]{"谢谢参与","一等奖","二等奖","三等奖","四等奖","参与奖"};
    //设置颜色


    private Bitmap mBgBitmap=BitmapFactory.decodeResource(getResources(),R.mipmap.bg2);
    //设置画笔
    private Paint mArcPaint;
    private Paint mTextPaint;
    //设置盘块数量
    private int count=6;
    //设置字体
    private float mTextSize=TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,20,getResources().getDisplayMetrics());


  //范围
    private RectF mRange=new RectF();
    //设置中心点
    private int mCenter;
    //直径
    private int mRedius;
    private int mPadding;

    //设置速度
    private int mSpeed=0;
    private volatile int mStartAngle=0;
    private boolean isShouldEnd;
    public int [] mColors;
    public LuckyPan(Context context) {
        this(context,null);
    }

    public LuckyPan(Context context, AttributeSet attrs) {
        super(context, attrs);
        mHolder= getHolder();

        mHolder.addCallback(this);

        //可获得焦点
        setFocusable(true);
        setFocusableInTouchMode(true);
        //设置常亮
        setKeepScreenOn(true);

        TypedArray a=context.obtainStyledAttributes(attrs,R.styleable.LuckyPan);
       mColors=new int[]{a.getColor(R.styleable.LuckyPan_red,Color.RED),
               a.getColor(R.styleable.LuckyPan_yellow,Color.YELLOW),
               a.getColor(R.styleable.LuckyPan_red,Color.RED),
               a.getColor(R.styleable.LuckyPan_yellow,Color.YELLOW),
               a.getColor(R.styleable.LuckyPan_red,Color.RED),
               a.getColor(R.styleable.LuckyPan_yellow,Color.YELLOW),
               a.getColor(R.styleable.LuckyPan_red,Color.RED)
        };

    }
  //开始绘制
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //边长
        int width=Math.min(getMeasuredWidth(),getMeasuredHeight());
        mPadding=getPaddingLeft();
        //直径
        mRedius=width-mPadding*2;
        //中心点
        mCenter=width/2;
        setMeasuredDimension(width,width);



    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isRunning=true;
        //开启线程
        mThread=new Thread(this);
        mThread.start();

        //初始化盘块画笔
        mArcPaint=new Paint();
        mArcPaint.setAntiAlias(true);
        mArcPaint.setDither(true);

        //初始化文本画笔
        mTextPaint=new Paint();
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(mTextSize);
        mRange=new RectF(mPadding,mPadding,mPadding+mRedius,mPadding+mRedius);


    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
      isRunning=false;
    }

    @Override
    public void run() {

        //不断绘制
         while (isRunning){
             long start=System.currentTimeMillis();

            draw();
            long end=System.currentTimeMillis();
            if(end-start<50){
                try {
                    //如果提前绘制完成，那么让他休息
                    Thread.sleep(50-(end-start));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
         }
    }

    private void draw() {
        try {
            mCanvas=mHolder.lockCanvas();
            if(mCanvas!=null){
               //绘制背景
               drawBg();

                int tmpAngle=mStartAngle;
                int sweepAngle=360/count;
                for (int i = 0; i <count ; i++) {
                    //绘制颜色
                    mArcPaint.setColor(mColors[i]);
                    mCanvas.drawArc(mRange,tmpAngle,sweepAngle,true,mArcPaint);
                    drawText(tmpAngle,sweepAngle,mTexts[i]);

                    tmpAngle+=sweepAngle;
                }
                //画圆
               // mCanvas.drawCircle(getMeasuredWidth()/2,getMeasuredWidth()/2,100,mTextPaint);
                //速度
               mStartAngle+=mSpeed;
                //如果点击停止按钮，速度递减
                if(isShouldEnd){
                    mSpeed-=1;
                }
                if(mSpeed<=0){
                    mSpeed=0;//速度设置为0
                    isShouldEnd=false;
                    mMyListener.isEnd(isShouldEnd);
                }
            }
        } catch (Exception e){

        }finally {
            mHolder.unlockCanvasAndPost(mCanvas);//绘制完成销毁
        }
    }
    //是否按下开始
    public void luckyStart(){
        mSpeed=50;
        isShouldEnd=false;
        mMyListener.isEnd(isShouldEnd);
    }
    //是否按下结束
    public void luckyEnd(){
        isShouldEnd=true;
        mMyListener.isEnd(isShouldEnd);
    }
    //判断是否开始
    public boolean isStart(){
        return mSpeed!=0;
    }
    public boolean isShouldEnd(){

        return isShouldEnd;
    }
    //接口回调
    public interface MyListener{
       void isEnd(boolean isShouldEnd);
    }


    public void setMyListener(MyListener myListener) {
        mMyListener = myListener;
    }

    //绘制文本
    private void drawText(int tmpAngle, int sweepAngle, String text) {
        Path path=new Path();
        path.addArc(mRange,tmpAngle,sweepAngle);

        float widthtext=mTextPaint.measureText(text);
        int hOffsset= (int) ((mRedius*Math.PI/count/2)-widthtext/2);
        int vOffset=mRedius/2/6;
        mCanvas.drawTextOnPath(text,path,hOffsset,vOffset,mTextPaint);
    }

    //绘制背景
    private void drawBg() {
        mCanvas.drawColor(Color.WHITE);
        //背景图片
        //mCanvas.drawBitmap(mBgBitmap,null,new RectF(mPadding/2,mPadding/2,getMeasuredWidth()-mPadding/2
        //,getMeasuredHeight()-mPadding/2),null);
    }
}
