package org.safermobile.sms;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.StringTokenizer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SMSSenderActivity extends Activity {
	
	private String _fromPhoneNumber;
	private String _toPhoneNumber;
	private SMSLogger _smsLogger;
	private SmsManager sms = SmsManager.getDefault();
    
	public final static short SMS_DATA_PORT = 7027;
	boolean _useDataPort = true;

	private TextView _textView = null;
	
	private final static String SENT = "SMS_SENT";
	private final static String DELIVERED = "SMS_DELIVERED";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log);
        
        _fromPhoneNumber = getMyPhoneNumber(); //get the local device number
        
        _textView = (TextView)findViewById(R.id.messageLog);
        
        loadPrefs();
        
    	try
		{	
    		_smsLogger = new SMSLogger(SMSLogger.MODE_SEND);
    		_smsLogger.setLogView(_textView);
		}
		catch (Exception e)
		{
			Toast.makeText(this, "Error setting up SMS Log: " + e.getMessage(), Toast.LENGTH_LONG).show();
		}
		
	
        
    }
    
    @Override
	protected void onResume() {
		super.onResume();
		
		 loadPrefs();
	}

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
     
    	   loadPrefs();
    }
    
    private void loadPrefs ()
    {
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplication());

        _toPhoneNumber = prefs.getString("pref_default_recipient", "");
        _useDataPort = prefs.getBoolean("pref_use_data", false);

    }
    
    private String getMyPhoneNumber(){
        TelephonyManager mTelephonyMgr;
        mTelephonyMgr = (TelephonyManager)
                getSystemService(Context.TELEPHONY_SERVICE); 
        return mTelephonyMgr.getLine1Number();
        }
    
    //---sends an SMS message to another device---
    private void sendSMS(String phoneNumber, String message, boolean useDataPort)
    {        
    	PendingIntent pi = null;
    	
    	 PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
    	            new Intent(SENT), 0);
    	        
    	 //---when the SMS has been sent---
         SMSSentStatusReceiver statusRev = new SMSSentStatusReceiver(_fromPhoneNumber, phoneNumber, message, _smsLogger);
         registerReceiver(statusRev, new IntentFilter(SENT));
        
        if (!useDataPort)
        {
        	sms.sendTextMessage(phoneNumber, null, message, pi, null);      
        }
        else
        {
        	sms.sendDataMessage(phoneNumber, null, SMS_DATA_PORT, message.getBytes(), pi, null);
        }
        
        _smsLogger.logSend(_fromPhoneNumber, phoneNumber, message, new Date());
    } 
    
    @Override
	protected void onDestroy() {
		super.onDestroy();
		
	}

	private void startSMSTest ()
    {
		
		_textView.setText("");
		
    	AlertDialog.Builder alert = new AlertDialog.Builder(this);

    	alert.setTitle("Start SMS");
    	alert.setMessage("Enter target phone number");

    	// Set an EditText view to get user input 
    	final EditText input = new EditText(this);
    	
    	if (_toPhoneNumber != null)
    		input.setText(_toPhoneNumber);
    	
    	alert.setView(input);

    	alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
    	public void onClick(DialogInterface dialog, int whichButton) {
    	  _toPhoneNumber = input.getText().toString();
    	  sendTestMessages (_toPhoneNumber, _useDataPort);
    	  }
    	});

    	alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    	  public void onClick(DialogInterface dialog, int whichButton) {
    	    // Canceled.
    	  }
    	});

    	alert.show();
    }
    
    private void sendTestMessages (String toPhoneNumber, boolean useDataPort)
    {
    	_smsLogger.rotateLog();
		
    	Iterator<String> itMsgs = loadTestMessageList().iterator();
    	
    	while (itMsgs.hasNext())
    		sendSMS(toPhoneNumber,itMsgs.next(), useDataPort);
    
    }
    
    private ArrayList<String> loadTestMessageList ()
    {
    	ArrayList<String> listMsg = new ArrayList<String>();
    	
    	String kwlist = Utils.loadTextFile(EditKeywordActivity.KEYWORD_FILE);
    	StringTokenizer st = new StringTokenizer(kwlist,"\n");
    	while (st.hasMoreTokens())
    		listMsg.add(st.nextToken());
    	
    	return listMsg;
    }
    //---sends an SMS message to another device---
    private void sendSMSMonitor(String phoneNumber, String message)
    {        
    	
        PendingIntent sentPI = null;
        PendingIntent deliveredPI = null;
       
        sentPI = PendingIntent.getBroadcast(this, 0,
            new Intent(SENT), 0);
 
        deliveredPI = PendingIntent.getBroadcast(this, 0,
            new Intent(DELIVERED), 0);
 
        //---when the SMS has been sent---
        SMSSentStatusReceiver statusRev = new SMSSentStatusReceiver(_fromPhoneNumber, phoneNumber, message, _smsLogger);
        registerReceiver(statusRev, new IntentFilter(SENT));
 
        //---when the SMS has been delivered---
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:

                    	//  SMSLogger.logDelivery(thisPhoneNumber, phoneNumber, "delivered", ts)

                        break;
                    case Activity.RESULT_CANCELED:
                        
                        //  SMSLogger.logDelivery(thisPhoneNumber, phoneNumber, "delivered", ts)

                        break;                        
                }
            }
        }, new IntentFilter(DELIVERED));        
        
        
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);   
        _smsLogger.logSend(_fromPhoneNumber, phoneNumber, message, new Date());

    }    
    
    /*
     * Create the UI Options Menu (non-Javadoc)
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
     public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);
         
         MenuItem mItem = null;
         
         mItem = menu.add(0, 1, Menu.NONE, "Start Test");
         mItem = menu.add(0, 2, Menu.NONE, "Settings");
        
         return true;
     }
     
     /* When a menu item is selected launch the appropriate view or activity
      * (non-Javadoc)
 	 * @see android.app.Activity#onMenuItemSelected(int, android.view.MenuItem)
 	 */
 	public boolean onMenuItemSelected(int featureId, MenuItem item) {
 		
 		super.onMenuItemSelected(featureId, item);
 		
 		if (item.getItemId() == 1)
 		{
 			startSMSTest();
 		}
 		else if (item.getItemId() == 2)
 		{
        	startActivityForResult(new Intent(getBaseContext(), SettingsActivity.class), 1);

 		}
 		
         return true;
 	}
}