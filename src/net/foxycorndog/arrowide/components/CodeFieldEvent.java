package net.foxycorndog.arrowide.components;

public class CodeFieldEvent
{
	private char character;
	
	private int  stateMask;
	private int  keyCode;
	
	private long when;
	
	private Object source;
	
	public CodeFieldEvent(char character, int stateMask, int keyCode, Object source)
	{
		this.when      = System.currentTimeMillis();
		
		this.character = character;
		this.stateMask = stateMask;
		this.keyCode   = keyCode;
		this.source    = source;
	}
	
	public char getCharacter()
	{
		return character;
	}
	
	public int getStateMask()
	{
		return stateMask;
	}
	
	public int getKeyCode()
	{
		return keyCode;
	}
	
	public Object getSource()
	{
		return source;
	}
	
	public long getWhen()
	{
		return when;
	}
}