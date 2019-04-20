package com.shizhefei.indicator.proxyfragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.shizhefei.indicator.demo.R;

public class BookFragment extends Fragment {
    private View view;
    private ProgressBar progressBar;
    private TextView textView;
    public static final String EXTRA_INT_POSITION = "extra_int_position";
    private Handler handler;
    private int position;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("tttt", " onCreate " + getArguments());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("tttt", " onAttach " + getArguments());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book, container, false);
        initView(view);
        Log.d("tttt", " onCreateView "+ getArguments());
        return view;
    }

    private void initView(View view) {
        Bundle arguments = getArguments();
        position = arguments.getInt(EXTRA_INT_POSITION);
        progressBar = view.findViewById(R.id.progressBar);
        textView = view.findViewById(R.id.textView);

        textView.setText("正在加载" + position + "页");
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                textView.setText("页面" + position + "加载成功.....");
            }
        }, 2000);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("tttt", " onDestroyView "+ getArguments());
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("tttt", " onDetach "+ getArguments());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("tttt", " onDestroy "+ getArguments());
    }
}

