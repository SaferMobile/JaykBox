package org.safermobile.sms;

import java.util.Date;

import android.util.Log;

public class SMSLogger {

	
	private final static String TAG = "JBSMS";
	
	public static void logSend (String from, String to, String smsMsg, Date sent)
	{
		String[] vals = {"send",from,to,smsMsg,sent.getTime()+""};
		Log.i(TAG, generateCSV(vals));
	}
	
	public static void logReceive (String from, String to, String smsMsg, Date rec)
	{
		String[] vals = {"rec",from,to,smsMsg,rec.getTime()+""};
		Log.i(TAG, generateCSV(vals));
	}
	
	public static void logError (String from, String to, String error, Date ts)
	{
		String[] vals = {"error",from,to,error,ts.getTime()+""};
		Log.i(TAG, generateCSV(vals));
	}
	
	public static void logDelivery (String from, String to, String deliveryStatus, Date ts)
	{
		String[] vals = {"delivery",from,to,deliveryStatus,ts.getTime()+""};
		Log.i(TAG, generateCSV(vals));
	}
	
	private static String generateCSV(String[] params)
	{
		StringBuffer csv = new StringBuffer();
		
		for (int i = 0; i < params.length; i++)
		{
			csv.append('"');
			csv.append(params[i]);
			csv.append('"');

			if ((i+1)<params.length)
				csv.append(',');
			
		}
		
		return csv.toString();
	}
}
