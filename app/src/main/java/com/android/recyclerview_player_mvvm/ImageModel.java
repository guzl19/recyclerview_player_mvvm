package com.android.recyclerview_player_mvvm;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by sz-guzhenliang on 2018-12-10.
 */

public class ImageModel extends BaseObservable {

    private static final String TAG = "AnalysisVideo";
    private String path;
    private String name;
    private Bitmap bitmap;
    private int position;

    public ImageModel(int position, String name, String path, Bitmap bitmap){
        this.name = name;
        this.path = path;
        this.bitmap = bitmap;
        this.position = position;
    }

    //添加监听回调接口
    private OnItemClickListener mOnItemClickListener;
    public interface OnItemClickListener{
        void onClick( int position,String path);
        void onLongClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener ){
        this. mOnItemClickListener = onItemClickListener;
    }


    @Bindable
    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    @Bindable
    public Bitmap getBitmap(){
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap){
        this.bitmap = bitmap;
    }

    @BindingAdapter({"android:src"})
    public static void setImageViewResource(ImageView imageView, Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }

    public int getPosition(){
        return position;
    }

    public void setPosition(int position){
        this.position = position;
    }

    public String getPath(){
        return path;
    }

    public void setPath(String path){
        this.path = path;
    }

    public void onClick(){
        mOnItemClickListener.onClick(position, path);
    }

    public boolean onLongClick(View view){
        mOnItemClickListener.onLongClick(position);
        return true;
    }


}
