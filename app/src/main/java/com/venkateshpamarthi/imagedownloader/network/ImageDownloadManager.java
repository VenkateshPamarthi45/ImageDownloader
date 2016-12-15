package com.venkateshpamarthi.imagedownloader.network;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.venkateshpamarthi.imagedownloader.R;
import com.venkateshpamarthi.imagedownloader.helper.bitmapUtilities.BitMapLruCache;
import com.venkateshpamarthi.imagedownloader.helper.bitmapUtilities.ImageDecoder;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by venkateshpamarthi on 14/12/16.
 */

public class ImageDownloadManager {

    private static final String TAG = "ImageDownloadManager";
    private Context context;
    private Queue<ImageModel> downloadQueue = null;
    private boolean isApiCalling = false;
    private volatile static ImageDownloadManager instance = null;

    public static ImageDownloadManager getInstance(Context context) {
        if (instance == null) {
            synchronized (ImageDownloadManager.class) {
                if (instance == null) {
                    instance = new ImageDownloadManager(context);
                }
            }
        }
        return instance;
    }

    public ImageDownloadManager(Context context) {
        this.context = context;
        if (downloadQueue == null)
            downloadQueue = new ConcurrentLinkedQueue<>();
    }

    public void addToQueue(String downloadItem, int data, ImageView imageView, onDownloadListener downloadListener) {
        ImageModel imageModel = new ImageModel(downloadItem,data, imageView, downloadListener);
        downloadQueue.offer(imageModel);
        downloadListener.preUpdate();
        callRestApi();
    }

    private void callRestApi() {
        ImageModel imageModel = downloadQueue.peek();
        if (!isApiCalling && imageModel != null) {
            if (cancelPotentialWork(imageModel.getData(), imageModel.getImageView())) {
                final BitmapWorkerTask task = new BitmapWorkerTask(imageModel);
                Bitmap placeholderBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
                final AsyncDrawable asyncDrawable =
                        new AsyncDrawable(context.getResources(), placeholderBitmap, task);
                imageModel.getImageView().setImageDrawable(asyncDrawable);
                task.execute(imageModel.getData());
            }
            BitmapWorkerTask bitmapWorkerTask = new BitmapWorkerTask(imageModel);
            bitmapWorkerTask.execute(imageModel.getData());
        }
    }

    public interface onDownloadListener {
        void preUpdate();

        void onProgressUpdate(int progress);

        void onPostExecute(Bitmap bitmap);

        void onErrorListener(String message);
    }

    private class BitmapWorkerTask extends AsyncTask<Integer, String, Bitmap> {
        ImageModel imageModel;
        private final WeakReference<ImageView> imageViewReference;
        public int data = -1;

        private BitmapWorkerTask(ImageModel imageModel) {
            this.imageModel = imageModel;
            imageViewReference = new WeakReference<ImageView>(imageModel.getImageView());
            Log.d(TAG, "MyAsyncTask() called with: itemModel = [" + imageModel.toString() + "]");
        }

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute: ");
            isApiCalling = true;
            super.onPreExecute();
            imageModel.getOnDownloadListener().preUpdate();
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            Log.i(TAG, "onPostExecute: ");
            if (isCancelled()) {
                bitmap = null;
            }
            isApiCalling = false;
            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                final BitmapWorkerTask bitmapWorkerTask =
                        getBitmapWorkerTask(imageView);
                if (this == bitmapWorkerTask && imageView != null) {
                    imageView.setImageBitmap(bitmap);
                    imageModel.getOnDownloadListener().onPostExecute(bitmap);
                }
            }
            downloadQueue.poll();
            checkInQueue();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            imageModel.getOnDownloadListener().onProgressUpdate(Integer.parseInt(values[0]));
        }

        @Override
        protected void onCancelled(Bitmap s) {
            super.onCancelled(s);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected Bitmap doInBackground(Integer... params) {
            int count;
            data = params[0];
            Bitmap bitmap = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(imageModel.getUrl());
                Log.i(TAG, "doInBackground: url " + url.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();

                int statusCode = urlConnection.getResponseCode();
                if (statusCode != 200) {
                    imageModel.getOnDownloadListener().onErrorListener(urlConnection.getResponseMessage());
                    return null;
                }
                int lengthOfFile = urlConnection.getContentLength();
                InputStream input = urlConnection.getInputStream();
                bitmap = ImageDecoder.decodeSampledBitmapFromResource(input, url.openStream(), 680, 360);
                BitMapLruCache.getInstance().putBitmap(imageModel.getUrl(), bitmap);
                byte data[] = new byte[1024];
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress("" + (int) ((total * 100) / lengthOfFile));
                }
                input.close();
            } catch (Exception e) {
                imageModel.getOnDownloadListener().onErrorListener(e.getMessage());
                Log.e("Error: ", e.getMessage());
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return bitmap;
        }
    }

    private void checkInQueue() {
        if (!isApiCalling) {
            callRestApi();
        }
    }

    /**
     * The type Async drawable.
     * inspired from https://developer.android.com/training/displaying-bitmaps/process-bitmap.html
     */
    private static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        AsyncDrawable(Resources res, Bitmap bitmap,
                             BitmapWorkerTask bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference =
                    new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
        }

        BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }

    private boolean cancelPotentialWork(int data, ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final int bitmapData = bitmapWorkerTask.data;
            // If bitmapData is not yet set or it differs from the new data
            if (bitmapData == -1 || bitmapData != data) {
                // Cancel previous task
                bitmapWorkerTask.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }

    private BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

}
