package rs.in.raf1;

import rs.in.raf1.AllNotes;
import rs.in.raf1.NewNote;
import rs.in.raf1.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class Main extends Activity {
	Button btnViewNotes;
	Button btnNewNote;

	protected boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		} else {
			Toast.makeText(Main.this, "You are not connected to Internet", Toast.LENGTH_SHORT).show();
			return false;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		// Buttons
		btnViewNotes = (Button) findViewById(R.id.btnViewNotes);
		btnNewNote = (Button) findViewById(R.id.btnCreateNote);

		// view products click event
		btnViewNotes.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				if (isOnline()) {
					// Launching All notes Activity
					Intent i = new Intent(getApplicationContext(), AllNotes.class);
					startActivity(i);
				}
			}
		});

		// view notes click event
		btnNewNote.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				if (isOnline()) {
					// Launching create new product activity
					Intent i = new Intent(getApplicationContext(), NewNote.class);
					startActivity(i);
				}
			}
		});

	}
}
