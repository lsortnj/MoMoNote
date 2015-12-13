package mo.pay.post_it.widget;

import java.io.File;

import mo.pay.post_it.R;
import mo.pay.post_it.bitmapPlugin.Util;
import mo.pay.post_it.logic.AlbumViewListener;
import mo.pay.post_it.logic.MomoTouchListener;
import mo.pay.post_it.logic.ResizeTouchListener;
import mo.pay.post_it.popup.PopupWindowBuilder;
import mo.pay.post_it.util.DeviceInfoUtil;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

public class MomoImageView extends FrameLayout
{
	public static final String TAG = "MomoImageView";
	
	public static final int MIN_WIDTH 	= 200;
	public static final int MIN_HEIGHT 	= 200;
	
	public static final int FRAME_NONE 		= 0;
	public static final int FRAME_POLAROID 	= 1;
	
	private ImageView		_image_view				= null;
	private ImageView		_delete_icon			= null;
	private ImageView		_resize_left_bottom		= null;
	private ImageView		_resize_right_top		= null;
	private ImageView		_resize_right_bottom	= null;
	private ImageView		_rotate					= null;
	private TextView		_text_view_file_not_found= null;
	private Activity		_activity				= null;
	
	private int			screen_w 			= 0;
	private int			screen_h 			= 0;
	private int			rotateTimes			= 0;
	
	private boolean isNeedToDrawBorder = false;
	
	private int _frameType = FRAME_POLAROID;
	
	private MomoTouchListener 	_momo_touch_listener 	= null;
	private AlbumViewListener 	_note_past_listener 	= null;
	private ResizeTouchListener _resize_listener 		= null;
	
	private ComponentLocation info = null;
	
	private Bitmap 	_bitmap 		= null;
	private String 	_image_path 	= "";
	private String  _image_format   = "";
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
						_note_past_listener.onNotePasteItemClicked(MomoImageView.this);
					}
					
					break;
			
				case MomoTouchListener.ON_CLICK_ONCE:
					
					break;
					
				case MomoTouchListener.ON_CLICK_TWICE:
					
					break;
					
				case MomoTouchListener.ON_DOUBLE_CLICK:
					PopupWindowBuilder.getMomoImageViewQuickAction(_activity, _note_past_listener, MomoImageView.this).show();
					break;
					
				case MomoTouchListener.ON_LONG_CLICK:
					
