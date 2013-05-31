package org.t2.pillplanner;

import java.text.DecimalFormat;
import java.util.ArrayList;
import org.json.JSONObject;
import org.t2.pillplanner.classes.GooglePlacesUtility;

import com.actionbarsherlock.app.SherlockFragmentActivity;
//import com.google.android.gms.common.GooglePlayServicesUtil;
//import com.google.android.gms.maps.CameraUpdate;
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
//import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
////import com.google.android.gms.maps.GoogleMapOptions;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.Marker;
//import com.google.android.gms.maps.model.MarkerOptions;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class PharmacyActivity extends SherlockFragmentActivity implements LocationListener{//, OnMarkerClickListener, OnInfoWindowClickListener{

	//https://code.google.com/apis/console/
	//beekaa account
	
	private float initialZoom = 10f;
	
	//private GoogleMap map;
	//private LatLng latLng;

	private MapInfoFragment mapInfo;
	
	private ArrayList<JSONObject> jsonPlacesArray;
	private LocationManager locationManager;
	private static final long MIN_TIME = 400;
	private static final float MIN_DISTANCE = 1000;

	private JSONObject jsonDetails;
	
	//private Timer updTimer;
	//private final int updateFrequencyMillis = 30000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_pharmacy);

		mapInfo = ((MapInfoFragment) getSupportFragmentManager().findFragmentById(R.id.popup));
		
		//GoogleMapOptions options = new GoogleMapOptions();

//		if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext()) == 0)
//		{
//			map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
//			map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//
//			map.setMyLocationEnabled(true);
//			map.setOnInfoWindowClickListener(this);
//					
//			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this); 
//
//		}
	}

	Runnable showInfo = new Runnable()
	{
		public void run()
		{
			try
			{
				mapInfo.setDetails(jsonDetails.getString("name"), jsonDetails.getString("formatted_address"), jsonDetails.getString("formatted_phone_number"));
				mapInfo.setInfoVisiblility(true);
			}
			catch(Exception ex){}
		}
	};
	
	Runnable updateUI = new Runnable() 
	{
		public void run() 
		{
			try {

				for(int i=0; i< jsonPlacesArray.size(); i++)
				{
					JSONObject tmp = jsonPlacesArray.get(i);
					JSONObject geo = tmp.getJSONObject("geometry");
					JSONObject loc = geo.getJSONObject("location");

					Double lat = loc.getDouble("lat");
					Double lng = loc.getDouble("lng");

					String placeName = tmp.getString("name");
					//String vicinity = tmp.getString("vicinity");

//					map.addMarker(new MarkerOptions()
//					.position(new LatLng(lat, lng))
//					.snippet("Tap for info")
//					.title(placeName));

				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	private void pollStatus()
	{
		Runnable myRunnable = new Runnable() 
		{
			public void run() 
			{

//				GooglePlacesUtility pu = new GooglePlacesUtility(getApplicationContext());
//				try {
//					jsonPlacesArray = pu.SearchPlaces(pu.getPlacesURLByTypes("pharmacy", latLng));
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//
//
//				try
//				{
//					runOnUiThread(updateUI);
//				}
//				catch(Exception ex){
//				}
			}
		};

		Thread thread = new Thread(null, myRunnable, "LocationThread");
		thread.start();
	}

	@Override 
	public void onPause()
	{
		super.onPause();
	}

	@Override
	public void onResume()
	{
		super.onResume();
	}

	@Override
	public void onLocationChanged(Location location) {
//		latLng = new LatLng(location.getLatitude(), location.getLongitude());
//		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, initialZoom);
//		map.animateCamera(cameraUpdate);
//		pollStatus();
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

//	@Override
//	public boolean onMarkerClick(final Marker marker) {
//
//		return true;
//	}
//
//	@Override
//	public void onInfoWindowClick(Marker marker) {
//
//		for(int i=0; i< jsonPlacesArray.size(); i++)
//		{
//			try
//			{
//				JSONObject tmp = jsonPlacesArray.get(i);
//				//String placeName = tmp.getString("name");
//
//				JSONObject geo = tmp.getJSONObject("geometry");
//				JSONObject loc = geo.getJSONObject("location");
//
//				DecimalFormat df = new DecimalFormat("####.######");
//				df.format(0.912385);
//				
//				Double lat = Double.parseDouble(df.format(loc.getDouble("lat")));
//				Double lng = Double.parseDouble(df.format(loc.getDouble("lng")));
//				Double mlat = Double.parseDouble(df.format(marker.getPosition().latitude));
//				Double mlng = Double.parseDouble(df.format(marker.getPosition().longitude));
//				
//				final String reference = tmp.getString("reference");
//
//				if((mlat.equals(lat)) && (mlng.equals(lng)))
//				{
//					Runnable myRunnable = new Runnable() 
//					{
//						public void run() 
//						{
//
//							GooglePlacesUtility pu = new GooglePlacesUtility(getApplicationContext());
//							try {
//								jsonDetails = pu.GetPlaceDetails(reference);
//								
//								runOnUiThread(showInfo);
//								
//							} catch (Exception e) {
//								e.printStackTrace();
//							}
//							
//						}
//					};
//
//					Thread thread = new Thread(null, myRunnable, "DetailThread");
//					thread.start();
//					break;
//				}
//			}
//			catch(Exception ex){}
//		}
//
//	}
}