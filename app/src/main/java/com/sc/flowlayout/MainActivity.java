package com.sc.flowlayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private FlowLayout<String> flowLayout;
    private FlowAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        flowLayout = findViewById(R.id.flowLayout);
        adapter = new FlowAdapter<String>() {
            @Override
            public View onBindView(Context context, int position, @Nullable String s) {
                TextView view = new TextView(context);
                view.setTextSize(10);
                view.setGravity(Gravity.CENTER);
                view.setPadding(10, 10, 10, 10);
                view.setText(s);
                view.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_round_orange));
                view.setTextColor(0xffffffff);
                return view;
            }
        };
        adapter.setData(new ArrayList<>(Arrays.asList(
                "时尚", "卡萨丁", "看看书", "开始的接口技\n术的放开手工", "看得开关键", "尽快", "看到科技", "打",
                "篮球", "足球", "棒球", "橄榄球", "冰球", "乒乓球", "羽毛球", "排球", "水球", "手球", "跳远", "撑杆跳",
                "时尚", "卡萨丁", "看看书", "开始的接口技\n术的放开手工", "看得开关键", "尽快", "看到科技", "打",
                "篮球", "足球", "棒球", "橄榄球", "冰球", "乒乓球", "羽毛球", "排球", "水球", "手球", "跳远", "撑杆跳",
                "时尚", "卡萨丁", "看看书", "开始的接口技\n术的放开手工", "看得开关键", "尽快", "看到科技", "打",
                "篮球", "足球", "棒球", "橄榄球", "冰球", "乒乓球", "羽毛球", "排球", "水球", "手球", "跳远", "撑杆跳",
                "时尚", "卡萨丁", "看看书", "开始的接口技\n术的放开手工", "看得开关键", "尽快", "看到科技", "打",
                "篮球", "足球", "棒球", "橄榄球", "冰球", "乒乓球", "羽毛球", "排球", "水球", "手球", "跳远", "撑杆跳",
                "时尚", "卡萨丁", "看看书", "开始的接口技\n术的放开手工", "看得开关键", "尽快", "看到科技", "打",
                "篮球", "足球", "棒球", "橄榄球", "冰球", "乒乓球", "羽毛球", "排球", "水球", "手球", "跳远", "撑杆跳"
        )));
        flowLayout.setAdapter(adapter);
        /*flowLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        }, 1000);*/
        adapter.notifyDataSetChanged();
    }
}