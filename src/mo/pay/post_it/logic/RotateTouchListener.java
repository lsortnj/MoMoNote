package mo.pay.post_it.logic;

import android.graphics.Matrix;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout.LayoutParams;

public class RotateTouchListener implements OnTouchListener 
{
	public static final String TAG = "RotateTouchListener";
	
	private int			first_drag_x		= 0;
	private int			first_drag_y		= 0;
	private boolean 	isRotating			= false;
	private double		degrees				= 0;
	private int			screen_w 			= 0;
	private int			screen_h 			= 0;
	private Matrix 		matrix 				= new Matrix();
	
	private View		view				= null;
	
	public RotateTouchListener(int screenW, int screenH, View parentView)
	{
		screen_w = screenW;
		screen_h = screenH;
		view     = parentView;
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		view.setRotation((int)degrees);
	      // Handle touch events here...
		switch (event.getAction() & MotionEvent.ACTION_MASK) 
		{
	    	case MotionEvent.ACTION_DOWN:
		         
			    break;
			    
			case MotionEvent.ACTION_UP:
				degrees = 0;
			    break;
			    
			case MotionEvent.ACTION_MOVE:
			     
				int centerX = view.getLayoutParams().width/2;
				int centerY = view.getLayoutParams().height/2;
				
				degrees = getDegreesFromTouchEvent(event.getX(),event.getY(),centerX,centerY);
				Log.e(TAG, "CenterX:"+centerX+"  CenterY:"+centerY+"  eX:"+event.getX()+"  eY:"+event.getY()+"  Degrees: "+degrees);
			    break;
		}
		return true;
	}
	
	private double getDegreesFromTouchEvent(float touch_x, float touch_y,float center_x, float center_y){
		double tx = touch_x - center_x, ty = touch_y - center_y;
		double t_length = Math.sqrt(tx*tx + ty*ty);
		double a = Math.acos(ty / t_length);
		
		return Math.atan2(touch_y - center_y, touch_x - center_x) * 180 / Math.PI;
    }

}
