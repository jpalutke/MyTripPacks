/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.crystaltowerdesigns.mytrippacks;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crystaltowerdesigns.mytrippacks.data.TripsContract;

/**
 * {@link TripsCursorAdapter} is an adapter that displays a {@link Cursor} of Trips.
 */
class TripsCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link TripsCursorAdapter}.
     *
     * @param context App context.
     * @param cursor  Cursor containing trip data.
     */
    public TripsCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0 /* flags */);
    }

    /**
     * Creates a blank item view.
     *
     * @param context App context.
     * @param cursor  Cursor containing trip data.
     * @param parent  The parent to which the new view is attached to
     *
     * @return New list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate the layout specified in list_item_view
        return LayoutInflater.from(context).inflate(R.layout.list_item_view, parent, false);
    }

    /**
     * Binds the trip row designated by the cursor
     *
     * @param view    Existing view.
     * @param context App context.
     * @param cursor  Cursor containing trip data.
     */
    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView tripNumberTextView = view.findViewById(R.id.textView_tripNumber);
        TextView receivedDateTextView = view.findViewById(R.id.textView_receivedDate);
        TextView fromToTextView = view.findViewById(R.id.textView_fromTo);

        // TODO: Get column indexes... can we pull the stop table details here?
        final int receivedDateColumnIndex = cursor.getColumnIndex(TripsContract.TripEntry.COLUMN_RECEIVED_DATE);
        final int idColumnIndex = cursor.getColumnIndex(TripsContract.TripEntry._ID);
        final int tripNumberColumnIndex = cursor.getColumnIndex(TripsContract.TripEntry.COLUMN_TRIP_NUMBER);
        final int fromToColumnIndex = cursor.getColumnIndex(TripsContract.TripEntry.COLUMN_FROM_TO);

        // Read the attributes from the Cursor for the current entry
        final String tripNumber = cursor.getString(tripNumberColumnIndex);
        final String receivedDate = cursor.getString(receivedDateColumnIndex);
        final String fromTo = cursor.getString(fromToColumnIndex);
        final int id = cursor.getInt(idColumnIndex);

        // TODO: Get COLUMN_STATE and colorize as needed
        // Update the TextViews with the attributes for the current entry
        tripNumberTextView.setText(tripNumber);
        receivedDateTextView.setText(receivedDate);
        fromToTextView.setText(fromTo);
    }
}
