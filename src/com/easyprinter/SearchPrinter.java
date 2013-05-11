
package com.easyprinter;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class SearchPrinter extends Activity {

	/**
	 * Called when the activity is first created.
	 */
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.settings_main);
	}
	
	/**
	 * Finish the activity
	 */
	@Override
	protected void onDestroy(){
		super.onDestroy();
		finish();
	}
	
	/**
	 * Hide the activity
	 * @param view	the view
	 */
	public void onCancelSettings(View view){
		onDestroy();
	}
	
	/**
	 * Send a message to the server to search a shared printer
	 * @param view		the view
	 */
	public void onSearchPrinter(View view){
		String message = "SEARCHING";		//the message to initialize
		TextView tx = (TextView) findViewById(R.id.text_ip_address);
		try{
			String ip = tx.getText().toString();
			Task t = new Task();
			t.execute(message, ip);	
			/*
			message = "FIN";
			t.execute(message, ip);
			* */
		}catch(Exception ex){
			Log.e("onSearchPrinter", ex.getMessage());
		}
	}
	
	//AsyncTask
	protected class Task extends AsyncTask<String, Integer, String>{

		@Override
		protected String doInBackground(String... params) {
			Comm c = new Comm();
			Log.d("AsyncTask", "Param0->" + params[0] + "; Param1->" + params[1]);
			return c.sendMessage(params[0], params[1]);
		}
		
		@Override
		protected void onPostExecute(String result){
			super.onPostExecute(result);
			Log.i("AsyncTask", result);
		}		
		
		@Override
		protected void onCancelled(){
			super.onCancelled();
			Log.i("AsyncTask", "Cancelled");
		}
	}
}
