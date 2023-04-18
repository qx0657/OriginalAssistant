package fun.qianxiao.originalassistant.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

import fun.qianxiao.originalassistant.MainActivity;
import fun.qianxiao.originalassistant.base.BaseFragment;

/**
 * MyPageAdapter
 *
 * @Author QianXiao
 * @Date 2023/3/10
 */
public class MyPageAdapter extends FragmentPagerAdapter {
    private List<BaseFragment<?, MainActivity>> fragments;
    private String[] titles;


    public MyPageAdapter(@NonNull FragmentManager fm, List<BaseFragment<?, MainActivity>> fragments, String[] titles) {
        super(fm);
        this.fragments = fragments;
        this.titles = titles;
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }
}
