
package com.easyprinter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class FileListAdapter extends BaseAdapter{
	private ArrayList<FileList> lista = new ArrayList<FileList>();
	
	/**
	 * How many items are in the adapter
	 * @return	count of items
	 */
	@Override
	public int getCount() {
		return lista.size();
	}

	/**
	 * Get the data item associated with the position
	 * @param position	the position of the item
	 * @return					the data
	 */
	@Override
	public Object getItem(int position) {
		return lista.get(position);
	}

	/**
	 * Get the row associated with the position
	 * @param position	the position of the item
	 * @return					the id of the item
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * Displays the data at the position in the data set. The view can be created from a layout or manually
	 * @param position			the position of the item
	 * @param convertView		the old view to reuse
	 * @param parent				the parent that this view will be attached
	 * @return							a view corresponding to the data at the specified position
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if ( convertView == null ){
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			convertView = inflater.inflate(R.layout.list_item_search, parent, false);
		}
		
		FileList fich = lista.get(position);
		TextView tx = (TextView)convertView.findViewById(R.id.txt_list);
		tx.setText(fich.getName());
		TextView tx2 = (TextView)convertView.findViewById(R.id.txt_list_absolute_path);
		tx2.setText(fich.getAbsolutePath());
		
		return convertView;
	}
	
	public void add(FileList filelist){
		lista.add(filelist);
	}
	
}
