/**
 * Copyright (C) 2016 Marco Aurélio Prado dos Santos Vidoca
 */

package com.sam_chordas.android.stockhawk.ui;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.rest.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Shows the share value varying in time
 */
public class StockDetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String STOCK_SYMBOL_KEY = "STOCK_SYMBOL_KEY";
    private static final int CURSOR_LOADER_ID = 0;

    private String mSymbol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(STOCK_SYMBOL_KEY)) {
            mSymbol = intent.getStringExtra(STOCK_SYMBOL_KEY);
            String title = String.format(getString(R.string.stock_detail_title), mSymbol.toUpperCase());
            toolbar.setTitle(title);
        }
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayShowTitleEnabled(true);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(CURSOR_LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, QuoteProvider.Quotes.withSymbol(mSymbol),
                new String[]{QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
                        QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.ISUP, QuoteColumns.DATETIME},
                null,
                null,
                QuoteColumns.DATETIME);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        initGraph(data);
    }

    private void initGraph(Cursor data) {
        int bidPriceColumnIndex = data.getColumnIndex(QuoteColumns.BIDPRICE);
        int dateTimeColumnIndex = data.getColumnIndex(QuoteColumns.DATETIME);

        GraphView graph = (GraphView) findViewById(R.id.graph);

        List<DataPoint> allDataPoint = new ArrayList<>();

        if (data.moveToFirst()) {
            while (data.moveToNext()) {
                Double bidPrice = data.getDouble(bidPriceColumnIndex);
                String dateTime = data.getString(dateTimeColumnIndex);
                Date date = Utils.getLocalDate(dateTime);
                DataPoint dataPoint = new DataPoint(date, bidPrice);
                allDataPoint.add(dataPoint);
            }
        }

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(allDataPoint.toArray(new DataPoint[allDataPoint.size()]));

        graph.addSeries(series);

        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(graph.getContext()));
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            graph.getGridLabelRenderer().setNumHorizontalLabels(3);
        } else {
            graph.getGridLabelRenderer().setNumHorizontalLabels(5);
        }

        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(false);

        graph.getGridLabelRenderer().setHumanRounding(false);
        graph.getGridLabelRenderer().setTextSize(22f);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
