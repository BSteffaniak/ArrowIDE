package net.foxycorndog.arrowide.components;

public class ContentEvent
{
	private long   when;
	
	private Object source;
	
	public ContentEvent()
	{
		when = System.currentTimeMillis();
	}
	
	public Object getSource()
	{
		return source;
	}
	
	public void setSource(Object source)
	{
		this.source = source;
	}
	
	public long getWhen()
	{
		return when;
	}
}