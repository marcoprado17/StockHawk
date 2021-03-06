package com.sam_chordas.android.stockhawk.rest;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.service.StockTaskService;
import com.sam_chordas.android.stockhawk.touch_helper.ItemTouchHelperAdapter;
import com.sam_chordas.android.stockhawk.touch_helper.ItemTouchHelperViewHolder;

/**
 * Created by sam_chordas on 10/6/15.
 * Credit to skyfishjy gist:
 * https://gist.github.com/skyfishjy/443b7448f59be978bc59
 * for the code structure
 */
public class QuoteCursorAdapter extends CursorRecyclerViewAdapter<QuoteCursorAdapter.ViewHolder>
        implements ItemTouchHelperAdapter {

    private static Context mContext;
    private static Typeface robotoLight;
    private boolean isPercent;
    private View mEmptyView;

    public QuoteCursorAdapter(Context context, Cursor cursor, View emptyView) {
        super(context, cursor);
        mContext = context;
        mEmptyView = emptyView;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        robotoLight = Typeface.createFromAsset(mContext.getAssets(), "fonts/Roboto-Light.ttf");
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_quote, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final Cursor cursor) {
        String symbol = cursor.getString(cursor.getColumnIndex("symbol"));
        String spacedSymbol = Utils.getSpacedSymbol(symbol);
        String symbolDescription = String.format(mContext.getString(R.string.symbol_description), spacedSymbol);
        viewHolder.symbol.setText(symbol);
        viewHolder.symbol.setContentDescription(symbolDescription);

        String bidPrice = cursor.getString(cursor.getColumnIndex("bid_price"));
        String bidPriceDescription = String.format(mContext.getString(R.string.bid_price_description), bidPrice);
        viewHolder.bidPrice.setText(bidPrice);
        viewHolder.bidPrice.setContentDescription(bidPriceDescription);

        int sdk = Build.VERSION.SDK_INT;
        if (cursor.getInt(cursor.getColumnIndex("is_up")) == 1) {
            if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
                viewHolder.change.setBackgroundDrawable(
                        mContext.getResources().getDrawable(R.drawable.percent_change_pill_green));
            } else {
                viewHolder.change.setBackground(
                        mContext.getResources().getDrawable(R.drawable.percent_change_pill_green));
            }
        } else {
            if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
                viewHolder.change.setBackgroundDrawable(
                        mContext.getResources().getDrawable(R.drawable.percent_change_pill_red));
            } else {
                viewHolder.change.setBackground(
                        mContext.getResources().getDrawable(R.drawable.percent_change_pill_red));
            }
        }
        if (Utils.showPercent) {
            String change = cursor.getString(cursor.getColumnIndex("percent_change"));
            String changeDescription = String.format(mContext.getString(R.string.change_description), change);
            viewHolder.change.setText(change);
            viewHolder.change.setContentDescription(changeDescription);
        } else {
            String change = cursor.getString(cursor.getColumnIndex("change"));
            String changeDescription = String.format(mContext.getString(R.string.change_description), change);
            viewHolder.change.setText(change);
            viewHolder.change.setContentDescription(changeDescription);
        }
    }

    @Override
    public void onItemDismiss(int position) {
        Cursor c = getCursor();
        c.moveToPosition(position);
        String symbol = c.getString(c.getColumnIndex(QuoteColumns.SYMBOL));
        mContext.getContentResolver().delete(QuoteProvider.Quotes.withSymbol(symbol), null, null);
        notifyItemRemoved(position);
        Intent dataUpdatedIntent = new Intent(StockTaskService.ACTION_DATA_UPDATED)
                .setPackage(mContext.getPackageName());
        mContext.sendBroadcast(dataUpdatedIntent);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    public Cursor swapCursor(Cursor newCursor) {
        mEmptyView.setVisibility(newCursor == null || newCursor.getCount() == 0 ? View.VISIBLE : View.GONE);
        return super.swapCursor(newCursor);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
            implements ItemTouchHelperViewHolder, View.OnClickListener {
        public final TextView symbol;
        public final TextView bidPrice;
        public final TextView change;

        public ViewHolder(View itemView) {
            super(itemView);
            symbol = (TextView) itemView.findViewById(R.id.stock_symbol);
            symbol.setTypeface(robotoLight);
            bidPrice = (TextView) itemView.findViewById(R.id.bid_price);
            change = (TextView) itemView.findViewById(R.id.change);
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
