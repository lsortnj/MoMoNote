package mo.pay.post_it_handDraw;

import android.app.Activity;
import android.view.View;
import mo.pay.post_it.popup.ActionItem;
import mo.pay.post_it.popup.QuickAction;
import mo.pay.post_it_album.AlbumPageView;

public class PopupDrawSetting
{
	private static QuickAction _drawSettingQuickAction 		= null;
	
	public static QuickAction getDrawSettingPopup
		(
			Activity activity,
			View view,
			AlbumPageView albumPageView
		) 
	{ 
		_drawSettingQuickAction = new QuickAction(view,QuickAction.TYPE_DARK);
		
		ActionItem drawSettingPanel = new ActionItem();
		drawSettingPanel.setCustomView(ViewDrawSetting.getViewDrawSetting(albumPageView));
		
		_drawSettingQuickAction.addActionItem(drawSettingPanel);
		
		return _drawSettingQuickAction;
	}
}
