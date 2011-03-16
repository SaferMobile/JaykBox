package org.safermobile.sms;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class SMSLogger {

	
	private final String TAG = "JBSMS";
	private TextView _tvLog;

	private int lineNum = 1;
	
	private String _logMode = null;
	
	private String basePath = "/sdcard/jbsms";
	private String logFilePath = null;
	
	public final static String MODE_SEND = "send";
	public final static String MODE_RECV = "recv";
	public final static String MODE_RECV_DATA = "recvdata";
	
	public SMSLogger (String logMode)
	{
		_logMode = logMode;
		
		if (logFilePath == null)
		{
			rotateLog();
		}
	}
	
	public void rotateLog () 
	{
		File fileDir = new File(basePath);
		if (!fileDir.exists())
			fileDir.mkdir();
		
		Date logDate = new Date();
		logFilePath = basePath + "/jbsmstest" + "-" + _logMode + "-" + logDate.getYear() + logDate.getMonth() + logDate.getDate() + ".csv";
		
	}
	
	public String getLogFilePath ()
	{
		return logFilePath;
	}
	
	public void setLogView (TextView tvLog)
	{
		_tvLog = tvLog;
	}
	
	public void logSend (String from, String to, String smsMsg, Date sent)
	{
		String[] vals = {"sent",from,to,smsMsg,sent.toGMTString()};
		String log = generateCSV(vals) + "\n";
		Log.i(TAG, log);
		
		if (_tvLog != null)
		{
			_tvLog.append(log);

		}
		
		Utils.saveTextFile(logFilePath, log, true);
	
	}
	
	public void logReceive (String from, String to, String smsMsg, Date rec)
	{
		String[] vals = {"rec",from,to,smsMsg,rec.toGMTString()};
		
		String log = generateCSV(vals) + "\n";
		
		Log.i(TAG, log);
		
		if (_tvLog != null)
		{
			_tvLog.append(log);
		}

		Utils.saveTextFile(logFilePath, log, true);

	
	}
	
	public void logError (String from, String to, String error, Date ts)
	{
		String[] vals = {"err",from,to,error,ts.toGMTString()};
		String log = generateCSV(vals) + "\n";
		Log.i(TAG, log);
		
		if (_tvLog != null)
		{
			_tvLog.append(log);
		}
		
		Utils.saveTextFile(logFilePath, log, true);

	}
	
	public void logDelivery (String from, String to, String deliveryStatus, Date ts)
	{
		String[] vals = {"del",from,to,deliveryStatus,ts.toGMTString()};
		String log = generateCSV(vals) + "\n";
		Log.i(TAG, log);
		
		if (_tvLog != null)
		{
			_tvLog.append(log);
		}
		
		Utils.saveTextFile(logFilePath, log, true);

	}
	
	private String generateCSV(String[] params)
	{
		StringBuffer csv = new StringBuffer();
		
		csv.append(lineNum + ",");
		
		for (int i = 0; i < params.length; i++)
		{
			csv.append('"');
			csv.append(params[i]);
			csv.append('"');

			if ((i+1)<params.length)
				csv.append(',');
			
		}
		
		lineNum++;
		
		return csv.toString();
	}
	
	
}
