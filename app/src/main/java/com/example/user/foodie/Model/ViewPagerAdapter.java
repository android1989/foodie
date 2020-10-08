package com.example.user.foodie.Model;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.user.foodie.fragments.Food;
import com.example.user.foodie.fragments.Kitchen;

public class ViewPagerAdapter extends FragmentPagerAdapter
{
    public ViewPagerAdapter(FragmentManager fm)
    {
        super(fm);
    }
    @Override
    public Fragment getItem(int position)
    {
        if(position == 0)
        {
            return new Kitchen();
        }
        if(position == 1)
        {
            return new Food();
        }
        return null;
    }

    @Override
    public int getCount()
    {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position)
    {
        if(position == 0)
        {
            return "Restaurent";
        }
        if(position == 1)
        {
            return "Dish";
        }
        return null;
    }
}
