package com.example.jsonlistview;

import java.util.ArrayList;
import java.util.List;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class DataAdapter extends ArrayAdapter<WebData>{

	
	private LayoutInflater inflater;
	List<WebData> dummyData = new ArrayList<WebData>();
	MainActivity host;
	
	
	public DataAdapter(MainActivity host, List<WebData> data)
	{
		super(host,R.layout.json_data,data);
        inflater = host.getWindow().getLayoutInflater();
        dummyData = data;
               
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		
		View itemView = convertView;
		
		if(itemView == null)
		{
			itemView = inflater.inflate(R.layout.json_data,parent,false);
		}
		
		WebData currentData = dummyData.get(position);
		TextView userName = (TextView) itemView.findViewById(R.id.tv_userName);
		userName.setText(currentData.getUserName());
		TextView userId = (TextView) itemView.findViewById(R.id.tv_userID);
		userId.setText(currentData.getUserId());
		return itemView;
	}
}
