package com.scut.picturelibrary.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

public class InSameListViewAdapter extends ArrayAdapter<String[]> {
	private List<String[]> mSameImagesUrlList;
	private int mImageViewResourceId;
	private int mTextViewResourceId;
	private int mResource;

	/**
	 * 
	 * @param context
	 * @param resource
	 * @param imageViewResourceId
	 * @param objects
	 *            String[]{image url, description}
	 */
	public InSameListViewAdapter(Context context, int resource,
			int imageViewResourceId, int textViewResourceId,
			List<String[]> objects) {
		super(context, resource, imageViewResourceId, objects);
		mImageViewResourceId = imageViewResourceId;
		mSameImagesUrlList = objects;
		mResource = resource;
		mTextViewResourceId = textViewResourceId;
	}

	@Override
	public int getCount() {
		return mSameImagesUrlList.size();
	}

	static class ViewHolder {
		TextView textView;
		ImageView imageView;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = View.inflate(getContext(), mResource, null);
			viewHolder.imageView = (ImageView) convertView
					.findViewById(mImageViewResourceId);
			viewHolder.textView = (TextView) convertView
					.findViewById(mTextViewResourceId);
			convertView.setTag(viewHolder);
		}
		viewHolder = (ViewHolder) convertView.getTag();
		String url = getItem(position)[0];
		String description = getItem(position)[1];
		ImageLoader.getInstance().displayImage(url, viewHolder.imageView);
		viewHolder.textView.setText(description);
		return convertView;
	}
}
