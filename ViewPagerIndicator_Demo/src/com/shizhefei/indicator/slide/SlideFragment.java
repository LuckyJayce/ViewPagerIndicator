package com.shizhefei.indicator.slide;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

import com.shizhefei.indicator.BaseFragment;
import com.shizhefei.indicator.R;

public class SlideFragment extends BaseFragment {
	private int tabIndex;
	public static final String INTENT_INT_INDEX = "intent_int_index";
	private int[] images = { R.drawable.p1, R.drawable.p2, R.drawable.p3, R.drawable.p4 };
	private ProgressBar progressBar;

	@Override
	protected void onCreateView(Bundle savedInstanceState) {
		super.onCreateView(savedInstanceState);
		setContentView(R.layout.fragment_slide);
		Bundle bundle = getArguments();
		tabIndex = bundle.getInt(INTENT_INT_INDEX);
		progressBar = (ProgressBar) findViewById(R.id.fragment_slide_progressBar);
		handler.sendEmptyMessageDelayed(1, 3000);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		handler.removeMessages(1);
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			getContentView().setBackgroundResource(images[tabIndex]);
			progressBar.setVisibility(View.GONE);
		}
	};
}
