package com.example.user.foodie;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.example.user.foodie.Model.ViewPagerAdapter;

public class Favourite_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);

        Toolbar main_toolbar = findViewById(R.id.fav_toolbar);
        setSupportActionBar(main_toolbar);
        getSupportActionBar().setTitle("Favourites");

        ViewPager viewpager = findViewById(R.id.fav_viewpager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewpager.setAdapter(viewPagerAdapter);

        TabLayout main_tablayout = findViewById(R.id.fav_tab_layout);
        main_tablayout.setupWithViewPager(viewpager);

    }
}
