package fun.qianxiao.originalassistant.activity.selectapp.adapter;

import android.content.Context;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;

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
public class AppInfoAdapter extends BaseAdapter<AppInfo, AppInfoAdapterViewHolder> {
    private Context context;

    public AppInfoAdapter(Context context, List<AppInfo> dataList) {
        super(dataList);
        this.context = context;
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
                itemClickListener.onItemClick(view, holder.getAdapterPosition(), appInfo);
            }
        });
        holder.binding.cardView.setOnLongClickListener(view -> {
            if (itemLongClickListener != null) {
                return itemLongClickListener.onItemLongClick(view, holder.getAdapterPosition(), appInfo);
            }
            return false;
        });
    }
}
