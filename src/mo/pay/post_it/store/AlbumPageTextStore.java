package mo.pay.post_it.store;

import java.util.ArrayList;
import java.util.List;

import mo.pay.post_it.util.SQLiteUtil;
import mo.pay.post_it.widget.ComponentLocation;
import mo.pay.post_it.widget.MomoTextView;
import mo.pay.post_it_album.Album;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

public class AlbumPageTextStore extends SQLiteOpenHelper
{
	public static final String TAG = "AlbumPageTextStore";
	
	public final static int 	VERSION 		= 3;
	public final static String 	DB_NAME 		= "momoPostIt.db";
	public final static String 	TABLE_NAME 		= "album_page_text";
	
	public static String 	DB_PATH = "";

	public final static int FIELD_SN_IDX 				= 0;
	public final static int FIELD_ALBUM_NAM_IDX 		= 1;
	public final static int FIELD_PAGE_NO_IDX 			= 2;
	public final static int FIELD_ALBUM_ID_IDX 			= 3;
	public final static int FIELD_TEXT_IDX 				= 4;
	public final static int FIELD_LEFT_IDX 				= 5;
	public final static int FIELD_TOP_IDX 				= 6;
	public final static int FIELD_WIDTH_IDX 			= 7;
	public final static int FIELD_HEIGHT_IDX 			= 8;
	public final static int FIELD_TEXT_SIZE_IDX 		= 9;
	public final static int FIELD_TEXT_COLOR_IDX 		= 10;
	public final static int FIELD_TEXT_ID_IDX 			= 11;

	public final static String FIELD_SN 				= "sn";
	public final static String FIELD_ALBUM_NAME 		= "album_name";
	public final static String FIELD_PAGE_NO 			= "page_no";
	public final static String FIELD_ALBUM_ID 			= "album_id";
	public final static String FIELD_TEXT	 			= "text";
	public final static String FIELD_LEFT	 			= "left";
	public final static String FIELD_TOP	 			= "top";
	public final static String FIELD_WIDTH				= "width";
	public final static String FIELD_HEIGHT 			= "height";
	public final static String FIELD_TEXT_SIZE			= "text_size";
	public final static String FIELD_TEXT_COLOR 		= "text_color";
	public final static String FIELD_TEXT_ID 			= "text_id";

	private final static String CREATE_TABLE_STATMENT = "CREATE TABLE "
			+ TABLE_NAME + "(" + FIELD_SN
			+ " INTEGER PRIMARY KEY AUTOINCREMENT ," + 
			FIELD_ALBUM_NAME+ " TEXT," + 
			FIELD_PAGE_NO + " TEXT," + 
			FIELD_ALBUM_ID + " TEXT," + 
			FIELD_TEXT + " TEXT," + 
			FIELD_LEFT + " TEXT," + 
			FIELD_TOP + " TEXT," + 
			FIELD_WIDTH + " TEXT," + 
			FIELD_HEIGHT + " TEXT," + 
			FIELD_TEXT_SIZE + " TEXT," +
			FIELD_TEXT_COLOR + " TEXT," +
			FIELD_TEXT_ID + " TEXT)";

	private static Activity 			_activity;
	private static AlbumPageTextStore	_instance = null;
	
	public AlbumPageTextStore(Context context)
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

