package org.ocsinventory.android.sections;

import java.util.ArrayList;

import org.ocsinventory.android.actions.OCSLog;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.input.InputManager;
import android.os.Build;
import android.view.InputDevice;


public class OCSInputs
{

	public ArrayList<OCSInput> inputs;
	  

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public OCSInputs(Context ctx) {
		OCSLog ocslog = OCSLog.getInstance();
		
		this.inputs= new ArrayList<OCSInput>();
		
		ocslog.append("OCSInputs");
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
			ocslog.append("OCSInputs BUILD Build.VERSION.SDK_INT ");
			Configuration config = ctx.getResources().getConfiguration();
			ocslog.append("config.keyboard"+ config.keyboard);
			ocslog.append("config.touchscreen"+ config.keyboard);
			 
			switch (config.keyboard) {
				case Configuration.KEYBOARD_QWERTY:
				case Configuration.KEYBOARD_12KEY:
					OCSInput ocsin = new   OCSInput ();
					ocsin.setType("keybord");
					break;
				case Configuration.KEYBOARD_NOKEYS:
				default:
					break;
			}

			OCSInput ocsin = new   OCSInput ();
			switch (config.touchscreen) {
				case Configuration.TOUCHSCREEN_STYLUS:
					ocsin.setType("STYLUS");
					break;
				case Configuration.TOUCHSCREEN_FINGER:
					ocsin.setType("OUCHSCREEN_FINGER");
					break;
				case  Configuration.TOUCHSCREEN_NOTOUCH:
				default:
					break;
			}
		}
			
		else {	
			
			InputManager inManager = (InputManager) ctx.getSystemService(Context.INPUT_SERVICE);
		
		
			int inDevices[] = inManager.getInputDeviceIds ();
			
			for ( int i=0; i< inDevices.length; i++) {
				InputDevice device = inManager.getInputDevice(inDevices[i]);
				ocslog.append("INPUT DESC : "+device.getDescriptor());
				ocslog.append(device.getName());
				
			}
		}
	}

	public String toXML() {
		StringBuffer strOut = new StringBuffer();
		for ( OCSInput o : inputs ) {
			strOut.append(o.toXml());
		}
		return strOut.toString();
	}
	public String toString() {
		StringBuffer strOut = new StringBuffer();
		for ( OCSInput o : inputs ) {
			strOut.append(o.toString());
		}
		return strOut.toString();
	}
	public ArrayList<OCSSection> getSections() {
		ArrayList<OCSSection> lst = new ArrayList<OCSSection>();
		for ( OCSInput o : inputs ) {
			lst.add(o.getSection());
		}
		return lst;
	}
}