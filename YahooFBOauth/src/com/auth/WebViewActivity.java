package com.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
/**
 * This is an activity to show a WebView
 * Use this WebView for Authorized validation or surfing the MMG web site
 *
 */
public class WebViewActivity extends Activity {
	private String tag = "WebViewActivity";
	private WebView webView;
	private ProgressBar progressBar;
	private Intent intent;
	private Activity activity = WebViewActivity.this;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//webView = new WebView(this);
		Log.e(tag, "web on create");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.web_view);
		webView = (WebView) findViewById(R.id.webwiew);
		progressBar = (ProgressBar) findViewById(R.id.webview_progress);
		intent = getIntent();
		if(intent != null) {
			String url = intent.getStringExtra("url");
			Log.d(tag, url);
			initWebView(url);
		}
	}
	
	/**
	 * initialize the WebView
	 * @param url
	 */
	private void initWebView(String url) {
		// init websettings, support  JS and zoom
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setSupportZoom(true);
		webSettings.setBuiltInZoomControls(true);
		webSettings.setLoadWithOverviewMode(true);
		if(intent.getAction() != null) {
			Log.d(tag, "action not null");
			webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		} else {
			// set webview init scale level
			webView.setInitialScale(1);
		}
		Log.d(tag, "web oncreate");
		webView.requestFocus();
		webView.setHorizontalScrollBarEnabled(true);
		
		webView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				// TODO Auto-generated method stub
				// show the load progressbar
				progressBar.setProgress(newProgress);
				Log.e(tag, "progress-->" + newProgress);
				if(newProgress == 100) {
					progressBar.setVisibility(View.GONE);
				} else {
					progressBar.setVisibility(View.VISIBLE);
				}
			}

		});
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				Log.d(tag, "load url-->" + url);
				view.loadUrl(url);
				return true;
			}
			@Override
			public void onPageFinished(WebView view, String url) {
				// TODO Auto-generated method stub
				Log.d("LoginActivity", "finish url-->" + url);
				// if the page is yahoo oauth,finish this activity and set the data back to login activity
				if(url.contains("myapp://LoginActivity")) {
					Bundle bundle = new Bundle();
					bundle.putString("url", url);
					setResult(RESULT_OK, new Intent(activity, LoginActivity.class).putExtras(bundle));
					activity.finish();
				}
			}
		});
		webView.loadUrl(url);
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		// back to the previous page 
		if(webView.canGoBack() && keyCode == KeyEvent.KEYCODE_BACK) {
			webView.goBack();
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}
}