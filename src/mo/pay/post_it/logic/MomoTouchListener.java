package mo.pay.post_it.logic;

import mo.pay.post_it.util.VibrateUtil;
import mo.pay.post_it.widget.MomoWidgetDef;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Handler;
import android.os.Message;
import android.util.FloatMath;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

public class MomoTouchListener implements OnTouchListener
{
	public static final String TAG = "MoveAndScaleTouchListener";
	
	public static final int ON_CLICK_ONCE			= 0x401;
	public static final int ON_CLICK_TWICE			= 0x402;
	public static final int ON_MULTI_SCALE_UP		= 0x403;
	public static final int ON_MULTI_SCALE_DOWN		= 0x404;
	public static final int ON_MULTI_MOVE			= 0x405;
	public static final int ON_TOUCHED				= 0x406;
	public static final int ON_CLICKED				= 0x407;
	public static final int ON_ACTION_UP_AFTER_MOVE	= 0x408;
	public static final int ON_DOUBLE_CLICK			= 0x409;
	public static final int ON_LONG_CLICK			= 0x410;
	public static final int ON_ROTATE				= 0x411;
	public static final int ON_EDIT_START			= 0x412;
	public static final int ON_EDIT_END				= 0x413;
	
	public static final int THRESHOLD_MOVE_ITEM     = 4;
	public static final int THRESHOLD_MULTI_TOUCH   = 30;
	
	private boolean 		_isMultiTouch 	= false;
	private boolean 		_isSingleTouch 	= false;
	private boolean 		_isEditing		= false;
	private PointF 			start 			= new PointF();
	private float 			oldDist 		= 1f;
	private float 			newDist 		= 1f;
	
	//For Rotate
	private static final int X0 = 0;
	private static final int Y0 = 1;
	private static final int X1 = 2;
	private static final int Y1 = 3;
	private Float[]	firstActionPointer 	= new Float[]{0f,0f,0f,0f}; 
	private Float[]	newActionPointer 	= new Float[]{0f,0f,0f,0f}; 
	
	private boolean 		_click 		= false;
	private int				_current_action = 0;
	
	//For Move
	private int			new_left 			= 0;
	private int			new_top 			= 0;
	private int			first_down_x	 	= 0;
	private int 		first_down_y	 	= 0;
	
	private int			raw_x_down	 	= 0;
	private int 		raw_y_down	 	= 0;
	
	//For Double Click
	private int		_down_counter					= 0;
	private boolean _is_double_click_timeout		= true;
	private int		DOUBLE_CLICK_TIMEOUT_DURATION 	= 300;	
	
	//For Long Click
	private boolean _is_long_click		= true;
	
	private boolean _isMultiZoom		= false;
	
	private Handler		handler   			= null;
	private View		parent_control   	= null;
	private View		control_inside   	= null;
	private int			screen_w 			= 0;
	private int			screen_h 			= 0;
	
	private Point		mini_size			= new Point(100,100);
	
	private Message		message = null;
	
