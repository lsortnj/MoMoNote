package mo.pay.post_it.ui;

import java.io.File;

import mo.pay.post_it.R;
import mo.pay.post_it.store.AlbumPageAudioStore;
import mo.pay.post_it.store.AlbumPageHandDrawStore;
import mo.pay.post_it.store.AlbumPageImageStore;
import mo.pay.post_it.store.AlbumPageTextStore;
import mo.pay.post_it.store.AlbumPageVideoStore;
import mo.pay.post_it.store.AlbumStore;
import mo.pay.post_it.util.FileUtil;
import mo.pay.post_it.util.PinCodeUtil;
import mo.pay.post_it.util.VibrateUtil;
import mo.pay.post_it.widget.MomoToast;
import mo.pay.post_it_album.Album;
import android.app.Activity;
import android.app.Dialog;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

public class DialogPinCode extends Dialog
{
	public static final int		ON_PINCODE_SET			= 0x301;
	public static final int		ON_PINCODE_CANCELED		= 0x302;
	public static final int		ON_PINCODE_VERIFY		= 0x303;
	
	public static final int		MODE_UNKNOW		 			= -1;
	public static final int		MODE_LOCK_ALBUM 			= 0x400;
	public static final int		MODE_CANCEL_LOCK_ALBUM 		= 0x401;
	public static final int		MODE_UNLOCK_TO_VIEW_ALBUM 	= 0x402;
	public static final int		MODE_UNLOCK_TO_DELETE_ALBUM = 0x403;
	
	private Activity 	_activity 	 = null;
	
	private EditText 	_pincodeEdit = null;
	private TextView 	_title		 = null;
	private View		_btnArea	 = null;
	private View	 	_btnOk		 = null;
	private View	 	_btnClear	 = null;
	private View	 	_btn1		 = null;
	private View	 	_btn2		 = null;
	private View	 	_btn3		 = null;
	private View	 	_btn4		 = null;
	private View	 	_btn5		 = null;
	private View	 	_btn6		 = null;
	private View	 	_btn7		 = null;
	private View	 	_btn8		 = null;
	private View	 	_btn9		 = null;
	private View	 	_btn0		 = null;
	
	private String 		_firstPINCode = "";
	private int 		_currentMode  = MODE_UNKNOW;
	
	private Album 		_album 		= null;
	private Handler		_handler 	= null;
	
	
	public DialogPinCode(Album album, Activity activity, int mode, Handler handler)
	{
		super(activity, R.style.Theme_Teansparent);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.dialog_pin_code);
		
		getWindow().setSoftInputMode(
			      WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		 
		_pincodeEdit = (EditText) findViewById(R.id.dialog_pincode_edittext);
		_title		 = (TextView) findViewById(R.id.dialog_pincode_title);
		_btnArea	 = findViewById(R.id.dialog_pincode_btn_area);
		_btnOk		 = findViewById(R.id.dialog_pincode_btn_ok);
		_btnClear	 = findViewById(R.id.diloag_pincode_btn_clear);
		_btn1		 = findViewById(R.id.dialog_pincode_btn1);
		_btn2		 = findViewById(R.id.dialog_pincode_btn2);
		_btn3		 = findViewById(R.id.dialog_pincode_btn3);
		_btn4		 = findViewById(R.id.dialog_pincode_btn4);
		_btn5		 = findViewById(R.id.dialog_pincode_btn5);
		_btn6		 = findViewById(R.id.dialog_pincode_btn6);
		_btn7		 = findViewById(R.id.dialog_pincode_btn7);
		_btn8		 = findViewById(R.id.dialog_pincode_btn8);
		_btn9		 = findViewById(R.id.dialog_pincode_btn9);
		_btn0		 = findViewById(R.id.dialog_pincode_btn0);
		
		_currentMode = mode;
		_activity    = activity;
		_album		 = album;
		_handler	 = handler;
		
//		_pincodeEdit.setInputType(InputType.TYPE_CLASS_TEXT);
//        InputMethodManager mgr = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
//        mgr.hideSoftInputFromWindow(_pincodeEdit.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
		
		switch(_currentMode)
		{
			case MODE_LOCK_ALBUM:
				_title.setText(activity.getString(R.string.title_set_pincode));
				break;
				
			case MODE_CANCEL_LOCK_ALBUM:
				_title.setText(activity.getString(R.string.title_input_pincode_to_unlock));
				break;
				
			case MODE_UNLOCK_TO_VIEW_ALBUM:
				_title.setText(activity.getString(R.string.title_input_pincode_to_unlock));
				break;
				
			case MODE_UNLOCK_TO_DELETE_ALBUM:
				_title.setText(activity.getString(R.string.title_input_pincode_to_unlock));
				break;
				
		}
		
		ButtonAreaClickListener listener = new ButtonAreaClickListener();
		
		_btnOk.setOnClickListener(listener);
		_btnClear.setOnClickListener(listener);
		
		_btn1.setOnClickListener(listener);
		_btn2.setOnClickListener(listener);
		_btn3.setOnClickListener(listener);
		_btn4.setOnClickListener(listener);
		_btn5.setOnClickListener(listener);
		_btn6.setOnClickListener(listener);
		_btn7.setOnClickListener(listener);
		_btn8.setOnClickListener(listener);
		_btn9.setOnClickListener(listener);
		_btn0.setOnClickListener(listener);
		
		_firstPINCode = "";
		
		setCanceledOnTouchOutside(true);
		_title.setSelected(true);
	}
	
