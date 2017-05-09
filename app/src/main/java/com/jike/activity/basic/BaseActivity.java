package com.jike.activity.basic;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.jike.R;
import com.jike.application.ActivityController;
import com.jike.utils.Constants;
import com.orm.SugarContext;

import java.util.Date;

/**
 * Created by liang on 2016/3/30.
 */
public class BaseActivity extends AppCompatActivity {
    protected long mExitTime;

    public static final int REQUEST_SELECT_FILE = 1000;
    public static final int REQUEST_SELECT_FILE_SINGLE = 1001;
    public static final int REQUEST_CAMREA = 2000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        init();
    }
    public String getTag(){
        return this.getClass().getSimpleName();
    }
    protected void init(){
        ActivityController.addActivity(this);
        //初始化各个数据库表
        SugarContext.init(this);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constants.START_REQUEST_CODE_LOGIN){
            if(resultCode == Constants.SUCCESS_INTEGER_CODE){
                showShortToastMessage("登陆成功");
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
                return true;
            } else {
                ActivityController.finishAll();
                System.exit(0);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void finish() {
        super.finish();
        ActivityController.removeActivity(this);
    }
    public void showShortToastMessage(String context) {
        Toast.makeText(this, context, Toast.LENGTH_SHORT).show();
    }

    public void showLongToastMessage(String context) {
        Toast.makeText(this, context, Toast.LENGTH_LONG).show();
    }
    /**
     * 请等待弹出
     */
    private Dialog waitDialog;
    /**
     * 打开请等待窗口
     */
    public  void showWaitDialog() {

        final long startTime = new Date().getTime();

        waitDialog = new Dialog(this, R.style.WaitDialogStyle);
        LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View waitView = layoutInflater.inflate(R.layout.loading_dialog, null);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        params.alpha = 0.0F;
        waitDialog.addContentView(waitView, params);

        //请等待时，不能返回键取消
        waitDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                long endTime = new Date().getTime();
                if ((endTime - startTime) > 5000) {
                    closeWaitDialog();
                }
                switch (keyCode) {
                    case KeyEvent.KEYCODE_HOME:
                        return true;
                    case KeyEvent.KEYCODE_BACK:
                        return true;
                    case KeyEvent.KEYCODE_CALL:
                        return true;
                    case KeyEvent.KEYCODE_SYM:
                        return true;
                    case KeyEvent.KEYCODE_VOLUME_DOWN:
                        return true;
                    case KeyEvent.KEYCODE_VOLUME_UP:
                        return true;
                    case KeyEvent.KEYCODE_STAR:
                        return true;
                }

                return false; //默认返回 false
            }
        });

        waitDialog.show();
    }

    /**
     * 关闭请等待窗口
     */
    public  void closeWaitDialog() {
        if(null != waitDialog) {
            waitDialog.cancel();
            waitDialog = null;
        }
    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
