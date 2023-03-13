package fun.qianxiao.originalassistant;

import android.content.Intent;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;

import java.util.ArrayList;
import java.util.List;

import fun.qianxiao.originalassistant.adapter.MyPageAdapter;
import fun.qianxiao.originalassistant.base.BaseActivity;
import fun.qianxiao.originalassistant.checkupdate.CheckUpdateManager;
import fun.qianxiao.originalassistant.databinding.ActivityMainBinding;
import fun.qianxiao.originalassistant.fragment.find.FindFragment;
import fun.qianxiao.originalassistant.fragment.me.MeFragment;
import fun.qianxiao.originalassistant.fragment.original.OriginalFragment;
import fun.qianxiao.originalassistant.fragment.test.TestFragment;
import fun.qianxiao.originalassistant.utils.PermissionManager;
import fun.qianxiao.originalassistant.utils.PrivacyPolicyManager;

/**
 * MainActivity
 *
 * @Author QianXiao
 * @Date 2023/3/10
 */
public class MainActivity extends BaseActivity<ActivityMainBinding> {
    private final String[] PAGES_TITLES = new String[]{"原创助手", "测试助手", "发现", "我的"};
    private final String[] PAGES_TAB_TEXTS = new String[]{"原创", "测试", "发现", "我的"};
    private ArrayList<CustomTabEntity> tabEntities = new ArrayList<>();

    @Override
    protected void initListener() {
        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                KeyboardUtils.hideSoftInput(getWindow());
            }

            @Override
            public void onPageSelected(int position) {
                if (binding.tabLayout.getCurrentTab() != position) {
                    binding.tabLayout.setCurrentTab(position);
                }
                if (position >= 0 && position < PAGES_TITLES.length) {
                    setTitle(PAGES_TITLES[position]);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        binding.tabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                if (binding.viewPager.getCurrentItem() != position) {
                    KeyboardUtils.hideSoftInput(getWindow());
                    binding.viewPager.setCurrentItem(position);
                }
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
    }

    @Override
    protected void initData() {
        PrivacyPolicyManager privacyPolicyManager = new PrivacyPolicyManager(context);
        if (!privacyPolicyManager.isAgreePrivacyPolicy()) {
            privacyPolicyManager.confrim(new PrivacyPolicyManager.OnPrivacyPolicyListener() {
                @Override
                public void onAgree() {
                    requestPermission();
                }

                @Override
                public void onRefuse() {
                    AppUtils.exitApp();
                }

                private void requestPermission() {
                    if (!PermissionManager.getInstance().hasAllPermission()) {
                        PermissionManager.getInstance().requestNeeded();
                    }
                }
            });
        }

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new OriginalFragment<MainActivity>());
        fragments.add(new TestFragment<MainActivity>());
        fragments.add(new FindFragment<MainActivity>());
        fragments.add(new MeFragment<MainActivity>());
        MyPageAdapter adapter = new MyPageAdapter(getSupportFragmentManager(), fragments, PAGES_TAB_TEXTS);
        binding.viewPager.setAdapter(adapter);
        tabEntities.clear();
        for (int i = 0; i < PAGES_TAB_TEXTS.length; i++) {
            int finalI = i;
            tabEntities.add(new CustomTabEntity() {
                @Override
                public String getTabTitle() {
                    return PAGES_TAB_TEXTS[finalI];
                }

                @Override
                public int getTabSelectedIcon() {
                    return 0;
                }

                @Override
                public int getTabUnselectedIcon() {
                    return 0;
                }
            });
        }
        binding.tabLayout.setTabData(tabEntities);
        //startActivity(OpenSourceLicenseActivity.class);
        //BrowserActivity.load(context, "https://www.baidu.com/");
        new CheckUpdateManager(context).check(true);
    }

    private void startActivity(Class<?> ac) {
        this.startActivity(new Intent(context, ac));
    }

    public void setTabNavigationHide(boolean hide) {
        if (hide) {
            binding.tabLayout.setVisibility(View.INVISIBLE);
        } else {
            binding.tabLayout.setVisibility(View.VISIBLE);
        }
    }
}