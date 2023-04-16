package fun.qianxiao.originalassistant.fragment.original.adapter;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import java.util.List;

import fun.qianxiao.originalassistant.R;
import fun.qianxiao.originalassistant.base.BaseAdapter;

/**
 * AppPicturesAdapter
 *
 * @Author QianXiao
 * @Date 2023/4/16
 */
public class AppPicturesAdapter extends BaseAdapter<String, AppPictureAdapterViewHolder> {
    public AppPicturesAdapter(List<String> dataList) {
        super(dataList);
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_app_picture;
    }

    @Override
    protected void onBindViewHolder(@NonNull AppPictureAdapterViewHolder holder, String s) {
        Glide.with(holder.binding.ivImage).load(s).transform(new MultiTransformation<>(new CenterCrop(), new RoundedCorners(10))).into(holder.binding.ivImage);
    }
}
