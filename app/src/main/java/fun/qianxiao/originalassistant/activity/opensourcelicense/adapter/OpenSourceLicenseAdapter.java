package fun.qianxiao.originalassistant.activity.opensourcelicense.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;

import java.util.List;

import fun.qianxiao.originalassistant.R;
import fun.qianxiao.originalassistant.activity.opensourcelicense.bean.OpenSourceLicense;
import fun.qianxiao.originalassistant.base.BaseAdapter;


/**
 * @author QianXiao
 */
public class OpenSourceLicenseAdapter extends BaseAdapter<OpenSourceLicense, OpenSourceLicenseAdapterViewHolder> {
    private Context context;

    public OpenSourceLicenseAdapter(List<OpenSourceLicense> dataList) {
        super(dataList);
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_opensourcelicense;
    }

    @Override
    protected void onBindViewHolder(@NonNull OpenSourceLicenseAdapterViewHolder holder, OpenSourceLicense openSourceLicense) {
        holder.binding.tvNameItemOsl.setText(openSourceLicense.getName());
        holder.binding.tvAuthorItemOsl.setText(openSourceLicense.getAnthor());
        holder.binding.tvDescribeItemOsl.setText(openSourceLicense.getDescribe());
        holder.binding.tvLicenseItemOsl.setText(openSourceLicense.getLicense());
        holder.binding.tvGoItemOsl.setOnClickListener(view -> context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(openSourceLicense.getUrl()))));
        holder.itemView.setOnClickListener(view -> context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(openSourceLicense.getUrl()))));

    }


}
