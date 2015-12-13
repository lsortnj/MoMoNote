package mo.pay.post_it;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import mo.pay.post_it.R;
import mo.pay.post_it.bitmapPlugin.Util;
import mo.pay.post_it.clipWeb.ClipWebContent;
import mo.pay.post_it.logic.AlbumViewListener;
import mo.pay.post_it.logic.ErrorProtector;
import mo.pay.post_it.multiMedia.IAddNoteObjectListener;
import mo.pay.post_it.popup.QuickAction;
import mo.pay.post_it.store.AlbumPageAudioStore;
import mo.pay.post_it.store.AlbumPageHandDrawStore;
import mo.pay.post_it.store.AlbumPageImageStore;
import mo.pay.post_it.store.AlbumPageTextStore;
import mo.pay.post_it.store.AlbumPageVideoStore;
import mo.pay.post_it.store.AlbumStore;
import mo.pay.post_it.store.AppPreference;
import mo.pay.post_it.ui.DialogEditName;
import mo.pay.post_it.ui.DialogPositiveNagative;
import mo.pay.post_it.ui.DialogSingleButton;
import mo.pay.post_it.ui.PageThumbAdapter;
import mo.pay.post_it.ui.PopupInsertNoteObject;
import mo.pay.post_it.userguide.UserGuideActivity;
import mo.pay.post_it.util.DeviceInfoUtil;
import mo.pay.post_it.util.FileUtil;
import mo.pay.post_it.util.NetworkUtil;
import mo.pay.post_it.util.VibrateUtil;
import mo.pay.post_it.widget.MomoAudioView;
import mo.pay.post_it.widget.MomoImageView;
import mo.pay.post_it.widget.MomoTextView;
import mo.pay.post_it.widget.MomoToast;
import mo.pay.post_it.widget.MomoVideoView;
import mo.pay.post_it_album.Album;
import mo.pay.post_it_album.AlbumPage;
import mo.pay.post_it_album.AlbumPageView;
import mo.pay.post_it_handDraw.ColorSelectListener;
import mo.pay.post_it_handDraw.DialogQuickColorPicker;
import mo.pay.post_it_handDraw.HandDraw;
import mo.pay.post_it_handDraw.IHandDrawListener;
import mo.pay.post_it_handDraw.PopupDrawSetting;
import mo.pay.post_it_handDraw.ViewDrawSetting;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
 
