package mo.pay.post_it.store;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import mo.pay.post_it.util.SQLiteUtil;
import mo.pay.post_it_album.Album;
import mo.pay.post_it_handDraw.HandDraw;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

public class AlbumPageHandDrawStore  extends SQLiteOpenHelper
{
	public static final String TAG = "AlbumPageHandDrawStore";
	
	public final static int VERSION = 3;
	public final static String DB_NAME = 	"momoPostIt.db";
	public final static String TABLE_NAME = "album_page_hand_draw";

	public static String 	DB_PATH = "";

	public final static int FIELD_SN_IDX 				= 0;
	public final static int FIELD_ALBUM_NAME_IDX 		= 1;
	public final static int FIELD_PAGE_NO_IDX 			= 2;
	public final static int FIELD_ALBUM_ID_IDX 			= 3;
	public final static int FIELD_FILE_PATH_IDX 		= 4;

	public final static String FIELD_SN 				= "sn";
	public final static String FIELD_ALBUM_NAME 		= "album_name";
	public final static String FIELD_PAGE_NO 			= "page_no";
	public final static String FIELD_ALBUM_ID 			= "album_id";
	public final static String FIELD_FILE_PATH 			= "file_path";

	private final static String CREATE_TABLE_STATMENT = "CREATE TABLE "
			+ TABLE_NAME + "(" + FIELD_SN
			+ " INTEGER PRIMARY KEY AUTOINCREMENT ," + 
			FIELD_ALBUM_NAME+ " TEXT," + 
			FIELD_PAGE_NO + " TEXT," + 
			FIELD_ALBUM_ID + " TEXT," + 
			FIELD_FILE_PATH + " TEXT)";

	private static Activity 				_activity;
	private static AlbumPageHandDrawStore	_instance = null;
	
	public AlbumPageHandDrawStore(Context context)
	{
		super(context, DB_NAME, null, VERSION);
		
		try
		{
			if(getWritableDatabase() != null)
			{
				if(!SQLiteUtil.isTableExist(getWritableDatabase(), TABLE_NAME))
				{
					getWritableDatabase().execSQL(CREATE_TABLE_STATMENT);
				}	
			}
		}catch(Exception e){Log.e(TAG, e.toString());}	
	}

	public static AlbumPageHandDrawStore instance()
	{
		if (_instance == null)
		{
			_instance = new AlbumPageHandDrawStore(_activity);

			return _instance;
		} else
		{
			return _instance;
		}
	}

	public static void construct(Activity activity)
	{
		_activity = activity;
		
		DB_PATH = Environment.getDataDirectory() + "/data/" + _activity.getPackageName() + "/databases/" +DB_NAME;
	}
	
	public void insertHandDraw(HandDraw handDraw)
	{
		SQLiteDatabase db = this.getReadableDatabase();

		try
		{
			Cursor cursor = null;

			cursor = db.rawQuery("select * from " + TABLE_NAME + " where "
					+ FIELD_ALBUM_NAME + " = '" + handDraw.getAlbumName() + "' and "
					+ FIELD_PAGE_NO + " = '" + handDraw.getPageNo() + "' and "
					+ FIELD_ALBUM_ID + " = '" + handDraw.getAlbumId() + "' and "
					+ FIELD_FILE_PATH + " = '" + handDraw.getStorePath() + "' ", null);

			if (cursor.getCount() > 0)
			{
				cursor.moveToFirst();

				updateHandDraw(cursor.getInt(FIELD_SN_IDX), handDraw);

				db.close();
			} 
			else
			{
				try
				{
					db.execSQL("insert into " + TABLE_NAME + " values(null,\""+ 
							handDraw.getAlbumName() + "\",\"" + 
							handDraw.getPageNo()+ "\",\"" + 
							handDraw.getAlbumId() + "\",\"" +
							handDraw.getStorePath() + "\")");

					db.close();
				} catch (SQLException e)
				{
					Log.e(TAG, e.toString());
				}
			}
		}
		catch(Exception e)
		{
			Log.e(TAG, e.toString());
		}
	}

	private void updateHandDraw(int sn, HandDraw handDraw)
	{
		SQLiteDatabase db = this.getReadableDatabase();

		String sql = "update " + TABLE_NAME + " set " + 
				FIELD_FILE_PATH+ " = '" + handDraw.getStorePath() + "' ," + 
				FIELD_ALBUM_NAME + " = '" + handDraw.getAlbumName() + "', " +
				FIELD_PAGE_NO + " = '" + handDraw.getPageNo() + "', " +
				FIELD_ALBUM_ID + " = '" + handDraw.getAlbumId() + "' " +
				" where " + FIELD_SN+ " = " + sn + "";

		try
		{
			db.execSQL(sql);

			db.close();
			
		} catch (SQLException e)
		{
			Log.e(TAG, e.toString());
		}
	}
	
	public void deleteAllHandDraw(String albumId)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		
		Cursor cursor = null;
		
		String select_sql = "select * from "+TABLE_NAME +" where "+
				FIELD_ALBUM_ID+" ='"+albumId + "' ";
		
		try
		{
			cursor = db.rawQuery(select_sql,null);
			
			if (cursor.getCount() > 0)
			{
				cursor.moveToFirst();
				
				while(!cursor.isAfterLast())
				{
					deleteImageFile(cursor.getString(FIELD_FILE_PATH_IDX));
					
					cursor.moveToNext();
				}
			}
			
			Log.i(TAG, "NotePatse: "+albumId);
		}
		catch(SQLException e)
		{
			Log.i(TAG, e.toString());
			
			db.close();
		}
		
