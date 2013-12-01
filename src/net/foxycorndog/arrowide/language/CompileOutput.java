package net.foxycorndog.arrowide.language;

public class CompileOutput
{
	private int		startIndex, endIndex;
	private int		lineNumber;
	private int		result;
	
	private String	message;
	
	public CompileOutput(int startIndex, int endIndex, int lineNumber, int result, String message)
	{
		this.startIndex = startIndex;
		this.endIndex   = endIndex;
		this.lineNumber = lineNumber;
		this.result     = result;
		
		this.message    = message;
	}
	
	public int getStartIndex()
	{
		return startIndex;
	}
	
	public int getEndIndex()
	{
		return endIndex;
	}
	
	public int getLineNumber()
	{
		return lineNumber;
	}
	
	public int getResult()
	{
		return result;
	}
	
	public String getMessage()
	{
		return message;
	}
	
	public String toString()
	{
		String str = "";
		
		str += message + ", " + startIndex + "-" + endIndex;
		
		return str;
	}
}