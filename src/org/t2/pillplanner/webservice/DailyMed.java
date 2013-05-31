package org.t2.pillplanner.webservice;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.t2.pillplanner.classes.NDCDrug;

import android.util.Log;

public class DailyMed {

	public static int startRow = 0;
	public static int maxRows = 100;

	public static ArrayList<NDCDrug> getImprintData(String ndc)
	{

		String baseURL = "http://dailymed.nlm.nih.gov/dailymed/services/v1/ndc/[ndc]/imprintdata.json";

		try
		{
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(baseURL.replace("[ndc]", ndc));

			HttpResponse response = httpClient.execute(httpGet);

			if (response.getStatusLine().getStatusCode() == 200) {

				BufferedReader br = new BufferedReader(
						new InputStreamReader((response.getEntity().getContent())));

				String result = "";
				String output;
				while ((output = br.readLine()) != null) {
					result += output;
				}

				httpClient.getConnectionManager().shutdown();

				ArrayList<NDCDrug> alDrugs = new ArrayList<NDCDrug>();
				
				JSONObject root = new JSONObject(result);
				JSONArray data = root.getJSONArray("DATA");
				for(int i=0;i<data.length();i++)
				{
					JSONArray drugJSON = data.getJSONArray(i);
					NDCDrug drug = new NDCDrug();
					drug.SETID = drugJSON.getString(0);
					drug.SPL_VERSION = drugJSON.getString(1);
					drug.NAME = drugJSON.getString(2);
					drug.PRODUCT_CODE = drugJSON.getString(3);
					drug.SPLCOLOR = drugJSON.getString(4);
					drug.COLOR_TEXT = drugJSON.getString(5);
					drug.SPLIMPRINT = drugJSON.getString(6);
					drug.SPLSHAPE = drugJSON.getString(7);
					drug.SHAPE_TEXT = drugJSON.getString(8);
					drug.SPLSIZE = drugJSON.getString(9);
					drug.SPLSCORE = drugJSON.getString(10);
					drug.SPLSYMBOL = drugJSON.getString(11);
					drug.SPLCOATING = drugJSON.getString(12);
					drug.PUBLISHED_DATE = drugJSON.getString(13);
					
					alDrugs.add(drug);
				}
				
				return alDrugs;
			}

		}
		catch(Exception ex){
			Log.v("ERROR", ex.toString());
		}
		return null;


	}

}
