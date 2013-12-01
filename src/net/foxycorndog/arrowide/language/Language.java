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
import net.foxycorndog.arrowide.xml.Reader;
import net.foxycorndog.arrowide.xml.XMLItem;
import static net.foxycorndog.arrowide.ArrowIDE.PROPERTIES;

public class Language
{
	private Image						image;
	
	private HashMap<String, XMLItem[]>	properties;
	
//	public  static final int JAVA = FileUtils.JAVA, GLSL = FileUtils.GLSL, ASSEMBLY = FileUtils.ASSEMBLY, FOXY = FileUtils.FOXY, CPP = FileUtils.CPP, C = FileUtils.C, PHP = FileUtils.PHP, PYTHON = FileUtils.PYTHON;
	
	private static ArrayList<CompilerListener>				listeners;
	
	private static HashMap<Integer, CommentProperties>		commentProperties;
	private static HashMap<Integer, MethodProperties>		methodProperties;
	private static HashMap<Integer, IdentifierProperties>	identifierProperties;
	
	private static HashMap<String, Language>				languages;
	
	public static void init()
	{
		listeners = new ArrayList<CompilerListener>();
		
		commentProperties    = new HashMap<Integer, CommentProperties>();
		methodProperties     = new HashMap<Integer, MethodProperties>();
		identifierProperties = new HashMap<Integer, IdentifierProperties>();
		
		languages            = new HashMap<String, Language>();
		
//		Keyword.addLanguage(JAVA);
//		Keyword.addLanguage(GLSL);
//		Keyword.addLanguage(ASSEMBLY);
//		Keyword.addLanguage(FOXY);
//		Keyword.addLanguage(CPP);
//		Keyword.addLanguage(C);
//		Keyword.addLanguage(PHP);
//		Keyword.addLanguage(PYTHON);
//		
//		JavaLanguage.init();
//		GLSLLanguage.init();
//		AssemblyLanguage.init();
//		FoxyLanguage.init();
//		CppLanguage.init();
//		CLanguage.init();
//		PHPLanguage.init();
//		PythonLanguage.init();
//		
//		commentProperties.put(JAVA, JavaLanguage.COMMENT_PROPERTIES);
//		commentProperties.put(GLSL, GLSLLanguage.COMMENT_PROPERTIES);
//		commentProperties.put(ASSEMBLY, AssemblyLanguage.COMMENT_PROPERTIES);
//		commentProperties.put(FOXY, FoxyLanguage.COMMENT_PROPERTIES);
//		commentProperties.put(CPP, CppLanguage.COMMENT_PROPERTIES);
//		commentProperties.put(C, CppLanguage.COMMENT_PROPERTIES);
//		commentProperties.put(PHP, PHPLanguage.COMMENT_PROPERTIES);
//		commentProperties.put(PYTHON, PythonLanguage.COMMENT_PROPERTIES);
//		
//		methodProperties.put(JAVA, JavaLanguage.METHOD_PROPERTIES);
//		methodProperties.put(GLSL, GLSLLanguage.METHOD_PROPERTIES);
//		methodProperties.put(ASSEMBLY, AssemblyLanguage.METHOD_PROPERTIES);
//		methodProperties.put(FOXY, FoxyLanguage.METHOD_PROPERTIES);
//		methodProperties.put(CPP, CppLanguage.METHOD_PROPERTIES);
//		methodProperties.put(C, CppLanguage.METHOD_PROPERTIES);
//		methodProperties.put(PHP, PHPLanguage.METHOD_PROPERTIES);
//		methodProperties.put(PYTHON, PythonLanguage.METHOD_PROPERTIES);
//		
//		identifierProperties.put(JAVA, JavaLanguage.IDENTIFIER_PROPERTIES);
//		identifierProperties.put(GLSL, GLSLLanguage.IDENTIFIER_PROPERTIES);
//		identifierProperties.put(ASSEMBLY, AssemblyLanguage.IDENTIFIER_PROPERTIES);
//		identifierProperties.put(FOXY, FoxyLanguage.IDENTIFIER_PROPERTIES);
//		identifierProperties.put(CPP, CppLanguage.IDENTIFIER_PROPERTIES);
//		identifierProperties.put(C, CppLanguage.IDENTIFIER_PROPERTIES);
//		identifierProperties.put(PHP, PHPLanguage.IDENTIFIER_PROPERTIES);
//		identifierProperties.put(PYTHON, PythonLanguage.IDENTIFIER_PROPERTIES);
		
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
		properties = Reader.read(xmlLocation);
		
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
	
	public static Program run(String fileLocation, ConsoleStream stream)
	{
		return run(fileLocation, stream, null);
	}
	
	public static Program run(String fileLocation, ConsoleStream stream, ProgramListener listener)
	{
		fileLocation = FileUtils.removeEndingSlashes(fileLocation.replace('\\', '/'));
		
		if (true)
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
	
	public static CommentProperties getCommentProperties(int language)
	{
		CommentProperties properties = null;
		
		if (commentProperties.containsKey(language))
		{
			properties = commentProperties.get(language);
		}
		
		return properties;
	}
	
	public static MethodProperties getMethodProperties(int language)
	{
		MethodProperties properties = null;
		
		if (methodProperties.containsKey(language))
		{
			properties = methodProperties.get(language);
		}
		
		return properties;
	}
	
	public static IdentifierProperties getIdentifierProperties(int language)
	{
		IdentifierProperties properties = null;
		
		if (identifierProperties.containsKey(language))
		{
			properties = identifierProperties.get(language);
		}
		
		return properties;
	}
	
	public void compile(String fileLocation, String code, String outputLocation, PrintStream stream)
	{
		if (fileLocation != null)
		{
			
		}
	}
	
	public static void addCompilerListener(CompilerListener listener)
	{
		listeners.add(listener);
	}
	
	public String getAttribute(String param)
	{
		String params[] = param.split(">");
		
		return properties.get(params[0])[0].getAttributes().get(params[1]);
	}
	
	public String getContents(String param)
	{
		String params[] = param.split(">");
		
		int index = Integer.valueOf(params[1]);
		
		return properties.get(params[0])[index].getContents();
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