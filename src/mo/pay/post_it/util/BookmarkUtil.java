package mo.pay.post_it.util;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.database.Cursor;
import android.provider.Browser;

import mo.pay.post_it.clipWeb.Bookmark;


public class BookmarkUtil
{
	private static List<Bookmark>	_bookmarks 		= new ArrayList<Bookmark>();
	private static Activity _activity = null;
	
	public static void init(Activity activity)
	{
		_activity = activity;
		
		new Thread(new Runnable(){
			@SuppressWarnings("deprecation")
			@Override
			public void run()
			{
				_bookmarks.clear();
				
				String[] proj = new String[] { Browser.BookmarkColumns.TITLE, Browser.BookmarkColumns.URL };
			    String sel = Browser.BookmarkColumns.BOOKMARK + " = 1"; // 0 = history, 1 = bookmark
			    Cursor mCur = _activity.managedQuery(Browser.BOOKMARKS_URI, proj, sel, null, null);
			    _activity.startManagingCursor(mCur);
			    mCur.moveToFirst();

			    String title = "";
			    String url   = "";
			    
			    if (mCur.moveToFirst() && mCur.getCount() > 0) {
			        while (mCur.isAfterLast() == false) {

			        	title = mCur.getString(mCur.getColumnIndex(Browser.BookmarkColumns.TITLE));
			        	url   = mCur.getString(mCur.getColumnIndex(Browser.BookmarkColumns.URL));
			        	
			        	if(title!=null && !title.equals("") &&
			        			url!=null && !url.equals(""))
			        	{
			        		Bookmark bookmark = new Bookmark(title,url);
				        	_bookmarks.add(bookmark);
			        	}
			        	
			            mCur.moveToNext();
			        }
			    }
			}
		}).start();
	}
	
	public static List<Bookmark> getBookmarks()
	{
		return _bookmarks;
	}
}
