package com.example.imageloader;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private NetWorkStateReceiver mNetWorkStateReceiver;
    private ImageLoader mImageLoader;
    private GridView mGridView;
    private ImageAdapter mImageAdapter;
    private boolean mIsGridViewIdle = true;
    private boolean mCanGetBitmapFromNetwork = true;
    private int mImageWidth;

    private ArrayList<String> mUrlList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_list);
        prepare();
        mNetWorkStateReceiver = new NetWorkStateReceiver();
        mImageLoader = ImageLoader.build(this);
        mGridView = (GridView) findViewById(R.id.gridView);
        mImageAdapter = new ImageAdapter(this);
        mGridView.setAdapter(mImageAdapter);
        mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    mIsGridViewIdle = true;
                    mImageAdapter.notifyDataSetChanged();
                } else {
                    mIsGridViewIdle = false;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        mImageWidth = 100;
        //showTip();
    }

    private void prepare() {
        mUrlList.add("https://pic.ntimg.cn/file/20210923/32296849_110611043104_2.jpg");
        mUrlList.add("https://pic.ntimg.cn/file/20210911/27131774_093632285104_2.jpg");
        mUrlList.add("https://pic.ntimg.cn/file/20210919/27935677_090312449124_2.jpg");
        mUrlList.add("https://pic.ntimg.cn/file/20210926/24220596_102558543123_2.jpg");
        mUrlList.add("https://pic.ntimg.cn/file/20210926/24220596_102154526120_2.jpg");
        mUrlList.add("https://pic162.huitu.com/res/20210519/845077_20210519120155813060_1.jpg");
        mUrlList.add("https://pic.ntimg.cn/file/20180705/391634_154004130818_2.jpg");
        mUrlList.add("https://pic.ntimg.cn/file/20210918/31982077_084641964105_2.jpg");
        mUrlList.add("https://pic.ntimg.cn/file/20210919/22636788_123106548120_2.jpg");

    }

    private void showTip() {
        if (Utils.isWifi(this)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("注意");
            builder.setMessage("初次使用会从网络下载大概5MB的图片，确认要下载吗？");
            builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mCanGetBitmapFromNetwork = true;
                    mImageAdapter.notifyDataSetChanged();
                }
            });
            builder.setNegativeButton("否", null);
            builder.show();
        }
    }

    @Override
    protected void onResume() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetWorkStateReceiver, filter);
        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(mNetWorkStateReceiver);
        super.onPause();
    }

    private class ImageAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private Drawable mDefaultDrawable;

        public ImageAdapter(Context context) {
            mInflater = LayoutInflater.from(getApplicationContext());
            mDefaultDrawable = context.getResources().getDrawable(R.drawable.abc_vector_test);
        }

        @Override
        public int getCount() {
            return mUrlList.size();
        }

        @Override
        public String getItem(int position) {
            return mUrlList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.image_list_item, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.image);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            ImageView imageView = viewHolder.imageView;
            final String tag = (String) imageView.getTag();
            final String url = getItem(position);
            if (!url.equals(tag)) {
                imageView.setImageDrawable(mDefaultDrawable);
            }
            if (mIsGridViewIdle && mCanGetBitmapFromNetwork) {
                imageView.setTag(url);
                mImageLoader.bindBitmap(url, imageView);
            }
            return convertView;
        }

        private class ViewHolder{
            public ImageView imageView;
        }

    }
}