package fun.qianxiao.originalassistant.activity.selectapp.adapter;

import android.view.View;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;

import java.util.List;

import fun.qianxiao.originalassistant.R;
import fun.qianxiao.originalassistant.base.BaseAdapter;
import fun.qianxiao.originalassistant.bean.AppInfo;

/**
 * TODO
 *
 * @Author QianXiao
 * @Date 2023/3/12
 */
public class AppInfoAdapter extends BaseAdapter<AppInfo, AppInfoAdapterViewHolder> {
    public AppInfoAdapter(List<AppInfo> dataList) {
        super(dataList);
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_app_info;
    }

    @Override
    protected void onBindViewHolder(@NonNull AppInfoAdapterViewHolder holder, AppInfo appInfo) {
        holder.binding.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListener.onItemClick(appInfo);
            }
        });
        holder.binding.tvAppName.setText(appInfo.getAppName());
        holder.binding.tvAppPackageName.setText(appInfo.getPackageName());
        Glide.with(holder.binding.ivAppIcon).load(appInfo.getIcon()).into(holder.binding.ivAppIcon);
    }
}
