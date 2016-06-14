package com.shizhefei.indicator.moretab;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.shizhefei.fragment.LazyFragment;
import com.shizhefei.indicator.demo.R;

public class MoreFragment extends LazyFragment {
    private ProgressBar progressBar;
    private TextView textView;
    private int tabIndex;
    public static final String INTENT_INT_INDEX = "intent_int_index";

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        handler = new Handler(Looper.getMainLooper()) {
            public void handleMessage(android.os.Message msg) {
                progressBar.setVisibility(View.GONE);
                textView.setVisibility(View.VISIBLE);
            }
        };

        setContentView(R.layout.fragment_tabmain_item);
        tabIndex = getArguments().getInt(INTENT_INT_INDEX);
        progressBar = (ProgressBar) findViewById(R.id.fragment_mainTab_item_progressBar);
        textView = (TextView) findViewById(R.id.fragment_mainTab_item_textView);
        textView.setText("界面" + " " + tabIndex + " 加载完毕");
        handler.sendEmptyMessageDelayed(1, 2000);
    }

    @Override
    public void onDestroyViewLazy() {
        super.onDestroyViewLazy();
        handler.removeCallbacksAndMessages(null);
    }

    private Handler handler;
}
