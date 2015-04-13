package com.scut.picturelibrary.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.scut.picturelibrary.R;
import com.scut.picturelibrary.adapter.InSimiGridViewAdapter;
import com.scut.picturelibrary.adapter.MediaFilesAdapter;
import com.scut.picturelibrary.loader.MediaFilesCursorLoader;
import com.scut.picturelibrary.views.DialogManager;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class SearchImageActivity extends ActionBarActivity implements
		LoaderCallbacks<Cursor> {
	private GridView mGridView;
	private MediaFilesAdapter mAdapter;
	private final int LOAD_ID = 0x20150401;
	private String query = "20152015201520152015";
	private TabHost mtabhost;
	private GridView nGridView;
	private TextView textview;
	private final String SORT_BY_NAME = MediaStore.Images.Media.DISPLAY_NAME;
	private final String SORT_BY_DATE = MediaStore.Images.Media.DATE_MODIFIED;
	private String mSort = SORT_BY_NAME;
	String mSearchUrl;
	InSimiGridViewAdapter nAdapter;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_files);
		getSupportLoaderManager().initLoader(LOAD_ID, null, this);
		// 初始化视图
		initView();

		initListener();
	}

	private void initView() {
		textview = (TextView) findViewById(R.id.tv);
		mtabhost = (TabHost) findViewById(R.id.tabhost);
		mtabhost.setup();
		mtabhost.addTab(mtabhost.newTabSpec("tab1").setIndicator("本地图片")
				.setContent(R.id.local));
		mtabhost.addTab(mtabhost.newTabSpec("tab2").setIndicator("网络图片")
				.setContent(R.id.grid_net_files));
		mGridView = (GridView) findViewById(R.id.grid_local_files);
		mAdapter = new MediaFilesAdapter(this, null);
		mGridView.setAdapter(mAdapter);
		nAdapter = new InSimiGridViewAdapter(this, R.layout.grid_files_item,
				R.id.img_grid_files_item_photo,
				new ArrayList<Map<String, String>>());
		nGridView = (GridView) findViewById(R.id.grid_net_files);
		nGridView.setAdapter(nAdapter);
	}

	private void initListener() {
		// 设置滚动时图片是否暂停加载的监听
		PauseOnScrollListener listener = new PauseOnScrollListener(
				ImageLoader.getInstance(), false, true);
		mGridView.setOnScrollListener(listener);
		mGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO 点击显示完整图片or播放视频
				// 目前是调用外部程序
				String path = mAdapter.getPath(position);
				Intent it = new Intent(Intent.ACTION_VIEW);
				Uri uri = Uri.parse("file:///" + path);
				if (mAdapter.getType(position).equals("video"))// 视频
					it.setDataAndType(uri, "video/*");
				else { // 图片
					it.setDataAndType(uri, "image/*");
				}
				startActivity(it);
			}
		});
		mGridView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (mAdapter.getType(position).equals("video")) {// 视频
				} else { // 图片
					final String path = mAdapter.getPath(position);
					final String filename = mAdapter.getTitle(position);
					DialogManager.showImageItemMenuDialog(
							SearchImageActivity.this, filename,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									switch (which) {
									case 0:
										Intent intent = new Intent();
										intent.setClass(
												SearchImageActivity.this,
												RecognizeImageActivity.class);
										intent.putExtra("path", path);
										intent.putExtra("filename", filename);
										SearchImageActivity.this
												.startActivity(intent);
										break;
									case 1:
										showShare(path);
										break;
									default:
										break;
									}
								}
							});
				}
				return false;
			}
		});
	}

	// 显示网络图片
	private void displayImage(JSONObject json) throws JSONException {
		// 从网页返回的json取data数组
		String thumbURL;
		JSONArray jsonarray = json.getJSONArray("data");
		// 获取data数组中的缩略图地址
		for (int i = 0; i < jsonarray.length(); i++)
		// 先转为jsonobject再获取数据；
		{
			thumbURL = ((JSONObject) jsonarray.get(i)).getString("thumbURL");
			// 适配器中加上图片地址
			Map<String,String> map = new HashMap<String, String>();
			map.put(InSimiGridViewAdapter.IMAGE_URL, thumbURL);
			nAdapter.add(map);
		}
		nAdapter.notifyDataSetChanged();
	}

	private void getSearchImage(String keyword) {
		mSearchUrl = "http://image.baidu.com/i?tn=baiduimagejson&ct=201326592&cl=2&lm=-1&st=-1&fm=result&fr=&sf=1&fmq=1349413075627_R&pv=&ic=0&nc=1&z=&se=1&showtab=0&fb=0&width=&height=&face=0&istype=2&word="
				+ keyword + "&rn=21&pn=1";
		;
		GetWorkerTask task = new GetWorkerTask();
		task.execute(mSearchUrl);
	}

	// 获取返回的Json,并进行displayimage的操作
	class GetWorkerTask extends AsyncTask<String, Void, JSONObject> {
		@Override
		protected JSONObject doInBackground(String... params) {
			HttpGet request = new HttpGet(params[0]);
			DefaultHttpClient client = new DefaultHttpClient();
			HttpResponse response;
			JSONObject json = null;
			try {
				response = client.execute(request);
				HttpEntity entity = response.getEntity();
				String result = EntityUtils.toString(entity, "UTF-8");
				json = new JSONObject(result);
				json.put("what", params[0]);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			} finally {
				request.abort();
			}
			return json;
		}

		protected void onPostExecute(JSONObject result) {
			if (result != null) {
				try {
					displayImage(result);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				System.out.println("no result");
			}
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.search, menu);
		final MenuItem action_search = menu.findItem(R.id.action_search);
		final SearchView searchView = (SearchView) action_search
				.getActionView();
		action_search.expandActionView();
		searchView.setOnQueryTextListener(new OnQueryTextListener() {
			@Override
			public boolean onQueryTextChange(String text) {
				Log.v("text", text);
				if (text != null) {
					query = text;
					nAdapter.clear();
					// 获取搜索内容
					getSearchImage(query);
					getSupportLoaderManager().restartLoader(LOAD_ID, null,
							SearchImageActivity.this);
				}
				return false;
			}

			@Override
			public boolean onQueryTextSubmit(String arg0) {
				return false;
			}
		});
		searchView
				.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
					@Override
					public void onFocusChange(View view, boolean focus) {
						if (!focus) {// searchView.setIconified(true);
						}
					}
				});
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {// 根据选项进行排序
		case R.id.action_sort_name:
			return resort(SORT_BY_NAME);
		case R.id.action_sort_date:
			return resort(SORT_BY_DATE);
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle arg1) {
		// 创建目标cursor，显示文件夹下图片视频文件
		MediaFilesCursorLoader cursorloader = new MediaFilesCursorLoader(this,
				new String[] { MediaStore.Images.Media._ID,// 文件ID
						MediaStore.Images.Media.BUCKET_ID, // 文件夹ID
						MediaStore.Images.Media.BUCKET_DISPLAY_NAME, // 直接包含该图片文件的文件夹名
						MediaStore.Images.Media.DATE_MODIFIED,// 修改日期
						MediaStore.Images.Media.DISPLAY_NAME, // 图片文件名
						MediaStore.Images.Media.DATA // 图片绝对路径
				}, "_display_name like '%" + query + "%'", null, mSort);
		return cursorloader;
	}

	public boolean resort(String sort) {
		if (!mSort.equals(sort)) {// 转换排序规则
			// 设置当前规则
			mSort = sort;
			// 重定cursor
			getSupportLoaderManager().restartLoader(LOAD_ID, null, this);
			return true;
		}
		return false;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		// 将新的cursor传入
		if (data == null) {
			textview.setVisibility(View.VISIBLE);
			textview.setText("未找到相关图片");
		} else {
			textview.setVisibility(View.INVISIBLE);
		}
		mAdapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// 取消cursor
		mAdapter.swapCursor(null);
	}

	private void showShare(String path) {
		ShareSDK.initSDK(this);
		OnekeyShare oks = new OnekeyShare();
		// 关闭sso授权
		oks.disableSSOWhenAuthorize();
		// 分享时Notification的图标和文字 2.5.9以后的版本不调用此方法
		// oks.setNotification(R.drawable.ic_launcher,
		// getString(R.string.app_name));
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle(getString(R.string.share));
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl("http://sharesdk.cn");
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		oks.setImagePath(path);// 确保SDcard下面存在此张图片
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl("http://sharesdk.cn");
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		oks.setComment("我是测试评论文本");
		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite(getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl("http://sharesdk.cn");
		// 启动分享GUI
		oks.show(this);
	}
}