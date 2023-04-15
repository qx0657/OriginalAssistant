package fun.qianxiao.originalassistant.appquery;

import java.util.List;

import fun.qianxiao.originalassistant.bean.AppQueryResult;
import fun.qianxiao.originalassistant.utils.net.ApiServiceManager;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * AppQuerier
 *
 * @Author QianXiao
 * @Date 2023/4/16
 */
public abstract class AppQuerier {

    public interface OnAppQueryListener {
        int QUERY_CODE_SUCCESS = 0;
        int QUERY_CODE_FAILED = -1;

        /**
         * onResult
         *
         * @param code           code
         * @param message        message
         * @param appQueryResult appQueryResult
         */
        void onResult(int code, String message, AppQueryResult appQueryResult);
    }


    protected <T> T createApi(Class<T> service) {
        return ApiServiceManager.getInstance()
                .create(service);
    }

    /**
     * query
     *
     * @param appName            appName
     * @param packageName        packageName
     * @param onAppQueryListener onAppQueryListener
     */
    public void query(String appName, String packageName, OnAppQueryListener onAppQueryListener) {
        ApiQueryResponseListener queryObserver = getApiQueryResponseListener();
        request(appName, packageName)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull ResponseBody responseBody) {
                        AppQueryResult appQueryResult = new AppQueryResult(appName, packageName);
                        final boolean[] error = {false};
                        final String[] errorMsg = new String[1];
                        queryObserver.onApiResponse(responseBody, new ApiQueryResponseListener.AnalysisResultInterface() {

                            @Override
                            public String getAppName() {
                                return appName;
                            }

                            @Override
                            public String getAppPackageName() {
                                return packageName;
                            }

                            @Override
                            public void setIntroduction(String introduction) {
                                appQueryResult.setAppIntroduction(introduction);
                            }

                            @Override
                            public void setPicture(List<String> appPictures) {
                                appQueryResult.setAppPictures(appPictures);
                            }

                            @Override
                            public void onError(int code, String msg) {
                                error[0] = true;
                                errorMsg[0] = msg;
                            }
                        });
                        if (!error[0]) {
                            onAppQueryListener.onResult(OnAppQueryListener.QUERY_CODE_SUCCESS, null, appQueryResult);
                        } else {
                            onAppQueryListener.onResult(OnAppQueryListener.QUERY_CODE_FAILED, errorMsg[0], null);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                        onAppQueryListener.onResult(OnAppQueryListener.QUERY_CODE_FAILED, e.getMessage(), null);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    protected abstract @NonNull AppQuerier.ApiQueryResponseListener getApiQueryResponseListener();

    protected abstract Observable<ResponseBody> request(String appName, String packageName);

    public interface ApiQueryResponseListener {

        public interface AnalysisResultInterface {
            /**
             * getAppName
             *
             * @return appName
             */
            String getAppName();

            /**
             * getAppPackageName
             *
             * @return packageName
             */
            String getAppPackageName();

            /**
             * setIntroduction
             *
             * @param introduction introduction
             */
            void setIntroduction(String introduction);

            /**
             * setPicture
             *
             * @param appPictures appPictures
             */
            void setPicture(List<String> appPictures);

            /**
             * onError
             *
             * @param code code
             * @param msg  msg
             */
            void onError(int code, String msg);
        }

        /**
         * onApiResponse
         *
         * @param response       responseBody
         * @param queryInterface queryInterface
         */
        void onApiResponse(@NonNull ResponseBody response, AnalysisResultInterface queryInterface);
    }
}
