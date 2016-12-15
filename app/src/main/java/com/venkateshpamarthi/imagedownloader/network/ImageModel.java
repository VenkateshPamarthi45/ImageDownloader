package com.venkateshpamarthi.imagedownloader.network;

import android.widget.ImageView;

/**
 * Created by venkateshpamarthi on 14/12/16.
 */

public class ImageModel {
    private String url;
    private int data;
    private ImageView imageView;
    private ImageDownloadManager.onDownloadListener onDownloadListener;

    public ImageModel(String url, int data, ImageView imageView, ImageDownloadManager.onDownloadListener onDownloadListener) {
        this.url = url;
        this.data = data;
        this.imageView = imageView;
        this.onDownloadListener = onDownloadListener;
    }


    public String getUrl() {
        return url;
    }

    public ImageDownloadManager.onDownloadListener getOnDownloadListener() {
        return onDownloadListener;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public int getData() {
        return data;
    }

    @Override
    public String toString() {
        return "ItemModel{" +
                "url='" + url + '\'' +
                ", onDownloadListener=" + onDownloadListener +
                '}';
    }
}
