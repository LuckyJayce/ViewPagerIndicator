/*
Copyright 2014 shizhefei（LuckyJayce）

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.shizhefei.view.indicator.slidebar;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.widget.TextView;

import com.shizhefei.view.indicator.Indicator;

/**
 * tab的文本多宽scrollbar就显示多宽，实现新浪个人首页的tab的scrollbar效果
 * 
 * LuckyJayce
 *
 */
public class TextWidthColorBar extends ColorBar {
	private Indicator indicator;
	private int realWidth = 0;

	public TextWidthColorBar(Context context, Indicator indicator, int color, int height) {
		super(context, color, height);
		this.indicator = indicator;
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		int width1 = getTextWidth(getTextView(position));
		int width2 = getTextWidth(getTextView(position + 1));
		realWidth = (int) (width1 * (1 - positionOffset) + width2 * (positionOffset));
	}

	@Override
	public int getWidth(int tabWidth) {
		if (realWidth == 0) {
			if (indicator.getIndicatorAdapter() != null) {
				TextView textView = getTextView(indicator.getCurrentItem());
				if (textView != null) {
					realWidth = getTextWidth(textView);
				}
			}
		}
		return realWidth;
	}

	/**
	 * 如果tab不是textView，可以通过重写该方法，返回tab里面的textView。
	 * 
	 * @param position
	 * @return
	 */
	protected TextView getTextView(int position) {
		return (TextView) indicator.getItemView(position);
	}

	private int getTextWidth(TextView textView) {
		if(textView==null){
		   return 0;
		}
		Rect bounds = new Rect();
		String text = textView.getText().toString();
		Paint paint = textView.getPaint();
		paint.getTextBounds(text, 0, text.length(), bounds);
		int width = bounds.left + bounds.width();
		return width;
	}

}
