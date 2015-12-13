package mo.pay.post_it.popup;

import android.content.Context;

import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;

import java.util.ArrayList;

import mo.pay.post_it.R;

/**
 * Popup window, shows action list as icon and text like the one in Gallery3D
 * app.
 * 
 * @author Lorensius. W. T
 */
public class QuickAction extends CustomPopupWindow
{
	public static final int	TYPE_DARK	= 0x001;
	public static final int	TYPE_LIGHT	= 0x002;
	
	private final View root;
	private final ImageView mArrowUp;
	private final ImageView mArrowDown;
	private final LayoutInflater inflater;
	private final Context context;

	protected static final int ANIM_GROW_FROM_LEFT = 1;
	protected static final int ANIM_GROW_FROM_RIGHT = 2;
	protected static final int ANIM_GROW_FROM_CENTER = 3;
	public static final int ANIM_REFLECT = 4;
	protected static final int ANIM_AUTO = 5;

	private int animStyle;
	private ViewGroup mTrack;
	private ScrollView scroller;
	private ArrayList<ActionItem> actionList;
	
	private int rootViewWidth = LayoutParams.WRAP_CONTENT;
	private int rootViewHeight = LayoutParams.WRAP_CONTENT;
	
	private int _type = TYPE_LIGHT;

	/**
	 * Constructor
	 * 
	 * @param anchor
	 *            {@link View} on where the popup window should be displayed
	 */
	public QuickAction(View anchor, int type)
	{
		super(anchor);

		_type = type;
		
		actionList = new ArrayList<ActionItem>();
		context = anchor.getContext();
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		root = (ViewGroup) inflater.inflate(_type==TYPE_DARK?R.layout.popup_dark:R.layout.popup_light, null);

		mArrowDown = (ImageView) root.findViewById(R.id.arrow_down);
		mArrowUp = (ImageView) root.findViewById(R.id.arrow_up);

		mArrowDown.setImageResource(_type==TYPE_DARK?R.drawable.arrow_down_dark:R.drawable.arrow_down_light);
		mArrowUp.setImageResource(_type==TYPE_DARK?R.drawable.arrow_up_dark:R.drawable.arrow_up_light);
		 
		setContentView(root);
  
		mTrack = (ViewGroup) root.findViewById(R.id.tracks);
		scroller = (ScrollView) root.findViewById(R.id.scroller);
		animStyle = ANIM_AUTO;
	}

	/**
	 * Set animation style
	 * 
	 * @param animStyle
	 *            animation style, default is set to ANIM_AUTO
	 */
	public void setAnimStyle(int animStyle)
	{
		this.animStyle = animStyle;
	}

	/**
	 * Add action item
	 * 
	 * @param action
	 *            {@link ActionItem} object
	 */
	public void addActionItem(ActionItem action)
	{
		actionList.add(action);
	}

	public void resetActionItem()
	{
		actionList.clear();

		mTrack.removeAllViews();
	}

	public int getActionItemCount()
	{
		return actionList.size();
	}

	/**
	 * Show popup window. Popup is automatically positioned, on top or bottom of
	 * anchor view.
	 * 
	 */
	public void show()
	{
		preShow();

		int xPos, yPos;

		int[] location = new int[2];

		anchor.getLocationOnScreen(location);

		Rect anchorRect = new Rect(location[0], location[1], location[0]
				+ anchor.getWidth(), location[1] + anchor.getHeight());

		mTrack.removeAllViews();

		createActionList();

		root.setLayoutParams(new LayoutParams(rootViewWidth,
				rootViewHeight));
		root.measure(rootViewWidth, rootViewHeight);

		int rootHeight = root.getMeasuredHeight();
		int rootWidth = root.getMeasuredWidth();

		int screenWidth = windowManager.getDefaultDisplay().getWidth();
		int screenHeight = windowManager.getDefaultDisplay().getHeight();

		if(screenHeight-rootHeight <= screenHeight*0.1)
		{
			rootViewHeight = (int) (screenHeight*0.7);
		}
		if(screenWidth-rootWidth <= screenWidth*0.1)
		{
			rootViewWidth = (int) (screenWidth*0.7);
		}
		
		root.setLayoutParams(new LayoutParams(rootViewWidth,
				rootViewHeight));
		root.measure(rootViewWidth, rootViewHeight); 
		
		// automatically get X coord of popup (top left)
		if ((anchorRect.left + rootWidth) > screenWidth)
		{
			xPos = anchorRect.left - (rootWidth - anchor.getWidth());
		} else
		{
			if (anchor.getWidth() > rootWidth)
			{
				xPos = anchorRect.centerX() - (rootWidth / 2);
			} else
			{
				xPos = anchorRect.left;
			}
		}

		int dyTop = anchorRect.top;
		int dyBottom = screenHeight - anchorRect.bottom;

		boolean onTop = (dyTop > dyBottom) ? true : false;

		if (onTop)
		{
			if (rootHeight > dyTop)
			{
				yPos = 15;
				LayoutParams l = scroller.getLayoutParams();
				l.height = dyTop - anchor.getHeight();
			} else
			{
				yPos = anchorRect.top - rootHeight;
			}
		} else
		{
			yPos = anchorRect.bottom;

			if (rootHeight > dyBottom)
			{
				LayoutParams l = scroller.getLayoutParams();
				l.height = dyBottom;
			}
		}

		showArrow(((onTop) ? R.id.arrow_down : R.id.arrow_up),
				anchorRect.centerX() - xPos);

		setAnimationStyle(screenWidth, anchorRect.centerX(), onTop);

		window.showAtLocation(anchor, Gravity.NO_GRAVITY, xPos, yPos);
	}

