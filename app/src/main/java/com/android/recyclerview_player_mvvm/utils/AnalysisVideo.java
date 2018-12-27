package com.android.recyclerview_player_mvvm.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.storage.StorageManager;
import android.util.Log;

import com.android.recyclerview_player_mvvm.R;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by sz-guzhenliang on 2018-12-11.
 */

public class AnalysisVideo {

    private static final String TAG = "AnalysisVideo";
    private static MediaMetadataRetriever media = null;
    public static List<String> scanUSBFiles(Context context, String dir){
        List<String> lists = new ArrayList<>();
        List<String> usbLists = listAllStorage(context);
        if(usbLists.size() <= 0) return lists;
        Log.d(TAG, "-----------------");
        for(int i=0;i<usbLists.size();i++){
            String tmp_path = usbLists.get(i).toString() + dir;
            List<String> tmplist = scan(context, tmp_path);
            for(int n=0;n<tmplist.size();n++){
                lists.add(tmplist.get(n));
            }
        }
        return lists;
    }

    public static Bitmap createVideoThumbnail(String path, int width, int height){
        Date curDate = new Date(System.currentTimeMillis());

        media = new MediaMetadataRetriever();
        media.setDataSource(path);
        Bitmap bitmap = media.getFrameAtTime(0, MediaMetadataRetriever.OPTION_NEXT_SYNC);
        /*
        String title = media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        String album = media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        String mime = media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
        String artist = media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        String duration = media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);// 播放时长单位为毫秒
        String bitrate = media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE); // 从api level 14才有，即从ICS4.0才有此功能
        String date = media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE);
        Log.d(TAG, "path" + path + ", album:" + album + ", mime:" + mime + ", artist:" + artist + ", duration:" + duration + ", bitrate:" + bitrate);
        */
        media.release();
        media = null;

        if(width > 0  && height > 0){
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height);
        }

        Date endDate = new Date(System.currentTimeMillis());
        long diff = endDate.getTime() - curDate.getTime();

        Log.d(TAG, "get picture time:" + diff);
        return bitmap;
    }


    /*//////////////////////////////////////////////////////////////////////////////////////////////////
                            本地函数
    //////////////////////////////////////////////////////////////////////////////////////////////////*/

    /**
     * 获取挂在所有U盘的路劲
     * @param context
     * @return
     */
    private static List<String> listAllStorage(Context context) {
        ArrayList<String> storages = new ArrayList<String>();
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        try {
            Class<?>[] paramClasses = {};
            Method getVolumeList = StorageManager.class.getMethod("getVolumeList", paramClasses);
            Object[] params = {};
            Object[] invokes = (Object[]) getVolumeList.invoke(storageManager, params);

            if (invokes != null) {
                for (int i = 0; i < invokes.length; i++) {
                    Object obj = invokes[i];
                    Method getPath = obj.getClass().getMethod("getPath", new Class[0]);
                    String path = (String) getPath.invoke(obj, new Object[0]);
                    storages.add(path);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        storages.trimToSize();
        return storages;
    }

    /**
     * 扫描单个目录下的文件
     * @param path
     */
    private static List<String> scan(Context context, String path){
        List<String> listFiles = new ArrayList<>();
        String name = "";
        File usbfiles = new File(path);
        if(!usbfiles.exists()){
            Log.d(TAG, "dir not exists");
            return listFiles;
        }
        File[] files = usbfiles.listFiles();
        for(File f : files){
            if(!f.isDirectory()){
                name = f.getAbsolutePath();
                if(IsVideo(context, name)){
                    listFiles.add(name);
                }

            }
        }
        return listFiles;
    }

    private static boolean IsVideo(Context context, String url){
        if(url==null||url.equalsIgnoreCase("")){
            return false;
        }
        int index_last = url.lastIndexOf(".");
        String last_name = url.substring(index_last).toLowerCase(new Locale("en"));
        if(check(last_name,context.getApplicationContext().getResources().getStringArray(R.array.video_filter))){
            return true ;
        }else {
            return  false;
        }
    }

    private static boolean check(final String name, final String[] extensions) {
        for (String end : extensions) {
            // Name never to null, without exception handling
            if (name.equals(end)) {
                return true;
            }
        }
        return false;
    }

}
