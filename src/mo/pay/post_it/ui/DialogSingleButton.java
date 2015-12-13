package mo.pay.post_it.ui;

import mo.pay.post_it.R;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DialogSingleButton extends Dialog
{
	public static final String 	DEFAULT_BUTTON_TEXT	= "OK";
	
	private Context		_context 			= null;
	
	private ImageView		_view_icon		= null;
	private TextView		_view_message	= null;
	private Button			_btn			= null;
	private LinearLayout	_root_layout	= null;
	
	public DialogSingleButton(Context context, String message)
	{
		super(context, R.style.Theme_Teansparent);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		_context = context;
		
		initial();
		
		setContentView(_root_layout);
		setMessage(message);
	}
	
	public DialogSingleButton(Context context)
	{
		super(context, R.style.Theme_Teansparent);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		_context = context;
		
		initial();
		
		setContentView(_root_layout);
	}
	
	private void initial()
	{
		//Initial view components
		_root_layout	= new LinearLayout(_context);
		_view_icon 		= new ImageView(_context);
		_view_message	= new TextView(_context);
		_btn			= new Button(_context);
		
		_root_layout.setPadding(40, 30, 40, 30);
		_root_layout.setOrientation(LinearLayout.VERTICAL);
		_root_layout.setBackgroundResource(R.drawable.dialog_bg_small);
		_view_icon.setLayoutParams(new LayoutParams(80,80));
		_view_icon.setPadding(5,0,5,0);
		_view_icon.setImageResource(R.drawable.logo);
		_view_message.setTextSize(TypedValue.COMPLEX_UNIT_PX,_context.getResources().getDimensionPixelOffset(R.dimen.message_text_size));
		_view_message.setTextColor(Color.BLACK);
		_btn.setBackgroundResource(R.drawable.button_normal);
		_btn.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,60));
		_btn.setText(DEFAULT_BUTTON_TEXT);
		_btn.setTextColor(_context.getResources().getColor(R.color.button_text));
		_btn.setTextSize(TypedValue.COMPLEX_UNIT_PX,_context.getResources().getDimensionPixelOffset(R.dimen.button_text_size));
		_btn.setOnClickListener
		(
			new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					dismiss();
				}
			}
		);
		
		
		//Create icon and message area
		LinearLayout top_area = new LinearLayout(_context);
		top_area.setPadding(10, 10, 10, 10);
		top_area.setOrientation(LinearLayout.HORIZONTAL);
		
		LinearLayout.LayoutParams top_params = new LinearLayout.LayoutParams
		(
			LayoutParams.WRAP_CONTENT,
			LayoutParams.WRAP_CONTENT
		);
		
		top_params.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
		
		_view_message.setLayoutParams(top_params);
		
		top_area.addView(_view_icon);
		top_area.addView(_view_message);
		
		//Layout generated
		_root_layout.addView(top_area);
		_root_layout.addView(_btn);
	}
	
	public void setButtonOnClickListener(View.OnClickListener listener)
	{
		_btn.setOnClickListener(listener);
	}
	
	public void setIcon(Bitmap icon)
	{
		_view_icon.setImageBitmap(icon);
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
