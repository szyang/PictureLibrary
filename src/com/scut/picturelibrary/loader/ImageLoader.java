package com.scut.picturelibrary.loader;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * 简易图片内存缓存机制，防止OOM
 * 
 * @author 黄建斌
 * 
 */
public class ImageLoader {

	/**
	 * 图片缓存技术的核心类，用于缓存所有加载好的图片，在程序内存达到设定值时会将最少最近使用的图片移除掉。
	 */
	private static LruCache<String, Bitmap> mMemoryCache;

	/**
	 * 单例模式 ImageLoader的实例。
	 */
	private static ImageLoader mImageLoader;

	private ImageLoader() {
		// 获取应用程序最大可用内存
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int cacheSize = maxMemory / 8;
		// 设置图片缓存大小为程序最大可用内存的1/8
		mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				// 重写此方法来衡量每张图片的大小，默认返回图片数量。
				// return bitmap.getByteCount() / 1024;
				return bitmap.getRowBytes() * bitmap.getHeight();
			}
		};
	}

	/**
	 * 获取ImageLoader的实例。
	 * 
	 * @return ImageLoader的实例。
	 */
	public static ImageLoader getInstance() {
		if (mImageLoader == null) {
			mImageLoader = new ImageLoader();
		}
		return mImageLoader;
	}

	/**
	 * 将一张图片存储到LruCache中。
	 * 
	 * @param key
	 *            LruCache的键，这里传入图片的URL地址。
	 * @param bitmap
	 *            LruCache的键，这里传入Bitmap对象。
	 */
	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemoryCache(key) == null) {
			mMemoryCache.put(key, bitmap);
		}
	}

	/**
	 * 从LruCache中获取一张图片，如果不存在就返回null。
	 * 
	 * @param key
	 *            LruCache的键，这里传入图片的URL地址。
	 * @return 对应传入键的Bitmap对象，或者null。
	 */
	public Bitmap getBitmapFromMemoryCache(String key) {
		return mMemoryCache.get(key);
	}


}