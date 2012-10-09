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
 */
package se.team05.listener;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;

/**
 * The MapLocationListener class is a LocationListener which uses callback to update the location
 * @author Patrik Thituson
 *
 */
public class MapLocationListener implements LocationListener
{
	public interface Callbacks
	{
		public void updateLocation(Location location);
	}

	private Callbacks callback;
	
	/**
	 * The Constructor
	 * @param callback
	 */
	public MapLocationListener(Callbacks callback)
	{
		this.callback = callback;
	}
	
	/**
	 * Helper method to calculate the distance between geo point A and B.
	 * 
	 * @param geoPointA
	 *            the first point
	 * @param geoPointB
	 *            the second point
	 * @return a float representing the distance.
	 */
	public static float getDistance(GeoPoint geoPointA, GeoPoint geoPointB)
	{
		double latitudeA = ((double) geoPointA.getLatitudeE6()) / 1E6;
		double longitudeA = ((double) geoPointA.getLongitudeE6()) / 1E6;
		double latitudeB = ((double) geoPointB.getLatitudeE6()) / 1E6;
		double longitudeB = ((double) geoPointB.getLongitudeE6()) / 1E6;
		float[] distance = new float[1];
		Location.distanceBetween(latitudeA, longitudeA, latitudeB, longitudeB, distance);
		return distance[0];
	}
	
	/**
	 * Calls updateLocation in callback with the location
	 */
	@Override
	public void onLocationChanged(Location location)
	{
		callback.updateLocation(location);
	}
	
	
	/**
	 * Unused method
	 */
	@Override
	public void onProviderDisabled(String provider)
	{

	}
	
	/**
	 * Unused method
	 */
	@Override
	public void onProviderEnabled(String provider)
	{

	}

	/**
	 * Unused method
	 */
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras)
	{

	}

}
