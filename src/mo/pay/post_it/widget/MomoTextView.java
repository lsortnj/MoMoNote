package mo.pay.post_it.widget;

import mo.pay.post_it.R;
import mo.pay.post_it.logic.AlbumViewListener;
import mo.pay.post_it.logic.MomoTouchListener;
import mo.pay.post_it.logic.ResizeTouchListener;
import mo.pay.post_it.popup.PopupWindowBuilder;
import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

public class MomoTextView extends FrameLayout
{
	public static final String TAG = "MomoTextView";
	
	public static final float		FONT_SIZE_MAX 		= 60f;
	public static final float		FONT_SIZE_NORMAL 	= 30f;
	public static final float		FONT_SIZE_MIN 		= 15f;
	
	private TextView		_text_view				= null;
	private ImageView		_delete_icon			= null;
	private ImageView		_resize_left_bottom		= null;
	private ImageView		_resize_right_top		= null;
	private ImageView		_resize_right_bottom	= null;
	private Activity		_activity				= null;
	
	private int			screen_w 			= 0;
	private int			screen_h 			= 0;
	
	private String  _textID = "";
	
	private MomoTouchListener 	_momo_touch_listener 	= null;
	private AlbumViewListener 	_note_past_listener 	= null;
	private ResizeTouchListener _resize_listener 		= null;
	
	private boolean isNeedToDrawBorder = false;
	private Paint 	_paint 			= new Paint();
	
	private Handler		_handler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			int 	multiChangeOffset 	= 0;
			float 	rotateDegree	 	= 0;
			
