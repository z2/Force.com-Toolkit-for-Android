package com.sforce.android.sample;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.sforce.android.soap.partner.BaseResponseListener;
import com.sforce.android.soap.partner.Salesforce;
import com.sforce.android.soap.partner.SaveResult;
import com.sforce.android.soap.partner.fault.ApiFault;
import com.sforce.android.soap.partner.sobject.SObject;

public class SforceUpdateAccount extends Activity implements OnClickListener{
	TextView sObjectType;
	TextView type;
	TextView name;
	TextView numberOfEmployees;
	TextView fax;
    TextView mText;
    
	Button updateButton;
	Button doneButton;
	Context context;
	OnClickListener ocl;
	String id=null;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.update_account_layout);
		setTitle("Sforce Toolkit Demo - Update Account");
		context=getApplicationContext();
		Intent intent=getIntent();
		sObjectType = (TextView)this.findViewById(R.id.sObjectType);
		type= (TextView)this.findViewById(R.id.type);
		type.setText("Account");
		name = (TextView)this.findViewById(R.id.name);
		
		Bundle bundle=intent.getExtras();
		id=bundle.getString("Id");
		name.setText(bundle.getString("Name"));
		numberOfEmployees = (TextView)this.findViewById(R.id.numberOfEmployees);
		numberOfEmployees.setText(bundle.getString("NumberOfEmployees"));
		fax = (TextView)this.findViewById(R.id.fax);
		fax.setText(bundle.getString("Fax"));
		updateButton = (Button)this.findViewById(R.id.updateButton);
		doneButton = (Button)this.findViewById(R.id.doneButton);
        mText = (TextView) this.findViewById(R.id.txt);
		updateButton.setOnClickListener(this);
		ocl=new DoneButtonClickListener();
		doneButton.setOnClickListener(ocl);
		Salesforce.init(context);
	}

	public void onClick(View v) {
		SObject obj = new SObject();
		obj.setType(type.getText().toString().trim());
        HashMap<String, String> requestFields=new HashMap<String, String>();
        requestFields.put("name", name.getText().toString());
        requestFields.put("numberOfEmployees", numberOfEmployees.getText().toString());
        requestFields.put("fax", fax.getText().toString());
        requestFields.put("Id", id);

        obj.setFields(requestFields);
        ArrayList<String> fields2Null = new ArrayList<String>();
        fields2Null.add("Ownership");
        obj.setFieldsToNull(fields2Null);
        
        ArrayList<SObject> objs = new ArrayList<SObject>();
        objs.add(obj);

        Salesforce.update(objs, new UpdateResponseListener());
	}

	public class DoneButtonClickListener implements OnClickListener{
		public void onClick(View v) {
			setResult(RESULT_OK);
	        finish();
		}
	}
    public class UpdateResponseListener extends BaseResponseListener{

	    @Override
        public void onComplete(final Object uresults) {
	    	ArrayList<SaveResult> resultArray=(ArrayList<SaveResult>) uresults;
			StringBuffer collateResults=new StringBuffer();
			for (SaveResult ur: resultArray){
				if (ur.isSuccess()){
					collateResults=collateResults.append("Record ").append(ur.getId()).append(" updated successfully.\n");
				}else{
					collateResults=collateResults.append("Record ").append(ur.getId()).append(" updation failed.\n");
					if (ur.getErrors()!=null){
						collateResults.append("Error Message: ").append(ur.getErrors().get(0).getMessage()).append("\n");
						collateResults.append("Status Code: ").append(ur.getErrors().get(0).getStatusCode().getValue()).append("\n");
					}
				}
			}
	    	final String display=collateResults.toString();
	    	SforceUpdateAccount.this.runOnUiThread(new Runnable() {
	            public void run() {
	                mText.setText(display);
	            }
	        });
        }        
        
      @Override
      public void onSforceError(ApiFault apiFault){
      	StringBuffer sb=new StringBuffer();
      	sb.append("Exception message:").append(apiFault.getExceptionMessage()).append("\n").append("Exception Code:").append(apiFault.getExceptionCode().toString());
      	final String display=sb.toString();
      	SforceUpdateAccount.this.runOnUiThread(new Runnable() {
            public void run() {
                mText.setText(display);
            }
        });
      }

  }
}