	private void doConfirm()
	{
		String pinAfterDecode = "";
		
		switch(_currentMode)
		{
			case MODE_LOCK_ALBUM:
				
				if(_firstPINCode.equals(""))
				{
					_firstPINCode = _pincodeEdit.getText().toString().trim();
					_pincodeEdit.setText("");
					_title.setText(_activity.getString(R.string.title_pincode_again));
					_pincodeEdit.startAnimation(AnimationUtils.loadAnimation(_activity, android.R.anim.fade_in));
					_btnArea.startAnimation(AnimationUtils.loadAnimation(_activity, android.R.anim.fade_in));
				}
				else
				{
					if(_firstPINCode.equals(_pincodeEdit.getText().toString().trim()))
					{
						//Insert to Store
						_album.setPassword(PinCodeUtil.encodePINCode(_firstPINCode));
						_album.isLocked(true);
						
						AlbumStore.instance().insertAlbum(_album);
						
						new MomoToast(_activity,_activity.getString(R.string.tip_set_pincode_success)).show();
						
						if(_handler != null)
						{
							_handler.sendEmptyMessage(ON_PINCODE_SET);
						}
						
						dismiss();
					}
					else
					{
						new MomoToast(_activity,_activity.getString(R.string.tip_second_pincode_not_equal)).show();
					}
				}
				
				break;
				
			case MODE_CANCEL_LOCK_ALBUM:
				
				pinAfterDecode = PinCodeUtil.decodePINCode(_album.getPassword());
				
				if(_pincodeEdit.getText().toString().trim().equals(pinAfterDecode))
				{
					//Insert to Store
					_album.setPassword("");
					_album.isLocked(false);
					
					AlbumStore.instance().insertAlbum(_album);
					
					if(_handler != null)
					{
						_handler.sendEmptyMessage(ON_PINCODE_CANCELED);
					}
					
					new MomoToast(_activity,_activity.getString(R.string.tip_cancel_lock_album_success)).show();
					dismiss();
				}
				else
				{
					new MomoToast(_activity,_activity.getString(R.string.tip_wrong_pincode_to_unlock)).show();
				}
				
				break;
				
			case MODE_UNLOCK_TO_VIEW_ALBUM:
				
				pinAfterDecode = PinCodeUtil.decodePINCode(_album.getPassword());
				
				if(_pincodeEdit.getText().toString().trim().equals(pinAfterDecode))
				{
					if(_handler != null)
					{
						Message msg = new Message();
						msg.what = ON_PINCODE_VERIFY;
						msg.obj  = _album;
						_handler.handleMessage(msg);
					}
					
					dismiss();
				}
				else
				{
					new MomoToast(_activity,_activity.getString(R.string.tip_wrong_pincode)).show();
				}
				
				break;
				
			case MODE_UNLOCK_TO_DELETE_ALBUM:
				
				pinAfterDecode = PinCodeUtil.decodePINCode(_album.getPassword());
				
				if(_pincodeEdit.getText().toString().trim().equals(pinAfterDecode))
				{
					deleteAlbum(_album);
					
					if(_handler != null)
					{
						Message msg = new Message();
						msg.what = ON_PINCODE_SET;
						_handler.handleMessage(msg);
					}
					
					dismiss();
				}
				else
				{
					new MomoToast(_activity,_activity.getString(R.string.tip_wrong_pincode)).show();
				}
		}
	}
	
