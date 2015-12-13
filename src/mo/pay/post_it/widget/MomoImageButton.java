package mo.pay.post_it.widget;

import mo.pay.post_it.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

public class MomoImageButton extends FrameLayout
{
	public static final int TYPE_NORMAL = 0x101;
	public static final int TYPE_LIGHT 	= 0x102;
	
	private Context			_context	= null;
	private TextView		_text_view	= null;
	private ImageView		_icon_view	= null;
	private int				_button_type = TYPE_NORMAL;
	
	public MomoImageButton(Context context)
	{
		super(context);

		_context = context;
		
		init();
	}
	
	public MomoImageButton(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		_context = context;
		
		init();
	}

	public MomoImageButton(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);

		_context = context;
		
		init();
	}
	
	public void setButtonType(int type)
	{
		_button_type = type;
		init();
	}
	
	public void setText(String text)
	{
		_text_view.setText(text);
	}
	
	public void setText(int resId)
	{
		_text_view.setText(_context.getString(resId));
	}
	
	public void setIcon(int resId)
	{
		_icon_view.setImageResource(resId);
	}
	
	public void setIcon(Bitmap bitmap)
	{
		_icon_view.setImageBitmap(bitmap);
	}
	
	public void setTextSize(int size)
	{
		_text_view.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
	}
	
	public void setTextColor(int color)
	{
		_text_view.setTextColor(color);
	}
	
	private void init()
	{
		removeAllViews();
		
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		
		setLayoutParams(params);
		setBackgroundResource(_button_type==TYPE_NORMAL?R.drawable.button_normal:R.drawable.button_type2);
		
		LinearLayout.LayoutParams linearParams = 
				new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		
		LinearLayout root_layout = new LinearLayout(_context);
		root_layout.setPadding(10, 5, 10, 10);
		root_layout.setOrientation(LinearLayout.HORIZONTAL);
		root_layout.setLayoutParams(params);
		
		linearParams = new LinearLayout.LayoutParams(50, 50);
		linearParams.rightMargin = 5;
		 
		_icon_view = new ImageView(_context);
		_icon_view.setBackgroundColor(Color.parseColor("#00000000"));
		_icon_view.setImageResource(R.drawable.logo);
		_icon_view.setScaleType(ScaleType.FIT_CENTER);
		_icon_view.setLayoutParams(linearParams);
		
		linearParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		linearParams.gravity = Gravity.CENTER;
		
		_text_view = new TextView(_context);
		_text_view.setText("Button");
		_text_view.setTextColor(_context.getResources().getColor(R.color.button_text));
		_text_view.setTextSize(TypedValue.COMPLEX_UNIT_PX,_context.getResources().getDimensionPixelSize(R.dimen.button_text_size));
		_text_view.setLayoutParams(params);
		_text_view.setGravity(Gravity.CENTER);
		
		root_layout.addView(_icon_view);
		root_layout.addView(_text_view);
		addView(root_layout);
	}
}
