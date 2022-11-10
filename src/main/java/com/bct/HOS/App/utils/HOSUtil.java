package com.bct.HOS.App.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

public class HOSUtil {

	public JSONArray sortJSOArray(JSONArray jsonArr,final String key) {

		JSONArray sortedJsonArray = new JSONArray();

		List<JSONObject> jsonValues = new ArrayList<JSONObject>();
		for (int i = 0; i < jsonArr.size(); i++) {
			jsonValues.add(jsonArr.getJSONObject(i));
		}
		Collections.sort(jsonValues, new Comparator<JSONObject>() {
			// You can change "Name" with "ID" if you want to sort by ID
			private final String KEY_NAME = key;

			public int compare(JSONObject a, JSONObject b) {
				String valA = new String();
				String valB = new String();

				try {
					valA = a.getString(KEY_NAME);
					valB = b.getString(KEY_NAME);
				} catch (JSONException e) {
					// do something
				}

				return valA.compareTo(valB);
				// if you want to change the sort order, simply use the following:
				// return -valA.compareTo(valB);
			}
		});

		for (int i = 0; i < jsonArr.size(); i++) {
			sortedJsonArray.add(jsonValues.get(i));
		}

		return sortedJsonArray;

	}

}
