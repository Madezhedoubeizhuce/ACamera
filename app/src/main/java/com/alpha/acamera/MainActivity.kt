package com.alpha.acamera

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
        private const val REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        configureLayout()
        initPermission()
    }

    private fun initPermission() {
        val allPermissions = arrayOf(Manifest.permission.CAMERA)
        val requestList: MutableList<String> = ArrayList()
        for (permission in allPermissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                requestList.add(permission)
            }
        }
        if (requestList.size > 0) {
            ActivityCompat.requestPermissions(this,
                    requestList.toTypedArray(), REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult: success")
            } else {
                Toast.makeText(this, "请授予应用权限后使用", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun configureLayout() {
        btnOpenCamera.setOnClickListener { openCamera() }
    }

    private fun openCamera() {
        val intent = Intent(this, CameraActivity::class.java)
        startActivity(intent)
    }
}