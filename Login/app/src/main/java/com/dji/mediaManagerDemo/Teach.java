package com.dji.mediaManagerDemo;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Teach extends AppCompatActivity {


    private static ViewPager mPager;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    private ArrayList<ImageModel> imageModelArrayList;

    private int[] myImageList = new int[]{R.drawable.harley2, R.drawable.benz2,
            R.drawable.vecto, R.drawable.webshots
            , R.drawable.bikess, R.drawable.img1};

    private SharedPreferences mSharedPreferences;
    private boolean firstOpenApp = true;
    private static final String DATA = "DATA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teach);
        mSharedPreferences = getSharedPreferences(DATA, MODE_PRIVATE);
        readData();
        CheckFirstIn();

        imageModelArrayList = new ArrayList<>();
        imageModelArrayList = populateList();

        init();


    }

    private void readData() {//讀取
        firstOpenApp = mSharedPreferences.getBoolean("Open", firstOpenApp);
    }

    private void saveData() {//儲存
        mSharedPreferences.edit()
                .putBoolean("Open", false)
                .apply();
    }

    private void CheckFirstIn() {
        if (firstOpenApp) {
            new AlertDialog.Builder(this)
                    .setMessage("這是第一次開啟App")
                    .setPositiveButton("確定進入", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();//關閉Dialog
                        }
                    }).show();
            firstOpenApp = false;
        } else if (firstOpenApp == false) {
            Intent intent = new Intent(this, ConnectionActivity.class);
            startActivity(intent);
        }
    }

    private ArrayList<ImageModel> populateList() {

        ArrayList<ImageModel> list = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
            ImageModel imageModel = new ImageModel();
            imageModel.setImage_drawable(myImageList[i]);
            list.add(imageModel);
        }


        return list;
    }

    private void init() {

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(new SlidingImage_Adapter(Teach.this, imageModelArrayList));

        CirclePageIndicator indicator = (CirclePageIndicator)
                findViewById(R.id.indicator);

        indicator.setViewPager(mPager);


        final float density = getResources().getDisplayMetrics().density;

//Set circle indicator radius
        indicator.setRadius(5 * density);

        NUM_PAGES = imageModelArrayList.size();

        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        };


        // Pager listener over indicator
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                currentPage = position;

            }

            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int pos) {

            }
        });

    }

    @Override
    protected void onPause() {//在onPause內儲存
        super.onPause();
        saveData();
    }


}
