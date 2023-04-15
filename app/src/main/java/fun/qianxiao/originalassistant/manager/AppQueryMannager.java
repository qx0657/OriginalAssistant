package fun.qianxiao.originalassistant.manager;

import fun.qianxiao.originalassistant.appquery.AppQuerier;
import fun.qianxiao.originalassistant.appquery.HLXAppQuerier;

/**
 * AppQueryMannager
 *
 * @Author QianXiao
 * @Date 2023/4/16
 */
public class AppQueryMannager {
    private volatile static AppQueryMannager instance;

    private AppQueryMannager() {
        AppQueryMannager.getInstance().createQuerier(HLXAppQuerier.class);
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

    public <T extends AppQuerier> AppQuerier createQuerier(Class<T> clazz) {
        if (clazz.getName().equals(HLXAppQuerier.class.getName())) {
            return new HLXAppQuerier();
        }

        // TODO

        throw new UnsupportedOperationException("not support " + clazz.getName());
    }

    public void superQuery(String appName, String packageName, AppQuerier.OnAppQueryListener onAppQueryListener) {

        // TODO

    }
}
