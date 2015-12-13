package mo.pay.post_it_handDraw;

public class HandDraw
{
	private String  _albumName		= "";
	private String  _albumId		= "";
	private String  _pageNo 		= "";
	private String  _storePath 		= "";
	
	public void setAlbumName(String name)
	{
		_albumName = name;
	}
	
	public String getAlbumName()
	{
		return _albumName;
	}
	
	public void setStorePath(String path)
	{
		_storePath = path;
	}
	
	public String getStorePath()
	{
		return _storePath;
	}
	
	public void setPageNo(String pageNo)
	{
		_pageNo = pageNo;
	}
	
	public String getPageNo()
	{
		return _pageNo;
	}
	
	public void setAlbumId(String albumId)
	{
		_albumId = albumId;
	}
	
	public String getAlbumId()
	{
		return _albumId;
	}
}