	/**
	 * Set animation style
	 * 
	 * @param screenWidth
	 *            screen width
	 * @param requestedX
	 *            distance from left edge
	 * @param onTop
	 *            flag to indicate where the popup should be displayed. Set TRUE
	 *            if displayed on top of anchor view and vice versa
	 */
	private void setAnimationStyle(int screenWidth, int requestedX,
			boolean onTop)
	{
		int arrowPos = requestedX - mArrowUp.getMeasuredWidth() / 2;

		switch (animStyle)
		{
		case ANIM_GROW_FROM_LEFT:
			window.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Left
					: R.style.Animations_PopDownMenu_Left);
			break;

		case ANIM_GROW_FROM_RIGHT:
			window.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Right
					: R.style.Animations_PopDownMenu_Right);
			break;

		case ANIM_GROW_FROM_CENTER:
			window.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Center
					: R.style.Animations_PopDownMenu_Center);
			break;

		case ANIM_REFLECT:
			window.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Reflect
					: R.style.Animations_PopDownMenu_Reflect);
			break;

		case ANIM_AUTO:
			if (arrowPos <= screenWidth / 4)
			{
				window.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Left
						: R.style.Animations_PopDownMenu_Left);
			} else if (arrowPos > screenWidth / 4
					&& arrowPos < 3 * (screenWidth / 4))
			{
				window.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Center
						: R.style.Animations_PopDownMenu_Center);
			} else
			{
				window.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Right
						: R.style.Animations_PopDownMenu_Right);
			}

			break;
		}
	}

	/**
	 * Create action list
	 */
	private void createActionList()
	{
		View view;
		String title;
		Drawable icon;
		View  custom_view;
		OnClickListener listener;
		OnLongClickListener longClickListener;

		for (int i = 0; i < actionList.size(); i++)
		{
			title = actionList.get(i).getTitle();
			icon = actionList.get(i).getIcon();
			custom_view = actionList.get(i).getCustomView();
			listener = actionList.get(i).getClickListener();
			longClickListener = actionList.get(i).getLongClickListener();

			view = getActionItem(title, icon, custom_view, listener, longClickListener);

			view.setFocusable(true);
			view.setClickable(true);

			mTrack.addView(view);
		}
	}

	/**
	 * Get action item {@link View}
	 * 
	 * @param title
	 *            action item title
	 * @param icon
	 *            {@link Drawable} action item icon
	 * @param listener
	 *            {@link View.OnClickListener} action item listener
	 * @return action item {@link View}
	 */
	private View getActionItem(String title, Drawable icon, View customView,
			OnClickListener listener, OnLongClickListener longClickListenr)
	{
		LinearLayout container = (LinearLayout) inflater.inflate(_type==TYPE_DARK?R.layout.action_item_dark:R.layout.action_item_light, null);
 
		ImageView 		img	 			= (ImageView) container.findViewById(R.id.icon);
		LinearLayout 	right_side_area = (LinearLayout) container.findViewById(R.id.right_side_area);
		TextView 		text 			= (TextView) container.findViewById(R.id.title);

		if (icon != null)
		{
			img.setImageDrawable(icon);
			img.setVisibility(View.VISIBLE);
		}

		if (title != null)
		{
			text.setText(title);
			text.setTypeface(null, Typeface.BOLD);
			text.setTextSize(TypedValue.COMPLEX_UNIT_PX,context.getResources().getDimensionPixelSize(R.dimen.button_text_size));
			text.setVisibility(View.VISIBLE);
			text.setTextColor(_type==TYPE_DARK?Color.parseColor("#B7D9C9"):Color.parseColor("#837F78"));
		}
		
		if( customView != null)
		{
			right_side_area.addView(customView);
			right_side_area.setVisibility(View.VISIBLE);
		}

		if (listener != null)
		{
			container.setOnClickListener(listener);
		}

		if (longClickListenr != null)
		{
			container.setOnLongClickListener(longClickListenr);
		}

		return container;
	}

	/**
	 * Show arrow
	 * 
	 * @param whichArrow
	 *            arrow type resource id
	 * @param requestedX
	 *            distance from left screen
	 */
	private void showArrow(int whichArrow, int requestedX)
	{
		final View showArrow = (whichArrow == R.id.arrow_up) ? mArrowUp
				: mArrowDown;
		final View hideArrow = (whichArrow == R.id.arrow_up) ? mArrowDown
				: mArrowUp;

		final int arrowWidth = mArrowUp.getMeasuredWidth();

		showArrow.setVisibility(View.VISIBLE);

		ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams) showArrow
				.getLayoutParams();

		param.leftMargin = requestedX - arrowWidth / 2;

		hideArrow.setVisibility(View.INVISIBLE);
	}
}