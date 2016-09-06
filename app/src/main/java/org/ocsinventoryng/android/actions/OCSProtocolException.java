package org.ocsinventoryng.android.actions;


public class OCSProtocolException extends Exception {

    /**
     *
     */
    private OCSLog ocslog = OCSLog.getInstance();
    private static final long serialVersionUID = 8115364599391499226L;

    public OCSProtocolException() {
        super();
    }

    public OCSProtocolException(String s) {
        super(s);
        ocslog.error(s);
    }
}
