package com.sina.news.face;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;


public class FaceFragment extends Fragment {

	private FaceGridView gridview;
	private List<FaceBean> mFaces = new ArrayList<FaceBean>();
	private GridViewAdapter adapter;
	private Handler mHandler;
	
	public void setArguments(List<FaceBean> faces,Handler handler){
		this.mFaces = faces;
		this.mHandler = handler;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_face, null);
		gridview = (FaceGridView) view.findViewById(R.id.gridview);
		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		adapter = new GridViewAdapter();
		gridview.setAdapter(adapter);
		gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Message msg = new Message();
				msg.obj = mFaces.get(position).getKey();
				mHandler.sendMessage(msg);
			}
		});
		super.onViewCreated(view, savedInstanceState);
	}
	
	
	private class GridViewAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return mFaces.size();
		}

		@Override
		public Object getItem(int position) {
			return mFaces.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewholder = null;
			if (convertView == null) {
				viewholder = new ViewHolder();
				convertView = LayoutInflater.from(getActivity()).inflate(
						R.layout.item_face, null);
				viewholder.imageview = (ImageView) convertView
						.findViewById(R.id.face_image);
				convertView.setTag(viewholder);
			} else {
				viewholder = (ViewHolder) convertView.getTag();
			}
			
			InputStream is = null;
			try {
				is = getActivity().getAssets().open(
						"face" + File.separator
								+ mFaces.get(position).getImageName() + ".png");
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (is != null) {
				Bitmap bitmap = BitmapFactory.decodeStream(is);
				viewholder.imageview.setImageBitmap(bitmap);
			}

			return convertView;
		}
		
		class ViewHolder{
			public ImageView imageview;
		}
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}
