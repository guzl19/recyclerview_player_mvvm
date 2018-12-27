package com.android.recyclerview_player_mvvm;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.recyclerview_player_mvvm.utils.MyVideoThumbLoader;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sz-guzhenliang on 2018-12-10.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    private Context context;
    private LayoutInflater layoutInflater;
    private int layoutid;
    private int variableid;
    private List<String> lists = new ArrayList<>();
    private int mWidth;
    private int mHeight;
    private MyVideoThumbLoader mVideoThumbLoader;
    private ImageModel mImageModel;
    //添加监听回调接口
    private OnItemClickListener mOnItemClickListener;
    public interface OnItemClickListener{

        void onClick( int position, String path);
        void onLongClick( RecyclerView.ViewHolder vh);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener ){
        this. mOnItemClickListener = onItemClickListener;
    }

    public RecyclerViewAdapter(Context context, LayoutInflater layoutInflater,int layoutid,int variable,
                               List<String> lists, int width, int height) {
        this.context = context;
        this.layoutInflater = layoutInflater;
        this.layoutid = layoutid;
        this.variableid = variable;
        this.lists = lists;
        this.mWidth = width;
        this.mHeight = height;

        mVideoThumbLoader = new MyVideoThumbLoader(context,width,height);
    }

    public MyVideoThumbLoader getmVideoThumbLoader(){
        return mVideoThumbLoader;
    }


    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewDataBinding viewDataBinding = DataBindingUtil.inflate(layoutInflater, layoutid,parent,false);
        MyViewHolder viewHolder = new MyViewHolder(viewDataBinding.getRoot().getRootView(), variableid, viewDataBinding);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        mImageModel = new ImageModel(position,"", "",null);
        mImageModel.setOnItemClickListener(new ImageModel.OnItemClickListener() {
            @Override
            public void onClick(int position, String path) {
                mOnItemClickListener.onClick(position, path);
            }

            @Override
            public void onLongClick(int position) {
                mOnItemClickListener.onLongClick(holder);
            }
        });
        mVideoThumbLoader.showThumbByAsynctack(lists.get(position), holder, mImageModel,mWidth, mHeight);
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    public void addItem(int position, String path){
        lists.add(position, path);
        notifyItemInserted(position);
    }

    public void removeItem(int position){
        lists.remove(position);
        notifyItemRemoved(position);
    }

    public static class MyViewHolder<T> extends RecyclerView.ViewHolder {
        ViewDataBinding viewDataBinding;
        int variableid;
        public MyViewHolder(View itemView, int var, ViewDataBinding vdBinding) {
            super(itemView);
            this.variableid = var;
            this.viewDataBinding = vdBinding;
        }

        public void setContent(T image){
            viewDataBinding.setVariable(variableid, image);
            viewDataBinding.executePendingBindings();
        }
    }
}
