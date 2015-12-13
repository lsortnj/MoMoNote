package mo.pay.post_it.widget;

import java.io.File;
import java.io.FileInputStream;

import mo.pay.post_it.defs.ActivityRequestCode;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class EmbaddedVideoPlayer extends SurfaceView implements Callback,
		OnPreparedListener,OnCompletionListener, OnErrorListener
{

	private String 			_file_path 		= "";
	private SurfaceHolder 	_holder;
	private MediaPlayer 	_player 		= new MediaPlayer();
	private Activity 		_activity;
	private boolean			_is_user_click	= false;
	private Bitmap			_play_icon		= null;
	private IPlayerHandler  _handler  		= null;

	public EmbaddedVideoPlayer(Activity context)
	{
		super(context); 

		_activity = context;
	}

	public EmbaddedVideoPlayer(Activity context, String filePath, LayoutParams param)
	{
		super(context);

		_file_path = filePath;

		_activity = context;

		this.setBackgroundColor(Color.BLACK);
		
		this.setLayoutParams(param); 
//		this.setOnTouchListener(this);
  		this.setZOrderOnTop(true);
		

		_holder = this.getHolder();
		_holder.addCallback(this);
		_holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	       
		_player.setOnPreparedListener(this);
		_player.setOnErrorListener(this);
		_player.setOnCompletionListener(this);
		
		this.requestFocus();
	}
	
	private void doPrepare()
	{
		if (_player.isPlaying()) {  
			_player.stop();
			_player.reset();  
        } 
        try 
        {  
        	if( Build.VERSION.SDK_INT==VERSION_CODES.FROYO )
        	{
        		toFullScreen();
        	}
        	else
        	{
        		_player.reset();
        		_player.setDisplay(_holder);
            	
            	FileInputStream fi = new FileInputStream(new File(_file_path));
            	_player.setDataSource(fi.getFD());
        		
            	_is_user_click = true;  
            	_player.setAudioStreamType(AudioManager.STREAM_MUSIC);

            	_player.prepare();
        	}
        } catch (Exception e) {  
            e.printStackTrace();  
        }
	}
	
	public void setPlayerHandler(IPlayerHandler handler)
	{
		_handler = handler;
	}
	
	public void startPlay()
	{
		doPrepare();
	}
	
	public void setPlayIcon(Bitmap icon)
	{
		_play_icon = icon;
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height)
	{
		// TODO Auto-generated method stub
	}

	public void toFullScreen()
	{
		int now_position = -1;

		if (_player.isPlaying())
		{
			now_position = _player.getCurrentPosition();
		}

		Intent intent = new Intent(_activity, KHVideoPlayerFullScreen.class);

		Bundle bundle = new Bundle();
		bundle.putString("video_path", _file_path);
		bundle.putInt("played_position", now_position);
		// bundle.putInt("page_index", value)

		intent.putExtras(bundle);

		stopPlay();
		
		_activity.startActivityForResult(intent,ActivityRequestCode.VIDEO_PLAYER_FULL_SCREEN);
	}

	public boolean isPlaying()
	{
		return _player.isPlaying();
	}
	
	public void stopPlay()
	{
		if (_player.isPlaying())
		{
			_player.stop();
			_player.reset();
			_is_user_click = false;
			invalidate();
			
			this.setVisibility(View.GONE);
			this.setVisibility(View.VISIBLE);
		}
	}

	public void setVideoPath(String path)
	{
		_file_path = path;
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onCompletion(MediaPlayer mp)
	{
		try
		{
			_player.reset();
			_is_user_click = false;
			invalidate();
			this.setVisibility(View.GONE);
			this.setVisibility(View.VISIBLE);
			
			if(_handler != null)
			{
				_handler.onPlayCompleted();
			}
		} 
		catch (IllegalStateException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		
		if( _play_icon == null )
		{
			return;
		}
		
		Paint paint = new Paint();
		
		if( !_player.isPlaying() )
		{
	        canvas.drawBitmap(_play_icon, (getWidth()/2)-(_play_icon.getWidth()/2), (getHeight()/2)-(_play_icon.getHeight()/2), paint);
		}
		else
		{
			canvas.drawColor(Color.BLACK);
		}
		
	}
	
	@Override
	public void onPrepared(MediaPlayer mp)
	{
		Log.i("mediaPlayer", "onPrepared");
		
		if(_is_user_click)
		{
			Log.i("mediaPlayer", "onPrepared is user click!");
			
			mp.start();
			
			invalidate();
		}
		else
		{
			mp.start();
			mp.pause();
			
			invalidate();
		}
	}

}
