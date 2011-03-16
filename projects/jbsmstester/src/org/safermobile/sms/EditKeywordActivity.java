package org.safermobile.sms;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class EditKeywordActivity extends Activity {

	private EditText txtEditor;
	
	public final static String BASE_PATH = "/sdcard/jbsms";

	public final static String KEYWORD_FILE = BASE_PATH + "/keywords.txt";
	
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.editor);
	        
	        txtEditor = (EditText)findViewById(R.id.editor);
	
	        txtEditor.setText(Utils.loadTextFile(KEYWORD_FILE));
	        
	        txtEditor.addTextChangedListener(new TextWatcher() { 
	        	
                public void  afterTextChanged (Editable s){ 
                
                	String newText = s.toString();
                	
                	Utils.saveTextFile(KEYWORD_FILE, newText, false);
                } 
	            
                public void  beforeTextChanged  (CharSequence s, int start, int count, int after){ 
	            	
	            }
            
	            public void  onTextChanged  (CharSequence s, int start, int before, int count) { 
	            	
	            } 

	        });
	     
	        
	    }
	
	/*
     public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);
         
         MenuItem mItem = null;
         
         mItem = menu.add(0, 1, Menu.NONE, "Add");
         mItem = menu.add(0, 2, Menu.NONE, "Delete");
         mItem = menu.add(0, 3, Menu.NONE, "Import");
         
        
         return true;
     }
     
 	public boolean onMenuItemSelected(int featureId, MenuItem item) {
 		
 		super.onMenuItemSelected(featureId, item);
 		
 		if (item.getItemId() == 1)
 		{
 		
 		}
 		else if (item.getItemId() == 2)
 		{
 		
 		}
 		else if (item.getItemId() == 3)
 		{
 		
 		}
         return true;
 	}
 	*/
     
}
