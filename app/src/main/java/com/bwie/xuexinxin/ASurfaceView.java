package com.bwie.xuexinxin;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * date:2018/11/5
 * author:薛鑫欣(吧啦吧啦)
 * function:
 */
public class ASurfaceView extends SurfaceView implements SurfaceHolder.Callback,Runnable {
    private SurfaceHolder mHolder;
    private Thread mThread;
    private Canvas mCanvas;

    private boolean isRunning;
    public ASurfaceView(Context context) {
        this(context,null);
    }

    public ASurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mHolder= getHolder();

        mHolder.addCallback(this);

        //可获得焦点
        setFocusable(true);
        setFocusableInTouchMode(true);
        //设置常亮
        setKeepScreenOn(true);

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isRunning=true;
        //开启线程
        mThread=new Thread(this);
        mThread.start();

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
            draw();
         }
    }

    private void draw() {
        try {
            mCanvas=mHolder.lockCanvas();
            if(mCanvas!=null){

            }
        } catch (Exception e){

        }finally {
            mHolder.unlockCanvasAndPost(mCanvas);//绘制完成销毁
        }
    }
}
