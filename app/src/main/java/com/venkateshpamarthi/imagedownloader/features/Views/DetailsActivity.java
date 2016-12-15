package com.venkateshpamarthi.imagedownloader.features.Views;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.venkateshpamarthi.imagedownloader.R;
import com.venkateshpamarthi.imagedownloader.helper.bitmapUtilities.BitMapLruCache;
import com.venkateshpamarthi.imagedownloader.helper.customViews.CircularProgressBar;
import com.venkateshpamarthi.imagedownloader.network.ImageDownloadManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DetailsActivity extends AppCompatActivity {

    private static final String TAG = "DetailsActivity";

    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.downloadButton)
    ImageButton downloadButton;
    @BindView(R.id.progressbar)
    CircularProgressBar progressBar;

    String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        if (getIntent().hasExtra("url")) {
            url = getIntent().getStringExtra("url");
        }
        downloadButton.setVisibility(View.GONE);
        Bitmap bitmap = BitMapLruCache.getInstance().getBitmap(url);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        }else{
            downloadBitmap();
        }

    }

    @OnClick(R.id.downloadButton)
    public void downloadButtonClick(){
        downloadBitmap();
    }

    private void downloadBitmap() {
        ImageDownloadManager.getInstance(this).addToQueue(url, 0, imageView, new ImageDownloadManager.onDownloadListener() {
            @Override
            public void preUpdate() {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onProgressUpdate(int progress) {
                progressBar.setProgress(progress);
            }

            @Override
            public void onPostExecute(Bitmap bitmap) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onErrorListener(String message) {
                if (downloadButton.getVisibility() == View.GONE) {
                    downloadButton.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
                Toast.makeText(DetailsActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

