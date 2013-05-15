
package com.easyprinter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.*;
import android.util.Log;
import android.view.View;
import android.widget.*;

public class SearchPrinter extends Activity {
	final String result = "";
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
		String r = "";
		//Handler handler = new Handler();
		
		try{
			String ip = tx.getText().toString();
			//set the ip
			TextView txt = (TextView) findViewById(R.id.ip_address);
			txt.setText(ip);
			
			final ProgressDialog progressdialog = ProgressDialog.show(this, "Loading...", null, true);
			new Thread(new Runnable(){
				@Override
				public void run(){
					try{
						Thread.sleep(2000);
						progressdialog.dismiss();
					}catch(InterruptedException ex){
						Log.e("Thread", ex.getMessage());
					}
				}
			}).start();
			
			Task t = new Task();
			t.execute(message, ip);
			r = t.get();
			
			if ( r.contains(";") ){
				message = "FIN";
				t.execute(message, ip);
			}
			else{
				Toast.makeText(this, "Arranque el servicio en el servidor", Toast.LENGTH_LONG).show();
			}
		}catch(Exception ex){
			Log.e("onSearchPrinter", ex.getMessage());
		}
	}
	
	/**
	 * AsyncTask class
	 * String: the params
	 * Void: progress (not in use)
	 * String: the result type
	 */
	class Task extends AsyncTask<String, Void, String>{
		private String r = "";

		@Override
		protected String doInBackground(String... params) {
			Comm c = new Comm();
			Log.d("AsyncTask", "Param0->" + params[0] + "; Param1->" + params[1]);
			r = c.sendMessage(params[0], params[1]);
			return r;
		}
		
		@Override
		protected void onPreExecute(){
			super.onPreExecute();
		}
		
		@Override
		protected void onPostExecute(String result){
			super.onPostExecute(result);
			
			Log.i("AsyncTask", result);
			if ( result.contains(";") ){
				Intent data = new Intent();
				data.putExtra("printerandip", result);
				setResult(Activity.RESULT_OK, data);
			}
			else{
				setResult(Activity.RESULT_CANCELED, null);
			}
			finish();
		}		
		
		@Override
		protected void onCancelled(){
			super.onCancelled();
			Log.i("AsyncTask", "Cancelled");
		}
	}
}
