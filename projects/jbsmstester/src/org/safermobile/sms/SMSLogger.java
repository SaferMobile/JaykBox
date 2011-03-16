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
	private File logFile = null;
	private BufferedWriter logWriter = null;
	private int lineNum = 1;
	
	private String _logTag = null;
	
	public SMSLogger (String logTag) throws IOException
	{
		_logTag = logTag;
		
		if (logWriter == null)
		{
			rotateLog();
		}
	}
	
	public void rotateLog () throws IOException
	{
		close();
		
		Date logDate = new Date();
		String filename = "/sdcard/jbsmstest" + "-" + _logTag + "-" + logDate.getYear() + logDate.getMonth() + logDate.getDate() + "-" + logDate.getHours() + logDate.getMinutes() + logDate.getSeconds() + ".csv";
		logFile = new File(filename);
		logWriter = new BufferedWriter (new FileWriter(logFile));
		lineNum = 1;
		logWriter.append("---------------------------------\n");
	}
	
	public void setLogView (TextView tvLog)
	{
		_tvLog = tvLog;
	}
	
	public void logSend (String from, String to, String smsMsg, Date sent)
	{
		String[] vals = {"sent",from,to,smsMsg,sent.getTime()+""};
		String log = generateCSV(vals);
		Log.i(TAG, log);
		
		if (_tvLog != null)
		{
			_tvLog.append(log);
			_tvLog.append("\n");

				
		}

		try {
			logWriter.write(log);
			logWriter.write("\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	public void logReceive (String from, String to, String smsMsg, Date rec)
	{
		String[] vals = {"rec",from,to,smsMsg,rec.getTime()+""};
		
		String log = generateCSV(vals);
		Log.i(TAG, log);
		
		if (_tvLog != null)
		{
			_tvLog.append(log);
			_tvLog.append("\n");
		}
		else
		{

	    	//
		}
		

		try {
			logWriter.write(log);
			logWriter.write("\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	public void logError (String from, String to, String error, Date ts)
	{
		String[] vals = {"err",from,to,error,ts.getTime()+""};
		Log.i(TAG, generateCSV(vals));
	}
	
	public void logDelivery (String from, String to, String deliveryStatus, Date ts)
	{
		String[] vals = {"del",from,to,deliveryStatus,ts.getTime()+""};
		Log.i(TAG, generateCSV(vals));
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
	
	public void close () throws IOException
	{
		if (logWriter != null)
		{
			logWriter.flush();
			logWriter.close();
		}
	}
}
