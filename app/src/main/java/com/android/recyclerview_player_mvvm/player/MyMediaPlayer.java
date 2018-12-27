package com.android.recyclerview_player_mvvm.player;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.android.recyclerview_player_mvvm.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sz-guzhenliang on 2018-12-13.
 */

public class MyMediaPlayer extends Activity{

    private static final String TAG = "MyMediaPlayer";
    private Context mContext;
    private MediaPlayer mMediaPlayer = null;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;

    private String mPath;
    private List<String> m_list = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mediaplayer_activity);
        mPath = getIntent().getType();
        m_list = (List<String>) getIntent().getSerializableExtra("playlist");
        initView();
    }

    public void initView(){
        mSurfaceView = findViewById(R.id.surfaceview);
        mSurfaceHolder = mSurfaceView.getHolder();
        mMediaPlayer = new MediaPlayer();
        mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if(mMediaPlayer == null){
                        mMediaPlayer = new MediaPlayer();
                    }
                    mMediaPlayer.reset();
                    mMediaPlayer.setDisplay(mSurfaceHolder);
                    mMediaPlayer.setDataSource(mPath);
                    mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mMediaPlayer.start();
                        }
                    });
                    mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            Log.d(TAG, "" + mPath + " pleyer Completion");
                            finish();
                        }
                    });
                    mMediaPlayer.prepareAsync();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(mMediaPlayer != null){
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

}
