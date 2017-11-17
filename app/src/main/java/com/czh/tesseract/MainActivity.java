package com.czh.tesseract;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.InputStream;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    TessBaseAPI mTessBaseAPI;
    String rootDirPath = MyApplication.mContext.getDir("TesseractDemo", Context.MODE_PRIVATE).getAbsolutePath();
    String dirPath = rootDirPath + "/tessdata";
    String FILENAME = "chi_sim.traineddata";
    Button btnRecognize;
    ImageView imgPhoto;
    Bitmap testBitmap;
    TextView tvResult;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnRecognize = findViewById(R.id.btnRecognize);
        imgPhoto = findViewById(R.id.imgPhoto);
        btnRecognize.setOnClickListener(this);
        tvResult = findViewById(R.id.tvResult);
        initBitmap();
        initTessApi();

    }

    private void initTessApi() {
        mTessBaseAPI = new TessBaseAPI();
        showLoading("正在初始化", false);
        Observable.just(true)
                .map(new Func1<Boolean, Boolean>() {
                    @Override
                    public Boolean call(Boolean aBoolean) {
                        File dir = new File(dirPath);
                        if (!dir.exists()) {
                            dir.mkdirs();
                        }
                        String filePath = dirPath + File.separator + FILENAME;
                        File file = new File(filePath);
                        if (!file.exists()) {
                            FileUtils.CopyAssets(getApplicationContext(), FILENAME, filePath);
                        }
                        return mTessBaseAPI.init(rootDirPath + File.separator, "chi_sim", TessBaseAPI.OEM_DEFAULT);
                    }
                })
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean initSuccess) {
                cancelLoading();
                log(initSuccess ? "初始化成功" : "初始化失败");
                append(initSuccess ? "初始化成功" : "初始化失败");
            }
        });
    }

    private void initBitmap() {
        Observable.just(true)
                .map(new Func1<Boolean, Bitmap>() {
                    @Override
                    public Bitmap call(Boolean aBoolean) {
                        InputStream inputStream = getClass().getResourceAsStream("/assets/testchinese.png");
                        testBitmap = BitmapFactory.decodeStream(inputStream);
                        return testBitmap;
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Bitmap>() {
                    @Override
                    public void call(Bitmap sdf) {
                        imgPhoto.setImageBitmap(sdf);
                    }
                });


    }

    private void log(String content) {
        Log.e("hahahaha", content);

    }

    private void append(String content) {
        tvResult.append(content + "\n");
    }

    Toast t;
    private void toast(String content) {
        log(content);
        if (t == null) {
            t = Toast.makeText(this, content, Toast.LENGTH_SHORT);
        } else {
            t.setText(content);
        }
        t.show();
    }

    public void showLoading(String msg, boolean cancelable) {
        cancelLoading();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog = new ProgressDialog(this);
        } else {
            dialog = new ProgressDialog(this);
        }
        dialog.setMessage(msg);
        dialog.setCancelable(cancelable);
        dialog.show();

    }

    public void cancelLoading() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnRecognize:
                if (mTessBaseAPI != null) {
                    showLoading("recognizing...", false);
                    Observable.just(true)
                            .flatMap(new Func1<Boolean, Observable<String>>() {
                                @Override
                                public Observable<String> call(Boolean aBoolean) {
                                    mTessBaseAPI.setImage(testBitmap);
                                    return Observable.just(mTessBaseAPI.getUTF8Text());
                                }
                            })
                            .subscribeOn(Schedulers.computation())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action1<String>() {
                                @Override
                                public void call(String result) {
                                    cancelLoading();
                                    log(result);
                                    append(result);
                                }
                            });
                }
                break;
        }
    }
}



