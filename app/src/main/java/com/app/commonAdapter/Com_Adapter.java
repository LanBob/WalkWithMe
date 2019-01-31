package com.app.commonAdapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

public abstract class Com_Adapter<T> extends RecyclerView.Adapter<Com_ViewHolder>{
    protected Context mContext;
    protected int mLayoutId;
    protected List<T> mDatas;
    protected LayoutInflater mInflater;

    //第二个参数是Item对用的LayoutId
    public Com_Adapter(Context context,int layoutId,List<T> datas){
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mLayoutId = layoutId;
        mDatas = datas;
    }


    @Override
    public Com_ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Com_ViewHolder holder = Com_ViewHolder.get(mContext,parent,mLayoutId);
        return holder;
    }

    //每个Item都会调用这个方法，也就是每次都会设置数据
    @Override
    public void onBindViewHolder(Com_ViewHolder holder, int position) {
        convert(holder,mDatas.get(position));
    }

    public abstract void convert(Com_ViewHolder holder,T t);

    @Override
    public int getItemCount() {
        return mDatas.size();
    }
}