package utiles;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.ricardopazdemiquel.moviles.Fragment.SetupViewPager_fragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edson on 08/06/2018.
 */

public class SectionsPageAdapter extends FragmentPagerAdapter {

    private final List<Fragment> mfFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList= new ArrayList<>();

    public void addFragment(Fragment fragment ,String title) {
        mfFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    public SectionsPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }

    @Override
    public Fragment getItem(int position) {
        return mfFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mfFragmentList.size();
    }
}
