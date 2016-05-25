package com.shizhefei.indicator.tabmain;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.shizhefei.fragment.LazyFragment;
import com.shizhefei.indicator.R;

public class SecondLayerFragment extends LazyFragment {
	public static final String INTENT_STRING_TABNAME = "intent_String_tabName";
	public static final String INTENT_INT_POSITION = "intent_int_position";
	private String tabName;
	private int position;
	private TextView textView;
	private ProgressBar progressBar;

	@Override
	protected void onCreateViewLazy(Bundle savedInstanceState) {
		super.onCreateViewLazy(savedInstanceState);
		tabName = getArguments().getString(INTENT_STRING_TABNAME);
		position = getArguments().getInt(INTENT_INT_POSITION);
		setContentView(R.layout.fragment_tabmain_item);
		textView = (TextView) findViewById(R.id.fragment_mainTab_item_textView);
		textView.setText(tabName + " " + position + " 界面加载完毕");
		progressBar = (ProgressBar) findViewById(R.id.fragment_mainTab_item_progressBar);
		handler.sendEmptyMessageDelayed(1, 3000);
	}

	@Override
	protected void onDestroyViewLazy() {
		super.onDestroyViewLazy();
		handler.removeMessages(1);
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			textView.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.GONE);
		};
	};
}
