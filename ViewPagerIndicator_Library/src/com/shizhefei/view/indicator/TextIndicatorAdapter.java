package com.shizhefei.view.indicator;

import android.view.View;
import android.view.ViewGroup;

import com.shizhefei.view.indicator.Indicator.IndicatorAdapter;

public abstract class TextIndicatorAdapter extends IndicatorAdapter {

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return null;
	}

	public abstract CharSequence getText(int position);

}
