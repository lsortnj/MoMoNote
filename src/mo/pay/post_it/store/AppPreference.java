package mo.pay.post_it.store;

import java.io.File;
import java.io.FileNotFoundException;

import mo.pay.post_it.util.SDCardUtil;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

public class AppPreference
{
	public static String APP_HOME						= "MoMoPostIt";
	public static String DEFAULT_UNZIP_TEMP_FOLDER 		= "unzip_temp";
	public static String DEFAULT_TEMP_NOTE_NAME 		= "temp.png";
	public static String ALBUM_HOME						= "album";
	public static String FOLDER_WEB_CONTENT				= "webcontent";

	public static String DEFAULT_APP_ROOT_PATH = Environment.getExternalStorageDirectory()
			.getAbsolutePath()+ File.separator+ APP_HOME + File.separator;
	
	private static Activity _activity;
	private static AppPreference _instance = null;

	private static SharedPreferences _preferences;

	public static void construct(Activity activity)
	{
		_activity = activity;

		if (SDCardUtil.isSDCardExistAndUseful())
		{
			DEFAULT_APP_ROOT_PATH = Environment.getExternalStorageDirectory()
					.getAbsolutePath()
					+ File.separator
					+ APP_HOME
					+ File.separator;
  
			DEFAULT_UNZIP_TEMP_FOLDER = "unzip_temp";
		} 
		else
		{
			DEFAULT_APP_ROOT_PATH = APP_HOME;

			try
			{
				_activity.openFileOutput("temp.txt",
						Activity.MODE_WORLD_READABLE);

				ContextWrapper cw = new ContextWrapper(_activity);
				
				File temp_folder = cw.getDir("unzip_temp", Context.MODE_WORLD_READABLE);
				
				int file_count = temp_folder.listFiles().length;

				for (int i = file_count - 1; i >= 0; i--)
				{
					temp_folder.listFiles()[i].delete();
				}
				
				DEFAULT_UNZIP_TEMP_FOLDER = temp_folder.getAbsolutePath();
			} 
			catch ( FileNotFoundException e)
			{
				e.printStackTrace();
			}
		}
		
	}
	
	public static AppPreference instance()
	{
		if (_instance == null)
		{
			_instance = new AppPreference();

			return _instance;
		} else
		{
			return _instance;
		}
	}

	public String getAlbumHomeFolder()
	{
		String path = "";

		_preferences = _activity.getSharedPreferences("APP_SETTING",
				Context.MODE_PRIVATE);

		path = _preferences.getString("AlbumHomeFolder", getAppRootFolder()
				+ File.separator + ALBUM_HOME + File.separator);

		if (SDCardUtil.isSDCardExistAndUseful())
		{
			path = getAppRootFolder() + ALBUM_HOME+ File.separator;

			if (!new File(path).exists())
			{
				new File(path).mkdir();
			}
		} else
		{
			try
			{
				ContextWrapper cw = new ContextWrapper(_activity);

				File note_folder = cw.getDir(ALBUM_HOME, Context.MODE_WORLD_READABLE);
				
				path = note_folder.getAbsolutePath()+ File.separator;
			} 
			catch (Exception e)
			{
				Log.i("AlbumHomeFolder", e.toString());
			}
		}

		SharedPreferences.Editor editor = _preferences.edit();

		editor.putString("AlbumHomeFolder", path);

		editor.commit();

		return path;
	}
	
	public String getWebContentFolder()
	{
		String path = "";

		_preferences = _activity.getSharedPreferences("APP_SETTING",
				Context.MODE_PRIVATE);

		path = _preferences.getString("WebContentFolder", getAppRootFolder()
				+ File.separator + FOLDER_WEB_CONTENT + File.separator);

		if (SDCardUtil.isSDCardExistAndUseful())
		{
			path = getAppRootFolder() + FOLDER_WEB_CONTENT+ File.separator;

			if (!new File(path).exists())
			{
				new File(path).mkdir();
			}
		} else
		{
			try
			{
				ContextWrapper cw = new ContextWrapper(_activity);

				File note_folder = cw.getDir(FOLDER_WEB_CONTENT, Context.MODE_WORLD_READABLE);
				
				path = note_folder.getAbsolutePath()+ File.separator;
			} 
			catch (Exception e)
			{
				Log.i("WebContentFolder", e.toString());
			}
		}

		SharedPreferences.Editor editor = _preferences.edit();

		editor.putString("WebContentFolder", path);

		editor.commit();

		return path;
	}
	
