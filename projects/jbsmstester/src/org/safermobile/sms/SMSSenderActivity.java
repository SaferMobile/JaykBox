package org.safermobile.sms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SMSSenderActivity extends Activity {
	
	private String thisPhoneNumber;
	private SMSLogger _smsLogger;
	private SmsManager sms = SmsManager.getDefault();
    
	public final static short SMS_DATA_PORT = 7027;
	boolean useDataPort = false;

	private TextView textView = null;
	
	private final static String SENT = "SMS_SENT";
	private final static String DELIVERED = "SMS_DELIVERED";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log);
        
        thisPhoneNumber = getMyPhoneNumber(); //get the local device number
        
        textView = (TextView)findViewById(R.id.messageLog);
        
    	try
		{	
    		_smsLogger = new SMSLogger("send");
    		_smsLogger.setLogView(textView);
		}
		catch (Exception e)
		{
			Toast.makeText(this, "Error setting up SMS Log: " + e.getMessage(), Toast.LENGTH_LONG).show();
		}
		
	
        
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
         SMSSentStatusReceiver statusRev = new SMSSentStatusReceiver(thisPhoneNumber, phoneNumber, message, _smsLogger);
         registerReceiver(statusRev, new IntentFilter(SENT));
        
        if (!useDataPort)
        {
        	sms.sendTextMessage(phoneNumber, null, message, pi, null);      
        }
        else
        {
        	sms.sendDataMessage(phoneNumber, null, SMS_DATA_PORT, message.getBytes(), pi, null);
        }
        
        _smsLogger.logSend(thisPhoneNumber, phoneNumber, message, new Date());
    } 
    
    @Override
	protected void onDestroy() {
		super.onDestroy();
		
		try {
			_smsLogger.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void startSMSTest ()
    {
		
		textView.setText("");
		
    	AlertDialog.Builder alert = new AlertDialog.Builder(this);

    	alert.setTitle("Start SMS");
    	alert.setMessage("Enter target phone number");

    	// Set an EditText view to get user input 
    	final EditText input = new EditText(this);
    	alert.setView(input);

    	alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
    	public void onClick(DialogInterface dialog, int whichButton) {
    	  String value = input.getText().toString();
    	  sendTestMessages (value, useDataPort);
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
    	try {
			_smsLogger.rotateLog();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	Iterator<String> itMsgs = loadTestMessageList().iterator();
    	
    	while (itMsgs.hasNext())
    		sendSMS(toPhoneNumber,itMsgs.next(), useDataPort);
    }
    
    private ArrayList<String> loadTestMessageList ()
    {
    	ArrayList<String> listMsg = new ArrayList<String>();
    	
    	listMsg.add("hello");
    	listMsg.add("ciao");
    	listMsg.add("foo");
    	listMsg.add("bar");
    	
    	
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
        SMSSentStatusReceiver statusRev = new SMSSentStatusReceiver(thisPhoneNumber, phoneNumber, message, _smsLogger);
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
        _smsLogger.logSend(thisPhoneNumber, phoneNumber, message, new Date());

    }    
    
    /*
     * Create the UI Options Menu (non-Javadoc)
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
     public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);
         
         MenuItem mItem = null;
         
         mItem = menu.add(0, 1, Menu.NONE, "Start Test");
        
        
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
 		
         return true;
 	}
}