			switch(msg.what)
			{
				case MomoTouchListener.ON_TOUCHED:
					
					if(_note_past_listener != null)
					{
						_note_past_listener.onNotePasteItemClicked(MomoTextView.this);
					}
					
					break;
			
				case MomoTouchListener.ON_CLICK_ONCE:
					
					break;
					
				case MomoTouchListener.ON_CLICK_TWICE:
					
					break;
					
				case MomoTouchListener.ON_DOUBLE_CLICK:
//					_note_past_listener.onMomoTextViewEditText(MomoTextView.this);
					PopupWindowBuilder.getMomoTextViewQuickAction(_activity, _note_past_listener, MomoTextView.this).show();
					break;
					
				case MomoTouchListener.ON_LONG_CLICK:
//					PopupWindowBuilder.getMomoTextViewQuickAction(_activity, _note_past_listener, MomoTextView.this).show();
					break;
					
				case MomoTouchListener.ON_MULTI_SCALE_UP:
					multiChangeOffset = Math.abs((Integer) msg.obj);
					break;
					
				case MomoTouchListener.ON_MULTI_SCALE_DOWN:
					multiChangeOffset = Math.abs((Integer) msg.obj);
					break;
					
				case MomoTouchListener.ON_ROTATE:		
					rotateDegree = (Float) msg.obj;
					break;
					
				case MomoTouchListener.ON_EDIT_START:		

					toEditMode();
					
					break;
					
			}
		}
	};
	
	public void toEditMode()
	{
		setBackgroundColor(Color.parseColor(MomoWidgetDef.SELECTED_BG_COLOR));
		isNeedToDrawBorder = true;
		
		_delete_icon.setVisibility(View.VISIBLE);
		_resize_left_bottom.setVisibility(View.VISIBLE);
		_resize_right_top.setVisibility(View.VISIBLE);
		_resize_right_bottom.setVisibility(View.VISIBLE);
		
		_momo_touch_listener.enableEdit();
		
		bringToFront();
		
		postInvalidate();
	}
	
	public MomoTextView(Activity activity)
	{
		super(activity);

		_activity = activity;
		
		init();
	}
	
	public TextView getTextView()
	{
		return _text_view;
	}
	
	public MomoTextView(Activity activity, AttributeSet attrs)
	{
		super(activity, attrs);

		_activity = activity;
		
		init();
	}

	public MomoTextView(Activity activity, AttributeSet attrs, int defStyle)
	{
		super(activity, attrs, defStyle);

		_activity = activity;
		
		init();
	}
	
	public void setAlbumPageViewListner(AlbumViewListener listener)
	{
		_note_past_listener = listener;
		
		_momo_touch_listener  = new MomoTouchListener
		(
			screen_w, screen_h,
			this,_handler
		);
		
		setOnTouchListener(_momo_touch_listener);
	}
	
	public void resetAllListener()
	{
		_note_past_listener 	= null;
		_momo_touch_listener 	= null;
		setOnTouchListener(new OnTouchListener(){
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1)
			{
				// TODO Auto-generated method stub
				return false;
			}});
	}
	
	public void refreshScreenResolution()
	{
		DisplayMetrics dm = new DisplayMetrics();
		_activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		
		screen_w = dm.widthPixels;
		screen_h = dm.heightPixels;
		
		if(_momo_touch_listener != null)
			_momo_touch_listener.setScreenSize(screen_w, screen_h);
		
		if(_resize_listener != null)
			_resize_listener.setScreenSize(screen_w, screen_h);
	}
	
	private void init()
	{
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;
		setLayoutParams(params);
		
		refreshScreenResolution();
		
		params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;
		params.setMargins
		(
			MomoWidgetDef.RESIZE_BTN_HEIGHT/2, MomoWidgetDef.RESIZE_BTN_HEIGHT/2, 
			MomoWidgetDef.RESIZE_BTN_HEIGHT/2, MomoWidgetDef.RESIZE_BTN_HEIGHT/2
		);
		
		_text_view = new TextView(_activity);
		_text_view.setBackgroundResource(R.drawable.text_view_round_bg);
		_text_view.setId(MomoWidgetDef.ID_SUB_VIEW);
		_text_view.setTextColor(Color.BLACK);
		_text_view.setTextSize(TypedValue.COMPLEX_UNIT_PX, FONT_SIZE_NORMAL);
		_text_view.setLayoutParams(params);
		
		params = new FrameLayout.LayoutParams(MomoWidgetDef.RESIZE_BTN_WDITH, MomoWidgetDef.RESIZE_BTN_HEIGHT);
		params.gravity = Gravity.LEFT | Gravity.TOP;
		
		_delete_icon = new ImageView(_activity);
		_delete_icon.setBackgroundColor(Color.parseColor("#00000000"));
		_delete_icon.setId(MomoWidgetDef.BTN_RESIZE_LEFT_TOP);
		_delete_icon.setImageResource(R.drawable.delete);
		_delete_icon.setScaleType(ScaleType.FIT_XY);
		_delete_icon.setLayoutParams(params);
		
		params = new FrameLayout.LayoutParams(MomoWidgetDef.RESIZE_BTN_WDITH, MomoWidgetDef.RESIZE_BTN_HEIGHT);
		params.gravity = Gravity.LEFT | Gravity.BOTTOM;
		
		_resize_left_bottom = new ImageView(_activity);
		_resize_left_bottom.setId(MomoWidgetDef.BTN_RESIZE_LEFT_BOTTOM);
		_resize_left_bottom.setImageResource(R.drawable.seekbar_thumb);
		_resize_left_bottom.setScaleType(ScaleType.FIT_XY);
		_resize_left_bottom.setLayoutParams(params);
		
		params = new FrameLayout.LayoutParams(MomoWidgetDef.RESIZE_BTN_WDITH, MomoWidgetDef.RESIZE_BTN_HEIGHT);
		params.gravity = Gravity.RIGHT | Gravity.TOP;
		
		_resize_right_top = new ImageView(_activity);
		_resize_right_top.setId(MomoWidgetDef.BTN_RESIZE_RIGHT_TOP);
		_resize_right_top.setImageResource(R.drawable.seekbar_thumb);
		_resize_right_top.setScaleType(ScaleType.FIT_XY);
		_resize_right_top.setLayoutParams(params);
		
		params = new FrameLayout.LayoutParams(MomoWidgetDef.RESIZE_BTN_WDITH, MomoWidgetDef.RESIZE_BTN_HEIGHT);
		params.gravity = Gravity.RIGHT | Gravity.BOTTOM;
		
		_resize_right_bottom = new ImageView(_activity);
		_resize_right_bottom.setId(MomoWidgetDef.BTN_RESIZE_RIGHT_BOTTOM);
		_resize_right_bottom.setImageResource(R.drawable.seekbar_thumb);
		_resize_right_bottom.setScaleType(ScaleType.FIT_XY);
		_resize_right_bottom.setLayoutParams(params);
		
		_resize_listener = new ResizeTouchListener(screen_w,screen_h,this);
		
		//Add Resize Listener
		_resize_left_bottom.setOnTouchListener(_resize_listener);
		_resize_right_top.setOnTouchListener(_resize_listener);
		_resize_right_bottom.setOnTouchListener(_resize_listener);
		
		//Add Delete listener
		_delete_icon.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v)
			{
				if(_note_past_listener != null)
				{
					_note_past_listener.onViewDelete(MomoTextView.this);
				}
			}
		});
		
		addView(_text_view);
		addView(_delete_icon);
		addView(_resize_left_bottom);
		addView(_resize_right_top);
		addView(_resize_right_bottom);
		
		_delete_icon.setVisibility(View.GONE);
		_resize_left_bottom.setVisibility(View.GONE);
		_resize_right_top.setVisibility(View.GONE);
		_resize_right_bottom.setVisibility(View.GONE);
		
		_textID = "TXT_"+System.currentTimeMillis()+Math.random()*999;
		
