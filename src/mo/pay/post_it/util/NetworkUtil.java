package mo.pay.post_it.util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil
{
	private static Activity _activity = null;
	
	public static void construct( Activity activity )
	{
		_activity = activity;
	}
	
	public static boolean hasConnectionExist()
	{
		if(_activity == null)
		{
			return false;
		}
		
		boolean result = false; 
		
		ConnectivityManager conn_manager = 
			(ConnectivityManager) _activity.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		if(conn_manager != null)
		{
			NetworkInfo[] info = conn_manager.getAllNetworkInfo(); 
			 
			if (info != null) 
			{ 
				for (int i = 0; i < info.length; i++) 
				{  
					if (info[i].getState() == NetworkInfo.State.CONNECTED) 
					{  
						result =  true; 
						break;
					} 
			   }  
			} 
			else
			{
				result =  false;
			}
		}
		
//		if( conn_manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isAvailable() )
//		{
//			if
//			(
//				conn_manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting()&&
//				conn_manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()
//			)
//			{
//				result = true;
//			}
//		}
//		else if( conn_manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isAvailable() )
//		{
//			if
//			(
//				conn_manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting()&&
//				conn_manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected()
//			)
//			{
//				result = true;
//			}
//		}
		
		
		return result;
	}
}
