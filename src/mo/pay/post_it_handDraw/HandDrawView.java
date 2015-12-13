package mo.pay.post_it_handDraw;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mo.pay.post_it.logic.LongClickEventTimeCounter;
import mo.pay.post_it.logic.WrapMotionEvent;
import mo.pay.post_it_album.AlbumPageView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class HandDrawView extends View
{
	/** Need to track this so the dirty region can accommodate the stroke. **/

	public static final String TAG = "HandDrawView";
	
	public static final int THRESHOLD_DRAW = 15;
	
	public static final String KEY_PATH  = "Path";
	public static final String KEY_PAINT = "Paint";
	
	public static final int STROKE_WIDTH_VERY_THIN 		= 3;
	public static final int STROKE_WIDTH_THIN 			= 5;
	public static final int STROKE_WIDTH_MEDIUM 		= 8;
	public static final int STROKE_WIDTH_HEAVY 			= 15;
	public static final int STROKE_WIDTH_HIGHLIGHT_INIT = 20;
	public static final int STROKE_WIDTH_ERASER 		= 26;

	public static final int STATE_UNKNOW 			= -1;
	public static final int STATE_PAINT_NORMAL 		= 0;
	public static final int STATE_PAINT_HIGHLIGHT 	= 1;
	public static final int STATE_ERASER 			= 2;
	public static final int STATE_PAINT_DISABLE 	= 3;
	
	public static final int ALPHA_NONE 				= 0;
	public static final int ALPHA_MIN 				= 64;
	public static final int ALPHA_MAX 				= 200;
	
	
	public static final int ON_NOTE_START_PAINT = 0x900;
	
	private Bitmap 		_note_bitmap 				= null;
	private boolean 	_is_draw 					= true;
//	private boolean 	_isInitailBitmap			= false;
//	private boolean 	_isResotreBitmap			= false;
	
	//Normal Paint
	private Paint 		_normalPaint 				= new Paint();
	private int 		_normalStrokeWidth 			= STROKE_WIDTH_VERY_THIN;
	private int 		_normalColor 				= Color.BLACK;
	
	
	//Highlight Paint
	private Paint 		_highlightPaint 			= new Paint();
	private int 		_highlightStrokeWidth 		= STROKE_WIDTH_HIGHLIGHT_INIT;
	private int 		_highlightColor 			= Color.BLACK;
	
	//Eraser Paint
	private Paint 		_eraserPaint 				= new Paint();
	private int 		_eraserStrokeWidth 			= STROKE_WIDTH_ERASER;
	private int 		_eraserColor 				= Color.TRANSPARENT;
	
	//Current Paint
	private Paint 		_currentPaint 				= _normalPaint;
	private int			_currentStrokeWidth			= _normalStrokeWidth;
	private int			_currentColor				= _normalColor;
	
	private Path 		_path 						= new Path();
	private List<Map<String,Object>>  _pathHistory	= new ArrayList<Map<String,Object>>();
	private String 		_note_save_path 			= "";
	
	private int 		_current_paint_state 		= STATE_UNKNOW;
	private int 		_alpha		 				= ALPHA_MIN;
	
	private LongClickEventTimeCounter 	_long_click_timer 	= null;
	private boolean _is_long_click		= false;
	
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
					
					int metaState = 0;
					
					MotionEvent motionEvent = MotionEvent.obtain(
							SystemClock.uptimeMillis(), 
							SystemClock.uptimeMillis(), 
						    MotionEvent.ACTION_DOWN, 
						    first_x, 
						    first_y, 
						    metaState
						);
					
					_albumPageView.performTouchBesidesHandDraw(motionEvent);
					
					break;
					
				case LongClickEventTimeCounter.ON_TIMER_STOP:
					
//					Log.e(TAG, "ON_TIMER_STOP");
//					
//					_is_long_click = false;
					
					break;
			}
		}
	};
	
	private AlbumPageView _albumPageView = null;
	
	public HandDrawView(Context context, AlbumPageView albumPageView)
	{
		super(context);

		_albumPageView = albumPageView;
		
		//Initial Normal
		_normalPaint.setAntiAlias(true);
		_normalPaint.setColor(_normalColor);
		_normalPaint.setStyle(Paint.Style.STROKE);
		_normalPaint.setStrokeJoin(Paint.Join.ROUND);
		_normalPaint.setDither(true);
		_normalPaint.setStrokeCap(Paint.Cap.ROUND);
		_normalPaint.setStrokeWidth(_normalStrokeWidth);

		//Initial Highlight
		_highlightPaint.setAntiAlias(true);
		_highlightPaint.setAlpha(_alpha);
		_highlightPaint.setColor(_highlightColor);
		_highlightPaint.setStyle(Paint.Style.STROKE);
		_highlightPaint.setDither(true);
		_highlightPaint.setStrokeCap(Paint.Cap.ROUND);
		_highlightPaint.setStrokeJoin(Paint.Join.ROUND);
		_highlightPaint.setStrokeWidth(_highlightStrokeWidth);
		
		//Initial Eraser
		_eraserPaint.setAntiAlias(true);
		_eraserPaint.setDither(true);
		_eraserPaint.setStrokeCap(Paint.Cap.ROUND);
		_eraserPaint.setAlpha(0xFF);
		_eraserPaint.setStyle(Paint.Style.STROKE);
		_eraserPaint.setStrokeJoin(Paint.Join.ROUND);
		_eraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
		_eraserPaint.setStrokeWidth(STROKE_WIDTH_ERASER);
		
		_currentPaint 				= _normalPaint;
		_currentStrokeWidth			= _normalStrokeWidth;
		_currentColor				= _normalColor;
		
		_path.setFillType(Path.FillType.WINDING);

		_is_draw = true;
		
		_long_click_timer = new LongClickEventTimeCounter(_long_click_timer_handler);
	}

	public void resetTouchEvent()
	{
		_is_long_click = false;
	}
	
	public int getCurrentPaintState()
	{
		return _current_paint_state;
	}

	public void setHighlightPaintAlpha(int alpha)
	{
		_alpha = alpha;
		_highlightPaint.setAlpha(_alpha);
	}
	
	public int getHighlightPaintAlpha()
	{
		return _alpha;
	}
	
	public void disablePaint()
	{
		_current_paint_state = STATE_PAINT_DISABLE;
	}
	
	public void enableHighlight()
	{
		_current_paint_state = STATE_PAINT_HIGHLIGHT;
		
		_highlightPaint.setAlpha(_alpha);
		
		_currentPaint 				= _highlightPaint;
		_currentStrokeWidth			= _highlightStrokeWidth;
		_currentColor				= _highlightColor;
	}
	
	public void enableNormal()
	{
		_current_paint_state = STATE_PAINT_NORMAL;

		_currentPaint 				= _normalPaint;
		_currentStrokeWidth			= _normalStrokeWidth;
		_currentColor				= _normalColor;
	}

	public void enableEraser()
	{
		_current_paint_state = STATE_ERASER;

		_currentPaint 				= _eraserPaint;
		_currentStrokeWidth			= _eraserStrokeWidth;
		_currentColor				= _eraserColor;
	}

	public void resetPath()
	{
		_path.reset();
		_currentPaint.setXfermode(null);
	}

	public int getCurrentColor()
	{
		return _currentColor;
	}
	
	public Paint getNormalPaint()
	{
		return _normalPaint;
	}
	
	public Paint getHighlightPaint()
	{
		return _highlightPaint;
	}
	
	public int getNormalPaintColor()
	{
		return _normalPaint.getColor();
	}
	
	public int getHighlightPaintColor()
	{
		return _highlightPaint.getColor();
	}

	public void setNormalColor(int color)
	{
		_normalColor = color;
		_normalPaint.setColor(_normalColor);
	}
	
	public void setHighlightColor(int color)
	{
		_highlightColor = color;
		_highlightPaint.setColor(_highlightColor);
	}
	
	public void setHighlightStrokeWidth(int width)
	{
		_highlightStrokeWidth = width;
		_highlightPaint.setStrokeWidth(_highlightStrokeWidth);
	}

	public int getHighlightStrokeWidth()
	{
		return _highlightStrokeWidth;
	}

	public void setNormalStrokeWidth(int width)
	{
		_normalStrokeWidth = width;
		_normalPaint.setStrokeWidth(_normalStrokeWidth);
	}
	
	public void setEraserStrokeWidth(int width)
	{
		_eraserStrokeWidth = width;
		_eraserPaint.setStrokeWidth(_eraserStrokeWidth);
	}
	
	public int getEraserStrokeWidth()
	{
		return _eraserStrokeWidth;
	}
	
	public int getNormalStrokeWidth()
	{
		return _normalStrokeWidth;
	}
	
	public int getCurrentStrokeWidth()
	{
		return _currentStrokeWidth;
	}

	public String getNoteSavePath()
	{
		return _note_save_path;
	}

	public boolean isDraw()
	{
		return _is_draw;
	}

	private boolean saveNoteAsFile(String aPath)
	{
		boolean result = false;

		try
		{
			Bitmap note_bitmap = null;

			File store_note = new File(aPath);

			store_note.createNewFile();

			FileOutputStream fos = new FileOutputStream(store_note);

			note_bitmap = _note_bitmap.copy(Bitmap.Config.ARGB_8888, true);

			System.gc();

			Canvas canvas = new Canvas(note_bitmap);
			canvas.drawColor(Color.TRANSPARENT);
			canvas.drawPath(_path, _currentPaint);

			note_bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);

			fos.flush();

			fos.close();

			result = true;
		} catch (Exception e)
		{
			result = false;
		}

		return result;
	}

	public boolean saveNotePaste(String path)
	{
		boolean result = saveNoteAsFile(path);

		//_note_bitmap = null;

		return result;
	}

	private HandDraw	_handDraw = null;
	
	public void drawNote(HandDraw handDraw)
	{
		_handDraw = handDraw;
		
		Bitmap note_bitmap = BitmapFactory.decodeFile(_handDraw.getStorePath());

		if( note_bitmap!= null)
		{
			_note_bitmap = note_bitmap.copy(Bitmap.Config.ARGB_8888, true);
			
//			_isResotreBitmap = true;
		}
		
		postInvalidate();
	}
	
	public void recycleHandDrawBitmap()
	{
		try
		{
			if(_note_bitmap!= null)
			{
				_note_bitmap.recycle();
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
       
        if(_note_bitmap != null)
        {
        	try{_note_bitmap.recycle();}catch(Exception e){}
        }
        
        _note_bitmap = null;
        
        _note_bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
      
        
        mCanvas = new Canvas(_note_bitmap);
        
        if(_handDraw != null)
        {
        	try
        	{
        		mCanvas.drawBitmap(BitmapFactory.decodeFile(_handDraw.getStorePath()), 0, 0,_currentPaint);
        	}catch(Exception e){}
        }
        
        postInvalidate(); 
        
//        if(!_isResotreBitmap && _note_bitmap == null)
//        {
//        	//new Hand draw
//        	 _note_bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
//             
//             mCanvas = new Canvas(_note_bitmap);
//        }
//        else
//        {
//        	mCanvas = new Canvas(_note_bitmap);
//        }
    }
	
	private Paint  mBitmapPaint = new Paint(Paint.DITHER_FLAG);

	@Override
	protected void onDraw(Canvas canvas)
	{
		if(_note_bitmap != null && !_note_bitmap.isRecycled())
		{
			canvas.drawBitmap(_note_bitmap, 0, 0, mBitmapPaint);
		} 
		
        canvas.drawPath(_path, _currentPaint);
        canvas.save();
	}
	
	private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    private void touch_start(float x, float y) {
    	_path.reset();
    	_path.moveTo(x, y);
        mX = x;
        mY = y;
    }
    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
        	_path.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
        	
        	if(_current_paint_state == STATE_ERASER)
            {
            	_path.moveTo(mX, mY);
            	_path.lineTo(x, y);
            	mCanvas.drawPath(_path, _currentPaint);
            	_path.reset();
            	_path.moveTo(x, y);
            }
        	
            mX = x;
            mY = y;
            
            _is_draw = true;
        }
    }
    
    private void touch_up() {
    	
    	if(_current_paint_state == STATE_ERASER)
        {
    		return;
        }
    	
    	_path.lineTo(mX, mY);
        // commit the path to our offscreen
        mCanvas.drawPath(_path, _currentPaint);
        
        Map<String,Object> map = new HashMap<String,Object>();
        map.put(KEY_PATH, _path);
        map.put(KEY_PAINT, _currentPaint);
        
        _pathHistory.add(map);
        
        // kill this so we don't double draw
        _path.reset();
    }

   
    float first_x = 0;
    float first_y = 0;
    
    MotionEvent downEvent = null;
    
	@Override
	public boolean onTouchEvent(MotionEvent rawEvent)
	{
		if(_is_long_click)
		{
			return false;
		}
		
		if( _current_paint_state == STATE_PAINT_DISABLE)
		{
			return false;
		}
		
		WrapMotionEvent event = WrapMotionEvent.wrap(rawEvent);
		
		float x = event.getX();
		float y = event.getY();
		
		switch (event.getAction() & MotionEvent.ACTION_MASK)
		{
			case MotionEvent.ACTION_DOWN:
				
				if(_albumPageView != null)
				{
					_albumPageView.deSelectAllViews();
				}
				
				if(!_long_click_timer.isCounting())
				{
					_long_click_timer.startCount();
				}
				
				first_x = event.getX();
			    first_y = event.getY();
			    
			    downEvent = rawEvent;
			    
				touch_start(x, y);
				invalidate();
				return true;
	
			case MotionEvent.ACTION_UP:
	
				if(_long_click_timer.isCounting())
				{
					_long_click_timer.stopCount();
				}
				
				touch_up();
				invalidate();
				return true;
	
			case MotionEvent.ACTION_MOVE:
			
				float xx = first_x - event.getX();
				float yy = first_y - event.getY();
				float moveDist = FloatMath.sqrt(xx * xx + yy * yy);
				
				if(moveDist>=THRESHOLD_DRAW)
				{
					if(_long_click_timer.isCounting())
					{
						_long_click_timer.stopCount();
					}
				}
				
				touch_move(x, y);
				invalidate();
				return true;
				
				default:
					return _is_long_click?false:true;
		}
		
	}

	
	
}
