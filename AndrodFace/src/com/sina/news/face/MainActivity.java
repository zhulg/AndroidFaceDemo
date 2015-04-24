package com.sina.news.face;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class MainActivity extends FragmentActivity implements OnClickListener {

	private LinearLayout mFaceLay;
	private ViewPager mViewPager;
	private EditText mEditText;
	private LinePageIndicator mLinePageIndicator;
	private Button mchooseFace;
	private FaceManager mFaceManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		initData();
	}

	private void initView() {
		mFaceLay = (LinearLayout) findViewById(R.id.face_lay);
		mViewPager = (ViewPager) findViewById(R.id.viewpager_face);
		mLinePageIndicator = (LinePageIndicator) findViewById(R.id.indicator_lay);
		mEditText = (EditText) findViewById(R.id.inputedittext);
		mchooseFace = (Button) findViewById(R.id.mybutton);
		mchooseFace.setOnClickListener(this);
		mEditText.setOnClickListener(this);

	}

	private void initData() {
		mFaceManager = FaceManager.getInstance(this);
		mFaceManager.setEditText(mEditText);
		ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(
				getSupportFragmentManager(),mFaceManager.createFaceFragment());
		mViewPager.setAdapter(pagerAdapter);
		mLinePageIndicator.setViewPager(mViewPager);
	}

	private class ViewPagerAdapter extends FragmentPagerAdapter {
		private ArrayList<FaceFragment> fragments;
		public ViewPagerAdapter(FragmentManager fm,ArrayList<FaceFragment> fragment) {
			super(fm);
			fragments = fragment;
		}
		@Override
		public Fragment getItem(int position) {
			return fragments.get(position);
		}
		@Override
		public int getCount() {
			return fragments.size();
		}
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			super.destroyItem(container, position, object);
		}

	}


	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.mybutton:
			if (mFaceLay.getVisibility() == View.GONE) {
				closeSoftKeyboard(MainActivity.this);
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						mFaceLay.setVisibility(View.VISIBLE);
					}
				}, 50);

			} else {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						mFaceLay.setVisibility(View.GONE);
					}
				}, 50);
			}
			break;
		case R.id.inputedittext:

			mFaceLay.setVisibility(View.GONE);
			break;
		default:
			break;
		}
	}

	public static void closeSoftKeyboard(Context context) {
		InputMethodManager inputMethodManager = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (inputMethodManager != null
				&& ((Activity) context).getCurrentFocus() != null) {
			inputMethodManager.hideSoftInputFromWindow(((Activity) context)
					.getCurrentFocus().getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mFaceManager != null) {
			mFaceManager.releaseFaceManager();
		}
	}

}
