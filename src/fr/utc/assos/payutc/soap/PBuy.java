package fr.utc.assos.payutc.soap;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.ksoap2.HeaderProperty;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpsTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;


public class PBuy {
	private final String TAG = "PBuy";
	
	private String host;
	private String namespace;
	HashMap<String, String> cookies = new HashMap<String, String>();
	
	public PBuy() {
		this("assos.utc.fr", "https://assos.utc.fr:443/buckutt/PBUY.class.php");
	}
	
	public PBuy(String _host, String _namespace) {
		host = _host;
		namespace = _namespace;
	}
	
    public int loadSeller(String data, int meanOfLogin, String ip, int poi_id) {
		// Création de la requête SOAP
		SoapObject request = new SoapObject (namespace, "loadSeller");
		request.addProperty("data", data);
		request.addProperty("meanOfLogin", meanOfLogin);
		request.addProperty("ip", ip);
		request.addProperty("poi_id", poi_id);
		try {
			SoapSerializationEnvelope soapObject = soap(request);
			Log.d("loadSeller", soapObject.getResponse().toString());
			int r_code = Integer.parseInt(soapObject.getResponse().toString());
			return r_code;
		} catch (Exception e) {
			Log.e("loadSeller", "", e);
			return -1;
	    }
    }
    
    public IdentityResult getSellerIdentity() {
		// Création de la requête SOAP
    	SoapObject request = new SoapObject (namespace, "getSellerIdentity");
		try {
			SoapSerializationEnvelope soapObject = soap(request);
			Log.d("getSellerIdentity", soapObject.getResponse().toString());
			return new IdentityResult(soapObject.getResponse().toString());
		} catch (Exception e) {
			Log.e("getSellerIdentity", "", e);
			return null;
	    }
    }
	
	public int loadBuyer(String data, int meanOfLogin, String ip) {
		// Création de la requête SOAP
		SoapObject request = new SoapObject (namespace, "loadBuyer");
		request.addProperty("data", data);
		request.addProperty("meanOfLogin", meanOfLogin);
		request.addProperty("ip", ip);
		try {
			SoapSerializationEnvelope soapObject = soap(request);
			Log.d("loadBuyer", soapObject.getResponse().toString());
			int r_code = Integer.parseInt(soapObject.getResponse().toString());
			return r_code;
		} catch (Exception e) {
			Log.e("loadBuyer", "", e);
			return -1;
	    }
	}
	
	public IdentityResult getBuyerIdentity() {
		// Création de la requête SOAP
		SoapObject request = new SoapObject (namespace, "getBuyerIdentity");
		try {
			SoapSerializationEnvelope soapObject = soap(request);
			Log.d("getBuyerIdentity", soapObject.getResponse().toString());
			return new IdentityResult(soapObject.getResponse().toString());
		} catch (Exception e) {
			Log.e("getBuyerIdentity", "", e);
			return null;
	    }
	}
	
	public GetPropositionResult getProposition() {
		// Création de la requête SOAP
		SoapObject request = new SoapObject (namespace, "getProposition");
		try {
			SoapSerializationEnvelope soapObject = soap(request);
			Log.d("getBuyerIdentity", soapObject.getResponse().toString());
			return new GetPropositionResult(soapObject.getResponse().toString());
		} catch (Exception e) {
			Log.e("getBuyerIdentity", "", e);
			return null;
	    }
	}
	
	public int select(int id, int cost, String trace) {
		// Création de la requête SOAP
		SoapObject request = new SoapObject (namespace, "select");
		request.addProperty("obj_id", id);
		request.addProperty("obj_credit", cost);
		request.addProperty("trace", trace);
		try {
			SoapSerializationEnvelope soapObject = soap(request);
			Log.d("select", soapObject.getResponse().toString());
			int r_code = Integer.parseInt(soapObject.getResponse().toString());
			return r_code;
		} catch (Exception e) {
			Log.e("select", "", e);
			return -1;
	    }
	}
	
