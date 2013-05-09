
package com.easyprinter;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import java.net.Socket;

public class SearchPrinter extends Activity {
	private static int TCP = 7770;

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
		//socket to server for searching shared printers
		final String message = "SEARCHING";		//the message to initialize
		final TextView tx = (TextView) findViewById(R.id.text_ip_address);
		try{
			final Socket s = new Socket(tx.getText().toString(), TCP);
			
			new Thread(new Runnable(){
				@Override
				public void run() {
					
				}
				
			}).start();
			
		}catch(Exception ex){
			Log.e("Socket", ex.getMessage());
		}
	}
}
