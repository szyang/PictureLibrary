package com.scut.picturelibrary.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.scut.picturelibrary.R;
import com.scut.picturelibrary.views.MyImageView;

public class ImageViewActivity extends Activity {

	private ViewPager mViewPager;

	String path;
	int mposition;
	int count;
	String[] path_gather;

	private Uri[] imgs;

	private ImageView[] mImageViews;// = new ImageView[imgs.length];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_image);
		Log.v("oncreate", "here");
		Intent intent = getIntent();
		path = intent.getStringExtra("path");
		Log.v("path", path);

		mposition = intent.getIntExtra("position", 0); // 当前位置
		Log.v("position", Integer.toString(mposition));
		count = intent.getIntExtra("count", 0);// 该文件夹下图片总数量
		Log.v("count", Integer.toString(count));

		path_gather = new String[count];
		imgs = new Uri[count];

		path_gather = intent.getStringArrayExtra("path_all");

		for (int i = 0; i < count; i++) {
			Log.v("this is path", path_gather[i]);
			imgs[i] = Uri.parse("file:///" + path_gather[i]);
		}

		mImageViews = new ImageView[count];

		mViewPager = (ViewPager) findViewById(R.id.image_viewpager);
		mViewPager.setAdapter(new PagerAdapter() {

			@Override
			public Object instantiateItem(ViewGroup container, int position) {

				MyImageView imageView = new MyImageView(getApplicationContext());
				imageView.setImageURI(imgs[position]);
				// imageView.setImageResource(imgs[position]);
				container.addView(imageView);
				mImageViews[position] = imageView;

				return imageView;
			}

			@Override
			public void destroyItem(ViewGroup container, int position,
					Object object) {

				container.removeView(mImageViews[position]);
			}

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				// TODO Auto-generated method stub
				return arg0 == arg1;
			}

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return mImageViews.length;
			}
		});

		mViewPager.setCurrentItem(mposition);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

}
