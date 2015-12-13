package mo.pay.post_it.widget;

import java.io.File;
import java.io.FileInputStream;

import mo.pay.post_it.R;

import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnClickListener;

public class AudioView extends LinearLayout implements OnClickListener,
SeekBar.OnSeekBarChangeListener
{
	private String 			_path			= "";
	private ImageView 		_playIcon		= null;
	private SeekBar			_progressBar	= null;
	private TextView		_audioName		= null;
	private MediaPlayer 	_player			= new MediaPlayer();
	private Handler 		mHandler 		= new Handler();
	private Context			_context		= null;
	
	public AudioView(Context context)
	{
		super(context);
		_context = context;
		init();
	}

	
	public AudioView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		_context = context;
		init();
	}

	public AudioView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		_context = context;
		init();
	}

	@Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) 
	{
        super.onSizeChanged(w, h, oldw, oldh);
        
//        int playIconNewWidth = h-20;
//        
//        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(playIconNewWidth,playIconNewWidth);
//		params.gravity = Gravity.CENTER;
//		
//		_playIcon.setLayoutParams(params);
	}
	
	private void init()
	{
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		this.setOrientation(LinearLayout.HORIZONTAL);
		setBackgroundResource(R.drawable.audio_view_bg);
//		this.setBackgroundColor(Color.GREEN);
		setLayoutParams(params);
		
		params = new LinearLayout.LayoutParams
		( 
			_context.getResources().getDimensionPixelOffset(R.dimen.audio_playicon_w),
			_context.getResources().getDimensionPixelOffset(R.dimen.audio_playicon_h)
		);  
		params.setMargins
		(  
			_context.getResources().getDimensionPixelOffset(R.dimen.audio_playicon_margin_left),
			_context.getResources().getDimensionPixelOffset(R.dimen.audio_playicon_margin_top),
			0, 0 
		);
//		params.gravity = Gravity.LEFT | Gravity.TOP;
		
		_playIcon = new ImageView(_context);
		_playIcon.setBackgroundColor(Color.parseColor("#00000000"));
		_playIcon.setScaleType(ScaleType.FIT_CENTER);
		_playIcon.setImageResource(R.drawable.sound);
		_playIcon.setLayoutParams(params); 
		_playIcon.setOnClickListener(this);
		 
		
		LinearLayout layoutRight = new LinearLayout(_context);
		layoutRight.setOrientation(LinearLayout.VERTICAL);
		
		params = new LinearLayout.LayoutParams
		( 
			_context.getResources().getDimensionPixelOffset(R.dimen.audio_right_area_w),
			LayoutParams.MATCH_PARENT
		);
		
		layoutRight.setLayoutParams(params);
		     
		params = new LinearLayout.LayoutParams
		(
			_context.getResources().getDimensionPixelOffset(R.dimen.audio_title_w),
			_context.getResources().getDimensionPixelOffset(R.dimen.audio_title_h)
		); 
		params.setMargins
		(
			_context.getResources().getDimensionPixelOffset(R.dimen.audio_title_margin_left),
			_context.getResources().getDimensionPixelOffset(R.dimen.audio_title_margin_top),
			_context.getResources().getDimensionPixelOffset(R.dimen.audio_title_margin_right),
			_context.getResources().getDimensionPixelOffset(R.dimen.audio_title_margin_bottom)
		);
		params.weight = 1; 
		
		_audioName = new TextView(_context);
		_audioName.setTextSize(TypedValue.COMPLEX_UNIT_PX,_context.getResources().getDimensionPixelOffset(R.dimen.audio_title_text_size));
		_audioName.setSingleLine(true);
		_audioName.setEllipsize(TruncateAt.MARQUEE);
		_audioName.setMarqueeRepeatLimit(-1); 
		_audioName.setLayoutParams(params);
		 
		params = new LinearLayout.LayoutParams
		(
			_context.getResources().getDimensionPixelOffset(R.dimen.audio_seek_w),
			_context.getResources().getDimensionPixelOffset(R.dimen.audio_seek_h)
		);
		params.setMargins
		(
			_context.getResources().getDimensionPixelOffset(R.dimen.audio_seek_margin_left),
			_context.getResources().getDimensionPixelOffset(R.dimen.audio_seek_margin_top),
			_context.getResources().getDimensionPixelOffset(R.dimen.audio_seek_margin_right),
			_context.getResources().getDimensionPixelOffset(R.dimen.audio_seek_margin_bottom)
		);
		params.weight = 1;
		 
		_progressBar = new SeekBar(_context);
		_progressBar.setThumbOffset(0); 
		_progressBar.setPadding
		( 
			_context.getResources().getDimensionPixelOffset(R.dimen.audio_seek_padding_left),
			_context.getResources().getDimensionPixelOffset(R.dimen.audio_seek_padding_top),
			_context.getResources().getDimensionPixelOffset(R.dimen.audio_seek_padding_right),
			_context.getResources().getDimensionPixelOffset(R.dimen.audio_seek_padding_bottom)
		);
		_progressBar.setProgressDrawable(_context.getResources().getDrawable(R.drawable.audio_view_progress));
		_progressBar.setThumb(_context.getResources().getDrawable(R.drawable.audio_seek_thumb));
		_progressBar.setMax(100);
		_progressBar.setLayoutParams(params);
		_progressBar.setOnSeekBarChangeListener(this);
		_progressBar.setEnabled(false);
		
		layoutRight.addView(_audioName);
		layoutRight.addView(_progressBar);
		
		addView(_playIcon);
		addView(layoutRight);
	}
	
	public void stopPlay()
	{
		if( _player.isPlaying() )
		{
			_player.stop();
			_player.reset();
		}
		
		_playIcon.setImageResource(R.drawable.sound);
		_progressBar.setProgress(0);
		_audioName.setSelected(false);
	}
	
	public void setAudioFilePath(String filePath)
	{
		_path = filePath;
		
		if(new File(filePath).exists())
		{
			_audioName.setText(new File(filePath).getName());
			_player.reset();
		}
		else
		{
			_audioName.setText(_context.getResources().getString(R.string.audio_not_found));
		}
	}


	@Override
	public void onClick(View v)
	{
		if (_player != null)
		{
			if (_player.isPlaying())
			{
				_player.stop();
				_playIcon.setImageResource(R.drawable.sound);
				_progressBar.setProgress(0);
				_audioName.setSelected(false);
				_progressBar.setEnabled(false);
			} 
			else
			{
				try
				{
					_player.setOnCompletionListener
					(
						new OnCompletionListener()
						{
							@Override
							public void onCompletion(MediaPlayer mp)
							{
								try
								{
									_playIcon.setImageResource(R.drawable.sound);
									FileInputStream fi = new FileInputStream(new File(_path));
									_player.reset();
						        	_player.setDataSource(fi.getFD());
									_player.prepare();
									_progressBar.setProgress(0);
								}
								catch(Exception e)
								{
									Log.i("AudioView", e.getMessage());
								}
							}
							
						}
					);
					
					FileInputStream fi = new FileInputStream(new File(_path));
					
					_player.reset();
		        	_player.setDataSource(fi.getFD());
		        	_player.prepare();  
		        	_player.setAudioStreamType(AudioManager.STREAM_MUSIC);
					_player.start();
					_playIcon.setImageResource(R.drawable.sound_playing);
					
					_audioName.requestFocus(); 
					_audioName.setSelected(true);
					_progressBar.setEnabled(true);
					updateProgressBar();
				}
				catch(Exception e)
				{
					Log.i("AudioView", e.getMessage());
				}
			}
		}
	}
	
	public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 50);        
    }	
	
	/**
	 * Background Runnable thread
	 * */
	private Runnable mUpdateTimeTask = new Runnable() {
		   public void run() {
			   if(_player == null || !_player.isPlaying())
				   return;
			   long totalDuration = _player.getDuration();
			   long currentDuration = _player.getCurrentPosition();
			  
			   // Updating progress bar
			   int progress = (int)getProgressPercentage(currentDuration, totalDuration);
			   //Log.d("Progress", ""+progress);
			   _progressBar.setProgress(progress);
			   
			   // Running this thread after 100 milliseconds
		       mHandler.postDelayed(this, 100);
		   }
		};
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
		 if(_player == null || !_player.isPlaying())
			 seekBar.setProgress(0);
	}

	/**
	 * When user starts moving the progress handler
	 * */
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// remove message Handler from updating progress bar
		mHandler.removeCallbacks(mUpdateTimeTask);
    }
	
	/**
	 * When user stops moving the progress hanlder
	 * */
	@Override
    public void onStopTrackingTouch(SeekBar seekBar) {
		mHandler.removeCallbacks(mUpdateTimeTask);
		int totalDuration = _player.getDuration();
		int currentPosition = progressToTimer(seekBar.getProgress(), totalDuration);
		
		// forward or backward to certain seconds
		_player.seekTo(currentPosition);
		
		// update timer progress again
		updateProgressBar();
    }
	
	
	
	/**
	 * Function to convert milliseconds time to
	 * Timer Format
	 * Hours:Minutes:Seconds
	 * */
	public String milliSecondsToTimer(long milliseconds){
		String finalTimerString = "";
		String secondsString = "";
		
		// Convert total duration into time
		   int hours = (int)( milliseconds / (1000*60*60));
		   int minutes = (int)(milliseconds % (1000*60*60)) / (1000*60);
		   int seconds = (int) ((milliseconds % (1000*60*60)) % (1000*60) / 1000);
		   // Add hours if there
		   if(hours > 0){
			   finalTimerString = hours + ":";
		   }
		   
		   // Prepending 0 to seconds if it is one digit
		   if(seconds < 10){ 
			   secondsString = "0" + seconds;
		   }else{
			   secondsString = "" + seconds;}
		   
		   finalTimerString = finalTimerString + minutes + ":" + secondsString;
		
		// return timer string
		return finalTimerString;
	}
	
	/**
	 * Function to get Progress percentage
	 * @param currentDuration
	 * @param totalDuration
	 * */
	public int getProgressPercentage(long currentDuration, long totalDuration){
		Double percentage = (double) 0;
		
		long currentSeconds = (int) (currentDuration / 1000);
		long totalSeconds = (int) (totalDuration / 1000);
		
		// calculating percentage
		percentage =(((double)currentSeconds)/totalSeconds)*100;
		
		// return percentage
		return percentage.intValue();
	}

	/**
	 * Function to change progress to timer
	 * @param progress - 
	 * @param totalDuration
	 * returns current duration in milliseconds
	 * */
	public int progressToTimer(int progress, int totalDuration) {
		int currentDuration = 0;
		totalDuration = (int) (totalDuration / 1000);
		currentDuration = (int) ((((double)progress) / 100) * totalDuration);
		
		// return current duration in milliseconds
		return currentDuration * 1000;
	}
	
	
}
