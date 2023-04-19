package fun.qianxiao.originalassistant.manager;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import com.blankj.utilcode.util.ThreadUtils;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import fun.qianxiao.originalassistant.appquery.AbstractAppQuerier;
import fun.qianxiao.originalassistant.appquery.BaifenAppQuery;
import fun.qianxiao.originalassistant.appquery.HLXAppQuerier;
import fun.qianxiao.originalassistant.appquery.IQuery;
import fun.qianxiao.originalassistant.appquery.TapTapAppQuery;
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
         * 葫芦侠;
         */
        HLX(HLXAppQuerier.class),
        /**
         * TapTap
         */
        TAPTAP(TapTapAppQuery.class),
        /**
         * Baifen
         */
        BAIFEN(BaifenAppQuery.class);

        private final Class<? extends AbstractAppQuerier<?, ?>> channel;

        AppQueryChannel(Class<? extends AbstractAppQuerier<?, ?>> aClass) {
            this.channel = aClass;
        }

        public Class<? extends AbstractAppQuerier<?, ?>> getChannel() {
            return channel;
        }
    }

    /**
     * 自动查询
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
