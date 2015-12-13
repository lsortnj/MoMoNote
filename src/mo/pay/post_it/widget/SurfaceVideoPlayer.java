package mo.pay.post_it.widget;

import mo.pay.post_it.R;
import mo.pay.post_it.logic.WrapMotionEvent;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;


public class SurfaceVideoPlayer extends FrameLayout implements IPlayerHandler
{
	public static final String TAG = "SurfaceVideoPlayer";
	
	private Activity 			_activity 				= null;
	private EmbaddedVideoPlayer _videoPlayer 			= null;
	private PlayerControlView	_playerControlView 		= null;
	private String				_videoPath      		= "";
	
	public SurfaceVideoPlayer(Activity activity)
	{
		super(activity);

		_activity = activity;
		
		init();
	}
	
	public SurfaceVideoPlayer(Activity activity, AttributeSet attrs)
	{
		super(activity, attrs);

		_activity = activity;
		
		init();
	}
	
	public SurfaceVideoPlayer(Activity activity, AttributeSet attrs, int defStyle)
	{
		super(activity, attrs, defStyle);

		_activity = activity;
		
		init();
	}
	
	public SurfaceVideoPlayer(Activity activity, String videoPath, LayoutParams parmas)
	{
		super(activity);

		_videoPath 	= videoPath;
		_activity 	= activity;
		
		init();
	}
	
	public void setVideoPath(String path)
	{
		_videoPath = path;
		_videoPlayer.setVideoPath(_videoPath);
	}
	
	private void init()
	{
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		
		_videoPlayer = new EmbaddedVideoPlayer(_activity,_videoPath,params);
		_videoPlayer.setPlayerHandler(this);
		_playerControlView = new PlayerControlView(_activity);
		_playerControlView.setLayoutParams(params);
		_playerControlView.setPlayerHandler(this);
		
		addView(_videoPlayer);
		addView(_playerControlView);
	}
	
	public void stopPlay()
	{
		if(_videoPlayer.isPlaying())
		{
			_videoPlayer.stopPlay();
			_playerControlView.drawPlayButton();
		}
	}
	
	@Override
	public void onPlayIconClick()
	{
		if(_videoPlayer.isPlaying())
		{
			_videoPlayer.stopPlay();
			_playerControlView.drawPlayButton();
		}
		else
		{
			_playerControlView.clearDraw();
			_videoPlayer.startPlay();
		}
	}

	@Override
	public void onPointerUp()
	{
		if(_videoPlayer.isPlaying())
		{
			_videoPlayer.toFullScreen();
		}
	}
	
	
	@Override
	public void onPlayCompleted()
	{
		_playerControlView.drawPlayButton();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	class PlayerControlView extends SurfaceView implements Callback,OnTouchListener
	{
		private boolean 		_is_multi_touch = false;
		private boolean 		_is_point_up	= false;
		private boolean 		_isDrawPlayIcon = false;
		private PointF 			start 			= new PointF();
		private float 			oldDist 		= 1f;
		private float 			newDist 		= 1f;
		
		private SurfaceHolder 	surfaceHolder;
		private IPlayerHandler  playerHandler;
		private Bitmap 			playIcon;
		
		
		public PlayerControlView(Context context)
		{
			super(context);
			init();
		}
		
		public PlayerControlView(Context context, AttributeSet attrs)
		{
			super(context, attrs);
			init();
		}

		public PlayerControlView(Context context, AttributeSet attrs,
				int defStyle)
		{
			super(context, attrs, defStyle);
			init();
		}
		
		public void setPlayerHandler(IPlayerHandler handler)
		{
			playerHandler = handler; 
		} 

		private void init()
		{
			surfaceHolder = getHolder();
			surfaceHolder.addCallback(this);
			surfaceHolder.setFormat(PixelFormat.TRANSPARENT);
			if( Build.VERSION.SDK_INT<=VERSION_CODES.GINGERBREAD_MR1 )
	    	{
				setZOrderOnTop(true);
	    	}
	        setOnTouchListener(this);
		}
		
		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height)
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder)
		{
			drawPlayButton();
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder)
		{
			// TODO Auto-generated method stub
			
		}
		
		@Override
	    protected void onSizeChanged(int w, int h, int oldw, int oldh) 
		{
	        super.onSizeChanged(w, h, oldw, oldh);
	        
	        if(_isDrawPlayIcon)
	        {
	        	drawPlayButton();
	        }
		}
		
		void clearDraw()
	    {
			_isDrawPlayIcon = false;
	        Canvas canvas = surfaceHolder.lockCanvas();
	        canvas.drawColor(Color.TRANSPARENT);
	        surfaceHolder.unlockCanvasAndPost(canvas);
//	        setVisibility(View.INVISIBLE);
	    }
		
		protected void drawPlayButton()
	    {
			_isDrawPlayIcon = true;
	        Canvas canvas = surfaceHolder.lockCanvas();
	        if(canvas == null)return;
	        canvas.drawColor(Color.TRANSPARENT);
	        playIcon = BitmapFactory.decodeResource(getResources(), R.drawable.btn_video_play);
	        canvas.drawBitmap
	        (
	        	playIcon, 
	        	(getWidth()/2)-(playIcon.getWidth()/2), 
	        	(getHeight()/2)-(playIcon.getHeight()/2),
	        	null
	        );
	        surfaceHolder.unlockCanvasAndPost(canvas);
//	        setVisibility(View.VISIBLE);
	    }
		
		/** Determine the space between the first two fingers */
		private float spacing(WrapMotionEvent event)
		{
			// ...
			float x = event.getX(0) - event.getX(1);
			float y = event.getY(0) - event.getY(1);
			return FloatMath.sqrt(x * x + y * y);
		}

		@Override
		public boolean onTouch(View v, MotionEvent rawEvent)
		{
			WrapMotionEvent event = WrapMotionEvent.wrap(rawEvent);

			int play_icon_left = (getWidth()/2)-(playIcon.getWidth()/2);
			int play_icon_top  = (getHeight()/2)-(playIcon.getHeight()/2);
			int x = Math.round(rawEvent.getX());
			int y = Math.round(rawEvent.getY());
			
			boolean is_click_play_icon = false;
			
			if(
				x >= play_icon_left && 
				x < play_icon_left+playIcon.getWidth() &&
				y >= play_icon_top && 
				y < play_icon_top+playIcon.getHeight()
			)
			{
				//Click playIcon
				is_click_play_icon = true;
			}
			
			// Handle touch events here...
			switch (event.getAction() & MotionEvent.ACTION_MASK)
			{
				case MotionEvent.ACTION_DOWN:
	
					start.set(event.getX(), event.getY());
	
					break;
	
				case MotionEvent.ACTION_POINTER_DOWN:
	
					_is_multi_touch = true;
	
					oldDist = spacing(event);
	
					break;
	
				case MotionEvent.ACTION_UP:
					
					Log.i("EmbaddedVideoPlayer", "ACTION_UP");
					
					if (!_is_multi_touch && !_is_point_up && is_click_play_icon)
					{
						//Single Click on the play icon
						playerHandler.onPlayIconClick();
					}
	
					_is_multi_touch = false;
					_is_point_up    = false;
	
					break;
	
				case MotionEvent.ACTION_POINTER_UP:
	
					newDist = spacing(event);
					
					if ((newDist - oldDist) > 0)
					{
						_is_point_up = true;
						
						playerHandler.onPointerUp();
						return true;
					}
					
					break;
	
				case MotionEvent.ACTION_MOVE:
	
					if (_is_multi_touch)
					{
						
					}
	
					break;
			}

			if(is_click_play_icon)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
	}
















	



	
}
