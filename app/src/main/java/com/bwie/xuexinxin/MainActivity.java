package com.bwie.xuexinxin;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private LuckyPan luck;
    private TextView ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setSupportActionBar(toolbar);

        //显示导航按钮
        toolbar.setNavigationIcon(R.drawable.a);

       toolbar.setNavigationOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               finish();
           }
       });


    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        luck = (LuckyPan) findViewById(R.id.luck);

        ok = (TextView) findViewById(R.id.ok);
        ok.setOnClickListener(this);
    }

    //点击事件
    @Override
    public void onClick(View v) {
          if(!luck.isStart()){//转盘不旋转
              luck.luckyStart();
              ok.setText("stop");
          } else {//转盘旋转
              if(!luck.isShouldEnd()){//没有按下停止按钮
                   luck.luckyEnd();//让他停止
                  ok.setText("start");
              } else {//已经按下停止按钮
                  ok.setText("start");
              }
          }
    }
}
