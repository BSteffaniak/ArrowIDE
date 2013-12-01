package net.foxycorndog.arrowide.xml;

import java.util.HashMap;

public class XMLItem
{
	private String 					contents;
	
	private HashMap<String, String>	attributes;
	
	public XMLItem(String contents, HashMap<String, String> attributes)
	{
		this.contents   = contents;
		this.attributes = attributes;
	}
	
	public String getContents()
	{
		return contents;
	}
	
	public HashMap<String, String> getAttributes()
	{
		return attributes;
	}
}