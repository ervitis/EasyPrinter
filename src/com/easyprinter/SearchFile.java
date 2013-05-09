
package com.easyprinter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import java.io.File;
import java.io.FilenameFilter;

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
		
		//int[] to = new int[] {R.id.txt_list};
		
		listView = (ListView) findViewById(R.id.list_files);
		filelistadapter = new FileListAdapter();
		Search();
		//filelistadapter.add(new FileList("/mnt/prueba", "prueba.txt"));
		
		listView.setAdapter(filelistadapter);		
		
		listView.setOnItemClickListener(new OnItemClickListener(){
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
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		finish();
	}
	
	public void Search(){
		try{
			File f = new File("/mnt/sdcard");
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
}
