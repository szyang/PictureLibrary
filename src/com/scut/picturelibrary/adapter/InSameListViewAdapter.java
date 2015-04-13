package com.scut.picturelibrary.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 识图中相同图片ListView的Adapter
 * 
 * @author 黄建斌
 * 
 */
public class InSameListViewAdapter extends ArrayAdapter<Map<String, String>> {
	private List<Map<String, String>> mSameImagesUrlList;
	private int mImageViewResourceId;
	private int mTextViewResourceId;
	private int mResource;

	public static final String IMAGE_URL = "imageUrl";
	public static final String FROM_URL = "fromUrl";
	public static final String FROM_PAGE_DESCRIPTION = "fromPageDescription";

	/**
	 * 
	 * @param context
	 * @param resource
	 * @param imageViewResourceId
	 * @param objects
	 *            String[]{image url, description, fromURL}
	 */
	public InSameListViewAdapter(Context context, int resource,
			int imageViewResourceId, int textViewResourceId,
			List<Map<String, String>> objects) {
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
		String url = getItem(position).get(IMAGE_URL);
		String description = getItem(position).get(FROM_PAGE_DESCRIPTION);
		ImageLoader.getInstance().displayImage(url, viewHolder.imageView);
		viewHolder.textView.setText(description);
		return convertView;
	}

	public String getFromURL(int position) {
		return getItem(position).get(FROM_URL);
	}
}
