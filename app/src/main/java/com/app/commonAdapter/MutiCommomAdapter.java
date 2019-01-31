package com.app.commonAdapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

public abstract class MutiCommomAdapter<T> extends Com_Adapter<T> {

    protected MultiItemTypeSupport<T> multiItemTypeSupport;

    public MutiCommomAdapter(Context context,  List<T> datas,MultiItemTypeSupport<T> multiItemTypeSupport) {
        super(context, -1, datas);
        this.multiItemTypeSupport = multiItemTypeSupport;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemViewType(int position) {
        return multiItemTypeSupport.getItemViewType(position,mDatas.get(position));//holder.getId（）通过这个位置设定
    }

    @Override
    public Com_ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = multiItemTypeSupport.getLayoutId(viewType);
        Com_ViewHolder holder = Com_ViewHolder.get(mContext,parent,layoutId);
        return holder;
    }
}