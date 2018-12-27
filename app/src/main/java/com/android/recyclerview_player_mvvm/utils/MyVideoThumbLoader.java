package com.android.recyclerview_player_mvvm.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.util.Log;
import com.android.recyclerview_player_mvvm.ImageModel;
import com.android.recyclerview_player_mvvm.R;
import com.android.recyclerview_player_mvvm.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sz-guzhenliang on 2018-12-10.
 */

public class MyVideoThumbLoader {

    private static final String TAG = "MyVideoThumbLoader";
    private Context mContext;
    private List<LoadImageAsync> mLoadImageAsync = new ArrayList<>();
    private int mWidth;
    private int mHeight;
    private Bitmap mBitmap;

    public  MyVideoThumbLoader(Context context, int width, int height){
        this.mContext = context;
        this.mWidth = width;
        this.mHeight = height;
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_launcher);
        mBitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height);

        mLoadImageAsync.clear();
    }

    public void showThumbByAsynctack(String path, RecyclerViewAdapter.MyViewHolder holder, ImageModel imageModel){
        new LoadImageAsync(path, holder,imageModel,mWidth, mHeight).execute();
    }

    public void showThumbByAsynctack(String path, RecyclerViewAdapter.MyViewHolder holder, ImageModel imageModel,int width, int height){
        imageModel.setName(getName(path));
        imageModel.setPath(path);
        imageModel.setBitmap(mBitmap);
        holder.setContent(imageModel);
        LoadImageAsync loadImageAsync = new LoadImageAsync(path, holder, imageModel, width, height);
        mLoadImageAsync.add(loadImageAsync);
        loadImageAsync.execute();
    }

    public void stopAsynctack(){
        for(int i=0;i<mLoadImageAsync.size();i++){
        //    Log.d(TAG, "cancel i :" + i);
            mLoadImageAsync.get(i).cancel(true);
        }
    }


    class LoadImageAsync extends AsyncTask<Integer, Void, Bitmap>{
        private ImageModel mImageModel;
        private String mPath;
        private int mWidth;
        private int mHeight;

        private RecyclerViewAdapter.MyViewHolder mHolder;
        public LoadImageAsync(String path, RecyclerViewAdapter.MyViewHolder holder, ImageModel imageModel,int width, int height){
            this.mPath = path;
            this.mHolder = holder;
            this.mWidth = width;
            this.mHeight = height;
            this.mImageModel = imageModel;
        }

        @Override
        protected Bitmap doInBackground(Integer... integers) {
            Bitmap bitmap = AnalysisVideo.createVideoThumbnail(mPath, mWidth, mHeight);
            if(bitmap == null){
                //如果没有获取到缩略图则显示一个默认的图片
                bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_launcher);
                bitmap = ThumbnailUtils.extractThumbnail(bitmap, mWidth, mHeight);
            }
   //         Log.d(TAG, "bitmap width:" + bitmap.getWidth() + ", height:" + bitmap.getHeight());
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap){
            mImageModel.setBitmap(bitmap);
            mHolder.setContent(mImageModel);
        }

    }

    private String getName(String path){
        String name = "";
        int index = path.lastIndexOf("/");
        if(index > 0){
            name = path.substring(index+1,path.length());
        }else {
            name = path;
        }
        return name;
    }


}
