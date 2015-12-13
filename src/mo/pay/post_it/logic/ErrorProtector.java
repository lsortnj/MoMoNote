package mo.pay.post_it.logic;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;
import android.os.Message;

public class ErrorProtector
{
	private static ErrorProtector _instance = null;
	private boolean _is_counting_click_time = false;

	private final int COUNTING_TIMES_UP = 0;
	private final int PREVENT_REPEAT_CLICK_DURATION = 500;

	public Handler _handleer = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case COUNTING_TIMES_UP:

				onClickProtectTimesUp();

				break;

			default:

				break;
			}
		}
	};

	public static ErrorProtector instance()
	{
		if (_instance == null)
		{
			_instance = new ErrorProtector();

			return _instance;
		} else
		{
			return _instance;
		}
	}

	public void clickStart()
	{
		_is_counting_click_time = true;

		try
		{
			Runnable r = new Runnable()
			{
				public void run()
				{
					new Timer().schedule(new TimerTask()
					{
						public void run()
						{
							Message timer_msg = new Message();

							timer_msg.what = COUNTING_TIMES_UP;

							_handleer.handleMessage(timer_msg);
						}
					},

					PREVENT_REPEAT_CLICK_DURATION);
				}
			};

			new Thread(r).start();
		} catch (Exception e)
		{

		}
	}

	public boolean isCountingClickTime()
	{
		return _is_counting_click_time;
	}

	public void onClickProtectTimesUp()
	{
		_is_counting_click_time = false;
	}

}
