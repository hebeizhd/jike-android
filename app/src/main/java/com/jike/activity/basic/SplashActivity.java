package com.jike.activity.basic;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.jike.R;
import com.jike.application.JikeApplication;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 启动页
 *
 * Created by liang on 2016/3/30.
 */
public class SplashActivity extends BaseActivity {
    /**
     * 启动页图片
     */
    @Bind(R.id.imageViewStart)
    ImageView imageViewStart;


    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initView();
    }

    private void initView() {
        ButterKnife.bind(this);
        //启动页停留1.5秒 做一些准备操作
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                JikeApplication.setStatusColor(Color.parseColor("#18b4ed"));
                Intent intent = new Intent(SplashActivity.this, WebLoader.class);
                startActivity(intent);
                finish();
            }
        }, 1500);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