	public int transaction(ArrayList<Integer> ids, String trace) {
		String s_ids = "";
		for (int id : ids) {
			s_ids += id;
			s_ids += ",";
		}
		if (s_ids.length()>0) {
			s_ids = s_ids.substring(0, s_ids.length()-1);
		}
		Log.d("coucou", s_ids);
		SoapObject request = new SoapObject (namespace, "transaction");
		request.addProperty("obj_ids", s_ids);
		request.addProperty("trace", trace);
		try {
			SoapSerializationEnvelope soapObject = soap(request);
			Log.d("transaction", soapObject.getResponse().toString());
			int r_code = Integer.parseInt(soapObject.getResponse().toString());
			return r_code;
		} catch (Exception e) {
			Log.e("transaction", "", e);
			return -1;
	    }
	}
	
	public int endTransaction() {
		// Création de la requête SOAP
		SoapObject request = new SoapObject (namespace, "endTransaction");
		try {
			SoapSerializationEnvelope soapObject = soap(request);
			Log.d("endTransaction", soapObject.getResponse().toString());
			int r_code = Integer.parseInt(soapObject.getResponse().toString());
			return r_code;
		} catch (Exception e) {
			Log.e("endTransaction", "", e);
			return -1;
	    }
	}
	
	public GetImageResult getImage(int id) {
		// Création de la requête SOAP
		SoapObject request = new SoapObject (namespace, "getImage");
		request.addProperty("img_id", id);
		try {
			SoapSerializationEnvelope soapObject = soap(request);
			Log.d("getImage", soapObject.getResponse().toString());
			return new GetImageResult(soapObject.getResponse().toString());
		} catch (Exception e) {
			Log.e("getImage", "", e);
			return null;
	    }
	}
    
	private SoapSerializationEnvelope soap (SoapObject request) 
			throws IOException, XmlPullParserException
	{
		Log.d(TAG, "soap");
		String soap_action = namespace + "#" + request.getName();
		Log.d(TAG, "action : "+soap_action);
		
		//Toutes les données demandées sont mises dans une enveloppe.
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope (
				SoapEnvelope.VER11);
		envelope.setOutputSoapObject (request);
		
		Log.d(TAG, "fin envelope");

		HttpsTransportSE androidHttpTransport = new HttpsTransportSE (host, 443, "/buckutt/PBUY.class.php", 10000);
		//Ceci est optionnel, on l'utilise pour savoir si nous voulons ou non utiliser 
		//un paquet "sniffer" pour vérifier le message original (androidHttpTransport.requestDump)
		androidHttpTransport.debug = true; 

		//Envoi de la requête
		List respHeaders = androidHttpTransport.call(soap_action, envelope, get_headers());

		Log.d(TAG, "fin call");
		
		update_cookies(respHeaders);

		Log.d(TAG, "fin cookie");
		
		
		return envelope;
	}
	
	synchronized List<HeaderProperty> get_headers() {
		List<HeaderProperty> headers = new ArrayList<HeaderProperty>();
        for (String cookie:cookies.keySet()) {
        	headers.add(new HeaderProperty("Cookie", cookie + "=" + cookies.get(cookie)));
        }
        return headers;
	}
	
	synchronized void update_cookies(List respHeaders) {
		if (respHeaders != null) {
            for (int i = 0; i < respHeaders.size(); ++i) {
                HeaderProperty hp = (HeaderProperty)respHeaders.get(i);
                String key = hp.getKey();
                String value = hp.getValue();
                if (key!=null && value!=null) {
                    if (key.equalsIgnoreCase("set-cookie")){
            			Log.d("soap", hp.getKey() + " -> " + hp.getValue());
                    	String[] cookieSplit = value.replace(" ", "").split(";");
                    	for (String cookieString : cookieSplit) {
                    		String cookieKey = cookieString.substring(0, cookieString.indexOf("="));
                    		String cookieValue = cookieString.substring(cookieString.indexOf("=")+1);
                    		cookies.put(cookieKey, cookieValue);
                    		Log.d("soap", cookieKey + " = " + cookieValue);
                    	}
                    }
                }
            }
        }
	}
}
