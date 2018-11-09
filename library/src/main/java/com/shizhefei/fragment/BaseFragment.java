package com.shizhefei.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

abstract class BaseFragment extends Fragment {
	protected LayoutInflater inflater;
	private View contentView;
	private Context context;
	private ViewGroup container;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getActivity().getApplicationContext();
	}

	@Override
	public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.inflater = inflater;
		this.container = container;
		onCreateView(savedInstanceState);
		if (contentView == null)
			return super.onCreateView(inflater, container, savedInstanceState);
		return contentView;
	}

	protected void onCreateView(Bundle savedInstanceState) {

	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		contentView = null;
		container = null;
		inflater = null;
	}

	public Context getApplicationContext() {
		return context;
	}

	public void setContentView(int layoutResID) {
		setContentView((ViewGroup) inflater.inflate(layoutResID, container, false));
	}

	public void setContentView(View view) {
		contentView = view;
	}

	public View getContentView() {
		return contentView;
	}

	public <T extends View> T findViewById(@IdRes int id) {
		if (contentView != null)
			return contentView.findViewById(id);
		return null;
	}
}
