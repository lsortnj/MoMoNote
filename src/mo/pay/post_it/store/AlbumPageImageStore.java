package mo.pay.post_it.store;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import mo.pay.post_it.util.SQLiteUtil;
import mo.pay.post_it.widget.ComponentLocation;
import mo.pay.post_it.widget.MomoImageView;
import mo.pay.post_it_album.Album;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

public class AlbumPageImageStore extends SQLiteOpenHelper
{
	public static final String TAG = "AlbumPageImageStore";
	
	public final static int 	VERSION 		= 3;
	public final static String 	DB_NAME 		= "momoPostIt.db";
	public final static String 	TABLE_NAME 		= "album_page_image";
	
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
	public final static int FIELD_ROTATE_DEGREE_IDX 	= 9;
	public final static int FIELD_IMAGE_FORMAT_IDX 		= 10;
	public final static int FIELD_FRAME_TYPE_IDX 		= 11;

	public final static String FIELD_SN 				= "sn";
	public final static String FIELD_ALBUM_NAME 		= "album_name";
	public final static String FIELD_PAGE_NO 			= "page_no";
	public final static String FIELD_ALBUM_ID 			= "album_id";
	public final static String FIELD_FILE_PATH 			= "file_path";
	public final static String FIELD_LEFT	 			= "left";
	public final static String FIELD_TOP	 			= "top";
	public final static String FIELD_WIDTH				= "width";
	public final static String FIELD_HEIGHT 			= "height";
	public final static String FIELD_ROTATE_DEGREE 		= "rotate_degree";
	public final static String FIELD_IMAGE_FORMAT 		= "image_format";
	public final static String FIELD_FRAME_TYPE 		= "frame_type";

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
			FIELD_HEIGHT + " TEXT," + 
			FIELD_ROTATE_DEGREE + " TEXT," + 
			FIELD_IMAGE_FORMAT + " TEXT," + 
			FIELD_FRAME_TYPE +  " TEXT)";

	private static Activity 			_activity;
	private static AlbumPageImageStore	_instance = null;
	
	public AlbumPageImageStore(Context context)
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

