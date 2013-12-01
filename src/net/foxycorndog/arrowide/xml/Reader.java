package net.foxycorndog.arrowide.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class Reader
{
	public static HashMap<String, XMLItem[]> read(String location)
	{
		HashMap<String, XMLItem[]> map = new HashMap<String, XMLItem[]>();
		
		try
		{
			File xmlFile = new File(location);
			InputStream is = new FileInputStream(xmlFile);
			XMLInputFactory factory = XMLInputFactory.newInstance();
			XMLStreamReader reader = factory.createXMLStreamReader(is);
			
			StringBuilder tabs = new StringBuilder();
			
			ArrayList<String> stack = new ArrayList<String>();
			
			String text     = null;
			String lastText = null;
			String name     = null;
			String lastName = null;
			
			HashMap<String, String> attributes = null;
			
			StringBuilder output = new StringBuilder();
			
			while (reader.hasNext())
			{
			    if (reader.hasText())
			    {
			    	text = reader.getText();
			    	
			    	text = formatText(text);
			    	
			    	if (text.length() > 0 && stack.size() > 0)
			    	{
				    	StringBuilder loc = new StringBuilder();
				    	
				    	for (int i = 0; i < stack.size(); i++)
				    	{
				    		loc.append(stack.get(i) + ".");
				    	}
				    		
				    	XMLItem arr[] = null;
				    	
				    	loc.deleteCharAt(loc.length() - 1);
				    	
				    	String key = loc.toString();
				    	
			    		XMLItem item = new XMLItem(text, attributes);
				    	
				    	if (map.containsKey(key))
			    		{
			    			XMLItem prev[] = map.get(key);
			    			
			    			prev[prev.length - 1] = item;
			    			
			    			arr = prev;
			    		}
			    		else
			    		{
			    			arr = new XMLItem[] { item };
			    		}
				    	
				    	map.put(key, arr);
			    	}
			    	
			    	
			    	
			    	lastText = text;
			    	lastName = name;
			    }
			    else if (reader.isStartElement())
			    {
			    	name = reader.getLocalName();
			    	
			    	stack.add(name);
			    	
			    	if (reader.getEventType() == XMLStreamConstants.START_ELEMENT)
			    	{
			    		attributes = new HashMap<String, String>();
			    		
			    		StringBuilder loc = new StringBuilder();
				    	
				    	for (int i = 0; i < stack.size(); i++)
				    	{
				    		loc.append(stack.get(i) + ".");
				    	}
				    	
				    	loc.deleteCharAt(loc.length() - 1);
			    		
				    	for (int i = 0; i < reader.getAttributeCount(); i++)
				    	{
				    		String key   = reader.getAttributeLocalName(i);
				    		String value = reader.getAttributeValue(i);
				    		
				    		attributes.put(key, value);
				    	}
				    	
				    	String key = loc.toString();
				    	
				    	XMLItem arr[] = null;
				    	
			    		XMLItem item = new XMLItem(null, attributes);
				    	
				    	if (map.containsKey(key))
			    		{
			    			XMLItem prev[] = map.get(key);
			    			
			    			arr = new XMLItem[prev.length + 1];
			    			
			    			for (int j = 0; j < prev.length; j++)
			    			{
			    				arr[j] = prev[j];
			    			}
			    			
			    			arr[arr.length - 1] = item;
			    		}
			    		else
			    		{
			    			arr = new XMLItem[] { item };
			    		}
				    		
				    	map.put(key, arr);
			    	}
			    }
			    else if (reader.isEndElement())
			    {
			    	stack.remove(stack.size() - 1);
			    }
			    
			    //TODO: FIX THIS!
			    reader.next();
			}
			
			if (output.length() > 0)
			{
				output.deleteCharAt(0);
			}
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (XMLStreamException e)
		{
//			e.printStackTrace();
		}
		
		return map;
	}
	
	private static char nextChar(String text)
	{
		for (int i = 0; i < text.length(); i++)
		{
			char c = text.charAt(i);
			
			if (c != '\t' && c != ' ' && c != '\n' && c != '\r')
			{
				return c;
			}
		}
		
		return 0;
	}
	
	private static String formatText(String text)
	{
		StringBuilder builder = new StringBuilder(text);
		
		int index = 0;
		
		while (index < builder.length())
		{
			char c = builder.charAt(index);
			
			if (c == '\t' || c == ' ' || c == '\n' || c == '\r')
			{
				builder.deleteCharAt(index);
			}
			else
			{
				return builder.toString();
			}
		}
		
		return builder.toString();
	}
}