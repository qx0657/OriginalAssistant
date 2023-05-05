package fun.qianxiao.originalassistant;

import android.content.Intent;
import android.view.View;

import androidx.viewpager.widget.ViewPager;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;

import java.util.ArrayList;
import java.util.List;

import fun.qianxiao.originalassistant.adapter.MyPageAdapter;
import fun.qianxiao.originalassistant.base.BaseActivity;
import fun.qianxiao.originalassistant.base.BaseFragment;
import fun.qianxiao.originalassistant.bean.AppInfo;
import fun.qianxiao.originalassistant.databinding.ActivityMainBinding;
import fun.qianxiao.originalassistant.fragment.find.FindFragment;
import fun.qianxiao.originalassistant.fragment.me.MeFragment;
import fun.qianxiao.originalassistant.fragment.original.OriginalFragment;
import fun.qianxiao.originalassistant.fragment.test.TestFragment;
import fun.qianxiao.originalassistant.manager.CheckUpdateManager;
import fun.qianxiao.originalassistant.manager.PermissionManager;
import fun.qianxiao.originalassistant.manager.PrivacyPolicyManager;
import fun.qianxiao.originalassistant.utils.AppListTool;
import fun.qianxiao.originalassistant.view.loading.ILoadingView;
import fun.qianxiao.originalassistant.view.loading.MyLoadingDialog;

/**
 * MainActivity
 *
 * @author QianXiao
 * @since 2023/3/10
 */
public class MainActivity extends BaseActivity<ActivityMainBinding> implements ILoadingView {
    private final String[] PAGES_TITLES = new String[]{"原创助手", "测试助手", "发现", "我的"};
    private final String[] PAGES_TAB_TEXTS = new String[]{"原创", "测试", "发现", "我的"};
    private final ArrayList<CustomTabEntity> tabEntities = new ArrayList<>();
    private List<BaseFragment<?, MainActivity>> fragments = new ArrayList<>();
    private int currentPosition;
    private MyLoadingDialog loadingDialog;

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
                    currentPosition = position;
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
        PrivacyPolicyManager privacyPolicyManager = PrivacyPolicyManager.getInstance();
        if (!privacyPolicyManager.isAgreePrivacyPolicy()) {
            privacyPolicyManager.confrim(context, new PrivacyPolicyManager.OnPrivacyPolicyListener() {
                @Override
                public void onAgree() {
                    // requestPermission();
                    MyApplication.uengInit();
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
        } else {
            MyApplication.uengInit();
        }
        fragments.clear();
        fragments.add(new OriginalFragment<>());
        fragments.add(new TestFragment<>());
        fragments.add(new FindFragment<>());
        fragments.add(new MeFragment<>());
        MyPageAdapter adapter = new MyPageAdapter(getSupportFragmentManager(), fragments, PAGES_TAB_TEXTS);
        binding.viewPager.setAdapter(adapter);
        binding.viewPager.setOffscreenPageLimit(4);
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

        preLoadAppList();
        if (privacyPolicyManager.isAgreePrivacyPolicy()) {
            CheckUpdateManager.getInstance().check(context, true);
        }
    }

    private void preLoadAppList() {
        ThreadUtils.executeBySingle(new ThreadUtils.SimpleTask<List<AppInfo>>() {
            @Override
            public List<AppInfo> doInBackground() throws Throwable {
                return AppListTool.getAppList(context);
            }

            @Override
            public void onSuccess(List<AppInfo> result) {

            }
        });
    }

    private void startActivity(Class<?> ac) {
        this.startActivity(new Intent(context, ac));
    }

    @Override
    public void onBackPressed() {
        if (fragments.get(currentPosition).onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }

    public void setTabNavigationHide(boolean hide) {
        if (hide) {
            binding.tabLayout.setVisibility(View.INVISIBLE);
        } else {
            binding.tabLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void openLoadingDialog(String msg) {
        if (loadingDialog == null) {
            loadingDialog = new MyLoadingDialog(context);
        }
        if (!loadingDialog.isShowing()) {
            loadingDialog.setMessage(msg);
            loadingDialog.show();
        }
    }

    @Override
    public void closeLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }
}