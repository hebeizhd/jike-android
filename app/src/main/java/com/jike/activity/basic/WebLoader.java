package com.jike.activity.basic;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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
import com.jike.utils.QueryTask;

import java.io.File;

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

            //The undocumented magic method override
            //Eclipse will swear at you if you try to put @Override here
            // For Android 3.0+
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {

                fileCallbackSingle = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                WebLoader.this.startActivityForResult(Intent.createChooser(i, "选择图片"), WebLoader.this.REQUEST_SELECT_FILE_SINGLE);

            }

            // For Android 3.0+
            public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
                fileCallbackSingle = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                WebLoader.this.startActivityForResult(Intent.createChooser(i, "选择图片"), WebLoader.this.REQUEST_SELECT_FILE_SINGLE);
            }

            //For Android 4.1
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                fileCallbackSingle = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                WebLoader.this.startActivityForResult(Intent.createChooser(i, "选择图片"), WebLoader.this.REQUEST_SELECT_FILE_SINGLE);

            }

            //For Android 5.0
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                // make sure there is no existing message
                if (fileCallback != null) {
                    fileCallback.onReceiveValue(null);
                    fileCallback = null;
                }
                fileCallback = filePathCallback;
                Intent intent = fileChooserParams.createIntent();
                try {
                    WebLoader.this.startActivityForResult(intent, WebLoader.this.REQUEST_SELECT_FILE);
                } catch (ActivityNotFoundException e) {
                    WebLoader.this.fileCallback = null;
                    return false;
                }
                return true;
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
                uploadImage(getRealPathFromURI(uri));
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


    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
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


    private Intent createDefaultOpenableIntent() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
        Intent chooser = createChooserIntent(createCameraIntent());
        chooser.putExtra(Intent.EXTRA_INTENT, i);
        return chooser;
    }
    private Intent createChooserIntent(Intent... intents) {
        Intent chooser = new Intent(Intent.ACTION_CHOOSER);
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intents);
        chooser.putExtra(Intent.EXTRA_TITLE, "选择图片");
        return chooser;
    }
    @SuppressWarnings("static-access")
    private Intent createCameraIntent() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String mCameraFilePath = FileUtils.getCameraPhotoPath();
        this.mCameraFilePath = mCameraFilePath;
        cameraIntent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(mCameraFilePath)));
        return cameraIntent;
    }

    public String openCamera(){
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String mCameraFilePath = FileUtils.getCameraPhotoPath();
        this.mCameraFilePath = mCameraFilePath;
        cameraIntent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(mCameraFilePath)));
        startActivityForResult(cameraIntent, REQUEST_CAMREA);
        return "true";
    }
    public String selectImage(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
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
