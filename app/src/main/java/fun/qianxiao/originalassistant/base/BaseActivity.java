package fun.qianxiao.originalassistant.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

import com.blankj.utilcode.util.LogUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Objects;

/**
 * Universal View Binding Activity Abstract Base Class
 *
 * @param <T> ViewBinding
 * @author QianXiao
 * @Date 2023/3/10
 */
public abstract class BaseActivity<T extends ViewBinding> extends AppCompatActivity {
    protected Context context;
    protected T binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        binding = getBinding();
        assert binding != null;
        setContentView(binding.getRoot());
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void showBackIcon() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }
}
