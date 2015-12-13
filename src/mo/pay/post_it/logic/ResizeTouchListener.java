package mo.pay.post_it.logic;

import android.graphics.Point;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

public class ResizeTouchListener implements OnTouchListener
{
	public static final int	RESIZE_LEFT_TOP			= 0x201;
	public static final int	RESIZE_LEFT_BOTTOM		= 0x202;
	public static final int	RESIZE_RIGHT_TOP		= 0x203;
	public static final int	RESIZE_RIGHT_BOTTOM		= 0x204;
	
	//For Resize
	private int			first_drag_x		= 0;
	private int			first_drag_y		= 0;
	private int  		current_drag_id		= -1;
	
	private FrameLayout.LayoutParams params;
	private FrameLayout.LayoutParams org_params;
	
	private Point		mini_size			= new Point(100,100);
	
	private int			screen_w 			= 0;
	private int			screen_h 			= 0;
	
	private View		parent_control		= null;
	
	private boolean isMove = false;
	
	public ResizeTouchListener(int screenW, int screenH, View parentView)
	{
		screen_w = screenW;
		screen_h = screenH;
		parent_control  = parentView;
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
	
	private void resetToMinWidth()
	{
		params.leftMargin = org_params.leftMargin;
		params.topMargin = org_params.topMargin;
		params.rightMargin = org_params.rightMargin;
		params.bottomMargin = org_params.bottomMargin;
		
		params.width = mini_size.x;
		
		parent_control.setLayoutParams(params);
	}
	
	private void resetToMinHeight()
	{
		params.leftMargin = org_params.leftMargin;
		params.topMargin = org_params.topMargin;
		params.rightMargin = org_params.rightMargin;
		params.bottomMargin = org_params.bottomMargin;
		
		params.height = mini_size.y;
		
		parent_control.setLayoutParams(params);
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent rawEvent)
	{
		current_drag_id = v.getId();
		
		WrapMotionEvent event = WrapMotionEvent.wrap(rawEvent);

		// Handle touch events here...
		switch (event.getAction() & MotionEvent.ACTION_MASK)
		{
			case MotionEvent.ACTION_DOWN:

				first_drag_x	= (int) event.getX();
				first_drag_y	= (int) event.getY();
				
				org_params = (LayoutParams) parent_control.getLayoutParams();
				
				break;

			case MotionEvent.ACTION_UP:

				if(isMove)
				{
					if(parent_control.getWidth()<mini_size.x)
					{
						resetToMinWidth();
					}
					if(parent_control.getHeight()<mini_size.y)
					{
						resetToMinHeight();
					}
				}
				
				isMove = false;
				
				break;

			
			case MotionEvent.ACTION_MOVE:
				
				isMove = true;
				
				resizeItem((int) rawEvent.getX(),(int) rawEvent.getY());
				
				break;
		}

		return true;
	}
	
	private void resizeItem(int x, int y)
	{
		params = new FrameLayout.LayoutParams(parent_control.getWidth(), parent_control.getHeight());
//		params = new FrameLayout.LayoutParams(sub_view.getMeasuredWidth()+70, sub_view.getMeasuredHeight()+70);
		
		params.gravity = Gravity.LEFT | Gravity.TOP;
		
		int new_w = 0;
		int new_h = 0;
		
		int w_change = x - first_drag_x;
		int h_change = y - first_drag_y;
		
		params.leftMargin = parent_control.getLeft();
		params.topMargin = parent_control.getTop();
		params.bottomMargin = parent_control.getBottom();
		params.rightMargin = parent_control.getRight();
		
		switch(current_drag_id)
		{
			case RESIZE_LEFT_TOP:
				
				//←Pull to left
				if(w_change<0)
				{
					//Width increase
					new_w = parent_control.getWidth() + Math.abs(w_change);
					params.leftMargin = parent_control.getLeft()-Math.abs(w_change);
				}
				//→Pull to right
				if(w_change>0)
				{
					//Width decrease
					new_w = parent_control.getWidth()-w_change;
					params.leftMargin = parent_control.getLeft()+w_change;
				}
				
				//↖ Pull up
				if(h_change<0)
				{
					//Height increase
					new_h = parent_control.getHeight() + Math.abs(h_change);
					params.topMargin = parent_control.getTop()- Math.abs(h_change);
				}
				//↘ Pull down
				if(h_change>0)
				{
					//Height decrease
					new_h = parent_control.getHeight()-h_change;
					params.topMargin = parent_control.getTop()+h_change;
				}
				
				break;
				
			case RESIZE_LEFT_BOTTOM:
				
				//←Pull to left
				if(w_change<0)
				{
					//Width increase
					new_w = parent_control.getWidth() + Math.abs(w_change);
					params.leftMargin = parent_control.getLeft()-Math.abs(w_change);
				}
				//→Pull to right
				if(w_change>0)
				{
					//Width decrease
					new_w = parent_control.getWidth()-w_change;
					params.leftMargin = parent_control.getLeft()+w_change;
				}
				
				//↖ Pull up
				if(h_change<0)
				{
					//Height decrease
					new_h = parent_control.getHeight() - Math.abs(h_change);
					params.bottomMargin = parent_control.getBottom()+ Math.abs(h_change);
				}
				//↘ Pull down
				if(h_change>0)
				{
					//Height increase
					new_h = parent_control.getHeight()+h_change;
					params.bottomMargin = parent_control.getBottom()-h_change;
				}
				
				break;
				
			case RESIZE_RIGHT_TOP:
				
				//Pull to left
				if(w_change<0)
				{
					//Width decrease
					new_w = parent_control.getWidth() - Math.abs(w_change);
					params.rightMargin = parent_control.getRight()-Math.abs(w_change);
				}
				//Pull to right
				if(w_change>0)
				{
					//Width increase
					new_w = parent_control.getWidth()+w_change;
					params.rightMargin = parent_control.getRight()+w_change;
				}
				
				//Pull up
				if(h_change<0)
				{
					//Height increase
					new_h = parent_control.getHeight() + Math.abs(h_change);
					params.topMargin = parent_control.getTop()- Math.abs(h_change);
				}
				//Pull down
				if(h_change>0)
				{
					//Height decrease
					new_h = parent_control.getHeight()-h_change;
					params.topMargin = parent_control.getTop()+h_change;
				}
				
				break;
				
			case RESIZE_RIGHT_BOTTOM:
				
				//Pull to left
				if(w_change<0)
				{
					//Width decrease
					new_w = parent_control.getWidth() - Math.abs(w_change);
					params.rightMargin = parent_control.getRight()-Math.abs(w_change);
				}
				//Pull to right
				if(w_change>0)
				{
					//Width increase
					new_w = parent_control.getWidth()+w_change;
					params.rightMargin = parent_control.getRight()+w_change;
				}
				
				//Pull up
				if(h_change<0)
				{
					//Height decrease
					new_h = parent_control.getHeight() - Math.abs(h_change);
					params.bottomMargin = parent_control.getTop()+ Math.abs(h_change);
				}
				//Pull down
				if(h_change>0)
				{
					//Height increase
					new_h = parent_control.getHeight()+h_change;
					params.bottomMargin = parent_control.getTop()-h_change;
				}
				
				break;
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
