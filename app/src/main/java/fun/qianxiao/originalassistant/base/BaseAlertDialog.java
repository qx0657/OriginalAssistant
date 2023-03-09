package fun.qianxiao.originalassistant.base;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.viewbinding.ViewBinding;

import com.blankj.utilcode.util.LogUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Objects;

/**
 * Create by QianXiao
 * On 2023/3/10
 */
public abstract class BaseAlertDialog<T extends ViewBinding> extends AlertDialog {
    protected Context context;
    protected View view;
    protected T binding;

    public BaseAlertDialog(@NonNull Context context) {
        super(context);
        this.context = context;
        Objects.requireNonNull(getWindow()).requestFeature(Window.FEATURE_NO_TITLE);
        ColorDrawable dw = new ColorDrawable(0x00000000);
        getWindow().setBackgroundDrawable(dw);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = getBinding();
        assert binding != null;
        view = binding.getRoot();
        setContentView(view);
        initListener();
        initData();
    }

    @SuppressWarnings("unchecked")
    private T getBinding() {
        try {
            Class<T> tClass = (Class<T>) ((ParameterizedType) Objects.requireNonNull(getClass().getGenericSuperclass())).getActualTypeArguments()[0];
            Method method = tClass.getMethod("inflate", LayoutInflater.class);
            return (T) method.invoke(null, getLayoutInflater());
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            LogUtils.e(e.toString());
            return null;
        }
    }

    /**
     * initListener
     */
    protected abstract void initListener();

    /**
     * initData
     */
    protected abstract void initData();
}
