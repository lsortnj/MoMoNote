package mo.pay.post_it;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import mo.pay.post_it.R;
import mo.pay.post_it.popup.ActionItem;
import mo.pay.post_it.popup.QuickAction;
import mo.pay.post_it.store.AlbumPageAudioStore;
import mo.pay.post_it.store.AlbumPageHandDrawStore;
import mo.pay.post_it.store.AlbumPageImageStore;
import mo.pay.post_it.store.AlbumPageTextStore;
import mo.pay.post_it.store.AlbumPageVideoStore;
import mo.pay.post_it.store.AlbumStore;
import mo.pay.post_it.store.AppPreference;
import mo.pay.post_it.ui.DialogPinCode;
import mo.pay.post_it.ui.DialogPositiveNagative;
import mo.pay.post_it.ui.DialogSingleLineInput;
import mo.pay.post_it.userguide.UserGuideActivity;
import mo.pay.post_it.util.BookmarkUtil;
import mo.pay.post_it.util.DeviceInfoUtil;
import mo.pay.post_it.util.FileUtil;
import mo.pay.post_it.util.NetworkUtil;
import mo.pay.post_it.util.VibrateUtil;
import mo.pay.post_it_album.Album;
import mo.pay.post_it_album.AlbumViewItemAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;

public class MoMoPostItActivity extends Activity
{
	public static final String TAG = "MoMoPostItActivity" ;
	
	private GridView  				_albumGridview 				= null;
	private AlbumViewItemAdapter	_albumAdapter				= null;
	private QuickAction				_quickActionDrawSetting 	= null;
	private List<Album> 			_albums 					= null;
	private ProgressDialog 			_progress_dialoag			= null;
	private ProgressBar				_refreshProgress			= null;
	private View 					_btnNewAlbum				= null;
	  
