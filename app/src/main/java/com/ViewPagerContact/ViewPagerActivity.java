package com.ViewPagerContact;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.styledmap.R;

public class ViewPagerActivity extends AppCompatActivity {

    FragmentPagerAdapter adapterViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);

        ViewPager viewPager = (ViewPager) findViewById(R.id.vp_pager);
        adapterViewPager = new FragmentPageAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapterViewPager);

        Toolbar mToolbar =
                (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle(getString(R.string.create_event_title));

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout_event_page);
        tabLayout.setupWithViewPager(viewPager);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class FragmentPageAdapter extends FragmentPagerAdapter {

        private static int NUM_ITEMS = 3;

        FragmentPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return PartecipantsFragment.newInstance(0);
                case 1:
                    return CondivisionPageFragment.newInstance(1);
                case 2:
                    return PartecipantsFragment.newInstance(2);
                default:
                    return null;

            }
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }
    }

}
