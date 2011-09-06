package com.auth;


import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import oauth.signpost.OAuth;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import oauth.signpost.signature.HmacSha1MessageSigner;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;

public class LoginActivity extends Activity {
	public String tag = "LoginActivity";
	private static final String YAHOO_API_KEY = "UVeWrh5a";
	private static final String YAHOO_CALLBACK_URL = "myapp://LoginActivity";
	private static final String YAHOO_CONSUMER_KEY = "dj0yJmk9ZE5pRTJTaU9TNTd1JmQ9WVdrOVZWWmxWM0pvTldFbWNHbzlNakEzT1RNME9UTTJNZy0tJnM9Y29uc3VtZXJzZWNyZXQmeD1mYQ--";
	private static final String YAHOO_CONSUMER_SECRET = "797621dbd8eefaea4c93cfea3b6b4c17609129bb";
	private static final String REQUEST_TOKEN_ENDPOINT_URL = "https://api.login.yahoo.com/oauth/v2/get_request_token";
	private static final String ACCESS_TOKEN_ENDPOINT_URL = "https://api.login.yahoo.com/oauth/v2/get_token";
	private static final String AUTHORIZE_WEBSITE_URL = "https://api.login.yahoo.com/oauth/v2/request_auth";
	private static final int YAHOO_WEB_REQUEST = 0;  // a request code of start webview activity for result

	private Button btnFacebook;
	private Button btnYahoo;
	private Button btnWebView;

	Facebook facebook = new Facebook("202111083183632");
	CommonsHttpOAuthConsumer mConsumer;
	CommonsHttpOAuthProvider mProvider;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Log.e(tag, "on create");
		btnFacebook = (Button) findViewById(R.id.facebook);
		btnYahoo = (Button) findViewById(R.id.yahoo);
		btnFacebook.setOnClickListener(new FBButtonClickListener());
		btnYahoo.setOnClickListener(new YahooButtonClickListener());	
		btnWebView = (Button) findViewById(R.id.web);
		btnWebView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(LoginActivity.this, WebViewActivity.class).putExtra("url", "http://matchmove.com/"));
			}
		});
	}
	/**
	 * start to the OAuth of Yahoo!
	 */
	private void callYahooOAuth() {
		// init CommonsHttpOAuthConsumer and CommonsHttpOAuthProvider
		mConsumer = new CommonsHttpOAuthConsumer(YAHOO_CONSUMER_KEY, YAHOO_CONSUMER_SECRET);
		Log.d(tag, "consumer");
		mConsumer.setMessageSigner(new HmacSha1MessageSigner());
		HttpClient client = new DefaultHttpClient();
		mProvider = new CommonsHttpOAuthProvider(REQUEST_TOKEN_ENDPOINT_URL,
				ACCESS_TOKEN_ENDPOINT_URL, AUTHORIZE_WEBSITE_URL, client);
		Log.d(tag, "provider");
		Log.d(tag, mConsumer.toString());
		try {
			// got the authUrl
			String authUrl = mProvider.retrieveRequestToken(mConsumer, YAHOO_CALLBACK_URL);
			Intent intent = new Intent(this, WebViewActivity.class);
			intent.putExtra("url", authUrl);
			intent.setAction(Intent.ACTION_DEFAULT);
			startActivityForResult(intent, YAHOO_WEB_REQUEST);
			Log.e(tag, "get user id");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	class YahooButtonClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Log.d(tag, "start yahoo login");
			callYahooOAuth();
		}
	}

	class FBButtonClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			facebook.authorize(LoginActivity.this, new String[] {},
					new DialogListener() {
				@Override
				public void onComplete(Bundle values) {
					// TODO Auto-generated method stub
					System.out.println("onComplete");
					getFacebookUserId(values);
				}

				@Override
				public void onFacebookError(FacebookError e) {
					// TODO Auto-generated method stub
					System.out.println("onFacebookError");
				}

				@Override
				public void onError(DialogError e) {
					// TODO Auto-generated method stub
					System.out.println("onError");
				}

				@Override
				public void onCancel() {
					// TODO Auto-generated method stub
					System.out.println("onCancel");
				}

			});
		}

	}
	
	@Override
    public void onAttachedToWindow() {
    	// TODO Auto-generated method stub
    	
        this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
    	super.onAttachedToWindow();
    }
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	// TODO Auto-generated method stub
    	if(keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME || keyCode == KeyEvent.KEYCODE_CALL) {
    		Log.e(tag, "home");
    		return true;
    	}
    	return super.onKeyDown(keyCode, event);
    }
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
    	// TODO Auto-generated method stub
    	if(keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME || keyCode == KeyEvent.KEYCODE_CALL) {
    		Log.e(tag, "home");
    		return true;
    	}
    	return super.onKeyUp(keyCode, event);
    }
	
	/**
	 * get the user id of Facebook
	 * @param bundle call back by facebook dialoglistener
	 * @return the user id of Facebook
	 */
	private String getFacebookUserId(Bundle bundle) {
		Log.d(tag, "size--> " + bundle.size());
		Log.d(tag, bundle.toString());
		Log.d(tag, facebook.getAccessToken());
		String accessToken = facebook.getAccessToken();
		String temp = accessToken.split("-")[1];
		String userId = temp.substring(0, temp.indexOf("|"));
		Log.e(tag, "facebook user id-->" + userId);
		return userId;
	}
	
	/**
	 * get the user id of Yahoo!
	 * @param data call back by yahoo! oauth
	 * @return the user id of yahoo!
	 */
	private String getYahooUserId(Intent data) {
		Uri uri = Uri.parse(data.getExtras().getString("url"));
		String verifier = uri.getQueryParameter(OAuth.OAUTH_VERIFIER);
		Log.d(tag, "verifier-->" + verifier);
		String userId = null;
		try {
			String token = mConsumer.getToken();
			String tokenSecret = mConsumer.getTokenSecret();
			Log.d(tag, "token-->" + token + "    secret-->" + tokenSecret);
			mProvider.setOAuth10a(true);
			mProvider.retrieveAccessToken(mConsumer, verifier);
			userId = mProvider.getResponseParameters().get("xoauth_yahoo_guid").first();
			Log.e(tag, "user id-->" + userId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userId;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		Log.d(tag, "result code-->" + resultCode);
		Log.d(tag, "request code-->" + requestCode);
		
		if(resultCode == RESULT_OK) {
			// get the user id by the uri which was validated by Yahoo!
			if(requestCode == YAHOO_WEB_REQUEST) {
				getYahooUserId(data);
			}
		}
	}
}