package com.example.jsonlistview;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.internal.widget.IcsAdapterView.AdapterContextMenuInfo;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;


public class MainActivity extends SherlockActivity {
	List<WebData> webData = new ArrayList<WebData>();
	List<WebData> parseData = new ArrayList<WebData>();
	List<WebData> feedRead = new ArrayList<WebData>();
		
	String response;
	ListView dataList;
	Boolean isInternetPresent = false;
    ConnectionDetector cd;
	int clickCounter= 0,itemSelected =0;
	boolean buttonClicked = false;
	private static final String App_URL = "https://api.github.com/gists/public";
	private static final String TWEETS_CACHE_FILE = "json_feed.ser";
	private static final int MENU_DELETE_ID = 1004;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		renderFeed();
		dataList = (ListView) findViewById(R.id.data_listView);
		registerForContextMenu(dataList);
		cd = new ConnectionDetector(getApplicationContext());
		isInternetPresent = cd.isConnectingToInternet();
		
		if (isInternetPresent) {
            
            GetWebpageTask task = new GetWebpageTask(MainActivity.this);
			task.execute(App_URL);
			
        } else {
            
            showAlertDialog(MainActivity.this, "No Internet Connection",
                    "You don't have internet connection. Click OK to close app !!!", false);
        }
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSherlock().getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		
		android.widget.AdapterView.AdapterContextMenuInfo info = (android.widget.AdapterView.AdapterContextMenuInfo) menuInfo;
		itemSelected = (int)info.id;
		Log.d("Menu Selected","Item Id :- "+itemSelected);
		menu.add(0, MENU_DELETE_ID, 0, "DELETE");
	}
	
	
	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
		
		if(item.getItemId() == MENU_DELETE_ID){
			
			deleteSelected(webData,itemSelected);
			Log.d("Item Selected","Id of Item :- "+itemSelected);
		}
		
		return super.onContextItemSelected(item);
		
	}
	
	
	public void deleteSelected(List<WebData> data, int selection)
	{
		Log.d("Before Deletion","Total size of data :- "+data.size());
		data.remove(selection);
		Log.d("After Deletion","Total size of data :- "+data.size());
		//letspopulateData(data);
		
	}
	
	
	
	public String getWebsite(String address)
	{
		StringBuffer stringBuffer = new StringBuffer();
		
		BufferedReader reader = null;
		try{
				URL url = new URL(address);
				HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
				
				        
				InputStream in = new BufferedInputStream(urlConnection.getInputStream());
				
				reader = new BufferedReader(new InputStreamReader(in));
				String line="";
				
				while((line = reader.readLine())!=null){
					stringBuffer.append(line);
				}
				
				
		}catch (IOException e){
			e.printStackTrace();
		}finally{
			if(reader != null){
			try {
					reader.close();
				}catch(IOException e){
					e.printStackTrace();					
				} 
				
			}
		}
		
		return stringBuffer.toString();
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		if(item.getItemId() == R.id.action_add)
		{
			letspopulateData(webData);
						
		}
		
		else if(item.getItemId() == R.id.action_remove){
			
			
			removeOneItem(webData);
			
		}
		
		return super.onOptionsItemSelected(item);
		
		
	}
	
	
	public void parseTheFeed(String response){
		
		try {
			JSONArray jsonObj = new JSONArray(response);
			
			for(int i=0 ; i<jsonObj.length(); i++)
		    {                                       
				WebData data = new WebData();
				JSONObject json_Data = jsonObj.getJSONObject(i);
		        String name = json_Data.getJSONObject("owner").getString("login");
		        String id = json_Data.getJSONObject("owner").getString("id");
		        data.setUserName(name.toString());
		        data.setUserId(id.toString());
		        webData.add(data);
		    }
			
			writeTweetstofile(webData, MainActivity.this);
			
		} catch (JSONException e) {
			Log.d("Failure","Dude I have failed");
			webData = feedRead;
		}
		
		
	}
	
	
	
	
	
	public void letspopulateData(List<WebData> data)
	{
		final List<WebData> listData = new ArrayList<WebData>();
		dataList = (ListView) findViewById(R.id.data_listView);
		if(clickCounter < data.size())
		{
			clickCounter++;
		}
		else
		{
			Toast.makeText(this, "Showing Data From Beginning", Toast.LENGTH_SHORT).show();
			clickCounter = 1;
		}
		
		for(int i = 0;i<clickCounter;i++)
		{
			listData.add(data.get(i));
		}
		
		DataAdapter adapter = new DataAdapter(MainActivity.this, listData);
		dataList.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		dataList.setStackFromBottom(true);
		adapter.notifyDataSetChanged();
		dataList.setAdapter(adapter);
		
		dataList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
		            long id) {
				
				WebData d = new WebData();
				d = listData.get(position);
				
				Intent intent = new Intent(MainActivity.this,SelectedDataActivity.class);
				intent.putExtra("object", d);
				startActivity(intent);
				
				
				//String message = "Position :- "+ position + " Item :- " + tv.getText().toString();
				//Log.d("Item Clicked","User name :- "+d.getUserName()+" id :- "+d.getUserId());
				//Toast.makeText(MainActivity.this, "User name :- "+d.getUserName()+"\nid :- "+d.getUserId(), Toast.LENGTH_SHORT).show();
			}
		});
		
		
		
		
	}
	
	
	
	public void removeOneItem(List<WebData> data){
	
		final List<WebData> listData = new ArrayList<WebData>();
		Log.d("Remove","Counter value :- "+clickCounter);
		
		if(clickCounter <= 0)
		{
			Toast.makeText(MainActivity.this, "Nothing to remove", Toast.LENGTH_SHORT).show();
		}
		
		else
		{
		for(int i = 0;i<clickCounter-1;i++)
		{
			listData.add(data.get(i));
		}
		
		clickCounter--;
		DataAdapter adapter = new DataAdapter(MainActivity.this, listData);
		dataList.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		dataList.setStackFromBottom(true);
		adapter.notifyDataSetChanged();
		dataList.setAdapter(adapter);
		
		dataList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
		            long id) {
				
				WebData d = new WebData();
				d = listData.get(position);
				Intent intent = new Intent(MainActivity.this,SelectedDataActivity.class);
				intent.putExtra("object", d);
				startActivity(intent);
				//String message = "Position :- "+ position + " Item :- " + tv.getText().toString();
				//Log.d("Item Clicked","User name :- "+d.getUserName()+" id :- "+d.getUserId());
				//Toast.makeText(MainActivity.this, "User name :- "+d.getUserName()+"\nid :- "+d.getUserId(), Toast.LENGTH_SHORT).show();
			}
		});
		
		}
		
	}
	
	public void registerClickCallback(final List<WebData> data)
	{
		
		dataList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View viewClicked, int position,
					long id) {
				WebData d = new WebData();
				d = data.get(position);
				//String message = "Position :- "+ position + " Item :- " + tv.getText().toString();
				Log.d("Item Clicked","User name :- "+d.getUserName()+" id :- "+d.getUserId());
				Toast.makeText(MainActivity.this, "User name :- "+d.getUserName()+" id :- "+d.getUserId(), Toast.LENGTH_SHORT).show();
				
				
			}
		});
	}
	
	public void showSuccessMessage(){
		
		Toast.makeText(this, "Data Fetched. Click + to view it !!!", Toast.LENGTH_LONG).show();
	}
	
	
	
	@SuppressWarnings("deprecation")
	public void showAlertDialog(Context context, String title, String message, final Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
 
        alertDialog.setTitle(title);
 
        alertDialog.setMessage(message);
         
        alertDialog.setIcon((status) ? R.drawable.success : R.drawable.fail);
 
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	if(status == false)
            	{
            		finish();
            	}
            }
        });
 
        alertDialog.show();
    }
	
	public void writeTweetstofile(List<WebData>data,Context ctx)
	{
		try {
				FileOutputStream fos = ctx.openFileOutput(TWEETS_CACHE_FILE, Context.MODE_PRIVATE);
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				oos.writeObject(data);
				oos.close();
				fos.close();
				Log.d("codelearn", "Successfully wrote tweets to the file asynchronously.");
		    //close operators
		  } catch (Exception e) {
		    Log.e("codelearn", "Error writing tweets", e);
		  }
	}
	
	
	@SuppressWarnings("unchecked")
	public void renderFeed() 
	{
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		
		try {			
				fis = openFileInput(TWEETS_CACHE_FILE);
				ois = new ObjectInputStream(fis);
				feedRead = ( List<WebData> ) ois.readObject();
				Log.e("codelearn", "Successfully read tweets from the file.");
		    //close operators
			} catch (Exception e) 
			{
				Log.e("codelearn", "Error reading tweets", e);
			}finally
			{
				try
				{
					ois.close();
					fis.close();
				}
				catch (Exception e)
				{
					Log.e("codelearn","Error");
					
				}
			}
	}
	
}


