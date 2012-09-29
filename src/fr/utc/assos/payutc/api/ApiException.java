package fr.utc.assos.payutc.api;

public class ApiException extends Exception {
	/**
	 * Je sais pas c'est quoi, eclipse l'a généré tout seul
	 */
	private static final long serialVersionUID = -7241957979809837995L;
	int errCode;
	String errMsg;
	public static final String DEFAULT_MSG	= "No msg. available.";
	
	ApiException(int err) {
		this(err, DEFAULT_MSG);
	}
	
	ApiException(int err, String err_msg) {
		errCode=err;
		if (err_msg==null) err_msg = DEFAULT_MSG;
		errMsg=err_msg;
	}
	
	@Override
	public String getMessage() {
		return "Err #" + errCode + " : " + errMsg;
	}
}