package mo.pay.post_it_album;

import java.util.ArrayList;
import java.util.List;

import mo.pay.post_it.R;

import android.app.Activity;
import android.text.TextUtils.TruncateAt;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AlbumViewItemAdapter extends BaseAdapter
{

	private Activity 		_activity = null;
	private LayoutInflater _inflater;
	
	private List<Album> _albums = new ArrayList<Album>();
	
	public AlbumViewItemAdapter(Activity activity, List<Album> items)
	{
		_activity 	= activity;
		_inflater 	= LayoutInflater.from(_activity);
		_albums = items;
	}
	
	public void setAlbums(List<Album> albums)
	{
		_albums = albums;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View return_view = null;

		return_view = _inflater.inflate(R.layout.album_view_item, null);

		ImageView cover  	 = (ImageView) return_view.findViewById(R.id.album_view_item_cover);
		TextView  name   	 = (TextView) return_view.findViewById(R.id.album_view_name);
		
		try
		{
			if(_albums.get(position) != null)
			{
				if(_albums.get(position).isLocked() && !_albums.get(position).getPassword().equals(""))
				{
					cover.setImageResource(R.drawable.album_cover_locked);
				}
				else
				{
					cover.setImageResource(R.drawable.album_cover_default);
				}
				
				name.setText(_albums.get(position).getAlbumName());
				name.setEllipsize(TruncateAt.MARQUEE);
				name.setMarqueeRepeatLimit(-1); 
				name.setSelected(true);
			}
		}catch(Exception e){}
		
		return_view.startAnimation(AnimationUtils.loadAnimation(_activity, android.R.anim.fade_in));
		return return_view;
	}
	
	@Override
	public int getCount()
	{
		// TODO Auto-generated method stub
		return _albums.size();
	}

	@Override
	public Object getItem(int position)
	{
		// TODO Auto-generated method stub
		return _albums.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		// TODO Auto-generated method stub
		return 0;
	}

}
