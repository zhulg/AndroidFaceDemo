package com.sina.news.face;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.widget.EditText;

public class FaceManager {

	public static final String DELE_KEY = "[delete_icon]";
	public static final String DELE_IMAGE_NAME = "face_delete";
	public static final int PAGE_SIZE = 3; // 页面数量
	public static final int FACE_COUNT = 20;// 每页数量

	public static FaceManager mFaceManager = null;
	private static HashMap<String, String> mFaceMap = new HashMap<String, String>();
	private ArrayList<FaceFragment> mFragments = new ArrayList<FaceFragment>();
	private Context mContext;
	private EditText mEditText;
	private ArrayList<FaceBean> mfacesList = new ArrayList<FaceBean>();

	public static synchronized FaceManager getInstance(Context context) {
		if (mFaceManager == null) {
			mFaceManager = new FaceManager(context);
		} else {
			return mFaceManager;
		}
		return mFaceManager;
	}

	public FaceManager(Context context) {
		mContext = context;
		init();
	}

	public void setEditText(EditText edittext) {
		mEditText = edittext;
	}

	private void init() {
		readFaceFile(mContext);
	}

	protected ArrayList<FaceFragment> createFaceFragment() {
		if (mfacesList == null || mfacesList.size() == 0) {
			return mFragments;
		}
		for (int i = 0; i < PAGE_SIZE; i++) {
			List<FaceBean> tempList = new ArrayList<FaceBean>();
			tempList = mfacesList.subList(FACE_COUNT * i, FACE_COUNT * (i + 1));
			List<FaceBean> pageFaces = new ArrayList<FaceBean>();
			pageFaces.addAll(tempList);
			// 添加删除icon
			pageFaces.add(new FaceBean(FaceManager.DELE_KEY,
					FaceManager.DELE_IMAGE_NAME));
			if (tempList != null && tempList.size() > 1) {
				FaceFragment faceFragment = new FaceFragment();
				faceFragment.setArguments(pageFaces, faceHandler);
				mFragments.add(faceFragment);
			}
		}
		return mFragments;
	}

	public ArrayList<FaceFragment> getFaceFragment() {
		return mFragments;
	}

	public static String getFacePath(String imageName) {
		return "face" + File.separator + imageName + ".png";
	}

	public static String getFaceImageName(Context context, String key) {
		return mFaceMap.get(key);
	}

	public void readFaceFile(Context context) {
		try {
			InputStream in = context.getResources().getAssets()
					.open("face_config");
			BufferedReader br = new BufferedReader(new InputStreamReader(in,
					"UTF-8"));
			String str = null;
			while ((str = br.readLine()) != null) {
				String[] temp = str.split("=");
				FaceBean tempFace = new FaceBean(temp[0], temp[1]);
				mfacesList.add(tempFace);
				mFaceMap.put(temp[0], temp[1]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public SpannableString getExpressionString(Context context, String str) {
		SpannableString spannableString = new SpannableString(str);
		String machersString = "\\[[\\s\\S]{1,4}\\]";
		Pattern sinaPatten = Pattern.compile(machersString,
				Pattern.CASE_INSENSITIVE);
		try {
			dealExpression(context, spannableString, sinaPatten, 0);
		} catch (Exception e) {
			Log.e("dealExpression", e.getMessage());
			return new SpannableString("");
		}
		return spannableString;
	}

	public static void dealExpression(Context context,
			SpannableString spannableString, Pattern patten, int start)
			throws SecurityException, IllegalArgumentException,
			IllegalAccessException {
		Matcher matcher = patten.matcher(spannableString);
		while (matcher.find()) {
			String key = matcher.group();
			if (matcher.start() < start) {
				continue;
			}
			String imageName = getFaceImageName(context, key);
			Bitmap bitmap = null;
			try {
				bitmap = BitmapFactory.decodeStream(context.getAssets().open(
						getFacePath(imageName)));
			} catch (IOException e) {
				e.printStackTrace();
			}
			int end = matcher.start() + key.length();
			if (bitmap != null) {
				Drawable drawable = new BitmapDrawable(bitmap);
				drawable.setBounds(0, 0, bitmap.getHeight(), bitmap.getWidth());
				ImageSpan imageSpan = new ImageSpan(drawable);
				spannableString.setSpan(imageSpan, matcher.start(), end,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			if (end < spannableString.length()) {
				dealExpression(context, spannableString, patten, end);
			}
			break;
		}
	}

	private Handler faceHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			String key = (String) msg.obj;
			if (key.equals(FaceManager.DELE_KEY)) {
				// 删除内容
				deleOneString();
			} else {
				if (mEditText == null) {
					return;
				}
				int edittextCursor = mEditText.getSelectionStart();
				Editable editable = mEditText.getText();
				editable.insert(edittextCursor, key);
				int newCursor = edittextCursor + key.length();
				mEditText.setText(editable);
				mEditText.setSelection(newCursor);
			}
		};
	};

	private void deleOneString() {
		if (mEditText == null) {
			return;
		}
		int edittextCursor = mEditText.getSelectionStart();
		Editable editable = mEditText.getText();
		String tempString = mEditText.getText().toString()
				.substring(0, edittextCursor);
		int end = tempString.lastIndexOf("]");
		if (end == edittextCursor - 1) {
			int start = tempString.lastIndexOf("[");
			if (start != -1) {
				editable.delete(start, edittextCursor);
			} else if (edittextCursor != 0) {
				editable.delete(edittextCursor - 1, edittextCursor);
			}
		} else if (edittextCursor != 0) {
			editable.delete(edittextCursor - 1, edittextCursor);
		}
	}

	public void releaseFaceManager() {
		mFaceMap.clear();
		mFaceMap = null;
		mFragments.clear();
		mFragments = null;
		mFaceManager = null;
	}

}
