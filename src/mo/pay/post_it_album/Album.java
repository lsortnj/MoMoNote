package mo.pay.post_it_album;

import java.util.ArrayList;
import java.util.List;

import mo.pay.post_it.widget.MomoImageView;

public class Album
{
	public static final int  ALBUM_LOCKED	= 1;
	public static final int  ALBUM_UNLOCK	= 0;
	
	private List<AlbumPage>  _pages = new ArrayList<AlbumPage>();
	
	private String  _albumId		= "";
	private String  _name 			= "";
	private String  _createDate 	= "";
	private String  _albumRootPath 	= "";
	private String 	_password		= ""; 
	private int		_isLocked		= ALBUM_UNLOCK;
	
	public Album(){}
	
	public Album(String name)
	{
		_name = name;
	}
	
	public void isLocked(boolean isLocked)
	{
		_isLocked = isLocked?1:0;
	}
	
	public boolean isLocked()
	{
		return _isLocked==1?true:false;
	}
	
	public void setPassword(String password)
	{
		_password = password;
	}
	
	public String getPassword()
	{
		return _password;
	}
	
	public void setAlbumId(String id)
	{
		_albumId = id;
	}
	
	public String getAlbumId()
	{
		return _albumId;
	}
	
	public void removeAllPages()
	{
		_pages.clear();
	}
	
	public void removePage(int pageIdx)
	{
		_pages.remove(pageIdx);
	}
	
	public void setAlbumRootPath(String path)
	{
		_albumRootPath = path;
	}
	
	public String getAlbumRootPath()
	{
		return _albumRootPath;
	}
	
	public void setCreateDate(String date)
	{
		_createDate = date;
	}
	
	public String getCreateDate()
	{
		return _createDate;
	}
	
	public List<AlbumPage> getAllAlbumPages()
	{
		return _pages;
	}
	
	public AlbumPage getAlbumPage(int idx)
	{
		return _pages.get(idx);
	}
	
	public void addAlbumPage(AlbumPage page)
	{
		_pages.add(page);
	}
	
	public void setAlbumPage(AlbumPage page, int idx)
	{
		_pages.set(idx, page);
	}
	
	public void deleteAlbumPage(AlbumPage page)
	{
		_pages.remove(page);
	}
	
	public void deleteAlbumPage(int idx)
	{
		_pages.remove(idx);
	}
	
	public String getAlbumName()
	{
		return _name;
	}
	
	public void setAlbumName(String name)
	{
		_name = name;
	}
	
	public int getPagecount()
	{
		return _pages.size();
	}
	
	public void recycleAllBitmap()
	{
		for(AlbumPage page : _pages)
		{
			for(MomoImageView momoIage : page.getImages())
			{
				momoIage.recycleBitmap();
			}
		}
	}
}
