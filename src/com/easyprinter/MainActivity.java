package com.easyprinter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainActivity extends Activity
{
  /**
   * Called when the activity is created
   * @param savedInstanceState contains the activity's previously frozen state
   */
  @Override
  protected void onCreate(Bundle savedInstanceState){
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
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
		
	}
}
