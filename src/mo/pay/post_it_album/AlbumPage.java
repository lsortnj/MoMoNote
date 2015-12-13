package mo.pay.post_it_album;

import java.util.ArrayList;
import java.util.List;

import mo.pay.post_it.widget.MomoAudioView;
import mo.pay.post_it.widget.MomoImageView;
import mo.pay.post_it.widget.MomoTextView;
import mo.pay.post_it.widget.MomoVideoView;
import mo.pay.post_it_handDraw.HandDraw;


public class AlbumPage
{ 
	private String 		_albumId		 	= "";
	private String 		_albumPageName 		= "";
	private String 		_albumName 			= "";
	private String 		_pageNo 			= "";
	private HandDraw	_handDraw      		= new HandDraw();
	
	private List<MomoTextView>	_texts 	= new ArrayList<MomoTextView>();
	private List<MomoImageView>	_images = new ArrayList<MomoImageView>();
	private List<MomoVideoView>	_videos = new ArrayList<MomoVideoView>();
	private List<MomoAudioView>	_audios = new ArrayList<MomoAudioView>();
	
	public void setHandDraw(HandDraw handDraw)
	{
		_handDraw = handDraw;
	}
	
	public HandDraw getHandDraw()
	{
		return _handDraw;
	}
	
	public void setAlbumId(String albumId)
	{
		_albumId = albumId;
	}
	
	public String getAlbumId()
	{
		return _albumId;
	}
	
	public void addTextViews(List<MomoTextView> textViews)
	{
		_texts.addAll(textViews);
	}
	
	public void addTextView(MomoTextView textView)
	{
		_texts.add(textView);
	}
	
	public void removeTextView(MomoTextView textView)
	{
		_texts.remove(textView);
	}
	
	public void removeAllTextView()
	{
		_texts.clear();
	}
	
	public void addImageViews(List<MomoImageView> imageViews)
	{
		_images.addAll(imageViews);
	}
	
	public void removeAllImageView()
	{
		_images.clear();
	}
	
	public void addImageView(MomoImageView imageView)
	{
		_images.add(imageView);
	}
	
	public void removeImageView(MomoImageView imageView)
	{
		_images.remove(imageView);
	}
	
	public void addVideoViews(List<MomoVideoView> imageViews)
	{
		_videos.addAll(imageViews);
	}
	
	public void addVideoView(MomoVideoView imageView)
	{
		_videos.add(imageView);
	}
	
	public void removeAllVideoView()
	{
		_videos.clear();
	}
	
	public void removeVideoView(MomoVideoView imageView)
	{
		_videos.remove(imageView);
	}
	
	public void addAudioViews(List<MomoAudioView> audioViews)
	{
		_audios.addAll(audioViews);
	}
	
	public void addAudioView(MomoAudioView audioView)
	{
		_audios.add(audioView);
	}
	
	public void removeAudioView(MomoAudioView audioView)
	{
		_audios.remove(audioView);
	}
	
	public void removeAllAudioView()
	{
		_audios.clear();
	}
	
	public List<MomoTextView> getTexts()
	{
		return _texts;
	}
	
	public List<MomoImageView> getImages()
	{
		return _images;
	}
	
	public List<MomoVideoView> getVideos()
	{
		return _videos;
	}
	
	public List<MomoAudioView> getAudios()
	{
		return _audios;
	}
	
	public String getAlbumPageName()
	{
		return _albumPageName;
	}

	public String getAlbumName()
	{
		return _albumName;
	}

	public String getPageNo()
	{
		return _pageNo;
	}

	public void setAlbumName(String name)
	{
		_albumName = name;
	}

	public void setPageNo(String pageNo)
	{
		_pageNo = pageNo;
	}

	public void setAlbumPageName(String name)
	{
		_albumPageName = name;
	}

}
