package mo.pay.post_it.ui;

import mo.pay.post_it.R;
import android.app.Activity;
import android.app.Dialog;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class DialogEditName extends Dialog
{
	public static final int		ON_NAME_EDITED		= 0x601;
	
	private Activity 	_context;
	private Handler	 	_handler;
	private EditText 	_edit_name;
	private String  	_org_string;

	public DialogEditName(Activity context, Handler handler, String orgString)
	{
		super(context, R.style.Theme_Teansparent);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		_context = context;
		_handler = handler;
		_org_string = orgString;

		setContentView(R.layout.dialog_edit_text);

		Button btn_ok = (Button) findViewById(R.id.dialog_rename_note_btn_ok);
		Button btn_cancel = (Button) findViewById(R.id.dialog_rename_note_btn_cancel);
		_edit_name = (EditText) findViewById(R.id.dialog_rename_note_edit_name);

		_edit_name.setText(_org_string);

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
					msg.what = ON_NAME_EDITED;
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
		
		this.setCanceledOnTouchOutside(false);
	}
}
