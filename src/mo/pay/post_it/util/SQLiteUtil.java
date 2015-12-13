package mo.pay.post_it.util;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SQLiteUtil
{
	public static final String TAG = "SQLiteUtil";
	
	public static boolean isTableExist(SQLiteDatabase db, String tableName)
	{
		try
		{
			if(db == null)
			{
				return true;
			}
			
			Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='"+tableName+"'", null);
			
			c.moveToFirst();
			
			if(c.getCount() >0)
			{ 
				return true;
			}
			else
			{
				return false;
			}
		}catch(Exception e){Log.e(TAG, e.toString());}
		
		return true;
	}
}
