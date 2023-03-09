package fun.qianxiao.originalassistant.activity.opensourcelicense;

import android.graphics.Rect;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
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
        setTitle("开源许可");

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        binding.rvOslActivity.setLayoutManager(new LinearLayoutManager(context));
        binding.rvOslActivity.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                //super.getItemOffsets(outRect, view, parent, state);
                outRect.set(0, 0, 0, ConvertUtils.dp2px(4));
            }
        });
        binding.rvOslActivity.setAdapter(new OpenSourceLicenseAdapter(Arrays.asList(
                new OpenSourceLicense("NCalendar", "yannecer", "一款安卓日历，仿miui，钉钉，华为的日历，万年历、365、周日历，月日历，月视图、周视图滑动切换，农历，节气，Andriod Calendar , MIUI Calendar,小米日历", "Apache 2.0", "https://github.com/yannecer/NCalendar"),
                new OpenSourceLicense("Lunar", "6tail", "lunar是一款无第三方依赖的公历(阳历)、农历(阴历、老黄历)、道历、佛历工具，支持星座、儒略日、干支、生肖、节气、节日、彭祖百忌、吉神(喜神/福神/财神/阳贵神/阴贵神)方位、胎神方位、冲煞、纳音、星宿、八字、五行、十神、建除十二值星、青龙名堂等十二神、黄道日及吉凶、法定节假日及调休等。", "Apache 2.0", "https://github.com/6tail/lunar-java"),
                new OpenSourceLicense("Android-PickerView", "Bigkoo", "这是一款仿iOS的PickerView控件，有时间选择器和选项选择器", "Apache 2.0", "https://github.com/Bigkoo/Android-PickerView"),
                new OpenSourceLicense("OkHttp", "square", "An HTTP & HTTP/2 client for Android and java applications.", "Apache 2.0", "https://github.com/square/okhttp"),
                new OpenSourceLicense("Retrofit", "square", "A type-safe HTTP client for Android and Java.", "Apache 2.0", "https://github.com/square/retrofit"),
                new OpenSourceLicense("RxJava", "ReactiveX", "RxJava is a Java VM implementation of Reactive Extensions: a library for composing asynchronous and event-based programs by using observable sequences.", "Apache 2.0", "https://github.com/ReactiveX/RxJava"),
                new OpenSourceLicense("Glide", "bumptech", "Glide is a fast and efficient open source media management and image loading framework for Android that wraps media decoding, memory and disk caching, and resource pooling into a simple and easy to use interface.", "BSD, part MIT and Apache 2.0", "https://github.com/bumptech/glide"),
                new OpenSourceLicense("AndroidUtilCode", "Blankj", "AndroidUtilCode is a powerful & easy to use library for Android.", "Apache 2.0", "https://github.com/Blankj/AndroidUtilCode"),
                new OpenSourceLicense("XPopup", "li-xiaojun", "XPopup2.0版本重磅来袭，2倍以上性能提升，带来可观的动画性能优化和交互细节的提升！！！功能强大，交互优雅，动画丝滑的通用弹窗！", "Apache 2.0", "https://github.com/li-xiaojun/XPopup"),
                //new OpenSourceLicense("DKVideoPlayer","Doikki","A video player for Android.","Apache 2.0","https://github.com/Doikki/DKVideoPlayer"),
                new OpenSourceLicense("FlycoTabLayout", "H07000223", "An Android TabLayout Lib has 3 kinds of TabLayout at present.", "Apache 2.0", "https://github.com/H07000223/FlycoTabLayout"),
                new OpenSourceLicense("GSON", "google", "Gson is a Java library that can be used to convert Java Objects into their JSON representation. ", "Apache 2.0", "https://github.com/google/gson"),
                new OpenSourceLicense("AgentWeb", "Justson", "AgentWeb 是一个基于的 Android WebView ，极度容易使用以及功能强大的库，提供了 Android WebView 一系列的问题解决方案 ，并且轻量和极度灵活", "Apache 2.0", "https://github.com/Justson/AgentWeb"),
                new OpenSourceLicense("ActiveAndroid", "pardom-zz", "ActiveAndroid is an active record style ORM (object relational mapper). What does that mean exactly? Well, ActiveAndroid allows you to save and retrieve SQLite database records without ever writing a single SQL statement. Each database record is wrapped neatly into a class with methods like save() and delete().", "Apache 2.0", "https://github.com/pardom-zz/ActiveAndroid"),
                new OpenSourceLicense("EasyHttp", "轮子哥", "简单易用的网络框架", "Apache 2.0", "https://github.com/getActivity/EasyHttp")
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
