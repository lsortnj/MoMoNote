package mo.pay.post_it_handDraw;

import mo.pay.post_it.R;
import mo.pay.post_it.util.DeviceInfoUtil;
import mo.pay.post_it.widget.MomoStrokeView;
import mo.pay.post_it_album.AlbumPageView;
import mo.pay.post_it_handDraw.MoreColorPickerDialog.OnColorChangedListener;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.FrameLayout.LayoutParams;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class ViewDrawSetting
{
	private static final int TARGET_DRAW_NORMAL		= 0x100;
	private static final int TARGET_DRAW_HIGHLIGHT	= 0x101;
	 
	private static final int COLOR_TABLE_ROWS 		= 4;
	private static final int COLOR_TABLE_COLUMNS 	= 4;
	
	private static int[] COLOR_TABLE = 
	{
		//row 1
		Color.parseColor("#FFFFFF"), Color.parseColor("#000000"),
		Color.parseColor("#8B0100"), Color.parseColor("#FF8C01"),
		//row 2
		Color.parseColor("#A9A9A9"), Color.parseColor("#40E1D1"),
		Color.parseColor("#028002"), Color.parseColor("#820083"),
		//row 3
		Color.parseColor("#FFFF00"), Color.parseColor("#FE0000"),
		Color.parseColor("#008081"), Color.parseColor("#0000FE"),
		//row 4
		Color.parseColor("#FA0385"), Color.parseColor("#948953"),
		Color.parseColor("#5291E1"), Color.parseColor("#32849C"),
	};
	
	
	private static	View 		_root					= null;
	private static  View		_viewDrawNormal			= null;
	private static  View		_viewDrawHighlight		= null;
	private static  View		_viewDrawEraser			= null;
	private static  ViewGroup	_settingPanelArea		= null;
	
	private static	MomoStrokeView 		_strokeIconNormal		= null;
	private static	MomoStrokeView 		_strokeIconHighlight	= null;
	private static	MomoStrokeView 		_strokeIconEraser		= null;
	
	private static  ImageView tabDrawNormal 	= null;
	private static  ImageView tabDrawHighlight 	= null;
	private static  ImageView tabEraser			= null;
	
	private static 	Context		_context				= null;
	
	private static  IHandDrawListener		_handDrawListener		= null;
	
	public static void initView
	(
		Context context, 
		IHandDrawListener handDrawListener
	)
	{
		_context 			= context;
		_handDrawListener 	= handDrawListener;
		
		LayoutInflater inflater = LayoutInflater.from(_context);
		
		_root 				 = inflater.inflate(R.layout.view_draw_setting, null);
		_viewDrawNormal 	 = inflater.inflate(R.layout.view_draw_normal, null);
		_viewDrawHighlight 	 = inflater.inflate(R.layout.view_draw_highlight, null);
		_viewDrawEraser	 	 = inflater.inflate(R.layout.view_draw_eraser, null);
		
		_strokeIconNormal 	  = (MomoStrokeView) _viewDrawNormal.findViewById(R.id.view_draw_normal_stroke_img);
		_strokeIconHighlight  = (MomoStrokeView) _viewDrawHighlight.findViewById(R.id.view_draw_highlight_stroke);
		_strokeIconEraser     = (MomoStrokeView) _viewDrawEraser.findViewById(R.id.view_draw_eraser_img_stroke);
		
		_settingPanelArea    = (ViewGroup) _root.findViewById(R.id.draw_setting_panel_area);
		
		initFunctionSwitch();
		initDrawNormalPanel();
		initDrawHighlightPanel();
		initDrawEraserPanel();
		
		_settingPanelArea.removeAllViews();
		_settingPanelArea.addView(_viewDrawNormal);
	}
	
	public static View getViewDrawSetting(AlbumPageView albumPageView)
	{
		if(_root.getParent() != null)
		{
			ViewParent parentView = _root.getParent();
			((ViewGroup)parentView).removeView(_root);
		}
		
		refreshCurrentValue(albumPageView);
		
		return _root;
	}
	
	private static void adjustWidth()
	{
		SeekBar normalSeek 		= (SeekBar) _viewDrawNormal.findViewById(R.id.view_draw_normal_seekbar);
		SeekBar highlightSeek 	= (SeekBar) _viewDrawHighlight.findViewById(R.id.view_draw_highlight_seekbar_stroke);
		SeekBar alphaSeek    	= (SeekBar) _viewDrawHighlight.findViewById(R.id.view_draw_highlight_seekbar_alpha);
		SeekBar eraserSeek 		= (SeekBar) _viewDrawEraser.findViewById(R.id.view_draw_eraser_seekbar_stroke);
		
		ViewGroup normalColorPick = (ViewGroup) _viewDrawNormal.findViewById(R.id.view_draw_normal_quick_pick_area);
		ViewGroup highlightColorPick = (ViewGroup) _viewDrawHighlight.findViewById(R.id.view_draw_highlight_quick_pick_area);
		
		int width = DeviceInfoUtil.getDeviceDisplayMetrics().widthPixels-80;
		
		if(DeviceInfoUtil.getDeviceDisplayMetrics().widthPixels >= 540)
		{
			width = 320; 
				 
			RelativeLayout.LayoutParams panelAreaParams = (RelativeLayout.LayoutParams) normalColorPick.getLayoutParams();
			panelAreaParams.width = 400;
			normalColorPick.setLayoutParams(panelAreaParams);
			 
			panelAreaParams = (RelativeLayout.LayoutParams) highlightColorPick.getLayoutParams();
			panelAreaParams.width = 400;
			highlightColorPick.setLayoutParams(panelAreaParams);
		}
		else
		{
			RelativeLayout.LayoutParams panelAreaParams = (RelativeLayout.LayoutParams) normalColorPick.getLayoutParams();
			panelAreaParams.width = LayoutParams.MATCH_PARENT;
			normalColorPick.setLayoutParams(panelAreaParams);
			
			panelAreaParams = (RelativeLayout.LayoutParams) highlightColorPick.getLayoutParams();
			panelAreaParams.width = LayoutParams.MATCH_PARENT;
			highlightColorPick.setLayoutParams(panelAreaParams);
		}
		
		RelativeLayout.LayoutParams normalSeekParams = (RelativeLayout.LayoutParams) normalSeek.getLayoutParams();
		normalSeekParams.width = width;
		normalSeek.setLayoutParams(normalSeekParams);
		
		RelativeLayout.LayoutParams highlightSeekParams = (RelativeLayout.LayoutParams) highlightSeek.getLayoutParams();
		highlightSeekParams.width = width;
		highlightSeek.setLayoutParams(highlightSeekParams);
		
		RelativeLayout.LayoutParams alphaSeekParams = (RelativeLayout.LayoutParams) alphaSeek.getLayoutParams();
		alphaSeekParams.width = width;
		alphaSeek.setLayoutParams(alphaSeekParams);
		
		RelativeLayout.LayoutParams eraserSeekParams = (RelativeLayout.LayoutParams) eraserSeek.getLayoutParams();
		eraserSeekParams.width = width;
		eraserSeek.setLayoutParams(eraserSeekParams);
	}
	
	private static void refreshCurrentValue(AlbumPageView albumPageView)
	{
		SeekBar normalSeek 		= (SeekBar) _viewDrawNormal.findViewById(R.id.view_draw_normal_seekbar);
		SeekBar highlightSeek 	= (SeekBar) _viewDrawHighlight.findViewById(R.id.view_draw_highlight_seekbar_stroke);
		SeekBar alphatSeek 		= (SeekBar) _viewDrawHighlight.findViewById(R.id.view_draw_highlight_seekbar_alpha);
		SeekBar eraserSeek 		= (SeekBar) _viewDrawEraser.findViewById(R.id.view_draw_eraser_seekbar_stroke);
		
		adjustWidth();
		
		normalSeek.setProgress(albumPageView.getHandDrawView().getNormalStrokeWidth());
		highlightSeek.setProgress(albumPageView.getHandDrawView().getHighlightStrokeWidth());
		alphatSeek.setProgress(albumPageView.getHandDrawView().getHighlightPaintAlpha());
		eraserSeek.setProgress(albumPageView.getHandDrawView().getEraserStrokeWidth());
		
		_strokeIconNormal.setColor(albumPageView.getHandDrawView().getNormalPaintColor());
		_strokeIconNormal.setStroke(albumPageView.getHandDrawView().getNormalStrokeWidth());
		
		_strokeIconHighlight.setColor(albumPageView.getHandDrawView().getHighlightPaintColor());
		_strokeIconHighlight.setStroke((albumPageView.getHandDrawView().getHighlightStrokeWidth()*2)/3);
		_strokeIconHighlight.setPaintAlpha(albumPageView.getHandDrawView().getHighlightPaintAlpha());
		
		_strokeIconEraser.setColor(Color.TRANSPARENT);
		_strokeIconEraser.setStroke(albumPageView.getHandDrawView().getEraserStrokeWidth()/2);
		
		switch(albumPageView.getHandDrawView().getCurrentPaintState())  
		{
			case HandDrawView.STATE_PAINT_NORMAL:
				toDrawNormal();
				break;
				
			case HandDrawView.STATE_PAINT_HIGHLIGHT:
				toDrawHighlight();
				break;
				
			case HandDrawView.STATE_ERASER:
				toDrawEraser();
				break;
				
			case HandDrawView.STATE_PAINT_DISABLE:
				toDrawNormal();
				break;
				
			case HandDrawView.STATE_UNKNOW:
				toDrawNormal();
				break;
		}
	}
	
	private static void toDrawNormal()
	{
		_handDrawListener.onDrawNormalSelected();
		_settingPanelArea.removeAllViews();
		_settingPanelArea.addView(_viewDrawNormal);
		_settingPanelArea.startAnimation(AnimationUtils.loadAnimation(_context, android.R.anim.fade_in));
		
		tabDrawNormal.setImageResource(R.drawable.draw_normal_f);
		tabDrawHighlight.setImageResource(R.drawable.draw_highlight);
		tabEraser.setImageResource(R.drawable.eraser);
	}
	
	private static void toDrawHighlight()
	{
		_handDrawListener.onDrawHighlightSelected();
		_settingPanelArea.removeAllViews();
		_settingPanelArea.addView(_viewDrawHighlight);
		_settingPanelArea.startAnimation(AnimationUtils.loadAnimation(_context, android.R.anim.fade_in));
		
		tabDrawHighlight.setImageResource(R.drawable.draw_highlight_f);
		tabDrawNormal.setImageResource(R.drawable.draw_normal);
		tabEraser.setImageResource(R.drawable.eraser);
	}
	
	private static void toDrawEraser()
	{
		_handDrawListener.onEraserSelected();
		_settingPanelArea.removeAllViews();
		_settingPanelArea.addView(_viewDrawEraser);
		_settingPanelArea.startAnimation(AnimationUtils.loadAnimation(_context, android.R.anim.fade_in));
		
		tabEraser.setImageResource(R.drawable.eraser_f);
		tabDrawNormal.setImageResource(R.drawable.draw_normal);
		tabDrawHighlight.setImageResource(R.drawable.draw_highlight);
	}
	
	private static void initFunctionSwitch()
	{
		tabDrawNormal 		= (ImageView) _root.findViewById(R.id.draw_setting_btn_draw_normal);
		tabDrawHighlight 	= (ImageView)_root.findViewById(R.id.draw_setting_btn_draw_highlight);
		tabEraser	 		= (ImageView)_root.findViewById(R.id.draw_setting_btn_draw_eraser);
		
		tabDrawNormal.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v)
			{
				toDrawNormal();
			}
		});
		
		tabDrawHighlight.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v)
			{
				toDrawHighlight();
			}
		});
		
		tabEraser.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v)
			{
				toDrawEraser();
			}
		});
	}
	
	private static void changeStrokeIconSize(MomoStrokeView strokeView, int currentValue)
	{
		strokeView.setStroke(currentValue);
	}
	
	private static void changeStrokeIconAlpha(MomoStrokeView strokeView, int currentValue)
	{
		strokeView.setPaintAlpha(currentValue);
	}
	
	private static void initDrawNormalPanel()
	{
		SeekBar 	strokeWidthSeek    = (SeekBar) _viewDrawNormal.findViewById(R.id.view_draw_normal_seekbar);
		ViewGroup	quickPickColorArea = (ViewGroup) _viewDrawNormal.findViewById(R.id.view_draw_normal_quick_pick_area);
		ViewGroup	buttonArea 		   = (ViewGroup) _viewDrawNormal.findViewById(R.id.view_draw_normal_btn_area);
	
		strokeWidthSeek.setProgress(HandDrawView.STROKE_WIDTH_THIN);
		strokeWidthSeek.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser)
			{
				if(progress<HandDrawView.STROKE_WIDTH_VERY_THIN)
				{
					seekBar.setProgress(HandDrawView.STROKE_WIDTH_VERY_THIN);
				}
				else
				{
					changeStrokeIconSize(_strokeIconNormal, progress);
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar){}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar)
			{
				_handDrawListener.onDrawNormalStrokeWidthChange(seekBar.getProgress());
			}
			
		});
		
		quickPickColorArea.removeAllViews();
		quickPickColorArea.addView(getColorTable(TARGET_DRAW_NORMAL));
		 
		LinearLayout.LayoutParams parmas = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		parmas.weight = 1;
		
		Button btnMoreColor = new Button(_context);
		btnMoreColor.setText(R.string.text_more_color);
		btnMoreColor.setTextColor(_context.getResources().getColor(R.color.button_text));
		btnMoreColor.setBackgroundResource(R.drawable.button_type2);
		btnMoreColor.setLayoutParams(parmas);
		btnMoreColor.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v)
			{
				MoreColorPickerDialog color_picker = new MoreColorPickerDialog
						(_context, new DrawNormalColoChangeListener(), Color.BLACK);

				color_picker.setCanceledOnTouchOutside(true);

				color_picker.show();
			}
		});
		
		Button btnOK = new Button(_context);
		btnOK.setText(R.string.text_comfirm);
		btnOK.setTextColor(_context.getResources().getColor(R.color.button_text));
		btnOK.setBackgroundResource(R.drawable.button_type2);
		btnOK.setLayoutParams(parmas);
		btnOK.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v)
			{
				_handDrawListener.onDone();
			}
		});
		
		buttonArea.removeAllViews();
		buttonArea.addView(btnMoreColor);
		buttonArea.addView(btnOK);
	}
	
	private static void initDrawHighlightPanel()
	{
		SeekBar 	strokeWidthSeek    = (SeekBar) _viewDrawHighlight.findViewById(R.id.view_draw_highlight_seekbar_stroke);
		SeekBar 	alphaSeek    	   = (SeekBar) _viewDrawHighlight.findViewById(R.id.view_draw_highlight_seekbar_alpha);
		ViewGroup	quickPickColorArea = (ViewGroup) _viewDrawHighlight.findViewById(R.id.view_draw_highlight_quick_pick_area);
		ViewGroup	buttonArea 		   = (ViewGroup) _viewDrawHighlight.findViewById(R.id.view_draw_highlight_btn_area);
	
		alphaSeek.setProgress(HandDrawView.ALPHA_MIN);
		alphaSeek.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser)
			{
				if(progress<HandDrawView.ALPHA_MIN)
				{
					seekBar.setProgress(HandDrawView.ALPHA_MIN);
				}
				else
				{
					changeStrokeIconAlpha(_strokeIconHighlight,progress);
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar){}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar)
			{
				_handDrawListener.onDrawHighlightAlphaChange(seekBar.getProgress());
			}
			
		});
		
		strokeWidthSeek.setProgress(HandDrawView.STROKE_WIDTH_THIN);
		strokeWidthSeek.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser)
			{
				if(progress<HandDrawView.STROKE_WIDTH_VERY_THIN)
				{
					seekBar.setProgress(HandDrawView.STROKE_WIDTH_VERY_THIN);
				}
				else
				{
					changeStrokeIconSize(_strokeIconHighlight,((progress*2)/3));
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar){}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar)
			{
				_handDrawListener.onDrawHighlightStrokeWidthChange(seekBar.getProgress());
			}
			
		});
		
		quickPickColorArea.removeAllViews();
		quickPickColorArea.addView(getColorTable(TARGET_DRAW_HIGHLIGHT));
		
		LinearLayout.LayoutParams parmas = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		parmas.weight = 1;
		
		Button btnMoreColor = new Button(_context);
		btnMoreColor.setText(R.string.text_more_color);
		btnMoreColor.setTextColor(_context.getResources().getColor(R.color.button_text));
		btnMoreColor.setBackgroundResource(R.drawable.button_type2);
		btnMoreColor.setLayoutParams(parmas);
		btnMoreColor.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v)
			{
				MoreColorPickerDialog color_picker = new MoreColorPickerDialog
						(_context, new DrawHighlightColoChangeListener(), Color.BLACK);

				color_picker.setCanceledOnTouchOutside(true);

				color_picker.show();
			}
		});
		
		Button btnOK = new Button(_context);
		btnOK.setText(R.string.text_comfirm);
		btnOK.setTextColor(_context.getResources().getColor(R.color.button_text));
		btnOK.setBackgroundResource(R.drawable.button_type2);
		btnOK.setLayoutParams(parmas);
		btnOK.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v)
			{
				_handDrawListener.onDone();
			}
		});
		
		buttonArea.removeAllViews();
		buttonArea.addView(btnMoreColor);
		buttonArea.addView(btnOK);
	}
	
	private static void initDrawEraserPanel()
	{
		SeekBar 	strokeWidthSeek    = (SeekBar) _viewDrawEraser.findViewById(R.id.view_draw_eraser_seekbar_stroke);
		ViewGroup	buttonArea 		   = (ViewGroup) _viewDrawEraser.findViewById(R.id.view_draw_eraser_btn_area);
		
		strokeWidthSeek.setProgress(HandDrawView.STROKE_WIDTH_ERASER);
		strokeWidthSeek.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser)
			{
				if(progress<HandDrawView.STROKE_WIDTH_THIN)
				{
					seekBar.setProgress(HandDrawView.STROKE_WIDTH_THIN);
				}
				else
				{
					changeStrokeIconSize(_strokeIconEraser,(progress/2));
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar){}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar)
			{
				_handDrawListener.onEraserStrokeWidthChange(seekBar.getProgress());
			}
			
		});
		
		
		LinearLayout.LayoutParams parmas = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		parmas.weight = 1;
		
		Button btnOK = new Button(_context);
		btnOK.setText(R.string.text_comfirm);
		btnOK.setTextColor(_context.getResources().getColor(R.color.button_text));
		btnOK.setBackgroundResource(R.drawable.button_type2);
		btnOK.setLayoutParams(parmas);
		btnOK.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v)
			{
				_handDrawListener.onDone();
			}
		});
		
		buttonArea.removeAllViews();
		buttonArea.addView(btnOK);
	}
	
	private static View getColorTable(final int target)
	{
		LinearLayout color_area = new LinearLayout(_context);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER_HORIZONTAL;
		
		color_area.setGravity(Gravity.CENTER_HORIZONTAL);
		color_area.setOrientation(LinearLayout.VERTICAL);
		color_area.setLayoutParams(params);
		
		int colorGridWidth = 60;
		
		for(int rowIdx=0; rowIdx<COLOR_TABLE_ROWS; rowIdx++)
		{
			LinearLayout color_row_layout = new LinearLayout(_context);
			color_row_layout.setLayoutParams(params);
			color_row_layout.setOrientation(LinearLayout.HORIZONTAL);
			color_row_layout.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			
			for (int columnIdx = 0; columnIdx < COLOR_TABLE_COLUMNS; columnIdx++)
			{
				LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
						colorGridWidth, colorGridWidth);
				param.weight = 1;
				param.setMargins(5, 5, 5, 5);

				Button color_grid = new Button(_context);
				color_grid.setGravity(Gravity.CENTER);
				color_grid.setLayoutParams(param);
				color_grid.setBackgroundColor(COLOR_TABLE[(rowIdx*(COLOR_TABLE_COLUMNS))+columnIdx]);
				color_grid.setTag(COLOR_TABLE[(rowIdx*(COLOR_TABLE_COLUMNS))+columnIdx]);
				color_row_layout.addView(color_grid);
				color_grid.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						v.startAnimation(AnimationUtils.loadAnimation(_context, android.R.anim.fade_in));
						
						switch(target)
						{
							case TARGET_DRAW_NORMAL:
								_strokeIconNormal.setColor((Integer)v.getTag());
								_handDrawListener.onDrawNormalColorChange((Integer)v.getTag());
								break;
								
							case TARGET_DRAW_HIGHLIGHT:
								_strokeIconHighlight.setColor((Integer)v.getTag());
								_handDrawListener.onDrawHighlightColorChange((Integer)v.getTag());
								break;
						}
						
					}
				});
			}
			
			color_area.addView(color_row_layout);
		}
		
		
		return color_area;
	}
	
	static class DrawNormalColoChangeListener implements OnColorChangedListener
	{
		@Override
		public void colorChanged(int color)
		{
			_strokeIconNormal.setColor(color);
			_handDrawListener.onDrawNormalColorChange(color);
		}
	}
	
	static class DrawHighlightColoChangeListener implements OnColorChangedListener
	{
		@Override
		public void colorChanged(int color)
		{
			_strokeIconHighlight.setColor(color);
			_handDrawListener.onDrawHighlightColorChange(color);
		}
	}
}
