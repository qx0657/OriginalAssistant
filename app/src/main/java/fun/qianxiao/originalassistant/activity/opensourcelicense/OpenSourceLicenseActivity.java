package fun.qianxiao.originalassistant.activity.opensourcelicense;

import android.graphics.Rect;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ConvertUtils;

import java.util.Arrays;

import fun.qianxiao.originalassistant.activity.opensourcelicense.adapter.OpenSourceLicenseAdapter;
import fun.qianxiao.originalassistant.activity.opensourcelicense.bean.OpenSourceLicense;
import fun.qianxiao.originalassistant.base.BaseActivity;
import fun.qianxiao.originalassistant.databinding.ActivityOpensourcelicenseBinding;


/**
 * @author QianXiao
 */
public class OpenSourceLicenseActivity extends BaseActivity<ActivityOpensourcelicenseBinding> {


    @Override
    protected void initListener() {

    }

    @Override
    protected void initData() {
        setTitle("三方库开源声明");
        showBackIcon();

        binding.rvOslActivity.setLayoutManager(new LinearLayoutManager(context));
        binding.rvOslActivity.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.set(0, 0, 0, ConvertUtils.dp2px(4));
            }
        });
        binding.rvOslActivity.setAdapter(new OpenSourceLicenseAdapter(Arrays.asList(
                new OpenSourceLicense("AndroidUtilCode", "Blankj", "AndroidUtilCode is a powerful & easy to use library for Android.", "Apache 2.0", "https://github.com/Blankj/AndroidUtilCode"),
                new OpenSourceLicense("OkHttp", "square", "An HTTP & HTTP/2 client for Android and java applications.", "Apache 2.0", "https://github.com/square/okhttp"),
                new OpenSourceLicense("Retrofit", "square", "A type-safe HTTP client for Android and Java.", "Apache 2.0", "https://github.com/square/retrofit"),
                new OpenSourceLicense("RxJava", "ReactiveX", "RxJava is a Java VM implementation of Reactive Extensions: a library for composing asynchronous and event-based programs by using observable sequences.", "Apache 2.0", "https://github.com/ReactiveX/RxJava"),
                new OpenSourceLicense("Glide", "bumptech", "Glide is a fast and efficient open source media management and image loading framework for Android that wraps media decoding, memory and disk caching, and resource pooling into a simple and easy to use interface.", "BSD, part MIT and Apache 2.0", "https://github.com/bumptech/glide"),
                new OpenSourceLicense("XPopup", "li-xiaojun", "XPopup2.0版本重磅来袭，2倍以上性能提升，带来可观的动画性能优化和交互细节的提升！！！功能强大，交互优雅，动画丝滑的通用弹窗！", "Apache 2.0", "https://github.com/li-xiaojun/XPopup"),
                new OpenSourceLicense("Logger", "orhanobut", "Simple, pretty and powerful logger for android", "Apache 2.0", "https://github.com/orhanobut/logger"),
                new OpenSourceLicense("FlycoTabLayout", "H07000223", "An Android TabLayout Lib has 3 kinds of TabLayout at present.", "Apache 2.0", "https://github.com/H07000223/FlycoTabLayout"),
                new OpenSourceLicense("FloatingActionButton", "zendesk", "Yet another library for drawing Material Design promoted actions.", "Apache 2.0", "https://github.com/H07000223/FlycoTabLayout"),
                new OpenSourceLicense("EasyHttp", "轮子哥", "简单易用的网络框架", "Apache 2.0", "https://github.com/getActivity/EasyHttp"),
                new OpenSourceLicense("ApacheCommonsTextUtils", "apache", "Apache Commons Text is a library focused on algorithms working on strings.", "Apache 2.0", "https://github.com/apache/commons-text")
        )));
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
