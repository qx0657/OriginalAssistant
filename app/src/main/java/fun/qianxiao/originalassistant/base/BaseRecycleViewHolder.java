package fun.qianxiao.originalassistant.base;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.blankj.utilcode.util.LogUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Objects;

/**
 * Create by QianXiao
 * On 2023/3/10
 *
 * @author QianXiao
 */
public abstract class BaseRecycleViewHolder<T extends ViewBinding> extends RecyclerView.ViewHolder {
    protected View view;
    public T binding;

    public BaseRecycleViewHolder(@NonNull View itemView) {
        super(itemView);
        this.view = itemView;
        binding = getBinding();
    }

    @SuppressWarnings("unchecked")
    protected T getBinding() {
        try {
            Class<T> tClass = (Class<T>) ((ParameterizedType) Objects.requireNonNull(getClass().getGenericSuperclass())).getActualTypeArguments()[0];
            Method method = tClass.getMethod("bind", View.class);
            return (T) method.invoke(null, view);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            LogUtils.e(e.toString());
            return null;
        }
    }
}
