package fr.utc.assos.payutc.soap;

public class GetImageResult extends SoapResult {
	
	private int mId;
	private String mMimetype;
	private int mWidth, mHeight;
	private String mEncoded;
	
	public GetImageResult(String wsdl_response) {
		super(wsdl_response);
	}
	
	@Override
	void processValues(String[][] values) {
		mId = Integer.parseInt(values[0][0]);
		mMimetype = values[0][1];
		mWidth = Integer.parseInt(values[0][2]);
		mHeight = Integer.parseInt(values[0][3]);
		mEncoded = values[0][4];
	}
	
	public String getEncoded() {
		return mEncoded;
	}

}
