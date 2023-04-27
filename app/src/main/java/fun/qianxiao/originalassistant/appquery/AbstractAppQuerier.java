package fun.qianxiao.originalassistant.appquery;

import android.text.TextUtils;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ThreadUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Objects;

import fun.qianxiao.originalassistant.api.appquery.AppQueryaApi;
import fun.qianxiao.originalassistant.bean.AnalysisResult;
import fun.qianxiao.originalassistant.manager.net.ApiServiceManager;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * AbstractAppQuerier<T extends AppQueryaApi, R>
 * T is the retrofit2 api service. sub interface of {@link AppQueryaApi}
 * R is response object class, support {@link JSONObject}、{@link ResponseBody}
 *
 * @Author QianXiao
 * @Date 2023/4/16
 */
public abstract class AbstractAppQuerier<T extends AppQueryaApi, R> implements IQuery {
    private final T apiService;

    @SuppressWarnings("unchecked")
    public AbstractAppQuerier() {
        apiService = ApiServiceManager.getInstance().create((Class<T>) getGenericType(0));
    }

    protected T getApi() {
        return apiService;
    }

    protected String getApiName() {
        try {
            Class<?> tClass = getGenericType(0);
            Field field = tClass.getDeclaredField("API_NAME");
            field.setAccessible(true);
            return (String) field.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return "";
    }

    @SuppressWarnings("unchecked")
    private Class<?> getGenericType(int index) {
        return (Class<?>) ((ParameterizedType) Objects.requireNonNull(getClass().getGenericSuperclass())).getActualTypeArguments()[index];
    }

    @SuppressWarnings("unchecked")
    private R responseTypeConvert(ResponseBody responseBody) throws IOException, JSONException {
        R r;
        if (getGenericType(1) == JSONObject.class) {
            String data = responseBody.string();
            JSONObject jsonObject = new JSONObject(data);
            r = (R) jsonObject;
        } else {
            r = (R) responseBody;
        }
        return r;
    }

    @Override
    public void query(String appName, String packageName, OnAppQueryListener onAppQueryListener) {
        LogUtils.i("app query use " + getApiName(), appName, packageName);
        AnalysisResult analysisResult = new AnalysisResult();
        analysisResult.setApi(getApiName());
        analysisResult.getAppQueryResult().setAppName(appName);
        analysisResult.getAppQueryResult().setPackageName(packageName);
        search(appName, packageName)
                .flatMap(new Function<ResponseBody, ObservableSource<ResponseBody>>() {
                    @Override
                    public ObservableSource<ResponseBody> apply(ResponseBody responseBody) throws Throwable {
                        R r = responseTypeConvert(responseBody);
                        return searchResponseAnalysisAndDetail(r, analysisResult);
                    }
                })
                .map(new Function<ResponseBody, AnalysisResult>() {
                    @Override
                    public AnalysisResult apply(ResponseBody responseBody) throws Throwable {
                        R r = responseTypeConvert(responseBody);
                        detailResponseAnalysis(r, analysisResult);
                        return analysisResult;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AnalysisResult>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull AnalysisResult analysisResult) {
                        if (analysisResult.isSuccess()) {
                            onAppQueryListener.onResult(OnAppQueryListener.QUERY_CODE_SUCCESS, null, analysisResult.getAppQueryResult());
                        } else {
                            onAppQueryListener.onResult(OnAppQueryListener.QUERY_CODE_FAILED, analysisResult.getErrorMsg(), null);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                        ThreadUtils.runOnUiThread(() -> {
                            if (!TextUtils.isEmpty(analysisResult.getAppQueryResult().getAppIntroduction())) {
                                LogUtils.e(e.toString());
                                onAppQueryListener.onResult(OnAppQueryListener.QUERY_CODE_SUCCESS, e.getMessage(), analysisResult.getAppQueryResult());
                            } else {
                                onAppQueryListener.onResult(OnAppQueryListener.QUERY_CODE_FAILED, e.getMessage(), null);
                            }
                        });
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * search
     *
     * @param appName     appName
     * @param packageName packageName
     * @return {@link Observable<ResponseBody>}
     */
    protected abstract Observable<ResponseBody> search(String appName, String packageName);

    /**
     * searchResponseAnalysisAndDetail
     *
     * @param searchResponse searchResponse
     * @param analysisResult analysisResult
     * @return {@link Observable<ResponseBody>}
     */
    protected abstract Observable<ResponseBody> searchResponseAnalysisAndDetail(R searchResponse, AnalysisResult analysisResult);

    /**
     * detailResponseAnalysis
     *
     * @param detailResponseJsonObject responseBody
     * @param analysisResult           analysisResult
     */
    protected abstract void detailResponseAnalysis(R detailResponseJsonObject, AnalysisResult analysisResult);
}
