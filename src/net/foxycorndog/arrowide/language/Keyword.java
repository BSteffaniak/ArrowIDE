package net.foxycorndog.arrowide.language;

import java.util.HashMap;

import org.eclipse.swt.graphics.Color;

public class Keyword
{
	private String													word;

	private Color													color;

	private static final HashMap<Integer, HashMap<String, Keyword>> keywords;
	
	static
	{
		keywords = new HashMap<Integer, HashMap<String, Keyword>>();
	}
	
	public static void addLanguage(int language)
	{
		keywords.put(language, new HashMap<String, Keyword>());
	}
	
	public Keyword(int language, String word)
	{
		this.word = word;
		
		keywords.get(language).put(word, this);
	}
	
	public Keyword(int language, String word, Color color)
	{
		this.word = word;
		
		keywords.get(language).put(word, this);
		
		setColor(color);
	}
	
	public String getWord()
	{
		return word;
	}
	
	public Color getColor()
	{
		return color;
	}
	
	public void setColor(Color color)
	{
		this.color = color;
	}
	
	public static boolean isKeyword(int language, String word)
	{
		if (!keywords.containsKey(language))
		{
			return false;
		}
		
		return keywords.get(language).containsKey(word);
	}
	
	public static Keyword getKeyword(int language, String word)
	{
		return keywords.get(language).get(word);
	}
}