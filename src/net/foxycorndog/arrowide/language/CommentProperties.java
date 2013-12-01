package net.foxycorndog.arrowide.language;

import org.eclipse.swt.graphics.Color;

public class CommentProperties
{
	public final boolean	MULTI_LINE_ENABLED, SINGLE_LINE_ENABLED;
	
	public final Color		COLOR;
	
	public final String		MULTI_LINE_START[], MULTI_LINE_END[], SINGLE_LINE_START[];

	public static final int	SINGLE_LINE = 1, MULTI_LINE = 2;
	
	public CommentProperties()
	{
		this(new String[] {}, new String[] {}, new String[] {}, null);
	}
	
	public CommentProperties(String singleLineStart[], Color color)
	{
		this(singleLineStart, new String[] {}, new String[] {}, color);
	}
	
	public CommentProperties(String multiLineStart[], String multiLineEnd[], Color color)
	{
		this(new String[] {}, multiLineStart, multiLineEnd, color);
	}
	
	public CommentProperties(String singleLineStart, Color color)
	{
		this(new String[] { singleLineStart }, new String[] {}, new String[] {}, color);
	}
	
	public CommentProperties(String multiLineStart, String multiLineEnd, Color color)
	{
		this(new String[] {}, new String[] { multiLineStart }, new String[] { multiLineEnd }, color);
	}
	
	public CommentProperties(String singleLineStart, String multiLineStart, String multiLineEnd, Color color)
	{
		this(new String[] { singleLineStart }, new String[] { multiLineStart }, new String[] { multiLineEnd }, color);
	}
	
	public CommentProperties(String singleLineStart, String multiLineStart[], String multiLineEnd[], Color color)
	{
		this(new String[] { singleLineStart }, multiLineStart, multiLineEnd, color);
	}
	
	public CommentProperties(String singleLineStart, String multiLineStart[], String multiLineEnd, Color color)
	{
		this(new String[] { singleLineStart }, multiLineStart, new String[] { multiLineEnd }, color);
	}
	
	public CommentProperties(String singleLineStart[], String multiLineStart[], String multiLineEnd[], Color color)
	{
		MULTI_LINE_ENABLED		= multiLineStart  != null && multiLineStart.length  > 0 && multiLineEnd.length > 0;
		SINGLE_LINE_ENABLED		= singleLineStart != null && singleLineStart.length > 0;
		
		this.SINGLE_LINE_START	= singleLineStart;
		this.MULTI_LINE_START	= multiLineStart;
		this.MULTI_LINE_END		= multiLineEnd;
		
		this.COLOR				= color;
	}
	
	public int startsComment(String str)
	{
		if (MULTI_LINE_ENABLED)
		{
			for (String com : MULTI_LINE_START)
			{
				if (com.equals(str))
				{
					return MULTI_LINE;
				}
			}
		}
		
		if (SINGLE_LINE_ENABLED)
		{
			for (String com : SINGLE_LINE_START)
			{
				if (com.equals(str))
				{
					return SINGLE_LINE;
				}
			}
		}
		
		return 0;
	}
	
	public int endsComment(String str)
	{
		if (!MULTI_LINE_ENABLED)
		{
			return 0;
		}
		
		for (String com : MULTI_LINE_END)
		{
			if (com.equals(str))
			{
				return MULTI_LINE;
			}
		}
		
		return 0;
	}
	
	public boolean startsToStartComment(String str)
	{
		if (!SINGLE_LINE_ENABLED)
		{
			return false;
		}
		
		for (String com : SINGLE_LINE_START)
		{
			if (com.startsWith(str))
			{
				return true;
			}
		}
		
		if (MULTI_LINE_ENABLED)
		{
			for (String com : MULTI_LINE_START)
			{
				if (com.startsWith(str))
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	public boolean startsToEndComment(String str)
	{
		if (!MULTI_LINE_ENABLED)
		{
			return false;
		}
		
		for (String com : MULTI_LINE_END)
		{
			if (com.startsWith(str))
			{
				return true;
			}
		}
		
		return false;
	}
}