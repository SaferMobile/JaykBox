package org.safermobile.sms;

import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class SMSReceiver extends BroadcastReceiver {


	SMSLogger _smsLogger;
	 
    @Override
    public void onReceive(Context context, Intent intent) 
    {
    	
    	if (_smsLogger == null)
    	{
	    	try
			{	
				_smsLogger = new SMSLogger("recv");
			}
			catch (Exception e)
			{
				Toast.makeText(context, "Error setting up SMS Log: " + e.getMessage(), Toast.LENGTH_LONG).show();
			}
			
    	}
    	
        //---get the SMS message passed in---
        Bundle bundle = intent.getExtras();        
        SmsMessage[] msgs = null;
        String str = "";            
        if (bundle != null)
        {
            //---retrieve the SMS message received---
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];            
            for (int i=0; i<msgs.length; i++){
                msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);                
                
		        String from = msgs[i].getOriginatingAddress();
		        String to = msgs[i].getServiceCenterAddress();
		        String msg = msgs[i].getMessageBody().toString();
		        Date rec = new Date(msgs[i].getTimestampMillis());
		        
		        _smsLogger.logReceive("recv-text",from, to, msg, rec);
		        
		        Toast.makeText(context, "recvd msg from: " + from, Toast.LENGTH_SHORT).show();
        	}
        }                         
    }
}