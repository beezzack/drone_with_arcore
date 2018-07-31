package com.example.tina.fbsdk_share;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.model.ShareVideo;
import com.facebook.share.model.ShareVideoContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;

import java.io.File;


public class MainActivity extends AppCompatActivity {

    public String selectedImagePath; //圖片檔案位置

    private int SELECT_FILE = 1;

    private Button shareButton;

    private CallbackManager callbackManager;
    private ShareDialog shareDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initFacebook();

        shareButton = (Button) findViewById(R.id.fb_share_button);


        /*API 23以上權限申請*/
        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            //驗證是否認證權限
            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申請權限
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                    return;
                }
            }
        }

        try {
            SharedPreferences preferencesGet = getApplicationContext()
                    .getSharedPreferences("image", android.content.Context.MODE_PRIVATE);
            selectedImagePath = preferencesGet.getString("selectedImagePath",
                    ""); // 圖片檔案位置，預設為空

            Log.i("selectedImagePath", selectedImagePath + "");

        } catch (Exception e) {
        }

        shareButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                selectImage();
            }
        });
    }

    private void initFacebook() {
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        // this part is optional
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {

            @Override
            public void onSuccess(Sharer.Result result) {
                //分享成功的回调，在这里做一些自己的逻辑处理
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    @Override @SuppressWarnings("deprecation")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //facebook的界面回调
        callbackManager.onActivityResult(requestCode, resultCode, data);

        //選擇開啟方式
        if (requestCode == SELECT_FILE && resultCode == RESULT_OK && null != data) {
            Uri selectedMediaUri = data.getData();
            if (selectedMediaUri.toString().contains("image")) {
                String[] projection = { MediaStore.Images.Media.DATA };
                Cursor cursor = managedQuery(selectedMediaUri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                selectedImagePath = cursor.getString(column_index);

                if (ShareDialog.canShow(SharePhotoContent.class)) {
                    Bitmap image2 = BitmapFactory.decodeFile(selectedImagePath);
                    SharePhoto photo = new SharePhoto.Builder().setBitmap(image2).build();
                    SharePhotoContent contentPhoto = new SharePhotoContent.Builder().addPhoto(photo).build();
                    shareDialog.show(contentPhoto);
                }
            } else  if (selectedMediaUri.toString().contains("video")) {
                String[] projection = { MediaStore.Video.Media.DATA };
                Cursor cursor = managedQuery(selectedMediaUri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
                cursor.moveToFirst();
                selectedImagePath = cursor.getString(column_index);

                if (ShareDialog.canShow(ShareVideoContent.class)) {
                    File file = new File(selectedImagePath);
                    Uri videoFileUri = Uri.fromFile(file);
                    ShareVideo ShareVideo = new ShareVideo.Builder().setLocalUrl(videoFileUri).build();
                    ShareVideoContent contentVideo = new ShareVideoContent.Builder().setVideo(ShareVideo).build();
                    shareDialog.show(contentVideo);
                }
            }
        }
    }

    //public void shareToFacebook() {
        //这里分享一个链接，更多分享配置参考官方介绍：https://developers.facebook.com/docs/sharing/android
        /* 分享文字
        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse("https://developers.facebook.com"))
                    .build();
            shareDialog.show(linkContent);
        */
        //分享圖片
        /*if (ShareDialog.canShow(SharePhotoContent.class)) {
            Bitmap image2 = BitmapFactory.decodeFile(selectedImagePath);
            SharePhoto photo = new SharePhoto.Builder().setBitmap(image2).build();
            SharePhotoContent contentPhoto = new SharePhotoContent.Builder().addPhoto(photo).build();
            shareDialog.show(contentPhoto);
        }
        if (ShareDialog.canShow(ShareVideoContent.class)) {
            File file = new File(selectedImagePath);
            Uri videoFileUri = Uri.fromFile(file);
            ShareVideo ShareVideo = new ShareVideo.Builder().setLocalUrl(videoFileUri).build();
            ShareVideoContent contentVideo = new ShareVideoContent.Builder().setVideo(ShareVideo).build();
            shareDialog.show(contentVideo);
        }*/




    //}

    /*@SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Uri selectedImageUri = data.getData();
        String[] projection = { MediaStore.MediaColumns.DATA };
        Cursor cursor = managedQuery(selectedImageUri, projection, null, null,
                null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();

        selectedImagePath = cursor.getString(column_index); // 選擇的照片位置
    }*/

    private void selectImage() {
        Intent intent1 = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent1.setType("image/* video/*");
        startActivityForResult(Intent.createChooser(intent1, "選擇開啟圖庫"), SELECT_FILE);
    }
}
