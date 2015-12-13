package mo.pay.post_it.clipWeb;

import mo.pay.post_it.R;
import mo.pay.post_it.util.DeviceInfoUtil;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DialogConfirmClip extends Dialog
{
	public static final String 		DEFAULT_POSITIVE_TEXT	= "Yes";
	public static final String 		DEFAULT_NAGTIVE_TEXT	= "No";
	
	private Context		_context 			= null;
	
	private ImageView		_clipImage		= null;
	private TextView		_view_message	= null;
	private Button			_positive_btn	= null;
	private Button			_nagtive_btn	= null;
	private LinearLayout	_root_layout	= null;
	
	
	public DialogConfirmClip(Context context)
	{
		super(context, R.style.Theme_Teansparent);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		_context = context;
		
		//Initial view components
		_root_layout	= new LinearLayout(_context);
		_clipImage 		= new ImageView(_context);
		_view_message	= new TextView(_context);
		_positive_btn	= new Button(_context);
		_nagtive_btn	= new Button(_context);
		 
		_root_layout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		_root_layout.setPadding(30, 30, 30, 30);
		_root_layout.setOrientation(LinearLayout.VERTICAL);
		_root_layout.setBackgroundResource(R.drawable.dialog_bg_small);
		
		//Define clip image size
		int edgeMin = Math.min(
				DeviceInfoUtil.getDeviceDisplayMetrics().widthPixels,
				DeviceInfoUtil.getDeviceDisplayMetrics().heightPixels);
		
		int clipImageW = (int) ((float)edgeMin * 0.5);
		
		_clipImage.setPadding(5,0,5,0);
		_clipImage.setVisibility(View.GONE);
		_view_message.setTextSize(TypedValue.COMPLEX_UNIT_PX,_context.getResources().getDimensionPixelOffset(R.dimen.message_text_size));
		_view_message.setTextColor(_context.getResources().getColor(R.color.text));
		_positive_btn.setBackgroundResource(R.drawable.button_normal);
		_positive_btn.setTextColor(_context.getResources().getColor(R.color.button_text));
		_nagtive_btn.setBackgroundResource(R.drawable.button_normal);
		_nagtive_btn.setTextColor(_context.getResources().getColor(R.color.button_text));
		_positive_btn.setText(_context.getString(R.string.text_comfirm));
		_nagtive_btn.setText(_context.getString(R.string.text_cancel));
		
		_positive_btn.setTextSize(TypedValue.COMPLEX_UNIT_PX,_context.getResources().getDimensionPixelOffset(R.dimen.button_text_size));
		_nagtive_btn.setTextSize(TypedValue.COMPLEX_UNIT_PX,_context.getResources().getDimensionPixelOffset(R.dimen.button_text_size));
		
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
				(
					LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT
				);
		params.weight = 1;
		
		//Create icon and message area
		LinearLayout top_area = new LinearLayout(_context);
		top_area.setLayoutParams(params);
		top_area.setPadding(10, 10, 10, 10);
		top_area.setOrientation(LinearLayout.VERTICAL);
		
		LinearLayout.LayoutParams top_params = new LinearLayout.LayoutParams
		(
			LayoutParams.MATCH_PARENT,
			LayoutParams.WRAP_CONTENT
		);
		top_params.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
		
		_view_message.setGravity(Gravity.CENTER_HORIZONTAL);
		_view_message.setLayoutParams(top_params);
		
		LinearLayout.LayoutParams img_params = new LinearLayout.LayoutParams(clipImageW,clipImageW);
		img_params.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
		_clipImage.setLayoutParams(img_params);
		
		top_area.addView(_view_message);
		top_area.addView(_clipImage);
		
		
		//Create button area
		LinearLayout btn_area = new LinearLayout(_context);
		btn_area.setLayoutParams(params);
		btn_area.setOrientation(LinearLayout.HORIZONTAL);
		btn_area.setWeightSum(2);
		
		LinearLayout.LayoutParams btn_params = new LinearLayout.LayoutParams
		(
			LayoutParams.MATCH_PARENT,
			LayoutParams.WRAP_CONTENT
		);
		
		btn_params.weight = 1;
		btn_params.gravity = Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM;
		
		_positive_btn.setLayoutParams(btn_params);
		_nagtive_btn.setLayoutParams(btn_params);
		
		btn_area.addView(_positive_btn);
		btn_area.addView(_nagtive_btn);
		
		//Layout generated
		_root_layout.addView(top_area);
		_root_layout.addView(btn_area);
		
		setContentView(_root_layout);
	}
	
	public void setPositiveText(String text)
	{
		_positive_btn.setText(text);
	}
	
	public void setNagtiveText(String text)
	{
		_nagtive_btn.setText(text);
	}
	
	public void setPositiveOnClickListener(View.OnClickListener listener)
	{
		_positive_btn.setOnClickListener(listener);
	}
	
	public void setNagtiveOnClickListener(View.OnClickListener listener)
	{
		_nagtive_btn.setOnClickListener(listener);
	}
	
	public void setClipImageBitmap(Bitmap icon)
	{
		_clipImage.setImageBitmap(icon);
		
		_clipImage.setVisibility(View.VISIBLE);
	}
	
	public void setMessage(String message)
	{
		_view_message.setText(message);
	}
	
	@SuppressWarnings("deprecation")
	public void setBackgroundImageBitmap(Drawable drawable)
	{
		_root_layout.setBackgroundDrawable(drawable);
	}
	
	
	
	
	
	
	
	
	
	
}
