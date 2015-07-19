package com.example.jsonlistview;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

public class GetWebpageTask extends AsyncTask<String, Void, String> {
	
MainActivity host;
private ProgressDialog pDialog;
	
	
	public GetWebpageTask(MainActivity host){
		this.host = host;
	}

	@Override
	protected void onPreExecute() {
		this.pDialog  = new ProgressDialog(host);
	    this.pDialog.setMessage("Please wait..");
	    this.pDialog.setIndeterminate(true);
	    this.pDialog.setCancelable(false);
	    this.pDialog.show();    
	}

	@Override
	protected String doInBackground(String... url) {
		
		return host.getWebsite(url[0]);
	}

	@Override
	protected void onPostExecute(String result) {
		this.pDialog.dismiss();
		//host.showAlertDialog(host, "Success", "Data fetched Successfully. Click + to view it", true);
		host.showSuccessMessage();
		Log.d("Webdata",result);
		host.parseTheFeed(result);
		super.onPostExecute(result);
		
	}

	@Override
	protected void onProgressUpdate(Void... values) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(values);
	}
	

}
