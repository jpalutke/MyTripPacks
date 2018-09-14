package com.crystaltowerdesigns.mytrippacks;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


import com.crystaltowerdesigns.mytrippacks.data.TripsContract.StopEntry;
import com.crystaltowerdesigns.mytrippacks.data.TripsContract.TripEntry;
import com.crystaltowerdesigns.mytrippacks.data.TripsProvider;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {

    // LOADER ID's
    private static final int TRIP_LIST_LOADER = 0;
    private static final int STOPS_LOADER = 1;

    private final Random randomNumberClass = new Random(); // Initialize the randomNumberClass
    private TripsCursorAdapter tripsCursorAdapter;

    private int getRandom(int upperBound, boolean zeroBased) {
        if (zeroBased)
            return randomNumberClass.nextInt(upperBound + 1);
        else
            return randomNumberClass.nextInt(upperBound) + 1;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView tripPackListView = findViewById(R.id.database_list_view);

        View emptyListView = findViewById(R.id.empty_inventory_view);
        tripPackListView.setEmptyView(emptyListView);

        tripsCursorAdapter = new TripsCursorAdapter(this, null);
        tripPackListView.setAdapter(tripsCursorAdapter);

        // item click listener
        tripPackListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Intent to View/Edit item
                Intent intent = new Intent(MainActivity.this, TripEditorActivity.class);

                // Append the "id" on to the {@link TripEntry#CONTENT_URI}.
                Uri currentTripUri = ContentUris.withAppendedId(TripEntry.CONTENT_URI, id);
                intent.setData(currentTripUri);

                // Launch the {@link TripEditorActivity} to display the data for the current item.
                startActivity(intent);
            }
        });

        // Launch the loader
        LoaderManager.getInstance(this).initLoader(TRIP_LIST_LOADER, null, this).forceLoad();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu to app bar.
        getMenuInflater().inflate(R.menu.app_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Menu option item was clicked in the app bar
        // Menu option item ID's are self explanatory
        // Take appropriate action(s)
        switch (item.getItemId()) {
            case R.id.action_server_sync:
                // dummy sync (add a new trip)
                insertTripItem();
                return true;
            case R.id.action_delete_all_trips:
                deleteAllTrips();
                return true;
            default: {
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("DefaultLocale")
    private void deleteAllTrips() {
        int tripsResult = getContentResolver().delete(TripEntry.CONTENT_URI, null, null);
        int stopsResult = getContentResolver().delete(StopEntry.CONTENT_URI, null, null);
        Toast.makeText(this, String.format("%d %s %d %s", tripsResult, "trips deleted.\n", stopsResult, "stops deleted."), Toast.LENGTH_LONG).show();
    }

    private void insertTripItem() {
        // Create a ContentValues object where column names are the keys.
        ContentValues TripValues = new ContentValues();

        String maxString = TripsProvider.getMaximum(this.getBaseContext(), TripEntry.TABLE_NAME, TripEntry.COLUMN_TRIP_NUMBER);
        if (maxString == null)
            maxString = "0";
        int nextTripNumber = Integer.parseInt(maxString) + 1;
        TripValues.put(TripEntry.COLUMN_STATE, TripEntry.STATE_ASSIGNED);
        TripValues.put(TripEntry.COLUMN_TRIP_NUMBER, nextTripNumber);
        TripValues.put(TripEntry.COLUMN_HUB_INITIAL, 0);
        TripValues.put(TripEntry.COLUMN_HUB_END, 0);
        TripValues.put(TripEntry.COLUMN_RECEIVED_DATE, "2018-01-01");
        TripValues.put(TripEntry.COLUMN_SUBMITTED_DATE, "2018-01-01");

        // add a random number of stops
        int howManyToAdd = getRandom(4, false) + 1;
        String fromTo = "";
        for (int count = 1; count <= howManyToAdd; count++) {
            // Create a ContentValues object where column names are the keys.
            ContentValues stop_values = new ContentValues();
            stop_values.put(StopEntry.COLUMN_TRIP_NUMBER, nextTripNumber);
            stop_values.put(StopEntry.COLUMN_LOCATION, String.format("'location' %d", count));
            stop_values.put(StopEntry.COLUMN_DATE_COMPLETED, "2018-01-01");
            stop_values.put(StopEntry.COLUMN_HUB, 0);
            stop_values.put(StopEntry.COLUMN_SORT_INDEX, count);
            if (count == 1)
                fromTo = String.format("'location' %d", count);
            else if (count == howManyToAdd)
                fromTo = fromTo + " to " + String.format("'location' %d", count);

            // Insert the stop record into the stop table
            @SuppressWarnings("unused") Uri newStopUri = getContentResolver().insert(StopEntry.CONTENT_URI, stop_values);
        }
        fromTo = fromTo + " (" + howManyToAdd + " stops)";
        TripValues.put(TripEntry.COLUMN_FROM_TO, fromTo);

        // Insert the Trip record into the trip table
        @SuppressWarnings("unused") Uri newTripUri = getContentResolver().insert(TripEntry.CONTENT_URI, TripValues);
        Toast.makeText(this, String.format(getString(R.string.trip_added_fmt), nextTripNumber), Toast.LENGTH_SHORT).show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                TripEntry._ID,
                TripEntry.COLUMN_TRIP_NUMBER,
                TripEntry.COLUMN_STATE,
                TripEntry.COLUMN_FROM_TO,
                TripEntry.COLUMN_RECEIVED_DATE,
                TripEntry.COLUMN_SUBMITTED_DATE,
                TripEntry.COLUMN_HUB_INITIAL,
                TripEntry.COLUMN_HUB_END};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this, TripEntry.CONTENT_URI, projection, null, null, "CAST(" + TripEntry.COLUMN_TRIP_NUMBER + " AS FLOAT) DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        cursor.moveToFirst();
        // Update {@link TripCursorAdapter} with this new cursor containing updated data
        tripsCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be reset
        tripsCursorAdapter.swapCursor(null);
    }

}
