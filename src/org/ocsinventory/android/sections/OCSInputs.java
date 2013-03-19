package org.ocsinventory.android.sections;

import java.util.ArrayList;

import org.ocsinventory.android.actions.OCSLog;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.Area;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;
import android.os.Build;


public class OCSInputs implements OCSSectionInterface
{
	final private String sectionTag = "INPUTS";
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
			
			OCSInput inkb = new   OCSInput ();
			inkb.setType("keybord");
			switch (config.keyboard) {
				case Configuration.KEYBOARD_QWERTY:
					inkb.setCaption("Keyboard querty");
					break;
				case Configuration.KEYBOARD_12KEY:
					inkb.setCaption("Keyboard 12 keys");
					break;
				case Configuration.KEYBOARD_NOKEYS:
					inkb.setCaption("No hardware keys");
				default:
					break;
			}
			inputs.add(inkb);
			
			OCSInput ocsin = new   OCSInput ();
			ocsin.setType("Touchscreeen");
			switch (config.touchscreen) {
				case Configuration.TOUCHSCREEN_STYLUS:
					ocsin.setCaption("Stylus touchscreen");
					break;
				case Configuration.TOUCHSCREEN_FINGER:
					ocsin.setCaption("Finger touchscreen");
					break;
				case  Configuration.TOUCHSCREEN_NOTOUCH:
					ocsin.setCaption("NO touchscreen");
					break;
				default:
					break;
			}
			inputs.add(ocsin);
		}

		// Infos sur les apareils photo
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
	    	OCSInput ocsci = new  OCSInput ();
	    	ocsci.setType("Camera");
	    	ocsci.setCaption("facing unknown");
	    	String sSz = getCameraMaxImgSize();
	    	ocsci.setDescription("Image size "+sSz);
	    	inputs.add(ocsci);
		} else {
		    int numberOfCameras = Camera.getNumberOfCameras();
		    CameraInfo cameraInfo = new CameraInfo();
		    String sSz = getCameraMaxImgSize();
		    
		    for (int i = 0; i < numberOfCameras; i++) {
		    	OCSInput ocsci = new  OCSInput ();
		    	ocsci.setType("Camera");
		        Camera.getCameraInfo(i, cameraInfo);
		        if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK)
		        	ocsci.setCaption("facing back");
		        else 
		        	ocsci.setCaption("facing front");
			    ocsci.setDescription("Image size "+sSz);
		    	inputs.add(ocsci);
		    }
	    }
	}
	private String getCameraMaxImgSize() {
		
	    Camera cam;
	    try {
	    	cam = Camera.open();
	    } catch ( RuntimeException e ) {
	    	return "busy";
	    }
	    Camera.Parameters params = cam.getParameters();
	    long max_v=0;
	    Size max_sz=null;
	    for ( Size sz : params.getSupportedPictureSizes() ) {
		    long v = sz.height * sz.width;
		    android.util.Log.d("OCSINPUT", String.valueOf(v) );
		    if ( v > max_v ){
		    	max_v =v;
		    	max_sz=sz;
		    }
	    }
	    cam.release();
	    return String.valueOf(max_sz.width)+"x"+String.valueOf(max_sz.height);
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
	public String  getSectionTag() {
		return sectionTag;
	}

}