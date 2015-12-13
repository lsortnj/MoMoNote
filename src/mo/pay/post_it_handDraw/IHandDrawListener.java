package mo.pay.post_it_handDraw;

public interface IHandDrawListener
{ 
	public void onDrawNormalSelected();
	public void onDrawHighlightSelected();
	public void onEraserSelected();
	public void onDone();
	public void onEraserStrokeWidthChange(int strokeWidth);
	public void onDrawNormalStrokeWidthChange(int strokeWidth);
	public void onDrawNormalColorChange(int color);
	public void onDrawHighlightStrokeWidthChange(int strokeWidth);
	public void onDrawHighlightColorChange(int color);
	public void onDrawHighlightAlphaChange(int alpha);
}
