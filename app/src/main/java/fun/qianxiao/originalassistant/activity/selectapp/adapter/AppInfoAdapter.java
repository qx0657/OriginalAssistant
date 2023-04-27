package fun.qianxiao.originalassistant.activity.selectapp.adapter;

import android.content.Context;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import fun.qianxiao.originalassistant.R;
import fun.qianxiao.originalassistant.base.BaseAdapter;
import fun.qianxiao.originalassistant.bean.AppInfo;

/**
 * AppInfoAdapter
 *
 * @Author QianXiao
 * @Date 2023/3/12
 */
public class AppInfoAdapter extends BaseAdapter<AppInfo, AppInfoAdapterViewHolder> implements Filterable {
    private Context context;
    private List<AppInfo> beforeSearchAppInfoList = new ArrayList<>();

    public AppInfoAdapter(Context context, List<AppInfo> dataList) {
        super(dataList);
        this.context = context;
        beforeSearchAppInfoList.addAll(dataList);
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_app_info;
    }

    @Override
    protected void onBindViewHolder(@NonNull AppInfoAdapterViewHolder holder, AppInfo appInfo) {
        holder.binding.tvAppName.setText(appInfo.getAppName());
        holder.binding.tvAppPackageName.setText(appInfo.getPackageName());
        Glide.with(holder.binding.ivAppIcon).load(appInfo.getIcon()).into(holder.binding.ivAppIcon);
        holder.binding.cardView.setOnClickListener(view -> {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(view, holder.getBindingAdapterPosition(), appInfo);
            }
        });
        holder.binding.cardView.setOnLongClickListener(view -> {
            if (itemLongClickListener != null) {
                return itemLongClickListener.onItemLongClick(view, holder.getBindingAdapterPosition(), appInfo);
            }
            return false;
        });
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if (charString.isEmpty()) {
                    dataList = beforeSearchAppInfoList;
                } else {
                    List<AppInfo> filteredList = new ArrayList<>();
                    for (AppInfo data : beforeSearchAppInfoList) {
                        if (data.getAppName().toString().contains(charString) ||
                                data.getPackageName().toString().contains(charString)) {
                            filteredList.add(data);
                        }
                    }
                    dataList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = dataList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                notifyDataSetChanged();
            }
        };
    }
}
