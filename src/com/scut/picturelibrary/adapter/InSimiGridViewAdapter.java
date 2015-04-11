package com.scut.picturelibrary.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 识图中类似图片Gridview的Adapter
 * 
 * @author 黄建斌
 * 
 */
public class InSimiGridViewAdapter extends ArrayAdapter<Map<String, String>> {
	private List<Map<String, String>> mSimiImagesUrlList;
	private int mImageViewResourceId;
	Context mContext;
	int mResource;

	public static final String IMAGE_URL = "imageUrl";
	public static final String FROM_URL = "fromUrl";

	/**
	 * 
	 * @param context
	 * @param resource
	 * @param imageViewResourceId
	 * @param objects
	 *            List<String[]> String[0]:image url,String[1]:fromPageURL
	 */
	public InSimiGridViewAdapter(Context context, int resource,
			int imageViewResourceId, List<Map<String, String>> objects) {
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
		String url = getItem(position).get(IMAGE_URL);
		ImageLoader.getInstance().displayImage(url, imageView);
		return convertView;
	}

	public String getFromURL(int position) {
		return getItem(position).get(FROM_URL);
	}
}
