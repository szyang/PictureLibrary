package com.scut.picturelibrary.adapter;

import java.util.List;

import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

/**
 * 识图中类似图片Gridview的Adapter
 * 
 * @author 黄建斌
 * 
 */
public class SimiAdapter extends ArrayAdapter<String> {
	private List<String> mSimiImagesUrlList;
	private int mImageViewResourceId;
	Context mContext;
	int mResource;

	public SimiAdapter(Context context, int resource, int imageViewResourceId,
			List<String> objects) {
		super(context, resource, imageViewResourceId, objects);
		mContext = context;
		mImageViewResourceId = imageViewResourceId;
		mSimiImagesUrlList = objects;
		mResource = resource;
	}

	@Override
	public int getCount() {
		return mSimiImagesUrlList.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView = null;
		if (convertView == null) {
			convertView = View.inflate(mContext, mResource, null);
			imageView = (ImageView) convertView
					.findViewById(mImageViewResourceId);
			convertView.setTag(imageView);
		}
		imageView = (ImageView) convertView.getTag();
		String url = getItem(position);
		ImageLoader.getInstance().displayImage(url, imageView);
		return convertView;
	}
}