	public static AlbumPageTextStore instance()
	{
		if (_instance == null)
		{
			_instance = new AlbumPageTextStore(_activity);

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
	
	public void insert
	(
		MomoTextView textItem, 
		String albumName, 
		String pageNo, 
		String albumId
	)
	{
		SQLiteDatabase db = this.getReadableDatabase();

		try
		{
			Cursor cursor = null;

			cursor = db.rawQuery("select * from " + TABLE_NAME + " where "
					+ FIELD_ALBUM_NAME + " = '" + albumName + "' and "
					+ FIELD_PAGE_NO + " = '" + pageNo + "' and "
					+ FIELD_ALBUM_ID + " = '" + albumId + "' and "
					+ FIELD_TEXT_ID + " = '" + textItem.getMomoTextViewID() + "'  ", null);

			
			if (cursor.getCount() > 0)
			{
				cursor.moveToFirst();

				updateAlbumPageText(cursor.getInt(FIELD_SN_IDX), textItem);

				db.close();
			} 
			else
			{
				try
				{
					db.execSQL("insert into " + TABLE_NAME + " values(null,\""+ 
							albumName + "\",\"" + 
							pageNo+ "\",\"" + 
							albumId + "\",\""+ 
							textItem.getText() + "\",\""+ 
							textItem.getLayoutInfo().getX() + "\",\""+ 
							textItem.getLayoutInfo().getY() + "\",\""+ 
							textItem.getLayoutInfo().getWidth() + "\",\""+ 
							textItem.getLayoutInfo().getHeight() + "\",\""+ 
							textItem.getTextSize() + "\",\""+ 
							textItem.getTextColor() + "\",\""+ 
							textItem.getMomoTextViewID() + "\")");

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
	
	public void albumNameChange(Album newAlbum)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		
		String sql = "update " + TABLE_NAME + " set " + 
				FIELD_ALBUM_NAME+ " = '" + newAlbum.getAlbumName() + "' "+
				" where " + FIELD_ALBUM_ID+ " = '" + newAlbum.getAlbumId() + "'";
		
		try
		{
			db.execSQL(sql);

			db.close();
			
		} catch (SQLException e)
		{
			Log.e(TAG, e.toString());
		}
	}

	private void updateAlbumPageText(int sn, MomoTextView item)
	{
		SQLiteDatabase db = this.getReadableDatabase();

		String sql = "update " + TABLE_NAME + " set " + 
				FIELD_TEXT+ " = '" + item.getText() + "' ," + 
				FIELD_TEXT_SIZE+ " = '" + item.getTextSize() + "' ," + 
				FIELD_TEXT_COLOR+ " = '" + item.getTextColor() + "' ," + 
				FIELD_LEFT+ " = '" + item.getLayoutInfo().getX() + "', " +
				FIELD_TOP+ " = '" + item.getLayoutInfo().getY() + "', " +
				FIELD_WIDTH+ " = '" + item.getLayoutInfo().getWidth() + "', " +
				FIELD_HEIGHT+ " = '" + item.getLayoutInfo().getHeight() + "' " +
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
	
	public void deleteAllText(String albumID)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		
		String sql = "delete from "+TABLE_NAME +" where "+
					FIELD_ALBUM_ID+" ='"+albumID + "' ";
		
		try
		{
			db.execSQL(sql);
			
			db.close();
			
			Log.i(TAG, "NotePatse: "+albumID);
		}
		catch(SQLException e)
		{
			Log.i(TAG, e.toString());
			
			db.close();
		}
	}
	
	public void deleteTextOfPage(String albumId, int pageIdx)
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
	
	private MomoTextView getMomoTextView(Cursor cursor)
	{
		ComponentLocation info = new ComponentLocation();
		info.setX(Integer.parseInt(cursor.getString(FIELD_LEFT_IDX)));
		info.setY(Integer.parseInt(cursor.getString(FIELD_TOP_IDX)));
		info.setWidth(Integer.parseInt(cursor.getString(FIELD_WIDTH_IDX)));
		info.setHeight(Integer.parseInt(cursor.getString(FIELD_HEIGHT_IDX)));
		
		MomoTextView txt = new MomoTextView(_activity);
		txt.setTextSize(Integer.parseInt(cursor.getString(FIELD_TEXT_SIZE_IDX)));
		txt.setTextColor(Integer.parseInt(cursor.getString(FIELD_TEXT_COLOR_IDX)));
		txt.setText(cursor.getString(FIELD_TEXT_IDX));
		txt.setMomoTextViewID(cursor.getString(FIELD_TEXT_ID_IDX));
		txt.setLayoutInfo(info);
		
		return txt;
	}
	
	public List<MomoTextView> getTexts(String albumName)
	{
		List<MomoTextView> texts = new ArrayList<MomoTextView>();
		
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = null;

		cursor = db.rawQuery("select * from " + TABLE_NAME + " where "+ 
				FIELD_ALBUM_NAME + " = '" + albumName + "' ", null);

		if (cursor.getCount() > 0)
		{
			cursor.moveToFirst();

			while(!cursor.isAfterLast())
			{
				MomoTextView txt = getMomoTextView(cursor);
				
				texts.add(txt);
				
				cursor.moveToNext();
			}
			
			db.close();
		} 
		
		return texts;
	}
	
	public List<MomoTextView> getTexts(String albumName, String pageNo, String albumeID)
	{
		List<MomoTextView> texts = new ArrayList<MomoTextView>();
		
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = null;

		cursor = db.rawQuery("select * from " + TABLE_NAME + " where "+ 
				FIELD_ALBUM_NAME + " = '" + albumName + "' and "+
				FIELD_PAGE_NO + " = '" + pageNo + "' and "+
				FIELD_ALBUM_ID + " = '" + albumeID + "' ", null);

		if (cursor.getCount() > 0)
		{
			cursor.moveToFirst();

			while(!cursor.isAfterLast())
			{
				MomoTextView txt = getMomoTextView(cursor);
				
				texts.add(txt);
				
				cursor.moveToNext();
			}
			
			db.close();
		} 
		
		return texts;
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
	
	public void update(String albumId, int orgIndex, int newIndex)
	{
		SQLiteDatabase db = this.getReadableDatabase();

		String sql = "update " + TABLE_NAME + " set " + FIELD_PAGE_NO
				+ " = '" + newIndex + "' " + " where " + FIELD_ALBUM_ID
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
