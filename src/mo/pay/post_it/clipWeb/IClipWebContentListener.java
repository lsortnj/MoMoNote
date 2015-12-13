package mo.pay.post_it.clipWeb;

import android.graphics.Bitmap;

public interface IClipWebContentListener
{
	public void onBookmarkSelected(String url);
	public void onContentCliped(Bitmap clipBitmap);
}
