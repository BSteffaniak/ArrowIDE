package net.foxycorndog.arrowide.event;

public class DropEvent extends Event
{
	private int				type;
	
	private Object			data;
	
	public static final int	FILES = 1, TEXT = 2;
	
	public DropEvent(int type, Object data)
	{
		this.type = type;
		this.data = data;
	}
	
	public int getType()
	{
		return type;
	}
	
	public Object getData()
	{
		return data;
	}
}