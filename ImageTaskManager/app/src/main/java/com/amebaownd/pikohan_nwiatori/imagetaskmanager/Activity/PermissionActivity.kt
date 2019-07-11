package com.amebaownd.pikohan_nwiatori.imagetaskmanager.Activity

import android.Manifest
import android.annotation.TargetApi
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast

class PermisionActivity :AppCompatActivity() {

    private val REQUEST_PERMISSION = 10;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

            Log.d("MainActivity", "onCreate()");

            if(Build.VERSION.SDK_INT >= 23){
                checkPermission();
            }
            else{
                readContentActivity();
            }
        }

        // Permissionの確認
        @TargetApi(Build.VERSION_CODES.M)
        public fun checkPermission() {
            // 既に許可している
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)==
                PackageManager.PERMISSION_GRANTED){
                readContentActivity();
            }
            // 拒否していた場合
            else{
                requestLocationPermission();
            }
        }

        // 許可を求める
        @TargetApi(Build.VERSION_CODES.M)
        private fun requestLocationPermission() {
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_PERMISSION);

            } else {
                val toast = Toast.makeText(this,
                "許可されないとアプリが実行できません", Toast.LENGTH_SHORT);
                toast.show();

                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_PERMISSION);

            }
        }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_PERMISSION) {
            // 使用が許可された
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                readContentActivity();
            } else {
                // それでも拒否された時の対応
                val toast = Toast.makeText(this,
                "これ以上なにもできません", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

        // Intent でActivityへ移行
        private fun readContentActivity() {
            val intent =  Intent(getApplication(), MainActivity::class.java);
            startActivity(intent);
        }
    }