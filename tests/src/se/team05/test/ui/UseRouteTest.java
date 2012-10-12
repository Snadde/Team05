package se.team05.test.ui;

import java.util.ArrayList;

import se.team05.R;
import se.team05.activity.ListExistingRoutesActivity;
import se.team05.activity.MainActivity;
import se.team05.activity.RouteActivity;
import se.team05.content.Route;
import se.team05.content.Track;
import se.team05.data.DBCheckPointAdapter;
import se.team05.data.DBGeoPointAdapter;
import se.team05.data.DBRouteAdapter;
import se.team05.data.Database;
import se.team05.data.DatabaseHandler;
import se.team05.overlay.CheckPoint;
import se.team05.test.util.MockLocationUtil;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.provider.MediaStore;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.Button;

import com.google.android.maps.GeoPoint;
import com.jayway.android.robotium.solo.Solo;

public class UseRouteTest extends ActivityInstrumentationTestCase2<MainActivity>
{
	private Solo solo;

	public UseRouteTest()
	{
		super(MainActivity.class);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		solo = new Solo(this.getInstrumentation(), getActivity());

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

	public void testUseRouteIfNonExisting()
	{
		solo.clickOnButton(1);
		solo.assertCurrentActivity("Expected ListExistingRoutesActivity", ListExistingRoutesActivity.class);
		solo.clickOnText("Click to add a route");
		solo.assertCurrentActivity("Expected RouteActivity", RouteActivity.class);
		solo.clickOnActionBarHomeButton();
		solo.assertCurrentActivity("Expected MainActivity", MainActivity.class);
	}
	
	public void testUseExistingRoute() throws Throwable
	{
		DatabaseHandler databaseHandler = new DatabaseHandler(getActivity());
		Route route = new Route("name", "description");
		long rid = databaseHandler.saveRoute(route);
		
		ArrayList<GeoPoint> geoPointList = new ArrayList<GeoPoint>();
		GeoPoint gpA = new GeoPoint((int)(47.975 * 1E6), (int)(17.056 * 1E6));
		GeoPoint gpB = new GeoPoint((int)(48.975 * 1E6), (int)(17.056 * 1E6));
		geoPointList.add(gpA);
		geoPointList.add(gpB);
		databaseHandler.saveGeoPoints(rid, geoPointList);
		
		CheckPoint checkPoint = new CheckPoint(new GeoPoint((int) (48.975 * 1E6), (int) (17.056 * 1E6)));
		checkPoint.setRid(rid);
		long cid = databaseHandler.saveCheckPoint(checkPoint);
		
		Cursor cursor = getActivity().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[] { MediaStore.Audio.Media.DATA }, null, null, null);
		cursor.moveToFirst();
		if(cursor.getCount() < 1)
		{
			fail("Could not find media in the media store, please add media to your device and reboot before testing again.");
		}
		else
		{
			Track track = new Track("id", "artist", "album", "title", cursor.getString(0), "displayName", "duration");
			databaseHandler.saveTrack(cid, track);
		}
		
		solo.clickOnButton(1);
		solo.clickInList(0);
		solo.assertCurrentActivity("Expected RouteActivity", RouteActivity.class);
		
		final RouteActivity routeActivity = (RouteActivity) solo.getCurrentActivity();
		
		Button startButton = (Button) solo.getView(R.id.start_existing_run_button);
		Button stopButton = (Button) solo.getView(R.id.stop_existing_run_button);
		assertEquals(startButton.getVisibility(), View.VISIBLE);
		assertEquals(stopButton.getVisibility(), View.GONE);
		solo.clickOnView(startButton);
		assertEquals(startButton.getVisibility(), View.GONE);
		assertEquals(stopButton.getVisibility(), View.VISIBLE);

		MockLocationUtil mockLocation = new MockLocationUtil();
		MockLocationUtil.publishMockLocation(47.975, 17.056, routeActivity);
		Thread.sleep(1500);
		Location locationA = mockLocation.getLastKnownLocationInApplication(routeActivity);
		assertEquals(47.975, locationA.getLatitude());
		assertEquals(17.056, locationA.getLongitude());
		
		MockLocationUtil.publishMockLocation(48.975, 17.056, routeActivity);
		Thread.sleep(1500);
		final Location locationB = mockLocation.getLastKnownLocationInApplication(routeActivity);
		assertEquals(48.975, locationB.getLatitude());
		assertEquals(17.056, locationB.getLongitude());
		
		runTestOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				routeActivity.updateLocation(locationB);
			}
		});
		
		
		solo.clickOnView(stopButton);
		solo.clickOnButton("Yes");
		assertEquals(startButton.getVisibility(), View.VISIBLE);
		assertEquals(stopButton.getVisibility(), View.GONE);
		
		solo.clickOnView(startButton);
		solo.clickOnView(stopButton);
		solo.clickOnButton("No");
		solo.assertCurrentActivity("Expected MainActivity", MainActivity.class);
	}

}
