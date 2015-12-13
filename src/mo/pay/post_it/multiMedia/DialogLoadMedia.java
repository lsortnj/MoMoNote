package mo.pay.post_it.multiMedia;

import mo.pay.post_it.R;
import mo.pay.post_it.widget.MomoImageButton;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DialogLoadMedia extends Dialog
{
	public static final int		ON_CAPTURE_IMAGE	= 0x700;
	public static final int		ON_RECORD_VIDEO		= 0x701;
	public static final int		ON_RECORD_AUDIO		= 0x702;
	public static final int		ON_LOAD_IMAGE		= 0x703;
	public static final int		ON_LOAD_VIDEO		= 0x704;
	public static final int		ON_LOAD_AUDIO		= 0x705;
	
	private Context		_context		= null;
	private Handler		_handler		= null;
	
	public DialogLoadMedia(Context context, Handler handler)
	{
		super(context, R.style.Theme_Teansparent);
		_context = context;
		_handler = handler;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		LinearLayout.LayoutParams params = 
				new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		
		LinearLayout root_layout = new LinearLayout(_context);
		root_layout.setPadding(80, 40, 80, 70);
		root_layout.setBackgroundResource(R.drawable.dialog_bg_small);
		root_layout.setOrientation(LinearLayout.VERTICAL);
		root_layout.setLayoutParams(params);
		
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
		
		TextView title_text = new TextView(_context);
		title_text.setLayoutParams(params);
		title_text.setText(_context.getString(R.string.text_add_multi_media));
		title_text.setTextSize(15);
		title_text.setTextColor(Color.BLACK);
		
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
				_handler.sendEmptyMessage(ON_LOAD_AUDIO);
				dismiss();
			}});
		
		btn_record_audio.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v)
			{
				_handler.sendEmptyMessage(ON_RECORD_AUDIO);
				dismiss();
			}});
		
		btn_capture_image.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v)
				{
					_handler.sendEmptyMessage(ON_CAPTURE_IMAGE);
					dismiss();
				}});
		
		btn_load_image.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v)
			{
				_handler.sendEmptyMessage(ON_LOAD_IMAGE);
				dismiss();
			}});
		
		btn_record_video.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v)
			{
				_handler.sendEmptyMessage(ON_RECORD_VIDEO);
				dismiss();
			}});
		
		btn_load_video.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v)
			{
				_handler.sendEmptyMessage(ON_LOAD_VIDEO);
				dismiss();
			}});
		
		button_layout.addView(title_text);
		button_layout.addView(btn_capture_image);
		button_layout.addView(btn_record_video);
		button_layout.addView(btn_record_audio);
		button_layout.addView(btn_load_image);
		button_layout.addView(btn_load_video);
		button_layout.addView(btn_load_audio);
		
		root_layout.addView(button_layout);
		
		setContentView(root_layout);
		
		setCanceledOnTouchOutside(true);
	}
	
}
