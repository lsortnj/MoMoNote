package mo.pay.post_it.store;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import mo.pay.post_it.util.FileUtil;
import mo.pay.post_it.util.SQLiteUtil;
import mo.pay.post_it_album.Album;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

public class AlbumStore  extends SQLiteOpenHelper
{
public static final String TAG = "AlbumStore";
	
	public final static int VERSION = 3;
	public final static String DB_NAME = 	"momoPostIt.db";
	public final static String TABLE_NAME = "album";
	
	public static String 	DB_PATH = "";

	public final static int FIELD_SN_IDX 				= 0;
	public final static int FIELD_ALBUM_ID_IDX 			= 1;
	public final static int FIELD_ALBUM_NAME_IDX 		= 2;
	public final static int FIELD_CREATE_DATE_IDX 		= 3;
	public final static int FIELD_ALBUM_ROOT_PATH_IDX 	= 4;
	public final static int FIELD_PASSWORD_IDX 			= 5;
	public final static int FIELD_IS_LOCKED_IDX 		= 6;

	public final static String FIELD_SN 				= "sn";
	public final static String FIELD_ALBUM_ID 			= "album_id";
	public final static String FIELD_ALBUM_NAME 		= "album_name";
	public final static String FIELD_CREATE_DATE 		= "create_date";
	public final static String FIELD_ALBUM_ROOT_PATH 	= "album_root_path";
	public final static String FIELD_PASSWORD 			= "password";
	public final static String FIELD_IS_LOCKED 			= "is_locked";

	private final static String CREATE_TABLE_STATMENT = "CREATE TABLE "
			+ TABLE_NAME + "(" + FIELD_SN
			+ " INTEGER PRIMARY KEY AUTOINCREMENT ," + FIELD_ALBUM_ID
			+ " TEXT," + FIELD_ALBUM_NAME + " TEXT," + FIELD_CREATE_DATE + " TEXT, "
			+ FIELD_ALBUM_ROOT_PATH + " TEXT, " + FIELD_PASSWORD +  " TEXT, "
			+ FIELD_IS_LOCKED + " TEXT )";

	private static Activity _activity;
	private static AlbumStore _instance = null;

	public AlbumStore(Context context)
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

