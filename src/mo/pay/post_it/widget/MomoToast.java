package mo.pay.post_it.widget;

import mo.pay.post_it.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MomoToast
{
	public static final float		FONT_SIZE_MAX 		= 60f;
	public static final float		FONT_SIZE_NORMAL 	= 30f;
	public static final float		FONT_SIZE_MIN 		= 15f;
	
	public static final int	 DURATION_LONG	= Toast.LENGTH_LONG;
	public static final int	 DURATION_SHORT	= Toast.LENGTH_SHORT;
	
	private Context			_context 		= null;
	private LinearLayout 	_toast_layout 	= null;
	private TextView 		_view_msg 		= null;
	private ImageView		_image_view		= null;
	private int				_duration		= DURATION_SHORT;
	
	
	
	public MomoToast(Context context, String message, Bitmap icon, Drawable bgDrawable)
	{
		_context = context;
		
		initial();
		setMessage(message);
		setIcon(icon);
		setBackground(bgDrawable);
	}
	
	public MomoToast(Context context, String message, Bitmap icon)
	{
		_context = context;
		
		initial();
		setMessage(message);
		setIcon(icon);
	}
	
	public MomoToast(Context context, String message)
	{
		_context = context;
		
		initial();
		
		setMessage(message);
	}
	
	public MomoToast(Context context)
	{
		_context = context;
		
		initial();
	}
	
	private void initial()
	{ 
		_toast_layout 	= new LinearLayout(_context);
		_view_msg 		= new TextView(_context);
		_image_view		= new ImageView(_context);
		
		_toast_layout.setOrientation(LinearLayout.HORIZONTAL);
//		_toast_layout.setBackgroundColor(Color.BLUE);
		_toast_layout.setBackgroundResource(R.drawable.white_round_bg);
		_toast_layout.setPadding(20, 15, 20, 15);
		_view_msg.setTextSize(TypedValue.COMPLEX_UNIT_PX, _context.getResources().getDimensionPixelOffset(R.dimen.message_text_size));
		_view_msg.setPadding(5, 5, 5, 5);
		_view_msg.setTypeface(null, Typeface.BOLD); 
		_view_msg.setTextColor(Color.WHITE);
		_view_msg.setGravity(Gravity.CENTER);
		_image_view.setImageResource(R.drawable.logo);
		_image_view.setPadding(25, 15, 25, 15);
		
		_image_view.setVisibility(View.GONE);
		
		_toast_layout.addView(_image_view);
		_toast_layout.addView(_view_msg);
	}
	
	public void setMessageTextSize(int size)
	{
		if(size > FONT_SIZE_MAX)
			size = (int) FONT_SIZE_MAX;
		
		_view_msg.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
	}
	
	public void setBackground(Drawable drawable)
	{
		_toast_layout.setBackgroundDrawable(drawable);
	}
	
	public void setMessage(String message)
	{
		_view_msg.setText(message);
	}
	
	public void setIcon(Bitmap icon)
	{
		_image_view.setImageBitmap(icon);
		_image_view.setVisibility(View.VISIBLE);
	}
	
	public void setDuration(int duration)
	{
		_duration = duration;
	}
	
	public void show()
	{
		Toast toast = Toast.makeText(_context, "",  _duration); 
		toast.setGravity(Gravity.CENTER_HORIZONTAL| Gravity.CENTER_VERTICAL, 0, 0); 
		toast.setView(_toast_layout);
		toast.show();
	}
	
	
}
