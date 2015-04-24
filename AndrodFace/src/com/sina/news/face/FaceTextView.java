package com.sina.news.face;

import android.content.Context;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.AttributeSet;
import android.widget.TextView;

public class FaceTextView extends TextView {


	public FaceTextView(Context context) {
		super(context);
	}

	public FaceTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FaceTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void setText(CharSequence text, BufferType type) {
		// String html =
		// "测试自定义的tag是<i class='abc'>[笑脸]</i>，真的是<i class='aaa'>[吐舌]</i>";
		// testString = Html.fromHtml(html);
		SpannableString spannableString = new SpannableString(text);
		spannableString = FaceManager.getInstance(getContext()).getExpressionString(getContext(),
				spannableString.toString());
		super.setText(spannableString, type);
	}
}
