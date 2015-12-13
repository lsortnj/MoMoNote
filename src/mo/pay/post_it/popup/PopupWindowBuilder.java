package mo.pay.post_it.popup;

import mo.pay.post_it.R;
import mo.pay.post_it.logic.AlbumViewListener;
import mo.pay.post_it.widget.MomoTextView;
import mo.pay.post_it_handDraw.HandDrawView;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class PopupWindowBuilder
{ 
	private static Activity 			_activity 				= null;
	private static Handler 				_handler 				= null;
	
	private static QuickAction 	_momo_img_view_quick_action 	= null;
	private static QuickAction 	_momo_tx_view_quick_action 	= null;

	
	public static QuickAction getMomoImageViewQuickAction
	(
		Activity activity,
		final AlbumViewListener listener,
		final View view
	)
	{
		_activity 	= activity;
		
		_momo_img_view_quick_action = new QuickAction(view,QuickAction.TYPE_DARK);
		
		
		ActionItem delete = new ActionItem();
		delete.setTitle(_activity.getString(R.string.text_delete));
		delete.setIcon(_activity.getResources().getDrawable(R.drawable.delete));
		delete.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				listener.onViewDelete(view);
				
				_momo_img_view_quick_action.dismiss();
			}
		});
		
		_momo_img_view_quick_action.addActionItem(delete);
		
		return _momo_img_view_quick_action;
	}
	
	public static QuickAction getMomoTextViewQuickAction
	(
		Activity activity,
		final AlbumViewListener listener,
		final View view
	)
	{
		_activity 	= activity;
		 
		_momo_tx_view_quick_action = new QuickAction(view,QuickAction.TYPE_DARK);
		
		SeekBar seekBarChangeSize =new SeekBar(_activity);
		seekBarChangeSize.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
		seekBarChangeSize.setThumb(_activity.getResources().getDrawable(R.drawable.seekbar_thumb));
		seekBarChangeSize.setProgressDrawable(_activity.getResources().getDrawable(R.drawable.seekbar_progress));
		seekBarChangeSize.setProgress(((MomoTextView)view).getTextSize());
		seekBarChangeSize.setMax((int) MomoTextView.FONT_SIZE_MAX);
		seekBarChangeSize.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser)
			{
				if(progress<MomoTextView.FONT_SIZE_MIN)
				{
					seekBar.setProgress((int)MomoTextView.FONT_SIZE_MIN);
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar){}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar)
			{
				listener.onMomoTextViewTextSizeChange((int)seekBar.getProgress(),view);
			}
			
		});
		
		
		ActionItem change_txt_size = new ActionItem();
		change_txt_size.setIcon(_activity.getResources().getDrawable(R.drawable.text_size));
		change_txt_size.setCustomView(seekBarChangeSize);
		
		ActionItem change_color = new ActionItem();
		change_color.setTitle(_activity.getString(R.string.momo_tx_view_op_change_color));
		change_color.setIcon(_activity.getResources().getDrawable(R.drawable.change_text_color));
		change_color.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				listener.onMomoTextViewColorChange(view);
				
				_momo_tx_view_quick_action.dismiss();
			}
		});
		
		ActionItem text_edit = new ActionItem();
		text_edit.setTitle(_activity.getString(R.string.momo_tx_view_op_change_text));
		text_edit.setIcon(_activity.getResources().getDrawable(R.drawable.text_edit));
		text_edit.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				listener.onMomoTextViewEditText(view);
				
				_momo_tx_view_quick_action.dismiss();
			}
		});
		
		ActionItem delete = new ActionItem();
		delete.setTitle(_activity.getString(R.string.text_delete));
		delete.setIcon(_activity.getResources().getDrawable(R.drawable.delete));
		delete.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				listener.onViewDelete(view);
				
				_momo_tx_view_quick_action.dismiss();
			}
		});
		
		_momo_tx_view_quick_action.addActionItem(change_txt_size);
		_momo_tx_view_quick_action.addActionItem(text_edit);
		_momo_tx_view_quick_action.addActionItem(change_color);
		_momo_tx_view_quick_action.addActionItem(delete);
		
		
		return _momo_tx_view_quick_action;
	}
	
	
}
