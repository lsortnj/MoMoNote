package mo.pay.post_it.ui;

import mo.pay.post_it.R;
import android.app.Activity;
import android.app.Dialog;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class DialogSingleLineInput extends Dialog
{
	public static final int		ON_INPUT_COMPLETED		= 0x601;
	
	private Activity 	_context;
	private Handler	 	_handler;
	private EditText 	_edit_name;
	private String  	_org_string;

	public DialogSingleLineInput(Activity context, Handler handler, String title, String orgString)
	{
		super(context, R.style.Theme_Teansparent);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
  
		_context = context;
		_handler = handler;
		_org_string = orgString;

		setContentView(R.layout.dialog_single_line_input);

		Button btn_ok = (Button) findViewById(R.id.dialog_single_input_btn_ok);
		Button btn_cancel = (Button) findViewById(R.id.dialog_single_input_btn_cancel);
		TextView tv_title = (TextView) findViewById(R.id.dialog_single_input_title);
		tv_title.setText(title);
		_edit_name = (EditText) findViewById(R.id.dialog_single_input_edittext);

		_edit_name.setText(_org_string);
		_edit_name.setSelection(0, _org_string.length());

		btn_ok.setTextSize(TypedValue.COMPLEX_UNIT_PX,_context.getResources().getDimensionPixelSize(R.dimen.button_text_size));
		btn_cancel.setTextSize(TypedValue.COMPLEX_UNIT_PX,_context.getResources().getDimensionPixelSize(R.dimen.button_text_size));
		
		btn_ok.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (!_edit_name.getText().toString().equals(""))
				{
					if (_edit_name.getText().toString().length() == 0)
					{
						new DialogSingleButton(_context,_context.getString(R.string.error_empty_imput)).show();

						return;
					}

					Message msg = new Message();
					msg.what = ON_INPUT_COMPLETED;
					msg.obj = _edit_name.getText().toString();
					
					_handler.sendMessage(msg);
					
					dismiss();
				}
			}
		});

		btn_cancel.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				dismiss();
			}
		});
		
		_edit_name.requestFocus();
	}

}
