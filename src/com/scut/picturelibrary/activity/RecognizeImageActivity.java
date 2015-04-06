package com.scut.picturelibrary.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.UploadFileListener;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.scut.picturelibrary.R;
import com.scut.picturelibrary.adapter.SimiAdapter;
import com.scut.picturelibrary.database.UploadedFileTableUtil;
import com.scut.picturelibrary.views.DialogManager;
import com.scut.picturelibrary.views.NoScrollGridView;

/**
 * 识图Activity
 * 
 * @author 黄建斌
 * 
 */
public class RecognizeImageActivity extends ActionBarActivity {
	private static final String TAG = "RecognizeImageActivity";
	String mFileName;
	String mSourcePath;
	String mUploadFileUrl;
	String mInsimiUrl;
	String mInsameUrl;
	int mSize;

	UploadedFileTableUtil mUploadedTable;
	SimiAdapter mAdapter;

	NoScrollGridView mSimiGridView;
	LinearLayout mSameImagesLayout;
	ImageView mSourceImage;
	TextView mSameImagesTitleTextView;
	TextView mSimiImagesTitleTextView;
	TextView mGuessTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recognize);
		Intent intent = getIntent();
		mSourcePath = intent.getStringExtra("path");
		mFileName = intent.getStringExtra("filename");

		Bmob.initialize(this, "ee96600c38da5fe2c41328c00b90e2a1");

		mSameImagesLayout = (LinearLayout) findViewById(R.id.ll_recognize_insame_images);
		mSameImagesTitleTextView = (TextView) findViewById(R.id.txt_recognize_insame_title);
		mSimiImagesTitleTextView = (TextView) findViewById(R.id.txt_recognize_insimi_title);
		mGuessTextView = (TextView) findViewById(R.id.txt_recognize_guess);
		mSourceImage = (ImageView) findViewById(R.id.img_recognize_source);
		mSimiGridView = (NoScrollGridView) findViewById(R.id.grid_recognize_insimi);

		mUploadedTable = new UploadedFileTableUtil(this);
		mAdapter = new SimiAdapter(this, R.layout.grid_files_item,
				R.id.img_grid_files_item_photo, new ArrayList<String>());

		mSimiGridView.setAdapter(mAdapter);

		ImageLoader.getInstance().displayImage("file://" + mSourcePath,
				mSourceImage);

		uploadImage(mSourcePath, mFileName);
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		DialogManager.dismissDialog();
	}

	private void displayDownloadedSameImages(JSONObject response) {
		JSONArray jsonarray;
		try {
			jsonarray = response.getJSONArray("data");
			if (jsonarray.toString().equals("[{}]")) {
				mSameImagesTitleTextView.setText("未找到相同图片");
			}
			for (int i = 0; i < jsonarray.length(); i++) {
				String thumbURL = ((JSONObject) jsonarray.get(i)).get(
						"thumbURL").toString();
				if (i < 3) {
					ImageLoader.getInstance().displayImage(thumbURL,
							(ImageView) mSameImagesLayout.getChildAt(i));
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void displayDownloadedSimiImages(JSONObject response) {
		JSONArray jsonarray;
		try {
			jsonarray = response.getJSONArray("data");
			Log.d(TAG, "jsonarray simi" + jsonarray.toString());
			if (jsonarray.toString().equals("[{}]")) {
				mSimiImagesTitleTextView.setText("未找到相似图片");
			}
			for (int i = 0; i < jsonarray.length(); i++) {
				String thumbURL = ((JSONObject) jsonarray.get(i)).get(
						"thumbURL").toString();
				mAdapter.add(thumbURL);
			}
			mAdapter.notifyDataSetChanged();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// private List<String> getResponseAllTitle(JSONObject response) {
	// List<String> result = new ArrayList<String>();
	// JSONArray jsonarray;
	// try {
	// jsonarray = response.getJSONArray("data");
	// Log.d(TAG, jsonarray.toString());
	// for (int i = 0; i < jsonarray.length(); i++) {
	// String fromPageTitleEnc = ((JSONObject) jsonarray.get(i)).get(
	// "fromPageTitleEnc").toString();
	// result.add(fromPageTitleEnc);
	// }
	// } catch (JSONException e) {
	// e.printStackTrace();
	// }
	// return result;
	// }

	private void uploadImage(String path, String filename) {
		File file = new File(path);
		final BmobFile bmobFile = new BmobFile(file);
		FileInputStream fis = null;
		String url = null;
		try {
			fis = new FileInputStream(file);
			mSize = fis.available();
			url = mUploadedTable.hasUploaded(filename, mSize);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.d(TAG, "file size is: " + mSize);
		DialogManager.showProgressDialog(RecognizeImageActivity.this, null);
		if (url != null) {// 已经上传过了
			mUploadFileUrl = url;
			getSimiSameImage(mUploadFileUrl);
			return;
		}
		// 未上传过，进行上传
		bmobFile.uploadblock(this, new UploadFileListener() {

			@Override
			public void onSuccess() {
				Toast.makeText(RecognizeImageActivity.this, "上传成功",
						Toast.LENGTH_LONG).show();
				mUploadFileUrl = bmobFile
						.getFileUrl(RecognizeImageActivity.this);
				// 上传结束，存入数据库
				mUploadedTable.insert(mFileName, mSize, mUploadFileUrl);
				getSimiSameImage(mUploadFileUrl);
			}

			@Override
			public void onProgress(Integer value) {
				if (DialogManager.getProgressDialog() != null) {
					DialogManager.getProgressDialog().setProgress(value);
				}
			}

			@Override
			public void onFailure(int code, String msg) {
				DialogManager.dismissDialog();
				Toast.makeText(RecognizeImageActivity.this, "上传失败",
						Toast.LENGTH_LONG).show();
			}
		});
	}

	private void getSimiSameImage(String uploadFileUrl) {
		// 返回最多9张类似图片
		mInsimiUrl = "http://stu.baidu.com/i?filename=&fm=15&rt=0&pn=0&rn=9&pn=0&ct=1&stt=1&tn=insimijson&ie=utf-8&objurl="
				+ uploadFileUrl;
		// 返回最多3个相同图片
		mInsameUrl = "http://stu.baidu.com/i?filename=&fm=15&rt=0&pn=0&rn=3&pn=0&ct=1&stt=1&tn=insamejson&ie=utf-8&objurl="
				+ uploadFileUrl;
		DialogManager.dismissDialog();
		httpGet(mInsameUrl, INSAME, new OnHttpEndListener() {
			@Override
			public void onHttpEnd(JSONObject json) {
				// List<String> keyworlds = KeyWordsAnalizer
				// .getKeywords(getResponseAllTitle(json));
				// if (keyworlds != null) {
				// Message msg = new Message();
				// msg.what = KEYWORDS;
				// msg.obj = keyworlds;
				// mHandler.sendMessage(msg);
				// }
			}
		});
		httpGet(mInsimiUrl, INSIMI, null);
	}

	private static final int INSIMI = 0x15040500;
	private static final int INSAME = 0x15040501;
	private static final int KEYWORDS = 0x15040502;
	RecognizeHandler mHandler = new RecognizeHandler(this);

	static class RecognizeHandler extends Handler {
		WeakReference<RecognizeImageActivity> context;

		public RecognizeHandler(RecognizeImageActivity ac) {
			context = new WeakReference<RecognizeImageActivity>(ac);
		}

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == INSIMI) {
				context.get().displayDownloadedSimiImages((JSONObject) msg.obj);
			}
			if (msg.what == INSAME) {
				context.get().displayDownloadedSameImages((JSONObject) msg.obj);
			}
			if (msg.what == KEYWORDS) {
				List<String> keyworlds = (List<String>) msg.obj;
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < keyworlds.size(); i++) {
					sb.append(keyworlds.get(i) + " ");
				}
				context.get().mGuessTextView.setText("您的图片可能与下面内容有关：\n\n  "
						+ sb.toString());
			}
		}
	}

	public interface OnHttpEndListener {
		public void onHttpEnd(JSONObject json);
	}

	private void httpGet(final String url, final int what,
			final OnHttpEndListener listener) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				HttpGet request = new HttpGet(url);
				HttpClient client = new DefaultHttpClient();
				org.apache.http.HttpResponse response;
				try {
					response = client.execute(request);
					HttpEntity entity = response.getEntity();
					String result = EntityUtils.toString(entity, "GBK");
					JSONObject json = new JSONObject(result);
					if (listener != null) {
						listener.onHttpEnd(json);
					}
					Message msg = new Message();
					msg.what = what;
					msg.obj = json;
					mHandler.sendMessage(msg);
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
				}

			}
		}).start();

	}
}
