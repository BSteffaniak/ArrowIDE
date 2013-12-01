package net.foxycorndog.arrowide.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class ConfigReader
{
	public static void read(String location, HashMap<String, String> map)
	{
		BufferedReader reader = null;
		
		try
		{
			reader = new BufferedReader(new FileReader(new File(location)));
		
			String line = null;
			
			for (int lineNum = 0; (line = reader.readLine()) != null; lineNum ++)
			{
				String lineData[] = line.split("=");
				
				String key        = lineData[0];
				String value      = "";
				
				for (int i = 1; i < lineData.length; i ++)
				{
					if (i > 1)
					{
						value += "=";
					}
					
					value += lineData[i];
				}
				
				map.put(key, value);
			}
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}