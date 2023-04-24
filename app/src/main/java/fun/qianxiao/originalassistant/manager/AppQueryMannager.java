package fun.qianxiao.originalassistant.manager;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import com.blankj.utilcode.util.ThreadUtils;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import fun.qianxiao.originalassistant.R;
import fun.qianxiao.originalassistant.appquery.AbstractAppQuerier;
import fun.qianxiao.originalassistant.appquery.BaifenAppQuerier;
import fun.qianxiao.originalassistant.appquery.HLXAppQuerier;
import fun.qianxiao.originalassistant.appquery.IQuery;
import fun.qianxiao.originalassistant.appquery.TapTapAppQuerier;
import fun.qianxiao.originalassistant.bean.AppQueryResult;

/**
 * AppQueryMannager
 *
 * @Author QianXiao
 * @Date 2023/4/16
 */
public class AppQueryMannager {
    private volatile static AppQueryMannager instance;
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    private AppQueryMannager() {
    }

    public static AppQueryMannager getInstance() {
        if (instance == null) {
            synchronized (AppQueryMannager.class) {
                if (instance == null) {
                    instance = new AppQueryMannager();
                }
            }
        }
        return instance;
    }

    public static @NonNull
    <T extends AbstractAppQuerier<?, ?>> IQuery createQuerier(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    public enum AppQueryChannel {
        /**
         * 葫芦侠
         */
        HLX(HLXAppQuerier.class, "葫芦侠", R.drawable.logo_huluxia),
        /**
         * TapTap
         */
        TAPTAP(TapTapAppQuerier.class, "TapTap", R.drawable.logo_taptap),
        /**
         * 百分网
         */
        BAIFEN(BaifenAppQuerier.class, "百分网", R.drawable.logo_baifen);

        private final Class<? extends AbstractAppQuerier<?, ?>> channel;
        private final String commonName;
        private final int iconRes;

        AppQueryChannel(Class<? extends AbstractAppQuerier<?, ?>> aClass, String commonName, int iconRes) {
            this.channel = aClass;
            this.commonName = commonName;
            this.iconRes = iconRes;
        }

        public Class<? extends AbstractAppQuerier<?, ?>> getChannel() {
            return channel;
        }

        public String getCommonName() {
            return commonName;
        }

        public int getIconRes() {
            return iconRes;
        }
    }

    /**
     * Query the APP and use each query in sequence until one is successful.
     * Note that should call in WorkerThread
     *
     * @param appName            appName
     * @param packageName        packageName
     * @param onAppQueryListener onAppQueryListener
     */
    @WorkerThread
    public synchronized void query(String appName, String packageName, AbstractAppQuerier.OnAppQueryListener onAppQueryListener) {
        final boolean[] success = {false};
        for (AppQueryChannel appQueryChannel : AppQueryChannel.values()) {
            AppQueryMannager.createQuerier(appQueryChannel.getChannel()).query(appName, packageName, new IQuery.OnAppQueryListener() {
                @Override
                @MainThread
                public void onResult(int code, String message, AppQueryResult appQueryResult) {
                    try {
                        lock.lock();
                        condition.signal();
                    } finally {
                        lock.unlock();
                    }
                    if (code == IQuery.OnAppQueryListener.QUERY_CODE_SUCCESS) {
                        success[0] = true;
                        onAppQueryListener.onResult(code, message, appQueryResult);
                    }
                }
            });
            try {
                lock.lock();
                condition.await(5500, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
            if (success[0]) {
                break;
            }
        }
        if (!success[0]) {
            ThreadUtils.runOnUiThread(() -> onAppQueryListener.onResult(IQuery.OnAppQueryListener.QUERY_CODE_FAILED,
                    "all api failed", null));
        }
    }
}
