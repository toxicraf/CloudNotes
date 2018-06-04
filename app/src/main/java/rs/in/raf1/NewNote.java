package rs.in.raf1;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NewNote extends Activity {

	// Progress Dialog
	private ProgressDialog pDialog;

	JSON jsonParser = new JSON();
	EditText inputTitle;
	EditText inputDesc;

	// url to create new note
	private static String url_create_note = "http://www.raf1.in.rs/notes/create_note.php";

	// JSON Node names
	private static final String TAG_SUCCESS = "success";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_note);

		// Edit Text
		inputTitle = (EditText) findViewById(R.id.inputTitle);
		inputDesc = (EditText) findViewById(R.id.inputDesc);

		// Create button
		Button btnCreateNote = (Button) findViewById(R.id.btnCreateNote);

		// button click event
		btnCreateNote.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				// creating new product in background thread
				new CreateNewNote().execute();
			}
		});
	}

	/**
	 * Background Async Task to Create new product
	 * */
	class CreateNewNote extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(NewNote.this);
			pDialog.setMessage("Creating Note..");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Creating product
		 * */
		@SuppressWarnings("deprecation")
		protected String doInBackground(String... args) {
			String title = inputTitle.getText().toString();
			String description = inputDesc.getText().toString();
			
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("title", title));
			params.add(new BasicNameValuePair("description", description));
						
			// getting JSON Object
			// Note that create product url accepts POST method
			JSONObject json = jsonParser.makeHttpRequest(url_create_note,
					"POST", params);
			
			// check log cat fro response
			Log.d("Create Response", json.toString());

			// check for success tag
			try {
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					// successfully created product
					Intent i = new Intent(getApplicationContext(), AllNotes.class);
					startActivity(i);
					
					// closing this screen
					finish();
				} else {
					// failed to create product
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
			// dismiss the dialog once done
			pDialog.dismiss();
		}

	}
}
