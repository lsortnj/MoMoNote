package mo.pay.post_it.logic;

import android.os.Handler;

public class LongClickEventTimeCounter
{
	public static final int	ON_TIMER_START	 		= 0x001;
	public static final int	ON_LONG_CLICK			= 0x002;
	public static final int	ON_TIMER_STOP		 	= 0x003;
	
	public static final int LONG_CLICK_TIME_VALUE   = 600;
	
	private Handler		_hander 	= null;
	private Thread		_thread		= null;
	private boolean		_is_count	= false;
	
	private long 		_start_timestamp = 0;
	
	public LongClickEventTimeCounter(Handler handler)
	{
		_hander = handler;
	}
	
	public void stopCount()
	{
		_is_count = false;
		
		if(_thread != null)
		{
			if(_thread.isAlive())
			{
//				_thread.stop();
				_thread.interrupt();
				_thread = null;
			}
		}
	}
	
	public boolean isCounting()
	{
		return _is_count;
	}
	
	public void startCount()
	{
		_is_count	= true;
		
		_start_timestamp = System.currentTimeMillis();
		
		Runnable runnable = new Runnable()
		{
			public void run()
			{
				if(_hander != null)
				{
					_hander.sendEmptyMessage(ON_TIMER_START);
				}
				
				while( _is_count )
				{
					long time_past = System.currentTimeMillis() - _start_timestamp;
					
					if( time_past >= LONG_CLICK_TIME_VALUE)
					{
						if(_hander != null)
						{
							_hander.sendEmptyMessage(ON_LONG_CLICK);
							_hander.sendEmptyMessage(ON_TIMER_STOP);
							
							stopCount();
						}
					}
				}
			}
		};
		
		_thread = new Thread(runnable);
		
		_thread.start();
	}
}
