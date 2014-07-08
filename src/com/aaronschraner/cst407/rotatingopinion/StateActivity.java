package com.aaronschraner.cst407.rotatingopinion;

import java.util.UUID;

import com.example.rotatingopinion.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;



public class StateActivity extends Activity
{
	private static final String KEY_RAND_TEXT = "RANDOM_TEXT";
	String randomString=null;
	TextView tvRand = null;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_state);
		tvRand = (TextView)findViewById(R.id.textView2);
		randomString = UUID.randomUUID().toString();
		tvRand.setText(randomString);
	}
	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		outState.putString(KEY_RAND_TEXT, randomString);
	}
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState)
	{
		super.onRestoreInstanceState(savedInstanceState);
		randomString=savedInstanceState.getString(KEY_RAND_TEXT);
		tvRand.setText(randomString);
	}
}
