package mo.pay.post_it.clipWeb;

import mo.pay.post_it.R;
import mo.pay.post_it.store.AppPreference;
import mo.pay.post_it.widget.MomoToast;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class ClipWebContent extends Activity implements IClipWebContentListener,OnEditorActionListener 
{
	public static final String TAG = "ClipWebContent" ;
	
	public static final String BUNDLE_KEY_WEB_CLIP_PATH = "webClipPath";
	 
	private EditText		_urlEditText 	= null;
	private RelativeLayout 	_urlArea 		= null;
	private WebView			_webView		= null;
	private ClipView		_clipView		= null;
	private ImageView 		btnClipByHand	= null;
	private ImageView 		btnClipRect 	= null;
	
	private ProgressBar 	progressBar;   
	
	private RelativeLayout _toolbarArea  	   = null;
	private RelativeLayout _toolAreaBrowseMode = null;
	private RelativeLayout _toolAreaClipMode   = null;
	
	private Bitmap _webviewSnap = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_clip_web);
		
		_urlEditText = (EditText) findViewById(R.id.clip_web_edittext_url);
		_urlArea = 	(RelativeLayout) findViewById(R.id.clip_web_url_area);
		_webView =  (WebView) findViewById(R.id.clip_web_webview);
		_clipView = (ClipView) findViewById(R.id.clip_web_clipview);
		_toolbarArea = (RelativeLayout) findViewById(R.id.clip_web_tool_area);
		_toolAreaBrowseMode = (RelativeLayout) findViewById(R.id.clip_web_browse_mode_tool_area);
		_toolAreaClipMode= (RelativeLayout) findViewById(R.id.clip_web_clip_mode_tool_area);
		progressBar = (ProgressBar) findViewById(R.id.clip_web_progressbar);
		 
		_urlEditText.setOnEditorActionListener(this);  
		_urlEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
		    @Override
		    public void onFocusChange(View v, boolean hasFocus) {
		    	_urlEditText.post(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(_urlEditText, InputMethodManager.SHOW_IMPLICIT);
                    }
                });
		    } 
		});
		
		ButtonClickListener listener = new ButtonClickListener();
		
		_clipView.setWebClipListener(this);
		
		//Tools in Browse mode
		View btnURLGo = findViewById(R.id.clip_web_btn_go);
		View btnStartClip = findViewById(R.id.clip_web_btn_start_clip);
		View btnInputURL = findViewById(R.id.clip_web_btn_input_url);
		View btnBookmark = findViewById(R.id.clip_web_btn_bookmark);
		View btnPageNext = findViewById(R.id.clip_web_btn_pagenext);
		View btnPageBack = findViewById(R.id.clip_web_btn_pageback);
		
		btnURLGo.setOnClickListener(listener);
		btnStartClip.setOnClickListener(listener);
		btnInputURL.setOnClickListener(listener);
		btnBookmark.setOnClickListener(listener);
		btnPageNext.setOnClickListener(listener);
		btnPageBack.setOnClickListener(listener);
		
		
		//Tools in Clip mode
		View btnExitClipMode = findViewById(R.id.clip_web_btn_exit_clip_mode);
		btnClipByHand = (ImageView) findViewById(R.id.clip_web_btn_byhand);
		btnClipRect   = (ImageView) findViewById(R.id.clip_web_btn_cliprect);
		
		btnExitClipMode.setOnClickListener(listener);
		btnClipByHand.setOnClickListener(listener);
		btnClipRect.setOnClickListener(listener);
		
		initWebView();
	}
	
	@Override
	protected void onDestroy()
	{
		AppPreference.instance().setLastBrowseURL(_webView.getUrl());
		
		super.onDestroy();
		 
		PopupBookmarks.destroyBookmarkPopup();
		
		if(_webviewSnap != null)
			_webviewSnap.recycle();
		
		_clipView.reset();
		_clipView.destroy();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		if (keyCode == KeyEvent.KEYCODE_BACK) 
		{
			if(_toolAreaClipMode.getVisibility()==View.VISIBLE)
			{
				toBrowseMode();
				return true;
			}
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
	private void doAnimationFadeIn(View view)
	{
		view.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
	}
	
	private void doAnimationFadeOut(View view)
	{
		view.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
	}
	
	private void initWebView()
	{      
	     WebSettings websettings = _webView.getSettings();  
	     websettings.setSupportZoom(true);  
	     websettings.setBuiltInZoomControls(true);   
	     websettings.setJavaScriptEnabled(true);  
	     websettings.setUseWideViewPort(true);

	     _webView.setWebViewClient(new WebViewClient()); 
	     progressBar.setVisibility(View.VISIBLE);
	     _webView.loadUrl(AppPreference.instance().getLastBrowseURL());  
	     _webView.setWebViewClient(new WebViewClient(){
	    	 
	    	 public boolean shouldOverrideUrlLoading(WebView view, String url) {  
	    		  progressBar.setVisibility(View.VISIBLE);
	              view.loadUrl(url);  
	              return true;  
	         }  
	   
	         public void onPageFinished(WebView view, String url) {  
	        	 progressBar.setVisibility(View.GONE); 
	         }  
	   
	         public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {  
	        	 progressBar.setVisibility(View.GONE); 
	             new MomoToast(ClipWebContent.this,"Error:"+description).show();  
	         }  
	     });
	     _webView.setOnTouchListener(new OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				if(_urlArea.getVisibility() == View.VISIBLE)
				{
					InputMethodManager imm = (InputMethodManager)getSystemService(
						      Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(_urlEditText.getWindowToken(), 0);
					doAnimationFadeOut(_urlArea);
					_urlArea.setVisibility(View.GONE);
				}
					
				return false;
			}
	     });
	     
	}
	
	private void toClipMode()
	{
		_toolAreaBrowseMode.setVisibility(View.GONE);
		_toolAreaClipMode.setVisibility(View.VISIBLE);
		
		btnClipByHand.setImageResource(R.drawable.byhand_f);
		btnClipRect.setImageResource(R.drawable.rect);
		
		refreshWebviewSnapshot();
		
		if(_webviewSnap != null) 
		{
			_clipView.reset();
			_clipView.setWebviewSnapshot(_webviewSnap);
			_clipView.setToolBarHeight(toolbarHeight);
			_clipView.setClipShape(ClipView.CLIP_SHAPE_FREE);
			_clipView.setVisibility(_clipView.getVisibility()==View.VISIBLE?View.GONE:View.VISIBLE);
		}
		else
		{
			new MomoToast(this,"Failed to capture screen!").show();
		}
		
		doAnimationFadeOut(_toolAreaBrowseMode);
		doAnimationFadeIn(_toolAreaClipMode);
		doAnimationFadeIn(_clipView);
	}
	
	private int toolbarHeight = 0;
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		Rect rectgle= new Rect();
		Window window= getWindow();
		window.getDecorView().getWindowVisibleDisplayFrame(rectgle);
		int statusBarHeight= rectgle.top;
		
		toolbarHeight = _toolbarArea.getHeight()+statusBarHeight;
	}
	
	private Bitmap refreshWebviewSnapshot()
	{
		try
		{
			if(_webviewSnap != null)
				_webviewSnap.recycle();
			
			_webviewSnap = null;
			
	    	View v = _webView.getRootView();
	    	_webviewSnap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
	    	Canvas allcanvas = new Canvas(_webviewSnap);
	    	v.draw(allcanvas);
	    	_webviewSnap = Bitmap.createBitmap
	    		( 
	    			_webviewSnap,
	    			0,toolbarHeight,
	    			v.getWidth(),
	    			v.getHeight()-toolbarHeight
	    		);
//	    	_webviewSnap.compress(CompressFormat.PNG, 100, new FileOutputStream("/sdcard/all.png"));
	    }
		catch(OutOfMemoryError oom){Log.e(TAG, "OOM Exception");}
		catch(Exception e){Log.e(TAG, "recycle bitmap:"+e.toString());}
		
		return _webviewSnap;
	}
	
	private void toBrowseMode()
	{
		_toolAreaBrowseMode.setVisibility(View.VISIBLE);
		_toolAreaClipMode.setVisibility(View.GONE);
		
		_clipView.reset();
		_clipView.setVisibility(View.GONE);
		
		doAnimationFadeOut(_toolAreaClipMode);
		doAnimationFadeIn(_toolAreaBrowseMode);
	}
	
	class ButtonClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			switch(v.getId())
			{
				//Browse mode
				case R.id.clip_web_btn_pagenext:
					if(_webView.canGoForward())
						_webView.goForward();
					break;
					
				case R.id.clip_web_btn_pageback:
					if(_webView.canGoBack())
						_webView.goBack();
					break;
					
				case R.id.clip_web_btn_bookmark:
					PopupBookmarks
						.getBookmarkPopup(ClipWebContent.this, ClipWebContent.this,v)
							.show();
					break;
			
				case R.id.clip_web_btn_input_url:
					_urlArea.setVisibility(_urlArea.getVisibility()==View.VISIBLE?View.GONE:View.VISIBLE);
					
					if(_urlArea.getVisibility() == View.VISIBLE)
					{
						_urlEditText.setText(_webView.getUrl());
						_urlEditText.selectAll();
						_urlEditText.requestFocus();
					}
					else
					{
						InputMethodManager imm = (InputMethodManager)getSystemService(
							      Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(_urlEditText.getWindowToken(), 0);
					}
					
					break;
			
				case R.id.clip_web_btn_start_clip:
					toClipMode();
					break;
			
				case R.id.clip_web_btn_go:
					goToUrl();
					break;
					
					
				//Clip Mode
				case R.id.clip_web_btn_exit_clip_mode:
					toBrowseMode();
					break;
					
				case R.id.clip_web_btn_byhand:
					btnClipByHand.setImageResource(R.drawable.byhand_f);
					btnClipRect.setImageResource(R.drawable.rect);
					_clipView.setClipShape(ClipView.CLIP_SHAPE_FREE);
					break;
					
				case R.id.clip_web_btn_cliprect:
					btnClipByHand.setImageResource(R.drawable.byhand);
					btnClipRect.setImageResource(R.drawable.rect_f);
					_clipView.setClipShape(ClipView.CLIP_SHAPE_RECT);
					break;
				
			}
		}
	}



	@Override
	public void onBookmarkSelected(String url)
	{
		if(url!=null && !url.equals(""))
		{
			progressBar.setVisibility(View.VISIBLE);
			
			_webView.loadUrl(url);
		}
	}

	private DialogConfirmClip _dialogUseClip;
	
	@Override
	public void onContentCliped(Bitmap clipBitmap)
	{
		_dialogUseClip = new DialogConfirmClip(this);
		_dialogUseClip.setClipImageBitmap(clipBitmap);
		_dialogUseClip.setMessage(getString(R.string.text_use_this));
		_dialogUseClip.setPositiveOnClickListener
    	(
    		new OnClickListener()
    		{
				@Override
				public void onClick(View v)
				{
					String webClipPath = AppPreference.instance().getWebContentFolder()+
							"webclip_"+System.currentTimeMillis()+".webcontent";
					
					_clipView.saveImageAfterCrop(webClipPath);
					_dialogUseClip.dismiss();
					
					Bundle bundle = new Bundle();  
				    bundle.putString(BUNDLE_KEY_WEB_CLIP_PATH, webClipPath);  
				    Intent intent = new Intent();  
				    intent.putExtras(bundle);  
					
					setResult(RESULT_OK,intent);
					finish();
				}
    		}
    	);
		_dialogUseClip.setNagtiveOnClickListener
    	(
    		new OnClickListener()
    		{
				@Override
				public void onClick(View v)
				{
					_dialogUseClip.dismiss();
					_clipView.eraseClip();
				}
    		}
    	);
		_dialogUseClip.setOnDismissListener(new OnDismissListener(){
			@Override
			public void onDismiss(DialogInterface dialog)
			{
				_clipView.eraseClip();
			}
		});
		
		
		_dialogUseClip.show();
	}

	private void goToUrl()
	{
		String url = _urlEditText.getText().toString();
		
		if(!_urlEditText.getText().equals(""))
		{
			progressBar.setVisibility(View.VISIBLE);
			
			if( !url.startsWith("http://") &&
				!url.startsWith("https://")&&
				!url.startsWith("ftp://"))
			{  
				url = "http://"+url;
			}
			
			_webView.loadUrl(url);  
			_urlArea.setVisibility(View.GONE);
			InputMethodManager imm = (InputMethodManager)getSystemService(
				      Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(_urlEditText.getWindowToken(), 0);
		}
	}
	
	@Override 
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
	{
		switch(actionId)
		{  
	        case EditorInfo.IME_ACTION_GO: 
	        	goToUrl(); 
	        	break;
        }  
		return false;
	}
}
