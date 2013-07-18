package com.makewithmoto.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.makewithmoto.base.BaseFragment;
import com.makewithmoto.sharedcomponents.R;

public class EmptyFragment extends BaseFragment {

	private View v;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		v = inflater.inflate(R.layout.fragment_empty, container, false);
		return v;

	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}

	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {

		}
		return true;
	}

}
