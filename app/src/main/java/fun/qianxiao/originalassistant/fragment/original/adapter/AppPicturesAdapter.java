package fun.qianxiao.originalassistant.fragment.original.adapter;

import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cc.shinichi.library.ImagePreview;
import fun.qianxiao.originalassistant.R;
import fun.qianxiao.originalassistant.base.BaseAdapter;

/**
 * AppPicturesAdapter
 *
 * @Author QianXiao
 * @Date 2023/4/16
 */
public class AppPicturesAdapter extends BaseAdapter<String, AppPictureAdapterViewHolder> {
    public static final String PLACEHOLDER_ADD = "add";
    private boolean showDelete;
    private OnAppPicturesAdapterListener onAppPicturesAdapterListener;

    public AppPicturesAdapter(OnAppPicturesAdapterListener onAppPicturesAdapterListener, List<String> dataList) {
        super(dataList);
        this.onAppPicturesAdapterListener = onAppPicturesAdapterListener;
        registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                if (onAppPicturesAdapterListener != null) {
                    if (getItemCount() > 1) {
                        onAppPicturesAdapterListener.onDataChange(false);
                    } else if (getItemCount() == 1) {
                        onAppPicturesAdapterListener.onDataChange(true);
                    }
                }
            }
        });
        setItemClickListener(new ItemClickListener<String>() {
            @Override
            public void onItemClick(View v, int pos, String bean) {
                if (!bean.equals(PLACEHOLDER_ADD)) {
                    List<String> listCopy = new ArrayList<>(getDataList());
                    listCopy.remove(listCopy.size() - 1);
                    ImagePreview.getInstance().setContext(v.getContext()).setIndex(pos).setImageList(listCopy).start();
                } else {
                    if (onAppPicturesAdapterListener != null) {
                        onAppPicturesAdapterListener.onAddClick(v);
                    }
                }
            }
        });
    }

    public interface OnAppPicturesAdapterListener {
        /**
         * on 'Add' item click
         *
         * @param view view
         */
        void onAddClick(View view);

        /**
         * on data change by DataObserver
         *
         * @param empty is empty
         */
        void onDataChange(boolean empty);
    }

    public void setOnAddClickListener(OnAppPicturesAdapterListener onAppPicturesAdapterListener) {
        this.onAppPicturesAdapterListener = onAppPicturesAdapterListener;
    }

    public void setShowDelete(boolean showDelete) {
        this.showDelete = showDelete;
    }

    public boolean isShowDelete() {
        return showDelete;
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_app_picture;
    }

    @Override
    protected void onBindViewHolder(@NonNull AppPictureAdapterViewHolder holder, String s) {
        holder.binding.ivDelete.setVisibility(View.GONE);
        if (s.equals(PLACEHOLDER_ADD)) {
            Drawable drawable = AppCompatResources.getDrawable(holder.itemView.getContext(), R.drawable.add);
            Glide.with(holder.binding.ivImage).load(drawable)
                    .error(R.drawable.ic_picture_load_fail)
                    .transform(new MultiTransformation<>(new CenterCrop(), new RoundedCorners(10)))
                    .into(holder.binding.ivImage);
        } else {
            if (s.startsWith("http")) {
                Glide.with(holder.binding.ivImage).load(s).error(R.drawable.ic_picture_load_fail)
                        .transform(new MultiTransformation<>(new CenterCrop(), new RoundedCorners(10)))
                        .into(holder.binding.ivImage);
            } else {
                Glide.with(holder.binding.ivImage).load(new File(s)).error(R.drawable.ic_picture_load_fail)
                        .transform(new MultiTransformation<>(new CenterCrop(), new RoundedCorners(10)))
                        .into(holder.binding.ivImage);
            }
            holder.binding.ivImage.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (itemLongClickListener != null) {
                        return itemLongClickListener.onItemLongClick(v, holder.getBindingAdapterPosition(), s);
                    }
                    return false;
                }
            });
            if (showDelete) {
                holder.binding.ivDelete.setVisibility(View.VISIBLE);
                holder.binding.ivDelete.setOnClickListener(v -> {
                    getDataList().remove(holder.getBindingAdapterPosition());
                    notifyItemRemoved(holder.getBindingAdapterPosition());
                    notifyDataSetChanged();
                });
            }
        }
        holder.binding.ivImage.setOnClickListener(v -> {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(holder.binding.ivImage, holder.getBindingAdapterPosition(), s);
            }
        });
    }
}