	private Handler _double_click_timer_handler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch(msg.what)
			{
				case DoubleClickEventTimeCounter.ON_TIMER_START:
					
					_is_double_click_timeout = false;
					
					break;
					
				case DoubleClickEventTimeCounter.ON_DOUBLE_CLICK_TIMEOUT:
					
					_is_double_click_timeout = true;
					
					break;
					
				case DoubleClickEventTimeCounter.ON_TIMER_STOP:
					
					_is_double_click_timeout = true;
					
					if(_down_counter == 1 && _current_action == MotionEvent.ACTION_UP)
					{
						if(handler!=null)
						{
							handler.sendEmptyMessage(ON_CLICKED);
						}
					}
					
					_down_counter = 0;
					
					break;
			}
		}
	};
	
	private Handler _long_click_timer_handler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch(msg.what)
			{
				case LongClickEventTimeCounter.ON_TIMER_START:
					
					_is_long_click = false;
					
					break;
					
				case LongClickEventTimeCounter.ON_LONG_CLICK:
					
					_is_long_click = true;
					
					if(handler!=null)
					{
						if(_isEditing)
						{
							handler.sendEmptyMessage(ON_LONG_CLICK);
						}
						else
						{
							handler.sendEmptyMessage(ON_EDIT_START);
							_isEditing = true;
						}
					}
					
					VibrateUtil.doVibrate(100);
					
					break;
					
				case LongClickEventTimeCounter.ON_TIMER_STOP:
					
					_is_long_click = true;
					
					break;
			}
		}
	};
	
	private DoubleClickEventTimeCounter _double_click_timer = null;
	private LongClickEventTimeCounter 	_long_click_timer 	= null;
	
	public MomoTouchListener
	(
		int screenW, 
		int screenH, 
		View parentView,  
		Handler aHandler
	)
	{
		screen_w 		= screenW;
		screen_h 		= screenH;
		parent_control  = parentView;
		control_inside	= parent_control.findViewById(MomoWidgetDef.ID_SUB_VIEW);
		handler  		= aHandler;
		
		_double_click_timer = new DoubleClickEventTimeCounter(_double_click_timer_handler);
		_double_click_timer.setTimeOutValue(DOUBLE_CLICK_TIMEOUT_DURATION);
		
		_long_click_timer = new LongClickEventTimeCounter(_long_click_timer_handler);
	}
	
	public void setScreenSize(int width, int height)
	{
		screen_w = width;
		screen_h = height;
	}
	
	public void setMiniSize(Point size)
	{
		mini_size = size;
	}
	
	public void disableEdit()
	{
		_isEditing = false;
	}
	
	public void enableEdit()
	{
		_isEditing = true;
	}
	
	private float getDegrees(MotionEvent rawEvent)
	{
		newActionPointer[X0] = rawEvent.getX(0);
		newActionPointer[Y0] = rawEvent.getY(0);
		newActionPointer[X1] = rawEvent.getX(1);
		newActionPointer[Y1] = rawEvent.getY(1);
		
		float m1 = (firstActionPointer[X1]-firstActionPointer[X0])/
						(firstActionPointer[X1]-firstActionPointer[Y0]);
		
		float m2 = (newActionPointer[X1]-newActionPointer[X0])/
				(newActionPointer[X1]-newActionPointer[Y0]);

		return (float) Math.toDegrees(Math.atan((m1-m2)/(1+(m1*m2))));
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
		if(handler != null)
		{
			handler.sendEmptyMessage(ON_TOUCHED);
		}
		
		WrapMotionEvent event = WrapMotionEvent.wrap(rawEvent);
		
		// Handle touch events here...
		switch (event.getAction() & MotionEvent.ACTION_MASK)
		{
		case MotionEvent.ACTION_DOWN:

			_current_action = MotionEvent.ACTION_DOWN;
			
			//Long click start count
			if(!_long_click_timer.isCounting())
			{
				_long_click_timer.startCount();
			}
			//First Down
			if(!_double_click_timer.isCounting())
			{
				_double_click_timer.startCount();
			}
			//After First Down
			if(_down_counter<2)
			{
				_down_counter++;
			}
			
			_isSingleTouch = true;
			_isMultiTouch = false;
			
			start.set(event.getX(), event.getY());
			
			first_down_x = (int) rawEvent.getX();
			first_down_y = (int) rawEvent.getY();
			
			raw_x_down = (int) rawEvent.getRawX();
			raw_y_down = (int) rawEvent.getRawY();
			
			org_params = (LayoutParams) parent_control.getLayoutParams();
			
			break;

		case MotionEvent.ACTION_POINTER_DOWN:

			if(_isEditing && _long_click_timer.isCounting())
			{
				_long_click_timer.stopCount();
			}
			
			_current_action = MotionEvent.ACTION_POINTER_DOWN;
			
			_isMultiTouch = true;
			_isSingleTouch = false;

			oldDist = spacing(event);

			firstActionPointer[X0] = rawEvent.getX(0);
			firstActionPointer[Y0] = rawEvent.getY(0);
			firstActionPointer[X1] = rawEvent.getX(1);
			firstActionPointer[Y1] = rawEvent.getY(1);
			
			break;

		case MotionEvent.ACTION_UP:

			if(_isEditing && _isMultiZoom)
			{
				//If Size too small
				if(parent_control.getWidth()<mini_size.x)
				{
					resetToMinWidth();
				}
				if(parent_control.getHeight()<mini_size.y)
				{
					resetToMinHeight();
				}
				
				_isMultiZoom = false;
			}
			
			if(_long_click_timer.isCounting())
			{
				_long_click_timer.stopCount();
			}
			
			_current_action = MotionEvent.ACTION_UP;
			
			_isSingleTouch = false;
			
			float x = Math.abs((int)rawEvent.getRawX() - raw_x_down);
			float y = Math.abs((int)rawEvent.getRawY() - raw_y_down);
			float space =  FloatMath.sqrt(x * x + y * y);
			
			if(space<8)
			{
				if(_down_counter == 1)
				{
					if(_is_double_click_timeout)
					{
						if(handler!=null)
						{
							handler.sendEmptyMessage(ON_CLICKED);
						}
					}
				}
				if(_down_counter == 2)
				{
					if(!_is_double_click_timeout)
					{
						//Double Click
						if(handler!=null)
						{
							handler.sendEmptyMessage(ON_DOUBLE_CLICK);
						}
						
						return true;
					}
					else
					{
						if(handler!=null)
						{
							handler.sendEmptyMessage(ON_CLICKED);
						}
					}
				}
				
				//Click
				if(!_click)
				{
					if(handler!=null)
					{
						handler.sendEmptyMessage(ON_CLICK_ONCE);
					}
				}
				else
				{
					if(handler!=null)
					{
						handler.sendEmptyMessage(ON_CLICK_TWICE);
					}
				}
				
				_click = !_click;
			}
			else
			{
				if(handler!=null)
				{
					Message msg = new Message();
					msg.what = ON_ACTION_UP_AFTER_MOVE;
					msg.obj = v;
					
					handler.sendMessage(msg);
				}
			}
			
			break;

		case MotionEvent.ACTION_POINTER_UP:

			_current_action = MotionEvent.ACTION_POINTER_UP;
			
			_isMultiTouch = false;

			if ((newDist - oldDist) > 0)
			{

			}
			
			break;

		case MotionEvent.ACTION_MOVE:
			
			_current_action = MotionEvent.ACTION_MOVE;
			
			float xx = first_down_x - rawEvent.getX();
			float yy = first_down_y - rawEvent.getY();
			float moveDist = FloatMath.sqrt(xx * xx + yy * yy);
			
			if(!_isEditing && moveDist>=THRESHOLD_MULTI_TOUCH)
			{
				if(_long_click_timer.isCounting())
				{
					_long_click_timer.stopCount();
				}
			}
			
			if (_isEditing && _isMultiTouch)
			{
				_isMultiZoom = true;
				
				float degrees = getDegrees(rawEvent);
				
				if(handler != null)
				{
					message = new Message();
					message.what = ON_ROTATE;
					message.obj  = degrees;
					handler.handleMessage(message);
				}
				
				newDist = spacing(event);
				//Scale Item
				if ((newDist - oldDist) >= THRESHOLD_MULTI_TOUCH)
				{
					if(handler!=null)
					{
						resizeItem();
						
						message = new Message();
						message.what = ON_MULTI_SCALE_UP;
						//Change offset
						Float increase = new Float(newDist - oldDist);
						message.obj  = increase.intValue();
						handler.handleMessage(message);
					}
				}
				else if ((oldDist - newDist) >= THRESHOLD_MULTI_TOUCH)
				{
					if(handler!=null)
					{
						resizeItem();
						
						message = new Message();
						message.what = ON_MULTI_SCALE_DOWN;
						//Change offset
						Float decrease = new Float(newDist - oldDist);
						message.obj  = decrease.intValue();
						handler.handleMessage(message);
					}
				}
			}
			else if(_isEditing && _isSingleTouch)
			{
				if (moveDist >= THRESHOLD_MOVE_ITEM)
				{
					if(_long_click_timer.isCounting())
					{
						_long_click_timer.stopCount();
					}
				}
				
				//Move Item
				moveItem((int)rawEvent.getX(),(int)rawEvent.getY());
			}
			
			break;
		}

		return true;
	}
	
	private void moveItem(int x, int y)
	{
		new_left = parent_control.getLeft()+(x-first_down_x);
		new_top = parent_control.getTop()+(y-first_down_y);
		
		if(control_inside != null)
		{
			if(new_left+parent_control.getWidth() > screen_w+((parent_control.getWidth()-control_inside.getWidth())/2))
			{
				new_left = screen_w- parent_control.getWidth()+((parent_control.getWidth()-control_inside.getWidth())/2);
			}
			if(new_left<-((parent_control.getWidth()-control_inside.getWidth())/2))
			{
				new_left=-((parent_control.getWidth()-control_inside.getWidth())/2);
			}
			if((new_top+parent_control.getHeight()) > screen_h+((parent_control.getHeight()-control_inside.getHeight())/2))
			{
				new_top = screen_h-parent_control.getHeight()+((parent_control.getHeight()-control_inside.getHeight())/2);
			}
			if(new_top<-((parent_control.getHeight()-control_inside.getHeight())/2))
			{
				new_top=-((parent_control.getHeight()-control_inside.getHeight())/2);
			}
		}
		else
		{
			if(new_left+parent_control.getWidth() > screen_w)
			{
				new_left = screen_w- parent_control.getWidth();
			}
			if(new_left < 0)
			{
				new_left = 0;
			}
			if((new_top+parent_control.getHeight()) > screen_h)
			{
				new_top = screen_h-parent_control.getHeight();
			}
			if(new_top < 0)
			{
				new_top = 0;
			}
		}
		
		FrameLayout.LayoutParams layoutparams = new FrameLayout.LayoutParams(parent_control.getWidth(), parent_control.getHeight());
		layoutparams.gravity = Gravity.LEFT | Gravity.TOP;
		layoutparams.setMargins
		(
			(int)(new_left),
			(int)(new_top), 
			0, 0
		);
		
		parent_control.setLayoutParams(layoutparams);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private void resetToMinWidth()
	{
		if(params != null)
		{
			params.leftMargin = org_params.leftMargin;
			params.topMargin = org_params.topMargin;
			params.rightMargin = org_params.rightMargin;
			params.bottomMargin = org_params.bottomMargin;
			
			params.width = mini_size.x;
			
			parent_control.setLayoutParams(params);
		}
		else
		{
			parent_control.setLayoutParams(new LayoutParams(mini_size.x,mini_size.y));
		}
	}
	
	private void resetToMinHeight()
	{
		if(params != null)
		{
			params.leftMargin = org_params.leftMargin;
			params.topMargin = org_params.topMargin;
			params.rightMargin = org_params.rightMargin;
			params.bottomMargin = org_params.bottomMargin;
			
			params.width = mini_size.y;
			
			parent_control.setLayoutParams(params);
		}
		else
		{
			parent_control.setLayoutParams(new LayoutParams(mini_size.x,mini_size.y));
		}
	}
	
	
	
	
	private int lastChangeOffset = 0;
	private FrameLayout.LayoutParams params;
	private FrameLayout.LayoutParams org_params;
	
	private void resizeItem()
	{
		params = new FrameLayout.LayoutParams(parent_control.getWidth(), parent_control.getHeight());
		params.gravity = Gravity.LEFT | Gravity.TOP;
		
		int new_w = 0;
		int new_h = 0;
		
		int change = (int) (newDist - oldDist)/4;
		
		//Prevent change continually
		if(lastChangeOffset == change)
		{
			return;
		}
		
		lastChangeOffset = change;
		
		params.leftMargin = parent_control.getLeft();
		params.topMargin = parent_control.getTop();
		params.bottomMargin = parent_control.getBottom();
		params.rightMargin = parent_control.getRight();
		
		if(change>0)
		{
			//Size increase
			new_w = parent_control.getWidth() + Math.abs(change)/4;
			new_h = parent_control.getHeight() + Math.abs(change)/4;
			
			params.leftMargin = parent_control.getLeft()-Math.abs(change)/4;
			params.topMargin = parent_control.getTop()-Math.abs(change)/4;
			params.bottomMargin = parent_control.getBottom()-Math.abs(change)/4;
			params.topMargin = parent_control.getTop()-Math.abs(change)/4;
		}
		
		if(change<0)
		{
			//Size decrease
			new_w = parent_control.getWidth() - Math.abs(change)/4;
			new_h = parent_control.getHeight() - Math.abs(change)/4;
			
			params.leftMargin = parent_control.getLeft()+Math.abs(change)/4;
			params.topMargin = parent_control.getTop()+Math.abs(change)/4;
			params.bottomMargin = parent_control.getBottom()+Math.abs(change)/4;
			params.topMargin = parent_control.getTop()+Math.abs(change)/4;
		}
		
		boolean isMaxWidth  = false;
		
		if(new_w>screen_w)
		{
			new_w = screen_w-parent_control.getLeft();
			
			params.width  = new_w;
			
			isMaxWidth = true;
		}
		
		if(!isMaxWidth)
		{
			if(new_w<screen_w && new_w>0)
			{
				params.width  = new_w;
			}
			if(new_h >screen_h)
			{
				new_h = screen_h - parent_control.getHeight();
				
				params.height = new_h;
			}
			if(new_h<screen_h && new_h>0)
			{
				params.height = new_h;
			}
		}
		
		
		if(params.leftMargin+params.width > screen_w)
		{
			params.leftMargin = screen_w- params.width;
		}
		if(params.leftMargin<0)
		{
			params.leftMargin=0;
		}
		if((params.topMargin+params.height) > screen_h )
		{
			params.topMargin = screen_h-params.height;
		}
		if(params.topMargin<0)
		{
			params.topMargin=0;
		}
		
		parent_control.setLayoutParams(params);
	}
	
	
	
	
}
