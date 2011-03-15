package org.safermobile.sms;

import java.util.Date;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.widget.Toast;

public class SMSSentStatusReceiver extends BroadcastReceiver {

	private String _toPhoneNumber;
	private String _fromPhoneNumber;
	
	private String _toMsg;
	
	public SMSSentStatusReceiver (String fromPhoneNumber, String toPhoneNumber, String msg)
	{
		_fromPhoneNumber = fromPhoneNumber;
		_toPhoneNumber = toPhoneNumber;
		_toMsg = msg;
	}
	
	@Override
	 public void onReceive(Context content, Intent arg1) {
		
		int resultCode = getResultCode();
		String resultTxt = "";
		
		Date ts = new Date();
		
        switch (getResultCode())
        {
            case Activity.RESULT_OK:
            	resultTxt = "sent";
                break;
            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
            	resultTxt = "generic failure";
                break;
            case SmsManager.RESULT_ERROR_NO_SERVICE:
            	resultTxt = "error no service";
                break;
            case SmsManager.RESULT_ERROR_NULL_PDU:
            	resultTxt = "error null pdu";
                break;
            case SmsManager.RESULT_ERROR_RADIO_OFF:
            	resultTxt = "radio off";
                break;
        }
        
        SMSLogger.logError(_fromPhoneNumber, _toPhoneNumber, resultTxt, ts);
    }

}
