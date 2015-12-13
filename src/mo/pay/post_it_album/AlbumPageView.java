package mo.pay.post_it_album;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.widget.FrameLayout;

import mo.pay.post_it.bitmapPlugin.Util;
import mo.pay.post_it.logic.AlbumViewListener;
import mo.pay.post_it.store.AlbumPageAudioStore;
import mo.pay.post_it.store.AlbumPageImageStore;
import mo.pay.post_it.store.AlbumPageVideoStore;
import mo.pay.post_it.util.DeviceInfoUtil;
import mo.pay.post_it.widget.MomoAudioView;
import mo.pay.post_it.widget.MomoImageView;
import mo.pay.post_it.widget.MomoTextView;
import mo.pay.post_it.widget.MomoVideoView;
import mo.pay.post_it_handDraw.HandDrawView;

public class AlbumPageView extends FrameLayout
{
	public static final String TAG = "AlbumPageView";
	
	private Activity	_activity = null;

	private List<MomoTextView>	_texts 	= new ArrayList<MomoTextView>();
	private List<MomoImageView>	_images = new ArrayList<MomoImageView>();
	private List<MomoVideoView>	_videos = new ArrayList<MomoVideoView>();
	private List<MomoAudioView>	_audios = new ArrayList<MomoAudioView>();
	private HandDrawView		_handDraw = null;
	private boolean 			_isEdit	= false;
	
	public AlbumPageView(Activity activity)
	{
		super(activity);
		
		_activity = activity;
		
		_handDraw = new HandDrawView(_activity, this);
		_handDraw.setBackgroundColor(Color.parseColor("#01444444"));
		_handDraw.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		
		DisplayMetrics dm = new DisplayMetrics();
		_activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		 
		addView(_handDraw);
	}

	@Override
	public boolean onTouchEvent(MotionEvent rawEvent)
	{
		super.onTouchEvent(rawEvent);
		 
		return false;
	}
	
	public boolean isNeedToSave()
	{
		return _isEdit;
	}