	public static AlbumStore instance()
	{
		if (_instance == null)
		{
			_instance = new AlbumStore(_activity);

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

	public void updateAlbumName(Album album)
	{
		SQLiteDatabase db = this.getReadableDatabase();

		String sql = "update " + TABLE_NAME + " set " + 
				FIELD_ALBUM_NAME  + " = '" + album.getAlbumName()+ "' ," + 
				FIELD_ALBUM_ROOT_PATH  + " = '" + album.getAlbumRootPath()+ "' " + 
				" where " + FIELD_ALBUM_ID+" = '"+album.getAlbumId()+"'";
		try
		{
			db.execSQL(sql);

			db.close();
		} catch (SQLException e)
		{
			Log.e(TAG, e.toString()); 
		}
	}
	
	public void insertAlbum(Album album)
	{
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = null;

		cursor = db.rawQuery("select * from " + TABLE_NAME + " where "
				+ FIELD_ALBUM_NAME + " = '" + album.getAlbumName() + "' and "
				+ FIELD_CREATE_DATE + " = '" + album.getCreateDate() + "' and "
				+ FIELD_ALBUM_ID + " = '" + album.getAlbumId()+ "'", null);

		
		if (cursor.getCount() > 0)
		{
			cursor.moveToFirst();

			updateAlbum(cursor.getInt(FIELD_SN_IDX), album);

			db.close();
		} else
		{
			try
			{
				db.execSQL("insert into " + TABLE_NAME + " values(null,\""
						+ album.getAlbumId() + "\",\"" 
						+ album.getAlbumName() + "\",\"" 
						+ album.getCreateDate() + "\",\""
						+ album.getAlbumRootPath() + "\",\"" 
						+ album.getPassword()+ "\",\""
						+ album.isLocked()+ "\" )");

				db.close();
			} catch (SQLException e)
			{
				Log.e(TAG, e.toString());
			}
		}
	}

	private void updateAlbum(int sn, Album album)
	{
		SQLiteDatabase db = this.getReadableDatabase();

		String sql = "update " + TABLE_NAME + " set " + 
				FIELD_ALBUM_ROOT_PATH+ " = '" + album.getAlbumRootPath() + "' ," + 
				FIELD_ALBUM_ID+ " = '" + album.getAlbumId() + "' ," + 
				FIELD_PASSWORD+ " = '" + album.getPassword() + "' ," + 
				FIELD_IS_LOCKED+ " = '" + album.isLocked() + "' ," + 
				FIELD_ALBUM_NAME  + " = '" + album.getAlbumName()+ "' ," + 
				FIELD_CREATE_DATE  + " = '" + album.getCreateDate() + "' " + 
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

	public void deleteAlbum(Album album)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		
		if(album != null)
		{
			String sql = "delete from " + TABLE_NAME + " where " + 
			FIELD_ALBUM_ID + " = '"+ album.getAlbumId() + "' " ;

			try
			{
				db.execSQL(sql);
		
				db.close();
		
				FileUtil.deleteForlder(new File(album.getAlbumRootPath()));
				
			} catch (SQLException e)
			{
				Log.e(TAG, e.toString());
			}
		}
	}
	
	public List<Album> getAllAlbum()
	{
		List<Album> albums = new ArrayList<Album>();

		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = null;

		cursor = db.rawQuery("select * from " + TABLE_NAME + " order by "+FIELD_CREATE_DATE+" desc" ,null);

		if (cursor.getCount() > 0)
		{
			cursor.moveToFirst();

			while (!cursor.isAfterLast())
			{
				Album album = new Album();

				album.setAlbumName(cursor.getString(FIELD_ALBUM_NAME_IDX));
				album.setCreateDate(cursor.getString(FIELD_CREATE_DATE_IDX));
				album.setAlbumId(cursor.getString(FIELD_ALBUM_ID_IDX));
				album.setAlbumRootPath(cursor.getString(FIELD_ALBUM_ROOT_PATH_IDX));
				album.setPassword(cursor.getString(FIELD_PASSWORD_IDX));
				album.isLocked(Boolean.parseBoolean(cursor.getString(FIELD_IS_LOCKED_IDX)));
				
				albums.add(album);
				
				cursor.moveToNext();
			}
		}

		db.close();

		cursor.close();

		return albums;
	}
	
	public Album getAlbum(String albumID)
	{
		Album album = null;

		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = null;

		cursor = db.rawQuery("select * from " + TABLE_NAME + " where "+ 
				FIELD_ALBUM_ID + " = '" + albumID + "' ",
				null);

		if (cursor.getCount() > 0)
		{
			cursor.moveToFirst();

			while (!cursor.isAfterLast())
			{
				album = new Album();

				album.setAlbumName(cursor.getString(FIELD_ALBUM_NAME_IDX));
				album.setCreateDate(cursor.getString(FIELD_CREATE_DATE_IDX));
				album.setAlbumId(cursor.getString(FIELD_ALBUM_ID_IDX));
				album.setAlbumRootPath(cursor.getString(FIELD_ALBUM_ROOT_PATH_IDX));
				album.setPassword(cursor.getString(FIELD_PASSWORD_IDX));
				album.isLocked(Boolean.parseBoolean(cursor.getString(FIELD_IS_LOCKED_IDX)));
				
				cursor.moveToNext();
			}
		}

		db.close();

		cursor.close();

		return album;
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
		
		String upgradeQuery = "";

		if (oldVersion ==2 && newVersion == 3)
		{
			upgradeQuery = "ALTER TABLE "+AlbumPageImageStore.TABLE_NAME+" ADD COLUMN "+AlbumPageImageStore.FIELD_FRAME_TYPE+" TEXT";
			
			db.execSQL(upgradeQuery);
		}
		
		Log.i(TAG,"onUpgrade");
	}
}
