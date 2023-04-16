package fun.qianxiao.originalassistant.appquery;

import androidx.annotation.MainThread;

import fun.qianxiao.originalassistant.bean.AppQueryResult;

/**
 * IQuery
 *
 * @Author QianXiao
 * @Date 2023/4/16
 */
public interface IQuery {
    interface OnAppQueryListener {
        int QUERY_CODE_SUCCESS = 0;
        int QUERY_CODE_FAILED = -1;

        /**
         * onResult
         *
         * @param code           code
         * @param message        message
         * @param appQueryResult appQueryResult
         */
        @MainThread
        void onResult(int code, String message, AppQueryResult appQueryResult);
    }

    /**
     * query
     *
     * @param appName            appName
     * @param packageName        packageName
     * @param onAppQueryListener onAppQueryListener
     */
    void query(String appName, String packageName, OnAppQueryListener onAppQueryListener);
}
