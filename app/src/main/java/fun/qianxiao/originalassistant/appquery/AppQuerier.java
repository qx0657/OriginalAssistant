package fun.qianxiao.originalassistant.appquery;

import com.blankj.utilcode.util.ThreadUtils;

import java.lang.reflect.ParameterizedType;
import java.util.Objects;

import fun.qianxiao.originalassistant.bean.AppQueryResult;
import fun.qianxiao.originalassistant.utils.net.ApiServiceManager;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * AppQuerier
 *
 * @Author QianXiao
 * @Date 2023/4/16
 */
public abstract class AppQuerier<T> implements IQuery {
    private T apiService;
    protected AppQueryResult appQueryResult;

    @SuppressWarnings("unchecked")
    protected T getApi() {
        if (apiService == null) {
            Class<T> tClass = (Class<T>) ((ParameterizedType) Objects.requireNonNull(getClass().getGenericSuperclass())).getActualTypeArguments()[0];
            apiService = ApiServiceManager.getInstance()
                    .create(tClass);
        }
        return apiService;
    }

    @Override
    public void query(String appName, String packageName, OnAppQueryListener onAppQueryListener) {
        ApiQueryResponseListener queryObserver = getApiQueryResponseListener();
        request(appName, packageName)
                .subscribeOn(Schedulers.newThread())
                .observeOn(getObserveThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull ResponseBody responseBody) {
                        appQueryResult = new AppQueryResult(appName, packageName);
                        final boolean[] error = {false};
                        final String[] errorMsg = new String[1];
                        queryObserver.onApiResponse(responseBody, new ApiQueryResponseListener.AnalysisResultInterface() {

                            @Override
                            public void onError(int code, String msg) {
                                error[0] = true;
                                errorMsg[0] = msg;
                            }

                            @Override
                            public OnAppQueryListener getOnAppQueryListener() {
                                return onAppQueryListener;
                            }
                        });
                        if (ThreadUtils.isMainThread()) {
                            if (!error[0]) {
                                onAppQueryListener.onResult(OnAppQueryListener.QUERY_CODE_SUCCESS, null, appQueryResult);
                            } else {
                                onAppQueryListener.onResult(OnAppQueryListener.QUERY_CODE_FAILED, errorMsg[0], null);
                            }
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

    protected Scheduler getObserveThread() {
        return AndroidSchedulers.mainThread();
    }

    protected abstract @NonNull AppQuerier.ApiQueryResponseListener getApiQueryResponseListener();

    protected abstract Observable<ResponseBody> request(String appName, String packageName);

    public interface ApiQueryResponseListener {

        interface AnalysisResultInterface {
            /**
             * onError
             *
             * @param code code
             * @param msg  msg
             */
            void onError(int code, String msg);

            /**
             * getOnAppQueryListener
             *
             * @return OnAppQueryListener
             */
            OnAppQueryListener getOnAppQueryListener();
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
