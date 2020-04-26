package com.alpha.acamera;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.alpha.turbojpeg.TurboJpegJni;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE = 1;

    private Button mBtnOpenCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        configureLayout();
        initPermission();
        initBreakpad();
    }

    private void initBreakpad() {
        BreakpadUtil.initExternalReportPath();
        BreakpadUtil.initBreakPad(this);
    }

    private void initPermission() {
        String[] allPermissions = new String[]{Manifest.permission.CAMERA};

        List<String> requestList = new ArrayList<>();

        for (String permission : allPermissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                requestList.add(permission);
            }
        }

        if (requestList.size() > 0) {
            ActivityCompat.requestPermissions(this,
                    requestList.toArray(new String[0]), REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult: success");
            } else {
                Toast.makeText(this, "请授予应用权限后使用", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void configureLayout() {
        mBtnOpenCamera = findViewById(R.id.btn_open_camera);
        mBtnOpenCamera.setOnClickListener((View v) -> {
            TurboJpegJni jni = new TurboJpegJni();
            jni.tjInitCompress();
            openCamera();
        });
    }

    private void openCamera() {
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }
}
