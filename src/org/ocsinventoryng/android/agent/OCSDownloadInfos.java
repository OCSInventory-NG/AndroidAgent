package org.ocsinventoryng.android.agent;

public class OCSDownloadInfos {
	public String getId() {
		return id;
	}
	public int getPri() {
		return pri;
	}
	/**
	 * 
	 * @return action requested
	 */
	public String getAct() {
		return act;
	}
	/**
	 * 
	 * @return digest value as string
	 */

	public String getDigest() {
		return digest;
	}
	/**
	 * 
	 * @return protocol to use (HTTP/HTTPS)
	 */
	public String getProto() {
		return proto;
	}
	/**
	 * 
	 * @return number of fragments
	 */
	public int getFrags() {
		return frags;
	}
	public String getDigest_algo() {
		return digest_algo;
	}
	public String getDigest_encode() {
		return digest_encode;
	}
	public String getPath() {
		return path;
	}
	public boolean isNotify_user() {
		return notify_user;
	}
	public String getNotify_text() {
		return notify_text;
	}
	public String getNotify_countdown() {
		return notify_countdown;
	}
	public boolean isNotify_can_abort() {
		return notify_can_abort;
	}
	public boolean isNotify_can_delay() {
		return notify_can_delay;
	}
	public boolean isNeed_done_action() {
		return need_done_action;
	}
	public String getNeed_done_action_text() {
		return need_done_action_text;
	}
	public String getGardefou() {
		return gardefou;
	}
	private String 	id;
	private int 	pri;
	private String	act;
	private String 	digest;
	private String 	proto;
	private int		frags;
	private String 	digest_algo;
	private String 	digest_encode;
	private String 	path;
	private String 	notify_text;
	private String 	notify_countdown;
	private boolean notify_user;
	private boolean notify_can_abort;
	private boolean notify_can_delay;
	private boolean need_done_action;
	private String 	need_done_action_text;
	private String 	gardefou;
	

	OCSDownloadInfos(String strinfos) {
		id=extrAttr(strinfos,"ID");
		act=extrAttr(strinfos,"ACT");
		digest=extrAttr(strinfos,"DIGEST");
		proto=extrAttr(strinfos,"PROTO");
		digest_algo=extrAttr(strinfos,"DIGEST_ALGO");
		digest_encode=extrAttr(strinfos,"DIGEST_ENCODE");
		path=extrAttr(strinfos,"PATH");
		notify_countdown=extrAttr(strinfos,"NOTIFY_COUNTDOWN");
		notify_can_abort=extrAttr(strinfos,"NOTIFY_CAN_ABORT").equals("1");
		notify_can_delay=extrAttr(strinfos,"NOTIFY_CAN_DELAY").equals("1");
		need_done_action=extrAttr(strinfos,"NEED_DONE_ACTION").equals("1");
		need_done_action_text=extrAttr(strinfos,"NEED_DONE_ACTION_TEXT");
		gardefou=extrAttr(strinfos,"GARDEFOU");
		try {
			pri = Integer.parseInt(extrAttr(strinfos,"PRI"));
		} catch ( NumberFormatException e ) {};
		try {
			frags = Integer.parseInt(extrAttr(strinfos,"FRAGS"));
		} catch ( NumberFormatException e ) {};
		
	}
	
	private String extrAttr(String doc, String attrName) {
		int x,y;
		x=doc.indexOf(attrName);
		x=doc.indexOf("\"",x);
		y=doc.indexOf("\"",x+1);
		android.util.Log.i("extrattr",attrName+":"+doc.substring(x+1, y));
		return doc.substring(x+1, y);
	}
}
