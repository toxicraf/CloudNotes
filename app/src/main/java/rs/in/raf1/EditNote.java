package rs.in.raf1;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EditNote extends Activity {

	EditText txtTitle;
	EditText txtDesc;
	EditText txtCreatedAt;
	Button btnSave;
	Button btnDelete;

	String nid;

	// Progress Dialog
	private ProgressDialog pDialog;

	// JSON parser class
	JSON jsonParser = new JSON();

	// single product url
	private static final String url_note_detials = "http://www.raf1.in.rs/notes/get_note_details.php";

	// url to update product
	private static final String url_update_note = "http://www.raf1.in.rs/notes/update_note.php";
	
	// url to delete product
	private static final String url_delete_note = "http://www.raf1.in.rs/notes/delete_note.php";

	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_NOTE = "note";
	private static final String TAG_NID = "nid";
	private static final String TAG_TITLE = "title";
	private static final String TAG_DESCRIPTION = "description";

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
		}
		return super.onOptionsItemSelected(item);
	}	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_note);

		// save button
		btnSave = (Button) findViewById(R.id.btnSave);
		btnDelete = (Button) findViewById(R.id.btnDelete);

		// getting product details from intent
		Intent i = getIntent();
		
		// getting note id (nid) from intent
		nid = i.getStringExtra(TAG_NID);

		// Getting complete product details in background thread
		new GetNoteDetails().execute();

		// save button click event
		btnSave.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// starting background task to update product
				new SaveNoteDetails().execute();
			}
		});

		// Delete button click event
		btnDelete.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// deleting product in background thread
				new DeleteNote().execute();
			}
		});

	}

	/**
	 * Background Async Task to Get complete product details
	 * */
	class GetNoteDetails extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(EditNote.this);
			pDialog.setMessage("Loading note details. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Getting product details in background thread
		 * */
		protected String doInBackground(String... params) {

			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					// Check for success tag
					int success;
					try {
						// Building Parameters
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("nid", nid));

						// getting product details by making HTTP request
						// Note that product details url will use GET request
						JSONObject json = jsonParser.makeHttpRequest(
								url_note_detials, "GET", params);

						// check your log for json response
						Log.d("Single Note Details", json.toString());
						
						// json success tag
						success = json.getInt(TAG_SUCCESS);
						if (success == 1) {
							// successfully received product details
							JSONArray noteObj = json
									.getJSONArray(TAG_NOTE); // JSON Array
							
							// get first product object from JSON Array
							JSONObject note = noteObj.getJSONObject(0);

							// product with this id found
							// Edit Text
							txtTitle = (EditText) findViewById(R.id.inputTitle);
							txtDesc = (EditText) findViewById(R.id.inputDesc);

							// display product data in EditText
							txtTitle.setText(note.getString(TAG_TITLE));
							txtDesc.setText(note.getString(TAG_DESCRIPTION));

						}else{
							// note with id not found
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});

			return null;
		}


		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once got all details
			pDialog.dismiss();
		}
	}

	/**
	 * Background Async Task to  Save product Details
	 * */
	class SaveNoteDetails extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(EditNote.this);
			pDialog.setMessage("Saving note ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Saving note
		 * */
		protected String doInBackground(String... args) {

			// getting updated data from EditTexts
			String title = txtTitle.getText().toString();
			String description = txtDesc.getText().toString();

			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(TAG_NID, nid));
			params.add(new BasicNameValuePair(TAG_TITLE, title));
			params.add(new BasicNameValuePair(TAG_DESCRIPTION, description));

			// sending modified data through http request
			// Notice that update product url accepts POST method
			JSONObject json = jsonParser.makeHttpRequest(url_update_note,
					"POST", params);

			// check json success tag
			try {
				int success = json.getInt(TAG_SUCCESS);
				
				if (success == 1) {
					// successfully updated
					Intent i = getIntent();
					// send result code 100 to notify about product update
					setResult(100, i);
					finish();
				} else {
					// failed to update product
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
			// dismiss the dialog once product updated
			pDialog.dismiss();
		}
	}

	/*****************************************************************
	 * Background Async Task to Delete Product
	 * */
	class DeleteNote extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(EditNote.this);
			pDialog.setMessage("Deleting Note...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Deleting product
		 * */
		protected String doInBackground(String... args) {

			// Check for success tag
			int success;
			try {
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("nid", nid));

				// getting product details by making HTTP request
				JSONObject json = jsonParser.makeHttpRequest(
						url_delete_note, "POST", params);

				// check your log for json response
				Log.d("Delete Note", json.toString());
				
				// json success tag
				success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					// product successfully deleted
					// notify previous activity by sending code 100
					Intent i = getIntent();
					// send result code 100 to notify about product deletion
					setResult(100, i);
					finish();
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
			// dismiss the dialog once product deleted
			pDialog.dismiss();

		}

	}
}
