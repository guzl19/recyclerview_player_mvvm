package com.android.recyclerview_player_mvvm;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.recyclerview_player_mvvm.player.MyMediaPlayer;
import com.android.recyclerview_player_mvvm.utils.AnalysisVideo;
import com.android.recyclerview_player_mvvm.utils.DividerGridItemDecoration;
import com.android.recyclerview_player_mvvm.utils.MyVideoThumbLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "AnalysisVideo";
    private MyVideoThumbLoader myVideoThumbLoader;
    private RecyclerViewAdapter recyclerViewAdapter;
    private RecyclerView recyclerView;
    private ItemTouchHelper mItemTouchHelper;

    private List<String> lists = new ArrayList<>(); //视频路径列表
    //设置显示图片的大小，如果设置为0x0 则显示视频中原图大小
    private int PIC_WIDTH = 512;
    private int PIC_HEIGHT = 384;

    private int addItem;

    private String[] movies = new String[]{
            "/sdcard/Movies/4k2k_60_d9v.mp4",
            "/sdcard/Movies/babe.mp4",
            "/sdcard/Movies/s01.d9v.mp4",
            "/sdcard/Movies/test.d9v.mp4"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG,"onCreate");
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.d(TAG,"onResume");
        initView();
        initData();
    }

    public void initView(){
        recyclerView = findViewById(R.id.recyclerView);
    }

    public void initData(){
        lists = AnalysisVideo.scanUSBFiles(this,"/Autoplay--");

        recyclerViewAdapter = new RecyclerViewAdapter(this,
                getLayoutInflater(), R.layout.playeradapter, BR.imageModel,lists,PIC_WIDTH,PIC_HEIGHT);
        GridLayoutManager layoutManager = new GridLayoutManager(this,4);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerGridItemDecoration(this));
     //   LinearLayoutManager layoutManager = new LinearLayoutManager(this)
        //拖动item
        mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                if(recyclerView.getLayoutManager() instanceof GridLayoutManager){
                    final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN |
                            ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                    final int swipeFlags = 0;
                    return makeMovementFlags(dragFlags, swipeFlags);
                }else {
                    final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                    final int swipeFlags = 0;
                    return makeMovementFlags(dragFlags, swipeFlags);
                }
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                //得到当拖拽的viewHolder的Position
                int fromPosition = viewHolder.getAdapterPosition();
                //拿到当前拖拽到的item的viewHolder
                int toPosition = target.getAdapterPosition();
                if(fromPosition < toPosition){
                    for(int i=fromPosition;i<toPosition;i++){
                        Collections.swap(lists, i, i+1);
                    }
                }else {
                    for(int i=toPosition;i<fromPosition;i++){
                        Collections.swap(lists, i, i-1);
                    }
                }
                recyclerViewAdapter.notifyItemMoved(fromPosition,toPosition);
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

            }

            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                //    viewHolder.itemView.setBackgroundColor(Color.LTGRAY);
                    viewHolder.itemView.setBackgroundColor(Color.YELLOW);
                }
                super.onSelectedChanged(viewHolder, actionState);
            }

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                viewHolder.itemView.setBackgroundColor(0);
            }

        });
        mItemTouchHelper.attachToRecyclerView(recyclerView);

        myVideoThumbLoader = recyclerViewAdapter.getmVideoThumbLoader();
        recyclerViewAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position, String path) {
                Log.d(TAG,"view postion:" + position + ", video path:" + path);
                stopAsynctack(); //点击后先停掉从视频中获取图片然后进行播放。

                Intent intent = new Intent(MainActivity.this, MyMediaPlayer.class);
                intent.setType(path);
                /*
                Bundle bundle = new Bundle() ;
                Object[] arr =  lists.toArray();//��Ч�����б�
                bundle.putSerializable("playlist",Scanfile.getVideoList().toArray());
                intent.putExtras(bundle);
                */
                startActivity(intent);
            }

            @Override
            public void onLongClick(RecyclerView.ViewHolder vh) {
                Log.d(TAG, "onLongClick");
                mItemTouchHelper.startDrag(vh);
            }
        });
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    @Override
    protected void onStop(){
        super.onStop();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        stopAsynctack();
    }

    private void stopAsynctack(){
        myVideoThumbLoader.stopAsynctack();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private int index = 0;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.start:
                addItem = recyclerViewAdapter.getItemCount()/2;
                if(index >= movies.length){
                    index = 0;
                }
                recyclerViewAdapter.addItem(addItem, movies[index]);
                index += 1;
                break;
            case R.id.over:
                recyclerViewAdapter.removeItem(addItem);
                break;
        }
        return true;
    }


}
