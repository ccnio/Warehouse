package com.ware.widget.recycler;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.ware.widget.recycler.FooterViewKt.hasMore;

public class MoreRecyclerView extends RecyclerView {
    private static final int TYPE_FOOTER = 122321;
    private static final String TAG = "MoreRecyclerView";
    private WrapAdapter mWrapAdapter;
    private LoadListener mLoadListener;
    private boolean mEnableMore = true;

    private final RecyclerView.AdapterDataObserver mDataObserver = new DataObserver();
    private Status mStatus = Status.MORE;

    public MoreRecyclerView(Context context) {
        this(context, null, 0);
    }

    public MoreRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MoreRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setAdapter(Adapter adapter) {
        mWrapAdapter = new WrapAdapter(adapter);
        super.setAdapter(mWrapAdapter);
        adapter.registerAdapterDataObserver(mDataObserver);
        mDataObserver.onChanged();
    }

    @Override
    public Adapter getAdapter() {
        if (mWrapAdapter != null) {
            return mWrapAdapter.getOriAdapter();
        }

        return null;
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        super.setLayoutManager(layout);
        if (mWrapAdapter != null) {
            if (layout instanceof GridLayoutManager) {
                mWrapAdapter.resetGridSpanSize(((GridLayoutManager) layout));
            }
        }
    }

    @Override
    public void onScrollStateChanged(int state) {
        if (state != SCROLL_STATE_IDLE && !hasMore(mStatus)) return;

        LayoutManager layoutManager = getLayoutManager();
        if (layoutManager == null) return;

        if (layoutManager instanceof GridLayoutManager) {
            int mLastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastCompletelyVisibleItemPosition();
            int itemCount = layoutManager.getItemCount();
            if (mLastVisibleItemPosition == itemCount) {
                updateStatus(Status.LOADING);
                mLoadListener.loadMore();
            }
        }
    }

    public void updateStatus(Status status) {
        mStatus = status;
    }

    private class WrapAdapter extends RecyclerView.Adapter<ViewHolder> {
        private RecyclerView.Adapter<ViewHolder> adapter;

        WrapAdapter(RecyclerView.Adapter<ViewHolder> adapter) {
            this.adapter = adapter;
        }

        RecyclerView.Adapter getOriAdapter() {
            return this.adapter;
        }

        @NotNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
            if (viewType == TYPE_FOOTER) {
                return new FooterHolder(new FooterView(parent.getContext()));
            }
            return adapter.onCreateViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(@NotNull RecyclerView.ViewHolder holder, int position) {
            boolean footer = isFooter(position);
            if (footer) {
                ((FooterHolder) holder).bindView(mStatus);
            } else if (adapter != null) {
                int itemCount = adapter.getItemCount();
                if (position < itemCount) {
                    adapter.onBindViewHolder(holder, position);
                }
            }
        }

        @Override
        public void onBindViewHolder(@NotNull RecyclerView.ViewHolder holder, int position, @NotNull List<Object> payloads) {
            boolean footer = isFooter(position);
            if (footer) {
                ((FooterHolder) holder).bindView(mStatus);
            } else if (adapter != null) {
                int itemCount = adapter.getItemCount();
                if (position < itemCount) {
                    if (payloads.isEmpty()) {
                        adapter.onBindViewHolder(holder, position);
                    } else {
                        adapter.onBindViewHolder(holder, position, payloads);
                    }
                }
            }
        }

        class FooterHolder extends RecyclerView.ViewHolder {
            public FooterHolder(@NonNull FooterView itemView) {
                super(itemView);
            }

            public void bindView(Status status) {
                ((FooterView) itemView).updateState(status);
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (isFooter(position)) {
                return TYPE_FOOTER;
            }
            if (adapter != null) {
                if (position < adapter.getItemCount()) {
                    int type = adapter.getItemViewType(position);
                    if (viewTypeConflict(type)) {
                        throw new IllegalStateException("XRecyclerView require itemViewType in adapter should be less than 10000 ");
                    }
                    return type;
                }
            }
            return super.getItemViewType(position);
        }

        @Override
        public int getItemCount() {
            int addSize = (mEnableMore ? 1 : 0);
            if (adapter != null) {
                return adapter.getItemCount() + addSize;
            }
            return addSize;
        }

        @Override
        public long getItemId(int position) {
            if (adapter != null && position < adapter.getItemCount()) {
                if (position < adapter.getItemCount()) {
                    return adapter.getItemId(position);
                }
            }
            return super.getItemId(position);
        }

        boolean isFooter(int position) {
            return (mEnableMore && position == getItemCount() - 1);
        }

        private boolean viewTypeConflict(int viewType) {
            return viewType == TYPE_FOOTER;
        }

        private void resetGridSpanSize(GridLayoutManager manager) {
            final GridLayoutManager gridManager = manager;
            GridLayoutManager.SpanSizeLookup oriLookup = gridManager.getSpanSizeLookup();
            GridLayoutManager.SpanSizeLookup newLookup = new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return (isFooter(position)) ? gridManager.getSpanCount() : oriLookup.getSpanSize(position);
                }
            };
            gridManager.setSpanSizeLookup(newLookup);
        }

        @Override
        public void onAttachedToRecyclerView(@NotNull RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
            RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
            if (manager instanceof GridLayoutManager) {
                resetGridSpanSize((GridLayoutManager) manager);
            }
            adapter.onAttachedToRecyclerView(recyclerView);
        }

        @Override
        public void onDetachedFromRecyclerView(@NotNull RecyclerView recyclerView) {
            adapter.onDetachedFromRecyclerView(recyclerView);
        }

        @Override
        public void onViewAttachedToWindow(@NotNull RecyclerView.ViewHolder holder) {
            super.onViewAttachedToWindow(holder);
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp instanceof StaggeredGridLayoutManager.LayoutParams && (isFooter(holder.getLayoutPosition()))) {
                StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                p.setFullSpan(true);
            }
            adapter.onViewAttachedToWindow(holder);
        }

        @Override
        public void onViewDetachedFromWindow(@NotNull RecyclerView.ViewHolder holder) {
            adapter.onViewDetachedFromWindow(holder);
        }

        @Override
        public void onViewRecycled(@NotNull RecyclerView.ViewHolder holder) {
            adapter.onViewRecycled(holder);
        }

        @Override
        public boolean onFailedToRecycleView(@NotNull RecyclerView.ViewHolder holder) {
            return adapter.onFailedToRecycleView(holder);
        }

        @Override
        public void unregisterAdapterDataObserver(@NotNull AdapterDataObserver observer) {
            adapter.unregisterAdapterDataObserver(observer);
        }

        @Override
        public void registerAdapterDataObserver(@NotNull AdapterDataObserver observer) {
            adapter.registerAdapterDataObserver(observer);
        }
    }

    private class DataObserver extends RecyclerView.AdapterDataObserver {
        @Override
        public void onChanged() {
            if (mWrapAdapter != null) {
                mWrapAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            mWrapAdapter.notifyItemRangeChanged(positionStart, itemCount, payload);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeRemoved(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            mWrapAdapter.notifyItemMoved(fromPosition, toPosition);
        }
    }

    public void setLoadListener(LoadListener listener) {
        mLoadListener = listener;
    }

    public interface LoadListener {
        void loadMore();
    }
}