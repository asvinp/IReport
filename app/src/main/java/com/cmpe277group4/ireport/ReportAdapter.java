package com.cmpe277group4.ireport;

import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Asvin on 12/3/2016.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Asvin on 12/2/2016.
 */

public class ReportAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<Report> mDataSource;

    private ArrayList<Report> arraylist;


    public ReportAdapter(Context context, ArrayList<Report> items) {
        mContext = context;
        mDataSource = items;

        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.arraylist = new ArrayList<Report>();
        this.arraylist.addAll(items);

    }


    //1
    @Override
    public int getCount() {

        return mDataSource.size();

    }

    //2
    @Override
    public Object getItem(int position) {
        return mDataSource.get(position);
    }

    //3
    @Override
    public long getItemId(int position) {
        return position;
    }

    //4
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;


        if (convertView == null){

            convertView = mInflater.inflate(R.layout.list_item_report, parent, false);

            holder = new ViewHolder();
            holder.thumbnailImageView = (ImageView) convertView.findViewById(R.id.report_list_thumbnail);
            holder.titleTextView = (TextView) convertView.findViewById(R.id.report_list_title);
            holder.subtitleTextView = (TextView) convertView.findViewById(R.id.report_list_subtitle);
            holder.detailTextView = (TextView) convertView.findViewById(R.id.report_list_detail);


            convertView.setTag(holder);

        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        TextView titleTextView = holder.titleTextView;
        TextView subtitleTextView = holder.subtitleTextView;
        TextView detailTextView = holder.detailTextView;
        ImageView thumbnailImageView = holder.thumbnailImageView;

        Report report = (Report) getItem(position);
        Log.d("ADAPTER_REPORT",report.date);

        //thumbnailImageView.setImageBitmap(report.imageBm);
        //new AsyncTaskLoadImage(report.image_litter, thumbnailImageView).execute();
// 2
        titleTextView.setText(report.date);
        subtitleTextView.setText(report.resident_id);
        detailTextView.setText(report.status_litter);
        new AsyncTaskLoadImage(report.image_litter, thumbnailImageView).execute();

// 3
//        Picasso.with(mContext).load(report.imageUrl).placeholder(R.mipmap.ic_launcher).into(thumbnailImageView);

        return convertView;
    }

    private static class ViewHolder {
        public TextView titleTextView;
        public TextView subtitleTextView;
        public TextView detailTextView;
        public ImageView thumbnailImageView;
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        mDataSource.clear();
        if (charText.length() == 0) {
            mDataSource.addAll(arraylist);
        }
        else
        {
            for (Report wp : arraylist)
            {
                if (wp.resident_id.toLowerCase(Locale.getDefault()).contains(charText))
                {
                    mDataSource.add(wp);
                }
                else if (wp.status_litter.toLowerCase(Locale.getDefault()).contains(charText)){
                    mDataSource.add(wp);

                }
            }
        }
        notifyDataSetChanged();
    }




}