	private Handler _newAlbumHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch(msg.what)
			{
				case DialogSingleLineInput.ON_INPUT_COMPLETED:
				
					if(AppPreference.instance().isFirstTimeCreateAlbum())
					{
						createNewAlbum((String)msg.obj);
					}
					else
					{
						toNewAlbum((String)msg.obj);
					}
				
					break;
			}
		}
	};
	
	private Handler _albumItemHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{ 
			refreshAlbum();
		}
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags
//        (
//        	WindowManager.LayoutParams.FLAG_FULLSCREEN, 
//            WindowManager.LayoutParams.FLAG_FULLSCREEN
//        );
		setContentView(R.layout.activity_mo_mo_post_it);setContentView(R.layout.activity_mo_mo_post_it);setContentView(R.layout.activity_mo_mo_post_it);
		
		constructResource();
		
		constructStore();
		
		_albums = AlbumStore.instance().getAllAlbum();
		
		_refreshProgress = (ProgressBar) findViewById(R.id.refreshProgress);
		
		_albumGridview = (GridView)findViewById(R.id.main_album_gridview);
		_albumAdapter  = new AlbumViewItemAdapter(this,_albums);
		_albumGridview.setAdapter(_albumAdapter);
		 
		_albumGridview.setOnItemClickListener(new AlbumViewClickListener());
		_albumGridview.setOnItemLongClickListener(new AlbumViewClickListener());
		
		_btnNewAlbum = findViewById(R.id.main_btn_new_album);
		_btnNewAlbum.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0)
			{
				new DialogSingleLineInput
				(
					MoMoPostItActivity.this,_newAlbumHandler,
					getString(R.string.title_input_album_name),""
				).show();
			}
		});
	}
	
	private void refreshAlbum()
	{
		_refreshProgress.setVisibility(View.VISIBLE);
		
		final Handler handler = new Handler()
		{
			public void handleMessage(Message msg)
			{
				_albumAdapter.notifyDataSetChanged();
				
				if(AppPreference.instance().isFirstLaunch())
				{
					RelativeLayout.LayoutParams params = (LayoutParams) _btnNewAlbum.getLayoutParams();
					Bundle bundle = new Bundle();
					bundle.putInt("type", UserGuideActivity.TYPE_FIRST_LANUCH_GUIDE);
					bundle.putInt("targetX", params.leftMargin);
					bundle.putInt("targetY", params.topMargin);
					bundle.putInt("targetW", params.width);
					bundle.putInt("targetH", params.height);
					
					Intent intent = new Intent();
					intent.setClass(MoMoPostItActivity.this, UserGuideActivity.class);
					intent.putExtras(bundle);
					startActivity(intent);
					AppPreference.instance().isFirstLaunch(false);
				}
				
				_refreshProgress.setVisibility(View.GONE);
			}
		};
		
		new Thread(new Runnable()
		{
			public void run()
			{
				_albums = AlbumStore.instance().getAllAlbum();
				_albumAdapter.setAlbums(_albums);
				
				handler.sendEmptyMessage(0);
			}
		}).start();
	}
	
	private void constructStore()
	{
		AlbumStore.construct(this);
		AlbumPageHandDrawStore.construct(this);
		AlbumPageVideoStore.construct(this);
		AlbumPageImageStore.construct(this);
		AlbumPageTextStore.construct(this);
		AlbumPageAudioStore.construct(this);
		AppPreference.construct(this);
	}
	
	private void toNewAlbum(String albumName)
	{
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putString("albumName", albumName);
		bundle.putInt("mode", PostItEditActivity.MODE_NEW);
		intent.putExtras(bundle);
		intent.setClass(MoMoPostItActivity.this,PostItEditActivity.class);
		startActivity(intent);
	}
	
	private void createNewAlbum(String albumName)
	{
		String 			 albumId 		= "MoPostIt_"+System.currentTimeMillis()+Math.random()*999;
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
		Album album = new Album();
		album.setAlbumName(albumName);
		album.setCreateDate(dateFormatter.format(new Date()));
		album.setAlbumId(albumId);
		
		String albumRootPath = AppPreference.instance().getAppRootFolder()+albumName;
		new File(albumRootPath).mkdir();
		  
		album.setAlbumRootPath(albumRootPath);
		
		AlbumStore.instance().insertAlbum(album);
		  
		refreshAlbum();
		
		Bundle bundle = new Bundle();
		bundle.putInt("type", UserGuideActivity.TYPE_ALBUM_MENU_GUIDE);
		bundle.putInt("toolAreaHeight", _btnNewAlbum.getHeight());
		
		Intent intent = new Intent();
		intent.setClass(this, UserGuideActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);
		
		AppPreference.instance().isFirstTimeCreateAlbum(false);
	}
	
	private void toViewMode(Album album)
	{
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putString("albumName", album.getAlbumName());
		bundle.putString("albumId", album.getAlbumId());
		bundle.putInt("mode", PostItEditActivity.MODE_VIEW);
		intent.putExtras(bundle);
		intent.setClass(MoMoPostItActivity.this,PostItEditActivity.class);
		startActivity(intent);
	}
	
	private void toEditMode(Album album)
	{
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putString("albumName", album.getAlbumName());
		bundle.putString("albumId", album.getAlbumId());
		bundle.putInt("mode", PostItEditActivity.MODE_EDIT);
		intent.putExtras(bundle);
		intent.setClass(MoMoPostItActivity.this,PostItEditActivity.class);
		startActivity(intent);
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		
		constructResource();
		constructStore();
		
		_albumItemHandler.sendEmptyMessage(0);
		
		System.gc();
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
	}
	
	@Override
	protected void onRestart()
	{
		super.onRestart();
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}

	private void constructResource()
	{
		AppPreference.construct(this);
		NetworkUtil.construct(this);
		DeviceInfoUtil.construct(this);
		VibrateUtil.construct(this);
		FileUtil.construce(this);
		BookmarkUtil.init(this);
	}
	
	private void deleteAlbum(Album album)
	{
		AlbumStore.instance().deleteAlbum(album);
		AlbumPageAudioStore.instance().deleteAllAudios(album.getAlbumId());
		AlbumPageHandDrawStore.instance().deleteAllHandDraw(album.getAlbumId());
		AlbumPageImageStore.instance().deleteAllImages(album.getAlbumId());
		AlbumPageTextStore.instance().deleteAllText(album.getAlbumId());
		AlbumPageVideoStore.instance().deleteAllVideo(album.getAlbumId());
		
		FileUtil.deleteForlder(new File(album.getAlbumRootPath()));
		
		_albumItemHandler.sendEmptyMessage(0);
	}
	
	private DialogPositiveNagative PNdialog = null;
	
	private Handler	_handlerPINcode = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch(msg.what)
			{
				case DialogPinCode.ON_PINCODE_SET:
					refreshAlbum();
					break;
					
				case DialogPinCode.ON_PINCODE_CANCELED:
					refreshAlbum();
					break;
					
				case DialogPinCode.ON_PINCODE_VERIFY:
					
					Album album = (Album) msg.obj;
					
					if(album != null) 
					{
						if(AppPreference.instance().isFirstTimeToEditMode())
						{
							toEditMode(album);
						}
						else
						{
							toViewMode(album);
						}
					}
					
					break;
			}
		}
	};
	
	private void doAlbumNameChange(final Album album, final String newAlbumName)
	{
		final Handler refreshHandler = new Handler()
		{
			public void handleMessage(Message msg)
			{
				refreshAlbum();
			}
		};
		  
		_progress_dialoag = ProgressDialog.show
				(
					this,
					"",
					getString(R.string.tip_please_wait)
				);
		
		new Thread(new Runnable(){
			public void run()
			{
				File   newAlbumRoot = new File(AppPreference.instance().getAppRootFolder() + newAlbumName);
				File   orgAlbumRoot = new File(album.getAlbumRootPath());
				String orgAlbumName = album.getAlbumName();
				
				album.setAlbumRootPath(newAlbumRoot.getAbsolutePath());
				album.setAlbumName(newAlbumName);
				
				//Update Store
				AlbumStore.instance().updateAlbumName(album);
				AlbumPageAudioStore.instance().albumNameChange(album, orgAlbumName);
				AlbumPageHandDrawStore.instance().albumNameChange(album, orgAlbumName);
				AlbumPageImageStore.instance().albumNameChange(album, orgAlbumName);
				AlbumPageTextStore.instance().albumNameChange(album);
				AlbumPageVideoStore.instance().albumNameChange(album, orgAlbumName);
				
				//Rename physical file
				if(orgAlbumRoot.exists() && orgAlbumRoot.isDirectory())
				{
					orgAlbumRoot.renameTo(newAlbumRoot);
				}
				
				refreshHandler.sendEmptyMessage(0);
				
				_progress_dialoag.dismiss();
			}
		}).start();
	}
	
	private QuickAction getAlbumEditPopup(View view, final Album album)
	{
		_quickActionDrawSetting = new QuickAction(view,QuickAction.TYPE_LIGHT);
		
		final Handler _handlerRename = new Handler()
		{
			public void handleMessage(Message msg)
			{
				switch(msg.what)
				{
					case DialogSingleLineInput.ON_INPUT_COMPLETED:
					
						doAlbumNameChange(album,(String)msg.obj);
						
						break;
				}
			}
		};
		
		ActionItem rename = new ActionItem();
		rename.setTitle(getString(R.string.text_rename));
		rename.setIcon(getResources().getDrawable(R.drawable.rename));
		rename.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v)
			{
				new DialogSingleLineInput
				(
					MoMoPostItActivity.this,_handlerRename,
					getString(R.string.title_input_album_name),""
				).show();
				
				_quickActionDrawSetting.dismiss();
			}
		});
		
		ActionItem lock = new ActionItem();
		lock.setTitle(getString(R.string.text_lock_album));
		lock.setIcon(getResources().getDrawable(R.drawable.lock));
		lock.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v)
			{
				new DialogPinCode
				(
					album,MoMoPostItActivity.this,
					DialogPinCode.MODE_LOCK_ALBUM,
					_handlerPINcode
				).show();
				
				_quickActionDrawSetting.dismiss();
			}
		});
		
		ActionItem unlock = new ActionItem();
		unlock.setTitle(getString(R.string.text_unlock_album));
		unlock.setIcon(getResources().getDrawable(R.drawable.unlock));
		unlock.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v)
			{
				new DialogPinCode
				(
					album,MoMoPostItActivity.this,
					DialogPinCode.MODE_CANCEL_LOCK_ALBUM,
					_handlerPINcode
				).show();
				
				_quickActionDrawSetting.dismiss();
			}
		});
		
		ActionItem delete = new ActionItem();
		delete.setTitle(getString(R.string.text_delete));
		delete.setIcon(getResources().getDrawable(R.drawable.delete));
		delete.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v)
			{
				VibrateUtil.doShortVibrate(3, 50);
				
				PNdialog = new DialogPositiveNagative(MoMoPostItActivity.this);
				PNdialog.setMessage(getString(R.string.confirm_delete));
				PNdialog.setPositiveOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v)
					{
						if(album.isLocked() && !album.getPassword().equals(""))
						{
							new DialogPinCode
							(
								album,MoMoPostItActivity.this,
								DialogPinCode.MODE_UNLOCK_TO_DELETE_ALBUM,
								_handlerPINcode
							).show();
						}
						else
						{
							deleteAlbum(album);
						}
						
						PNdialog.dismiss();
					}
				});
				PNdialog.setNagtiveOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v)
					{
						PNdialog.dismiss();
					}
				});
				PNdialog.show();
				
				_quickActionDrawSetting.dismiss();
			}
		});
		
		_quickActionDrawSetting.addActionItem(rename);
		if(album.isLocked() && !album.getPassword().equals(""))
		{
			_quickActionDrawSetting.addActionItem(unlock);
		}
		else
		{
			_quickActionDrawSetting.addActionItem(lock);
		}
		
		_quickActionDrawSetting.addActionItem(delete);
		
		return _quickActionDrawSetting;
	}
	
	class AlbumViewClickListener implements OnItemClickListener, OnItemLongClickListener
	{
		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View view,
				int position, long arg3)
		{
			if(_albumAdapter.getItem(position) != null)
			{
				VibrateUtil.doVibrate(100);
				//Show option dialog
				getAlbumEditPopup(view, (Album)_albumAdapter.getItem(position)).show();
			}
			
			return false;
		}

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3)
		{
			Album clickAlbum = (Album)_albumAdapter.getItem(position);
			
			if(clickAlbum != null)
			{
				if(clickAlbum.isLocked() && !clickAlbum.getPassword().equals(""))
				{
					new DialogPinCode
					(
						clickAlbum,MoMoPostItActivity.this,
						DialogPinCode.MODE_UNLOCK_TO_VIEW_ALBUM,
						_handlerPINcode
					).show();
				}
				else
				{
					if(AppPreference.instance().isFirstTimeToEditMode())
					{
						toEditMode((Album)_albumAdapter.getItem(position));
					}
					else
					{
						toViewMode((Album)_albumAdapter.getItem(position));
					}
				}
			}
		}
		
	}
}
