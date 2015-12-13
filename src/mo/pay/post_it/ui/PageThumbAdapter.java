package mo.pay.post_it.ui;

import java.io.File;

import mo.pay.post_it.R;
import mo.pay.post_it_album.Album;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.text.TextUtils.TruncateAt;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PageThumbAdapter extends BaseAdapter
{
	private Activity 			_activity 	= null;
	private LayoutInflater 		_inflater	= null;
	private Album				_album		= null;
	
	public PageThumbAdapter(Activity activity,Album album)
	{
		_album 		= album;
		_activity 	= activity;
		_inflater 	= LayoutInflater.from(_activity);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View return_view = null;
		
		String thumbPath = "";
		
		File[] pageFolders = new File(_album.getAlbumRootPath()).listFiles();
		
		if(pageFolders!= null)
		{
			try
			{
				thumbPath = pageFolders[position].getAbsolutePath()+ File.separator+"thumb.thu";
			}
			catch(Exception e)
			{
				thumbPath="";
			}
		}

		return_view = _inflater.inflate(R.layout.page_thumb, null);

		ImageView image  = (ImageView) return_view.findViewById(R.id.thumb_image);
		TextView  title  = (TextView) return_view.findViewById(R.id.thumb_text);
		 
		if(new File(thumbPath).exists())
		{
			image.setImageBitmap(BitmapFactory.decodeFile(thumbPath));
		}
		else
		{
			image.setImageResource(R.drawable.page_thumb);
		}
		
		title.setText(""+(position+1));
		title.setEllipsize(TruncateAt.MARQUEE); 
		title.setMarqueeRepeatLimit(-1); 
		title.setSelected(true);
		return_view.startAnimation(AnimationUtils.loadAnimation(_activity, android.R.anim.fade_in));
		return return_view;
	}

	@Override
	public int getCount()
	{
		return _album.getPagecount();
	}

	@Override
	public Object getItem(int position)
	{
		return _album.getAlbumPage(position);
	}

	@Override
	public long getItemId(int position)
	{
		return 0;
	}
}
