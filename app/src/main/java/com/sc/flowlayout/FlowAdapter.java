package com.sc.flowlayout;

import android.annotation.Nullable;
import android.content.Context;
import android.database.Observable;
import android.view.View;

import java.util.List;

/**
 * @author: SuperChen
 * @create-date: 2021/7/8 15:07
 * @last-modified-date: 2021/7/8 15:07
 * @description: 流式布局适配器
 */
public abstract class FlowAdapter<T> {

    private List<T> data;

    private Ob observable;

    private OnFlowItemClickListener<T> onFlowItemClickListener;

    public FlowAdapter() {
        init();
    }

    public FlowAdapter(List<T> data) {
        this.data = data;
        init();
    }

    private void init() {
        observable = new Ob();
    }

    public abstract View onBindView(Context context, int position, @Nullable T t);

    public void setData(List<T> data) {
        this.data = data;
    }

    public void notifyDataSetChanged() {
        if (observable != null) {
            observable.notifyDataSetChanged();
        }
    }

    public int getCount() {
        return data != null ? data.size() : 0;
    }

    @Nullable
    public T getItem(int position) {
        return data != null && position >= 0 && position < data.size() ? data.get(position) : null;
    }

    public void setOnItemClickListener(OnFlowItemClickListener<T> onItemClickListener) {
        this.onFlowItemClickListener = onItemClickListener;
    }

    public OnFlowItemClickListener<T> getOnItemClickListener() {
        return onFlowItemClickListener;
    }

    public void registerObserver(Observer<T> observer) {
        try {
            observable.registerObserver(observer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unRegisterObserver(Observer<T> observer) {
        try {
            observable.unregisterObserver(observer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void destroy() {
        if (observable != null) {
            observable.unregisterAll();
            observable = null;
        }
        if (data != null) {
            data.clear();
            data = null;
        }
    }


    private static class Ob extends Observable<Observer> {
        public void notifyDataSetChanged() {
            if (mObservers != null) {
                for (int i = 0; i < mObservers.size(); i++) {
                    mObservers.get(i).onChanged();
                }
            }
        }

        public void unregisterAll() {
            if (mObservers != null) {
                for (int i = 0; i < mObservers.size(); i++) {
                    unregisterObserver(mObservers.get(i));
                }
                mObservers.clear();
            }
        }
    }


    public interface Observer<T> {
        void onChanged();
    }


    public interface OnFlowItemClickListener<T> {
        void onItemClick(View view, int position, T t);
    }
}
