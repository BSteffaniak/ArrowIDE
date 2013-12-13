package net.foxycorndog.arrowide.language;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.swt.graphics.Image;

import net.foxycorndog.arrowide.ArrowIDE;
import net.foxycorndog.arrowide.Program;
import net.foxycorndog.arrowide.console.ConsoleStream;
import net.foxycorndog.arrowide.event.CompilerListener;
import net.foxycorndog.arrowide.event.ProgramListener;
import net.foxycorndog.arrowide.file.FileUtils;
import net.foxycorndog.arrowide.xml.XMLReader;
import net.foxycorndog.arrowide.xml.XMLItem;
import static net.foxycorndog.arrowide.ArrowIDE.PROPERTIES;

public class Language
{
	private Image						image;
	
	private String						keywords[][];
	
	private HashMap<String, XMLItem[]>	properties;
	
//	public  static final int JAVA = FileUtils.JAVA, GLSL = FileUtils.GLSL, ASSEMBLY = FileUtils.ASSEMBLY, FOXY = FileUtils.FOXY, CPP = FileUtils.CPP, C = FileUtils.C, PHP = FileUtils.PHP, PYTHON = FileUtils.PYTHON;
	
	private static ArrayList<CompilerListener>	listeners;
	
	private static HashMap<String, Language>	languages;
	
	public static void init()
	{
		listeners = new ArrayList<CompilerListener>();
		
		languages            = new HashMap<String, Language>();
		
		File root = new File((String)PROPERTIES.get("resources.location") + "res/languages");
		
		File children[] = root.listFiles();
		
		if (children != null)
		{
			for (int i = 0; i < children.length; i++)
			{
				File child = children[i];
				
				String location = child.getAbsolutePath();
				
				String ext = FileUtils.getFileExtension(location);
				
				if (child.isFile() && ext != null && ext.equals(".xml"))
				{
					Language language = new Language(location);
					
					String languageName = FileUtils.getFileNameWithoutExtension(location);
					
					languageName = languageName.toLowerCase();
					
					languages.put(languageName, language);
				}
			}
		}
	}
	
	public Language(String xmlLocation)
	{
		properties = XMLReader.read(xmlLocation);
		
		XMLItem items[] = getItems("language.keywords");
		
		keywords = new String[items.length][];

		for (int i = 0; i < items.length; i++)
		{
			String contents = items[i].getContents();
			
			contents = contents.replace("\r", "");
			contents = contents.replace("\n", " ");
			contents = contents.replace("\t", "");
			contents = contents.replace("  ", "");
			contents = contents.replace("   ", "");
			
			String list[] = contents.split(" ");
			
			if (list[0].length() == 0)
			{
				String newList[] = new String[list.length - 1];
				
				System.arraycopy(list, 1, newList, 0, newList.length);
				
				list = newList;
			}
			
//			if (keywords.length == 0)
//			{
				keywords[i] = list;
//			}
//			else
//			{
//				String newKeywords[] = new String[keywords.length + list.length];
//
//				System.arraycopy(keywords, 0, newKeywords, 0, keywords.length);
//				System.arraycopy(list, 0, newKeywords, keywords.length, list.length);
//				
//				keywords = newKeywords;
//			}
		}
		
		String imageName = getAttribute("language>image");
		
		if (imageName != null)
		{
			try
			{
				image = new Image(ArrowIDE.DISPLAY, new FileInputStream(PROPERTIES.get("resources.location") + "res/images/" + imageName));
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public Image getImage()
	{
		return image;
	}
	
	public boolean canRun()
	{
		return Boolean.valueOf(getAttribute("language>executable"));
	}
	
	public Program run(String fileLocation, ConsoleStream stream)
	{
		return run(fileLocation, stream, null);
	}
	
	public Program run(String fileLocation, ConsoleStream stream, ProgramListener listener)
	{
		fileLocation = FileUtils.removeEndingSlashes(fileLocation.replace('\\', '/'));
		
		if (!canRun())
		{
			throw new UnsupportedOperationException("The execution of the specified language is not currently supported by ArrowIDE.");
		}
		
		return null;
	}
	
	public boolean canCompile()
	{
		return Boolean.valueOf(getAttribute("language>compilable"));
	}
	
	public String getCompileError()
	{
		if (!canCompile() && canRun())
		{
			return "The file you are trying to compile is an interpreted language, use the run button instead.";
		}
		
		return "Arrow IDE does not support compiling the specified file type.";
	}
	
	public void compile(String fileLocation, String code, String outputLocation, PrintStream stream)
	{
		if (!canCompile())
		{
			throw new UnsupportedOperationException(getCompileError());
		}
		
		stream.println("Compiled successfully.");
	}
	
	public static void addCompilerListener(CompilerListener listener)
	{
		listeners.add(listener);
	}
	
	public String getAttribute(String param)
	{
		String params[] = param.split(">");

		XMLItem items[] = properties.get(params[0]);
		
		if (items != null)
		{
			int index = 0;
			
			if (params.length > 2)
			{
				index = Integer.valueOf(params[2]);
			}
			
			XMLItem item = items[index];
			
			if (item != null)
			{
				return item.getAttributes().get(params[1]);
			}
		}
		
		return null;
	}
	
	public String[][] getKeywords()
	{
		return keywords;
	}
	
	public String getContents(String param)
	{
		String params[] = param.split(">");
		
		int index = 0;
		
		if (params.length > 1)
		{
			Integer.valueOf(params[1]);
		}
		
		XMLItem items[] = properties.get(params[0]);
		
		if (items != null)
		{
			XMLItem item = items[index];
			
			if (item != null)
			{
				return item.getContents();
			}
		}
		
		return null;
	}
	
	public XMLItem[] getItems(String param)
	{
		XMLItem items[] = properties.get(param);
		
		if (items == null)
		{
			return new XMLItem[0];
		}
		
		return items;
	}
	
	public static Language getLanguageByExtension(String location)
	{
		String extension = FileUtils.getFileExtension(location);
		
		if (extension == null)
		{
			return null;
		}
		
		extension = extension.toLowerCase();
		
		Iterator<String> i = languages.keySet().iterator();
		
		while (i.hasNext())
		{
			String   key      = i.next();
			
			Language language = languages.get(key);
			
			String   contents = language.getContents("language.extensions>0");
			
			contents = contents.replace("\r", "");
			contents = contents.replace("\n", "");
			contents = contents.replace("\t", "");
			
			String extensions[] = contents.split(" ");
			
			for (int f = 0; f < extensions.length; f++)
			{
				if (extension.equals(extensions[f].toLowerCase()))
				{
					return language;
				}
			}
		}
		
		return null;
	}
	
	public static Language getLanguage(String name)
	{
		return languages.get(name.toLowerCase());
	}
}