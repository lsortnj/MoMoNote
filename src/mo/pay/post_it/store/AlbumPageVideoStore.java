package mo.pay.post_it.store;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import mo.pay.post_it.util.SQLiteUtil;
import mo.pay.post_it.widget.ComponentLocation;
import mo.pay.post_it.widget.MomoVideoView;
import mo.pay.post_it_album.Album;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.widget.FrameLayout;

public class AlbumPageVideoStore extends SQLiteOpenHelper
{
	public static final String TAG = "AlbumPageVideoStore";
	
	public final static int 	VERSION 		= 3;
	public final static String 	DB_NAME 		= "momoPostIt.db";
	public final static String 	TABLE_NAME 		= "album_page_video";
	
	public static String 	DB_PATH = "";

	public final static int FIELD_SN_IDX 				= 0;
	public final static int FIELD_ALBUM_NAME_IDX 		= 1;
	public final static int FIELD_PAGE_NO_IDX 			= 2;
	public final static int FIELD_ALBUM_ID_IDX 			= 3;
	public final static int FIELD_FILE_PATH_IDX 		= 4;
	public final static int FIELD_LEFT_IDX 				= 5;
	public final static int FIELD_TOP_IDX 				= 6;
	public final static int FIELD_WIDTH_IDX 			= 7;
	public final static int FIELD_HEIGHT_IDX 			= 8;

	public final static String FIELD_SN 				= "sn";
	public final static String FIELD_ALBUM_NAME 		= "album_name";
	public final static String FIELD_PAGE_NO 			= "page_no";
	public final static String FIELD_ALBUM_ID 			= "album_id";
	public final static String FIELD_FILE_PATH 			= "file_path";
	public final static String FIELD_LEFT	 			= "left";
	public final static String FIELD_TOP	 			= "top";
	public final static String FIELD_WIDTH				= "width";
	public final static String FIELD_HEIGHT 			= "height";

	private final static String CREATE_TABLE_STATMENT = "CREATE TABLE "
			+ TABLE_NAME + "(" + FIELD_SN
			+ " INTEGER PRIMARY KEY AUTOINCREMENT ," + 
			FIELD_ALBUM_NAME+ " TEXT," + 
			FIELD_PAGE_NO + " TEXT," + 
			FIELD_ALBUM_ID + " TEXT," + 
			FIELD_FILE_PATH + " TEXT," + 
			FIELD_LEFT + " TEXT," + 
			FIELD_TOP + " TEXT," + 
			FIELD_WIDTH + " TEXT," + 
			FIELD_HEIGHT +  " TEXT)";

	private static Activity 			_activity;
	private static AlbumPageVideoStore	_instance = null;
	
	public AlbumPageVideoStore(Context context)
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

	public static AlbumPageVideoStore instance()
	{
		if (_instance == null)
		{
			_instance = new AlbumPageVideoStore(_activity);

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
		MomoVideoView videoItem, 
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
					+ FIELD_FILE_PATH + " = '" + videoItem.getVideoPath() + "' ", null);

			if (cursor.getCount() > 0)
			{
				cursor.moveToFirst();

				updateAlbumPageVideo(cursor.getInt(FIELD_SN_IDX), videoItem);

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
							videoItem.getVideoPath() + "\",\""+ 
							videoItem.getLayoutInfo().getX() + "\",\""+ 
							videoItem.getLayoutInfo().getY() + "\",\""+ 
							videoItem.getLayoutInfo().getWidth() + "\",\""+ 
							videoItem.getLayoutInfo().getHeight() + "\")");

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

	private void updateAlbumPageVideo(int sn, MomoVideoView item)
	{
		SQLiteDatabase db = this.getReadableDatabase();

		String sql = "update " + TABLE_NAME + " set " + 
				FIELD_FILE_PATH+ " = '" + item.getVideoPath() + "' ," + 
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
	
	public void deleteVideo(MomoVideoView videoView)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		
		String sql = "delete from "+TABLE_NAME +" where "+
				FIELD_FILE_PATH+" ='"+videoView.getVideoPath() + "' ";
		
		try
		{
			db.execSQL(sql);
			db.close();
			deleteVideoFile(videoView.getVideoPath());
		}
		catch(SQLException e)
		{
			Log.i(TAG, e.toString());
			
			db.close();
		}
	}
	
	public void deleteAllVideo(String albumId)
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
					deleteVideoFile(cursor.getString(FIELD_FILE_PATH_IDX));
					
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
			
			Log.i(TAG, "NotePatse: "+albumId);
		}
		catch(SQLException e)
		{
			Log.i(TAG, e.toString());
			
			db.close();
		}
	}
	
	public void deleteVideoFile(String path)
	{
		if(new File(path).exists())
		{
			new File(path).delete();
		}
	}
	
	public void deleteVideoOfPage(String albumId, int pageIdx)
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
	
	private MomoVideoView getMomoVideoView(Cursor cursor)
	{
		ComponentLocation info = new ComponentLocation();
		info.setX(Integer.parseInt(cursor.getString(FIELD_LEFT_IDX)));
		info.setY(Integer.parseInt(cursor.getString(FIELD_TOP_IDX)));
		info.setWidth(Integer.parseInt(cursor.getString(FIELD_WIDTH_IDX)));
		info.setHeight(Integer.parseInt(cursor.getString(FIELD_HEIGHT_IDX)));
		
		String video_path = cursor.getString(FIELD_FILE_PATH_IDX);
		
		if(new File(video_path).exists())
		{
			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(info.getWidth(),info.getHeight());
			params.gravity = Gravity.LEFT | Gravity.TOP;
			params.leftMargin = info.getX();
			params.topMargin  = info.getY();
			
			MomoVideoView video = new MomoVideoView(_activity,video_path);
			video.setVideoPath(video_path);
			video.setLayoutInfo(info);
			video.setLayoutParams(params);
			
			return video;
		}
		
		return null;
	}
	
	public List<MomoVideoView> getVideos(String albumName)
	{
		List<MomoVideoView> videos = new ArrayList<MomoVideoView>();
		
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = null;

		cursor = db.rawQuery("select * from " + TABLE_NAME + " where "+ 
				FIELD_ALBUM_NAME + " = '" + albumName + "' ", null);

		if (cursor.getCount() > 0)
		{
			cursor.moveToFirst();

			while(!cursor.isAfterLast())
			{
				MomoVideoView video = getMomoVideoView(cursor);
				
				if(video != null)
				{
					videos.add(video);
				}
				
				cursor.moveToNext();
			}
			
			db.close();
		} 
		
		return videos;
	}
	
	public List<MomoVideoView> getVideos(String albumName, String pageNo, String albumId)
	{
		List<MomoVideoView> videos = new ArrayList<MomoVideoView>();
		
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = null;

		cursor = db.rawQuery("select * from " + TABLE_NAME + " where "+ 
				FIELD_ALBUM_NAME + " = '" + albumName + "' and "+
				FIELD_PAGE_NO + " = '" + pageNo + "' and "+
				FIELD_ALBUM_ID + " = '" + albumId + "' ", null);

		if (cursor.getCount() > 0)
		{
			cursor.moveToFirst();

			while(!cursor.isAfterLast())
			{
				MomoVideoView video = getMomoVideoView(cursor);
				
				if(video != null)
				{
					videos.add(video);
				}
				
				cursor.moveToNext();
			}
			
			db.close();
		} 
		
		return videos;
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
