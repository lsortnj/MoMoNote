package mo.pay.post_it.clipWeb;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import mo.pay.post_it.logic.WrapMotionEvent;
import mo.pay.post_it_handDraw.HandDraw;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PathEffect;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class ClipView extends View
{ 
	public static final String TAG = "ClipView";
	
	public static final int THRESHOLD_DRAW = 15;
	public static final int CROP_IMAGE_GAP = 5;
	
	public static final int STROKE_WIDTH_VERY_THIN 		= 3;
	public static final int STROKE_WIDTH_THIN 			= 5;
	public static final int STROKE_WIDTH_MEDIUM 		= 8;
	public static final int STROKE_WIDTH_HEAVY 			= 15;
	
	public static final int CLIP_SHAPE_FREE 	= 0x500;
	public static final int CLIP_SHAPE_RECT 	= 0x501;
	public static final int CLIP_SHAPE_CIRCLE 	= 0x502;
	

	public static final int ON_NOTE_START_PAINT = 0x900;
	
	private Bitmap 		_bitmap 		= null;
	private Bitmap      _bitmapSnapshot = null;
	private Bitmap      _bitmapAfterCrop = null;
	private boolean 	_is_draw 		= true;
	
	private IClipWebContentListener _listener = null;
	
	//Paint
	private Paint 		_paint 				= new Paint();
	private int 		_paintStrokeWidth 	= STROKE_WIDTH_VERY_THIN;
	private int 		_paintColor 		= Color.RED;
	
	private int 		_clipShape			= CLIP_SHAPE_FREE;
	private int			_toolBarHeight		= 0;
	
	private Path 		_path 				= new Path();
	private String 		_savePath 			= "";
	
	public ClipView(Context context)
	{
		super(context);
		init();
	}

	public ClipView(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		init();
	}
	
	public ClipView(Context context, AttributeSet attrs, int defStyle) 
	{
		super(context, attrs, defStyle);
		init();
	}
	
	public void setWebClipListener(IClipWebContentListener listener)
	{
		_listener = listener;
	}
	
	public void setToolBarHeight(int toolbarHeight)
	{
		_toolBarHeight = toolbarHeight;
	}
	
	public Bitmap getWebviewSnapshot()
	{
		return _bitmapSnapshot;
	}

	public void setClipShape(int clipShape)
	{
		_clipShape = clipShape;
		
		eraseClip();
	}
	
	public void eraseClip()
	{
		if(mCanvas != null)
			mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
	}
	
	public void setWebviewSnapshot(Bitmap bitmapSnapshot)
	{
		this._bitmapSnapshot = bitmapSnapshot;
		
		if(_bitmapAfterCrop != null)
			_bitmapAfterCrop.recycle();
		
		_bitmapAfterCrop = Bitmap.createBitmap(
   			 _bitmapSnapshot.getWidth(), _bitmapSnapshot.getHeight(),
   			 Bitmap.Config.ARGB_8888);
	}

	private void init()
	{
		PathEffect effect = new DashPathEffect(new float[] { 1, 2, 4, 8}, 1);
		
		//Initial Normal
		_paint.reset();
		_paint.setAntiAlias(true);
		_paint.setColor(_paintColor);
		_paint.setStyle(Paint.Style.STROKE);
		_paint.setStrokeJoin(Paint.Join.ROUND);
		_paint.setDither(true);
		_paint.setStrokeCap(Paint.Cap.ROUND);
		_paint.setStrokeWidth(_paintStrokeWidth);
		_paint.setPathEffect(effect);
//		_paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));

		_path.reset();
		_path.setFillType(Path.FillType.WINDING);

		_bitmapSnapshot = null;
		
		_is_draw = true;
	}

	public Paint getNormalPaint()
	{
		return _paint;
	}
	
	public int getNormalPaintColor()
	{
		return _paint.getColor();
	}
	
	public void setNormalColor(int color)
	{
		_paintColor = color;
		_paint.setColor(_paintColor);
	}
	
	public void setNormalStrokeWidth(int width)
	{
		_paintStrokeWidth = width;
		_paint.setStrokeWidth(_paintStrokeWidth);
	}
	
	public int getNormalStrokeWidth()
	{
		return _paintStrokeWidth;
	}
	
	public String getNoteSavePath()
	{
		return _savePath;
	}

	public boolean isDraw()
	{
		return _is_draw;
	}

	public void reset()
	{
		eraseClip();
		
		try
		{
			if(_bitmapAfterCrop != null)
				_bitmapAfterCrop.recycle();
		}catch(Exception e){e.printStackTrace();}
		
		init();
	}
	
	public void destroy()
	{
		try
		{
			if(_bitmap != null)
				_bitmap.recycle();
			if(_bitmapAfterCrop != null)
				_bitmapAfterCrop.recycle();
		}catch(Exception e){e.printStackTrace();}
	}
	
	private HandDraw	_handDraw = null;
	
	public void recycleHandDrawBitmap()
	{
		try
		{
			if(_bitmap!= null)
			{
				_bitmap.recycle();
				System.gc();
			}
		}catch(Exception e){Log.d("RecycleHandDraw", e.toString());}
	}
	
	Canvas mCanvas = null;
	int  width  = 0;
	int  height = 0;
	@Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) 
	{
        super.onSizeChanged(w, h, oldw, oldh);
   
        width  = w;
        height = h;
        
        if(width > height)
        {
        	height = width;
        }
        else
        {
        	width = height;
        }
       
        if(_bitmap != null)
        {
        	try{_bitmap.recycle();}catch(Exception e){}
        }
        
        _bitmap = null;
        
        _bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
      
        
        mCanvas = new Canvas(_bitmap);
        
        if(_handDraw != null)
        {
        	try
        	{
        		mCanvas.drawBitmap(BitmapFactory.decodeFile(_handDraw.getStorePath()), 0, 0,_paint);
        	}catch(Exception e){}
        }
        
        postInvalidate(); 
    }
	
	private Paint  mBitmapPaint = new Paint(Paint.DITHER_FLAG);

	@Override
	protected void onDraw(Canvas canvas)
	{
		if(_bitmap != null)
		{
			canvas.drawBitmap(_bitmap, 0, 0, mBitmapPaint);
		}
		
        canvas.drawPath(_path, _paint);
        canvas.save();
	}
	
	private float mX, mY;
	
	//To calculate area range crop by finger
	private float minX, maxX, minY, maxY;
	
    private static final float TOUCH_TOLERANCE = 4;

    private void touch_start(float x, float y) {
    	
    	if(_bitmapAfterCrop != null)
    	{
    		try
    		{
    			_bitmapAfterCrop.recycle();
    			_bitmapAfterCrop = Bitmap.createBitmap(
    		   			 _bitmapSnapshot.getWidth(), _bitmapSnapshot.getHeight(),
    		   			 Bitmap.Config.ARGB_8888);
    		}catch(Exception e){Log.e("TouchDownEX",e.toString());}
    	}
    	
    	mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
    	_path.reset();
    	_path.moveTo(x, y);
    	
        mX = x;
        mY = y;
        maxX = x;
        minX = x;
        maxY = y;
        minY = y;
    }
    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
        	
        	switch(_clipShape)
        	{
        		case CLIP_SHAPE_FREE:
        			
        			_path.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
                	
                    mX = x;
                    mY = y;
                   
                    maxX = mX>maxX?mX:maxX;
                    minX = mX<minX?mX:minX;
                    maxY = mY>maxY?mY:maxY;
                    minY = mY<minY?mY:minY;
                   
                    
                    if(_bitmapSnapshot != null)
                    {
                    	 maxX = maxX>_bitmapSnapshot.getWidth()?_bitmapSnapshot.getWidth():maxX;
                    	 minX = minX<0?0:minX;
                    	 maxY = maxY>_bitmapSnapshot.getHeight()?_bitmapSnapshot.getHeight():maxY;
                    	 minY = minX<0?0:minY;
                    } 
        			
        			break;
        			
        		case CLIP_SHAPE_RECT:
        			
        			mX = x;
                    mY = y;
                    
                    _path.reset();
        			_path.addRect(
        					Math.min(first_x, x), Math.min(first_y, y), 
        					Math.max(first_x, x), Math.max(first_y, y), 
        					Direction.CW);
        			 
        			break;
        	}
        	
           
            _is_draw = true;
        }
    }
    
    private void touch_up() {
    	
    	switch(_clipShape)
    	{
    		case CLIP_SHAPE_FREE:
    			
    			_path.lineTo(mX, mY);
    	    	_path.lineTo(first_x, first_y);
    			
    			break;
    			 
    		case CLIP_SHAPE_RECT:
    			_path.reset();
    			_path.addRect(
    					Math.min(first_x, mX), Math.min(first_y, mY), 
    					Math.max(first_x, mX), Math.max(first_y, mY), 
    					Direction.CW);
    			break;
    	} 
    	
        mCanvas.drawPath(_path, _paint);
        
        cropImageByPath();
        
        _path.reset();
    }
    
    private void cropImageByPath()
    {
    	try
        {
        	 Canvas canvas = new Canvas(_bitmapAfterCrop);
 
        	 Paint cropPaint = new Paint();
        	 cropPaint.setAntiAlias(true);
        	 
        	 canvas.clipPath(_path);
        	 canvas.drawBitmap
        	 (
				_bitmapSnapshot, 0,0, cropPaint
        	 );
        	 
        	//將圖片縮小至選取範圍
        	switch(_clipShape)
         	{
         		case CLIP_SHAPE_FREE:
         			
         			_bitmapAfterCrop = Bitmap.createBitmap(_bitmapAfterCrop, 
               			 (int)minX<0?0:(int)minX, 
               			 (int)minY<0?0:(int)minY, 
               			 (int)(Math.abs(maxX-minX)), 
               			 (int)(Math.abs(maxY-minY)));
         			
         			break;
         			 
         		case CLIP_SHAPE_RECT:
         			
         			int startX = (int) Math.min(first_x, mX);
         			int endX   = (int) Math.max(first_x, mX);
         			int startY = (int) Math.min(first_y, mY);
         			int endY   = (int) Math.max(first_y, mY);
         			
         			startX = startX<0?0:startX;
         			endX   = endX<0?0:endX;
         			startX = startX>_bitmapAfterCrop.getWidth()?_bitmapAfterCrop.getWidth():startX;
         			endX   = endX>_bitmapAfterCrop.getWidth()?_bitmapAfterCrop.getWidth():endX;
         			
         			startY = startY<0?0:startY;
         			endY   = endY<0?0:endY;
         			startY = startY>_bitmapAfterCrop.getHeight()?_bitmapAfterCrop.getHeight():startY;
         			endY   = endY>_bitmapAfterCrop.getHeight()?_bitmapAfterCrop.getHeight():endY;
         			
         			_bitmapAfterCrop = Bitmap.createBitmap(_bitmapAfterCrop, 
         					startX, startY, 
         					Math.abs(endX-startX), Math.abs(endY-startY));
         			break;
         	} 
        	 
        	 
        	 if(_listener != null)
        		 _listener.onContentCliped(_bitmapAfterCrop);
        	 
        }catch(Exception e){e.printStackTrace();}
    }
    
    public void saveImageAfterCrop(String savePath)
    {
    	try
		{
			_bitmapAfterCrop.compress(CompressFormat.PNG, 100, new FileOutputStream(savePath));
			
		} catch (FileNotFoundException e){e.printStackTrace();}
    }

   
    float first_x = 0;
    float first_y = 0;
    
    MotionEvent downEvent = null;
    
	@Override
	public boolean onTouchEvent(MotionEvent rawEvent)
	{
		WrapMotionEvent event = WrapMotionEvent.wrap(rawEvent);
		
		float x = event.getX();
		float y = event.getY();
		
		switch (event.getAction() & MotionEvent.ACTION_MASK)
		{
			case MotionEvent.ACTION_DOWN:
				
				first_x = event.getX();
			    first_y = event.getY();
			    
			    downEvent = rawEvent;
			    
				touch_start(x, y);
				invalidate();
				return true;
	
			case MotionEvent.ACTION_UP:
	
				touch_up();
				invalidate();
				return true;
	
			case MotionEvent.ACTION_MOVE:
			
				float xx = first_x - event.getX();
				float yy = first_y - event.getY();
				float moveDist = FloatMath.sqrt(xx * xx + yy * yy);
				
				if(moveDist>=THRESHOLD_DRAW)
				{
					 
				}
				
				touch_move(x, y);
				invalidate();
				return true;
				
				default:
					return true;
		}
		
	}
}