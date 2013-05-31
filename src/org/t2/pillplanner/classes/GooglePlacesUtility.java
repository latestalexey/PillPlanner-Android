package org.t2.pillplanner.classes;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

//import com.google.android.gms.maps.model.LatLng;

import android.content.Context;
import android.content.ContextWrapper;

/**
 * Handles all JSON lookups. Used to pull place data from Google Places
 * 
 * @author Steve Ody (stephen.ody@tee2.org)
 */

//API CONSOLE: https://code.google.com/apis/console/

public class GooglePlacesUtility extends ContextWrapper 
{

	public String gPlacesURL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?radius=16093&sensor=true&key=";
	public String gDetailsURL = "https://maps.googleapis.com/maps/api/place/details/json?";
	
	public GooglePlacesUtility(Context base) 
	{
		super(base);
	}

//	public String getPlacesURLByTypes(String Types, LatLng latlng) 
//	{
//
//		String outString = gPlacesURL + Global.googlePlacesAPIKey;
//
//		if (!Types.trim().equals(""))
//			outString += "&types=" + Types;
//
//		outString += "&location=" + latlng.latitude + "," + latlng.longitude;
//
//		return outString;
//	}

	public ArrayList<JSONObject> SearchPlaces(String uri) throws Exception 
	{
	    
		ArrayList<JSONObject> results = new ArrayList<JSONObject>();

		// Get the data from google
		HttpGet httpGet = new HttpGet(uri);
		HttpParams httpParameters = new BasicHttpParams();
		int timeoutConnection = 5000;
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
		int timeoutSocket = 5000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

		DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
		HttpResponse response = httpClient.execute(httpGet);
		BufferedReader r = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		StringBuilder sb = new StringBuilder();
		String s = null;

		while ((s = r.readLine()) != null) 
		{
			sb.append(s);
		}

		JSONObject jsonResults = new JSONObject(sb.toString());

		// Parse the JSON into an arraylist
		JSONArray dataArray = null;
		JSONObject jsonEntry;
		dataArray = jsonResults.getJSONArray("results");
		if (dataArray != null) 
		{

			for (int i = 0; i < dataArray.length(); i++) 
			{

				jsonEntry = dataArray.getJSONObject(i);
				results.add(jsonEntry);
			}
		}

		return results;

	}

	public JSONObject GetPlaceDetails(String reference) throws Exception 
	{
	    
		String uri = gDetailsURL + "reference=" + reference + "&sensor=true&key=" + Global.googlePlacesAPIKey;

		// Get the data from google
		HttpGet req = new HttpGet(uri);

		HttpClient client = new DefaultHttpClient();
		HttpResponse resLogin = client.execute(req);
		BufferedReader r = new BufferedReader(new InputStreamReader(resLogin.getEntity().getContent()));
		StringBuilder sb = new StringBuilder();
		String s = null;
		while ((s = r.readLine()) != null) 
		{
			sb.append(s);
		}

		JSONObject jsonResults = new JSONObject(sb.toString());

		// Parse the JSON object
		JSONObject jsonEntry;
		jsonEntry = jsonResults.getJSONObject("result");
		

		return jsonEntry;

	}

}


