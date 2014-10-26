package com.shizhefei.view.utils;

import android.graphics.Color;

/**
 * 用于获取两个颜色的过渡色
 * 
 * @author zsy
 *
 */
public class ColorGradient {
	private int color1;
	private int color2;
	private int count;
	private int[] color1Values;
	private int[] color2Values;

	public ColorGradient(int color1, int color2, int count) {
		super();
		this.color1 = color1;
		this.color2 = color2;
		this.count = count;
		color1Values = toColorValue(color1);
		color2Values = toColorValue(color2);
	}

	public int getColor(int i) {
		int[] result = new int[4];
		for (int j = 0; j < color2Values.length; j++) {
			result[j] = (int) (color1Values[j] + (color2Values[j] - color1Values[j]) * 1.0 / count * i);
		}
		return Color.argb(result[0], result[1], result[2], result[3]);
	}

	private int[] toColorValue(int color) {
		int[] values = new int[4];
		values[0] = Color.alpha(color);
		values[1] = Color.red(color);
		values[2] = Color.green(color);
		values[3] = Color.blue(color);
		return values;
	}

	public int getColor1() {
		return color1;
	}

	public int getColor2() {
		return color2;
	}

	public int getCount() {
		return count;
	}

}
