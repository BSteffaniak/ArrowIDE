package net.foxycorndog.arrowide.file;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import static net.foxycorndog.arrowide.ArrowIDE.PROPERTIES;

/**
 * 
 * 
 * @author	Braden Steffaniak
 * @since	Oct 5, 2013 at 4:55:34 PM
 * @since	v0.1
 * @version	Oct 5, 2013 at 4:55:34 PM
 * @version	v0.7.5
 */
public class FileUtils
{
//	private static final HashMap<String, HashSet<String>>	ENDINGS;
	private static final TypeList TYPES;
	
	/**
	 * Class used for storing the information of file types.
	 * 
	 * @author	Braden Steffaniak
	 * @since	Feb 13, 2013 at 10:09:45 PM
	 * @since	v0.7
	 * @version	v0.7
	 */
	private static class Type
	{
		private boolean	language;
		
		private int		type;
		
		private String	ext;
		
		public Type(String ext, int type, boolean language)
		{
			this.ext      = ext;
			this.type     = type;
			this.language = language;
		}
	}
	
	/**
	 * Class that holds several Type instances. Can search through
	 * them too.
	 * 
	 * @author	Braden Steffaniak
	 * @since	Feb 13, 2013 at 10:13:48 PM
	 * @since	v0.7
	 * @version	v0.7
	 */
	private static class TypeList
	{
		private ArrayList<Type> types;
		
		public TypeList()
		{
			types = new ArrayList<Type>();
		}
		
		public void add(Type type)
		{
			types.add(type);
		}
		
		public boolean isLanguage(String ext)
		{
			for (Type type : types)
			{
				if (type.language && type.ext.equals(ext))
				{
					return true;
				}
			}
			
			return false;
		}
		
		public int getType(String ext)
		{
			for (Type type : types)
			{
				if (type.language && type.ext.equals(ext))
				{
					return type.type;
				}
			}
			
			return 0;
		}
		
		public boolean containsExt(String ext)
		{
			for (Type type : types)
			{
				if (type.ext.equals(ext))
				{
					return true;
				}
			}
			
			return false;
		}
	}
	
	static
	{
		TYPES = new TypeList();
		
//		TYPES.add(new Type("java",  JAVA,     true));
//		TYPES.add(new Type("vs",    GLSL,     true));
//		TYPES.add(new Type("vert",  GLSL,     true));
//		TYPES.add(new Type("fs",    GLSL,     true));
//		TYPES.add(new Type("frag",  GLSL,     true));
//		TYPES.add(new Type("shade", GLSL,     true));
//		TYPES.add(new Type("shad",  GLSL,     true));
//		TYPES.add(new Type("sha",   GLSL,     true));
//		TYPES.add(new Type("asm",   ASSEMBLY, true));
//		TYPES.add(new Type("foxy",  FOXY,     true));
//		TYPES.add(new Type("txt",   TXT,      false));
//		TYPES.add(new Type("rtf",   RTF,      false));
//		TYPES.add(new Type("exe",   EXE,      false));
//		TYPES.add(new Type("class", CLASS,    false));
//		TYPES.add(new Type("c",     C,        true));
//		TYPES.add(new Type("cpp",   CPP,      true));
//		TYPES.add(new Type("h",     H,        true));
//		TYPES.add(new Type("php",   PHP,      true));
//		TYPES.add(new Type("php5",  PHP,      true));
//		TYPES.add(new Type("py",    PYTHON,   true));
		
//		ENDINGS = new HashMap<String, HashSet<String>>();
//		
//		ENDINGS.put("glsl.vertex.endings", toHashSet(new String[] { "vs", "vert" }));
//		ENDINGS.put("glsl.fragment.endings", toHashSet(new String[] { "fs", "frag", "shade", "shad", "sha" }));
//		
//		ENDINGS.put("java", toHashSet(new String[] { "java" }));
//		
//		ENDINGS.put("assembly", toHashSet(new String[] { "asm" }));
//		
//		ENDINGS.put("foxy", toHashSet(new String[] { "foxy" }));
//
//		ENDINGS.put("cpp", toHashSet(new String[] { "cpp" }));
//		
//		ENDINGS.put("h", toHashSet(new String[] { "h" }));
//		
//		ENDINGS.put("php", toHashSet(new String[] { "php", "php5" }));
	}
	