//					PopupWindowBuilder.getMomoImageViewQuickAction(_activity, _note_past_listener, MomoImageView.this).show();
					
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
		isNeedToDrawBorder = true;
		
		_delete_icon.setVisibility(View.VISIBLE);
		_resize_left_bottom.setVisibility(View.VISIBLE);
		_resize_right_top.setVisibility(View.VISIBLE);
		_resize_right_bottom.setVisibility(View.VISIBLE);
		_rotate.setVisibility(View.VISIBLE);
		
		_momo_touch_listener.enableEdit();
		
		bringToFront();
		
		postInvalidate();
	}
	
	
	
	public int getFrameType()
	{
		return _frameType;
	}


	public void setFrameType(int frameType)
	{
		this._frameType = frameType;
		
		refreshFrame();
	}



	public MomoImageView(Activity activity)
	{
		super(activity);

		_activity = activity;
		
		init();
	}
	
	public MomoImageView(Activity activity, AttributeSet attrs)
	{
		super(activity, attrs);

		_activity = activity;
		
		init();
	}
	
	public void setAlbumPageViewListner(AlbumViewListener listener)
	{
		_note_past_listener = listener;
		
		_momo_touch_listener  = new MomoTouchListener
		(
			screen_w,
			screen_h,
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
	

	public MomoImageView(Activity activity, AttributeSet attrs, int defStyle)
	{
		super(activity, attrs, defStyle);

		_activity = activity;
		
		init();
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
	
	private void refreshFrame()
	{
		switch(_frameType)
		{
			case FRAME_NONE:
				_image_view.setBackgroundColor(Color.TRANSPARENT);
				break;
				
			case FRAME_POLAROID:
				_image_view.setBackgroundColor(Color.WHITE);
				break;
		}
	}
	
	private void init()
	{
		refreshScreenResolution();
		
		
//		setPadding(5, 5, 5, 10);
		
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		params.gravity = Gravity.CENTER | Gravity.TOP;
		params.setMargins
		(
			MomoWidgetDef.RESIZE_BTN_HEIGHT/2, MomoWidgetDef.RESIZE_BTN_HEIGHT/2, 
			MomoWidgetDef.RESIZE_BTN_HEIGHT/2, MomoWidgetDef.RESIZE_BTN_HEIGHT/2
		);
		 
		_image_view = new ImageView(_activity);
		_image_view.setPadding(10, 10, 10, 30);
		_image_view.setId(MomoWidgetDef.ID_SUB_VIEW);
		_image_view.setScaleType(ScaleType.CENTER_INSIDE);
		_image_view.setImageBitmap(_bitmap);
		_image_view.setLayoutParams(params);
		
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
	 	
		params = new FrameLayout.LayoutParams(MomoWidgetDef.RESIZE_BTN_WDITH, MomoWidgetDef.RESIZE_BTN_HEIGHT);
		params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
		
		_rotate = new ImageView(_activity);
		_rotate.setId(MomoWidgetDef.BTN_ROTATE);
		_rotate.setImageResource(R.drawable.rotate);
		_rotate.setScaleType(ScaleType.FIT_XY);
		_rotate.setLayoutParams(params);
		
		_resize_listener = new ResizeTouchListener(screen_w,screen_h,this);
		_resize_listener.setMiniSize(new Point(MIN_WIDTH,MIN_HEIGHT));
		
		//Add Resize Listener
		_resize_left_bottom.setOnTouchListener(_resize_listener);
		_resize_right_top.setOnTouchListener(_resize_listener);
		_resize_right_bottom.setOnTouchListener(_resize_listener);
		
		//Add Rotate Listener
		_rotate.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v)
			{
				rotateTimes += 1;
				
				if(rotateTimes == 4)
				{
					rotateTimes = 0;
				}
				
				doRotate();
			}
		});
		
		//Add Delete listener
		_delete_icon.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v)
			{
				if(_note_past_listener != null)
				{
					_note_past_listener.onViewDelete(MomoImageView.this);
				}
			}
		});
		
		params = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
		params.setMargins
		(
			MomoWidgetDef.RESIZE_BTN_HEIGHT/2, MomoWidgetDef.RESIZE_BTN_HEIGHT/2, 
			MomoWidgetDef.RESIZE_BTN_HEIGHT/2, MomoWidgetDef.RESIZE_BTN_HEIGHT
		);
		
		//For file not found
		_text_view_file_not_found = new TextView(_activity);
		_text_view_file_not_found.setLayoutParams(params);
		_text_view_file_not_found.setTypeface(null, Typeface.BOLD);
		_text_view_file_not_found.setText(_activity.getResources().getString(R.string.text_file_not_found));
		_text_view_file_not_found.setTextColor(_activity.getResources().getColor(R.color.text));
		_text_view_file_not_found.setTextSize(TypedValue.COMPLEX_UNIT_PX,_activity.getResources().getDimensionPixelSize(R.dimen.tiny_text_size));
		_text_view_file_not_found.setVisibility(View.GONE);
		
		addView(_image_view);
		addView(_text_view_file_not_found);
		addView(_delete_icon);
		addView(_resize_left_bottom);
		addView(_resize_right_top);
		addView(_resize_right_bottom);
		addView(_rotate);
		
		_delete_icon.setVisibility(View.GONE);
		_resize_left_bottom.setVisibility(View.GONE);
		_resize_right_top.setVisibility(View.GONE);
		_resize_right_bottom.setVisibility(View.GONE);
		_rotate.setVisibility(View.GONE);
	
		refreshFrame();
	}
	
	private void doRotate()
	{
		Matrix mat = new Matrix();
        mat.postRotate(90);
        Bitmap bMapRotate = Bitmap.createBitmap(_bitmap, 0, 0, _bitmap.getWidth(), _bitmap.getHeight(), mat, true);
        _image_view.setImageBitmap(bMapRotate);
        _bitmap.recycle();
        _bitmap = null;
        _bitmap = bMapRotate;
	}
	
	public void performRotate()
	{
		if(new File(_image_path).exists())
		{
			Bitmap bitmap  = Util.makeBitmap
					(
						-1, 
						2*DeviceInfoUtil.getDeviceDisplayMetrics().widthPixels 
						*DeviceInfoUtil.getDeviceDisplayMetrics().heightPixels,
						Uri.fromFile(new File(_image_path)), 
						_activity.getContentResolver(), false
					);
			
			Matrix mat = new Matrix();
	        mat.postRotate(rotateTimes*90);
	        Bitmap bMapRotate = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mat, true);
	        _image_view.setImageBitmap(bMapRotate);
	        _bitmap.recycle();
	        _bitmap = null;
	        _bitmap = bMapRotate;
		}
	}
	
	public void setRotateDegree(int degree)
	{
		rotateTimes = degree/90;
	}
	
	public int getRotateDegree()
	{
		return rotateTimes*90;
	}
	
	RotateAnimation animation = null;
	
	public void deSelect()
	{
		hideBorder();
		
		if(_momo_touch_listener != null)
			_momo_touch_listener.disableEdit();
	}
	
	private void hideBorder()
	{
		isNeedToDrawBorder = false;
		
		_delete_icon.setVisibility(View.GONE);
		_resize_left_bottom.setVisibility(View.GONE);
		_resize_right_top.setVisibility(View.GONE);
		_resize_right_bottom.setVisibility(View.GONE);
		_rotate.setVisibility(View.GONE);
		
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
	
	@Override
	protected void dispatchDraw(Canvas canvas)
	{
		super.dispatchDraw(canvas);
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
	
	@Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) 
	{
        super.onSizeChanged(w, h, oldw, oldh);
        
	}
	
	public void setMiniSize(Point size)
	{
		_resize_listener.setMiniSize(size);
	}
	
	public void setScaleType(ScaleType scaleType)
	{
		_image_view.setScaleType(scaleType);
	}
	
	public void setImageBitmap(Bitmap bitmap)
	{
		if(bitmap==null)
		{
			_bitmap =BitmapFactory.decodeResource(getResources(), R.drawable.img_not_found);
			_text_view_file_not_found.setVisibility(View.VISIBLE);
		}
		else
		{
			_bitmap =bitmap;
			_text_view_file_not_found.setVisibility(View.GONE);
		}
		
		_image_view.setImageBitmap(_bitmap);
	}
	
	public void setImageBitmap(Bitmap bitmap, String path)
	{
		if(!new File(path).exists() || bitmap==null)
		{
			_text_view_file_not_found.setVisibility(View.VISIBLE);
			_bitmap=BitmapFactory.decodeResource(getResources(), R.drawable.img_not_found);
			
			_bitmap      = bitmap;
			_image_path  = path;
		}
		else
		{
			_text_view_file_not_found.setVisibility(View.GONE);
			_bitmap      = bitmap;
			_image_path  = path;
		}
		
		_image_view.setImageBitmap(_bitmap);
	}
	
	public void setImagePath(String path)
	{
		_image_path  = path;
	}
	
	public String getImagePath()
	{
		return _image_path;
	}
	
	public String getImageFileName()
	{
		return _image_path.substring(_image_path.lastIndexOf(File.separator)+1);
	}
	
	public void recycleBitmap()
	{
		try{
			if(_bitmap != null)
			{
				_bitmap.recycle();
				_bitmap = null;
			}
			
			System.gc();
		}catch(Exception e){Log.d("RecycleMomoImageView", e.toString());}
		
	}
	
	public ComponentLocation getLayoutInfo()
	{
		FrameLayout.LayoutParams params = (LayoutParams) getLayoutParams();
		
		info = new ComponentLocation
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
		this.info = info;
		
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams
		(
			info.getWidth(),
			info.getHeight()
		);
		params.gravity = Gravity.LEFT | Gravity.TOP;
		params.leftMargin = info.getX();
		params.topMargin  = info.getY();
		
		this.setLayoutParams(params);
	}
}