		String sql = "delete from "+TABLE_NAME +" where "+
					FIELD_ALBUM_ID+" ='"+albumId + "' ";
		
		try
		{
			db.execSQL(sql);
			
			db.close();
		}
		catch(SQLException e)
		{
			Log.i(TAG, e.toString());
			
			db.close();
		}
	}
	
	public void deleteImageFile(String path)
	{
		if(new File(path).exists())
		{
			new File(path).delete();
		}
	}
	
	public void deleteHandDrawOfPage(String albumId, int pageIdx)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		
		String sql = "delete from "+TABLE_NAME +" where "+
				FIELD_ALBUM_ID+" ='"+albumId + "' and "+
				FIELD_PAGE_NO+" ='"+pageIdx+"'";
		
		try
		{
			db.execSQL(sql);
			db.close();
		}
		catch(SQLException e)
		{
			Log.i(TAG, e.toString());
			
			db.close();
		}
	}
	
	public void albumNameChange(Album newAlbum, String orgAlbumName)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		
		Cursor cursor = null;
		
		String select_sql = "select * from "+TABLE_NAME +" where "+
				FIELD_ALBUM_ID+" ='"+newAlbum.getAlbumId() + "' ";
		
		String update_sql = "";
		
		try
		{
			cursor = db.rawQuery(select_sql,null);
			
			String orgPath = "";
			String newPath = "";
			
			if (cursor.getCount() > 0)
			{
				cursor.moveToFirst();
				
				while(!cursor.isAfterLast()) 
				{
					orgPath = cursor.getString(FIELD_FILE_PATH_IDX);
					newPath = orgPath.replace(orgAlbumName, newAlbum.getAlbumName());
					
					update_sql = "update " + TABLE_NAME + " set " + 
							FIELD_FILE_PATH+ " = '" + newPath + "' , " +
							FIELD_ALBUM_NAME+ " = '"+newAlbum.getAlbumName() +"'"+
							" where " + FIELD_SN+ " = " + cursor.getInt(FIELD_SN_IDX) + "";
					
					try{db.execSQL(update_sql);}
					catch (SQLException e){Log.e(TAG, e.toString());}
					
					cursor.moveToNext();
				}
			}
		}
		catch(SQLException e)
		{
			Log.i(TAG, e.toString());
			
			db.close();
		}
	}
	
	public List<HandDraw> getHandDrawsByAlbumName(String name)
	{
		List<HandDraw> handDraws = new ArrayList<HandDraw>();
		
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = null;

		cursor = db.rawQuery("select * from " + TABLE_NAME + " where "+ 
				FIELD_ALBUM_NAME + " = '" + name +  "' ", null);

		if (cursor.getCount() > 0)
		{
			cursor.moveToFirst();

			while(!cursor.isAfterLast())
			{
				HandDraw handDraw = new HandDraw();
				handDraw.setAlbumId(cursor.getString(FIELD_ALBUM_ID_IDX));
				handDraw.setAlbumName(cursor.getString(FIELD_ALBUM_NAME_IDX));
				handDraw.setPageNo(cursor.getString(FIELD_PAGE_NO_IDX));
				handDraw.setStorePath(cursor.getString(FIELD_FILE_PATH_IDX));
				
				handDraws.add(handDraw);
				
				cursor.moveToNext();
			}
			
			db.close();
		} 
		
		return handDraws;
	}
	
	public List<HandDraw> getHandDrawsByAlbumId(String albumId)
	{
		List<HandDraw> handDraws = new ArrayList<HandDraw>();
		
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = null;

		cursor = db.rawQuery("select * from " + TABLE_NAME + " where "+ 
				FIELD_ALBUM_ID + " = '" + albumId +  "' ", null);

		if (cursor.getCount() > 0)
		{
			cursor.moveToFirst();

			while(!cursor.isAfterLast())
			{
				HandDraw handDraw = new HandDraw();
				handDraw.setAlbumId(cursor.getString(FIELD_ALBUM_ID_IDX));
				handDraw.setAlbumName(cursor.getString(FIELD_ALBUM_NAME_IDX));
				handDraw.setPageNo(cursor.getString(FIELD_PAGE_NO_IDX));
				handDraw.setStorePath(cursor.getString(FIELD_FILE_PATH_IDX));
				
				handDraws.add(handDraw);
				
				cursor.moveToNext();
			}
			
			db.close();
		} 
		
		return handDraws;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		Log.i(TAG, "onCreate");
		
		db.execSQL(CREATE_TABLE_STATMENT);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
//		String sql = "DROP TABLE IF EXISTS " + TABLE_NAME;
//
//		db.execSQL(sql);
//
//		onCreate(db);
		
		Log.i(TAG,"onUpgrade");
	}
	
	public void update(String albumId, int orgIndex, int newIndex, String newFilePath)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		
		String sql = "update " + TABLE_NAME + " set " + FIELD_PAGE_NO
				+ " = '" + newIndex + "' ,"+FIELD_FILE_PATH+"='"+newFilePath+"'" + " where " + FIELD_ALBUM_ID
				+ " = '" + albumId + "' and "+FIELD_PAGE_NO+" ='"+orgIndex+"'";

		try
		{
			db.execSQL(sql);

			db.close();
			
		} catch (SQLException e)
		{
			Log.e("updateSQLite", e.toString());
		}
	}
}
