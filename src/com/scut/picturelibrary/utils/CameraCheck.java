package com.scut.picturelibrary.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;

public class CameraCheck {
	public static boolean CheckCamera(Context mContext) {
		if (mContext.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA)) {
			return true;
		} else {
			return false;
		}
	}

	public static Camera getCameraInstance(Context mContext) {
		Camera c = null;
		if (CheckCamera(mContext)) {
			try {
				c = Camera.open();
			} catch (Exception e) {
				c=null;
			}
		}
		return c; // returns null if camera is unavailable
	}


}
