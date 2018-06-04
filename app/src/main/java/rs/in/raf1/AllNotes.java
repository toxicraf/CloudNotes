package rs.in.raf1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class AllNotes extends ListActivity {

	// Progress Dialog
	private ProgressDialog pDialog;

	// Creating JSON Parser object
	JSON jParser = new JSON();

	ArrayList<HashMap<String, String>> notesList;

	// url to get all products list
	private static String url_all_notes = "http://www.raf1.in.rs/notes/get_all_notes.php";

	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_NOTES = "notes";
	private static final String TAG_NID = "nid";
	private static final String TAG_TITLE = "title";

	// products JSONArray
	JSONArray notes = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.all_notes);

		// Hashmap for ListView
		notesList = new ArrayList<HashMap<String, String>>();

		// Loading products in Background Thread
		new LoadAllNotes().execute();

		// Get listview
		ListView lv = getListView();

		// on selecting single note
		// launching Edit Note Screen
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// getting values from selected ListItem
				String nid = ((TextView) view.findViewById(R.id.nid)).getText()
						.toString();

				// Starting new intent
				Intent in = new Intent(getApplicationContext(),
						EditNote.class);
				// sending id to next activity
				in.putExtra(TAG_NID, nid);
				
				// starting new activity and expecting some response back
				startActivityForResult(in, 100);
			}
		});

	}

	// Response from Edit Product Activity
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// if result code 100
		if (resultCode == 100) {
			// if result code 100 is received 
			// means user edited/deleted product
			// reload this screen again
			Intent intent = getIntent();
			finish();
			startActivity(intent);
		}

	}

	/**
	 * Background Async Task to Load all product by making HTTP Request
	 * */
	class LoadAllNotes extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(AllNotes.this);
			pDialog.setMessage("Loading notes. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting All products from url
		 * */
		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			// getting JSON string from URL
			JSONObject json = jParser.makeHttpRequest(url_all_notes, "GET", params);
			
			// Check your log cat for JSON reponse
			Log.d("All Notes: ", json.toString());

			try {
				// Checking for SUCCESS TAG
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					// products found
					// Getting Array of Products
					notes = json.getJSONArray(TAG_NOTES);

					// looping through All Products
					for (int i = 0; i < notes.length(); i++) {
						JSONObject c = notes.getJSONObject(i);

						// Storing each json item in variable
						String nid = c.getString(TAG_NID);
						String title = c.getString(TAG_TITLE);

						// creating new HashMap
						HashMap<String, String> map = new HashMap<String, String>();

						// adding each child node to HashMap key => value
						map.put(TAG_NID, nid);
						map.put(TAG_TITLE, title);

						// adding HashList to ArrayList
						notesList.add(map);
					}
				} else {
					// no products found
					// Launch Add New product Activity
					Intent i = new Intent(getApplicationContext(),
							NewNote.class);
					// Closing all previous activities
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all products
			pDialog.dismiss();
			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					/**
					 * Updating parsed JSON data into ListView
					 * */
					ListAdapter adapter = new SimpleAdapter(
							AllNotes.this, notesList,
							R.layout.list_item, new String[] { TAG_NID,
									TAG_TITLE},
							new int[] { R.id.nid, R.id.title });
					// updating listview
					setListAdapter(adapter);
				}
			});

		}

	}
}