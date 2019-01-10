package com.sccodesoft.schoolfinder;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


public class TabsAccessorAdapter extends FragmentPagerAdapter
{
    public TabsAccessorAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i)
    {
        switch (i)
        {
            case 0:
                NewSearchFragment newSearchFragment = new NewSearchFragment();
                return newSearchFragment;

            case 1:
                SearchHistoryFragment searchHistoryFragment = new SearchHistoryFragment();
                return searchHistoryFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount()
    {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        switch (position)
        {
            case 0:
                return "New Search";

            case 1:
                return "Search History";

            default:
                return null;
        }
    }

}
