package fun.qianxiao.originalassistant.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;

import com.blankj.utilcode.util.LogUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Objects;

/**
 * TODO
 *
 * @Author QianXiao
 * @Date 2023/3/10
 */
public abstract class BaseFragment<T extends ViewBinding> extends Fragment {
    protected T binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = getBinding();
        assert binding != null;
        initListener();
        initData();
        return binding.getRoot();
    }

    /**
     * initListener
     */
    protected abstract void initListener();

    /**
     * initData
     */
    protected abstract void initData();

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
}
