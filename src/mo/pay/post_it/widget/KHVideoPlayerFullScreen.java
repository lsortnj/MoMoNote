package mo.pay.post_it.widget;

import mo.pay.post_it.R;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

public class KHVideoPlayerFullScreen extends Activity
{

	private String _path = "";
	private int _played_position = -1;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags
        (
        	WindowManager.LayoutParams.FLAG_FULLSCREEN, 
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
		setContentView(R.layout.video_player_full_screen);

		Bundle bundle = this.getIntent().getExtras();

		_path = bundle.getString("video_path");
		_played_position = bundle.getInt("played_position");

		VideoView video_view = (VideoView) findViewById(R.id.video_view_full_screen);

		MediaController mediaController = new MediaController(this);

		mediaController.setAnchorView(video_view);

		video_view.setMediaController(mediaController);

		video_view.setVideoURI(Uri.parse(_path));

		video_view.start();

		if (_played_position != -1)
		{
			video_view.seekTo(_played_position);
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{

			finish();

			return true;
		}

		return super.onKeyDown(keyCode, event);
	}
}
