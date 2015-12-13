package mo.pay.post_it_handDraw;

import mo.pay.post_it.R;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;

public class DialogQuickColorPicker extends Dialog implements MoreColorPickerDialog.OnColorChangedListener
{
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

	private ColorSelectListener _listener = null;
	private Context _context;
	private View	_target_view = null;

	public DialogQuickColorPicker(Context context, ColorSelectListener listener)
	{
		super(context, R.style.Theme_Teansparent);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		_context = context;
		_listener = listener;

		setContentView(R.layout.dialog_color_picker);
		LinearLayout colorArea = (LinearLayout) findViewById(R.id.dialog_quick_color_picker_color_area);
		Button more_color = (Button) findViewById(R.id.dialog_quick_color_picker_btn_more_color);
		Button cancel = (Button) findViewById(R.id.dialog_quick_color_pick_btn_cancel);

		cancel.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				dismiss();
			}
		});

		more_color.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				MoreColorPickerDialog color_picker = new MoreColorPickerDialog(
						_context, DialogQuickColorPicker.this, Color.BLACK);

				color_picker.setCanceledOnTouchOutside(true);

				color_picker.show();
			}
		});

		colorArea.addView(getColorTable());
	}
	
	public void setTargetView(View targetView)
	{
		_target_view = targetView;
	}

	@Override
	public void colorChanged(int color)
	{
		if (_listener != null)
		{
			_listener.onColorSelected(color, this, _target_view);
			
			dismiss();
		}
	}

	public View getColorTable()
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
						
						_listener.onColorSelected((Integer) v.getTag(), DialogQuickColorPicker.this , _target_view);

						dismiss();
					}
				});
			}
			
			color_area.addView(color_row_layout);
		}
		
		return color_area;
	}

}
