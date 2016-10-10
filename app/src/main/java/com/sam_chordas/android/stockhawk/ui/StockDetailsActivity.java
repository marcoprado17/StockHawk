/**
 * Copyright (C) 2016 Marco Aurélio Prado dos Santos Vidoca
 */

package com.sam_chordas.android.stockhawk.ui;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.ChartView;
import com.db.chart.view.LineChartView;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.rest.QuoteInTimeCursorAdapter;

/**
 * Shows the share value varying in time
 */
public class StockDetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final String STOCK_SYMBOL_KEY = "STOCK_SYMBOL_KEY";
    private static final int CURSOR_LOADER_ID = 0;

    private String mSymbol;
    private QuoteInTimeCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        Intent intent = getIntent();
        if(intent != null && intent.hasExtra(STOCK_SYMBOL_KEY)){
            mSymbol = intent.getStringExtra(STOCK_SYMBOL_KEY);
            String title = String.format(getString(R.string.stock_detail_title), mSymbol.toUpperCase());
            toolbar.setTitle(title);
        }
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if(supportActionBar != null){
            supportActionBar.setDisplayShowTitleEnabled(true);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
//        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        mCursorAdapter = new QuoteInTimeCursorAdapter(this, null);
//        recyclerView.setAdapter(mCursorAdapter);
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
                QuoteColumns.DATETIME + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        data = filter(data);
//        mCursorAdapter.swapCursor(data);

        LineChartView chart = (LineChartView) findViewById(R.id.linechart);

        chart.setStep(5);
        chart.setAxisLabelsSpacing(10f);

        LineSet lineSet = new LineSet();
        int bidPriceColumnIndex = data.getColumnIndex(QuoteColumns.BIDPRICE);
        int dateTimeColumnIndex = data.getColumnIndex(QuoteColumns.DATETIME);

        if(data.moveToFirst()){
            while (data.moveToNext()){
                Double bidPrice = data.getDouble(bidPriceColumnIndex);
                String dateTime = data.getString(dateTimeColumnIndex);
                lineSet.addPoint(dateTime, Float.parseFloat(bidPrice.toString()));
            }
        }

        chart.addData(lineSet);
        chart.show();
    }

    private Cursor filter(Cursor data) {
        // TODO: Remove rows with a datetime less than a predefined interval (3h)
        return data;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
//        mCursorAdapter.swapCursor(null);
    }
}
