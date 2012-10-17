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

package se.team05.test.activity;

import java.util.ArrayList;

import se.team05.R;
import se.team05.activity.ListExistingResultsActivity;
import se.team05.activity.ListExistingRoutesActivity;
import se.team05.activity.MainActivity;
import se.team05.activity.RouteActivity;
import se.team05.activity.ShowResultsActivity;
import se.team05.content.ParcelableGeoPoint;
import se.team05.content.Result;
import se.team05.content.Route;
import se.team05.content.Track;
import se.team05.data.DBCheckPointAdapter;
import se.team05.data.DBGeoPointAdapter;
import se.team05.data.DBRouteAdapter;
import se.team05.data.Database;
import se.team05.data.DatabaseHandler;
import se.team05.listener.MapLocationListener;
import se.team05.overlay.CheckPoint;
import se.team05.test.util.MockLocationUtil;
import android.app.Activity;
import android.app.ListActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.provider.MediaStore;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.maps.GeoPoint;
import com.jayway.android.robotium.solo.Solo;

public class ShowResultsActivityTest extends ActivityInstrumentationTestCase2<MainActivity>
{
	private Solo solo;
	private ImageView oldRouteImage;

	public ShowResultsActivityTest()
	{
		super(MainActivity.class);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		
		Activity activity = getActivity();
		solo = new Solo(this.getInstrumentation(), activity);
		oldRouteImage = (ImageView) activity.findViewById(R.id.image_existing_route);

		SQLiteDatabase db = new Database(this.getInstrumentation().getTargetContext()).getWritableDatabase();
		db.delete(DBRouteAdapter.TABLE_ROUTES, null, null);
		db.delete(DBCheckPointAdapter.TABLE_CHECKPOINTS, null, null);
		db.delete(DBGeoPointAdapter.TABLE_GEOPOINTS, null, null);
	}

