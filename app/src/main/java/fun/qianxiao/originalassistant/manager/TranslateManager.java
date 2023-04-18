package fun.qianxiao.originalassistant.manager;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import com.blankj.utilcode.util.ThreadUtils;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import fun.qianxiao.originalassistant.translate.AbstractTranslate;
import fun.qianxiao.originalassistant.translate.BaiduTranslate;
import fun.qianxiao.originalassistant.translate.GoogleTranslate;
import fun.qianxiao.originalassistant.translate.ITranslate;
import fun.qianxiao.originalassistant.translate.YoudaoTranslate;

/**
 * TranslateManager
 *
 * @Author QianXiao
 * @Date 2023/4/16
 */
public class TranslateManager {
    private volatile static TranslateManager instance;
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    public enum TranslateInterfaceType {
        /**
         * 有道;
         */
        YOUDAO_TRANSLATE(YoudaoTranslate.class),
        /**
         * 百度;
         */
        BAIDU_TRANSLATE(BaiduTranslate.class),
        /**
         * 谷歌;
         */
        GOOGLE_TRANSLATE(GoogleTranslate.class);

        private Class<? extends AbstractTranslate<?>> channel;

        TranslateInterfaceType(Class<? extends AbstractTranslate<?>> channel) {
            this.channel = channel;
        }

        public Class<? extends AbstractTranslate<?>> getChannel() {
            return channel;
        }
    }

    private TranslateManager() {
    }

    public static TranslateManager getInstance() {
        if (instance == null) {
            synchronized (TranslateManager.class) {
                if (instance == null) {
                    instance = new TranslateManager();
                }
            }
        }
        return instance;
    }

    public static @NonNull
    <T extends AbstractTranslate<?>> ITranslate createTranslater(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    /**
     * 自动翻译
     * Note that should call in WorkerThread
     *
     * @param text                text
     * @param onTranslateListener onTranslateListener
     */
    @WorkerThread
    public void translate(String text, ITranslate.OnTranslateListener onTranslateListener) {
        final boolean[] success = {false};
        for (TranslateInterfaceType translateInterfaceType : TranslateInterfaceType.values()) {
            TranslateManager.createTranslater(translateInterfaceType.getChannel()).translate(
                    text, new ITranslate.OnTranslateListener() {
                        @Override
                        @MainThread
                        public void onTranslateResult(int code, String msg, String result) {
                            try {
                                lock.lock();
                                condition.signal();
                            } finally {
                                lock.unlock();
                            }
                            if (code == ITranslate.OnTranslateListener.TRANSLATE_SUCCESS) {
                                success[0] = true;
                                onTranslateListener.onTranslateResult(code, msg, result);
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
            ThreadUtils.runOnUiThread(() -> onTranslateListener.onTranslateResult(ITranslate.OnTranslateListener.TRANSLATE_ERROR,
                    "all api failed", null));
        }
    }

}
