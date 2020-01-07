package com.ware.widget.recycler

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

/**
 * mark:
 * 1. onBindViewHolder payloads
 * 2. registerAdapterDataObserver
 * 3. notifyDataSetChanged() / notifyItemRangeChanged(positionStart, itemCount) 造成的　onCreateViewHolder区别
 */
class MoreRecyclerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : RecyclerView(context, attrs, defStyle) {
    private var mWrapAdapter: WrapAdapter? = null
    private var mLoadListener: LoadListener? = null
    private var mEnableMore = true
    private val mDataObserver: AdapterDataObserver = DataObserver()
    private var mStatus = Status.MORE
    override fun setAdapter(adapter: Adapter<in ViewHolder>?) {
        adapter?.let {
            mWrapAdapter = WrapAdapter(it)
            super.setAdapter(mWrapAdapter)

            it.registerAdapterDataObserver(mDataObserver)
            mDataObserver.onChanged()
        }

    }

    override fun getAdapter(): Adapter<*>? {
        return mWrapAdapter?.oriAdapter
    }

    fun setEnableMore(enable: Boolean) {
        mEnableMore = enable
    }

    override fun setLayoutManager(layout: LayoutManager?) {
        super.setLayoutManager(layout)
        if (mWrapAdapter != null) {
            if (layout is GridLayoutManager) {
                mWrapAdapter?.resetGridSpanSize(layout)
            }
        }
    }

    override fun onScrollStateChanged(state: Int) {
        if (state != SCROLL_STATE_IDLE && !hasMore(mStatus)) return
        val layoutManager = layoutManager ?: return
        var lastVisibleItemPosition = NO_POSITION
        val itemCount = layoutManager.itemCount
        when (layoutManager) {
            is GridLayoutManager -> {
                lastVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition()
            }
            is LinearLayoutManager -> {
                lastVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition()
            }
            is StaggeredGridLayoutManager -> {
                val into = IntArray(layoutManager.spanCount)
                layoutManager.findLastCompletelyVisibleItemPositions(into)
                lastVisibleItemPosition = findLast(into)
            }
        }
        if (lastVisibleItemPosition == itemCount) {
            updateStatus(Status.LOADING)
            mLoadListener!!.loadMore()
        }
    }

    private fun findLast(lastPositions: IntArray): Int {
        var max = lastPositions[0]
        for (value in lastPositions) {
            if (value > max) {
                max = value
            }
        }
        return max
    }

    fun updateStatus(status: Status) {
        mStatus = status
    }

    private inner class WrapAdapter internal constructor(private val adapter: Adapter<ViewHolder>) : Adapter<ViewHolder>() {
        val oriAdapter: Adapter<ViewHolder>
            get() = adapter

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return if (viewType == TYPE_FOOTER) {
                FooterHolder(FooterView(parent.context))
            } else adapter.onCreateViewHolder(parent, viewType)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            Log.d(TAG, "onBindViewHolder: ")
            if (holder is FooterHolder) {
                holder.bindView(mStatus)
            } else {
                val itemCount = adapter.itemCount
                if (position < itemCount) {
                    adapter.onBindViewHolder(holder, position)
                }
            }
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: List<Any>) {
            Log.d(TAG, "onBindViewHolder: 222222222222222222222222222")
            val footer = isFooter(position)
            if (footer) {
                (holder as FooterHolder).bindView(mStatus)
            } else {
                val itemCount = adapter.itemCount
                if (position < itemCount) {
                    if (payloads.isEmpty()) {
                        adapter.onBindViewHolder(holder, position)
                    } else {
                        adapter.onBindViewHolder(holder, position, payloads)
                    }
                }
            }
        }

        internal inner class FooterHolder(itemView: FooterView) : ViewHolder(itemView) {
            fun bindView(status: Status?) {
                (itemView as FooterView).updateState(status!!)
            }

            init {
                itemView.layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            }
        }

        override fun getItemViewType(position: Int): Int {
            if (isFooter(position)) {
                return TYPE_FOOTER
            }
            if (position < adapter.itemCount) {
                val type = adapter.getItemViewType(position)
                check(!viewTypeConflict(type)) { "itemViewType in adapter conflict with MoreRecyclerView's footer type" }
                return type
            }
            return super.getItemViewType(position)
        }

        override fun getItemCount(): Int {
            val addSize = if (mEnableMore) 1 else 0
            return adapter.itemCount + addSize
        }

        override fun getItemId(position: Int): Long {
            if (position < adapter.itemCount) {
                if (position < adapter.itemCount) {
                    return adapter.getItemId(position)
                }
            }
            return super.getItemId(position)
        }

        fun isFooter(position: Int): Boolean {
            return mEnableMore && position == itemCount - 1
        }

        private fun viewTypeConflict(viewType: Int): Boolean {
            return viewType == TYPE_FOOTER
        }

        fun resetGridSpanSize(manager: GridLayoutManager) {
            val oriLookup = manager.spanSizeLookup
            val newLookup: SpanSizeLookup = object : SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (isFooter(position)) manager.spanCount else oriLookup.getSpanSize(position)
                }
            }
            manager.spanSizeLookup = newLookup
        }

        override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
            super.onAttachedToRecyclerView(recyclerView)
            val manager = recyclerView.layoutManager
            if (manager is GridLayoutManager) {
                resetGridSpanSize(manager)
            }
            adapter.onAttachedToRecyclerView(recyclerView)
        }

        override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
            adapter.onDetachedFromRecyclerView(recyclerView)
        }

        override fun onViewAttachedToWindow(holder: ViewHolder) {
            super.onViewAttachedToWindow(holder)
            val lp = holder.itemView.layoutParams
            if (lp is StaggeredGridLayoutManager.LayoutParams && isFooter(holder.layoutPosition)) {
                lp.isFullSpan = true
            }
            adapter.onViewAttachedToWindow(holder)
        }

        override fun onViewDetachedFromWindow(holder: ViewHolder) {
            adapter.onViewDetachedFromWindow(holder)
        }

        override fun onViewRecycled(holder: ViewHolder) {
            adapter.onViewRecycled(holder)
        }

        override fun onFailedToRecycleView(holder: ViewHolder): Boolean {
            return adapter.onFailedToRecycleView(holder)
        }

        override fun unregisterAdapterDataObserver(observer: AdapterDataObserver) {
            adapter.unregisterAdapterDataObserver(observer)
        }

        override fun registerAdapterDataObserver(observer: AdapterDataObserver) {
            adapter.registerAdapterDataObserver(observer)
        }

    }

    private inner class DataObserver : AdapterDataObserver() {
        override fun onChanged() {
            mWrapAdapter?.notifyDataSetChanged()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            mWrapAdapter?.notifyItemRangeInserted(positionStart, itemCount)
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            mWrapAdapter?.notifyItemRangeChanged(positionStart, itemCount)
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            mWrapAdapter?.notifyItemRangeChanged(positionStart, itemCount, payload)
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            mWrapAdapter?.notifyItemRangeRemoved(positionStart, itemCount)
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            mWrapAdapter?.notifyItemMoved(fromPosition, toPosition)
        }
    }

    fun setLoadListener(listener: LoadListener) {
        mLoadListener = listener
    }

    interface LoadListener {
        fun loadMore()
    }

    companion object {
        private const val TYPE_FOOTER = 122321
        private const val TAG = "MoreRecyclerView"
    }
}