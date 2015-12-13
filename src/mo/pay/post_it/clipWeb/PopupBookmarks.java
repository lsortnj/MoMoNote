package mo.pay.post_it.clipWeb;


import mo.pay.post_it.R;
import mo.pay.post_it.popup.ActionItem;
import mo.pay.post_it.popup.QuickAction;
import mo.pay.post_it.util.BookmarkUtil;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;

public class PopupBookmarks
{
	private static 	QuickAction				_quickAction	= null;
	private static  IClipWebContentListener _listener		= null;
	private static	Activity				_activity   	= null;
	
	public static void destroyBookmarkPopup()
	{
		_quickAction = null;
	}
	
	public static QuickAction getBookmarkPopup
		(
			Activity activity,
			IClipWebContentListener listener,
			View view
		)
	{
		_listener = listener;
		_activity = activity;
		
		if(_quickAction!=null)
			return _quickAction;
		
		_quickAction = new QuickAction(view,QuickAction.TYPE_DARK);
		
		for(final Bookmark bookmark : BookmarkUtil.getBookmarks())
		{
			ActionItem bookmarkItem = new ActionItem();
			bookmarkItem.setIcon(_activity.getResources().getDrawable(R.drawable.camera));
			bookmarkItem.setTitle(bookmark.getTitle());
			bookmarkItem.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) 
				{
					_quickAction.dismiss();
					
					_listener.onBookmarkSelected(bookmark.getUrl());
				}
			});
			
			_quickAction.addActionItem(bookmarkItem);
		}
		
		
		return _quickAction;
	}
	
}
