
package com.easyprinter;

import android.app.*;
import android.content.*;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import java.io.*;

/**
 *
 * @author victor
 */
public class SearchFile extends Activity {
	FileListAdapter filelistadapter;
	
	/**
	 * Called when the activity is first created.
	 * @param icicle	Bundle
	 */
	@Override
	protected void onCreate(Bundle icicle) {
		final ListView listView;
		final Context c = SearchFile.this;
		super.onCreate(icicle);
		setContentView(R.layout.search_main);
		
		listView = (ListView) findViewById(R.id.list_files);
		filelistadapter = new FileListAdapter();
		
		final ProgressDialog progressdialog = ProgressDialog.show(this, "Loading", null, true);
		new Thread(new Runnable(){
			@Override
			public void run(){
				try{
					Thread.sleep(1500);
					progressdialog.dismiss();
				}catch(InterruptedException ex){
					Log.e("Thread", ex.getMessage());
				}
			}
		}).start();
		
		Search();
		
		if ( filelistadapter == null ) Log.e("Search file", "error");
		listView.setAdapter(filelistadapter);		
		
		listView.setOnItemClickListener(new OnItemClickListener(){
			
			/**
			 * Set a listener on an item override the method from the interface OnItemClickListener
			 */
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id){
				TextView tx = (TextView)view.findViewById(R.id.txt_list_absolute_path);
				String s = tx.getText().toString();
				
				Intent data = new Intent();
				data.putExtra("fichero", s);
				setResult(Activity.RESULT_OK, data);
				finish();
			}
		});
	}
	
	/**
	 * onDestroy method
	 */
	@Override
	protected void onDestroy(){
		super.onDestroy();
		finish();
	}
	
	/**
	 * Search files in the sdcard
	 * Note: future versions set parameter for a custom search in a directory
	 */
	public void Search(){
		try{
			File f = new File("/mnt/sdcard/download/");
			if ( f.isDirectory() ){
				File[] docfiles = f.listFiles(new FilenameFilter(){
					@Override
					public boolean accept(File dir, String name){
						return name.endsWith(".pdf") || name.endsWith(".txt");
					}
				});
				
				for(int i=0;i<docfiles.length;i++){
					filelistadapter.add(new FileList(f.getPath(), docfiles[i].getName()));
				}
			}
		}catch(Exception ex){
			Log.e("SearchFile", ex.getMessage());
		}
	}
	
	/**
	 * Cancel de search and return to the previous activity
	 * @param view		the view of the button
	 */
	public void onCancelSearch(View view){
		onDestroy();
	}
}
