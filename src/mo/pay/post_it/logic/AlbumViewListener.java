package mo.pay.post_it.logic;

import android.view.View;

public interface AlbumViewListener
{
	public static final int	TO_MODE_EDIT			= 0x1001;
	public static final int	TO_MODE_VIEW			= 0x1002;
	public static final int	ACTION_DELETE			= 0x1003;
	public static final int	CHANGE_SIZE_SMALL		= 0x1004;
	public static final int	CHANGE_SIZE_MEDIUM		= 0x1005;
	public static final int	CHANGE_SIZE_LARGE		= 0x1006;
	public static final int	ON_NOTE_PASTE_CREATED	= 0x1007;
	public static final int	CHANGE_TO_STYLE1		= 0x1008;
	public static final int	CHANGE_TO_STYLE2		= 0x1009;
	public static final int	CHANGE_TO_STYLE3		= 0x1010;
	
	
	public void onNotePasteItemClicked(View view);
	public void onMomoTextViewColorChange(View view);
	public void onMomoTextViewEditText(View view);
	public void onMomoTextViewTextSizeChange(int size,View view);
	public void onViewDelete(View view);
}