	private static <E> HashSet<E> toHashSet(E array[])
	{
		HashSet<E> set = new HashSet<E>();
		
		for (E s : array)
		{
			set.add(s);
		}
		
		return set;
	}
	
	public static boolean contains(String str, String endings[])
	{
		for (String ending : endings)
		{
			if (str.equals(ending))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean delete(File file)
	{
		if (file.isDirectory())
		{
			File subFiles[] = file.listFiles();
			
			for (int i = 0; i < subFiles.length; i ++)
			{
				delete(subFiles[i]);
			}
		}
		
		return file.delete();
	}
	
	public static String getParentFolder(String location)
	{
		location = location.replace('\\', '/');
		location = removeEndingSlashes(location);
		
		int index = location.length() - 1;
		
		while (index >= 0)
		{
			if (location.charAt(index) == '/')
			{
				index--;
				
				break;
			}
			else
			{
				index--;
			}
		}
		
		return removeEndingSlashes(location.substring(0, index + 1));
	}
	
	public static String removeEndingSlashes(String location)
	{
		int lastIndex = location.length() - 1;
		
		while (lastIndex >= 0 && location.charAt(lastIndex) == '/')
		{
			lastIndex--;
		}
		
		return location.substring(0, lastIndex + 1);
	}
	
	public static String getFileName(String location)
	{
		location       = location.replace('\\', '/');
		
		location       = removeEndingSlashes(location);
		
		int firstIndex = location.lastIndexOf('/') + 1;
		
		return location.substring(firstIndex, location.length());
	}
	
	public static String getFileNameWithoutExtension(String location)
	{
		location = getFileName(location);
		
		int lastIndex = location.lastIndexOf('.');
		
		if (lastIndex > 0)
		{
			location = location.substring(0, lastIndex);
		}
		
		return location;
	}
	
	/**
	 * Removes the extension from the file, however keeps the preceding
	 * path.
	 * 
	 * @param location The location of the file.
	 * @return The file location without the extension.
	 */
	public static String removeExtension(String location)
	{
		int lastIndex = location.length();
		
		for (int i = location.length() - 1; i >= 0; i--)
		{
			char c = location.charAt(i);
			
			if (c == '.')
			{
				lastIndex = i;
				break;
			}
			if (c == '/' || c == '\\')
			{
				break;
			}
		}
		
		location = location.substring(0, lastIndex);
		
		return location;
	}
	
	public static String getPrecedingPath(String path, String relativeTo)
	{
		File relativeFile = new File(relativeTo);
		
//		if (relativeFile.exists())
//		{
			if (relativeFile.exists() && !relativeFile.isDirectory())
			{
				relativeTo = getParentFolder(relativeTo);
			}
			
			String folderName = "/" + getFileName(relativeTo) + "/";
			
			int index = path.lastIndexOf(folderName);
			
			index = index < 0 ? 0 : index;
			
			return path.substring(0, index);
//		}
//		else
//		{
//			throw new IllegalArgumentException('"' + relativeTo + '"' + " must be an existing location.");
//		}
	}
	
	public static String getPathRelativeTo(String path, String relativeTo)
	{
		File relativeFile = new File(relativeTo);
		
//		if (relativeFile.exists())
//		{
			if (relativeFile.exists() && !relativeFile.isDirectory())
			{
				relativeTo = getParentFolder(relativeTo);
			}
			
			String folderName = "/" + getFileName(relativeTo) + "/";
			
			int index = path.lastIndexOf(folderName);
			
			return path.substring(index + folderName.length());
//		}
//		else
//		{
//			throw new IllegalArgumentException('"' + relativeTo + '"' + " must be an existing location.");
//		}
	}
	
	/**
	 * Check whether the File at the specified location is of the
	 * type file.
	 * 
	 * @param location The location of the File to check.
	 * @return Whether the File at the location is of the type file.
	 */
	public static boolean isFile(String location)
	{
		File f = new File(location);
		
		return f.isFile();
	}
	
	/**
	 * Check whether the File at the specified location exists.
	 * 
	 * @param location The location of the File to check.
	 * @return Whether the File at the location exists.
	 */
	public static boolean fileExists(String location)
	{
		File f = new File(location);
		
		return f.exists();
	}
	
	public static Font loadMonospacedFont(Display display, String name, String location, int size, int style)
	{
		File file = new File(location);
		
		if (!file.exists())
		{
			throw new IllegalStateException("\"" + file.toString() + "\" does not exist.");
		}
		
		if (!display.loadFont(file.toString()))
		{
			throw new IllegalStateException("\"" + file.toString() + "\" did not load correctly.");
		}
		
		final Font font = new Font(display, name, size, style);
		
		display.addListener(SWT.Dispose, new Listener()
		{
			public void handleEvent(Event event)
			{
				font.dispose();
			}
		});
		
		return font;
	}
	
	public static void writeFile(String location, String text) throws IOException
	{
		File file = new File(location);
		
		PrintWriter writer = new PrintWriter(new FileWriter(file));
		
		writer.print(text);
		
		writer.close();
	}
	
	public static String getAbsolutePath(String location) throws IOException
	{
		File fileLocation = new File(location);
		
		String loc        = fileLocation.getCanonicalPath();
		
		return loc.replace('\\', '/');
	}
	
	private static ArrayList<String> listChildFilesList(String parent, String root, String extension)
	{
		parent = parent.replace('\\', '/');
		parent = removeEndingSlashes(parent) + "/";
		
		root   = root.replace('\\', '/');
		root   = removeEndingSlashes(root) + "/";
		
		ArrayList<String> directories = new ArrayList<String>();
		
		File parentFile = new File(parent);
		
		File children[] = parentFile.listFiles();
		
		if (children != null)
		{
			for (int i = 0; i < children.length; i++)
			{
				if (children[i].isDirectory())
				{
//					File children2[] = children[i].listFiles();
//					
//					boolean able = false;
//					
//					for (int j = 0; j < children2.length; j++)
//					{
//						if (children2[j].getName().endsWith(".java"))
//						{
//							able = true;
//							
//							break;
//						}
//					}
//					
//					if (able)
//					{
//						directories.add(children[i].getAbsolutePath().replace('\\', '/').replace(root, ""));
//					}
					
					directories.addAll(listChildFilesList(children[i].getAbsolutePath(), root, extension));
				}
				else
				{
					if (children[i].getName().endsWith(".java"))
					{
						directories.add(children[i].getAbsolutePath().replace('\\', '/').replace(root, ""));
					}
				}
			}
		}
		
		return directories;
	}
	
	public static String[] listChildFiles(String parent, String extension)
	{
		return listChildFilesList(parent, parent, extension).toArray(new String[0]);
	}
	
	public static String getExecutablePrefix()
	{
		String osName = (String)PROPERTIES.get("os.name");
		
		if (osName.equals("linux"))
		{
			return "sh ";
		}

		return "";
	}
	
	public static String getExecutableExtension()
	{
		String osName = (String)PROPERTIES.get("os.name");
		
		if (osName.equals("windows"))
		{
			return ".exe";
		}
		else if (osName.equals("linux"))
		{
			return ".sh";
		}
		else if (osName.equals("macosx"))
		{
			return ".app";
		}

		return "";
	}
	
	public static String getFileExtension(String file)
	{
		file = file.replace('\\', '/');
		
		int index  = file.lastIndexOf('.');
		int index2 = file.lastIndexOf('/');
		
		if (index < index2 || index <= 0)
		{
			return null;
		}
		
		return file.substring(index);
	}
}