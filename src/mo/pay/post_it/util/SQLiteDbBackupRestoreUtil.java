package mo.pay.post_it.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import android.util.Log;

public class SQLiteDbBackupRestoreUtil
{
	public static void backupSQLiteDB( String srcPath, String desPath ) throws IOException,FileNotFoundException
	{
		File src = new File(srcPath);
		File des = new File(desPath);
		
		if( !src.exists() )
			FileUtil.createFileOnSDCard(srcPath);
		
		if( !des.exists() )
			FileUtil.createFileOnSDCard(desPath);
		
		copyFile(src, des);
	}
	
	public static boolean restoreSQLiteDB( String orgDBPath, String importDBPath )
	{
		if( ! SDCardUtil.isSDCardExistAndUseful() ) return false;
		 
		File orgFile 	= new File(orgDBPath);
		File importFile = new File(importDBPath);
 
		if (!importFile.exists()) 
		{
			Log.d("SQLite Util", "restoreSQLiteDB File does not exist");
			return false;
		}
 
		try 
		{
			copyFile(importFile, orgFile);
			
			Log.i("SQLite Util", "restoreSQLiteDB ok  from "+importDBPath+" to org "+orgDBPath);
			
			return true;
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			return false;
		}
	}


	private static void copyFile(File src, File dst) throws IOException,FileNotFoundException 
	{
		FileChannel inChannel 	= new FileInputStream(src).getChannel();
		FileChannel outChannel 	= new FileOutputStream(dst).getChannel();
		
		try 
		{
			inChannel.transferTo(0, inChannel.size(), outChannel);
		} 
		finally 
		{
			if (inChannel != null)
				inChannel.close();
			if (outChannel != null)
				outChannel.close();
		}
	}
}
