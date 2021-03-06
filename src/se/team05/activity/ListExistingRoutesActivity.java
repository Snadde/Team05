/**
	This file is part of Personal Trainer.

    Personal Trainer is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.

    Personal Trainer is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Personal Trainer.  If not, see <http://www.gnu.org/licenses/>.

    (C) Copyright 2012: Daniel Kvist, Henrik Hugo, Gustaf Werlinder, Patrik Thitusson, Markus Schutzer
 */
package se.team05.activity;

import se.team05.R;
import se.team05.content.Route;
import se.team05.data.DBRouteAdapter;
import se.team05.data.DatabaseHandler;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * An activity that will present the user with the option to choose and old
 * route. Gets routes from database and presents them in a listview.
 * 
 * @author Markus, Henrik Hugo
 * 
 */
public class ListExistingRoutesActivity extends ListActivity
{
	private Context context;

	/**
	 * Presents a list of all existing routes to choose from and
	 * if no routes exist an option to create a new route will
	 * be presented.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_existing_routes);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// Add empty view with quick link to record a new route
		this.context = getApplicationContext();
		TextView emptyView = (TextView) findViewById(R.id.empty_view);
		emptyView.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(context, RouteActivity.class);
				startActivity(intent);
			}
		});
		getListView().setEmptyView(emptyView);

		// Setup database connection and get cursor with results
		DatabaseHandler db = new DatabaseHandler(this);
		Cursor cursor = db.getAllRoutesCursor();

		// Setup adapter
		RouteListCursorAdapter routeListCursorAdapter = new RouteListCursorAdapter(
				this, android.R.layout.simple_list_item_1, 
				cursor,
				new String[] { DBRouteAdapter.COLUMN_NAME },
				new int[] { android.R.id.text1 }
		);

		this.setListAdapter(routeListCursorAdapter);

	}
	
	/**
	 * Is called upon by the system whenever a listitem is clicked
	 * and will then launch RouteActivity, passing along the id of
	 * the selected route.
	 */
	public void onListItemClick(ListView l, View v, int position, long id)
	{
		Intent intent;

		intent = new Intent(this.getApplicationContext(), RouteActivity.class);
		intent.putExtra(Route.EXTRA_ID, id);
		
		// Debugging of values passed in by the system
		Log.d(getString(R.string.id), String.valueOf(id));
		Log.d(getString(R.string.position), String.valueOf(position));

		this.startActivity(intent);
	}

	/**
	 * This method is called when an item in the action bar (options menu) has
	 * been pressed. Currently this only takes the user to the parent activity
	 * (main activity).
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Simple class for automatically formating the content, from a cursor
	 * returned by the database, to a listview.
	 * 
	 * @author Henrik Hugo
	 * 
	 */
	private class RouteListCursorAdapter extends SimpleCursorAdapter
	{

		@SuppressWarnings("deprecation")
		public RouteListCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to)
		{
			super(context, layout, c, from, to);
		}

		@SuppressWarnings("deprecation")
		public RouteListCursorAdapter(Context context, int layout, Cursor c)
		{
			super(context, layout, c, null, null);
		}
	}

}
