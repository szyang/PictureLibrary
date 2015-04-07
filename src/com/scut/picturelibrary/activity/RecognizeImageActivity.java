package com.scut.picturelibrary.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.RedirectHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
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
	@SuppressWarnings("unused")
	private static final String TAG = "RecognizeImageActivity";
	String mFileName;
	String mSourcePath;
	String mUploadFileUrl;
	String mInsimiUrl;
	String mInsameUrl;
	int mSize;

	Set<GetWorkerTask> taskCollection;
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

		taskCollection = new HashSet<GetWorkerTask>();

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
		cancelAllTasks();
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
		GetWorkerTask simitask = new GetWorkerTask();
		taskCollection.add(simitask);
		simitask.execute(INSIMI, mInsimiUrl);
		GetWorkerTask sametask = new GetWorkerTask();
		taskCollection.add(sametask);
		sametask.execute(INSAME, mInsameUrl);
	}

	public void cancelAllTasks() {
		if (taskCollection != null) {
			for (GetWorkerTask task : taskCollection) {
				task.cancel(false);
			}
		}
	}

	private static final String INSIMI = "insimi";
	private static final String INSAME = "insame";
	private String mLocationQuery;
	private String mKeyword;

	public URI getLocationURI(HttpResponse response) {
		Header[] headers = response.getAllHeaders();
		String locationName = "Location";
		URI locationUrl = null;
		String location = null;
		for (int i = 0; i < headers.length; i++) {
			String name = headers[i].getName();
			if (name.equals(locationName)) {
				location = headers[i].getValue();
				break;
			}
		}
		if (location != null) {
			try {
				locationUrl = new URI(location);
				mLocationQuery = locationUrl.getQuery();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
		return locationUrl;
	}

	public String getQueryString(String url, String name) {
		String[] params = url.split("&");
		for (int i = 0; i < params.length; i++) {
			if (params[i].startsWith(name)) {
				String[] values = params[i].split("=");
				if (values.length > 1)
					return params[i].split("=")[1];
			}
		}
		return null;
	}

	class GetWorkerTask extends AsyncTask<String, Void, JSONObject> {

		// params what url
		@Override
		protected JSONObject doInBackground(String... params) {
			HttpGet request = new HttpGet(params[1]);
			DefaultHttpClient client = new DefaultHttpClient();
			client.setRedirectHandler(new RedirectHandler() {
				@Override
				public boolean isRedirectRequested(HttpResponse response,
						HttpContext context) {
					return RecognizeImageActivity.this.getLocationURI(response) != null;
				}

				@Override
				public URI getLocationURI(HttpResponse response,
						HttpContext context) throws ProtocolException {
					return RecognizeImageActivity.this.getLocationURI(response);
				}
			});
			org.apache.http.HttpResponse response;
			JSONObject json = null;
			try {
				response = client.execute(request);
				HttpEntity entity = response.getEntity();
				String result = EntityUtils.toString(entity, "GBK");
				if (params[0].equals(INSAME)) {
					mKeyword = getQueryString(mLocationQuery, "keyword");
				}
				json = new JSONObject(result);
				json.put("what", params[0]);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
			} finally {
				request.abort();
			}
			return json;
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			if (result != null) {
				try {
					String what = result.get("what").toString();
					if (what.equals(INSIMI)) {
						displayDownloadedSimiImages(result);
					}
					if (what.equals(INSAME)) {
						displayDownloadedSameImages(result);
						if (mKeyword != null && mKeyword.length() > 0)
							mGuessTextView.setText("您要找的可能是:\n\n  " + mKeyword);
						else
							mGuessTextView.setText("找不到关键字");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			taskCollection.remove(this);
		}
	}
}
