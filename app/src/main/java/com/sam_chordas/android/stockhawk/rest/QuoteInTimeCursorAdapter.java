/**
 * Copyright (C) 2016 Marco Aurélio Prado dos Santos Vidoca
 */
package com.sam_chordas.android.stockhawk.rest;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.touch_helper.ItemTouchHelperViewHolder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Bind cursor data to widget list item layout
 */
public class QuoteInTimeCursorAdapter extends CursorRecyclerViewAdapter<QuoteInTimeCursorAdapter.ViewHolder> {

    private static Context mContext;
    private static Typeface robotoLight;

    public QuoteInTimeCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        robotoLight = Typeface.createFromAsset(mContext.getAssets(), "fonts/Roboto-Light.ttf");
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_quote_in_time, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final Cursor cursor) {
        String dateTime = cursor.getString(cursor.getColumnIndex(QuoteColumns.DATETIME));
        dateTime = dateTime.substring(0,10)+' '+dateTime.substring(11, 19);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        TimeZone utcZone = TimeZone.getTimeZone("UTC");
        simpleDateFormat.setTimeZone(utcZone);
        Date myDate = null;
        try {
            myDate = simpleDateFormat.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        String formattedDate = simpleDateFormat.format(myDate);


        viewHolder.date.setText(Utils.getDate(formattedDate));
        String time = String.format(mContext.getString(R.string.time_format), Utils.getTime(formattedDate));
        viewHolder.time.setText(time);

        String bidPrice = cursor.getString(cursor.getColumnIndex("bid_price"));
        String bidPriceDescription = String.format(mContext.getString(R.string.bid_price_description), bidPrice);
        viewHolder.bidPrice.setText(bidPrice);
        viewHolder.bidPrice.setContentDescription(bidPriceDescription);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
            implements ItemTouchHelperViewHolder, View.OnClickListener {
        public final TextView date;
        public final TextView time;
        public final TextView bidPrice;

        public ViewHolder(View itemView) {
            super(itemView);
            date = (TextView) itemView.findViewById(R.id.date);
            date.setTypeface(robotoLight);
            time = (TextView) itemView.findViewById(R.id.time);
            time.setTypeface(robotoLight);
            bidPrice = (TextView) itemView.findViewById(R.id.bid_price);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
