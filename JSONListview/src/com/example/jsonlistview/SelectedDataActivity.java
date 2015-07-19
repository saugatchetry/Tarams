package com.example.jsonlistview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class SelectedDataActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.selecteddata);
		TextView tv = (TextView) findViewById(R.id.textView1);
		
		Intent received = getIntent();
		WebData data = (WebData) received.getSerializableExtra("object");
		
		tv.setText("Username :- "+data.getUserName()+"\nId :- "+data.getUserId());
		
	}

}
