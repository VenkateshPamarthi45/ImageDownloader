package com.venkateshpamarthi.imagedownloader.helper.bitmapUtilities;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;


/**
 * Created by venkateshpamarthi on 14/12/16.
 */

public class BitMapLruCache{

    private volatile static BitMapLruCache instance = null;
    static LruCache<String, Bitmap> lruCache = null;
    //private DiskLruCache mDiskLruCache;
    private final Object mDiskCacheLock = new Object();
    private boolean mDiskCacheStarting = true;
    private static final int DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB
    private static final String DISK_CACHE_SUBDIR = "thumbnails";

    public static BitMapLruCache getInstance(){
        if (instance == null) {
            synchronized (BitMapLruCache.class) {
                if (instance == null) {
                    final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
                    final int cacheSize = maxMemory / 8;
                    lruCache = new LruCache<>(cacheSize);
                    instance = new BitMapLruCache();
                }
            }
        }
        return instance;
    }

    private BitMapLruCache() {
    }

    public LruCache<String, Bitmap> getLruCache() {
        return lruCache;
    }

    public Bitmap getBitmap(String url) {
        return lruCache.get(url);
    }

    public void putBitmap(String url, Bitmap bitmap) {
        lruCache.put(url, bitmap);
    }
}
