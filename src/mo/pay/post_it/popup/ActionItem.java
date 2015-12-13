package mo.pay.post_it.popup;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;

/**
 * Action item, displayed as menu with icon and text.
 * 
 * @author Lorensius. W. L. T
 * 
 */
public class ActionItem
{
	private View	custom_view;
	private Drawable icon;
	private String title;
	private OnClickListener listener;
	private OnLongClickListener long_click_listener;

	/**
	 * Constructor
	 */
	public ActionItem()
	{
	}
	
	public void setCustomView(View view)
	{
		custom_view = view;
	}
	
	public View getCustomView()
	{
		return custom_view;
	}

	/**
	 * Constructor
	 * 
	 * @param icon
	 *            {@link Drawable} action icon
	 */
	public ActionItem(Drawable icon)
	{
		this.icon = icon;
	}

	/**
	 * Set action title
	 * 
	 * @param title
	 *            action title
	 */
	public void setTitle(String title)
	{
		this.title = title;
	}

	/**
	 * Get action title
	 * 
	 * @return action title
	 */
	public String getTitle()
	{
		return this.title;
	}

	/**
	 * Set action icon
	 * 
	 * @param icon
	 *            {@link Drawable} action icon
	 */
	public void setIcon(Drawable icon)
	{
		this.icon = icon;
	}

	/**
	 * Get action icon
	 * 
	 * @return {@link Drawable} action icon
	 */
	public Drawable getIcon()
	{
		return this.icon;
	}

	/**
	 * Set on click listener
	 * 
	 * @param listener
	 *            on click listener {@link View.OnClickListener}
	 */
	public void setOnClickListener(OnClickListener listener)
	{
		this.listener = listener;
	}

	/**
	 * Set on long click listener
	 * 
	 * @param listener
	 *            on ling click listener {@link View.OnLongClickListener}
	 */
	public void setOnLongClickListener(OnLongClickListener listener)
	{
		this.long_click_listener = listener;
	}

	/**
	 * Get on click listener
	 * 
	 * @return on click listener {@link View.OnClickListener}
	 */
	public OnClickListener getClickListener()
	{
		return this.listener;
	}

	/**
	 * Get on long click listener
	 * 
	 * @return on long click listener {@link View.OnLongClickListener}
	 */
	public OnLongClickListener getLongClickListener()
	{
		return this.long_click_listener;
	}
}