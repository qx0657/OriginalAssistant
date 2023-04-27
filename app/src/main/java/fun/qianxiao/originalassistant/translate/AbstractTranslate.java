package fun.qianxiao.originalassistant.translate;

import com.blankj.utilcode.util.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Objects;

import fun.qianxiao.originalassistant.api.translate.TranslateApi;
import fun.qianxiao.originalassistant.manager.net.ApiServiceManager;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * AbstractTranslate<T>
 * T is the retrofit2 api service
 *
 * @Author QianXiao
 * @Date 2023/4/16
 */
public abstract class AbstractTranslate<T extends TranslateApi> implements ITranslate {
    private final T apiService;

    public AbstractTranslate() {
        apiService = ApiServiceManager.getInstance().create(getGenericType());
    }

    protected T getApi() {
        return apiService;
    }

    protected String getApiName() {
        try {
            Class<T> tClass = getGenericType();
            Field field = tClass.getDeclaredField("API_NAME");
            field.setAccessible(true);
            return (String) field.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return "";
    }

    @SuppressWarnings("unchecked")
    private Class<T> getGenericType() {
        return (Class<T>) ((ParameterizedType) Objects.requireNonNull(getClass().getGenericSuperclass())).getActualTypeArguments()[0];
    }

    @Override
    public void translate(String text, OnTranslateListener onTranslateListener) {
        LogUtils.i("translate use " + getApiName(), text);
        request(text).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull ResponseBody responseBody) {
                        try {
                            String data = responseBody.string();
                            JSONObject jsonObject = new JSONObject(data);

                            response(jsonObject, onTranslateListener);
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                            onTranslateListener.onTranslateResult(OnTranslateListener.TRANSLATE_ERROR, e.getMessage(), null);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        onTranslateListener.onTranslateResult(OnTranslateListener.TRANSLATE_ERROR, e.getMessage(), null);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * request
     *
     * @param text text
     * @return {@link Observable<ResponseBody>}
     */
    protected abstract Observable<ResponseBody> request(String text);

    /**
     * response
     *
     * @param jsonObject          jsonObject
     * @param onTranslateListener onTranslateListener
     */
    protected abstract void response(JSONObject jsonObject, OnTranslateListener onTranslateListener);
}