//		setOnTouchListener(_move_n_scale_listener);
	}
	
	public void setMomoTextViewID(String id)
	{
		_textID = id;
	}
	
	public String getMomoTextViewID()
	{
		return _textID;
	}
	
	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		
		if(isNeedToDrawBorder)
		{
			drawBorder(canvas);
		}
	}
	
	public void deSelect()
	{
		hideBorder();
		if(_momo_touch_listener != null)
			_momo_touch_listener.disableEdit();
	}
	
	private void hideBorder()
	{
		setBackgroundColor(Color.TRANSPARENT);
		isNeedToDrawBorder = false;
		
		_delete_icon.setVisibility(View.GONE);
		_resize_left_bottom.setVisibility(View.GONE);
		_resize_right_top.setVisibility(View.GONE);
		_resize_right_bottom.setVisibility(View.GONE);
		
		postInvalidate();
	}
	
	@Override
	protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld)
	{
		super.onSizeChanged(xNew, yNew, xOld, yOld);
		postInvalidate();
	}
	
	private void drawBorder(Canvas canvas)
	{
		PathEffect effects = new DashPathEffect(new float[]{5,5,5,5}, 3);
		
		_paint.setColor(Color.parseColor(MomoWidgetDef.SELECTED_BORDER_COLOR));
		_paint.setStrokeWidth(3);
		_paint.setPathEffect(effects);
		
		int bufGap = (MomoWidgetDef.RESIZE_BTN_HEIGHT/2)-2;
		
		// | left
		canvas.drawLine
		(
			bufGap, bufGap, 
			bufGap, canvas.getHeight()-bufGap, _paint
		);
		// ＿
		canvas.drawLine
		(
			MomoWidgetDef.RESIZE_BTN_WDITH/2, canvas.getHeight()-bufGap, 
			canvas.getWidth()-bufGap, canvas.getHeight()-bufGap, _paint
		);
		// | right
		canvas.drawLine
		(
			canvas.getWidth()-bufGap, bufGap, 
			canvas.getWidth()-bufGap, canvas.getHeight()-bufGap, _paint
		);
		// ￣
		canvas.drawLine
		(
			MomoWidgetDef.RESIZE_BTN_WDITH/2, bufGap, 
			canvas.getWidth()-bufGap, bufGap, _paint
		);
	}
	
	public void setMiniSize(Point size)
	{
		_resize_listener.setMiniSize(size);
	}
	
	public int getTextColor()
	{
		return _text_view.getCurrentTextColor();
	}
	
	public int getTextSize()
	{
		return (int) _text_view.getTextSize();
	}
	
	public String getText()
	{
		return (String) _text_view.getText();
	}
	
	public void setTextColor(int color)
	{
		_text_view.setTextColor(color);
	}
	
	public void setTextSize(int size)
	{
		_text_view.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
	}
	
	public void setText(String text)
	{
		_text_view.setText(text);
	}
	
	
	
	public ComponentLocation getLayoutInfo()
	{
		FrameLayout.LayoutParams params = (LayoutParams) getLayoutParams();
		
		ComponentLocation info = new ComponentLocation
		(
			params.leftMargin,
			params.topMargin,
			params.width,
			params.height
		);
		
		return info;
	}
	
	public void setLayoutInfo(ComponentLocation info)
	{
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams
		(
			info.getWidth(),
			info.getHeight()
		);
		params.gravity = Gravity.LEFT | Gravity.TOP;
		params.setMargins
		(
			info.getX(), 
			info.getY(), 
			0, 0
		);
		
		setLayoutParams(params);
	}
}