	@Override
	protected void tearDown()
	{
		SQLiteDatabase db = new Database(this.getInstrumentation().getTargetContext()).getWritableDatabase();
		db.delete(DBRouteAdapter.TABLE_ROUTES, null, null);
		db.delete(DBCheckPointAdapter.TABLE_CHECKPOINTS, null, null);
		db.delete(DBGeoPointAdapter.TABLE_GEOPOINTS, null, null);
		solo.finishOpenedActivities();
	}

//	public void testUseRouteIfNonExisting()
//	{
//		solo.clickOnView(oldRouteImage);
//		solo.assertCurrentActivity("Expected ListExistingRoutesActivity", ListExistingRoutesActivity.class);
//		solo.clickOnText("Click to add a route");
//		solo.assertCurrentActivity("Expected RouteActivity", RouteActivity.class);
//		solo.clickOnActionBarHomeButton();
//		solo.assertCurrentActivity("Expected MainActivity", MainActivity.class);
//	}
//	
//	public void testUseExistingRoute() throws Throwable
//	{
//		DatabaseHandler databaseHandler = new DatabaseHandler(getActivity());
//		Route route = new Route("name", "description");
//		route.setId(databaseHandler.saveRoute(route));
//		
//		ArrayList<ParcelableGeoPoint> geoPointList = new ArrayList<ParcelableGeoPoint>();
//		ParcelableGeoPoint gpA = new ParcelableGeoPoint((int)(47.975 * 1E6), (int)(17.056 * 1E6));
//		ParcelableGeoPoint gpB = new ParcelableGeoPoint((int)(48.975 * 1E6), (int)(17.056 * 1E6));
//		geoPointList.add(gpA);
//		geoPointList.add(gpB);
//		databaseHandler.saveGeoPoints(route.getId(), geoPointList);
//		
//		CheckPoint checkPoint = new CheckPoint(new GeoPoint((int) (48.975 * 1E6), (int) (17.056 * 1E6)));
//		checkPoint.setRid(route.getId());
//		long cid = databaseHandler.saveCheckPoint(checkPoint);
//		
//		Cursor cursor = getActivity().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[] { MediaStore.Audio.Media.DATA }, null, null, null);
//		cursor.moveToFirst();
//		if(cursor.getCount() < 1)
//		{
//			fail("Could not find media in the media store, please add media to your device and reboot before testing again.");
//		}
//		else
//		{
//			Track track = new Track("id", "artist", "album", "title", cursor.getString(0), "displayName", "duration");
//			databaseHandler.saveTrack(cid, track);
//		}
//		
//		route.setCheckPoints(databaseHandler.getCheckPoints(route.getId()));
//		
//		solo.clickOnView(oldRouteImage);
//		solo.assertCurrentActivity("ListExistingRoutesActivity expected", ListExistingRoutesActivity.class);
//		solo.clickInList(0);
//		solo.assertCurrentActivity("Expected RouteActivity", RouteActivity.class);
//		
//		final RouteActivity routeActivity = (RouteActivity) solo.getCurrentActivity();
//		
//		Button startButton = (Button) solo.getView(R.id.start_button);
//		Button stopButton = (Button) solo.getView(R.id.stop_button);
//		assertEquals(startButton.getVisibility(), View.VISIBLE);
//		assertEquals(stopButton.getVisibility(), View.GONE);
//		solo.clickOnView(startButton);
//		assertEquals(startButton.getVisibility(), View.GONE);
//		assertEquals(stopButton.getVisibility(), View.VISIBLE);
//
//		final MapLocationListener locationListener = new MapLocationListener(routeActivity, false, route.getCheckPoints());
//		MockLocationUtil mockLocation = new MockLocationUtil();
//		MockLocationUtil.publishMockLocation(47.975, 17.056, routeActivity, route.getCheckPoints(), locationListener);
//		Thread.sleep(1500);
//		Location locationA = mockLocation.getLastKnownLocationInApplication(routeActivity);
//		assertEquals(47.975, locationA.getLatitude());
//		assertEquals(17.056, locationA.getLongitude());
//		
//		MockLocationUtil.publishMockLocation(48.975, 17.056, routeActivity, route.getCheckPoints(), locationListener);
//		Thread.sleep(1500);
//		final Location locationB = mockLocation.getLastKnownLocationInApplication(routeActivity);
//		assertEquals(48.975, locationB.getLatitude());
//		assertEquals(17.056, locationB.getLongitude());
//		
//		runTestOnUiThread(new Runnable()
//		{
//			@Override
//			public void run()
//			{
//				locationListener.onLocationChanged(locationB);
//			}
//		});
//		
//		
//		solo.clickOnView(stopButton);
//		solo.clickOnButton("Yes");
//		assertEquals(startButton.getVisibility(), View.VISIBLE);
//		assertEquals(stopButton.getVisibility(), View.GONE);
//		
//		solo.clickOnView(startButton);
//		solo.clickOnView(stopButton);
//		solo.clickOnButton("No");
//	}

	public void testShowingAndDeletingResults()
	{
		DatabaseHandler databaseHandler = new DatabaseHandler(getActivity());
		Route route = new Route("name", "description");
		route.setId(databaseHandler.saveRoute(route));
		
		Result result = new Result(route.getId(), 200000000L, 3700, 1000, 0);		
		long id = databaseHandler.saveResult(result);
		result = databaseHandler.getResultById(id);
		
		Result result2 = new Result(route.getId(), 200000000L, 3700, 1000, 0);		
		long id2 = databaseHandler.saveResult(result);
		result2 = databaseHandler.getResultById(id2);
		
		solo.clickOnView(oldRouteImage);
		solo.assertCurrentActivity("Expected ListExistingRoutesActivity", ListExistingRoutesActivity.class);
		solo.clickInList(0);
		solo.assertCurrentActivity("Expected RouteActivity", RouteActivity.class);
		Button showResultButton =(Button) solo.getView(R.id.show_result_button);
		solo.clickOnView(showResultButton);
		solo.assertCurrentActivity("Expected ListExistingResultsActivity", ListExistingResultsActivity.class);
		solo.clickInList(0);
		
		solo.assertCurrentActivity("Expected ShowResultsActivity", ShowResultsActivity.class);
		Button deleteButton =(Button) solo.getView(R.id.delete_results_button);
		solo.clickOnView(deleteButton);
		solo.assertCurrentActivity("Expected ListExistingResultsActivity", ListExistingResultsActivity.class);
		
		solo.clickInList(0);
		solo.assertCurrentActivity("Expected ShowResultsActivity", ShowResultsActivity.class);
		solo.clickOnActionBarHomeButton();		
		solo.assertCurrentActivity("Expected MainActivity", MainActivity.class);		
	}
}