	public String getAppRootFolder()
	{
		String path = "";

		_preferences = _activity.getSharedPreferences("APP_SETTING",
				Context.MODE_PRIVATE);

		path = _preferences.getString("AppRootFolder", DEFAULT_APP_ROOT_PATH);

		if (SDCardUtil.isSDCardExistAndUseful())
		{
			path = DEFAULT_APP_ROOT_PATH;

			if (!new File(path).exists())
			{
				new File(path).mkdir();
			}
		} else
		{
			try
			{   
				ContextWrapper cw = new ContextWrapper(_activity);

				File file = cw.getDir(DEFAULT_APP_ROOT_PATH,
						Activity.MODE_WORLD_READABLE);

				path = file.getAbsolutePath();
			} catch (Exception e)
			{
				Log.i("getAppRootFolder", e.toString());
			}
		}

		SharedPreferences.Editor editor = _preferences.edit();
 
		editor.putString("AppRootFolder", path);

		editor.commit();

		return path;
	}
	
	public boolean isFirstLaunch()
	{
		_preferences = _activity.getSharedPreferences("APP_SETTING",
				Context.MODE_PRIVATE);

		return _preferences.getBoolean("isFirstLaunch", true);
	}
	
	public void isFirstLaunch(boolean isFirstLaunch)
	{
		SharedPreferences.Editor editor = _preferences.edit();

		editor.putBoolean("isFirstLaunch", isFirstLaunch);

		editor.commit();
	}

	public void isFirstTimeCreateAlbum(boolean isFirstTimeCreateAlbum)
	{
		SharedPreferences.Editor editor = _preferences.edit();

		editor.putBoolean("isFirstTimeCreateAlbum", isFirstTimeCreateAlbum);

		editor.commit();
	}

	public boolean isFirstTimeCreateAlbum()
	{
		_preferences = _activity.getSharedPreferences("APP_SETTING",
				Context.MODE_PRIVATE);
 
		return _preferences.getBoolean("isFirstTimeCreateAlbum", true);
	}
	
	public void isFirstTimeToEditMode(boolean isFirstTimeToEditMode)
	{
		SharedPreferences.Editor editor = _preferences.edit();

		editor.putBoolean("isFirstTimeToEditMode", isFirstTimeToEditMode);

		editor.commit();
	}

	public boolean isFirstTimeToEditMode()
	{
		_preferences = _activity.getSharedPreferences("APP_SETTING",
				Context.MODE_PRIVATE);

		return _preferences.getBoolean("isFirstTimeToEditMode", true);
	}
	
	public void isFirstTimeToViewMode(boolean isFirstTimeToViewMode)
	{
		SharedPreferences.Editor editor = _preferences.edit();

		editor.putBoolean("isFirstTimeToViewMode", isFirstTimeToViewMode);

		editor.commit();
	}

	public boolean isFirstTimeToViewMode()
	{
		_preferences = _activity.getSharedPreferences("APP_SETTING",
				Context.MODE_PRIVATE);

		return _preferences.getBoolean("isFirstTimeToViewMode", true);
	}
	
	public void isFirstTimeAddText(boolean isFirstTimeAddText)
	{
		SharedPreferences.Editor editor = _preferences.edit();

		editor.putBoolean("isFirstTimeAddText", isFirstTimeAddText);

		editor.commit();
	}

	public boolean isFirstTimeAddText()
	{
		_preferences = _activity.getSharedPreferences("APP_SETTING",
				Context.MODE_PRIVATE);

		return _preferences.getBoolean("isFirstTimeAddText", true);
	}
	
	public void isFirstTimeAddObject(boolean isFirstTimeAddObject)
	{
		SharedPreferences.Editor editor = _preferences.edit();

		editor.putBoolean("isFirstTimeAddObject", isFirstTimeAddObject);

		editor.commit();
	}

	public boolean isFirstTimeAddObject()
	{
		_preferences = _activity.getSharedPreferences("APP_SETTING",
				Context.MODE_PRIVATE);

		return _preferences.getBoolean("isFirstTimeAddObject", true);
	}
	
	public void isFirstTimeObjectLostFocus(boolean isFirstTimeObjectLostFocus)
	{
		SharedPreferences.Editor editor = _preferences.edit();

		editor.putBoolean("isFirstTimeObjectLostFocus", isFirstTimeObjectLostFocus);

		editor.commit();
	}

	public boolean isFirstTimeObjectLostFocus()
	{
		_preferences = _activity.getSharedPreferences("APP_SETTING",
				Context.MODE_PRIVATE);

		return _preferences.getBoolean("isFirstTimeObjectLostFocus", true);
	}
	
	public String getLastBrowseURL()
	{
		_preferences = _activity.getSharedPreferences("APP_SETTING",
				Context.MODE_PRIVATE);

		return _preferences.getString("LastBrowseURL", "http://google.com");
	}
	
	public void setLastBrowseURL(String lastBrowseURL)
	{
		SharedPreferences.Editor editor = _preferences.edit();

		editor.putString("LastBrowseURL", lastBrowseURL);

		editor.commit();
	}
}
