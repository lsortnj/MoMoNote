package mo.pay.post_it.logic;

import android.os.Handler;

public class DoubleClickEventTimeCounter 
{
	public static final int	ON_TIMER_START	 		= 0x001;
	public static final int	ON_DOUBLE_CLICK_TIMEOUT = 0x002;
	public static final int	ON_TIMER_STOP		 	= 0x003;
	
	private Handler		_hander 	= null;
	private Thread		_thread		= null;
	private boolean		_is_count	= false;
	
	private long 		_start_timestamp = 0;
	
	private int 		_timeout_value 	= 0;
	
	public DoubleClickEventTimeCounter(Handler handler)
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
	
	public void setTimeOutValue(int value)
	{
		_timeout_value = value;
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
					
					if( time_past > _timeout_value)
					{
						if(_hander != null)
						{
							_hander.sendEmptyMessage(ON_DOUBLE_CLICK_TIMEOUT);
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
