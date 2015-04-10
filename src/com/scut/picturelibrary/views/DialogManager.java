package com.scut.picturelibrary.views;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * 对话框管理
 * 
 * @author 黄建斌
 * 
 */
public class DialogManager {
	private static Dialog mDialog;
	private static ProgressDialog mProgressDialog;

	public static void showImageItemMenuDialog(Context context, String title,
			DialogInterface.OnClickListener listener) {
		dismissDialog();
		android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(
				context);
		// 设置对话框的标题
		builder.setTitle(title);
		builder.setItems(new String[] { "识图" }, listener);
		// 创建一个列表对话框
		mDialog = builder.create();
		mDialog.show();
	}

	public static void showProgressDialog(Context context,
			ProgressDialog.OnClickListener cancelListener) {
		dismissDialog();
		// 构建一个下载进度条
		// 创建ProgressDialog对象
		mProgressDialog = new ProgressDialog(context);
		// 设置进度条风格，风格为长形
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setTitle("上传图片中");
		mProgressDialog.setProgress(0);
		// 设置ProgressDialog 的进度条是否不明确
		mProgressDialog.setIndeterminate(false);
		// 设置ProgressDialog 是否可以按退回按键取消
		mProgressDialog.setCancelable(false);
		// 设置ProgressDialog 的一个Button
		if (cancelListener != null) {
			mProgressDialog.setCancelable(true);
			mProgressDialog.setButton(0, "取消", cancelListener);
		}
		// 让ProgressDialog显示
		mProgressDialog.show();
	}

	public static ProgressDialog getProgressDialog() {
		return mProgressDialog;
	}

	public static void showSimpleDialog(Context ctx, String title,
			String message, DialogInterface.OnCancelListener onCancelListener) {
		dismissDialog();
		android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(
				ctx);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setCancelable(false);
		if (onCancelListener != null) {
			builder.setCancelable(true);
			builder.setOnCancelListener(onCancelListener);
		}
		// 创建一个列表对话框
		mDialog = builder.create();
		mDialog.show();
	}

	public static void dismissDialog() {
		if (mDialog != null) {
			mDialog.dismiss();
			mDialog = null;
		}
		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
	}

}
