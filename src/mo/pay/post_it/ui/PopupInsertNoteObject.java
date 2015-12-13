package mo.pay.post_it.ui;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.View.OnClickListener;
import mo.pay.post_it.R;
import mo.pay.post_it.multiMedia.IAddNoteObjectListener;
import mo.pay.post_it.popup.ActionItem;
import mo.pay.post_it.popup.QuickAction;

public class PopupInsertNoteObject 
{
	private static 	QuickAction				_insertObjectQuickAction	= null;
	private static  IAddNoteObjectListener 	_listener			= null;
	private static	Activity				_activity   		= null;
	private static	DialogTwoButton			_addImgDialog		= null;
	private static	DialogTwoButton			_addVideoDialog		= null;
	private static	DialogTwoButton			_addAudioDialog		= null;
	
	public static QuickAction getInsertObjectPopup
		(
			Activity activity,
			IAddNoteObjectListener listener,
			View view
		)
	{
		_listener = listener;
		_activity = activity;
		
		_insertObjectQuickAction = new QuickAction(view,QuickAction.TYPE_DARK);
		
		ActionItem addImage = new ActionItem();
		addImage.setIcon(_activity.getResources().getDrawable(R.drawable.camera));
		addImage.setTitle(_activity.getString(R.string.text_add_image));
		addImage.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) 
			{
				_insertObjectQuickAction.dismiss();
				
				getAddImageSelectionDialog().show();
			}
		});
		
		ActionItem addVideo = new ActionItem();
		addVideo.setIcon(_activity.getResources().getDrawable(R.drawable.vedio_recorder));
		addVideo.setTitle(_activity.getString(R.string.text_add_video));
		addVideo.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) 
			{
				_insertObjectQuickAction.dismiss();
				
				getAddVideoSelectionDialog().show();
			}
		});
		
		ActionItem addText = new ActionItem();
		addText.setIcon(_activity.getResources().getDrawable(R.drawable.add_text));
		addText.setTitle(_activity.getString(R.string.text_add_text));
		addText.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) 
			{
				_insertObjectQuickAction.dismiss();
				
				_listener.onAddText();
			}
		});
		
		ActionItem addAudio = new ActionItem();
		addAudio.setIcon(_activity.getResources().getDrawable(R.drawable.audio_recorder));
		addAudio.setTitle(_activity.getString(R.string.text_add_audio));
		addAudio.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) 
			{
				_insertObjectQuickAction.dismiss();
				
				getAddAudioSelectionDialog().show();
			} 
		}); 
		
		ActionItem clipWebContent = new ActionItem();
		clipWebContent.setIcon(_activity.getResources().getDrawable(R.drawable.clip_web_content));
		clipWebContent.setTitle(_activity.getResources().getString(R.string.text_clip_webcontent));
		clipWebContent.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) 
			{ 
				_insertObjectQuickAction.dismiss();
				
				_listener.onWebContentClip();
			}
		});
		
		_insertObjectQuickAction.addActionItem(addImage);
		_insertObjectQuickAction.addActionItem(addVideo);
		_insertObjectQuickAction.addActionItem(addText);
		_insertObjectQuickAction.addActionItem(addAudio);
		_insertObjectQuickAction.addActionItem(clipWebContent);
		
		return _insertObjectQuickAction;
	}
	
	private static Dialog getAddAudioSelectionDialog()
	{
		_addAudioDialog = new DialogTwoButton(_activity);
		
		_addAudioDialog.setMessage(_activity.getString(R.string.msg_choose_way_too_add_audio));
		
		_addAudioDialog.setButtonLeftText(_activity.getString(R.string.text_record_audio));
		_addAudioDialog.setButtonLeftIcon(BitmapFactory.decodeResource(_activity.getResources(), R.drawable.audio_recorder));
		_addAudioDialog.setButtonLeftOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				_listener.onRecordAudio();
				_addAudioDialog.dismiss();
			}
		});
		
		_addAudioDialog.setButtonRightText(_activity.getString(R.string.text_import_audio));
		_addAudioDialog.setButtonRightIcon(BitmapFactory.decodeResource(_activity.getResources(), R.drawable.import_audio));
		_addAudioDialog.setButtonRightOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				_listener.onImportAudio();
				_addAudioDialog.dismiss();
			}
		});
		
		return _addAudioDialog;
	}
	
	private static Dialog getAddVideoSelectionDialog()
	{
		_addVideoDialog = new DialogTwoButton(_activity);
		
		_addVideoDialog.setMessage(_activity.getString(R.string.msg_choose_way_too_add_video));
		
		_addVideoDialog.setButtonLeftText(_activity.getString(R.string.text_record_video));
		_addVideoDialog.setButtonLeftIcon(BitmapFactory.decodeResource(_activity.getResources(), R.drawable.vedio_recorder));
		_addVideoDialog.setButtonLeftOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				_listener.onRecordvideo();
				_addVideoDialog.dismiss();
			}
		});
		
		_addVideoDialog.setButtonRightText(_activity.getString(R.string.text_import_video));
		_addVideoDialog.setButtonRightIcon(BitmapFactory.decodeResource(_activity.getResources(), R.drawable.import_video));
		_addVideoDialog.setButtonRightOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				_listener.onImportVideo();
				_addVideoDialog.dismiss();
			}
		});
		
		return _addVideoDialog;
	}
	
	private static Dialog getAddImageSelectionDialog()
	{
		_addImgDialog = new DialogTwoButton(_activity);
		
		_addImgDialog.setMessage(_activity.getString(R.string.msg_choose_way_too_add_image));
		
		_addImgDialog.setButtonLeftText(_activity.getString(R.string.text_take_photo));
		_addImgDialog.setButtonLeftIcon(BitmapFactory.decodeResource(_activity.getResources(), R.drawable.camera));
		_addImgDialog.setButtonLeftOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				_listener.onTakePhoto();
				_addImgDialog.dismiss();
			}
		});
		
		_addImgDialog.setButtonRightText(_activity.getString(R.string.text_import_image));
		_addImgDialog.setButtonRightIcon(BitmapFactory.decodeResource(_activity.getResources(), R.drawable.import_image));
		_addImgDialog.setButtonRightOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				_listener.onImportImage();
				_addImgDialog.dismiss();
			}
		});
		
		return _addImgDialog;
	}
	
}
