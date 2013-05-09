package com.easyprinter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends Activity
{
	private static final int REQUEST_MENU_CODE = 1;
	private static final int REQUEST_SEARCH_FILE = 2;
	private static boolean bGetPrinter = false;
	
  /**
   * Called when the activity is created
   * @param savedInstanceState contains the activity's previously frozen state
   */
  @Override
  protected void onCreate(Bundle savedInstanceState){
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
		
		
		//create some dumb files
		/*
		File f = new File("/mnt/sdcard/prueba/");
		try{
			if ( f.mkdir() ){
				f = new File("/mnt/sdcard/prueba/prueba3.txt");
				f.createNewFile();
			}
		}catch(Exception ex){
			Log.e("MainActivity", ex.getMessage());
		}
		*/
  }
  
  /**
   * Called after the activity has been stopped
   */
  @Override
  protected void onRestart(){
		super.onRestart();
  }
  
  /**
   * Called after onCreate and is on the foreground
   */
  @Override
  protected void onStart(){
		super.onStart();
  }
  
  /**
   * Called when the activity start interacting with the user
   */
  @Override
  protected void onResume(){
		super.onResume();
  }
  
  /**
   * Called when the activity resumes its previously state. It's used to store some data
   */
  @Override
  protected void onPause(){
		super.onPause();
  }
  
  /**
   * When the activity is no longer visible to the user
   */
  @Override
  protected void onStop(){
		super.onStop();
  }
  
  /**
   * Called when the activity is destroyed
   */
  @Override
  protected void onDestroy(){
		super.onDestroy();
		finish();
  }
	
	/**
	 * Initialize the contents of the options menu
	 * @param menu			the menu
	 * @return					<code>true</code> to display the menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		super.onCreateOptionsMenu(menu);
		MenuInflater mInf = getMenuInflater();
		mInf.inflate(R.menu.menu_main, menu);
		return true;
	}
	
	/**
	 * Called when an item is selected from the menu
	 * @param featuredId	the panel where the menu is in
	 * @param item				the menu item selected
	 * @return						<code>true</code> to finnish the selection or 
	 *										<code>false</code> to perform the normal menu handling
	 */
	@Override
	public boolean onMenuItemSelected(int featuredId, MenuItem item){		
		//dispatch
		//exit
		if ( item.getItemId() == R.id.exit ){
			onDestroy();
		}
		//help
		else if ( item.getItemId() == R.id.help ){
			//dialog
			helpDialog();
		}
		//print
		else if ( item.getItemId() == R.id.print ){
			//send the document and progress dialog
		}
		//settings
		else{
			Intent i = new Intent(this, SearchPrinter.class);
			startActivityForResult(i, REQUEST_MENU_CODE);
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Called when an activity launched exists giving the request code and the result code
	 * @param requestCode	the request code to identify it
	 * @param resultCode	the result code returned from the child activity
	 * @param data				the data returned
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if ( requestCode == REQUEST_MENU_CODE ){
			//code settings
			if ( resultCode == RESULT_OK ){
				bGetPrinter = true;
			}
		}
		else if ( requestCode == REQUEST_SEARCH_FILE ){
			if ( resultCode == RESULT_OK ){
				//enable print menu
				//show the file name
				String s = data.getStringExtra("fichero");
				Log.d("MainActivity", s);
			}
		}
	}
	
	/**
	 * Show the new activity for search the file to print
	 * @param view		the View
	 */
	public void onShowSearch(View view){
		Intent i = new Intent(this, SearchFile.class);
		startActivityForResult(i, REQUEST_SEARCH_FILE);
	}
	
	/**
	 * Create the dialog help
	 */
	public void helpDialog(){
		Dialog d = new Dialog(MainActivity.this);
		d.setTitle("Ayuda");
		d.setContentView(R.layout.help_dialog);
		d.show();
	}
}
