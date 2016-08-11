package com.shizhefei.indicator.year;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.shizhefei.fragment.LazyFragment;
import com.shizhefei.indicator.demo.R;

/**
 * Created by LuckyJayce on 2016/8/11.
 */
public class YearFragment extends LazyFragment {
    public static final String INTENT_INT_POSITION = "intent_int_position";

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);

        Log.d("pppp", "onCreateViewLazy  " + this+" "+savedInstanceState);

        int position = getArguments().getInt(INTENT_INT_POSITION);

        setContentView(R.layout.fragment_tabmain_item);
        final TextView textView = (TextView) findViewById(R.id.fragment_mainTab_item_textView);
        textView.setText(" " + position + " 界面加载完毕");
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.fragment_mainTab_item_progressBar);
        textView.postDelayed(new Runnable() {
            @Override
            public void run() {
                textView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        }, 3000);
    }
}
