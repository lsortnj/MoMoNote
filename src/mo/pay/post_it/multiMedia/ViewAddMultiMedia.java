package mo.pay.post_it.multiMedia;

import mo.pay.post_it.R;
import mo.pay.post_it.widget.MomoImageButton;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

public class ViewAddMultiMedia
{
	private static 	Context			_context	= null;
	private static	LinearLayout 	_root	    = null;
	
	private static  IAddNoteObjectListener _listener	= null;
	
	public static void initView(Context context, IAddNoteObjectListener listener)
	{
		_context = context;
		_listener = listener;
		
		init();
	}
	
	public static View getAddMultiMediaView()
	{
		if(_root.getParent() != null)
		{
			ViewParent parentView = _root.getParent();
			((ViewGroup)parentView).removeView(_root);
		}
		
		return _root;
	}
	
	private static void init()
	{
		LinearLayout.LayoutParams params = 
				new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		
		_root = new LinearLayout(_context);
		_root.setOrientation(LinearLayout.VERTICAL);
		_root.setLayoutParams(params);
		
		//Button Area
		params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);

		
		LinearLayout button_layout = new LinearLayout(_context);
		button_layout.setOrientation(LinearLayout.VERTICAL);
		button_layout.setLayoutParams(params);
		
		MomoImageButton btn_capture_image = new MomoImageButton(_context);
		MomoImageButton btn_record_video  = new MomoImageButton(_context);
		MomoImageButton btn_record_audio  = new MomoImageButton(_context);
		MomoImageButton btn_load_image    = new MomoImageButton(_context);
		MomoImageButton btn_load_video	  = new MomoImageButton(_context);
		MomoImageButton btn_load_audio	  = new MomoImageButton(_context);
		
		params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		params.weight = 1;
		params.leftMargin = 10;
		params.rightMargin = 10;
		params.topMargin = 20;
		
		btn_capture_image.setIcon(R.drawable.camera);
		btn_record_video.setIcon(R.drawable.vedio_recorder);
		btn_record_audio.setIcon(R.drawable.audio_recorder);
		btn_load_image.setIcon(R.drawable.import_image);
		btn_load_video.setIcon(R.drawable.import_video);
		btn_load_audio.setIcon(R.drawable.import_audio);
		
		btn_capture_image.setTextSize(18);
		btn_record_video.setTextSize(18);
		btn_record_audio.setTextSize(18);
		btn_load_image.setTextSize(18);
		btn_load_video.setTextSize(18);
		btn_load_audio.setTextSize(18);
		
		btn_capture_image.setText(_context.getString(R.string.text_take_photo));
		btn_record_video.setText(_context.getString(R.string.text_record_video));
		btn_record_audio.setText(_context.getString(R.string.text_record_audio));
		btn_load_image.setText(_context.getString(R.string.text_import_image));
		btn_load_video.setText(_context.getString(R.string.text_import_video));
		btn_load_audio.setText(_context.getString(R.string.text_import_audio));
		
		btn_capture_image.setLayoutParams(params);
		btn_load_image.setLayoutParams(params);
		btn_record_audio.setLayoutParams(params);
		btn_record_video.setLayoutParams(params);
		btn_load_video.setLayoutParams(params);
		btn_load_audio.setLayoutParams(params);
		
		btn_load_audio.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v)
			{
				_listener.onImportAudio();
			}});
		
		btn_record_audio.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v)
			{
				_listener.onRecordAudio();
			}});
		
		btn_capture_image.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v)
				{
					_listener.onTakePhoto();
				}});
		
		btn_load_image.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v)
			{
				_listener.onImportImage();
			}});
		
		btn_record_video.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v)
			{
				_listener.onRecordvideo();
			}});
		
		btn_load_video.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v)
			{
				_listener.onImportVideo();
			}});
		
		button_layout.addView(btn_capture_image);
		button_layout.addView(btn_record_video);
		button_layout.addView(btn_record_audio);
		button_layout.addView(btn_load_image);
		button_layout.addView(btn_load_video);
		button_layout.addView(btn_load_audio);
		
		_root.addView(button_layout);
	}
}