	private Handler _handler = new Handler(){
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg)
		{
			Map<String,Object> map = (Map<String,Object>) msg.obj;
		
			doScreenCapture
			(
				(String)map.get("Path"),
				(Integer)map.get("ToolBarHeight"),
				(Boolean)map.get("IsThumb")
			);
		}
	};
	
	public void savePageScreenCapture
		(
			String savePath, 
			int toolBarHeight,
			boolean isThumb
		)
	{
		Map<String,Object> map = new HashMap<String,Object>();
 		
		map.put("Path", savePath);
		map.put("ToolBarHeight", toolBarHeight);
		map.put("IsThumb", isThumb);
		
		Message msg = new Message();
		msg.obj = map;
		
		_handler.sendMessage(msg);
	}
	
	private void doScreenCapture
		(
			String savePath, 
			int toolBarHeight,
			boolean isThumb
		)
	{
		FileOutputStream fout = null;
		
		if(new File(savePath).exists())
		{
			new File(savePath).delete();
		}
		
		Rect rectgle= new Rect();
		Window window= _activity.getWindow();
		window.getDecorView().getWindowVisibleDisplayFrame(rectgle);
		int StatusBarHeight= rectgle.top;
		
		try{fout = new FileOutputStream(savePath);} 
		catch (FileNotFoundException e){e.printStackTrace();}
	
		View v = getRootView();
		Bitmap bitmap		= null;
		Bitmap shareBitmap	= null;
		try
		{
			bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
	    	Canvas canvas = new Canvas(bitmap);
	    	v.draw(canvas);
	    	
	    	int width  = bitmap.getWidth();
	    	int height = bitmap.getHeight()-toolBarHeight-StatusBarHeight;
	    	
	    	shareBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);;
	    	
	    	canvas = new Canvas(shareBitmap);
	        canvas.drawBitmap(bitmap, 0, -(toolBarHeight+StatusBarHeight), new Paint());
	    	
	        if(isThumb)
	        {
	        	float scaleWidth = ((float) width/3) / width;
	    	    float scaleHeight = ((float) height/3) / height;
	    	     
	    	    Matrix matrix = new Matrix();
	    	    matrix.postScale(scaleWidth, scaleHeight);

	    	    shareBitmap = Bitmap.createBitmap(shareBitmap, 0, 0, width, height, matrix, false);
	    	}
	        shareBitmap.compress(CompressFormat.JPEG, 100, fout);
    		
    		bitmap.recycle();
    		shareBitmap.recycle();
		}
		catch(OutOfMemoryError oom){Log.e(TAG, "OOM Exception");}
		catch(Exception e){Log.e(TAG, "recycle share bitmap:"+e.toString());}
		finally
		{
			if(bitmap!=null)bitmap.recycle();
			if(shareBitmap!=null)shareBitmap.recycle();
		}
	}
	
	public HandDrawView getHandDrawView()
	{
		return _handDraw;
	}
	
	private void refreshZOrder()
	{
		removeAllViews();
		
		for(MomoImageView img : getAllImageViews())
		{
			addView(img);
		}
		
		for(MomoTextView txt : getAllTextViews())
		{
			addView(txt);
		}
		
		addView(_handDraw);
		
		for(MomoVideoView video : getAllVideoViews())
		{
			addView(video);
		}
		
		for(MomoAudioView audio : getAllAudioViews())
		{
			addView(audio);
		}
	}
	
	public void addTextViews(List<MomoTextView> textViews)
	{
		_texts.addAll(textViews);
		
		_isEdit = true;
	}
	
	public void addTextView(MomoTextView textView)
	{
		_texts.add(textView);
		_isEdit = true;
//		addView(textView);
	}
	
	public void addImageViews(List<MomoImageView> imageViews)
	{
		_images.addAll(imageViews);
		_isEdit = true;
	}
	
	public void addImageView(MomoImageView imageView)
	{
		_images.add(imageView);
		
		addView(imageView);
		_isEdit = true;
	}
	
	public List<MomoImageView> getAllImageViews()
	{
		return _images;
	}
	
	public void addVideoViews(List<MomoVideoView> videoViews)
	{
		_videos.addAll(videoViews);
	}
	
	public void addVideoView(MomoVideoView videoViews)
	{
		_videos.add(videoViews);
	}
	
	public List<MomoVideoView> getAllVideoViews()
	{
		return _videos;
	}
	
	public void addAudioViews(List<MomoAudioView> audioViews)
	{
		_audios.addAll(audioViews);
	}
	
	public void addAudioView(MomoAudioView audioViews)
	{
		_audios.add(audioViews);
	}
	
	public List<MomoAudioView> getAllAudioViews()
	{
		return _audios;
	}
	
	public List<MomoTextView> getAllTextViews()
	{
		return _texts;
	}
	
	public void performTouchBesidesHandDraw(MotionEvent e)
	{
		View view = null;
		FrameLayout.LayoutParams params = null;
		
		
		for(int i=getChildCount()-1; i>=0; i--)
		{
			view = getChildAt(i);
			params = (LayoutParams) view.getLayoutParams();
			
			//Check if User click view
			if(
				//Left
				params.leftMargin > e.getX() || 
				//Top
				params.topMargin > e.getY() || 
				//Bottom
				(params.topMargin + params.height) < e.getY() ||
				//Right
				(params.leftMargin + params.width) < e.getX() 
			  )
			{
				continue;
			}
			
			if(!(getChildAt(i) instanceof HandDrawView))
			{
				if(getChildAt(i).dispatchTouchEvent(e))
				{
					break;
				}
			}
		}
	}
	
	public void orientationChanged()
	{
		for(MomoImageView img : getAllImageViews())
		{
			img.refreshScreenResolution();
		}
		
		for(MomoTextView txt : getAllTextViews())
		{
			txt.refreshScreenResolution();
		}
		
		
		for(MomoVideoView video : getAllVideoViews())
		{
			video.refreshScreenResolution();
		}
		
		for(MomoAudioView audio : getAllAudioViews())
		{
			audio.refreshScreenResolution();
		}
	}
	
	public void restoreForView(AlbumPage albumPage)
	{
		removeAllViews();
		
		if(albumPage.getHandDraw()!=null && new File(albumPage.getHandDraw().getStorePath()).exists())
		{
			_handDraw.drawNote(albumPage.getHandDraw());
		}
		 
		addView(_handDraw);
		
		addVideoViews(albumPage.getVideos());
		addImageViews(albumPage.getImages());
		addTextViews(albumPage.getTexts());
		addAudioViews(albumPage.getAudios());
		
		for(MomoImageView img : getAllImageViews())
		{
			removeParentView(img);
			 
//			Bitmap bitmap  = Util.makeBitmap
//			(
//				-1, 
//				2*DeviceInfoUtil.getDeviceDisplayMetrics().widthPixels 
//				*DeviceInfoUtil.getDeviceDisplayMetrics().heightPixels,
//				Uri.fromFile(new File(img.getImagePath())), 
//				_activity.getContentResolver(), false
//			);
			
			Bitmap bitmap  = Util.makeBitmap
					(
						Util.IMAGE_MAX_WIDTH, 
						((DeviceInfoUtil.getDeviceDisplayMetrics().widthPixels 
						*DeviceInfoUtil.getDeviceDisplayMetrics().heightPixels))/2,
						Uri.fromFile(new File(img.getImagePath())), 
						_activity.getContentResolver(), false
					);
			
			img.setImageBitmap(bitmap);
			img.resetAllListener();
			addView(img);
			img.performRotate();
		}
		
		for(MomoTextView txt : getAllTextViews())
		{
			removeParentView(txt);
			txt.resetAllListener();
			addView(txt);
		}
		
		_handDraw.bringToFront();
		
		for(MomoVideoView video : getAllVideoViews())
		{
			removeParentView(video);
			video.resetAllListener();
			addView(video);
		}
		
		for(MomoAudioView audio : getAllAudioViews())
		{
			removeParentView(audio);
			audio.resetAllListener();
			addView(audio);
		}
	}
	
	public void enableEdit(AlbumViewListener listener)
	{
		for(MomoImageView img : getAllImageViews())
		{
			img.setAlbumPageViewListner(listener);
		}
		
		for(MomoTextView txt : getAllTextViews())
		{
			txt.setAlbumPageViewListner(listener);
		}
		
		for(MomoVideoView video : getAllVideoViews())
		{
			video.setAlbumPageViewListner(listener);
		}
		
		for(MomoAudioView audio : getAllAudioViews())
		{
			audio.setAlbumPageViewListner(listener);
		}
		
		_handDraw.enableNormal();
		
		_isEdit = false;
	}
	
	public void disableEdit()
	{
		for(MomoImageView img : getAllImageViews())
		{
			img.deSelect();
			img.resetAllListener();
		}
		
		for(MomoVideoView video : getAllVideoViews())
		{
			video.deSelect();
			video.resetAllListener();
		}
		
		for(MomoTextView txt : getAllTextViews())
		{
			txt.deSelect();
			txt.resetAllListener();
		}
		
		for(MomoAudioView audio : getAllAudioViews())
		{
			audio.deSelect();
			audio.resetAllListener();
		}
		
		_handDraw.disablePaint();
	}
	
	public void deSelectAllViews()
	{
		refreshZOrder();
		
		for(MomoImageView img : getAllImageViews())
		{
			img.deSelect();
		}
		
		_handDraw.resetTouchEvent();
		
		for(MomoVideoView video : getAllVideoViews())
		{
			video.deSelect();
		}
		
		for(MomoTextView txt : getAllTextViews())
		{
			txt.deSelect();
		}
		
		for(MomoAudioView audio : getAllAudioViews())
		{
			audio.deSelect();
		}
	}
	
	public void changeFocusEditView(View currentSelectView)
	{
//		refreshZOrder();
		
		for(MomoImageView img : getAllImageViews())
		{
			if(!currentSelectView.equals(img))
			{
				img.deSelect();
			}
		}
		
		for(MomoTextView txt : getAllTextViews())
		{
			if(!currentSelectView.equals(txt))
			{
				txt.deSelect();
			}
		}
		
		for(MomoVideoView video : getAllVideoViews())
		{
			if(!currentSelectView.equals(video))
			{
				video.deSelect();
			}
		}
		
		for(MomoAudioView audio : getAllAudioViews())
		{
			if(!currentSelectView.equals(audio))
			{
				audio.deSelect();
			}
		}
	}
	
	public void restoreForEdit(AlbumPage albumPage, AlbumViewListener listener)
	{
		removeAllViews();
		
		if(new File(albumPage.getHandDraw().getStorePath()).exists())
		{
			_handDraw.drawNote(albumPage.getHandDraw());
		}
		
		addView(_handDraw);
		
		addAudioViews(albumPage.getAudios());
		addVideoViews(albumPage.getVideos());
		addImageViews(albumPage.getImages());
		addTextViews(albumPage.getTexts());
		
		for(MomoImageView img : getAllImageViews())
		{
			removeParentView(img);
			
//			Bitmap bitmap  = Util.makeBitmap
//			(
//				-1, 
//				2*DeviceInfoUtil.getDeviceDisplayMetrics().widthPixels 
//				*DeviceInfoUtil.getDeviceDisplayMetrics().heightPixels,
//				Uri.fromFile(new File(img.getImagePath())), 
//				_activity.getContentResolver(), false
//			);
			
			Bitmap bitmap  = Util.makeBitmap
					(
						Util.IMAGE_MAX_WIDTH, 
						((DeviceInfoUtil.getDeviceDisplayMetrics().widthPixels 
						*DeviceInfoUtil.getDeviceDisplayMetrics().heightPixels))/2,
						Uri.fromFile(new File(img.getImagePath())), 
						_activity.getContentResolver(), false
					);
			
			img.setImageBitmap(bitmap);
			img.setAlbumPageViewListner(listener);
			addView(img);
			img.performRotate();
		}
		
		for(MomoTextView txt : getAllTextViews())
		{
			removeParentView(txt);
			txt.setAlbumPageViewListner(listener);
			super.addView(txt);
		}
		
		_handDraw.bringToFront();
		
		for(MomoVideoView video : getAllVideoViews())
		{
			removeParentView(video);
			video.setAlbumPageViewListner(listener);
			super.addView(video);
		}
		
		for(MomoAudioView audio : getAllAudioViews())
		{
			removeParentView(audio);
			audio.setAlbumPageViewListner(listener);
			super.addView(audio);
		}
		
		_isEdit = false;
	}
	
	private void removeParentView(View view)
	{
		if(view.getParent() != null)
		{
			ViewParent parentView = view.getParent();
			((ViewGroup)parentView).removeView(view);
		}
	}
	
	public void removeView(View view)
	{
		if(view instanceof MomoTextView)
		{
			_texts.remove(view);
		}
		else if(view instanceof MomoImageView)
		{
			AlbumPageImageStore.instance().deleteImage((MomoImageView)view);
			((MomoImageView)view).recycleBitmap();
			_images.remove(view);
		}
		else if(view instanceof MomoVideoView)
		{
			AlbumPageVideoStore.instance().deleteVideo((MomoVideoView)view);
			_videos.remove(view);
		}
		else if(view instanceof MomoAudioView)
		{
			AlbumPageAudioStore.instance().deleteAudio((MomoAudioView)view);
			_audios.remove(view);
		}
	}
	
	public void stopAllPlayer()
	{
		for(MomoVideoView video : getAllVideoViews())
		{
			video.stopPlay();
		}
		for(MomoAudioView audio : getAllAudioViews())
		{
			audio.stopPlay();
		}
	}
	
	public boolean isEmpty()
	{
		if(
			_images.size()==0 && _texts.size() ==0 && 
			_videos.size()==0 && _audios.size()==0 && 
			_handDraw.isDraw()
		  )
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public void recycleAllBitmap()
	{
		new Thread(new Runnable(){
			public void run()
			{
				try
				{
					if(_handDraw != null)
					{
						_handDraw.recycleHandDrawBitmap();
					}
					
					for(MomoImageView img : getAllImageViews())
					{
						img.recycleBitmap();
					}
				}catch(Exception e){}
			}
		}).start();
	}
}
