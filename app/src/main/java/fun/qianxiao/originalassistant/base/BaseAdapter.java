package fun.qianxiao.originalassistant.base;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Objects;

/**
 * BaseAdapter
 *
 * @Author QianXiao
 * @Date 2023/3/10
 */
public abstract class BaseAdapter<BEAN, VH extends BaseRecycleViewHolder<?>> extends RecyclerView.Adapter<VH> {
    public final String TAG = "BaseAdapter";
    protected List<BEAN> dataList;
    protected ItemClickListener<BEAN> itemClickListener;
    protected ItemLongClickListener<BEAN> itemLongClickListener;

    public BaseAdapter(List<BEAN> dataList) {
        this.dataList = dataList;
    }

    public interface ItemClickListener<B> {
        /**
         * item click event
         *
         * @param v    view
         * @param bean item_data
         */
        void onItemClick(View v, B bean);
    }

    public interface ItemLongClickListener<B> {
        /**
         * item long-click event
         *
         * @param v    view
         * @param bean item_data
         */
        void onItemLongClick(View v, B bean);
    }

    public void setItemClickListener(ItemClickListener<BEAN> itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setItemLongClickListener(ItemLongClickListener<BEAN> itemLongClickListener) {
        this.itemLongClickListener = itemLongClickListener;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(getItemLayoutId(), parent, false);
        try {
            Class<VH> tClass = (Class<VH>) ((ParameterizedType) Objects.requireNonNull(getClass().getGenericSuperclass())).getActualTypeArguments()[1];
            Constructor<VH> constructor = tClass.getDeclaredConstructor(View.class);
            return (VH) constructor.newInstance(view);
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * getIteMLayoutId
     *
     * @return Item Layout Id
     */
    protected abstract int getItemLayoutId();

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        BEAN bean = dataList.get(position);
        onBindViewHolder(holder, position, bean);
    }

    /**
     * onBindViewHolder
     *
     * @param holder
     * @param bean
     */
    protected abstract void onBindViewHolder(@NonNull VH holder, int position, BEAN bean);

    public List<BEAN> getDataList() {
        return dataList;
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void clearData() {
        dataList.clear();
        notifyDataSetChanged();
    }
}
