package com.venkateshpamarthi.imagedownloader.features.Views;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.venkateshpamarthi.imagedownloader.R;
import com.venkateshpamarthi.imagedownloader.helper.bitmapUtilities.BitMapLruCache;
import com.venkateshpamarthi.imagedownloader.helper.customViews.CircularProgressBar;
import com.venkateshpamarthi.imagedownloader.network.ImageDownloadManager;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by venkateshpamarthi on 14/12/16.
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private static final String TAG = "ImageAdapter";

    private List<String> downloadList;
    private Context context;


    public ImageAdapter(Context context, List<String> downloadList) {
        this.context = context;
        this.downloadList = downloadList;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ImageViewHolder holder, final int position) {
        final String url = downloadList.get(position);
        holder.setItem(url);
        Bitmap bitmap = BitMapLruCache.getInstance().getBitmap(url);
        if (bitmap != null) {
            holder.downloadButton.setVisibility(View.GONE);
            holder.imageView.setImageBitmap(bitmap);
        }
        holder.downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.imageView.setTag(url);
                ImageDownloadManager.getInstance(context).addToQueue(url, position, holder.imageView, new ImageDownloadManager.onDownloadListener() {
                    @Override
                    public void preUpdate() {
                        holder.downloadButton.setVisibility(View.GONE);
                        holder.progressBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onProgressUpdate(int progress) {
                        holder.progressBar.setProgress(progress);
                    }

                    @Override
                    public void onPostExecute(Bitmap bitmap) {
                        holder.progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onErrorListener(String message) {
                        if(holder.downloadButton.getVisibility() == View.GONE) {
                            holder.downloadButton.setVisibility(View.VISIBLE);
                            holder.progressBar.setVisibility(View.GONE);
                        }
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return downloadList.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.imageView)
        ImageView imageView;
        @BindView(R.id.downloadButton)
        ImageButton downloadButton;
        @BindView(R.id.progressbar)
        CircularProgressBar progressBar;
        private String item;

        @OnClick(R.id.imageView)
        public void imageClicked(){
            Intent intent = new Intent(context, DetailsActivity.class);
            intent.putExtra("url",item);
            context.startActivity(intent);
        }
        public ImageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setItem(String item) {
            this.item = item;
        }
    }
}
