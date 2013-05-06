package com.easyprinter;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity
{
  /**
   * Called when the activity is created
   * @param savedInstanceState contains the activity's previously frozen state
   */
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
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
}
