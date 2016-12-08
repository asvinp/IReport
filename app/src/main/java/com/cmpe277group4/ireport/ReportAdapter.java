package com.cmpe277group4.ireport;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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

/**
 * Created by Asvin on 12/2/2016.
 */

public class ReportAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<Report> mDataSource;

    public ReportAdapter(Context context, ArrayList<Report> items) {
        mContext = context;
        mDataSource = items;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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
        // Get view for row item
        View rowView = mInflater.inflate(R.layout.list_item_report, parent, false);

        // Get title element
        TextView titleTextView =
                (TextView) rowView.findViewById(R.id.report_list_title);

        TextView subtitleTextView =
                (TextView) rowView.findViewById(R.id.report_list_subtitle);


// Get detail element
        TextView detailTextView =
                (TextView) rowView.findViewById(R.id.report_list_detail);

// Get thumbnail element
        ImageView thumbnailImageView =
                (ImageView) rowView.findViewById(R.id.report_list_thumbnail);

        Report report = (Report) getItem(position);

        thumbnailImageView.setImageBitmap(report.imageBm);
// 2
        titleTextView.setText(report.date);
//        subtitleTextView.setText(report.time);
        detailTextView.setText(report.severity_litter);

// 3
//        Picasso.with(mContext).load(report.imageUrl).placeholder(R.mipmap.ic_launcher).into(thumbnailImageView);

        return rowView;
    }
}