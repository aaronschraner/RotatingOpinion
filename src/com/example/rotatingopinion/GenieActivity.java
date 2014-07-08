package com.example.rotatingopinion;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import android.app.Activity;
import android.os.Bundle;
import android.util.Xml;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

//import android.util.Log; //DEBUG

public class GenieActivity extends Activity
{
	public static final String KEY_BUTTONS_CLICKABLE ="BUTTONS_CLICKABLE"; //state encoding key for button clickability
	boolean buttonClickable[] = {true, true, true }; //stores button clickability state
	Button b1 = null;
	Button b2 = null;
	Button b3 = null;
	Button secretButton = null; //Ssshhh..
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_genie); //open Genie activity
		b1=(Button)findViewById(R.id.button1);
		b2=(Button)findViewById(R.id.button2);
		b3=(Button)findViewById(R.id.button3);
		secretButton=(Button)findViewById(R.id.button4); 
		secretButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v)
			{
				for(short i=0;i<3;i++)
					buttonClickable[i]=true; 
				updateClickableness();
			}
		}); 
		restoreButtons(); //reload Button states from XML cache file
		OnClickListener buttonListener = new OnClickListener() { //general purpose button listener
			public void onClick(View v)
			{
				((Button)v).setClickable(false);
				String buttonText=(String)((Button)v).getText();
				if(buttonText.equals("Wish 1"))
					buttonClickable[0]=false;
				if(buttonText.equals("Wish 2"))
					buttonClickable[1]=false;
				if(buttonText.equals("Wish 3"))
					buttonClickable[2]=false;
			}
			
		};
		b1.setOnClickListener(buttonListener);
		b2.setOnClickListener(buttonListener);
		b3.setOnClickListener(buttonListener);
		updateClickableness();
	}
	private String boolToString(boolean in) //used in XML encoding
	{
		if(in)
			return "true";
		else
			return "false";
	}
	private boolean stringToBool(String in) //used in XML decoding
	{
		if(in.equals("true"))
			return true;
		else if(in.equals("false"))
			return false;
		else return false;
	}
	private void saveButtons() //save button states to XML file
	{
		updateClickableness(); //you can never have too many of these
		try {
			File xmlFile=new File(getCacheDir()+"/wishcache.xml"); //open XML cache file
			FileOutputStream fos = new FileOutputStream(xmlFile); //open file stream for file (required for XML writing)
			XmlSerializer wishxml = Xml.newSerializer(); //create XML container for button data
			StringWriter writer = new StringWriter(); //for writing XML to file
			String namespace=null; 
			wishxml.setOutput(writer); //direct XML output
			wishxml.startDocument("UTF-8",true); //initialize document
			wishxml.startTag(namespace, "button_states");
			for(int i=0;i<3;i++)
			{
				wishxml.startTag(namespace, "button");
				wishxml.attribute(namespace, "id", Integer.toString(i));
				wishxml.attribute(namespace, "clickable", boolToString(buttonClickable[i]));
				wishxml.endTag(namespace, "button");
			}
			wishxml.endTag(namespace, "button_states"); //end list of buttons
			wishxml.endDocument(); //end XML doc 
			fos.write(writer.toString().getBytes()); //output XML string to file
			fos.close(); //close XML file
			//Log.w("FilePath", xmlFile.getAbsolutePath() ); //DEBUG
			
		} catch (FileNotFoundException e) {
			//Log.w("FilePath","FILE NOT FOUND 404 EVERYONE PANIC"); //DEBUG
		} catch (IOException e) {
			//Log.w("FilePath","IOEXCEPTION WE ARE DOOMED"); //DEBUG
		}
	}
	
	
	void updateClickableness() //ensures that button clickability is always up to date
	{
		b1=(Button)findViewById(R.id.button1);
		b2=(Button)findViewById(R.id.button2);
		b3=(Button)findViewById(R.id.button3);
		b1.setClickable(buttonClickable[0]);
		b2.setClickable(buttonClickable[1]);
		b3.setClickable(buttonClickable[2]);
		
	}
	private void restoreButtons() //reload button states from XML file
	{
		updateClickableness(); 
		File xmlFile=new File(getCacheDir()+"/wishcache.xml"); //open XML cache file
		InputStream fis = null;
		String ns=null;
		//Log.w("FilePath","Idk what's going on but restoreButtons is running..."); //DEBUG
		try
		{
			//Log.w("FilePath","Beginning restoreButtons try block"); //DEBUG
			fis=new FileInputStream(xmlFile); //open XML file for reading
			XmlPullParser buttonXmlParser=Xml.newPullParser(); //create an XML parser
			buttonXmlParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false); 
			buttonXmlParser.setInput(fis,null); //assign parser to our InputStream
			buttonXmlParser.next(); //skip header information
			buttonXmlParser.require(XmlPullParser.START_TAG, ns, "button_states"); //ensure we are in the correct spot
			buttonXmlParser.nextTag(); //move to next tag
			for(int i=0;i<3;i++)
			{
				int buttonId;
				boolean clickable;
				buttonXmlParser.require(XmlPullParser.START_TAG, ns, "button"); //ensure correct spot
				buttonId = Integer.parseInt(buttonXmlParser.getAttributeValue(ns, "id")); //get button ID attribute
				clickable = stringToBool(buttonXmlParser.getAttributeValue(ns, "clickable")); //get clickability attribute
				buttonClickable[buttonId] = clickable; //assign clickability to selected button
				
				//Log.w("FilePath", "For Loop iteration " + i + "; id: " + buttonXmlParser.getAttributeValue(ns, "id") + //DEBUG
						//"; clickable: " + buttonXmlParser.getAttributeValue(ns, "clickable"));
				buttonXmlParser.nextTag(); //skip to next tag
				buttonXmlParser.require(XmlPullParser.END_TAG, ns, "button"); //check spot
				buttonXmlParser.nextTag(); //next tag
			}
			//Log.w("FilePath","Assigning Clickableness"); //DEBUG
			
			updateClickableness(); //self-explanatory
			
			//Log.w("FilePath","End Try block"); //DEBUG
		} catch(FileNotFoundException e) {
			//Log.w("FilePath","SHE'S GONE CAPTAIN"); //DEBUG
		} catch(IOException e) {
			//Log.w("FilePath", "WE GOT AN IOEXCEPTION ON READ CAPTAIN"); //DEBUG
		} catch (XmlPullParserException e) {
			//Log.w("FilePath","XML pull parser problem"+e.toString()); //DEBUG
			try 
			{
				fis.close();
			} catch (IOException e2) {
				//Log.w("FilePath","problem closing xml"); //DEBUG
			}
		}
		updateClickableness(); //You can never be too careful.
	}
	@Override
	protected void onDestroy()
	{
		super.onDestroy(); //thank you obvious stack trace
		saveButtons(); //write button states to XML file
		
	}
	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		outState.putBooleanArray(KEY_BUTTONS_CLICKABLE, buttonClickable); //to avoid rotating for free wishes
		//and to minimize filesystem operations
	}
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState)
	{
		super.onRestoreInstanceState(savedInstanceState);
		buttonClickable=savedInstanceState.getBooleanArray(KEY_BUTTONS_CLICKABLE); //restore from background
		updateClickableness(); 
	}
}
