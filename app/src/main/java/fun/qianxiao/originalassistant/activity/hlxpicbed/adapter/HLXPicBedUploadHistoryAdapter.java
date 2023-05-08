package fun.qianxiao.originalassistant.activity.hlxpicbed.adapter;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import java.util.List;

import fun.qianxiao.originalassistant.R;
import fun.qianxiao.originalassistant.base.BaseAdapter;
import fun.qianxiao.originalassistant.bean.HLXPicBedUploadHistory;

/**
 * HLXPicBedUploadHistoryAdapter
 *
 * @Author QianXiao
 * @Date 2023/5/8
 */
public class HLXPicBedUploadHistoryAdapter extends BaseAdapter<HLXPicBedUploadHistory, HLXPicBedUploadHistoryAdapterViewHolder> {

    public HLXPicBedUploadHistoryAdapter(List<HLXPicBedUploadHistory> dataList) {
        super(dataList);
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_hlx_pic_bed_upload_history;
    }

    @Override
    protected void onBindViewHolder(@NonNull HLXPicBedUploadHistoryAdapterViewHolder holder, HLXPicBedUploadHistory hlxPicBedUploadHistory) {
        String url = hlxPicBedUploadHistory.getUploadPictureResult().getUrl();
        Glide.with(holder.binding.ivImage).load(url).error(R.drawable.ic_picture_load_fail)
                .transform(new MultiTransformation<>(new CenterCrop(), new RoundedCorners(10)))
                .into(holder.binding.ivImage);
        holder.itemView.setOnClickListener(v -> {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(v, holder.getBindingAdapterPosition(), hlxPicBedUploadHistory);
            }
        });
    }
}
