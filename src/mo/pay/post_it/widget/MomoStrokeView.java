package mo.pay.post_it.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class MomoStrokeView extends View 
{  
	private int 	_color 		= Color.BLACK;
	private int 	_stroke 	= 5;
	private int 	_alpha	 	= 255;
	private Paint	_paint 		= new Paint();
	private Paint	_borderPaint = new Paint();
	 
	public MomoStrokeView(Context context) 
	{
		super(context);
		init();
	} 

	public MomoStrokeView(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		init();
	}
	
	public MomoStrokeView(Context context, AttributeSet attrs, int defStyle) 
	{
		super(context, attrs, defStyle);
		init();
	}
	
	private void init()
	{
		_paint.setAntiAlias(true);
		_borderPaint.setAntiAlias(true);
		_borderPaint.setColor(Color.parseColor("#FFFFFF"));
	}
	
	public void setColor(int color)
	{
		_color = color;
		_paint.setColor(_color);
		_paint.setAlpha(_alpha);
		postInvalidate();
	}
	
	public void setStroke(int stroke)
	{
		_stroke = stroke;
		_paint.setStrokeWidth(_stroke);
		postInvalidate();
	}
	
	public void setPaint(Paint paint)
	{
		_paint = paint;
		_color = _paint.getColor();
		_stroke = (int) _paint.getStrokeWidth();
		postInvalidate();
	}
	
	public void setPaintAlpha(int alpha)
	{
		_alpha = alpha;
		_paint.setAlpha(_alpha);
		postInvalidate();
	}

	
	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		
		canvas.drawCircle(getWidth()/2, getHeight()/2, _stroke+3, _borderPaint);
		
//		_paint.setColor(_color);
		canvas.drawCircle(getWidth()/2, getHeight()/2, _stroke, _paint);
	}
}
