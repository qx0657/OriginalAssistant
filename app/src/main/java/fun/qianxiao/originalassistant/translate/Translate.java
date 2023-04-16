package fun.qianxiao.originalassistant.translate;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.Objects;

import fun.qianxiao.originalassistant.utils.net.ApiServiceManager;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * Translate
 *
 * @Author QianXiao
 * @Date 2023/4/16
 */
public abstract class Translate<T> implements ITranslate {
    private T apiService;

    @SuppressWarnings("unchecked")
    protected T getApi() {
        if (apiService == null) {
            Class<T> tClass = (Class<T>) ((ParameterizedType) Objects.requireNonNull(getClass().getGenericSuperclass())).getActualTypeArguments()[0];
            apiService = ApiServiceManager.getInstance()
                    .create(tClass);
        }
        return apiService;
    }

    protected abstract void response(JSONObject jsonObject, OnTranslateListener onTranslateListener);

    @Override
    public void translate(String en, OnTranslateListener onTranslateListener) {
        request(en).subscribeOn(Schedulers.newThread())
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

    protected abstract Observable<ResponseBody> request(String en);
}
