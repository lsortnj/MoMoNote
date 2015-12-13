package mo.pay.post_it.widget;

public class ComponentLocation
{
	private int _x = 0;
	private int _y = 0;
	private int _width = 0;
	private int _height = 0;

	public ComponentLocation(){}
	
	public ComponentLocation(int x, int y, int width, int height)
	{
		_x = x;
		_y = y;
		_width = width;
		_height = height;
	}

	public int getX()
	{
		return _x;
	}

	public int getY()
	{
		return _y;
	}

	public int getWidth()
	{
		return _width;
	}

	public int getHeight()
	{
		return _height;
	}

	public void setX(int x)
	{
		_x = x;
	}

	public void setY(int y)
	{
		_y = y;
	}

	public void setWidth(int width)
	{
		_width = width;
	}

	public void setHeight(int height)
	{
		_height = height;
	}
}