	public static AlbumPageImageStore instance()
	{
		if (_instance == null)
		{
			_instance = new AlbumPageImageStore(_activity);

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
		MomoImageView imageItem, 
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
					+ FIELD_FILE_PATH + " = '" + imageItem.getImagePath() + "' ", null);

			if (cursor.getCount() > 0)
			{
				cursor.moveToFirst();

				updateAlbumPageIamge(cursor.getInt(FIELD_SN_IDX), imageItem);

				db.close();
			} 
			else
			{
				try
				{ 
					if(VERSION == 3 && (cursor.getColumnIndex(FIELD_FRAME_TYPE)==-1))
					{
						String upgradeQuery = "ALTER TABLE "+TABLE_NAME+" ADD COLUMN "+FIELD_FRAME_TYPE+" TEXT";
						
						db.execSQL(upgradeQuery);
					}
					
					db.execSQL("insert into " + TABLE_NAME + " values(null,\""+ 
							albumName + "\",\"" + 
							pageNo+ "\",\"" + 
							albumId + "\",\""+ 
							imageItem.getImagePath() + "\",\""+ 
							imageItem.getLayoutInfo().getX() + "\",\""+ 
							imageItem.getLayoutInfo().getY() + "\",\""+ 
							imageItem.getLayoutInfo().getWidth() + "\",\""+ 
							imageItem.getLayoutInfo().getHeight() + "\",\""+ 
							imageItem.getRotateDegree() + "\",\""+ 
							imageItem.getImagePath().substring(imageItem.getImagePath().lastIndexOf(".")) + "\",\""+ 
							imageItem.getFrameType() + "\")");

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

	private void updateAlbumPageIamge(int sn, MomoImageView item)
	{
		SQLiteDatabase db = this.getReadableDatabase();

		String sql = "update " + TABLE_NAME + " set " + 
				FIELD_FILE_PATH+ " = '" + item.getImagePath() + "' ," + 
				FIELD_LEFT+ " = '" + item.getLayoutInfo().getX() + "', " +
				FIELD_TOP+ " = '" + item.getLayoutInfo().getY() + "', " +
				FIELD_WIDTH+ " = '" + item.getLayoutInfo().getWidth() + "', " +
				FIELD_HEIGHT+ " = '" + item.getLayoutInfo().getHeight() + "', " +
				FIELD_ROTATE_DEGREE+ " = '" + item.getRotateDegree() + "' ," +
				FIELD_FRAME_TYPE+ " = '" + item.getFrameType() + "' ," +
				FIELD_IMAGE_FORMAT+ " = '" + item.getImagePath().substring(item.getImagePath().lastIndexOf(".")) + "' " +
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
	
	public void deleteImage(MomoImageView imgView)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		
		String sql = "delete from "+TABLE_NAME +" where "+
				FIELD_FILE_PATH+" ='"+imgView.getImagePath() + "' ";
		
		try
		{
			db.execSQL(sql);
			db.close();
			deleteImageFile(imgView.getImagePath());
		}
		catch(SQLException e)
		{
			Log.i(TAG, e.toString());
			
			db.close();
		}
	}
	
	public void deleteAllImages(String albumId)
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
//			new File(path).delete();
		}
	}
	
	public void deleteIamgesOfPage(String albumId, int pageIdx)
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
	
	private MomoImageView getMomoImageView(Cursor cursor, boolean decodeFile)
	{
		MomoImageView img = new MomoImageView(_activity);
		
		ComponentLocation info = new ComponentLocation();
		info.setX(Integer.parseInt(cursor.getString(FIELD_LEFT_IDX)));
		info.setY(Integer.parseInt(cursor.getString(FIELD_TOP_IDX)));
		info.setWidth(Integer.parseInt(cursor.getString(FIELD_WIDTH_IDX)));
		info.setHeight(Integer.parseInt(cursor.getString(FIELD_HEIGHT_IDX)));
		
		String img_path = cursor.getString(FIELD_FILE_PATH_IDX);
		
		if(new File(img_path).exists())
		{
			if(decodeFile)
			{
				BitmapFactory.Options bitmap_opt = new BitmapFactory.Options();
		    	bitmap_opt.inJustDecodeBounds = true;
		    	BitmapFactory.decodeFile(img_path, bitmap_opt);
		    	
				boolean is_need_to_insamplesize = false;
				
				if(bitmap_opt.outWidth > 500)
				{
					is_need_to_insamplesize = true; 
				}
				if(bitmap_opt.outHeight > 500)
				{
					is_need_to_insamplesize = true; 
				}
				
				bitmap_opt.inJustDecodeBounds = false;
				
				if(is_need_to_insamplesize)
				{
					bitmap_opt.inSampleSize = 2;
				}
				
				Bitmap bitmap = BitmapFactory.decodeFile(img_path, bitmap_opt);
				img.setImageBitmap(bitmap);
				
			} 
		}
		
		if(cursor.getString(FIELD_FRAME_TYPE_IDX) == null)
		{
			img.setFrameType(MomoImageView.FRAME_POLAROID);
		}
		else
		{
			img.setFrameType(Integer.parseInt(cursor.getString(FIELD_FRAME_TYPE_IDX)));
		}
		
		img.setRotateDegree(Integer.parseInt(cursor.getString(FIELD_ROTATE_DEGREE_IDX)));
		img.setImagePath(img_path);
		img.setLayoutInfo(info);
		
		return img;
	}
	
	public List<MomoImageView> getImages(String albumName, boolean decodeFile)
	{
		List<MomoImageView> images = new ArrayList<MomoImageView>();
		
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = null;

		cursor = db.rawQuery("select * from " + TABLE_NAME + " where "+ 
				FIELD_ALBUM_NAME + " = '" + albumName + "' ", null);

		if (cursor.getCount() > 0)
		{
			cursor.moveToFirst();

			while(!cursor.isAfterLast())
			{
				MomoImageView img = getMomoImageView(cursor, decodeFile);
					
				if(img != null)
					images.add(img);
				
				cursor.moveToNext();
			}
			
			db.close();
		} 
		
		return images;
	}
	
	public List<MomoImageView> getImages(String albumName, String pageNo, String albumId, boolean decodeFile)
	{
		List<MomoImageView> images = new ArrayList<MomoImageView>();
		
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
				MomoImageView img = getMomoImageView(cursor, decodeFile);
				
				if(img != null)
					images.add(img);
				
				cursor.moveToNext();
			}
			
			db.close();
		} 
		
		return images;
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
		String upgradeQuery = "";

		if (oldVersion == 2 && newVersion == 3)
		{
			upgradeQuery = "ALTER TABLE "+TABLE_NAME+" ADD COLUMN "+FIELD_FRAME_TYPE+" TEXT";
			
			db.execSQL(upgradeQuery);
		}
				
		
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
