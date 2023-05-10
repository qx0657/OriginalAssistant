package fun.qianxiao.originalassistant.activity.selectapp;

import android.content.Intent;
import android.os.Build;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AutoCompleteTextView;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.util.List;

import fun.qianxiao.originalassistant.R;
import fun.qianxiao.originalassistant.activity.selectapp.adapter.AppInfoAdapter;
import fun.qianxiao.originalassistant.base.BaseActivity;
import fun.qianxiao.originalassistant.base.BaseAdapter;
import fun.qianxiao.originalassistant.bean.AppInfo;
import fun.qianxiao.originalassistant.databinding.ActivitySelectAppBinding;
import fun.qianxiao.originalassistant.utils.AppListTool;
import fun.qianxiao.originalassistant.view.loading.ILoadingView;
import fun.qianxiao.originalassistant.view.loading.MyLoadingDialog;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * SelectAppActivity
 *
 * @Author QianXiao
 * @Date 2023/3/12
 */
public class SelectAppActivity extends BaseActivity<ActivitySelectAppBinding> implements ILoadingView {
    public static final int RESULT_CODE_SELECT_APP_NONE = -1;
    public static final int RESULT_CODE_SELECT_APP_OK = 0;
    public static final String KEY_APP_NAME = "KEY_APP_NAME";
    public static final String KEY_APP_PACKAGE_NAME = "KEY_APP_PACKAGE_NAME";

    private AppInfoAdapter adapter;
    private SearchView searchView;
    private MyLoadingDialog loadingDialog;

    private final BaseAdapter.ItemClickListener<AppInfo> itemClickListener = (v, pos, bean) -> {
        Intent data = new Intent();
        data.putExtra(KEY_APP_NAME, bean.getAppName());
        data.putExtra(KEY_APP_PACKAGE_NAME, bean.getPackageName());
        SelectAppActivity.this.setResult(RESULT_CODE_SELECT_APP_OK, data);
        SelectAppActivity.this.finish();
    };

    private final AppInfoAdapter.OnItemLongClickPopItemSelectListener onItemLongClickPopItemSelectListener = new AppInfoAdapter.OnItemLongClickPopItemSelectListener() {
        @Override
        public void onPopItemSelectAppName(AppInfo appInfo) {

        }
    };

    @Override
    protected void initListener() {
        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAppList(false);
            }
        });
    }

    @Override
    protected void initData() {
        setTitle("选择应用");
        showBackIcon();
        binding.rvAppList.setLayoutManager(new LinearLayoutManager(context));
        getAppList(true);
    }

    private void initRecycleViewAnim() {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.app_info_item_anim);
        LayoutAnimationController layoutAnimationController = new LayoutAnimationController(animation);
        layoutAnimationController.setOrder(LayoutAnimationController.ORDER_NORMAL);
        layoutAnimationController.setDelay(0.1f);
        binding.rvAppList.setLayoutAnimation(layoutAnimationController);
    }

    private void getAppList(boolean isShowLoadingDialog) {
        final long startTime = TimeUtils.getNowMills();
        Observable.create((ObservableOnSubscribe<List<AppInfo>>) emitter -> {
            List<AppInfo> appInfoList = AppListTool.getAppList(context);
            emitter.onNext(appInfoList);
            emitter.onComplete();
        }).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<AppInfo>>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                        if (isShowLoadingDialog) {
                            openLoadingDialog("加载中");
                        }
                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull List<AppInfo> appInfoList) {
                        if (!appInfoList.isEmpty()) {
                            final long endTime = TimeUtils.getNowMills();
                            long spendTime = endTime - startTime;
                            LogUtils.i("getAppList size " + appInfoList.size() + " spend time: " + spendTime + "ms");

                            showAppList(appInfoList);
                        } else {
                            ToastUtils.showShort("获取App列表为空");
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                        ToastUtils.showShort("获取App列表出错: " + e.toString());
                    }

                    @Override
                    public void onComplete() {
                        if (isShowLoadingDialog) {
                            closeLoadingDialog();
                        }
                        if (binding.swipeRefreshLayout.isRefreshing()) {
                            binding.swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                });
    }

    private void showAppList(List<AppInfo> appInfoList) {
        adapter = new AppInfoAdapter(context, appInfoList);
        adapter.setItemClickListener(itemClickListener);
        adapter.setOnItemLongClickPopItemSelectListener(onItemLongClickPopItemSelectListener);
        binding.rvAppList.setAdapter(adapter);
        initRecycleViewAnim();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_select_app, menu);
        initSearchView(menu);
        return true;
    }

    private void initSearchView(Menu menu) {
        MenuItem menuItem = menu.findItem(R.id.menu_item_search);
        searchView = (SearchView) menuItem.getActionView();
        View view = searchView.getRootView().findViewById(R.id.search_src_text);
        if (view instanceof AutoCompleteTextView) {
            AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) view;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                autoCompleteTextView.setTextCursorDrawable(R.drawable.edit_text_cursor_white);
            }
        }
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                doSearch(newText);
                return true;
            }
        });
    }

    private void doSearch(String newText) {
        if (adapter == null) {
            return;
        }
        adapter.getFilter().filter(newText);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (searchView != null && !searchView.isIconified()) {
                searchView.setIconified(true);
                return false;
            }
            setResult(RESULT_CODE_SELECT_APP_NONE);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
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
