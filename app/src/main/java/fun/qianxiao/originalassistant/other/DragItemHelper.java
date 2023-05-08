package fun.qianxiao.originalassistant.other;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;

import fun.qianxiao.originalassistant.base.BaseAdapter;

/**
 * DragItemHelper
 *
 * @Author QianXiao
 * @Date 2023/5/9
 */
public class DragItemHelper {
    private ItemTouchHelper itemTouchHelper;
    private final BaseAdapter<?, ?> adapter;

    public DragItemHelper(BaseAdapter<?, ?> adapter) {
        this.adapter = adapter;
        this.itemTouchHelper = new ItemTouchHelper(getCallBack());
    }

    private ItemTouchHelper.Callback getCallBack() {
        return new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int dragFlags = 0;
                if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                    dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                } else if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                    dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                }
                return makeMovementFlags(dragFlags, 0);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                // 得到当拖拽的viewHolder的Position
                int fromPosition = viewHolder.getBindingAdapterPosition();
                // 拿到当前拖拽到的item的viewHolder
                int toPosition = target.getBindingAdapterPosition();
                if (fromPosition == adapter.getItemCount() - 1 || toPosition == adapter.getItemCount() - 1) {
                    return false;
                }
                if (fromPosition < toPosition) {
                    for (int i = fromPosition; i < toPosition; i++) {
                        Collections.swap(adapter.getDataList(), i, i + 1);
                    }
                } else {
                    for (int i = fromPosition; i > toPosition; i--) {
                        Collections.swap(adapter.getDataList(), i, i - 1);
                    }
                }
                adapter.notifyItemMoved(fromPosition, toPosition);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        };
    }

    public void attach(RecyclerView recyclerView) {
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }
}
