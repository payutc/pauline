package fr.utc.assos.payutc.api;

public class ApiException extends Exception {
	/**
	 * Je sais pas c'est quoi, eclipse l'a généré tout seul
	 */
	private static final long serialVersionUID = -7241957979809837995L;
	String errType;
	String errCode;
	String errMsg;
	public static final String DEFAULT_MSG	= "No msg. available.";
	
	
	ApiException(String err_type, String err_code, String err_msg) {
		errType = err_type;
		if (errType == null || errType == "") {
			errType = "UntypedError";
		}
		errCode=err_code;
		if (err_msg==null) err_msg = DEFAULT_MSG;
		errMsg=err_msg;
	}
	
	@Override
	public String getMessage() {
		return "Err " + errType + "(" + errCode + ")" + " : " + errMsg;
	}
}
