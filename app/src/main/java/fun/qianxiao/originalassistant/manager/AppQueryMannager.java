package fun.qianxiao.originalassistant.manager;

import androidx.annotation.NonNull;

import fun.qianxiao.originalassistant.appquery.AppQuerier;
import fun.qianxiao.originalassistant.appquery.IQuery;

/**
 * AppQueryMannager
 *
 * @Author QianXiao
 * @Date 2023/4/16
 */
public class AppQueryMannager {
    private volatile static AppQueryMannager instance;

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
    <T extends AppQuerier<?>> IQuery createQuerier(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    public void superQuery(String appName, String packageName, AppQuerier.OnAppQueryListener onAppQueryListener) {

        // TODO

    }
}
