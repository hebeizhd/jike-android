package com.jike.activity.basic;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.jike.R;
import com.jike.entity.QueryParam;
import com.jike.utils.Constants;
import com.jike.utils.File2Code;
import com.jike.utils.FileUtils;
import com.jike.utils.HttpUtils;
import com.jike.utils.PermissionCheck;
import com.jike.utils.QueryTask;

import java.io.File;
import java.text.SimpleDateFormat;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * web页面加载器
 * Created by Administrator on 2016/4/15.
 */
public class WebLoader extends BaseActivity {
    @Bind(R.id.mWebView)
    WebView webView;
    private String url;

    private String mCameraFilePath;

    private ValueCallback<Uri> fileCallbackSingle;
    private ValueCallback<Uri[]> fileCallback;

    private String callbackKey="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_loader);

        initView();
        Intent intent = getIntent();
        if (intent != null) {
            WebSettings webSettings = webView.getSettings();
            //设置WebView属性，能够执行Javascript脚本
            webSettings.setJavaScriptEnabled(true);
            //设置可以访问文件
            webSettings.setAllowFileAccess(true);
            //设置支持缩放
            webSettings.setBuiltInZoomControls(false);
            webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

            /***打开本地缓存提供JS调用**/
            webSettings.setDomStorageEnabled(true);
            // Set cache size to 8 mb by default. should be more than enough
            webSettings.setAppCacheMaxSize(1024*1024*8);
            // This next one is crazy. It's the DEFAULT location for your app's cache
            // But it didn't work for me without this line.
            // UPDATE: no hardcoded path. Thanks to Kevin Hawkins
            String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
            webSettings.setAppCachePath(appCachePath);
            webSettings.setAppCacheEnabled(true);

            String ua = webSettings.getUserAgentString();
            webSettings.setUserAgentString(ua+":jikeWeb");

            //屏蔽长按粘贴复制
            webView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    return true;
                }
            });
            reloadPage();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initView() {
        ButterKnife.bind(this);

    }

    private void reloadPage() {
        webView.loadUrl(Constants.getUrl("/#/index"));
        //设置Web视图
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("tel:")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                } else if (url.startsWith("camera:")) {
                    callbackKey = url.substring(9);
                    WebLoader.this.openCamera();
                } else if (url.startsWith("chooser:")) {
                    callbackKey = url.substring(10);
                    WebLoader.this.selectImage();
                } else if (url.startsWith("http:") || url.startsWith("https:")) {
                    view.loadUrl(url);
                }
                Log.i("TAG","callbackKey="+callbackKey);
                return true;
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }
        });
    }
    @Override
    //设置回退
    //覆盖Activity类的onKeyDown(int keyCoder,KeyEvent event)方法
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack(); //goBack()表示返回WebView的上一页面
            return true;
        }
        Log.i("TEST-BACK", "返回键监听");
        return super.onKeyDown(keyCode,event);
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webView.clearHistory();

            ((ViewGroup) webView.getParent()).removeView(webView);
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(resultCode == RESULT_OK){
            if (requestCode == REQUEST_CAMREA) {
                uploadImage(mCameraFilePath);
            } else if (requestCode == REQUEST_SELECT_FILE) {
                Uri uri = intent.getData();
                Uri uri1 = Uri.parse(uri.toString().replace("%3A","/"));
                Log.i("TAG","path ========="+getRealPathFromURI(this, uri1));
                uploadImage(getRealPathFromURI(this,uri1));
            }
        }else{
            pushSelectResult("",callbackKey);
        }
    }

    public void uploadImage(String path){
        //上传图片QueryTask
        String imageData = File2Code.bitmapToBase64(path);
        QueryParam queryParam = new QueryParam();
        queryParam.put("imageData",imageData);
        new QueryTask<>(uploadPhotoHandler,"oss/upload/image/base64",String.class).execute(queryParam);
    }

    private QueryTask.CallBack<String> uploadPhotoHandler = new QueryTask.CallBack<String>() {
        @Override
        public void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        public void onCancelExecute() {
            super.onCancelExecute();
        }

        @Override
        public void onSuccess(String s) {
            pushSelectResult(HttpUtils.getImageUrl(s), callbackKey);
        }

        @Override
        public void onFail(String msg) {
            super.onFail(msg);
            pushSelectResult("",callbackKey);
        }
    };


    public String getRealPathFromURI( final Context context,final Uri uri) {
        if ( null == uri ) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if ( scheme == null )
            data = uri.getPath();
        else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
            data = uri.getPath();
        } else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
            String[] proj = { MediaStore.Images.Media.DATA };
            CursorLoader loader = new CursorLoader(context, uri, proj, null, null, null);
            Cursor cursor = loader.loadInBackground();
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            data= cursor.getString(column_index);
        }
        return data;
    }

    public static Uri getImageContentUri(Context context, java.io.File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    public String openCamera(){
        PermissionCheck.checkPermission(this, PermissionCheck.CAMERA_PERMISSION_TYPE, REQUEST_CAMREA);
        return "true";
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode == REQUEST_CAMREA){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){

                //獲取系統版本
                int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                // 激活相机
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // 判断存储卡是否可以用，可用进行存储
                String state = Environment.getExternalStorageState();

                if (state.equals(Environment.MEDIA_MOUNTED)) {
                    String filePath = FileUtils.getCameraPhotoPath();
                    this.mCameraFilePath = filePath;
                    File tempFile = new File(filePath);
                    if (currentapiVersion < 24) {
                        // 从文件中创建uri
                        Uri uri = Uri.fromFile(tempFile);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    } else {
                        //兼容android7.0 使用共享文件的形式
                        ContentValues contentValues = new ContentValues(1);
                        contentValues.put(MediaStore.Images.Media.DATA, tempFile.getAbsolutePath());
                        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    }
                } else {
                    showLongToastMessage("请确认已经插入SD卡");
                }
                // 开启一个带有返回值的Activity，请求码为REQUEST_CAMREA
                startActivityForResult(intent, REQUEST_CAMREA);

            }
        }else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public String selectImage(){
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_SELECT_FILE);
        return "true";
    }
    public String pushSelectResult(String url,String key){
//        String imageData = File2Code.bitmapToBase64(url);
//        Log.i("TAG","imageData==========="+imageData);
        webView.loadUrl("javascript:onFileSelected('" + url + "','" + key + "')");
        return null;
    }
}
