package com.shizhefei.indicator.slide;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

import com.shizhefei.fragment.LazyFragment;
import com.shizhefei.indicator.R;

public class SlideFragment extends LazyFragment {
	private int tabIndex;
	public static final String INTENT_INT_INDEX = "intent_int_index";
	private int[] images = { R.drawable.p1, R.drawable.p2, R.drawable.p3, R.drawable.p4 };
	private ProgressBar progressBar;

	@Override
	protected void onCreateViewLazy(Bundle savedInstanceState) {
		super.onCreateViewLazy(savedInstanceState);
		setContentView(R.layout.fragment_slide);
		Bundle bundle = getArguments();
		tabIndex = bundle.getInt(INTENT_INT_INDEX);
		progressBar = (ProgressBar) findViewById(R.id.fragment_slide_progressBar);
		handler.sendEmptyMessageDelayed(1, 3000);
	}

	@Override
	public void onDestroyViewLazy() {
		super.onDestroyViewLazy();
		handler.removeMessages(1);
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			getContentView().setBackgroundResource(images[tabIndex]);
			progressBar.setVisibility(View.GONE);
		}
	};
}