	private void deleteAlbum(Album album)
	{
		AlbumStore.instance().deleteAlbum(album);
		AlbumPageAudioStore.instance().deleteAllAudios(album.getAlbumId());
		AlbumPageHandDrawStore.instance().deleteAllHandDraw(album.getAlbumId());
		AlbumPageImageStore.instance().deleteAllImages(album.getAlbumId());
		AlbumPageTextStore.instance().deleteAllText(album.getAlbumId());
		AlbumPageVideoStore.instance().deleteAllVideo(album.getAlbumId());
		
		FileUtil.deleteForlder(new File(album.getAlbumRootPath()));
	}
	
	class ButtonAreaClickListener implements View.OnClickListener
	{
		@Override
		public void onClick(View v) 
		{
			VibrateUtil.doVibrate(60);
			
			switch(v.getId())
			{
				case R.id.dialog_pincode_btn1:
					_pincodeEdit.setText(_pincodeEdit.getText().length()<8?_pincodeEdit.getText()+"1":_pincodeEdit.getText());
					break;
					
				case R.id.dialog_pincode_btn2:
					_pincodeEdit.setText(_pincodeEdit.getText().length()<8?_pincodeEdit.getText()+"2":_pincodeEdit.getText());
					break;
					
				case R.id.dialog_pincode_btn3:
					_pincodeEdit.setText(_pincodeEdit.getText().length()<8?_pincodeEdit.getText()+"3":_pincodeEdit.getText());
					break;
					
				case R.id.dialog_pincode_btn4:
					_pincodeEdit.setText(_pincodeEdit.getText().length()<8?_pincodeEdit.getText()+"4":_pincodeEdit.getText());
					break;
					
				case R.id.dialog_pincode_btn5:
					_pincodeEdit.setText(_pincodeEdit.getText().length()<8?_pincodeEdit.getText()+"5":_pincodeEdit.getText());
					break;
					
				case R.id.dialog_pincode_btn6:
					_pincodeEdit.setText(_pincodeEdit.getText().length()<8?_pincodeEdit.getText()+"6":_pincodeEdit.getText());
					break;
					
				case R.id.dialog_pincode_btn7:
					_pincodeEdit.setText(_pincodeEdit.getText().length()<8?_pincodeEdit.getText()+"7":_pincodeEdit.getText());
					break;
					
				case R.id.dialog_pincode_btn8:
					_pincodeEdit.setText(_pincodeEdit.getText().length()<8?_pincodeEdit.getText()+"8":_pincodeEdit.getText());
					break;
					
				case R.id.dialog_pincode_btn9:
					_pincodeEdit.setText(_pincodeEdit.getText().length()<8?_pincodeEdit.getText()+"9":_pincodeEdit.getText());
					break;
					
				case R.id.dialog_pincode_btn0:
					_pincodeEdit.setText(_pincodeEdit.getText().length()<8?_pincodeEdit.getText()+"0":_pincodeEdit.getText());
					break;
					
				case R.id.dialog_pincode_btn_ok:
					
					if(_pincodeEdit.getText().toString().trim().equals(""))
					{
						new MomoToast(_activity,_activity.getString(R.string.tip_pincode_empty)).show();
					}
					else
					{
						doConfirm();
					}
					
					break;
					
				case R.id.diloag_pincode_btn_clear:
					_pincodeEdit.setText("");
					break;
			}
		}
	}
}
