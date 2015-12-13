/**
 * 
 */
package mo.pay.post_it.userguide;

import mo.pay.post_it.R;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author Chester Chen
 *
 */
public class UserGuideActivity extends Activity implements OnClickListener
{
	public static final int TYPE_FIRST_LANUCH_GUIDE 		= 0x300;
	public static final int TYPE_ALBUM_MENU_GUIDE 	 		= 0x301;
	public static final int TYPE_FIRST_INSERT_OBJECT_GUIDE 	= 0x302;
	public static final int TYPE_EDIT_MODE_GUIDE 			= 0x303;
	public static final int TYPE_ADD_TEXT_GUIDE 			= 0x305;
	public static final int TYPE_REEDIT_OBJECT_GUIDE 		= 0x306;
	
	public static final int EDIT_MODE_GUIDE_ITEMS		= 5;
	public static final int VIEW_MODE_GUIDE_ITEMS		= 2;
	
	private int 		_type 				= TYPE_FIRST_LANUCH_GUIDE;
	private int 		_currentGuideIdex 	= 0;
	private ImageView	_pointerView 		= null;
	private FrameLayout	_mainContainer 		= null;
	private TextView	_messageView 		= null;
	
	private int iconX = 0;
	private int iconY = 0;
	private int iconW = 0;
	private int iconH = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_userguide_main);
		
		_type = this.getIntent().getIntExtra("type", TYPE_FIRST_LANUCH_GUIDE);
		
		_mainContainer= (FrameLayout) findViewById(R.id.userguide_main_container);
		_pointerView  = (ImageView) findViewById(R.id.userguide_main_pointer);
		_messageView  = (TextView) findViewById(R.id.userguide_text_message);
		
		_mainContainer.setOnClickListener(this);
		
		switch(_type)
		{
			case TYPE_FIRST_LANUCH_GUIDE:
				showCreateAlbumGuide();
				break;
				
			case TYPE_ALBUM_MENU_GUIDE:
				showAlbumMenuGuide();
				break;
				
			case TYPE_FIRST_INSERT_OBJECT_GUIDE:
				showFirstAddObjectGuide();
				break;
				
			case TYPE_EDIT_MODE_GUIDE:
				_currentGuideIdex = 0;
				showEditModeGuide();
				break;
				
			case TYPE_ADD_TEXT_GUIDE:
				showAddTextGuide();
				break;
				
			case TYPE_REEDIT_OBJECT_GUIDE:
				showReEditObjectGuide();
				break;
				
		}
	}
	
	private void showAlbumMenuGuide()
	{
		int toolBarHeight  = getIntent().getIntExtra("toolBarHeight", 85);
		
		
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;
		params.setMargins(0, 30, 0, 0);
		
		_messageView.setLayoutParams(params);
		_messageView.setText(getResources().getString(R.string.user_guide_album_menu_guide));
		
		params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.LEFT | Gravity.TOP;
		params.setMargins(80, toolBarHeight + 50, 0, 0);
		 
		Matrix mat = new Matrix();
        mat.postRotate(270);
        Bitmap bMapRotate = BitmapFactory.decodeResource(getResources(), R.drawable.pointer_l_hand);
        bMapRotate = Bitmap.createBitmap(bMapRotate, 0, 0, bMapRotate.getWidth(), bMapRotate.getHeight(), mat, true);
        _pointerView.setImageBitmap(bMapRotate);
		_pointerView.setVisibility(View.VISIBLE);
		_pointerView.setLayoutParams(params);
		
		doAnimatePointer(); 
	}
	
	private void showFirstAddObjectGuide()
	{
		int toolBarHeight  = getIntent().getIntExtra("toolBarHeight", 0);
		
		
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
		params.setMargins(0, toolBarHeight+50, 0, 0);
		
		_messageView.setLayoutParams(params);
		_messageView.setText(getResources().getString(R.string.user_guide_first_add_object));
		
		params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;
		params.setMargins(0, 0, 0, 30);
		
		Matrix mat = new Matrix();
        mat.postRotate(180);
        Bitmap bMapRotate = BitmapFactory.decodeResource(getResources(), R.drawable.pointer_l_hand);
        bMapRotate = Bitmap.createBitmap(bMapRotate, 0, 0, bMapRotate.getWidth(), bMapRotate.getHeight(), mat, true);
        _pointerView.setImageBitmap(bMapRotate);
		_pointerView.setVisibility(View.VISIBLE);
		_pointerView.setLayoutParams(params);
		
		doAnimatePointer(); 
	}
	
	private void showReEditObjectGuide()
	{
		int toolBarHeight  = getIntent().getIntExtra("toolBarHeight", 0);
		
		_pointerView.setVisibility(View.GONE);
		
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
		params.setMargins(0, toolBarHeight+50, 0, 0);
		
		_messageView.setLayoutParams(params);
		_messageView.setText(getResources().getString(R.string.user_guide_reedit_object));
	}
	
	private void showAddTextGuide()
	{
		int toolBarHeight  = getIntent().getIntExtra("toolBarHeight", 0);
		
		
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
		params.setMargins(0, toolBarHeight+50, 0, 0);
		
		_messageView.setLayoutParams(params);
		_messageView.setText(getResources().getString(R.string.user_guide_add_text));
		
		params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;
		params.setMargins(0, 0, 0, 30);
		
		Matrix mat = new Matrix();
        mat.postRotate(180);
        Bitmap bMapRotate = BitmapFactory.decodeResource(getResources(), R.drawable.pointer_l_hand);
        bMapRotate = Bitmap.createBitmap(bMapRotate, 0, 0, bMapRotate.getWidth(), bMapRotate.getHeight(), mat, true);
        _pointerView.setImageBitmap(bMapRotate);
		_pointerView.setVisibility(View.VISIBLE);
		_pointerView.setLayoutParams(params);
		
		doAnimatePointer(); 
	}
	
	private void showEditModeGuide()
	{
		int toolAreaHeight  = getIntent().getIntExtra("toolAreaHeight", 80);
		int toolButtonWidth = getIntent().getIntExtra("toolButtonWidth", 70);
		
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		
		switch(_currentGuideIdex)
		{
			//welcome message
			case 0:
				_pointerView.setVisibility(View.GONE);
				
				params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
				params.gravity = Gravity.CENTER;
				
				_messageView.setText(getResources().getString(R.string.userguide_edit_mode));
				_messageView.setLayoutParams(params);
				break;
			//introducing add object
			case 1:
				params = new FrameLayout.LayoutParams(toolButtonWidth,toolButtonWidth);
				params.topMargin = toolAreaHeight;
				params.gravity = Gravity.RIGHT;
				_pointerView.setVisibility(View.VISIBLE);
				_pointerView.setLayoutParams(params);
				
				
				params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
				params.gravity = Gravity.CENTER;
				
				_messageView.setText(getResources().getString(R.string.user_guide_edit_mode_add_object));
				_messageView.setLayoutParams(params);
				break;
			//introducing hand draw tool	
			case 2:
				params = new FrameLayout.LayoutParams(toolButtonWidth,toolButtonWidth);
				params.rightMargin = toolButtonWidth;
				params.topMargin = toolAreaHeight;
				params.gravity = Gravity.RIGHT;
				_pointerView.setVisibility(View.VISIBLE);
				_pointerView.setLayoutParams(params);
				
				
				params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
				params.gravity = Gravity.CENTER;
				
				_messageView.setText(getResources().getString(R.string.user_guide_edit_mode_draw_tool));
				_messageView.setLayoutParams(params);
				break;
			//to view mode	
			case 3:
				params = new FrameLayout.LayoutParams(toolButtonWidth,toolButtonWidth);
				params.rightMargin = toolButtonWidth*2;
				params.topMargin = toolAreaHeight;
				params.gravity = Gravity.RIGHT;
				_pointerView.setVisibility(View.VISIBLE);
				_pointerView.setLayoutParams(params);
				
				
				params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
				params.gravity = Gravity.CENTER;
				
				_messageView.setText(getResources().getString(R.string.user_guide_edit_mode_to_view_mode));
				_messageView.setLayoutParams(params);
				break;
			//next to add new page	
			case 4:
				params = new FrameLayout.LayoutParams(toolButtonWidth,toolButtonWidth);
				params.bottomMargin = toolButtonWidth;
				params.gravity = Gravity.RIGHT | Gravity.BOTTOM;
				
				Matrix mat = new Matrix();
		        mat.postRotate(180);
		        Bitmap bMapRotate = BitmapFactory.decodeResource(getResources(), R.drawable.pointer_l_hand);
		        bMapRotate = Bitmap.createBitmap(bMapRotate, 0, 0, bMapRotate.getWidth(), bMapRotate.getHeight(), mat, true);
		        _pointerView.setImageBitmap(bMapRotate);
				_pointerView.setVisibility(View.VISIBLE);
				_pointerView.setLayoutParams(params);
				  
				
				params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
				params.gravity = Gravity.CENTER;
				
				_messageView.setText(getResources().getString(R.string.user_guide_edit_mode_next_to_add_page));
				_messageView.setLayoutParams(params);
				break;
		}
		
		doAnimatePointer(); 
	}
	
	private void showCreateAlbumGuide()
	{
		iconX = this.getIntent().getIntExtra("targetX", TYPE_FIRST_LANUCH_GUIDE);
		iconY = this.getIntent().getIntExtra("targetY", TYPE_FIRST_LANUCH_GUIDE);
		iconW = this.getIntent().getIntExtra("targetW", TYPE_FIRST_LANUCH_GUIDE);
		iconH = this.getIntent().getIntExtra("targetH", TYPE_FIRST_LANUCH_GUIDE);
		
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		params.leftMargin = iconX;
		params.topMargin = iconY+iconH+20;
		params.gravity = Gravity.RIGHT;
		
		_pointerView.setLayoutParams(params);
		
		params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;
		
		_messageView.setText(getResources().getString(R.string.userguide_create_album));
		_messageView.setLayoutParams(params);
		
		doAnimatePointer(); 
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
	}
	
	private boolean  _isPointerAni	 	= true;
	private Thread   _pointerAniThread  = null;
	
	private void doAnimatePointer()
	{
		_isPointerAni = false;
		
		final Handler aniHandler = new Handler(){
			public void handleMessage(Message msg)
			{  
				_pointerView.startAnimation
				(
					AnimationUtils.loadAnimation
					(
						UserGuideActivity.this.getBaseContext(), 
						android.R.anim.fade_in
					)
				);
			}
		};
		
		if(_pointerAniThread != null)
		{
			while(_pointerAniThread.isAlive())
			{
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		_isPointerAni = true;
		
		_pointerAniThread = new Thread(new Runnable(){
			public void run()
			{
				while(_isPointerAni)
				{
					aniHandler.sendEmptyMessage(0);
					
					try{Thread.sleep(1000);} 
					catch (InterruptedException e){e.printStackTrace();}
				}
			}
		});
		_pointerAniThread.start();
	}
	

	/* (non-Javadoc)
	 * @see android.view.View.OnTouchListener#onTouch(android.view.View, android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent arg1)
	{
		switch(_type)
		{
			case TYPE_FIRST_LANUCH_GUIDE:
				finish();
				break;
				
			case TYPE_ALBUM_MENU_GUIDE:
				finish();
				break;
				
			case TYPE_FIRST_INSERT_OBJECT_GUIDE:
				finish();
				break;
				
			case TYPE_EDIT_MODE_GUIDE:
				
				break;
				
			case TYPE_ADD_TEXT_GUIDE:
				finish();
				break;
				
			case TYPE_REEDIT_OBJECT_GUIDE:
				finish();
				break;
				
		}
		
		return false;
	}

	@Override
	public void onClick(View v) 
	{
		switch(_type)
		{
			case TYPE_FIRST_LANUCH_GUIDE:
				finish();
				break;
				
			case TYPE_ALBUM_MENU_GUIDE:
				finish();
				break;
				
			case TYPE_FIRST_INSERT_OBJECT_GUIDE:
				finish();
				break;
				
			case TYPE_EDIT_MODE_GUIDE:
				
				if(_currentGuideIdex == EDIT_MODE_GUIDE_ITEMS-1)
					finish();
				
				_currentGuideIdex++;
				showEditModeGuide();
				
				break;
				
			case TYPE_ADD_TEXT_GUIDE:
				finish();
				break;
				
			case TYPE_REEDIT_OBJECT_GUIDE:
				finish();
				break;
		}
	}
}
