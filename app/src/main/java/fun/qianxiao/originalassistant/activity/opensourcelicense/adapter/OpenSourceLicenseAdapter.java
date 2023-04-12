package fun.qianxiao.originalassistant.activity.opensourcelicense.adapter;

import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.ActivityUtils;

import java.util.List;

import fun.qianxiao.originalassistant.R;
import fun.qianxiao.originalassistant.activity.opensourcelicense.bean.OpenSourceLicense;
import fun.qianxiao.originalassistant.base.BaseAdapter;


/**
 * @author QianXiao
 */
public class OpenSourceLicenseAdapter extends BaseAdapter<OpenSourceLicense, OpenSourceLicenseAdapterViewHolder> {

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
        holder.binding.tvGoItemOsl.setOnClickListener(view -> gotoUrl(openSourceLicense.getUrl()));
        holder.itemView.setOnClickListener(view -> gotoUrl(openSourceLicense.getUrl()));
    }

    private void gotoUrl(String url) {
        ActivityUtils.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }
}