@SuppressWarnings("deprecation")
public class PostItEditActivity extends Activity 
	implements AlbumViewListener ,IHandDrawListener, IAddNoteObjectListener
{  
	public static final int MAX_IMAGE_COUNT_FOR_FREE = 2;
	public static final int MAX_MEDIA_COUNT_FOR_FREE = 1;
	
	public static final String TAG = "PostItEditActivity";
	        
	public static final int MODE_NEW	= 0x701;
	public static final int MODE_EDIT	= 0x702;
	public static final int MODE_VIEW	= 0x703;
	        
	private static final int	ACTION_LOAD_IMAGE		= 0x401;
	private static final int	ACTION_LOAD_VIDEO		= 0x402;
	private static final int	ACTION_CAPTURE_IMAGE	= 0x403;
	private static final int	ACTION_RECORD_VIDEO		= 0x404;
	private static final int	ACTION_LOAD_AUDIO		= 0x405;
	private static final int	ACTION_RECORD_AUDIO		= 0x406;
	private static final int	ACTION_CLIP_WEB_CONTENT	= 0x407;
	 
	public static final int IMAGE_MAX_WIDTH		= 300;
	public static final int IMAGE_MAX_HEIGHT	= 300;
	public static final int IMAGE_MIN_WIDTH		= 100;
	public static final int IMAGE_MIN_HEIGHT	= 100;
	
	//Tool Area
	private View						_btnShare			 = null;
	private View						_btnAddNoteObject	 = null;
	private View						_btnDraw			 = null;
	private View						_btnNextPage		 = null;
	private View						_btnPreviousPage	 = null;
	private View						_btnView			 = null;
	private View						_btnEdit			 = null;
	private View						_btnDeletePage		 = null;
	private View						_btnPageSwitch		 = null;
	
	private RelativeLayout 				_noteArea			 		= null;
	private RelativeLayout 				_toolArea			 		= null;
	private RelativeLayout 				_toolIconArea			 	= null;
	private Album						_album						= null;
	private AlbumPageView				_currentAlbumPageView		= null;
	private DialogPositiveNagative 		_dialog_exit_comfirm 		= null;
	private DialogPositiveNagative 		_dialog_delete_page_comfirm = null;
	private DialogPositiveNagative 		_dialog_free_edition 		= null;
	private QuickAction					_quickActionDrawSetting 	= null;
	private Gallery						_pageSwitchGallery			= null;
//	private ProgressBar					_loadingProgress			= null;
	 
	private Uri				currImageURI  					= null;
	private Uri				captureImageURI  				= null;
	private ProgressDialog 	_progress_dialoag				= null;
	private String 			_albumId     					= "";
	private String 			_albumName 						= "";
	
	private int				_current_mode 					= MODE_EDIT;
	private int 			_currentPageIdx 				= 0;
	
	private SimpleDateFormat 		_dateFormatter 		= new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
	private GestureDetector 		_gesture_detector	= null;
	
	
	//Limitation for free version
	private boolean isFreeVersion = false;
	private static final int FREE_VERSION_MAX_IMAGE = 2;
	private static final int FREE_VERSION_MAX_MEDIA = 1;
	
	private Handler _initHandler = new Handler(){
		public void handleMessage(Message msg)
		{
			initViews();
			
			initExitConfirmDialog();
			
			initFreeEditionDialog();
			
			initState();
			
			_progress_dialoag.dismiss();
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
      
		setContentView(R.layout.activity_post_it_edit);
		
		_current_mode	= getIntent().getIntExtra("mode",MODE_NEW);
		
		_albumName 		= getIntent().getStringExtra("albumName");
		_albumId		= getIntent().getStringExtra("albumId");
		
		_noteArea 		= (RelativeLayout) findViewById(R.id.clip_web_content_area);
		_toolArea		= (RelativeLayout) findViewById(R.id.clip_web_tool_area);
		_toolIconArea	= (RelativeLayout) findViewById(R.id.clip_web_tool_icon_area);
		_pageSwitchGallery = (Gallery) findViewById(R.id.page_switch_gallery);
		
		_btnShare 		= findViewById(R.id.edit_btn_share);
		_btnAddNoteObject 	= findViewById(R.id.edit_btn_add_note_object);
		_btnDraw 		= findViewById(R.id.edit_btn_draw);
		_btnNextPage	= findViewById(R.id.edit_btn_next_page);
		_btnPreviousPage= findViewById(R.id.edit_previous_page);
		_btnView		= findViewById(R.id.edit_btn_view);
		_btnEdit		= findViewById(R.id.edit_btn_edit);
		_btnDeletePage  = findViewById(R.id.edit_btn_del_page);
		_btnPageSwitch	= findViewById(R.id.clip_web_btn_start_clip);
//		_loadingProgress = (ProgressBar) findViewById(R.id.edit_loading);
		
		ToolClickListener toolListener = new ToolClickListener();
		ToolLongClicklistenre toolLongClickListener = new ToolLongClicklistenre();
		
		_btnShare.setOnClickListener(toolListener);
		_btnAddNoteObject.setOnClickListener(toolListener);
		_btnDraw.setOnClickListener(toolListener);
		_btnNextPage.setOnClickListener(toolListener);
		_btnPreviousPage.setOnClickListener(toolListener);
		_btnView.setOnClickListener(toolListener);
		_btnEdit.setOnClickListener(toolListener);
		_btnDeletePage.setOnClickListener(toolListener);
		_btnPageSwitch.setOnClickListener(toolListener);
		  
		_btnShare.setOnLongClickListener(toolLongClickListener);
		_btnAddNoteObject.setOnLongClickListener(toolLongClickListener);
		_btnDraw.setOnLongClickListener(toolLongClickListener);
		_btnNextPage.setOnLongClickListener(toolLongClickListener);
		_btnPreviousPage.setOnLongClickListener(toolLongClickListener);
		_btnView.setOnLongClickListener(toolLongClickListener);
		_btnEdit.setOnLongClickListener(toolLongClickListener);
		_btnDeletePage.setOnLongClickListener(toolLongClickListener);
		 
		_pageSwitchGallery.getLayoutParams().height=getResources().getDimensionPixelSize(R.dimen.page_switch_gallery_height);
		 
		_btnPreviousPage.setVisibility(View.INVISIBLE);
		 
		_gesture_detector = new GestureDetector(new ViewModePageSwitchGesture());
		
		_noteArea.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				if(_pageSwitchGallery.getVisibility()==View.VISIBLE)
				{
					_pageSwitchGallery.setVisibility(View.GONE);
					_pageSwitchGallery.startAnimation(AnimationUtils.loadAnimation(PostItEditActivity.this, android.R.anim.fade_out));
				}
				return false;
			}
			
		});
		
		_progress_dialoag = ProgressDialog.show
				(
					this,
					"",
					getString(R.string.tip_please_wait)
				);
		
		new Thread(new Runnable(){
			public void run()
			{
				_initHandler.sendEmptyMessage(0);
			}
		}).start();
		
	}
	
	private void initFreeEditionDialog()
	{
		_dialog_free_edition = new DialogPositiveNagative(this);
		_dialog_free_edition.setMessage(getString(R.string.tip_free_limit_image));
		_dialog_free_edition.setPositiveText(getString(R.string.btn_buy));
		_dialog_free_edition.setNagtiveText(getString(R.string.btn_no_thanks));
		_dialog_free_edition.setPositiveOnClickListener
    	(
    		new OnClickListener()
    		{
				@Override
				public void onClick(View v)
				{
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(Uri.parse("market://details?id=mo.pay.post_it"));
					startActivity(intent);
					
					_dialog_free_edition.dismiss();
				}
    		}
    	);
		_dialog_free_edition.setNagtiveOnClickListener
    	(
    		new OnClickListener()
    		{
				@Override
				public void onClick(View v)
				{
					_dialog_free_edition.dismiss();
				}
    		}
    	);
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		
		if((_current_mode == MODE_NEW || _current_mode == MODE_EDIT) 
				&& AppPreference.instance().isFirstTimeToEditMode())
		{
			adjustToolBar();
			
			Bundle bundle = new Bundle();
			bundle.putInt("type", UserGuideActivity.TYPE_EDIT_MODE_GUIDE);
			bundle.putInt("toolAreaHeight", _toolArea.getLayoutParams().height);
			bundle.putInt("toolButtonWidth", _btnAddNoteObject.getLayoutParams().width);
			
			Intent intent = new Intent();
			intent.setClass(this, UserGuideActivity.class);
			intent.putExtras(bundle);
			startActivity(intent);
			AppPreference.instance().isFirstTimeToEditMode(false);
		}
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		
		if(_current_mode != MODE_VIEW)
		{
			saveHandDraw();
		}
		
		_currentAlbumPageView.orientationChanged();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState)
	{
		super.onRestoreInstanceState(savedInstanceState);
	}
	
	@Override
	protected void onStop()
	{
		super.onStop();
		
		if(_progress_dialoag != null)
			_progress_dialoag.dismiss();
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		
		if(_progress_dialoag != null)
			_progress_dialoag.dismiss();
	}
	
	private void initViews()
	{
		ViewDrawSetting.initView(this, this);
//		ViewAddMultiMedia.initView(this, this);
	}
	
	private void initExitConfirmDialog()
	{
		VibrateUtil.doShortVibrate(3, 50);
		
		_dialog_exit_comfirm = new DialogPositiveNagative(this);
    	_dialog_exit_comfirm.setMessage(getString(R.string.comfirm_exit_note_paste_mode));
    	_dialog_exit_comfirm.setIcon
    	(
    		BitmapFactory.decodeResource(getResources(), 
    	    			R.drawable.logo)
    	);
    	_dialog_exit_comfirm.setPositiveOnClickListener
    	(
    		new OnClickListener()
    		{
				@Override
				public void onClick(View v)
				{
					_dialog_exit_comfirm.dismiss();
					
					if(_current_mode == MODE_VIEW)
					{
						finish();
					}
					else
					{
						saveBeforeExite();
					}
				}
    			
    		}
    	);
    	_dialog_exit_comfirm.setNagtiveOnClickListener
    	(
    		new OnClickListener()
    		{
				@Override
				public void onClick(View v)
				{
					_dialog_exit_comfirm.dismiss();
				}
    			
    		}
    	);
	}

	@Override
	public void onResume()
	{
		super.onResume();
		Log.e("PostItActivity", "onResume");
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();

		_currentAlbumPageView = null;
		
		Log.e("PostItActivity", "onDestroy");
	}
	 
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		if (keyCode == KeyEvent.KEYCODE_BACK) 
		{
			_currentAlbumPageView.stopAllPlayer();
			
			switch(_current_mode)
			{
				case MODE_VIEW:
					_dialog_exit_comfirm.show();
					break;
					
				case MODE_EDIT:
					changeMode(MODE_VIEW);
					break;
					
				case MODE_NEW:
					changeMode(MODE_VIEW);
					break;
			}
			
			return true;
			
//			if(_current_mode != MODE_VIEW)
//			{
//				_dialog_exit_comfirm.show();
//				
//				return true;
//	    	}
	    }
	        
	    return super.onKeyDown(keyCode, event);
	}
	
	private void saveHandDraw()
	{
		//Create Album Store Folder
		String store_folder = _album.getAlbumRootPath()+File.separator+"Page"+_currentPageIdx + File.separator;
		
		if(!new File(store_folder).exists())
		{
			new File(store_folder).mkdir();
		}
		
		String handDrawImgSavePath = store_folder+"handDraw.draw";
		
		AlbumPage albumPage = _album.getAlbumPage(_currentPageIdx);
		albumPage.setAlbumName(_album.getAlbumName());
		albumPage.setAlbumPageName("");
		
		HandDraw handDraw = albumPage.getHandDraw();
		
		if(_currentAlbumPageView.getHandDrawView().saveNotePaste(handDrawImgSavePath))
		{
			//Save hand write note
			handDraw.setAlbumId(_albumId);
			handDraw.setAlbumName(_albumName);
			handDraw.setPageNo(String.valueOf(_currentPageIdx));
			handDraw.setStorePath(handDrawImgSavePath);
			
			AlbumPageHandDrawStore.instance().insertHandDraw(handDraw);
			
			albumPage.setHandDraw(handDraw);
		}
		
		_album.setAlbumPage(albumPage, _currentPageIdx);
	}
	
	class FileCopyObject
	{
		private String _sourcePath = "";
		private String _destinationPath = "";
		
		public FileCopyObject(String sourcePath, String destinationPath)
		{
			_sourcePath = sourcePath;
			_destinationPath = destinationPath;
		}
		public String getSourcePath()
		{
			return _sourcePath;
		}
		public String getDestination()
		{
			return _destinationPath;
		}
	}
	
	private List<FileCopyObject> fileNeedToCopy = new ArrayList<FileCopyObject>();
	
	private static final int SAVE_PAGE_START  = 0x001;
	private static final int SAVE_PAGE_FINISH = 0x002;
	
	private Handler _savePageHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch(msg.what)
			{
				case SAVE_PAGE_START:
					_pageSwitchGallery.setVisibility(View.GONE);
					_btnNextPage.setVisibility(View.GONE);
					_btnPreviousPage.setVisibility(View.GONE);
					break;
					
				case SAVE_PAGE_FINISH:
					adjustPageSwitchIcon();
					adjustToolBar();
					break;
			}
		}
	};
	
	private boolean saveCurrentAlbumPage(boolean isRecycleBitmap)
	{
		boolean result = false;

		if (_currentAlbumPageView != null)
		{
			_savePageHandler.sendEmptyMessage(SAVE_PAGE_START);
			
			fileNeedToCopy.clear();
			
			/**
			 *  Album/PAGE_NO/ hand write PNG,image,Video,audio...
			 */
			_currentAlbumPageView.stopAllPlayer();
			
			//Create Album Store Folder
			String store_folder = _album.getAlbumRootPath()+File.separator+"Page"+_currentPageIdx + File.separator;
			
			_currentAlbumPageView.savePageScreenCapture
			(
				store_folder+"thumb.thu", 
				_toolArea.getLayoutParams().height,
				true
			);
			
			String handDrawImgSavePath = store_folder+"handDraw.draw";
			
			AlbumPage albumPage = _album.getAlbumPage(_currentPageIdx);
			albumPage.setAlbumName(_album.getAlbumName());
			albumPage.setAlbumPageName("");
			
			//Prevent view already has parent
			albumPage.removeAllAudioView();
			albumPage.removeAllImageView();
			albumPage.removeAllTextView();
			albumPage.removeAllVideoView();
			
			HandDraw handDraw = albumPage.getHandDraw();
			
			if(_currentAlbumPageView.getHandDrawView().saveNotePaste(handDrawImgSavePath))
			{
				//Save hand write note
				handDraw.setAlbumId(_albumId);
				handDraw.setAlbumName(_albumName);
				handDraw.setPageNo(String.valueOf(_currentPageIdx));
				handDraw.setStorePath(handDrawImgSavePath);
				
				AlbumPageHandDrawStore.instance().insertHandDraw(handDraw);
				
				albumPage.setHandDraw(handDraw);
			}
			
			
			
			if (!_currentAlbumPageView.isEmpty())
			{
				try
				{
					//Copy all video to store folder
					for(MomoVideoView video : _currentAlbumPageView.getAllVideoViews())
					{
						if(!new File(store_folder+video.getVideoFileName()).exists())
						{
//							fileNeedToCopy.add(new FileCopyObject(video.getVideoPath(), store_folder+video.getVideoFileName()));
//							FileUtil.copyFile(video.getVideoPath(), store_folder+video.getVideoFileName());
						}
//						video.setVideoPath(store_folder+video.getVideoFileName());
						
						albumPage.addVideoView(video);
						
						//Insert into store
						AlbumPageVideoStore.instance().insert
						(
							video, _albumName, 
							String.valueOf(_currentPageIdx), 
							_albumId
						);
					}
					
					//Copy all image to store folder
					for(MomoImageView img : _currentAlbumPageView.getAllImageViews())
					{
						if(!new File(store_folder+img.getImageFileName()).exists())
						{
//							fileNeedToCopy.add(new FileCopyObject(img.getImagePath(), store_folder+img.getImageFileName()));
//							FileUtil.copyFile(img.getImagePath(), store_folder+img.getImageFileName());
						}
						
//						img.setImagePath(store_folder+img.getImageFileName());
						
						albumPage.addImageView(img);
						
						//Insert into store
						AlbumPageImageStore.instance().insert
						(
							img, _albumName, 
							String.valueOf(_currentPageIdx), 
							_albumId
						);
					}
					
					//Save Text
					for(MomoTextView txt : _currentAlbumPageView.getAllTextViews())
					{
						albumPage.addTextView(txt);
						
						//Insert into store
						AlbumPageTextStore.instance().insert
						(
							txt, 
							_albumName, 
							String.valueOf(_currentPageIdx), 
							_albumId
						);
					}
					
					//Save Audio
					for(MomoAudioView audio : _currentAlbumPageView.getAllAudioViews())
					{
						if(!new File(store_folder + audio.getAudioFileName()).exists())
						{
//							fileNeedToCopy.add(new FileCopyObject(audio.getAudioPath(), store_folder+audio.getAudioFileName()));
//							FileUtil.copyFile(audio.getAudioPath(), store_folder+audio.getAudioFileName());
						}
						
//						audio.setAudioPath(store_folder + audio.getAudioFileName());
						
						albumPage.addAudioView(audio);
						
						//Insert into store
						AlbumPageAudioStore.instance().insert
						(
							audio, 
							_albumName, 
							String.valueOf(_currentPageIdx), 
							_albumId
						);
					}

					result = true;
					
					new Thread(new Runnable(){
						public void run()
						{
							for(FileCopyObject fileCopyObj : fileNeedToCopy)
							{
								FileUtil.copyFile(fileCopyObj.getSourcePath(),fileCopyObj.getDestination());
							}
						}
					}).start();
				} 
				catch (Exception e)
				{
					Log.i("insertNotePasteToStore", e.toString());
					
					result = false;
				}
			}
			
			_album.setAlbumPage(albumPage, _currentPageIdx);
			
			if(isRecycleBitmap)
				_currentAlbumPageView.recycleAllBitmap();
			
			_savePageHandler.sendEmptyMessage(SAVE_PAGE_FINISH);
		}

		return result;
	}
	
	private void saveBeforeExite()
	{
		if(_currentAlbumPageView != null)
		{
			_progress_dialoag = ProgressDialog.show
			(
				this,
				"",
				getString(R.string.note_paste_saving)
			);
			
			Runnable run = new Runnable()
			{
				public void run()
				{
//					_currentAlbumPageView.stopAllPlayer();
					
					if(_current_mode != MODE_VIEW)
					{
						saveCurrentAlbumPage(true);
					}
					
					recycleAllBitmaps();
					
				    _progress_dialoag.dismiss();
					
					finish();
				}
			};
			
			new Thread(run).start();
		}
		else
		{
			finish();
		}
	}
	
	private void recycleAllBitmaps()
	{
		_currentAlbumPageView.recycleAllBitmap();
		_album.recycleAllBitmap();
		System.gc();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) 
	{ 
		if (resultCode == RESULT_OK) 
		{
			if (requestCode == ACTION_CLIP_WEB_CONTENT)
			{
				String webClipPath = data.getExtras().getString(ClipWebContent.BUNDLE_KEY_WEB_CLIP_PATH);
				
				if(webClipPath!=null && !webClipPath.equals(""))
				{
					Bitmap bitmap  = Util.makeBitmap
						(
							Util.IMAGE_MAX_WIDTH, 
							((DeviceInfoUtil.getDeviceDisplayMetrics().widthPixels 
							*DeviceInfoUtil.getDeviceDisplayMetrics().heightPixels))/2,
							Uri.parse("file://"+webClipPath), 
							getContentResolver(), false
						);
					
					convertImageIntoImageview(bitmap, webClipPath, MomoImageView.FRAME_NONE);
				}
			}
			if (requestCode == ACTION_LOAD_AUDIO)
			{
				currImageURI = data.getData();
				
				String audioPath = getRealPathFromURI(currImageURI);
				
				if(FileUtil.getMIMEType(new File(audioPath)) != FileUtil.FILE_TYPE_AUDIO)
				{ 
					new DialogSingleButton(this,getString(R.string.error_this_is_not_a_audio)).show();
					
					return;
				}
				
				convertAudioIntoAudioview();
			}
			if (requestCode == ACTION_RECORD_AUDIO)
			{
				currImageURI = data.getData();
				convertAudioIntoAudioview();
			}
			if (requestCode == ACTION_LOAD_IMAGE)
			{
				try
				{
					currImageURI = data.getData();
					
					String img_path = getRealPathFromURI(currImageURI);
					
					if(FileUtil.getMIMEType(new File(img_path)) != FileUtil.FILE_TYPE_IMAGE)
					{
						new DialogSingleButton(this,getString(R.string.error_this_is_not_a_image)).show();
						
						return;
					}
					
					convertImageIntoImageview();
				}
				catch(Exception e)
				{
					new DialogSingleButton(this,getString(R.string.error_to_load_image_from_external)).show();
				}
	        }
			if(requestCode == ACTION_LOAD_VIDEO)
			{
				try
				{
					currImageURI = data.getData();
					
					String video_path = getRealPathFromURI(currImageURI);
					
					if(FileUtil.getMIMEType(new File(video_path)) != FileUtil.FILE_TYPE_VIDEO)
					{ 
						new DialogSingleButton(this,getString(R.string.error_this_is_not_a_video)).show();
						
						return;
					}
					
					convertVideoIntoVideoview();
				}
				catch(Exception e)
				{
					new DialogSingleButton(this,getString(R.string.error_to_load_video_from_external)).show();
				}
			}
			if(requestCode == ACTION_RECORD_VIDEO)
			{
				currImageURI = data.getData();
				convertVideoIntoVideoview();
			}
			if(requestCode == ACTION_CAPTURE_IMAGE)
			{
				Cursor cursor = null;
				try  
				{
				    String [] proj={
				    	MediaStore.Images.Media.DATA, 
				    	MediaStore.Images.Media._ID, 
				    	MediaStore.Images.ImageColumns.ORIENTATION
				    };
				    
				    cursor = managedQuery( 
				    		captureImageURI,
				            proj, // Which columns to return
				            null,       // WHERE clause; which rows to return (all rows)
				            null,       // WHERE clause selection arguments (none)
				            null); // Order-by clause (ascending by name)
				    
				    int file_ColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				    
				    if (cursor.moveToFirst()) 
				    {
				    	Bitmap bitmap  = Util.makeBitmap
								(
									IMAGE_MAX_WIDTH, 
									((DeviceInfoUtil.getDeviceDisplayMetrics().widthPixels 
									*DeviceInfoUtil.getDeviceDisplayMetrics().heightPixels))/2,
									captureImageURI, 
									getContentResolver(), false
								);
				    	
				    	
						FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(300,300);
						
						params.gravity = Gravity.CENTER;
						
						MomoImageView img_view = new MomoImageView(PostItEditActivity.this);
						img_view.setScaleType(ScaleType.FIT_CENTER);
						img_view.setImageBitmap(bitmap,cursor.getString(file_ColumnIndex));
						img_view.setLayoutParams(params);
						img_view.setAlbumPageViewListner(PostItEditActivity.this);
//						img_view.setMiniSize(new Point(IMAGE_MIN_WIDTH,IMAGE_MIN_HEIGHT));
						img_view.toEditMode();
						
						_currentAlbumPageView.addImageView(img_view);
						
						toFirstAddObjectGuide();
				    }
				} 
				finally 
				{
				    if (cursor != null) 
				    {
//				        cursor.close();
				    }
				}
			}
	   }
	}
	
	public String getRealPathFromURI(Uri contentUri) 
    {
    	String mo = contentUri.toString();
    	
    	if(mo.contains("file:///"))
    	{
    		try
			{
    			String path ="";
    			
    			path = new File(new URI(contentUri.toString())).getAbsolutePath();
    			
				return path;
			} 
    		catch (URISyntaxException e)
			{
				e.printStackTrace();
			}
    	}
    	else
    	{
    		String[] proj = { MediaStore.Images.Media.DATA };
            Cursor cursor = managedQuery(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            
            return cursor.getString(column_index);
    	}
    	
    	return null;
    }
	
	private void convertAudioIntoAudioview()
	{
		try 
		 { 
			String audioPath = getRealPathFromURI(currImageURI);
			 
			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(365,185);
	 		
			params.gravity = Gravity.CENTER;
			 
			MomoAudioView audioView = new MomoAudioView(PostItEditActivity.this,audioPath);
			audioView.setAudioPath(audioPath);
			audioView.setAlbumPageViewListner(PostItEditActivity.this);
			audioView.setLayoutParams(params);
			audioView.toEditMode();
			 
			_currentAlbumPageView.addAudioView(audioView);
			_currentAlbumPageView.addView(audioView);
			 
//			toFirstAddObjectGuide();
		 } 
		 catch (Exception e) 
		 {
			 new MomoToast(this,getString(R.string.error_to_load_audio_from_external));
		 }
	}
	
	private void convertVideoIntoVideoview()
	{
		 try 
		 {
			String video_path = getRealPathFromURI(currImageURI);
			
			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(400,300);
			params.gravity = Gravity.CENTER;
			
			MomoVideoView video_view = new MomoVideoView(PostItEditActivity.this,video_path);
			video_view.setVideoPath(video_path);
			video_view.setAlbumPageViewListner(PostItEditActivity.this);
			video_view.setLayoutParams(params);
			video_view.toEditMode();
			
			_currentAlbumPageView.addVideoView(video_view);
			_currentAlbumPageView.addView(video_view);
			
			toFirstAddObjectGuide();
		 } 
		 catch (Exception e) 
		 {
			 new MomoToast(this,getString(R.string.error_to_load_video_from_external));
		 }
	}
	
	private void convertImageIntoImageview() throws URISyntaxException
	{
		 try 
		 {
			Bitmap bitmap  = Util.makeBitmap
					(
						Util.IMAGE_MAX_WIDTH, 
						((DeviceInfoUtil.getDeviceDisplayMetrics().widthPixels 
						*DeviceInfoUtil.getDeviceDisplayMetrics().heightPixels))/2,
						currImageURI, 
						getContentResolver(), false
					);
			
			convertImageIntoImageview(bitmap, getRealPathFromURI(currImageURI), MomoImageView.FRAME_POLAROID);
		 } 
		 catch (Exception e) 
		 {
			 new MomoToast(this,getString(R.string.error_to_load_image_from_external)).show();
		 }
	}
	
	private void convertImageIntoImageview(Bitmap bitmap, String path, int frameType)
	{
		try 
		 {
			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(300,300);
			
			params.gravity = Gravity.CENTER;
			
			MomoImageView img_view = new MomoImageView(PostItEditActivity.this);
			img_view.setFrameType(frameType);
			img_view.setScaleType(ScaleType.FIT_CENTER);
			img_view.setImageBitmap(bitmap,path);
			img_view.setLayoutParams(params);
			img_view.setAlbumPageViewListner(PostItEditActivity.this);
			img_view.toEditMode();
			
			_currentAlbumPageView.addImageView(img_view);
			
			toFirstAddObjectGuide();
		 } 
		 catch (Exception e) 
		 {
			 new MomoToast(this,getString(R.string.error_to_load_image_from_external)).show();
		 }
	}
	
	private void doShareNotePaste()
	{
		if(_currentAlbumPageView != null)
		{
			_currentAlbumPageView.stopAllPlayer();
		}
		
		_progress_dialoag = ProgressDialog.show(this, "", getString(R.string.tip_image_generating_please_wait));
		
		final String temp_file = AppPreference.instance().getAppRootFolder()+"share.png";
		
		final Handler handler = new Handler()
		{
			public void handleMessage(Message msg)
			{
				 _progress_dialoag.dismiss();
				
				 switch(msg.what)
				 {
				 	case -1:
				 		new MomoToast(PostItEditActivity.this,getString(R.string.error_share_note)).show();
				 		
				 		_toolArea.setVisibility(View.VISIBLE);
				 		
				 		break;
				 		
				 	case 0:
				 		
				 		Intent sharingIntent = new Intent(Intent.ACTION_SEND);
			        	Uri uri = Uri.fromFile(new File(temp_file));

			        	sharingIntent.setType("image/png");
			        	sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
			        	startActivity(Intent.createChooser(sharingIntent, getString(R.string.choose_app_to_share_notepaste)));
				 		
				 		break;
				 }
				 
				
			}
		};
		
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
//				FileOutputStream fout = null;
//				
//				if(new File(temp_file).exists())
//				{
//					new File(temp_file).delete();
//				}
//				
//				try{fout = new FileOutputStream(temp_file);} 
//				catch (FileNotFoundException e){e.printStackTrace();}
//			
				if(_currentAlbumPageView==null)
				{
					handler.sendEmptyMessage(-1);
					return;
				}
				
				_currentAlbumPageView.savePageScreenCapture
				(
					temp_file,
					_toolArea.getLayoutParams().height,
					false
				);
				
				handler.sendEmptyMessage(0);
			}
		}).start();
	}
	
	private void initState()
	{
		_noteArea.setBackgroundResource(_current_mode==MODE_VIEW?R.drawable.bg_view:R.drawable.bg_edit);
		_toolArea.setBackgroundResource(_current_mode==MODE_VIEW?R.drawable.top_area_bg_view:R.drawable.top_area_bg_edit);
		
		switch(_current_mode)
		{
			case MODE_EDIT:
				
				prepareFromExistAlbum();
				
				disableViewModeGesture();
				
				break;
				
			case MODE_NEW:
				
				createNewAlbum();
				
				addNewAlbumPage();
				
				disableViewModeGesture();
				
				break;	
				
			case MODE_VIEW:
				
				prepareFromExistAlbum();
				
				enableViewModeGesture();
				
				break;	
		}
		
		adjustPageSwitchIcon();
		adjustToolBar();
	}
	
	@Override 
	public boolean dispatchTouchEvent(MotionEvent event) 
	{ 
		if(_current_mode == MODE_VIEW && _gesture_detector.onTouchEvent(event))
		{ 
			event.setAction(MotionEvent.ACTION_CANCEL); 
		} 
		
		return super.dispatchTouchEvent(event); 
	} 
	
	private void toFirstAddObjectGuide()
	{
		if(AppPreference.instance().isFirstTimeAddObject())
		{
			Bundle bundle = new Bundle();
			bundle.putInt("type", UserGuideActivity.TYPE_FIRST_INSERT_OBJECT_GUIDE);
			bundle.putInt("toolBarHeight",_toolArea.getHeight());
			
			Intent intent = new Intent();
			intent.setClass(PostItEditActivity.this, UserGuideActivity.class);
			intent.putExtras(bundle);
			startActivity(intent);
			
			AppPreference.instance().isFirstTimeAddObject(false);
		}
	}
	
	private void toObjectEditGuide()
	{
		if(AppPreference.instance().isFirstTimeObjectLostFocus())
		{
			Bundle bundle = new Bundle();
			bundle.putInt("type", UserGuideActivity.TYPE_REEDIT_OBJECT_GUIDE);
			bundle.putInt("toolBarHeight",_toolArea.getHeight());
			
			Intent intent = new Intent();
			intent.setClass(PostItEditActivity.this, UserGuideActivity.class);
			intent.putExtras(bundle);
			startActivity(intent);
			
			AppPreference.instance().isFirstTimeObjectLostFocus(false);
		}
	}
	
	private void disableViewModeGesture()
	{
		if(_currentAlbumPageView == null)
			return;
		 
		_currentAlbumPageView.setOnTouchListener(new OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				if(_current_mode != MODE_VIEW)
				{
					if(_currentAlbumPageView != null)
					{
						_currentAlbumPageView.deSelectAllViews();
						toObjectEditGuide();
					}
				}
				
				return false;
			}
		});
		
		_currentAlbumPageView.getHandDrawView().setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(_current_mode != MODE_VIEW)
				{
					if(_currentAlbumPageView != null)
					{
						_currentAlbumPageView.deSelectAllViews();
						toObjectEditGuide();
					}
				}
			}
		});
		
	}
	
	private void enableViewModeGesture()
	{
		adjustPageSwitchIcon();
		adjustToolBar();
		if(_currentAlbumPageView==null)
			return;
		
		_currentAlbumPageView.setOnTouchListener(new OnTouchListener()
		{
			public boolean onTouch(View v, MotionEvent event)
			{
				if (_gesture_detector.onTouchEvent(event))
				{
					return true;
				}

				return false;
			}

		});
	}
	
	private void changeMode(int mode)
	{
		if(_currentAlbumPageView != null)
			_currentAlbumPageView.stopAllPlayer(); 
		
		_current_mode = mode;
		
		_noteArea.setBackgroundResource(_current_mode==MODE_VIEW?R.drawable.bg_view:R.drawable.bg_edit);
		_toolArea.setBackgroundResource(_current_mode==MODE_VIEW?R.drawable.top_area_bg_view:R.drawable.top_area_bg_edit);
		
		switch(_current_mode)
		{ 
			case MODE_EDIT:
				  
				_currentAlbumPageView.enableEdit(this);
				disableViewModeGesture();
				_toolIconArea.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));

				new MomoToast(this,getString(R.string.tip_mode_edit)).show();
				
				break;
				   
			case MODE_NEW:
				
				_currentAlbumPageView.enableEdit(this);
				disableViewModeGesture();
			 	break;	 
			 	 
			case MODE_VIEW:
				
				doSavePage(CHANGE_TO_VIEW_MODE,-1);
				enableViewModeGesture();
				_toolIconArea.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
				
				break;	
		}
		
		adjustPageSwitchIcon();
		adjustToolBar();
	}
	
	private void adjustToolBar()
	{
		switch(_current_mode)
		{
			case MODE_EDIT:
				
				_btnAddNoteObject.setVisibility(View.VISIBLE);
				_btnDraw.setVisibility(View.VISIBLE);
				_btnView.setVisibility(View.VISIBLE);
				
				if(_album != null)
				{
					_btnDeletePage.setVisibility(_album.getPagecount()>1?View.VISIBLE:View.GONE);
				}
				
				_btnEdit.setVisibility(View.GONE);
				_btnShare.setVisibility(View.INVISIBLE);
				
				break;
				 
			case MODE_NEW:
				 
				_btnAddNoteObject.setVisibility(View.VISIBLE);
				_btnDraw.setVisibility(View.VISIBLE);
				_btnView.setVisibility(View.VISIBLE);
				
				if(_album != null)
				{
					_btnDeletePage.setVisibility(_album.getPagecount()>1?View.VISIBLE:View.GONE);
				}
				
				_btnEdit.setVisibility(View.GONE);
				_btnShare.setVisibility(View.INVISIBLE);
				 
				break;	
				
			case MODE_VIEW:
				
				_btnAddNoteObject.setVisibility(View.GONE);
				_btnDraw.setVisibility(View.GONE);
				_btnView.setVisibility(View.GONE);
				_btnDeletePage.setVisibility(View.GONE);
				_btnEdit.setVisibility(View.VISIBLE);
				_btnShare.setVisibility(View.VISIBLE);
				
				break;	
		}
	}
	
	private void refreshAlbum()
	{
		_album.removeAllPages();
		
		List<HandDraw> handDraws = AlbumPageHandDrawStore.instance().getHandDrawsByAlbumId(_albumId); 
		
		for(HandDraw handDraw : handDraws)
		{
			String pageNo = handDraw.getPageNo();
			
			AlbumPage page = new AlbumPage();
			page.setHandDraw(handDraw);
			page.setAlbumId(_albumId);
			page.setAlbumName(_albumName);
			page.setPageNo(pageNo);
			page.setAlbumPageName("");
			page.addAudioViews(AlbumPageAudioStore.instance().getAudios(_albumName, pageNo, _albumId));
			page.addImageViews(AlbumPageImageStore.instance().getImages(_albumName, pageNo, _albumId,false));
			page.addTextViews(AlbumPageTextStore.instance().getTexts(_albumName, pageNo, _albumId));
			page.addVideoViews(AlbumPageVideoStore.instance().getVideos(_albumName, pageNo, _albumId));
			
			_album.addAlbumPage(page);
		}
	}
	
	private void prepareFromExistAlbum()
	{
		final Handler handler = new Handler()
		{
			public void handleMessage(Message msg)
			{
				_album = AlbumStore.instance().getAlbum(_albumId);
				
				refreshAlbum();
				
				if(_album.getPagecount()==0)
				{
					addNewAlbumPage();
				}
				
				_currentPageIdx = 0;
				
				showAlbumPage(_currentPageIdx);
				
				_progress_dialoag.dismiss();
			}
		};
		
		new Thread(new Runnable(){
			@Override
			public void run()
			{
				handler.sendEmptyMessage(0);
			}
		}).start();
		
		
		
		
		
//		_album = AlbumStore.instance().getAlbum(_albumId);
//		
//		List<HandDraw> handDraws = AlbumPageHandDrawStore.instance().getHandDrawsByAlbumId(_albumId); 
//		
//		//Every AlbumPage contains one handDraw
//		for(HandDraw handDraw : handDraws)
//		{
//			String pageNo = handDraw.getPageNo();
//			
//			AlbumPage page = new AlbumPage();
//			page.setHandDraw(handDraw);
//			page.setAlbumId(_albumId);
//			page.setAlbumName(_albumName);
//			page.setPageNo(pageNo);
//			page.setAlbumPageName("");
//			page.addAudioViews(AlbumPageAudioStore.instance().getAudios(_albumName, pageNo, _albumId));
//			page.addImageViews(AlbumPageImageStore.instance().getImages(_albumName, pageNo, _albumId,false));
//			page.addTextViews(AlbumPageTextStore.instance().getTexts(_albumName, pageNo, _albumId));
//			page.addVideoViews(AlbumPageVideoStore.instance().getVideos(_albumName, pageNo, _albumId));
//			
//			_album.addAlbumPage(page);
//		}
//		
//		if(_album.getPagecount()==0)
//		{
//			addNewAlbumPage();
//		}
//		
//		_currentPageIdx = 0;
//		
//		showAlbumPage(_currentPageIdx);
	}
	
	private void adjustPageSwitchIcon()
	{
		if(_current_mode == MODE_VIEW)
		{
			_btnNextPage.setVisibility(View.INVISIBLE);
			_btnPreviousPage.setVisibility(View.INVISIBLE);
		}
		else
		{
			_btnNextPage.setVisibility(View.VISIBLE);
			_btnPreviousPage.setVisibility(View.VISIBLE);
			
			if(_currentPageIdx == 0)
			{
				_btnPreviousPage.setVisibility(View.INVISIBLE);
			}
			if(_currentPageIdx > 0 && _currentPageIdx<_album.getPagecount()-1)
			{
				_btnNextPage.setVisibility(View.VISIBLE);
				_btnPreviousPage.setVisibility(View.VISIBLE);
			}
		}
	}
	
	private void createNewAlbum()
	{
		_albumId = "MoPostIt_"+System.currentTimeMillis()+Math.random()*999;
		
		_album = new Album();
		_album.setAlbumName(_albumName);
		_album.setCreateDate(_dateFormatter.format(new Date()));
		_album.setAlbumId(_albumId);
		
		String albumRootPath = AppPreference.instance().getAppRootFolder()+_albumName;
		boolean resultMkdir = new File(albumRootPath).mkdir();
		
		_album.setAlbumRootPath(albumRootPath);
		
		Log.d("createNewAlbum", "Create New Album folder : "+resultMkdir);
		
		AlbumStore.instance().insertAlbum(_album);
	}
	
	private void addNewAlbumPage()
	{
		_noteArea.removeAllViews();
		
		_currentAlbumPageView = null;
		_currentAlbumPageView = new AlbumPageView(this);
		_currentAlbumPageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		_currentAlbumPageView.setBackgroundColor(Color.TRANSPARENT);
		_noteArea.addView(_currentAlbumPageView);
		
		_album.addAlbumPage(new AlbumPage());
		
		//Create PageNo Folder
		String page_folder = _album.getAlbumRootPath()+File.separator+"Page"+_currentPageIdx;
		new File(page_folder).mkdir();
	}
	
	private static final int NEXT_PAGE 			= 0x100;
	private static final int PREVIOUS_PAGE 		= 0x101;
	private static final int PAGE_JUMP	 		= 0x103;
	private static final int CHANGE_TO_VIEW_MODE = 0x102;
	
	private Handler _handlerAfterSave = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch(msg.what)
			{
				case CHANGE_TO_VIEW_MODE:
					
//					showAlbumPage(_currentPageIdx);
					adjustToolBar();
					_currentAlbumPageView.disableEdit();
					new MomoToast(PostItEditActivity.this,getString(R.string.tip_mode_view)).show();
					break;
			
				case NEXT_PAGE:
					
					_currentAlbumPageView.removeAllViews();
					_noteArea.removeAllViews();
					
					_currentPageIdx++;
					
					if(_currentPageIdx > _album.getPagecount()-1)
					{
						//Last Page, add new page
						addNewAlbumPage();
					}
					else
					{
						//Not last, get from Album, Restore from album
						showAlbumPage(_currentPageIdx);
					}
					
					_noteArea.startAnimation(AnimationUtils.loadAnimation(PostItEditActivity.this, R.anim.slide_from_left_to_right));
					
					break;
					
				case PREVIOUS_PAGE:
					
					_currentAlbumPageView.removeAllViews();
					_noteArea.removeAllViews();
					
					_currentPageIdx--;
					
					//Restore from AlbumPage
					showAlbumPage(_currentPageIdx);
					
					_noteArea.startAnimation(AnimationUtils.loadAnimation(PostItEditActivity.this, R.anim.slide_from_right_to_left));
					
					break;
					
				case PAGE_JUMP:
					
					_currentPageIdx = (Integer) msg.obj;
				
					showAlbumPage(_currentPageIdx);
					
					_noteArea.startAnimation(AnimationUtils.loadAnimation(PostItEditActivity.this,android.R.anim.fade_in));
			}
			
			adjustPageSwitchIcon();
			adjustToolBar();
			
			switch(_current_mode)
			{
				case MODE_EDIT:
					disableViewModeGesture();
					break;
					
				case MODE_VIEW:
					enableViewModeGesture();
					break;
					
				case MODE_NEW:
					disableViewModeGesture();
					break;
			}
			
			adjustToolBar();
		}
	};
	
	private void doSavePage(final int action, final int jumpToPageIndex)
	{
		_progress_dialoag = ProgressDialog.show(this, "", getString(R.string.tip_saving));
		
		new Thread(new Runnable(){
			public void run()
			{
				saveCurrentAlbumPage(action == CHANGE_TO_VIEW_MODE?false:true);
				
				Message msg = new Message();
				msg.what = action;
				msg.obj  = jumpToPageIndex;
				
				_handlerAfterSave.sendMessage(msg);
				
				_progress_dialoag.dismiss();
			}
		}).start();
	}
	
	private void showAlbumPage(final int pageIdx)
	{
		_noteArea.removeAllViews();
		
		if(_currentAlbumPageView!=null)
		{ 
			_currentAlbumPageView.stopAllPlayer();
			_currentAlbumPageView.removeAllViews();
			_currentAlbumPageView.recycleAllBitmap();
		}
		
		_currentAlbumPageView = null;
		_currentAlbumPageView = new AlbumPageView(PostItEditActivity.this);
		_currentAlbumPageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		_currentAlbumPageView.setBackgroundColor(Color.TRANSPARENT);
		_currentAlbumPageView.removeAllViews();
		
		if(_current_mode == MODE_VIEW)
		{
			_currentAlbumPageView.getHandDrawView().disablePaint();
		}
		
		AlbumPage page = _album.getAlbumPage(pageIdx);
		
		switch(_current_mode)
		{
			case MODE_EDIT:
				_currentAlbumPageView.restoreForEdit(page, PostItEditActivity.this);
				break;
				
			case MODE_VIEW:
				_currentAlbumPageView.restoreForView(page);
				break;
				
			case MODE_NEW:
				_currentAlbumPageView.restoreForEdit(page, PostItEditActivity.this);
				break;
		}
		
		_noteArea.addView(_currentAlbumPageView);
	}
	
	private void doNextPage()
	{
		if(_current_mode == MODE_VIEW)
		{
			if(_currentPageIdx == _album.getPagecount()-1)
			{
				VibrateUtil.doVibrate(100);
				new MomoToast(this,getString(_album.getPagecount()==1?R.string.tip_only_one_page:R.string.tip_last_page)).show();
				return;
			}
			
			_currentPageIdx++;
			
			showAlbumPage(_currentPageIdx);

			adjustPageSwitchIcon();
			adjustToolBar();
			
			_noteArea.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_from_left_to_right));
		}
		else
		{
			doSavePage(NEXT_PAGE,-1);
		}
	}
	
	private void doPreviousPage()
	{
		if(_currentPageIdx ==0)
		{
			VibrateUtil.doVibrate(100);
			new MomoToast(this,getString(_album.getPagecount()==1?R.string.tip_only_one_page:R.string.tip_first_page)).show();
			return;
		}
		
		if(_current_mode == MODE_VIEW)
		{
			_currentPageIdx--;
			
			//Restore from AlbumPage
			showAlbumPage(_currentPageIdx);

			adjustPageSwitchIcon();
			adjustToolBar();
			
			_noteArea.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_from_right_to_left));
		}
		else
		{
			doSavePage(PREVIOUS_PAGE,-1);
		}
	}
	
	class MomoTextViewColorChange implements ColorSelectListener
	{
		@Override
		public void onColorSelected(int color, Dialog sourceDialog,
				View targetView)
		{
			sourceDialog.dismiss();
			
			((MomoTextView)targetView).setTextColor(color);
		}
	}
	
	@Override
	public void onNotePasteItemClicked(View view)
	{
//		_currentAlbumPageView.getHandDrawView().disablePaint();
		_currentAlbumPageView.changeFocusEditView(view);
	}

	@Override
	public void onMomoTextViewColorChange(View view)
	{
		DialogQuickColorPicker dialog = new DialogQuickColorPicker
		(
			this,
			new MomoTextViewColorChange()
		);

		dialog.setTargetView(view);
		dialog.show();
	}

	@Override
	public void onMomoTextViewEditText(final View view)
	{ 
		Handler edit_handler = new Handler()
		{
			public void handleMessage(Message msg)
			{
				switch(msg.what)
				{
					case DialogEditName.ON_NAME_EDITED:
						
						((MomoTextView)view).setText((String)msg.obj);
						
						break;
				}
			}
		};
		
		new DialogEditName(this,edit_handler,((MomoTextView)view).getText()).show();
	}
	
	@Override
	public void onMomoTextViewTextSizeChange(int size, View view)
	{
		if(view instanceof MomoTextView)
		{
			((MomoTextView)view).setTextSize(size);
		}
	}

	@Override
	public void onViewDelete(View view)
	{
		_currentAlbumPageView.removeViewInLayout(view);
		_currentAlbumPageView.removeView(view);
		_currentAlbumPageView.postInvalidate();
	}
	
	private Handler _addTextHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch(msg.what)
			{
				case DialogEditName.ON_NAME_EDITED:
					
					String text = (String)msg.obj;
					
					MomoTextView tx_view = new MomoTextView(PostItEditActivity.this);
					tx_view.setTextColor(Color.BLACK);
					tx_view.setText(text);
					tx_view.setAlbumPageViewListner(PostItEditActivity.this);
					
					FrameLayout.LayoutParams params = new FrameLayout.LayoutParams
							(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					
					if(Build.VERSION.SDK_INT <= 10 )
					{
						params = new FrameLayout.LayoutParams(200, 120);
					}
					 
					params.gravity = Gravity.CENTER;
					tx_view.setLayoutParams(params);
					tx_view.toEditMode();
					
					_currentAlbumPageView.addTextView(tx_view);
					_currentAlbumPageView.addView(tx_view);
					
					Log.e("addtext", ""+tx_view.getLayoutParams().width);
					
					if(AppPreference.instance().isFirstTimeAddText())
					{
						Bundle bundle = new Bundle();
						bundle.putInt("type", UserGuideActivity.TYPE_ADD_TEXT_GUIDE);
						bundle.putInt("toolBarHeight",_toolArea.getHeight());
						
						Intent intent = new Intent();
						intent.setClass(PostItEditActivity.this, UserGuideActivity.class);
						intent.putExtras(bundle);
						startActivity(intent);
						AppPreference.instance().isFirstTimeAddText(false);
					}
					
					break;
			}
		}
	};
	
	class ToolLongClicklistenre implements OnLongClickListener
	{
		@Override
		public boolean onLongClick(View v) 
		{
			VibrateUtil.doVibrate(100);
			
			switch(v.getId())
			{
				case R.id.edit_btn_share:
					
					new MomoToast(PostItEditActivity.this,getString(R.string.tip_share)).show();
					break;
					
				case R.id.edit_btn_add_note_object:
					new MomoToast(PostItEditActivity.this,getString(R.string.tip_add_note_object)).show();
					break;
					
				case R.id.edit_btn_draw:
					new MomoToast(PostItEditActivity.this,getString(R.string.tip_hand_draw)).show();
					break;
					
				case R.id.edit_btn_next_page:
					
					if(_current_mode!=MODE_VIEW && _currentPageIdx == _album.getPagecount()-1)
					{
						new MomoToast(PostItEditActivity.this,getString(R.string.tip_add_page)).show();
					}
					else
					{
						new MomoToast(PostItEditActivity.this,getString(R.string.tip_next_page)).show();
					}
					
					break;
					
				case R.id.edit_previous_page:
					new MomoToast(PostItEditActivity.this,getString(R.string.tip_previous_page)).show();
					break;
					
				case R.id.edit_btn_view:
					new MomoToast(PostItEditActivity.this,getString(R.string.tip_to_mode_view)).show();
					break;
					
				case R.id.edit_btn_edit:
					new MomoToast(PostItEditActivity.this,getString(R.string.tip_to_mode_edit)).show();
					break;
					
				case R.id.edit_btn_del_page:
					new MomoToast(PostItEditActivity.this,getString(R.string.tip_delete_page)).show();
					break;
			}
			
			return true;
		}
		
	}
	
	class ToolClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			// To prevent continuously click by user
			if (ErrorProtector.instance().isCountingClickTime())
			{
				return;
			} else
			{
				ErrorProtector.instance().clickStart();
			}
			
			_currentAlbumPageView.deSelectAllViews();
			
			switch(v.getId())
			{
				case R.id.edit_btn_share:
					
					doShareNotePaste();
					
					break;
					
				case R.id.edit_btn_add_note_object:

					PopupInsertNoteObject.getInsertObjectPopup(PostItEditActivity.this, PostItEditActivity.this, v).show();
					 
					break;
					
				case R.id.edit_btn_draw:

					_quickActionDrawSetting = PopupDrawSetting.getDrawSettingPopup
						(
							PostItEditActivity.this, 
							v,
							_currentAlbumPageView
						);
					_quickActionDrawSetting.show();
					
					break;
					
				case R.id.edit_btn_next_page:
					doNextPage();
					break;
					
				case R.id.edit_previous_page:
					doPreviousPage();
					break;
					
				case R.id.edit_btn_view:
					changeMode(MODE_VIEW);
					break;
					
				case R.id.edit_btn_edit:
					changeMode(MODE_EDIT);
					break;
					
				case R.id.edit_btn_del_page:
					deletePage();
					break;
					
				case R.id.clip_web_btn_start_clip:
					
					if(_pageSwitchGallery.getVisibility()==View.GONE)
					{
						initPageSwitchGallery();
						_pageSwitchGallery.setVisibility(View.VISIBLE);
						_pageSwitchGallery.setSelection(_currentPageIdx);
						_pageSwitchGallery.startAnimation(AnimationUtils.loadAnimation(PostItEditActivity.this, android.R.anim.fade_in));
					}
					else
					{
						_pageSwitchGallery.setVisibility(View.GONE);
						_pageSwitchGallery.startAnimation(AnimationUtils.loadAnimation(PostItEditActivity.this, android.R.anim.fade_out));
					}
					
					break;
			} 
		}
		
	}
	
	private void initPageSwitchGallery()
	{
		PageThumbAdapter thumbAdapter = new PageThumbAdapter(this,_album);
		
		_pageSwitchGallery.setAdapter(thumbAdapter);
		_pageSwitchGallery.setOnItemClickListener
		(
			new OnItemClickListener()
			{
				@SuppressWarnings("rawtypes")
				public void onItemClick(AdapterView parent, View view, int position, long id) 
				{ 
					if(position == _currentPageIdx)
						return;
					
					_pageSwitchGallery.setVisibility(View.GONE);
					_pageSwitchGallery.startAnimation(AnimationUtils.loadAnimation(PostItEditActivity.this, android.R.anim.fade_out));
				
					if(_current_mode == MODE_VIEW)
					{
						_currentPageIdx = position;
						
						showAlbumPage(_currentPageIdx);
						  
						_noteArea.startAnimation
						(
							AnimationUtils.loadAnimation(
									PostItEditActivity.this, 
									android.R.anim.fade_in));
						
						adjustPageSwitchIcon();
						adjustToolBar();
					}
					else
					{
						doSavePage(PAGE_JUMP,position);
					}
				}
			}
		);
	}
	
	private void doDeletePage()
	{
		_progress_dialoag = ProgressDialog.show
				(
					PostItEditActivity.this,"",getString(R.string.tip_deleting_page)
				);
		
		final Handler handler = new Handler()
		{
			public void handleMessage(Message msg)
			{
				_currentAlbumPageView.removeAllViews();
				
				int deleteIndex = _currentPageIdx;
				int pageCountBeforeDelete = _album.getPagecount();
				
				List<Integer> needToUpdateIndex = new ArrayList<Integer>();
				
				//refresh index
				if(deleteIndex==0)
				{
					//All index -1
					for(int i=1; i<pageCountBeforeDelete; i++)
					{
						needToUpdateIndex.add(i);
					}
				}
				else if(deleteIndex == pageCountBeforeDelete-1)
				{
					//last do nothing
				}
				else
				{
					//update index behind current
					for(int i=deleteIndex+1; i<pageCountBeforeDelete; i++)
					{
						needToUpdateIndex.add(i);
					}
				}
				
				boolean isNext = true;
				
				_album.removePage(_currentPageIdx);
				
				if(_currentPageIdx > _album.getPagecount()-1)
				{
					_currentPageIdx = _album.getPagecount()-1;
					isNext = false;
				}
				
				File[] pageFolders = new File(_album.getAlbumRootPath()).listFiles();
				
				//update index
				for(Integer orgIndex : needToUpdateIndex)
				{
					//Update SQLite
					AlbumPageAudioStore.instance().update(_albumId, orgIndex, orgIndex-1);
					AlbumPageHandDrawStore.instance().update
					(
						_albumId, orgIndex, orgIndex-1,
						_album.getAlbumRootPath()+File.separator+"Page"+(orgIndex-1)+File.separator+"handDraw.draw"
					);
					AlbumPageImageStore.instance().update(_albumId, orgIndex, orgIndex-1);
					AlbumPageTextStore.instance().update(_albumId, orgIndex, orgIndex-1);
					AlbumPageVideoStore.instance().update(_albumId, orgIndex, orgIndex-1);
					
					//Update Folder name
					for(File pageFolder : pageFolders)
					{
						if(pageFolder.getName().equals("Page"+orgIndex))
						{
							String newFolderName ="Page"+(orgIndex-1);
							
							pageFolder.renameTo(new File(pageFolder.getParentFile(),newFolderName));
							
							break;
						}
					}
				}
				
				refreshAlbum();
				
				showAlbumPage(_currentPageIdx);
				
				if(isNext)
				{ 
					_noteArea.startAnimation(
							AnimationUtils.loadAnimation(
									PostItEditActivity.this, 
									R.anim.slide_from_left_to_right));
					
				} 
				else
				{
					_noteArea.startAnimation(
							AnimationUtils.loadAnimation(
									PostItEditActivity.this, 
									R.anim.slide_from_right_to_left));
				}
				
				
				
				adjustPageSwitchIcon();
				adjustToolBar();
				
				_progress_dialoag.dismiss();
			}
		};
		
		new Thread(new Runnable(){
			public void run()
			{
				//Delete SQLite
				AlbumPageAudioStore.instance().deleteAudioOfPage(_albumId, _currentPageIdx);
				AlbumPageHandDrawStore.instance().deleteHandDrawOfPage(_albumId, _currentPageIdx);
				AlbumPageImageStore.instance().deleteIamgesOfPage(_albumId, _currentPageIdx);
				AlbumPageTextStore.instance().deleteTextOfPage(_albumId, _currentPageIdx);
				AlbumPageVideoStore.instance().deleteVideoOfPage(_albumId, _currentPageIdx);
				
				//Delete Folder
				String page_folder = _album.getAlbumRootPath()+File.separator+"Page"+_currentPageIdx + File.separator;
				
				if(new File(page_folder).exists())
				{
					FileUtil.deleteForlder(new File(page_folder));
				}
				
				handler.sendEmptyMessage(0);
			}
		}).start();
	}
	
	private void deletePage()
	{
		_dialog_delete_page_comfirm = new DialogPositiveNagative(this);
		_dialog_delete_page_comfirm.setMessage(getString(R.string.conform_delete_page));
		_dialog_delete_page_comfirm.setPositiveOnClickListener
    	(
    		new OnClickListener()
    		{
				@Override
				public void onClick(View v)
				{
					_dialog_delete_page_comfirm.dismiss();
					
					_currentAlbumPageView.stopAllPlayer();
					
					doDeletePage();
				}
    			
    		}
    	);
		_dialog_delete_page_comfirm.setNagtiveOnClickListener
    	(
    		new OnClickListener()
    		{
				@Override
				public void onClick(View v)
				{
					_dialog_delete_page_comfirm.dismiss();
				}
    		}
    	);
		_dialog_delete_page_comfirm.show();
	}
	
	class ViewModePageSwitchGesture extends SimpleOnGestureListener
	{
		public static final int SWIPE_MIN_DISTANCE = 100;
		public static final int SWIPE_MAX_DISTANCE = 2500;
		public static final int SWIPE_THRESHOLD_VELOCITY = 30;
		
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY)
		{ 
			try
			{
				if(_pageSwitchGallery.getVisibility()==View.VISIBLE)
					return false;
				
				if (Math.abs(e1.getX() - e2.getX()) > SWIPE_MAX_DISTANCE)
					return false;

				if ((e2.getX() - e1.getX()) > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
				{
					// →
					doPreviousPage();
					
					System.gc();

					return true;
				} else if ((e1.getX() - e2.getX()) > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
				{
					// ←
					doNextPage();
					System.gc();

					return true;
				}

			} catch (Exception e)
			{
				Log.e("ViewModePageSwitchGesture", e.toString());
			}

			return false;
		}

		@Override
		public boolean onDoubleTap(MotionEvent e)
		{
			return false;
		}

	}

	@Override
	public void onDrawNormalSelected()
	{
		_currentAlbumPageView.getHandDrawView().enableNormal();
	}

	@Override
	public void onDrawHighlightSelected()
	{
		_currentAlbumPageView.getHandDrawView().enableHighlight();
	}

	@Override
	public void onEraserSelected()
	{
		_currentAlbumPageView.getHandDrawView().enableEraser();
	}

	@Override
	public void onDrawNormalStrokeWidthChange(int strokeWidth)
	{
		_currentAlbumPageView.getHandDrawView().setNormalStrokeWidth(strokeWidth);
		_currentAlbumPageView.getHandDrawView().enableNormal();
	}

	@Override
	public void onDrawNormalColorChange(int color)
	{
		_currentAlbumPageView.getHandDrawView().setNormalColor(color);
		_currentAlbumPageView.getHandDrawView().enableNormal();
	}

	@Override
	public void onDrawHighlightStrokeWidthChange(int strokeWidth)
	{
		_currentAlbumPageView.getHandDrawView().setHighlightStrokeWidth(strokeWidth);
		_currentAlbumPageView.getHandDrawView().enableHighlight();
	}
	
	@Override
	public void onDone() 
	{
		if(_quickActionDrawSetting!=null)
		{
			_quickActionDrawSetting.dismiss();
		}
	}

	@Override
	public void onDrawHighlightColorChange(int color)
	{
		_currentAlbumPageView.getHandDrawView().setHighlightColor(color);
		_currentAlbumPageView.getHandDrawView().enableHighlight();
	}

	@Override
	public void onDrawHighlightAlphaChange(int alpha)
	{
		_currentAlbumPageView.getHandDrawView().setHighlightPaintAlpha(alpha);
		_currentAlbumPageView.getHandDrawView().enableHighlight();
	}

	@Override
	public void onEraserStrokeWidthChange(int strokeWidth)
	{
		_currentAlbumPageView.getHandDrawView().setEraserStrokeWidth(strokeWidth);
		_currentAlbumPageView.getHandDrawView().enableEraser();
	}
	
	@Override
	public void onTakePhoto()
	{
		if(isFreeVersion)
		{
			if(_currentAlbumPageView.getAllImageViews().size()>=FREE_VERSION_MAX_IMAGE)
			{
				_dialog_free_edition.setMessage(getString(R.string.tip_free_limit_image));
				_dialog_free_edition.show();
				return;
			}
		}
		
		
		//define the file-name to save photo taken by Camera activity
		String fileName = "new-photo-name.jpg";
		//create parameters for Intent with filename
		ContentValues values = new ContentValues();
		values.put(MediaStore.Images.Media.TITLE, fileName);
		values.put(MediaStore.Images.Media.DESCRIPTION,"Image capture by camera");
		//imageUri is the current activity attribute, define and save it for later usage (also in onSaveInstanceState)
		captureImageURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
		//create new Intent
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, captureImageURI);
		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
		startActivityForResult(intent, ACTION_CAPTURE_IMAGE);
		
	}

	@Override
	public void onRecordvideo()
	{
		if(isFreeVersion)
		{
			if(_currentAlbumPageView.getAllVideoViews().size()>=FREE_VERSION_MAX_MEDIA)
			{
				_dialog_free_edition.setMessage(getString(R.string.tip_free_limit_media));
				_dialog_free_edition.show();
				return;
			}
		}
		
		
		Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);  
		startActivityForResult(intent, ACTION_RECORD_VIDEO);
	}

	@Override
	public void onRecordAudio()
	{
		if(isFreeVersion)
		{
			if(_currentAlbumPageView.getAllAudioViews().size()>=FREE_VERSION_MAX_MEDIA)
			{
				_dialog_free_edition.setMessage(getString(R.string.tip_free_limit_media));
				_dialog_free_edition.show();
				return;
			}
		}
		
		Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);  
		startActivityForResult(intent, ACTION_RECORD_AUDIO);
	}

	@Override
	public void onImportImage()
	{
		if(isFreeVersion)
		{
			if(_currentAlbumPageView.getAllImageViews().size()>=FREE_VERSION_MAX_IMAGE)
			{
				_dialog_free_edition.setMessage(getString(R.string.tip_free_limit_image));
				_dialog_free_edition.show();
				return;
			}
		}
		
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);  
		intent.setType("image/*");
		startActivityForResult(intent, ACTION_LOAD_IMAGE);
	}

	@Override
	public void onImportVideo()
	{
		if(isFreeVersion)
		{
			if(_currentAlbumPageView.getAllVideoViews().size()>=FREE_VERSION_MAX_MEDIA)
			{
				_dialog_free_edition.setMessage(getString(R.string.tip_free_limit_media));
				_dialog_free_edition.show();
				return;
			}
		}
		
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);  
		intent.setType("video/*");
		startActivityForResult(intent, ACTION_LOAD_VIDEO);
	}

	@Override
	public void onImportAudio()
	{
		if(isFreeVersion)
		{
			if(_currentAlbumPageView.getAllAudioViews().size()>=FREE_VERSION_MAX_MEDIA)
			{
				_dialog_free_edition.setMessage(getString(R.string.tip_free_limit_media));
				_dialog_free_edition.show();
				return;
			}
		}
		
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);  
		intent.setType("audio/*");
		startActivityForResult(intent, ACTION_LOAD_AUDIO);
	}

	@Override
	public void onAddText() 
	{
		new DialogEditName(PostItEditActivity.this,_addTextHandler,"").show();
	}

	@Override
	public void onWebContentClip()
	{
		if(!NetworkUtil.hasConnectionExist())
		{
			new MomoToast(this,getString(R.string.tip_no_internet_connection)).show();
			
			return;
		}
		
		Intent intent = new Intent();
		intent.setClass(this, ClipWebContent.class);
		startActivityForResult(intent, ACTION_CLIP_WEB_CONTENT);
	}

}
