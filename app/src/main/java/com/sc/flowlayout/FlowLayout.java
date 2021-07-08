package com.sc.flowlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * @author: SuperChen
 * @create-date: 2021/7/8 15:07
 * @last-modified-date: 2021/7/8 15:07
 * @description: 流式布局，适用标签Tag
 */
public class FlowLayout<T> extends FrameLayout implements FlowAdapter.Observer<T> {

    private static final String TAG = "FlowLayouts";

    private static final boolean DEBUG = true;


    private SparseIntArray maxHeightRowArray;
    //横向间距，适用每一行中的每个item之间的间距，第一列与第二列之间的间距
    private int spaceHorizontal = 10;
    //竖向间距，适用行与行之间的间距，第一行与第二行的间距
    private int spaceVertical = 10;
    //适配器
    private FlowAdapter<T> adapter;


    public FlowLayout(Context context) {
        this(context, null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        maxHeightRowArray = new SparseIntArray();
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return super.generateDefaultLayoutParams();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = getPaddingTop() + getPaddingBottom();

        int rowWidth = 0;
        //这一行的最大高度
        int thisRowMaxHeight = 0;
        //当前行
        int currentRow = 0;
        //换行的下标
        int changeRowIndex = 0;
        MarginLayoutParams parentLp = (MarginLayoutParams) getLayoutParams();
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            MarginLayoutParams childLp = (MarginLayoutParams) child.getLayoutParams();

            int childWidth = childLp.leftMargin + childLp.rightMargin + child.getMeasuredWidth();
            int childHeight = childLp.topMargin + childLp.bottomMargin + child.getMeasuredHeight();

            rowWidth += childWidth;

            //换行
            if (rowWidth + (spaceHorizontal * (i - changeRowIndex)) > width - getPaddingLeft() - getPaddingRight()) {
                child.setTag(true);
                thisRowMaxHeight = childHeight;
                rowWidth = childWidth;
                if (heightMode != MeasureSpec.EXACTLY) {
                    height += maxHeightRowArray.get(currentRow) + spaceVertical;
                }
                currentRow++;
                changeRowIndex = i;
            }
            //不换行
            else {
                thisRowMaxHeight = Math.max(thisRowMaxHeight, childHeight);
            }
            maxHeightRowArray.put(currentRow, thisRowMaxHeight);
            if (DEBUG) {
                Log.d(TAG,
                        "  changeRowIndex = " + changeRowIndex
                                + " , i = " + i
                                + " , width = " + width
                                + " , rowWidth = " + rowWidth
                                + " , maxHeightRowArray = " + maxHeightRowArray
                );
            }
        }
        if (heightMode != MeasureSpec.EXACTLY) {
            height += maxHeightRowArray.get(currentRow);
            setMeasuredDimension(width, height);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        //上一次换行的child的index
        int lastChangeRowIndex = -1;
        //当前行
        int currentRow = 0;
        //当前child的左边距
        int rowLeft = getPaddingLeft();
        //当前child的上边距
        int rowTop = getPaddingTop();
        //换行的下标
        int changeRowIndex = 0;
        //单行中所有分割线的宽度
        int allDividerWidthInOneRow = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getTag() != null) {
                changeRowIndex = i;
            }

            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

            int childLeft, childTop, childRight, childBottom;
            //换行
            if (rowLeft + child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin > getMeasuredWidth() - getPaddingRight()) {
                rowTop += maxHeightRowArray.get(currentRow) + spaceVertical;
                currentRow++;
                rowLeft = getPaddingLeft();
            }
            //不换行
            childLeft = rowLeft + lp.leftMargin;
            childRight = childLeft + child.getMeasuredWidth() + lp.rightMargin;
            childTop = rowTop + lp.topMargin;
            childBottom = childTop + child.getMeasuredHeight() + lp.bottomMargin;
            child.layout(childLeft, childTop, childRight, childBottom);
            rowLeft = childRight + spaceHorizontal;
            if (DEBUG) {
                Log.d(TAG,
                        " changeRowIndex = " + changeRowIndex
                                + " , i = " + i
                                + " , (" + childLeft + " , " + childTop + " , " + childRight + " , " + childBottom + ")"
                                + " , childWidth = " + child.getMeasuredWidth()
                                + " , rowLeft = " + rowLeft
                );
            }
        }
    }

    @Override
    public void onChanged() {
        removeAllViews();
        if (adapter == null) {
            throw new IllegalArgumentException("You must call setAdapter() first.");
        }
        for (int i = 0; i < adapter.getCount(); i++) {
            View child = adapter.onBindView(getContext(), i, adapter.getItem(i));
            if (child.getLayoutParams() == null) {
                child.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
            addView(child);
            final int finalI = i;
            child.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (adapter.getOnItemClickListener() != null) {
                        adapter.getOnItemClickListener().onItemClick(v, finalI, adapter.getItem(finalI));
                    }
                }
            });
        }
        requestLayout();
    }

    /**
     * 设置数据适配器，设置后请调用{@link FlowAdapter#notifyDataSetChanged()}通知数据修改
     * 请注意：
     * 如果需要点击事件，请在{@link FlowAdapter#notifyDataSetChanged()}之前进行调用
     * {@link FlowAdapter#setOnItemClickListener(FlowAdapter.OnFlowItemClickListener)}
     *
     * @param adapter 适配器
     */
    public void setAdapter(FlowAdapter<T> adapter) {
        this.adapter = adapter;
        if (this.adapter != null) {
            this.adapter.unRegisterObserver(this);
            this.adapter.registerObserver(this);
        }
    }

    public void setSpaceHorizontal(int spaceHorizontal) {
        this.spaceHorizontal = spaceHorizontal;
        requestLayout();
    }

    public void setSpaceVertical(int spaceVertical) {
        this.spaceVertical = spaceVertical;
        requestLayout();
    }
}
