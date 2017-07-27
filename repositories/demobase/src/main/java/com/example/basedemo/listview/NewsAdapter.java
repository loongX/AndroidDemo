package com.example.basedemo.listview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.basedemo.R;

import java.util.List;

/**
 * Created by loongago on 2017-07-05.
 */

public class NewsAdapter extends BaseAdapter implements AbsListView.OnScrollListener {
    private List<NewsBean> mList;
    private LayoutInflater mInflater;
    private ImageLoader mImageLoader;
    private int mStart, mEnd;
    public static String[] URLS;
    private Boolean mFirstIn;

    public NewsAdapter(Context context, List<NewsBean> data, ListView listView) {
        mList = data;
        mInflater = LayoutInflater.from(context);
        mImageLoader = new ImageLoader(listView);
        URLS = new String[data.size()];
        for (int i = 0; i < data.size(); i ++){
            URLS[i] = data.get(i).newsIconUrl;
        }
        mFirstIn = true;
        //注意为ListView注册滚动监听器
        listView.setOnScrollListener(this);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_news, null);
            viewHolder.ivIcon = (ImageView) convertView.findViewById(R.id.iV_img);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tV_title);
            viewHolder.tvContent = (TextView) convertView.findViewById(R.id.tV_content);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String url = mList.get(position).newsIconUrl;
        viewHolder.ivIcon.setTag(url);
//        new ImageLoader().showImageByThread(viewHolder.ivIcon, url);
        mImageLoader.showImageByAsyncTask(viewHolder.ivIcon, url);
        viewHolder.tvTitle.setText(mList.get(position).newsTitle);
        viewHolder.tvContent.setText(mList.get(position).newsContent);
        return convertView;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE) {
            //加载可见项
            mImageLoader.loadImages(mStart, mEnd);
        } else {
            //停止任务
            mImageLoader.cancelAllTasks();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        mStart = firstVisibleItem;
        mEnd = firstVisibleItem + visibleItemCount;
        if (mFirstIn && visibleItemCount > 0){
            mImageLoader.loadImages(mStart, mEnd);
            mFirstIn = false;
        }
    }

    class ViewHolder {
        public TextView tvTitle, tvContent;
        public ImageView ivIcon;
    }
}
