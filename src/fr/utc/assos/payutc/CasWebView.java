package fr.utc.assos.payutc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class CasWebView extends Activity {
	WebView webview;
	Pattern pattern = Pattern.compile("ticket=([^&]+)");
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.caswebview);
        CookieSyncManager.createInstance(this);  
        CookieManager cookieManager = CookieManager.getInstance();  
        cookieManager.removeSessionCookie();
        webview = (WebView) findViewById(R.id.webview);
        webview.setWebViewClient(new HelloWebViewClient());
        webview.getSettings().setJavaScriptEnabled(true);
        webview.loadUrl("https://cas.utc.fr/cas/login?service=https://cas.utc.fr/");
    }
    
    protected void returnTicket(String ticket) {
		Intent intent = new Intent();
		intent.putExtra("ticket", ticket);
        setResult(RESULT_OK, intent);
		finish();
    }
    
    private class HelloWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
        	Log.i("url", url);
        	Matcher matcher = pattern.matcher(url);
            if (matcher.find()) {
                returnTicket(matcher.group(1));
            }
            view.loadUrl(url);
            return true;
        }
    }
}
