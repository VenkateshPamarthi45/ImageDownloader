package com.venkateshpamarthi.imagedownloader.features.Views;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.venkateshpamarthi.imagedownloader.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    @BindView(R.id.recyclerView_image)
    RecyclerView recyclerViewImage;
    ImageAdapter imageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        imageAdapter = new ImageAdapter(this, urlList());
        recyclerViewImage.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerViewImage.setItemAnimator(new DefaultItemAnimator());
        recyclerViewImage.setAdapter(imageAdapter);
    }

    private List<String> urlList(){
        List<String> strings = new ArrayList<>();
        strings.add("https://s3-ap-southeast-1.amazonaws.com/assets1.craftsvilla.com/AppLaunch/Deals/1_saree.png");
        strings.add("https://s3-ap-southeast-1.amazonaws.com/assets1.craftsvilla.com/AppLaunch/Deals/2_lehenga.png");
        strings.add("https://s3-ap-southeast-1.amazonaws.com/assets1.craftsvilla.com/AppLaunch/Deals/3_salwarsuit.png");
        strings.add("https://s3-ap-southeast-1.amazonaws.com/assets1.craftsvilla.com/AppLaunch/Deals/4_jewellery.png");
        strings.add("https://s3-ap-southeast-1.amazonaws.com/assets1.craftsvilla.com/AppLaunch/Deals/5_kurti.png");
        strings.add("https://s3-ap-southeast-1.amazonaws.com/assets1.craftsvilla.com/AppLaunch/Deals/6_men.png");
        strings.add("http://assets1.craftsvilla.com/banner-craftsvilla/cvfeeds/1481611397_avanya_131216_hero_app.jpg");
        strings.add("http://assets1.craftsvilla.com/banner-craftsvilla/cvfeeds/1481292207_party_91216_hero_app.jpg");
        strings.add("http://assets1.craftsvilla.com/banner-craftsvilla/cvfeeds/1481292183_gowns_91216_hero_app.jpg");
        strings.add("http://assets1.craftsvilla.com/banner-craftsvilla/cvfeeds/1481291241_indigo_91016_tile_hp.jpg");
        strings.add("http://assets1.craftsvilla.com/banner-craftsvilla/cvfeeds/1481291217_geometric_91016_tile_hp.jpg");
        strings.add("http://assets1.craftsvilla.com/banner-craftsvilla/cvfeeds/1481291482_palazzos_91016_tile_hp.jpg");
        return strings;
    }
}
