package net.foxycorndog.arrowide;

import static net.foxycorndog.arrowide.ArrowIDE.PROPERTIES;

import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.foxycorndog.arrowide.color.ColorUtils;
import net.foxycorndog.arrowide.command.Command;
import net.foxycorndog.arrowide.command.CommandListener;
import net.foxycorndog.arrowide.components.CodeField;
import net.foxycorndog.arrowide.components.CodeFieldEvent;
import net.foxycorndog.arrowide.components.CodeFieldListener;
import net.foxycorndog.arrowide.components.CompositeSizer;
import net.foxycorndog.arrowide.components.ConsoleField;
import net.foxycorndog.arrowide.components.ContentEvent;
import net.foxycorndog.arrowide.components.ContentListener;
import net.foxycorndog.arrowide.components.SizerListener;
import net.foxycorndog.arrowide.components.SplashScreen;
import net.foxycorndog.arrowide.components.TitleBar;
import net.foxycorndog.arrowide.components.menubar.DropdownMenu;
import net.foxycorndog.arrowide.components.menubar.DropdownMenuListener;
import net.foxycorndog.arrowide.components.menubar.Menubar;
import net.foxycorndog.arrowide.components.menubar.MenubarListener;
import net.foxycorndog.arrowide.components.tabmenu.TabMenu;
import net.foxycorndog.arrowide.components.tabmenu.TabMenuEvent;
import net.foxycorndog.arrowide.components.tabmenu.TabMenuListener;
import net.foxycorndog.arrowide.components.toolbar.Toolbar;
import net.foxycorndog.arrowide.components.toolbar.ToolbarListener;
import net.foxycorndog.arrowide.components.treemenu.TreeMenu;
import net.foxycorndog.arrowide.components.treemenu.TreeMenuListener;
import net.foxycorndog.arrowide.components.window.Window;
import net.foxycorndog.arrowide.console.ConsoleListener;
import net.foxycorndog.arrowide.console.ConsoleStream;
import net.foxycorndog.arrowide.dialog.AlertDialog;
import net.foxycorndog.arrowide.dialog.Dialog;
import net.foxycorndog.arrowide.dialog.DialogFilter;
import net.foxycorndog.arrowide.dialog.FileBrowseDialog;
import net.foxycorndog.arrowide.dialog.FileInputDialog;
import net.foxycorndog.arrowide.dialog.OptionDialog;
import net.foxycorndog.arrowide.dialog.PreferencesDialog;
import net.foxycorndog.arrowide.dialog.PropertiesDialog;
import net.foxycorndog.arrowide.dialog.TextInputDialog;
import net.foxycorndog.arrowide.dialog.preferencesdialogpanel.AssemblyPanel;
import net.foxycorndog.arrowide.dialog.preferencesdialogpanel.CppPanel;
import net.foxycorndog.arrowide.dialog.preferencesdialogpanel.GeneralPanel;
import net.foxycorndog.arrowide.dialog.preferencesdialogpanel.JavaPanel;
import net.foxycorndog.arrowide.dialog.preferencesdialogpanel.PythonPanel;
import net.foxycorndog.arrowide.event.CompilerEvent;
import net.foxycorndog.arrowide.event.CompilerListener;
import net.foxycorndog.arrowide.event.DropEvent;
import net.foxycorndog.arrowide.event.DropListener;
import net.foxycorndog.arrowide.event.ProgramListener;
import net.foxycorndog.arrowide.file.ConfigReader;
import net.foxycorndog.arrowide.file.FileStreamReader;
import net.foxycorndog.arrowide.file.FileUtils;
import net.foxycorndog.arrowide.language.CompileOutput;
import net.foxycorndog.arrowide.language.Language;
import net.foxycorndog.arrowide.printer.TextPrinter;
import net.foxycorndog.arrowide.xml.Reader;
import net.foxycorndog.arrowide.xml.XMLItem;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Monitor;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.GLContext;

import sun.org.mozilla.javascript.internal.ast.IdeErrorReporter;

/**
 * Main class for the ArrowIDE program.
 * 
 * @author	Braden Steffaniak
 * @since	Feb 13, 2013 at 4:46:00 PM
 * @since	v0.1.0
 * @version	Feb 13, 2013 at 4:46:00 PM
 * @version	v0.8.0
 */
public class ArrowIDE implements ContentListener, CodeFieldListener, TabMenuListener, ProgramListener, DropListener
{
	private boolean								filesNeedRefresh;
	private boolean								custom;
	private boolean								toggleCodeField, codeFieldToggled;
	
	private int									curId;
	private int									titleBarHeight;
	private int									oldTabId, oldTabId2;
	private int									lastTabRightClicked;
	
	private double								oldCodeFieldHorizontalPercentage, oldCodeFieldVerticalPercentage;

	private CodeField							codeField;

	private ConsoleField						consoleField;

	private String								fileLocation;
	
	private Program								mainProgram;

	private Image								folderImage, fileImage;

	private PropertiesDialog					properties;
	private PreferencesDialog					preferences;

	private Composite							contentPanel;
	
	private Menubar								menubar;

	private TitleBar							titleBar;
	
	private Toolbar								toolbar;

	private TreeMenu							treeMenu;
	
	private CompositeSizer						treeMenuSizer, codeFieldSizer;

	private TabMenu								fileTabs, consoleTabs;
	
	private Menu								treeMenuItemMenu;

	private DropdownMenu						tabMenu;
	
	private ConsoleStream						consoleStream;

	private Dialog								newFolderDialog, newFileDialog, newProjectDialog;
	
	private TextInputDialog						renameFileDialog;

	private HashMap<Integer, String>			treeItemLocations;
	private HashMap<String, Integer>			treeItemIds;
	private HashMap<Integer, String>			treeItemDirectories;
	private HashMap<String, String>				fileCache;
	private HashMap<String, Boolean>			fileCacheSaved;
	private HashMap<Integer, String>			tabFileLocations;
	private HashMap<String, Integer>			tabFileIds;
	private HashMap<Integer, Integer>			tabTopPixels;
	private HashMap<Integer, Point>				tabSelections;
//	private HashMap<, Integer>					consoleTabIds;
	private HashMap<Integer, Program>			consoleTabPrograms;
	
	private ArrayList<Program>					programs;

	private static boolean						restarting;
	private static boolean						exiting;
	private static boolean						libsLoaded;
	private static boolean						rememberSetup;
	
	private static int							untitledNumber;
	
	private static FileStreamReader				fileReader;
	private static Thread						fileReaderThread;
	
	private static File							lockFile;
	
	private static Window						window;
	
	private static SplashScreen					splash;

	private static String						configLocation;
	private static String						dataLocation, resourcesLocation;

	public static final Display					DISPLAY;

	public static final Color					TITLE_BAR_BACKGROUND, TITLE_BAR_FOREGROUND, FOCUS_COLOR, NON_FOCUS_COLOR;
	public static final Color					CONSOLE_RUNNING_COLOR, CONSOLE_TERMINATED_COLOR, CONSOLE_DEFAULT_COLOR;
	
	public static final HashMap<String, String>	CONFIG_DATA;
	
	public static final HashMap<String, HashMap<String, XMLItem[]>>	PROJECT_PROPERTIES;
	public static final HashMap<String, HashMap<String, XMLItem[]>>	PROJECT_CLASSPATHS;
	// public static final HashMap<Integer, String> CONFIG_LINE_NUMBER_DATA;
	// public static final HashMap<String, Integer> CONFIG_LINE_NUMBERS;

	public static final HashMap<String, Object>	PROPERTIES;
	
	private static ArrayList<Thread>			fileViewerThreads;
	private static ArrayList<Thread>			fileViewerThreadsStop;

	public native boolean cpuSupports64();
	
	/**
	 * Instantiate the {@link #DISPLAY display}, and the color palette.
	 */
	static
	{
		Display disp = null;
		
		try
		{
			disp = new Display();
		}
		catch (UnsatisfiedLinkError e)
		{
			System.err.println("You probably are getting this error because " +
					"you have the wrong cpu architecture version downloaded. " +
					"Try again with a different architecture. (ie. If you " +
					"downloaded the x32 arch, download the x64, or vice versa)");
			
			e.printStackTrace();
		}
		
		DISPLAY = disp;
		
		TITLE_BAR_BACKGROUND = new Color(DISPLAY, 240, 240, 240);
		TITLE_BAR_FOREGROUND = TITLE_BAR_BACKGROUND;
		
		FOCUS_COLOR = new Color(DISPLAY, 255, 255, 255);
		NON_FOCUS_COLOR = ColorUtils.lighten(TITLE_BAR_BACKGROUND, 10);

		CONSOLE_DEFAULT_COLOR = new Color(DISPLAY, 255, 255, 255);
		CONSOLE_RUNNING_COLOR = new Color(DISPLAY, 230, 255, 230);
		CONSOLE_TERMINATED_COLOR = new Color(DISPLAY, 255, 230, 230);
	}
	
	/**
	 * Initialize the CONFIG_DATA HashMaps and set the os properties
	 * in the PROPERTIES HashMap.
	 */
	static
	{
		CONFIG_DATA             = new HashMap<String, String>();
		
		PROJECT_PROPERTIES      = new HashMap<String, HashMap<String, XMLItem[]>>();
		PROJECT_CLASSPATHS      = new HashMap<String, HashMap<String, XMLItem[]>>();
//		CONFIG_LINE_NUMBER_DATA = new HashMap<Integer, String>();
//		CONFIG_LINE_NUMBERS     = new HashMap<String, Integer>();
		
		PROPERTIES              = new HashMap<String, Object>();
		
		String osName = System.getProperty("os.name").toLowerCase();
		
		if (osName.contains("mac"))
		{
			PROPERTIES.put("os.name", "macosx");
			PROPERTIES.put("composite.modifiers", SWT.BORDER);
			PROPERTIES.put("key.control", SWT.COMMAND);
			PROPERTIES.put("os.executable.extension", "");
			PROPERTIES.put("colon", ';');
		}
		else if (osName.contains("win"))
		{
			PROPERTIES.put("os.name", "windows");
			PROPERTIES.put("composite.modifiers", SWT.NONE);
			PROPERTIES.put("key.control", SWT.CTRL);
			PROPERTIES.put("os.executable.extension", ".exe");
			PROPERTIES.put("colon", ';');
		}
		else if (osName.contains("lin"))
		{
			PROPERTIES.put("os.name", "linux");
			PROPERTIES.put("composite.modifiers", SWT.NONE);
			PROPERTIES.put("key.control", SWT.CTRL);
			PROPERTIES.put("os.executable.extension", "");
			PROPERTIES.put("colon", ':');
		}
	}
	
	/**
	 * Set up the data location variable.
	 */
	static
	{
		if (PROPERTIES.get("os.name").equals("windows"))
		{
			dataLocation = System.getenv("AppData").replace('\\', '/') + "/.ArrowIDE/";
		}
		else if (PROPERTIES.get("os.name").equals("macosx"))
		{
			dataLocation = System.getProperty("user.home") + "/Library/Application Support/ArrowIDE/";
		}
		else if (PROPERTIES.get("os.name").equals("linux"))
		{
			dataLocation = "/opt/ArrowIDE/";
		}
		
		File f = new File(dataLocation);
		
		if (!f.isDirectory())
		{
			f.mkdirs();
		}
		
		PROPERTIES.put("data.location", dataLocation);
	}
	
	/**
	 * Set up the resources location variable.
	 */
	static
	{
		if (PROPERTIES.get("os.name").equals("windows"))
		{
			String programFiles = System.getenv("ProgramFiles");
			
			resourcesLocation = programFiles + " (x86)";
			
			if (new File(resourcesLocation).isDirectory())
			{
				resourcesLocation += "/ArrowIDE/";
			}
			else
			{
				resourcesLocation = programFiles + "/ArrowIDE/";
			}
			
			if (new File(resourcesLocation).isDirectory() == false)
			{
				resourcesLocation = "";
			}
		}
		else if (PROPERTIES.get("os.name").equals("macosx"))
		{
			resourcesLocation = System.getProperty("user.home") + "/Library/Application Support/ArrowIDE/";
		}
		else if (PROPERTIES.get("os.name").equals("linux"))
		{
//			resourcesLocation = "/opt/ArrowIDE/";
		}
		
		PROPERTIES.put("resources.location", resourcesLocation);
	}
	
    /**
     * Load the necessary libraries needed for finding out the
     * System architecture.
     */
	private static void loadLibs()
	{
		if (libsLoaded)
		{
			return;
		}
		
//		String resLoc = FileUtils.getParentFolder(configLocation);
//		
//		System.setProperty("java.library.path", System.getProperty("java.library.path") + ":" + resLoc + "/res");
		
		String os = (String)PROPERTIES.get("os.name");
		
		if (os.equals("windows"))
		{
//			try
//			{
//				System.load(new File("res/SysArch32.dll").getAbsolutePath());
//			}
//			catch (UnsatisfiedLinkError e)
//			{
//				System.load(new File("res/SysArch.dll").getAbsolutePath());
//			}
		}
		else if (os.equals("macosx"))
		{
//			System.load(new File("res/SysArch.jnilib").getAbsolutePath());
		}
		
		libsLoaded = true;
	}
	
	/**
	 * Set the os.arch value for the {@link #PROPERTIES} variable.
	 */
	private void setArchitecture()
	{
		if (PROPERTIES.containsKey("os.arch"))
		{
			return;
		}
		
		int bitness = 32;
		
		if (PROPERTIES.get("os.name").equals("macosx"))
		{
			bitness = 64;
		}
		else
		{
//			bitness = cpuSupports64() ? 64 : 32;
		}
		
		PROPERTIES.put("os.arch", bitness);
	}
	
	/**
	 * The initial starting point of the program. It is only called
	 * whenever the program is started from scratch without a restart.
	 * 
	 * @param args The command line arguments. (Unused)
	 */
	public static void main(String args[])
	{
		rememberSetup       = true;
		
		boolean newWindow   = false;
		
		ArrayList<String> args2 = new ArrayList<String>();
		
		for (int i = 0; i < args.length; i++)
		{
			if (args[i].toLowerCase().equals("-nw"))
			{
				newWindow = true;
			}
			else if (args[i].toLowerCase().equals("-noremember"))
			{
				rememberSetup = false;
			}
			else
			{
				args2.add(args[i]);
			}
		}
		
		args = args2.toArray(new String[0]);
		
		if (newWindow)
		{
			start(args);
			
			return;
		}
		
		lockFile = new File(dataLocation + "lock");
		
		if (lockFile.isFile())
		{
			try
			{
				PrintWriter p = new PrintWriter(lockFile);
				
				for (int i = 0; i < args.length; i++)
				{
					p.println(args[i]);
				}
				
				p.close();
				
				lockFile.delete();
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
		}
		
		if (!lockFile.isFile())
		{
			try
			{
				lockFile.createNewFile();
				lockFile.deleteOnExit();
			}
			catch (IOException e)
			{
				e.printStackTrace();
				System.exit(1);
			}
			
			start(args);
		}
	}
	
	/**
	 * The constructor for this class. Initializes the window that is
	 * used for programming.
	 * 
	 * @param display The Display instance to use.
	 * @param args The files to load on start up.
	 */
	public ArrowIDE(final Display display, String args[])
	{
		setArchitecture();
		
		final ArrowIDE thisIDE = this;
		
		if (lockFile != null)
		{
			try
			{
				fileReader = new FileStreamReader(lockFile)
				{
					public void dataReceived(final String data)
					{
						display.syncExec(new Runnable()
						{
							public void run()
							{
								try
								{
									openFile(data);
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
						});
					}
				};
			}
			catch (FileNotFoundException e1)
			{
				e1.printStackTrace();
			}
			catch (IOException e1)
			{
				e1.printStackTrace();
			}
		}
		
		fileReaderThread = new Thread(fileReader);
		
		fileReaderThread.start();
		
		if (CONFIG_DATA.containsKey("window.custom"))
		{
			custom = Boolean.valueOf(CONFIG_DATA.get("window.custom"));
		}
		
		Monitor monitor = DISPLAY.getPrimaryMonitor();
		final Rectangle screenBounds = monitor.getBounds();
		
		int height = (int)(monitor.getBounds().height / 1.5f);
		int width  = (int)(monitor.getBounds().width / 1.5f);
		
		oldCodeFieldHorizontalPercentage = -1;
		oldCodeFieldVerticalPercentage   = -1;
		
		window = new Window(DISPLAY, custom);//, SWT.SHELL_TRIM & (~SWT.RESIZE));
		window.setSize(width, height);
		window.addDropListener(this);
		
		final Rectangle shellBounds = window.getBounds();
		 
		window.setLocation(screenBounds.width / 2 - shellBounds.width / 2, screenBounds.height / 2 - shellBounds.height / 2);
		window.setTitle("Arrow IDE");
		
		window.addListener(SWT.Close, new Listener()
		{
			public void handleEvent(Event event)
			{
				if (tabFileIds.containsKey(fileLocation))
				{
					int id = tabFileIds.get(fileLocation);
					
					tabSelections.put(id, codeField.getSelection());
					tabTopPixels.put(id, codeField.getTopPixel());
				}
				
				event.doit = closeTabs();
			}
		});
		
		String currentLocation = new File(resourcesLocation).getAbsolutePath();
		
		PROPERTIES.put("arrowide.location", currentLocation);
		
		/**
		 * Set up the OpenGL (lwjgl) capabilities for the program.
		 */
		{
			System.setProperty("org.lwjgl.librarypath", PROPERTIES.get("arrowide.location") + "/res/native/" + PROPERTIES.get("os.name") + "/");
			
			Composite comp = new Composite(window.getShell(), SWT.NONE);
			comp.setLayout(new FillLayout());
			
			GLData data = new GLData();
			data.doubleBuffer = true;
			final GLCanvas canvas = new GLCanvas(comp, SWT.NONE, data);
			
			canvas.setCurrent();
			
			try
			{
				GLContext.useContext(canvas);
			}
			catch(LWJGLException e)
			{
				e.printStackTrace();
			}
		}
		
		if (CONFIG_DATA.containsKey("monitor.width") && CONFIG_DATA.containsKey("monitor.height"))
		{
			width  = Integer.parseInt(CONFIG_DATA.get("monitor.width"));
			height = Integer.parseInt(CONFIG_DATA.get("monitor.height"));
			
			if (width == screenBounds.width && height == screenBounds.height)
			{
				if (CONFIG_DATA.containsKey("window.width") && CONFIG_DATA.containsKey("window.height"))
				{
					width  = Integer.parseInt(CONFIG_DATA.get("window.width"));
					height = Integer.parseInt(CONFIG_DATA.get("window.height"));
					
					if (width > 0 && height > 0)
					{
						window.setSize(width, height);
					}
				}
				
				if (CONFIG_DATA.containsKey("window.x") && CONFIG_DATA.containsKey("window.y"))
				{
					int x = Integer.parseInt(CONFIG_DATA.get("window.x"));
					int y = Integer.parseInt(CONFIG_DATA.get("window.y"));
					
					window.setLocation(x, y);
				}
			}
			else
			{
				
			}
		}
		
		setConfigDataValue("monitor.width", screenBounds.width + "");
		setConfigDataValue("monitor.height", screenBounds.height + "");
		
		if (CONFIG_DATA.containsKey("window.fullscreen"))
		{
			boolean fullscreen = Boolean.valueOf(CONFIG_DATA.get("window.fullscreen"));
			
			window.setFullscreen(fullscreen);
		}
		
		if (!window.isFullscreen() && CONFIG_DATA.containsKey("window.maximized"))
		{
			boolean maximized = Boolean.valueOf(CONFIG_DATA.get("window.maximized"));
			
			window.setMaximized(maximized);
		}
		
		window.setBackground(new Color(display, 225, 225, 225));
		
		if (custom)
		{
			window.setBorderColor(new Color(display, 215, 215, 215));
			window.setBorderSize(3);
		}
		
		display.addFilter(SWT.KeyDown, new Listener()
		{
			public void handleEvent(Event event)
			{
				if (event.keyCode == SWT.F11)
				{
					window.setFullscreen(!window.isFullscreen());
				}
			}
		});
		
		contentPanel = window.getContentPanel();
		contentPanel.setLocation(0, 0);
		contentPanel.setSize(window.getClientArea().width, window.getClientArea().height);
		contentPanel.setBackground(window.getBackground());
		
		if (custom)
		{
			titleBar = new TitleBar(window, 28, SWT.MIN | SWT.MAX | SWT.CLOSE | SWT.CENTER);
			titleBar.setBackground(TITLE_BAR_BACKGROUND);
			titleBar.setForeground(TITLE_BAR_FOREGROUND);
			
			titleBarHeight = titleBar.getHeight();
		}
		
//		GridLayout b = new GridLayout();
//		b.makeColumnsEqualWidth = false;
//		
//		shell.setLayout(b);
		
		fileCache     = new HashMap<String, String>();
		
		codeField     = new CodeField(contentPanel);
		consoleField  = new ConsoleField(contentPanel);
		
		codeField.addContentListener(this);
		codeField.addCodeFieldListener(this);
		codeField.setBackground(FOCUS_COLOR);
		codeField.setAutoUpdate(true);
		
		int toolbarHeight = 35;
		int contentWidth  = (int)(contentPanel.getSize().x / 100f * 83);
		int conHeight     = (int)((contentPanel.getSize().y) / 100f * 25);
		
		codeField.setSize(contentWidth, contentPanel.getSize().y - conHeight - toolbarHeight - 16);
		codeField.setLocation(contentPanel.getSize().x - codeField.getWidth(), toolbarHeight);//contentPanel.getSize().y - codeField.getHeight());
		codeField.setShowLineNumbers(true);
		
		consoleField.setSize(contentWidth, conHeight);
		consoleField.setLocation(codeField.getBounds().x, codeField.getHeight() + codeField.getBounds().y + 5);
		
		try
		{
			consoleStream = new ConsoleStream(dataLocation + "consoleLog.txt");
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		
		try
		{
			folderImage = new Image(display, new FileInputStream(PROPERTIES.get("resources.location") + "res/images/folderimage.png"));
			fileImage   = new Image(display, new FileInputStream(PROPERTIES.get("resources.location") + "res/images/fileimage.png"));
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		
		tabFileLocations  = new HashMap<Integer, String>();
		tabFileIds        = new HashMap<String, Integer>();
		tabTopPixels      = new HashMap<Integer, Integer>();
		tabSelections     = new HashMap<Integer, Point>();
		
		fileTabs = new TabMenu(contentPanel);
		fileTabs.setBackground(ColorUtils.darken(contentPanel.getBackground(), 20));
		fileTabs.addListener(this);
		
		consoleTabs = new TabMenu(contentPanel);
		consoleTabs.setBackground(ColorUtils.darken(contentPanel.getBackground(), 20));
		consoleTabs.setMaxWidth(codeField.getWidth() + 2);
		consoleTabs.addListener(this);
		
		programs = new ArrayList<Program>();
		consoleTabPrograms = new HashMap<Integer, Program>();
		
	    fileTabs.setMaxWidth(codeField.getWidth() + 2);
		codeField.setLocation(codeField.getX(), codeField.getY() + fileTabs.getHeight());
		fileTabs.setLocation(codeField.getX(), 2);
		
		preferences = new PreferencesDialog(contentPanel);
		preferences.addDialogPanel(new GeneralPanel(preferences.getContentPanel(), this));
		preferences.addDialogPanel(new JavaPanel(preferences.getContentPanel()));
		preferences.addDialogPanel(new CppPanel(preferences.getContentPanel()));
		preferences.addDialogPanel(new AssemblyPanel(preferences.getContentPanel()));
		preferences.addDialogPanel(new PythonPanel(preferences.getContentPanel()));
		
		properties = new PropertiesDialog(contentPanel);
		properties.addDialogPanel(new JavaPanel(properties.getContentPanel()));
		
		menubar = new Menubar(contentPanel);
		menubar.setBackground(TITLE_BAR_BACKGROUND);
		menubar.setSize(contentPanel.getSize().x, 21);
		menubar.setFontSize(12);
		
		menubar.addMenuHeader("File");
		menubar.addMenuSubItem("New", "File");
		menubar.addSeparator("File");
		menubar.addMenuSubItem("Open...", "File");
		menubar.addSeparator("File");
		menubar.addMenuSubItem("Save", "File");
		menubar.addMenuSubItem("Save as...", "File");
		menubar.addSeparator("File");
		menubar.addMenuSubItem("Refresh", "File");
		menubar.addSeparator("File");
		menubar.addMenuSubItem("Print...", "File");
		menubar.addSeparator("File");
		menubar.addMenuSubItem("Restart", "File");
		menubar.addMenuSubItem("Exit", "File");

		menubar.addMenuSubItem("Project...", "File>New");
		menubar.addMenuSubItem("Empty File", "File>New");
		
		menubar.addMenuHeader("Edit");
		menubar.addMenuSubItem("Preferences...", "Edit");

		menubar.addMenuHeader("Project");
		menubar.addMenuSubItem("Properties...", "Project");
		
		menubar.addListener(new MenubarListener()
		{
			public void subItemPressed(String subItemId)
			{
				if (subItemId.equals("File>New>Empty File"))
				{
					newFile();
				}
				else if (subItemId.equals("File>New>Project..."))
				{
					newProject();
				}
				else if (subItemId.equals("File>Open..."))
				{
					try
					{
						openFile();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
				else if (subItemId.equals("File>Save"))
				{
					try
					{
						saveFile(fileLocation);
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
				else if (subItemId.equals("File>Save as..."))
				{
					try
					{
						saveFile(null);
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
				else if (subItemId.equals("File>Refresh"))
				{
					refreshFileViewer(false);
				}
				else if (subItemId.equals("File>Print..."))
				{
					PrintDialog dialog = new PrintDialog(window.getShell(), SWT.NONE);
					dialog.setScope(PrinterData.SELECTION);
					
					PrinterData data = dialog.open();
					
					FontData fd[] = codeField.getFont().getFontData().clone();
					fd[0].setHeight(10);
					
					TextPrinter printer = new TextPrinter(data, codeField.getText(), new Font(display, fd[0]), codeField.getStyles());
					printer.setMargins(1, 1, 1, 1);
					
					if (!printer.print())
					{
						System.err.println("Was not able to print!");
					}
				}
				else if (subItemId.equals("File>Restart"))
				{
					restart();
				}
				else if (subItemId.equals("File>Exit"))
				{
					window.close();
				}
				else if (subItemId.equals("Edit>Preferences..."))
				{
					preferences.open();
				}
				else if (subItemId.equals("Project>Properties..."))
				{
					properties.open();
				}
			}
		});
		
		try
		{
			toolbar       = new Toolbar(contentPanel);
			
			toolbar.setBackground(window.getBackground());

			toolbar.addToolItem("Save", new Image(display, new FileInputStream(PROPERTIES.get("resources.location") + "res/images/savebutton.png")));
			toolbar.addSeparator();
			toolbar.addToolItem("Compile", new Image(display, new FileInputStream(PROPERTIES.get("resources.location") + "res/images/compilebutton.png")));
			toolbar.addSeparator();
			toolbar.addToolItem("Run", new Image(display, new FileInputStream(PROPERTIES.get("resources.location") + "res/images/runbutton.png")));
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		
		toolbar.addListener(new ToolbarListener()
		{
			public void toolItemPressed(String toolItemName)
			{
				if (toolItemName.equals("Save"))
				{
					try
					{
						saveFile(fileLocation);
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
				else if (toolItemName.equals("Compile"))
				{
					consoleField.setText("");
					
					if (fileLocation == null)
					{
						try
						{
							openFile();
						}
						catch (IOException e)
						{
							System.err.println("Compilation failed.");
						}
					}
					else
					{
						Language language = Language.getLanguageByExtension(fileLocation);
						
						try
						{
							saveFile(fileLocation);
						}
						catch (IOException e1)
						{
							e1.printStackTrace();
						}
				
						consoleField.setText("");
						
						String outputLocation = FileUtils.getParentFolder(fileLocation) + "/";
						
						new File(outputLocation).mkdirs();
						
						try
						{
							language.compile(fileLocation, codeField.getRawText(), outputLocation, consoleStream);
						}
						catch (UnsupportedOperationException e)
						{
							consoleStream.println(e.getMessage());
						}
					}
				}
				else if (toolItemName.equals("Run"))
				{
					try
					{
						saveFile(fileLocation);
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
					
					consoleField.setText("");
					
					try
					{
						Language language = codeField.getLanguage();
						
						Program program = language.run(fileLocation, consoleStream, thisIDE);
						programStarted(program);
						
						if (program != null)
						{
							for (int i = programs.size() - 1; i >= 0; i--)
							{
								Program p = programs.get(i);
								
								if (!p.isRunning())
								{
									consoleTabs.closeTab(p.getId());
									
									programs.remove(i);
								}
							}
							
							programs.add(program);
							
							int tabId = consoleTabs.addTab(program.getName());
							
							program.setId(tabId);
							
							consoleTabPrograms.put(tabId, program);
							
							setMainProgram(tabId);
							
							updateLayout();
						}
					}
					catch (UnsupportedOperationException e)
					{
						consoleStream.println(e.getMessage());
					}
				}
			}
		});
		
		Language.init();
		
		Language.addCompilerListener(new CompilerListener()
		{
			public void compiled(CompilerEvent event)
			{
				final CompileOutput outputs[] = event.getOutputs();
				
				final String files[] = event.getFiles();
				
				final PrintStream stream = event.getStream();
				
				final boolean isInViewer = treeItemLocations.containsKey(fileLocation);
				
				
				display.syncExec(new Runnable()
				{
					public void run()
					{
//						if (outputs[0].getResult() == 0)
						{
							// TODO: Update the use of the stream!
							if (stream != null)
							{
								stream.println(outputs[0].getMessage());//"Compiled successfully.");
							}
				
							if (isInViewer)
							{
								for (int i = 0; i < files.length; i ++)
								{
									addToFileViewer(files[i]);
								}
							}
						}
					}
				});
				
				DISPLAY.syncExec(new Runnable()
				{
					public void run()
					{
						for (int i = 0; i < outputs.length; i++)
						{
							if (outputs[i].getResult() != 0)
							{
//								codeField.addError(outputs[i].getStartIndex(), outputs[i].getEndIndex());
//								System.out.print(outputs[i] + "; ");
							}
						}
						
//						System.out.println();
						
//						codeField.highlightSyntax();
					}
				});
			}
		});
		
		fileViewerThreads     = new ArrayList<Thread>();
		fileViewerThreadsStop = new ArrayList<Thread>();
		
		fileCacheSaved        = new HashMap<String, Boolean>();
		treeItemLocations     = new HashMap<Integer, String>();
		treeItemIds           = new HashMap<String, Integer>();
		treeItemDirectories   = new HashMap<Integer, String>();
		
		treeMenu              = new TreeMenu(contentPanel);
		treeMenu.setSize(contentPanel.getSize().x - codeField.getWidth(), codeField.getHeight() + consoleField.getHeight());
		treeMenu.setLocation(0, codeField.getY());
		treeMenu.setBackground(NON_FOCUS_COLOR);
		
		treeMenuSizer = new CompositeSizer(contentPanel, CompositeSizer.VERTICAL);
		treeMenuSizer.setSize(5, treeMenu.getHeight());
		treeMenuSizer.setLocation(treeMenu.getLocation().x + treeMenu.getWidth(), treeMenu.getLocation().y);
		treeMenuSizer.setBackground(new Color(DISPLAY, 100, 100, 100));
		treeMenuSizer.setForeground(new Color(DISPLAY, 230, 230, 230));
		treeMenuSizer.setMinimumX(treeMenu.getLocation().x);
		treeMenuSizer.addSizerListener(new SizerListener()
		{
			public void sizerMoved(int dx, int dy)
			{
				treeMenu.setSize(treeMenu.getWidth() + dx, treeMenu.getHeight());
				codeField.setSize(codeField.getWidth() - dx, codeField.getHeight());
				codeField.setLocation(codeField.getX() + dx, codeField.getY());
				
				consoleField.setSize(codeField.getWidth(), consoleField.getHeight());
				consoleField.setLocation(codeField.getX(), consoleField.getLocation().y);

				codeFieldSizer.setSize(codeField.getWidth(), 5);
				codeFieldSizer.setLocation(codeField.getLocation().x, codeField.getLocation().y + codeField.getHeight());
				codeFieldSizer.setMinimumY(codeField.getY());
				
				toolbar.setLocation(codeField.getX(), toolbar.getY());
				fileTabs.setLocation(codeField.getX(), fileTabs.getY());
				consoleTabs.setLocation(codeField.getX(), consoleTabs.getY());
			}
		});
		
		codeFieldSizer = new CompositeSizer(contentPanel, CompositeSizer.HORIZONTAL);
		codeFieldSizer.setSize(codeField.getWidth(), 3);
		codeFieldSizer.setLocation(codeField.getLocation().x, codeField.getLocation().y + codeField.getHeight() + 1);
		codeFieldSizer.setBackground(new Color(DISPLAY, 100, 100, 100));
		codeFieldSizer.setForeground(new Color(DISPLAY, 230, 230, 230));
		codeFieldSizer.setMinimumY(codeField.getY());
		codeFieldSizer.addSizerListener(new SizerListener()
		{
			public void sizerMoved(int dx, int dy)
			{
				codeField.setSize(codeField.getWidth(), codeField.getHeight() + dy);
				
				consoleField.setSize(codeField.getWidth(), consoleField.getHeight() - dy);
				consoleField.setLocation(codeField.getX(), consoleField.getLocation().y + dy);
				
				consoleTabs.setLocation(codeField.getX(), consoleTabs.getY() + dy);
			}
		});
		
		treeMenuItemMenu = new Menu(treeMenu);
		treeMenu.setMenu(treeMenuItemMenu);
		
		final MenuItem newFolder = new MenuItem(treeMenuItemMenu, SWT.CASCADE);
		newFolder.setText("New Folder...");
		
		final MenuItem newFile = new MenuItem(treeMenuItemMenu, SWT.CASCADE);
		newFile.setText("New File...");
		
		final MenuItem rename = new MenuItem(treeMenuItemMenu, SWT.CASCADE);
		rename.setText("Rename...");
		
		final MenuItem delete = new MenuItem(treeMenuItemMenu, SWT.CASCADE);
		delete.setText("Delete");
		
		SelectionListener menuListener = new SelectionListener()
		{
			public void widgetDefaultSelected(SelectionEvent e)
			{
				if (e.widget == delete)
				{
					int id = treeMenu.getSelection();
//					
//					if (treeItems.containsKey(id))
//					{
//						System.out.println("is file");
//					}
					
					String location = treeItemLocations.get(id);
					
					deleteFile(location);

					removeFromFileViewer(location);
				}
				else if (e.widget == newFolder)
				{
					String preLoc = treeItemLocations.get(treeMenu.getSelection());
					
					if (preLoc == null)
					{
						preLoc = FileUtils.removeEndingSlashes(CONFIG_DATA.get("workspace.location")) + "/";
					}
					
					String preLocation = FileUtils.removeEndingSlashes(preLoc);

					if (FileUtils.isFile(preLocation))
					{
						preLocation = FileUtils.getParentFolder(preLocation);
					}
					
					newFolderDialog = new FileInputDialog("Enter the folder name:", "Folder name:", true, preLocation + "/", false);
					
					String location = newFolderDialog.open();
					
					if (location != null)
					{
						File f = new File(location);
						f.mkdirs();

						addToFileViewer(location);
					}
				}
				else if (e.widget == newFile)
				{
					String preLoc = treeItemLocations.get(treeMenu.getSelection());
					
					if (preLoc == null)
					{
						preLoc = FileUtils.removeEndingSlashes(CONFIG_DATA.get("workspace.location")) + "/";
					}
					
					String preLocation = FileUtils.removeEndingSlashes(preLoc);
					
					if (FileUtils.isFile(preLocation))
					{
						preLocation = FileUtils.getParentFolder(preLocation);
					}
					
					newFileDialog = new FileInputDialog("Enter the file name:", "File name:", false, preLocation + "/", false);
					
					String location = newFileDialog.open();
					
					if (location != null)
					{
						File f = new File(location);
						
						try
						{
							f.createNewFile();
						
							openFile(location);
							
							addToFileViewer(location);
						}
						catch (IOException e2)
						{
							e2.printStackTrace();
						}
					}
				}
				else if (e.widget == rename)
				{
					final int selection		= treeMenu.getSelection();
					
					final String loc		= treeItemLocations.get(selection);
					
					boolean willContinue	= false;
					
					if (fileCache.containsKey(loc))
					{
						if (!fileCacheSaved.get(loc))
						{
							String result = null;
							
							OptionDialog optDialog = new OptionDialog(window, "Save?", "Would you like to save before renaming?");
							
							result = optDialog.open();
							
							if (result != null)
							{
								if (result.equals("yes"))
								{
									try
									{
										saveFile(loc);
									}
									catch (IOException e1)
									{
										e1.printStackTrace();
									}
									
									willContinue	= true;
								}
								else if (result.equals("no"))
								{
									willContinue = true;
								}
							}
						}
						else
						{
							willContinue = true;
						}
					}
					else
					{
						willContinue = true;
					}
					
					if (willContinue)
					{
						renameFileDialog = new TextInputDialog("Enter the new name:", "New name:", FileUtils.getFileName(treeItemLocations.get(selection)));
						
						renameFileDialog.addDialogFilter(new DialogFilter()
						{
							public String filter(String text)
							{
								text = FileUtils.removeEndingSlashes(text.replace('\\', '/'));
								
								for (int i = 0; i < text.length(); i ++)
								{
									if (text.charAt(i) == '/')
									{
										return "The name must be in the same location.";
									}
								}
								
								
								String newLoc		= FileUtils.getParentFolder(loc) + "/" + text;
								
								boolean currentFile	= text.equals(FileUtils.getFileName(loc));
								
								if (currentFile)
								{
									return "The name must be different than the current name.";
								}
								
								removeFromFileViewer(loc);
								
								File f = new File(loc);
								
								boolean successful = f.renameTo(new File(newLoc));
								
								if (successful)
								{
									if (fileCache.containsKey(loc))
									{
										fileCache.put(newLoc, fileCache.remove(loc));
										fileCacheSaved.put(newLoc, fileCacheSaved.remove(loc));
									}
									
									if (tabFileLocations.containsValue(loc))
									{
										int tabId = tabFileIds.remove(loc);
										
										fileTabs.setTabText(tabId, FileUtils.getFileName(newLoc));
										tabFileLocations.put(tabId, newLoc);
										
										tabFileIds.put(newLoc, tabId);
									}
									
									if (tabFileIds.containsKey(loc))
									{
										tabFileIds.put(newLoc, tabFileIds.remove(loc));
									}
									
									if (currentFile)
									{
										fileLocation = newLoc;
										
										codeField.setLanguage(Language.getLanguageByExtension(fileLocation));
										
										codeField.highlightSyntax();
									}
									
									if (loc.equals(fileLocation))
									{
										fileLocation = newLoc;
									}
									
									boolean before = true;
									
									if (fileCacheSaved.containsKey(newLoc))
									{
										before = fileCacheSaved.get(newLoc);
									}

//									refreshFileViewer();
									addToFileViewer(newLoc);
									
									fileCacheSaved.put(newLoc, before);
								}
								else
								{
									addToFileViewer(loc);
								}
								
								return null;
							}
						});
						
						String result = renameFileDialog.open();
						
						if (result != null)
						{
							FileUtils.removeEndingSlashes(result.replace('\\', '/'));
						}
					}
				}
			}

			public void widgetSelected(SelectionEvent e)
			{
				widgetDefaultSelected(e);
			}
		};
		
		newFolder.addSelectionListener(menuListener);
		newFile.addSelectionListener(menuListener);
		rename.addSelectionListener(menuListener);
		delete.addSelectionListener(menuListener);
		
		treeMenuItemMenu.addMenuListener(new MenuAdapter()
	    {
	        public void menuShown(MenuEvent e)
	        {
	        	int selection = treeMenu.getSelection();
	        	
	        	if (selection == -1)
	        	{
	        		newFolder.setEnabled(true);
	        		newFile.setEnabled(true);
	        		rename.setEnabled(false);
	        		delete.setEnabled(false);
	        	}
	        	else
	        	{
	        		String location = treeItemLocations.get(selection);
	        		
	        		boolean isDirectory = new File(location).isDirectory();
	        		
	        		if (isDirectory)
	        		{
	        			newFolder.setEnabled(true);
		        		newFile.setEnabled(true);
		        		rename.setEnabled(true);
		        		delete.setEnabled(true);
	        		}
	        		else
	        		{
	        			newFolder.setEnabled(false);
		        		newFile.setEnabled(false);
		        		rename.setEnabled(true);
		        		delete.setEnabled(true);
	        		}
	        	}
	        }
	    });
		
		treeMenu.addListener(new TreeMenuListener()
		{
			public void treeItemDoubleClicked(int id)
			{
				if (!treeItemDirectories.containsKey(id))
				{
					String location = treeItemLocations.get(id);
					
					try
					{
						openFile(location);
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}

			public void treeItemRightClicked(int id)
			{
				
			}

			public void treeItemSelected(int id)
			{
				
			}
		});
		
		
		consoleStream.addConsoleListener(new ConsoleListener()
		{
			public void onPrintln(final Object o)
			{
				
				if (o instanceof String)
				{
					consoleField.append((String)o);
				}
			}
			
			public void onPrint(Object o)
			{
				if (o instanceof String)
				{
					consoleField.append((String)o);
				}
			}
		});
		
	    ControlListener shellListener = new ControlListener()
		{
			public void controlMoved(ControlEvent e)
			{
				setConfigDataValue("window.x", window.getLocation().x + "");
				setConfigDataValue("window.y", window.getLocation().y + "");
			}

			// TODO clean this crap up.
			public void controlResized(ControlEvent e)
			{
				updateLayout();
				
				if (!window.isMaximized() && !window.isFullscreen())
				{
					setConfigDataValue("window.width", window.getWidth() + "");
					setConfigDataValue("window.height", window.getHeight() + "");
				}
				
				setConfigDataValue("window.maximized", window.isMaximized() + "");
				
				setConfigDataValue("window.fullscreen", window.isFullscreen() + "");
			}
		};
		
		window.addControlListener(shellListener);
		
		shellListener.controlResized(null);
		
		window.addPaintListener(new PaintListener()
		{
			public void paintControl(PaintEvent e)
			{
				e.gc.drawString(codeField.getSelection().x + "", 0, 50);
			}
		});
		
		tabMenu = new DropdownMenu(contentPanel, null);
		tabMenu.addMenuItem("Open in New Window", "Open in New Window");
		tabMenu.addDropdownMenuListener(new DropdownMenuListener()
		{
			@Override
			public void itemUnhovered(String text)
			{
				
			}
			
			@Override
			public void itemSelected(String text)
			{
				if (text.equals("Open in New Window"))
				{
					String location = tabFileLocations.get(lastTabRightClicked);
					System.out.println(location);
					
					String args[] = new String[] { "-nw", "-noRemember", '"' + location + '"' };
					
					try
					{
						openNewIDE(args);
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}
			
			@Override
			public void itemHovered(String text)
			{
				
			}
		});
		
		if (CONFIG_DATA.containsKey("last.tabs") && rememberSetup)
		{
			String lastTabs[] = CONFIG_DATA.get("last.tabs").split(";");
			
			String location   = null;
			
			StringBuilder builder = new StringBuilder();
			
			for (int i = 0; i < lastTabs.length; i++)
			{
				if (lastTabs[i].length() <= 0)
				{
					continue;
				}
				
				String value[] = lastTabs[i].split(" ");
				
				int offset = value.length - 4;
				
				if (value.length > 4)
				{
					StringBuilder locBuilder = new StringBuilder();
					
					for (int j = 0; j < value.length - 3; j++)
					{
						locBuilder.append(value[j] + " ");
					}
					
					locBuilder.deleteCharAt(locBuilder.length() - 1);
					
					location = locBuilder.toString();
				}
				else
				{
					location = value[0];
				}
				
				Point selection = new Point(Integer.valueOf(value[offset + 1]), Integer.valueOf(value[offset + 2]));
				int   topPixel  = Integer.valueOf(value[offset + 3]);
				
				try
				{
					openFile(location, false, i == lastTabs.length - 1, selection, topPixel);
					
//					builder.append(lastTabs[i] + ';');
				}
				catch (IllegalArgumentException e)
				{
					
				}
				catch (IOException e)
				{
					if (e instanceof FileNotFoundException)
					{
						printFileNotFoundError(location);
					}
					else
					{
						e.printStackTrace();
					}
					// If file wasnt found.. or something else.
				}
			}
			
//			setConfigDataValue("last.tabs", builder.toString());
		}
		
		if (CONFIG_DATA.containsKey("last.tab"))
		{
			String lastTab = CONFIG_DATA.get("last.tab");
			
			if (lastTab.length() > 0)
			{
				if (tabFileIds.containsKey(lastTab))
				{
					int id = tabFileIds.get(lastTab);
					
					fileTabs.setSelection(id);
				}
			}
		}
		
		// Load up the files passed through the command-line arguments.
		for (int i = 0; i < args.length; i++)
		{
			String location = args[i];
			
			try
			{
				openFile(location);
			}
			catch (IOException e)
			{
				if (e instanceof FileNotFoundException)
				{
					printFileNotFoundError(location);
				}
				else
				{
					e.printStackTrace();
				}
			}
		}
		
		updateLayout();
	}
	
	/**
	 * Updates the components locations in the frame.
	 */
	private void updateLayout()
	{
		contentPanel.setSize(window.getClientArea().width, window.getClientArea().height);
		
		menubar.setSize(contentPanel.getSize().x, menubar.getHeight());
		menubar.setLocation(0, titleBarHeight);
		
//		codeField.setSize(propCodeWidth, propCodeHeight);
		codeField.setLocation(contentPanel.getSize().x - codeField.getWidth(), fileTabs.getHeight() + fileTabs.getY());
		
		toolbar.setSize(toolbar.getWidth(), 35);
		toolbar.setLocation(codeField.getX(), menubar.getY() + menubar.getHeight());
		
		boolean cTabs = programs.size() > 0;
		int cOffset   = cTabs ? consoleTabs.getHeight() : 0;
		
		consoleField.setSize(codeField.getWidth(), window.getClientArea().height - codeFieldSizer.getHeight() - cOffset - codeField.getHeight() - fileTabs.getHeight() - fileTabs.getY());
		consoleField.setLocation(codeField.getBounds().x, codeField.getHeight() + codeField.getBounds().y + 5 + cOffset);
		
//		tabs.setWidth(codeField.getWidth() + 2);
		fileTabs.setLocation(codeField.getX(), toolbar.getY() + toolbar.getHeight() + 2);
		consoleTabs.setLocation(consoleField.getLocation().x, consoleField.getLocation().y - cOffset);
		
		treeMenu.setLocation(treeMenu.getLocation().x, codeField.getY());
		treeMenu.setSize(contentPanel.getSize().x - codeField.getWidth() - 5, consoleField.getLocation().y + consoleField.getHeight() - codeField.getY());
		
		treeMenuSizer.setSize(5, treeMenu.getHeight());
		treeMenuSizer.setLocation(treeMenu.getLocation().x + treeMenu.getWidth(), treeMenu.getLocation().y);

		codeFieldSizer.setSize(codeField.getWidth(), 5);
		codeFieldSizer.setLocation(codeField.getLocation().x, codeField.getLocation().y + codeField.getHeight());
		codeFieldSizer.setMinimumY(codeField.getY());
	}
	
	/**
	 * The start method that is used to start up the whole ArrowIDE
	 * program. Creates the window and puts the stuff in it.
	 * 
	 * @param args The files to load on start up.
	 */
	public static void start(String args[])
	{
//		splash = new Shell(display, SWT.ON_TOP);
//		splash.setSize(largeIcon.getBounds().width, largeIcon.getBounds().height);
//		splash.setLocation(screenBounds.width / 2 - splash.getSize().x / 2, screenBounds.height / 2 - splash.getSize().y / 2);
//		
//		Label splashImage = new Label(splash, SWT.NONE);
//		splashImage.setSize(splash.getSize());
//		splashImage.setImage(largeIcon);
		
//		splash = new SplashScreen("res/images/iconlarge.png", 3000);
		
//		splash.open(3000);
		
		untitledNumber  = 0;
		
		ArrowIDE ide    = null;

		File configFile = new File(dataLocation + "arrow.config");
		
		if (!configFile.isFile())
		{
			try
			{
				configFile.createNewFile();
			}
			catch (IOException e)
			{
				System.err.println("An error has occurred while starting ArrowIDE. You can try running ArrowIDE as an Administrator. If that fails, reinstall ArrowIDE and try again.");
				
				//e.printStackTrace();
				System.exit(1);
			}
		}
		
		configLocation = configFile.getAbsolutePath().replace('\\', '/');
		
		loadLibs();
		
		createConfigData();
		
		if (!workspaceCreated())
		{
			chooseWorkspace();
		}
			
		ide = openIDE(args);
		
//		System.out.println(OS.SendMessage(shell.handle, OS.EM_SETSEL, 5, 9));//new TCHAR(0, "2dasdf", true)));
		
		window.setFocus();
		window.forceActive();
		window.forceFocus();
		window.setActive();
		
		if (window.isFullscreen())
		{
			window.setFullscreen(false);
			window.setMaximized(true);
			window.setMaximized(false);
			window.setFullscreen(true);
		}
		
		ide.codeField.setFocus();
		
		while (!window.isDisposed() && !exiting)
		{
			if (!DISPLAY.readAndDispatch())
			{
				ide.update();
//				splash.update();
				
				DISPLAY.sleep();
			}
		}
		
		ide.updateTabsConfig();
		
		for (int i = 0; i < ide.programs.size(); i++)
		{
			try
			{
				ide.programs.get(i).terminate();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		
		if (restarting)
		{
			ide.preferences.getWindow().dispose();
			ide.properties.getWindow().dispose();
			
			restarting = false;
			
			start(new String[0]);
		}
		else
		{
			exit(window);
		}
	}
	
	/**
	 * Create a new ArrowIDE and then open it.
	 * 
	 * @param args The files to load on start up.
	 * @return The created ArrowIDE object.
	 */
	public static ArrowIDE openIDE(String args[])
	{
		String location = CONFIG_DATA.get("workspace.location.relative");
		
		try
		{
			location = FileUtils.getAbsolutePath(location);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			exit(window);
		}
		
		CONFIG_DATA.put("workspace.location", location);
		
		ArrowIDE ide = new ArrowIDE(DISPLAY, args);
		
		window.open();
		
		Image icon = null;

		try
		{
			if (PROPERTIES.get("os.name").equals("windows") || PROPERTIES.get("os.name").equals("macosx"))
			{
				icon = new Image(DISPLAY, new FileInputStream(PROPERTIES.get("resources.location") + "res/images/iconlarge.png"));
			}
			else if (PROPERTIES.get("os.name").equals("linux"))
			{
				icon = new Image(DISPLAY, new FileInputStream(PROPERTIES.get("resources.location") + "res/images/iconmedium.png"));
			}
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		
		window.setIcon(icon);
		
		ide.refreshFileViewer(true);
		
		return ide;
	}
	
	/**
	 * Restarts the program to a fresh state.
	 */
	public static void restart()
	{
		restarting = true;
		
		window.dispose();
		
//		start();
//		
//		restarting = false;
	}
	
	/**
	 * The method that is called right before the exiting of the program.
	 * 
	 * @param shell The window to close (The main window).
	 */
	public static void exit(Window shell)
	{
		//System.exit(0);
		
		exiting = true;
		
		if (fileViewerThreads != null)
		{
			for (int i = 0; i < fileViewerThreads.size(); i++)
			{
				try
				{
					fileViewerThreads.get(i).join(1);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}
		
		DISPLAY.close();
		
		if (fileReader != null)
		{
			try
			{
				fileReader.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
			try
			{
				fileReaderThread.join(1);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		
		System.exit(0);
		
//		if (shell != null && !shell.isDisposed())
//		{
//			shell.dispose();
//		}
	}
	
    /**
     * Update the tabs in the configuration file (arrow.config).
     * Save the current tabs for the next session after closing.
     */
	private void updateTabsConfig()
	{
		if (fileLocation != null)
		{
			setConfigDataValue("last.tab", fileLocation + "");
		}
		else
		{
			setConfigDataValue("last.tab", "");
		}
		
		StringBuilder builder = new StringBuilder();
		
		Set<Integer> ids = tabFileLocations.keySet();
		
		Iterator<Integer> i = ids.iterator();
		
		while (i.hasNext())
		{
			int id = i.next();
			
			String location = tabFileLocations.get(id);
			
			String data = location + " " + tabSelections.get(id).x + " " + tabSelections.get(id).y + " " + tabTopPixels.get(id) + ";";
			
			builder.append(data);
		}
		
		setConfigDataValue("last.tabs", builder.toString());
	}
	
	/**
	 * Method used to choose the Workspace location. Sets the value of
	 * the absolute location in the workspace.location key for the {@link #CONFIG_DATA}.
	 */
	public static void chooseWorkspace()
	{
		DialogFilter filter = new DialogFilter()
		{
			public String filter(String text)
			{
				File f = new File(text);
				
				if (!f.exists())
				{
					return "The directory must exist.";
				}
				
				return null;
			}
		};
		
		FileBrowseDialog chooseWorkspace = new FileBrowseDialog("Choose your project workspace folder:", "Workspace:", FileBrowseDialog.DIRECTORY);
		chooseWorkspace.addDialogFilter(filter);
		
		String location = chooseWorkspace.open();
		
		if (location == null)
		{
			exit(window);
		}
		
		setConfigDataValue("workspace.location.relative", location);
		
		try
		{
			setConfigDataValue("workspace.location", FileUtils.getAbsolutePath(location));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns whether a workspace has been located or created.
	 * 
	 * @return Whether the workspace has been located or created.
	 */
	public static boolean workspaceCreated()
	{
		File workspaceDirectory = null;
		
		if (!CONFIG_DATA.containsKey("workspace.location") || new File(CONFIG_DATA.get("workspace.location")).isDirectory() == false)
		{
			chooseWorkspace();
		}
		
		try
		{
			setConfigDataValue("workspace.location", FileUtils.getAbsolutePath(CONFIG_DATA.get("workspace.location.relative")));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		workspaceDirectory = new File(CONFIG_DATA.get("workspace.location"));
		
		return workspaceDirectory.exists();
	}
	
	/**
	 * Method that appends the given String to the value of the key given.
	 * 
	 * @param key	The key to append to.
	 * @param value	The value to append.
	 */
	public static synchronized void appendConfigDataValue(String key, String value)
	{
		String prev = "";
		
		if (CONFIG_DATA.containsKey(key))
		{
			prev = CONFIG_DATA.get(key);
		}
		
		setConfigDataValue(key, prev + value);
	}
	
	/**
	 * Set a CONFIG_DATA value in the HashMap and the arrow.config file.
	 * If the key is not already in the file, it will add it to the end.
	 * 
	 * @param key The key of the property to set.
	 * @param value The value of the property to set.
	 */
	public static synchronized void setConfigDataValue(String key, String value)
	{
		if (!rememberSetup)
		{
			return;
		}
		
		boolean added = false;
		
		CONFIG_DATA.put(key, value);
		
		try
		{
			PrintWriter p = new PrintWriter(new FileWriter(configLocation));
			
			String keys[] = CONFIG_DATA.keySet().toArray(new String[0]);
			
			for (int i = 0; i < CONFIG_DATA.size(); i ++)
			{
				String lineKey   = null;
				String lineValue = null;
				
				lineKey          = keys[i];
				
				if (lineKey.equals(key))
				{
					lineValue = CONFIG_DATA.get(lineKey);
				}
				else
				{
					lineValue = CONFIG_DATA.get(lineKey);
				}
				
				p.print(lineKey + "=" + lineValue + (i == CONFIG_DATA.size() - 1 ? "" : "\r\n"));
			}
		
			p.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Creates and initializes the CONFIG_DATA. Puts all of the correct
	 * values into the HashMap.
	 */
	private static void createConfigData()
	{
		File file = new File(configLocation);
		
		try
		{
			ConfigReader.read(configLocation, CONFIG_DATA);
			
			if (!CONFIG_DATA.containsKey("workspace.location"))
			{
				PrintWriter writer;
				writer = new PrintWriter(new FileWriter(configLocation));
				
				writer.print("workspace.location=");
				
				writer.close();
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
	
	/**
	 * Method that checks to see if the given location is a valid
	 * project. If it is, then load all of the classpath and
	 * project properties into the {@link #PROJECT_PROPERTIES} and
	 * the classpaths into the {@link #PROJECT_CLASSPATHS}.
	 * 
	 * @param location The location of the root folder of the project
	 * 		to check.
	 */
	public static void checkProject(String location)
	{
		location = FileUtils.removeEndingSlashes(location);
		
		String propsLocation = location + "/.properties";
		String cpLocation = location + "/.classpath";
		
		File props = new File(propsLocation);
		File cp = new File(cpLocation);
		
		if (props.isFile())
		{
			if (!PROJECT_PROPERTIES.containsKey(propsLocation))
			{
				HashMap<String, XMLItem[]> map = Reader.read(propsLocation);
				
				PROJECT_PROPERTIES.put(propsLocation, map);
			}
		}
		if (cp.isFile())
		{
			if (!PROJECT_CLASSPATHS.containsKey(cpLocation))
			{
				HashMap<String, XMLItem[]> map = Reader.read(cpLocation);
				
				PROJECT_CLASSPATHS.put(cpLocation, map);
			}
		}
	}
	
	/**
	 * Creates a Dialog to ask for the project name. Next it creates a
	 * directory/folder for the project and refreshes the file viewer.
	 */
	public void newProject()
	{
		newProjectDialog = new FileInputDialog("Enter the name of your project:", "Project name:", "", true, CONFIG_DATA.get("workspace.location"), false);
		
		String location  = newProjectDialog.open();
		
		if (location != null)
		{
			File f = new File(location);
			f.mkdirs();
			
			f = new File(location + "/bin");
			f.mkdirs();
			
			f = new File(location + "/res");
			f.mkdirs();
			
			f = new File(location + "/src");
			f.mkdirs();
			
			try
			{
				f = new File(location + "/.classpath");
				f.createNewFile();
			
				f = new File(location + "/.properties");
				f.createNewFile();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}

			addToFileViewer(location);
			addToFileViewer(location + "/bin");
			addToFileViewer(location + "/res");
			addToFileViewer(location + "/src");
			addToFileViewer(location + "/.classpath");
			addToFileViewer(location + "/.properties");
			refreshFileViewer(location, false);
		}
	}
	
	/**
	 * Creates new file and saves the old one. Switches to the new file
	 * for editing automatically.
	 */
	public void newFile()
	{
		codeField.setText("");
		codeField.setLanguage(null);
		
		String location = "Untitled" + untitledNumber++;
		
		fileLocation = location;
		
		addTab(location, location);
		
		setFileSaved(location, false);
	}
	
	/**
	 * Creates a new FileDialog at the absolute path of the workspace
	 * location.
	 * 
	 * @return The created, unopened FileDialog at the workspace path
	 * 		used for opening files.
	 */
	public FileDialog openFileBrowseDialog()
	{
		String location = null;
		
		if (CONFIG_DATA.containsKey("dialog.location"))
		{
			location = CONFIG_DATA.get("dialog.location");
			
			if (!new File(location).exists())
			{
				location = CONFIG_DATA.get("workspace.location");
			}
		}
		else
		{
			location = CONFIG_DATA.get("workspace.location");
		}
		
		return openFileBrowseDialog(location);
	}
	
	/**
	 * Instantiates a new FileDialog set for all types of files to be
	 * set at the specified location.
	 * 
	 * @param relativeLocation The location for the FileDialog to start.
	 * @return The created, unopened FileDialog used for opening files.
	 */
	public FileDialog openFileBrowseDialog(String location)
	{
		FileDialog dialog = new FileDialog(window.getShell(), SWT.OPEN | SWT.MULTI);
		dialog.setFilterNames(new String[] { "All Files (*)" });
		dialog.setFilterExtensions(new String[] { "*" });
		dialog.setFilterPath(location);
		
		return dialog;
	}
	
	/**
	 * Opens a FileDialog to search for a file to open, then opens
	 * the result.
	 */
	public void openFile() throws IOException
	{
		FileDialog dialog = openFileBrowseDialog();
		
		String location   = dialog.open();
		
		if (location == null)
		{
			return;
		}
		
		location           = location.replace('\\', '/');
		
		String parent      = FileUtils.getParentFolder(location) + "/";
		
		String fileNames[] = dialog.getFileNames();
		
		for (int i = 0; i < fileNames.length; i++)
		{
			openFile(parent + fileNames[i]);
		}
		
		setConfigDataValue("dialog.location", parent);
	}
	
	/**
	 * Open a file located at the specified location.
	 * 
	 * @param location The location of the file to open.
	 */
	public void openFile(String location) throws FileNotFoundException, IOException
	{
		openFile(location, true, true);
	}
	
	/**
	 * Method that opens a file at the specified location and also
	 * takes the option whether or not to cache the save the fileLocation
	 * in the {@link #CONFIG_DATA} for reuse when the IDE is restarted.
	 * 
	 * @param	location The location of the file to open.
	 * @param	cache Whether or not to save the file for use after restart.
	 * @param 	setLanguage Whether or not to set the language of the codeField.
	 * @throws	FileNotFoundException Thrown if the file can not be found.
	 * @throws	IOException Thrown if there was trouble reading or writing.
	 */
	public void openFile(String location, boolean cache, boolean setLanguage) throws FileNotFoundException, IOException
	{
		openFile(location, cache, setLanguage, new Point(0, 0), 0);
	}

	/**
	 * Method that opens a file at the specified location and also
	 * takes the option whether or not to cache the save the fileLocation
	 * in the {@link #CONFIG_DATA} for reuse when the IDE is restarted.
	 * 
	 * @param location The location of the file to open.
	 * @param cache Whether or not to save the file for use after restart.
	 * @param setLanguage Whether or not to set the language of the codeField.
	 * @throws FileNotFoundException Thrown if the file can not be found.
	 * @throws IOException Thrown if there was trouble reading or writing.
	 */
	public void openFile(String location, boolean cache, boolean setLanguage, Point selection, int topPixel) throws FileNotFoundException, IOException, IllegalArgumentException
	{
		openFile(location, cache, setLanguage, selection, topPixel, false);
	}
	
	/**
	 * Method that opens a file at the specified location and also
	 * takes the option whether or not to cache the save the fileLocation
	 * in the {@link #CONFIG_DATA} for reuse when the IDE is restarted.
	 * 
	 * @param location The location of the file to open.
	 * @param cache Whether or not to save the file for use after restart.
	 * @param setLanguage Whether or not to set the language of the codeField.
	 * @param reOpen Whether or not t force reopening and refreshing the file.
	 * @throws FileNotFoundException Thrown if the file can not be found.
	 * @throws IOException Thrown if there was trouble reading or writing.
	 */
	public void openFile(String location, boolean cache, boolean setLanguage, Point selection, int topPixel, boolean reOpen) throws FileNotFoundException, IOException, IllegalArgumentException
	{
		location = location.replace('\\', '/');
		
		boolean alreadyOpen = fileCache.containsKey(location);
		
		if (oldTabId != 0)
		{
			tabTopPixels.put(oldTabId, codeField.getTopPixel());
			tabSelections.put(oldTabId, codeField.getSelection());
		}
		
		if (alreadyOpen && !reOpen)
		{
			codeField.setText(fileCache.get(location), true, true);
			
			int tabId = tabFileIds.get(location);
			
			String oldLocation = fileLocation;
			
			fileLocation = location;
			
			fileTabs.setSelection(tabId);
			
			if (!location.equals(oldLocation))
			{
				oldTabId = tabId;
			}
		}
		else
		{
			File file = new File(location);
			
			if (file.isDirectory())
			{
				return;
			}
			else if (!file.isFile())
			{
				throw new FileNotFoundException();
			}
			else if (!file.canRead())
			{
				System.err.println("The specified file at '" + location + "' cannot be read.");
				
				AlertDialog dialog = new AlertDialog(window, "Read-only", "The file \"" + FileUtils.getFileName(location) + "\" cannot be read.");
				
				dialog.open();
				
				return;
			}
			
			BufferedReader reader = new BufferedReader(new FileReader(file));
			
			StringBuilder builder = new StringBuilder();
			
			String line = "";
			
			while ((line = reader.readLine()) != null)
			{
				builder.append(line + "\r\n");
			}
			
			reader.close();
			
			if (builder.length() > 0)
			{
				builder.deleteCharAt(builder.length() - 1);
				builder.deleteCharAt(builder.length() - 1);
			}
			
			String fileContents = builder.toString();
			
			fileCache.put(location, fileContents);
			fileCacheSaved.put(location, true);
			
			codeField.setText(fileContents, true);
			
			if (!location.equals(fileLocation))
			{
				addTab(location, selection, topPixel, cache);
				
				if (fileLocation != null)
				{
					int tabId = tabFileIds.get(location);
					
					oldTabId = tabId;
				}
			}
			
			fileLocation = location;
			
			if (!file.canWrite())
			{
				AlertDialog dialog = new AlertDialog(window, "Read-only", "The file \"" + FileUtils.getFileName(location) + "\" is read-only. You cannot save directly to this file.");
				
				dialog.open();
			}
		}
		
		if (setLanguage)
		{
			codeField.setLanguage(Language.getLanguageByExtension(location));
		}
		else
		{
			codeField.highlightSyntax();
		}
		
		IllegalArgumentException e1 = null, e2 = null;
		
		try 
		{
			codeField.setSelection(selection);
		}
		catch (IllegalArgumentException e)
		{
			e1 = e;
		}
		
		try 
		{
			codeField.setTopPixel(topPixel);
		}
		catch (IllegalArgumentException e)
		{
			e2 = e;
		}
		
		
		codeField.setFocus();
		codeField.redraw();
		
		if (e1 != null)
		{
			throw e1;
		}
		else if (e2 != null)
		{
			throw e2;
		}
	}
	
	/**
	 * Creates a new FileDialog at the absolute path of the workspace
	 * location.
	 * 
	 * @return The created, unopened FileDialog at the workspace path
	 * 		used for saving.
	 */
	public FileDialog openSaveDialog()
	{
		String location = null;
		
		if (CONFIG_DATA.containsKey("dialog.location"))
		{
			location = CONFIG_DATA.get("dialog.location");
			
			if (!new File(location).exists())
			{
				location = CONFIG_DATA.get("workspace.location");
			}
		}
		else
		{
			location = CONFIG_DATA.get("workspace.location");
		}
		
		return openSaveDialog(location);
	}
	
	/**
	 * Instantiates a new FileDialog set for all types of files to be
	 * set at the specified location.
	 * 
	 * @param relativeLocation The location for the FileDialog to start.
	 * @return The created, unopened FileDialog used for saving.
	 */
	public FileDialog openSaveDialog(String location)
	{
		File file = new File(location);
		
		if (!file.isDirectory())
		{
			location = FileUtils.getParentFolder(file.getAbsolutePath());
		}
		
		FileDialog dialog = new FileDialog(window.getShell(), SWT.SAVE);
		dialog.setFilterNames(new String[] { "All Files (*)" });
		dialog.setFilterExtensions(new String[] { "*" });
		dialog.setFilterPath(location);
		
		return dialog;
	}
	
	/**
	 * Check if the file at the specified location exists and if the
	 * user would like to overwrite it.
	 * 
	 * @param location The location of the file to check.
	 * @return True if the file exists and the user doesn't want to
	 * 		overwrite it.
	 */
	private boolean checkOverwrite(String location)
	{
		if (!fileLocation.equals(location))
		{
			File f = new File(location);
			
			if (f.isFile())
			{
				OptionDialog saveDialog = new OptionDialog(window, "Overwrite?", "\"" + FileUtils.getFileName(location) + "\" already exists. Would you like to overwrite it?");
				
				String result = saveDialog.open();
				
				if (!result.equals("yes"))
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Saves a file located at the specified location.
	 * 
	 * @param location The location of the file to open.
	 * @throws IOException 
	 */
	public void saveFile(String location) throws IOException
	{
		if (checkOverwrite(location))
		{
			return;
		}
		
		// If no files are open.
		if (fileLocation == null && location == null)
		{
			try
			{
				openFile();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
			return;
		}
		
		if (location != null)
		{
			location = location.replace('\\', '/');
		}
		
		String  oldLoc   = null;
		boolean closeOld = false;
		
		if (location == null || location.startsWith("Untitled"))
		{
			boolean hasTab = tabFileIds.containsKey(fileLocation);
			int     tabId  = 0;
			
			if (hasTab)
			{
				tabId = tabFileIds.get(fileLocation);
			}
			
			oldLoc = location;
			
			FileDialog dialog = null;
			
			if (fileLocation != null)
			{
				dialog = openSaveDialog(fileLocation);
			}
			else
			{
				dialog = openSaveDialog();
			}
			
			if (fileLocation != null)
			{
				String filename = FileUtils.getFileName(fileLocation);
				
				dialog.setFileName(filename);
				
				oldLoc = fileLocation;
			}
			
			location = dialog.open();
			
			if (location != null)
			{
				location = location.replace('\\', '/');
				
				if (checkOverwrite(location))
				{
					return;
				}
				
				String tabLocation = getTab(location);
				
				if (tabLocation != null)
				{
					OptionDialog saveDialog = new OptionDialog(window, "Save?", "The file " + '"' + FileUtils.getFileName(location) + '"' + " is already open. Would you like to overwrite it?" );
					
					String result = saveDialog.open();
					
					if (result.equals("yes"))
					{
						closeOld = true;
					}
					else
					{
						return;
					}
				}
				else
				{
					if (oldLoc.equals(location))
					{
						saveFile(location);
						
						return;
					}
					else
					{
						if (hasTab)
						{
							String fileName = FileUtils.getFileName(location);
							
							tabFileIds.remove(oldLoc);
							
							fileTabs.setTabText(tabId, fileName);
							
							tabFileIds.put(location, tabId);
							tabFileLocations.put(tabId, location);
							
							fileCache.remove(oldLoc);
							fileCacheSaved.remove(oldLoc);
							
							fileCache.put(location, codeField.getWritableText());
							fileCacheSaved.put(location, true);
							
							String lastTabs = CONFIG_DATA.get("last.tabs");
							
							String files = lastTabs.replace(oldLoc, location);
							setConfigDataValue("last.tabs", files);
						}
					}
				}
			}
		}
		
		String oldLoc2 = fileLocation;
		
		if (location == null)
		{
			return;
		}
		
		if (fileLocation == null)
		{
			fileLocation = "";
		}
		
		boolean currentFile = location.equals(fileLocation);
		
		File f = new File(location);
		
		if (!f.isFile())
		{
			f.createNewFile();
		}
		
		boolean cantWrite = !f.canWrite();
		
		if (f.canWrite())
		{
			try
			{
				FileUtils.writeFile(location, codeField.getWritableText());
			}
			catch (FileNotFoundException e)
			{
				cantWrite = true;
			}
		}
		
		if (cantWrite)
		{
			System.err.println("The specified file at '" + location + "' is read-only.");
			
			OptionDialog saveDialog = new OptionDialog(window, "Read-only", "\"" + FileUtils.getFileName(location) + "\" is read-only. Would you like to save it elsewhere instead?");
			
			String result = saveDialog.open();
			
			if (result.equals("yes"))
			{
				saveFile(null);
			}
			
			return;
		}
		
		fileLocation = location;
		
		codeField.setLanguage(Language.getLanguageByExtension(location));
		
		boolean highlight = codeField.getLanguage() == null;
		
		//TODO: did if rog et something? <-- Wtf? <-- It was "Did I forget something?" <-- Oohhh, Thanks self.
		if (highlight)
		{
			codeField.highlightSyntax();
		}
		
		setFileSaved(location, true);
		
		boolean isInViewer = treeItemLocations.containsKey(location);
		
//		if (!isInViewer)
//		{
//			addToFileViewer(location);
//		}
		
		if (closeOld)
		{
			int id       = tabFileIds.get(oldLoc);
			int topPixel = 0;
			
			Point selection = null;
			
			if (oldLoc2.equals(oldLoc))
			{
				topPixel  = codeField.getTopPixel();
				selection = codeField.getSelection();
			}
			else
			{
				topPixel  = tabTopPixels.get(id);
				selection = tabSelections.get(id);
			}
			
			closeTab(oldLoc, true, fileTabs);
			openFile(location, true, true, selection, topPixel, true);
		}
	}
	
	private boolean closeTabs()
	{
		Iterator<String> i = tabFileIds.keySet().iterator();
		
		ArrayList<String> files = new ArrayList<String>();
		
		while (i.hasNext())
		{
			String key = i.next();
			
			if (fileCacheSaved.containsKey(key))
			{
				if (!fileCacheSaved.get(key))
				{
					int tabId = tabFileIds.get(key);
					
					fileTabs.setSelection(tabId);
					
					OptionDialog saveDialog = new OptionDialog(window, "Save?", "\"" + FileUtils.getFileName(key) + "\" has not been saved, would you like to save it?");
					
					String result = saveDialog.open();
					
					if (result.equals("cancel"))
					{
						return false;
					}
					else if (result.equals("yes"))
					{
						try
						{
							saveFile(key);
						}
						catch (IOException e)
						{
							e.printStackTrace();
						}
					}
					else if (result.equals("no"))
					{
						
					}
				}
			}
		}
		
		return true;
	}
	
	private boolean closeTab(String tabLocation, boolean trigger, TabMenu source)
	{
		if (trigger)
		{
			fileTabs.closeTab(tabFileIds.get(tabLocation));
		}
		else
		{
			int     tabId  = tabFileIds.get(tabLocation);
			
			boolean cancel = false;
			
			if (source == fileTabs)
			{
				if (tabId == oldTabId2)
				{
					toggleCodeField = false;
				}
				
				oldTabId2 = 0;
				
				int newId		= fileTabs.getSelection();
				
				String location = tabFileLocations.get(tabId);
				String result	= null;
		
				boolean askSave	= false;
				
				if (fileCacheSaved.containsKey(location))
				{
					if (!fileCacheSaved.get(location))
					{
						askSave = true;
					}
				}
				else
				{
					if (!isCodeFieldEmpty())
					{
						askSave = true;
					}
				}
				
				if (askSave)
				{
					OptionDialog saveDialog = new OptionDialog(window, "Save?", "\"" + FileUtils.getFileName(location) + "\" has not been saved, would you like to save it?");
					
					result = saveDialog.open();
				}
				
				if (result != null)
				{
					if (result.equals("yes"))
					{
						try
						{
							saveFile(location);
						}
						catch (IOException e)
						{
							e.printStackTrace();
						}
					}
					else if (result.equals("no"))
					{
						setFileSaved(location, true);
					}
					else
					{
						cancel = true;
					}
				}
				else
				{
					cancel = askSave;
				}
			}
			else if (source == consoleTabs)
			{
				Program program = consoleTabPrograms.get(tabId);
				
				if (program.isRunning())
				{
					program.getProcess().destroy();
				}
				
				removeProgram(program);
				
				resetMainProgram(tabId);
			}
		
			return !cancel;
		}
		
		return false;
	}
	
	private boolean isTab(String location) throws IOException
	{
		return getTab(location) != null;
	}
	
	private String getTab(String location) throws IOException
	{
		Collection<String> locations = tabFileLocations.values();
		
		Iterator<String> i = locations.iterator();
		
		File f = new File(location);
		
		while (i.hasNext())
		{
			String loc = i.next();
			
			File f2 = new File(loc);
			
			if (f.getCanonicalPath().equals(f2.getCanonicalPath()))
			{
				return loc;
			}
		}
		
		return null;
	}
	
	/**
	 * Method that adds the file at the specified location to file viewer.
	 * Faster than refreshing the whole doggone file viewer.
	 * 
	 * @param location The location of the file to add.
	 */
	public void addToFileViewer(String location)
	{
		if (treeItemLocations.containsValue(location))
		{
			return;
		}
		
		location.replace('\\', '/');
		
		String workspace = CONFIG_DATA.get("workspace.location");
		
		workspace.replace('\\', '/');
		
		if (!location.toLowerCase().contains(workspace.toLowerCase()))
		{
			return;
		}
		
		File file     = new File(location);

		boolean isDirectory = file.isDirectory();
		
		String name   = FileUtils.getFileName(location);
		
		Image img     = isDirectory ? folderImage : getFileImage(location);
		
		String parentLocation = FileUtils.getParentFolder(location);
		
		int parentId = 0;
		
		if (parentLocation.equals(CONFIG_DATA.get("workspace.location")))
		{
			
		}
		else
		{
			parentId = treeItemIds.get(parentLocation);
		}
		
		int id = treeMenu.addItem(parentId, name, img);
		
		if (fileCacheSaved.containsKey(location))
		{
			fileCacheSaved.put(location, true);
		}

		treeItemLocations.put(id, location);
		treeItemIds.put(location, id);
	}
	
	/**
	 * Method to remove the file at the specified location from the file
	 * viewer. Faster than refreshing the whole doggone file viewer.
	 * 
	 * @param location The location of the file to remove.
	 */
	public void removeFromFileViewer(String location)
	{
		int id = treeItemIds.get(location);
		
		treeItemDirectories.remove(id);
		treeItemIds.remove(location);
		treeItemLocations.remove(id);
		
		if (treeMenu.containsItem(id))
		{
			treeMenu.removeItem(id);
		}
	}
	
	/**
	 * Refresh the file viewer to all of the updated file names.
	 * If a file has been added, add it to the
	 * {@link #treeMenu file viewer}.
	 * 
	 * @param ignoreRemove Whether or not to ignore the process of
	 * 		searching for files that were removed.
	 */
	public void refreshFileViewer(boolean ignoreRemove)
	{
		refreshFileViewer(CONFIG_DATA.get("workspace.location"), 0, ignoreRemove);
	}
	
	/**
	 * Method to refresh the files within the directory of the specified
	 * location.
	 * 
	 * @param location The location of the root directory to refresh.
	 * @param ignoreRemove Whether or not to ignore the process of
	 * 		searching for files that were removed.
	 */
	public void refreshFileViewer(String location, boolean ignoreRemove)
	{
		int parentId = treeItemIds.get(location);
		
		refreshFileViewer(location, parentId, ignoreRemove);
	}

	/**
	 * Method to refresh the files within the directory of the specified
	 * location.
	 * 
	 * @param location The location of the root directory to refresh.
	 * @param parentId The id of the TreeMenu item to refresh.
	 * @param ignoreRemove Whether or not to ignore the process of
	 * 		searching for files that were removed.
	 */
	public void refreshFileViewer(final String location, final int parentId, final boolean ignoreRemove)
	{
		Thread refreshThread = new Thread()
		{
			public void run()
			{
				File parent  = new File(location);
				
				try
				{
					findSubFiles(parent, parentId, true);
				}
				catch (IOException e)
				{
					e.printStackTrace();
					
					if (!exiting)
					{
						exit(window);
					}
				}
				
				if (exiting)
				{
					return;
				}
				
				String locations[] = treeItemLocations.values().toArray(new String[0]);
				
				if (!ignoreRemove)
				{
					for (int i = 0; i < locations.length; i ++)
					{
						File file = new File(locations[i]);
						
						if (!file.exists())
						{
							final int id = treeItemIds.get(locations[i]);
							
							treeItemIds.remove(locations[i]);
							treeItemLocations.remove(id);
							
							if (treeMenu.containsItem(id))
							{
								DISPLAY.syncExec(new Runnable()
								{
									public void run()
									{
										treeMenu.removeItem(id);
									}
								});
							}
						}
					}
				}
				
				treeMenu.alphabetize();
				
				fileViewerThreads.remove(this);
			}
		};
		
		fileViewerThreads.add(refreshThread);
		
		refreshThread.start();
	}
	
	/**
	 * Finds the sub-files of a directory and if they have not been
	 * added, add them to the TreeMenu.
	 * 
	 * @param file The directory to search sub-files for.
	 * @param parent The id of the directory TreeMenu item.
	 * @throws IOException 
	 */
	private void findSubFiles(File file, final int parent, boolean ignoreRemove) throws IOException
	{
		if (exiting)
		{
			return;
		}
		
		File subFiles[] = file.listFiles();
		
		if (subFiles != null)
		{
			for (int i = 0; i < subFiles.length; i ++)
			{
				if (exiting)
				{
					return;
				}
				
				boolean isDirectory = subFiles[i].isDirectory();
				
				final String orig   = subFiles[i].getCanonicalPath().replace('\\', '/');
				final String name   = FileUtils.getFileName(orig);
				
				if (name.charAt(0) == '.')
				{
					continue;
				}
				
//				String location      = orig;//orig.toLowerCase();
				
				int id               = 0;
				
				final Image img      = isDirectory ? folderImage : getFileImage(orig);
				
				boolean alreadyAdded = treeItemIds.containsKey(orig);
				
				if (!ignoreRemove)
				{
					alreadyAdded = true;
					
					if (treeItemLocations.containsValue(orig))
					{
						DISPLAY.syncExec(new Runnable()
						{
							public void run()
							{
								curId = treeItemIds.get(orig);
							}
						});
						
						id = curId;
						
						if (isDirectory)
						{
							findSubFiles(subFiles[i], id, ignoreRemove);
						}
					}
					// Set text correctly of renamed files.
					else if (treeItemLocations.containsValue(orig))
					{
						id = treeItemIds.get(orig);
						
						treeMenu.setTreeItemText(id, name);
					}
					else
					{
						alreadyAdded = false;
					}
				}
				
				if (!alreadyAdded)
				{
					DISPLAY.syncExec(new Runnable()
					{
						public void run()
						{
							curId = treeMenu.addItem(parent, name, img);
						}
					});
					
					id = curId;
					
					if (isDirectory)
					{
						findSubFiles(subFiles[i], id, ignoreRemove);
						
						treeItemDirectories.put(id, orig);
					}
					
					if (fileCacheSaved.containsKey(orig))
					{
						fileCacheSaved.put(orig, true);
					}
					
					treeItemLocations.put(id, orig);
					treeItemIds.put(orig, id);
				}
			}
		}
	}
	
	/**
	 * Implemented method that occurs whenever the content
	 * of a TextField is changed. In this case it tells you that
	 * the current file has been changed and needs to be saved.
	 */
	public void contentChanged(ContentEvent event)
	{
		Object source = event.getSource();
		
		if (source == codeField)
		{
			if (fileLocation != null)
			{
				setFileSaved(fileLocation, false);
				
				final String code = codeField.getText();
				
				new Thread()
				{
					public void run()
					{
						final String outputLocation = FileUtils.getParentFolder(fileLocation) + "/";
						
						DISPLAY.syncExec(new Runnable()
						{
							public void run()
							{
//								try
//								{
//									consoleField.setText("");
//									
//									Language.compile(fileLocation, codeField.getRawText(), outputLocation, consoleStream);
//								}
//								catch (UnsupportedOperationException e)
//								{
//									consoleStream.println(e.getMessage());
//								}
							}
						});
						
//						Language.compile(fileLocation, code, outputLocation, null);
					}
				}.start();
			}
		}
	}
	
	/**
	 * Return the Image associated with the type of file given through
	 * the location parameter.
	 * 
	 * @param location The location of the file.
	 * @return The Image associated with the file.
	 */
	private Image getFileImage(String location)
	{
		Language language = Language.getLanguageByExtension(location);
		
		if (language != null)
		{
			Image image = language.getImage();
			
			if (image != null)
			{
				return image;
			}
		}
		
		return fileImage;
	}
	
	/**
	 * Checks whether the text returned from the codeField is null, or
	 * if it is an empty String.
	 * 
	 * @return Whether the codeField is empty.
	 */
	public boolean isCodeFieldEmpty()
	{
		String text = codeField.getText();
		
		return text == null || text.equals("");
	}
	
	/**
	 * Set the tab and TreeMenu item associated with the file
	 * location to start with a '*' depending if the file is
	 * saved or not.
	 * 
	 * @param location The location of the file to set as saved or not.
	 * @param saved Whether the file is saved or not.
	 */
	public void setFileSaved(String location, boolean saved)
	{
		String locKey	= location;
		
		String text		= null;
		
		int id			= 0;
		
		if (saved)
		{
			if (treeItemIds.containsKey(locKey))
			{
				id = treeItemIds.get(locKey);
			}
			
			if (tabFileIds.containsKey(locKey))
			{
				int tabId	= tabFileIds.get(locKey);
				
				text		= fileTabs.getTabText(tabId);
			}
			else if (treeItemLocations.containsValue(locKey))
			{
				text = treeMenu.getTreeItemText(id);
			}
			
			if (text != null && text.startsWith("*"))
			{
				text = text.substring(1);
			
				if (treeItemLocations.containsValue(locKey))
				{
					treeMenu.setTreeItemText(id, text);
				}
				if (tabFileIds.containsKey(locKey))
				{
					fileTabs.setTabText(tabFileIds.get(locKey), text);
				}
			}
			
			fileCacheSaved.put(locKey, true);
		}
		else
		{
			int tabId = tabFileIds.get(locKey);
			
			text = fileTabs.getTabText(tabId);
			
			if (!text.startsWith("*"))
			{
				text = "*" + text;
			}
			
			if (treeItemIds.containsKey(locKey))
			{
				id = treeItemIds.get(locKey);
				treeMenu.setTreeItemText(id, text);
			}
			
			fileTabs.setTabText(tabId, text);
			
			fileCacheSaved.put(locKey, false);
			
			String fileContents = codeField.getText();
			
			fileCache.put(locKey, fileContents);
		}
	}
	
	/**
	 * Method to remove all of the TreeMenu items and clear the
	 * HashMaps.
	 */
	public void removeAllTreeItems()
	{
		treeMenu.removeAllItems();
		
		treeItemLocations.clear();
		treeItemIds.clear();
		treeItemDirectories.clear();
	}
	
	/**
	 * Add a tab of the file at fileLocation to the TabMenu.
	 * 
	 * @param fileLocation The location of the file to represent.
	 */
	private void addTab(String fileLocation)
	{
		addTab(fileLocation, true);
	}
	
	/**
	 * Method to add a tab at the specified location.
	 * 
	 * @param fileLocation The location of the file that was opened.
	 * @param cache Whether or not to save the tab for later use after
	 * 		restart.
	 */
	private void addTab(String fileLocation, boolean cache)
	{
		String fileName = FileUtils.getFileName(fileLocation);
		
		addTab(fileName, fileLocation, cache);
	}
	
	/**
	 * Method to add a tab at the specified location with the tab labeled
	 * with the fileName param.
	 * 
	 * @param fileName The String to label the tab with.
	 * @param location The location of the file that was opened.
	 * @param selection The selection to start the tab contents off with.
	 * @param topPixel The value of the top pixel of the contents.
	 * @param cache Whether or not to save the tab for later use after
	 * 		restart.
	 */
	private void addTab(String location, Point selection, int topPixel, boolean cache)
	{
		String fileName = FileUtils.getFileName(location);
		
		addTab(fileName, location, selection, topPixel, cache);
	}
	
	/**
	 * Add a tab of the file with the fileName and location to the TabMenu.
	 * 
	 * @param fileName The name of the file to represent.
	 * @param location The location, including the fileName, of the file.
	 */
	private void addTab(String fileName, String location)
	{
		addTab(fileName, location, true);
	}
	
	/**
	 * Method to add a tab at the specified location with the tab labeled
	 * with the fileName param.
	 * 
	 * @param fileName The String to label the tab with.
	 * @param location The location of the file that was opened.
	 * @param cache Whether or not to save the tab for later use after
	 * 		restart.
	 */
	private void addTab(String fileName, String location, boolean cache)
	{
		addTab(fileName, location, new Point(0, 0), 0, cache);
	}
	
	/**
	 * Method to add a tab at the specified location with the tab labeled
	 * with the fileName param.
	 * 
	 * @param fileName The String to label the tab with.
	 * @param location The location of the file that was opened.
	 * @param selection The selection to start the tab contents off with.
	 * @param topPixel The value of the top pixel of the contents.
	 * @param cache Whether or not to save the tab for later use after
	 * 		restart.
	 */
	private void addTab(String fileName, final String location, Point selection, int topPixel, boolean cache)
	{
		final int id = fileTabs.addTab(fileName);
		
		if (oldTabId == 0)
		{
			oldTabId = id;
		}
		
		tabTopPixels.put(id, topPixel);
		tabSelections.put(id, selection);
		
		tabFileLocations.put(id, location);
		tabFileIds.put(location, id);
		
		if (cache)
		{
			new Thread()
			{
				public void run()
				{
					appendConfigDataValue("last.tabs", location + " " + tabSelections.get(id).x + " " + tabSelections.get(id).y + " " + tabTopPixels.get(id) + ";");
				}
			}.start();
		}
	}
	
	/**
	 * Implemented method that is called whenever a tab's close icon
	 * is pressed. If the file in the tab is not saved, ask whether to
	 * save it or not.
	 * 
	 * @param event The TabMenuEvent sent with the tab close.
	 * @return Whether to close the tab or not.
	 */
	public boolean tabClosing(TabMenuEvent event)
	{
		return closeTab(tabFileLocations.get(event.getTabId()), false, event.getSource());
	}
	
	public void tabClosed(TabMenuEvent event)
	{
		int tabId = event.getTabId();
		
		int newId = fileTabs.getSelection();
		
		String location = tabFileLocations.get(tabId);
		
		String lastTabs = CONFIG_DATA.get("last.tabs");
		
		int start = lastTabs.indexOf(tabFileLocations.get(tabId));
		int end   = lastTabs.indexOf(';', start) + 1;
		
		String data = lastTabs.substring(start, end);
		
		String files = lastTabs.replace(data, "");
		setConfigDataValue("last.tabs", files);
		
		tabFileLocations.remove(tabId);
		tabFileIds.remove(location);
		tabTopPixels.remove(tabId);
		tabSelections.remove(tabId);
		
		fileCache.remove(location);
		fileCacheSaved.remove(location);
		tabFileIds.remove(location);
		tabFileLocations.remove(tabId);
		
		if (tabId == oldTabId)
		{
			oldTabId = newId;
		}
		
		if (tabId != newId && newId >= 0)
		{
			String loc = tabFileLocations.get(newId);

			try
			{
				openFile(loc);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			fileLocation = null;
			codeField.setText("");
		}
		
		if (fileTabs.getNumTabs() <= 0)
		{
			setCodeFieldExpanded(false);
		}
	}
	
	/**
	 * Implemented method that is called whenever a tab is selected
	 * in a TabMenu. It then opens the file.
	 * 
	 * @param event The TabMenuEvent sent with the tab select.
	 */
	public void tabSelected(TabMenuEvent event)
	{
		boolean down = event.wasDown();
		
		int tabId    = event.getTabId();
		int button   = event.getButton();
		
		if (event.getSource() == fileTabs)
		{
			String location = tabFileLocations.get(tabId);
			
			if (button == 1 && event.wasClicked())
			{
				if (location != null)
				{
					if (!location.equals(fileLocation))
					{
						try
						{
							openFile(location);
						
							codeField.setSelection(tabSelections.get(tabId));
							codeField.setTopPixel(tabTopPixels.get(tabId));
						
							// Did not select the current tab content...
							//codeField.select();
						}
						catch (IOException e)
						{
							e.printStackTrace();
						}
					}
				}
			}
			else if (button == 2 && event.wasClicked())
			{
				String args[] = new String[] { "-nw", "-noRemember", '"' + location + '"' };
				
				try
				{
					openNewIDE(args);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			else if (button == 3)
			{
				if (event.wasClicked())
				{
					Point loc = event.getLocation();
					
					loc = DISPLAY.map(fileTabs.getControl(), null, loc);
					
					tabMenu.setLocation(loc.x, loc.y);
					tabMenu.open();
				}
				else
				{
					lastTabRightClicked = tabId;
				}
			}
			else
			{
				if (location != null && !location.equals(fileLocation))
				{
					try
					{
						openFile(location, true, true, tabSelections.get(tabId), tabTopPixels.get(tabId));
					
						// Did not select the current tab content...
						//codeField.select();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}
		}
		else if (event.getSource() == consoleTabs)
		{
			setMainProgram(tabId);
		}
	}
	
	/**
	 * Implemented method that is called whenever a tab is double clicked
	 * in a TabMenu. It expands or shrinks the CodeField.
	 * 
	 * @param event The TabMenuEvent sent with the tab double click.
	 */
	public void tabDoubleClicked(TabMenuEvent event)
	{
		int button = event.getButton();
		
		if (event.getSource() == fileTabs)
		{
			if (button == 1)
			{
				oldTabId2 = event.getTabId();
				
				toggleCodeField = true;
			}
		}
	}
    
    /**
     * Get whether or not the CodeField is expanded to fill the
     * full screen space.
     * 
     * @return Whether or not the CodeField is expanded.
     */
	private boolean isCodeFieldExpanded()
	{
		return oldCodeFieldHorizontalPercentage != -1;
	}
	
    /**
     * Set whether or not to expand the CodeField to fill, or release
     * the remaining space.
     * 
     * @param expand If true, expand the CodeField to fill the remaining
     *         space. If false, release the remaining space.
     */
	private void setCodeFieldExpanded(boolean expand)
	{
		Point contentSize = window.getContentPanel().getSize();
		
		Point size = new Point(0, 0);
		
		size.x = codeField.getWidth()  + treeMenu.getWidth() + treeMenuSizer.getWidth();
		size.y = codeField.getHeight() + consoleField.getHeight() + codeFieldSizer.getHeight();
		
		if (consoleTabs.getNumTabs() > 0)
		{
			size.y += consoleTabs.getHeight();
		}
		
		if (expand)
		{
			oldCodeFieldHorizontalPercentage = codeField.getWidth()  / (double)contentSize.x;
			oldCodeFieldVerticalPercentage   = codeField.getHeight() / (double)contentSize.y;
			
			codeField.setLocation(0, codeField.getY());
			codeField.setSize(size);
			
			codeFieldSizer.setVisible(false);
			treeMenuSizer.setVisible(false);
		}
		else
		{
			if (isCodeFieldExpanded())
			{
				int width  = (int)Math.round(oldCodeFieldHorizontalPercentage * contentSize.x);
				int height = (int)Math.round(oldCodeFieldVerticalPercentage   * contentSize.y);
				
				codeField.setLocation(contentSize.x - width, codeField.getY());
				codeField.setSize(width, height);
				
				oldCodeFieldHorizontalPercentage = -1;
				oldCodeFieldVerticalPercentage   = -1;
				
				codeFieldSizer.setVisible(true);
				treeMenuSizer.setVisible(true);
			}
		}
		
		updateLayout();
		
		codeField.setFocus();
	}
	
	/**
	 * Open a new instance of the IDE.
	 * 
	 * @param args The command-line args to pass to the new instance.
	 * @throws IOException 
	 */
	private void openNewIDE(String args[]) throws IOException
	{
		String args2[] = new String[args.length + 1];
		
		args2[0] = '"' + FileUtils.getExecutablePrefix() + "ArrowIDE" + FileUtils.getExecutableExtension() + '"';
		
		for (int i = 0; i < args.length; i++)
		{
			args2[i + 1] = args[i];
		}
		
		args = args2;
		
		Command c = new Command(DISPLAY, args, resourcesLocation);
		
		c.addCommandListener(new CommandListener()
		{
			@Override
			public void resultReceived(int result)
			{
				if (result != 0)
				{
					System.err.println("Error (" + result + ") occurred while trying to open a new instance of the IDE.");
				}
			}
			
			@Override
			public void commandExecuted()
			{
				
			}
		});
		
		ProgramListener listener = new ProgramListener()
		{
			@Override
			public void programTerminated(Program program)
			{
				
			}
			
			@Override
			public void programStarted(Program program)
			{
			}
			
			@Override
			public void messageReceived(String message)
			{
				System.out.println(message);
			}
			
			@Override
			public void errorMessageReceived(String message)
			{
				System.err.println(message);
			}
		};
		
		c.execute("ArrowIDE (New instance)", listener);
	}

    /**
     * Reset the main Program that is running to the next best
     * candidate from the tab that is given.
     * 
     * @param tabId The id of the tab to get the next best Program
     *         from.
     */
	private void resetMainProgram(int tabId)
	{
		int newId = consoleTabs.getSelection();
		
		if (newId <= 0 || newId == tabId)
		{
			mainProgram = null;
			
			setMainProgram(0);
			
			consoleField.setBackground(CONSOLE_DEFAULT_COLOR);
		}
		else
		{
			setMainProgram(newId);
		}
		
		updateLayout();
	}
	
    /**
     * Set the Main Program that is running to the Program that is
     * under the specified tabId.
     * 
     * @param tabId The id of the tab to set the main Program to.
     */
	private void setMainProgram(int tabId)
	{
		if (consoleTabPrograms.containsKey(tabId))
		{
			Program selectedProgram = consoleTabPrograms.get(tabId);
			
			if (mainProgram != selectedProgram)
			{
				mainProgram = selectedProgram;
				
				consoleField.setText(mainProgram.getText());
				
				Color color = null;
				
				if (selectedProgram.isRunning())
				{
					color = CONSOLE_RUNNING_COLOR;
				}
				else
				{
					color = CONSOLE_TERMINATED_COLOR;
				}
				
				consoleField.setBackground(color);
			}
		}
		else
		{
			consoleField.setText("");
		}
	}
	
	/**
	 * Remove the specified Program from the list of Programs.
	 * 
	 * @param program The specified Program to remove.
	 */
	public void removeProgram(Program program)
	{
		programs.remove(program);
	}
	
	/**
	 * Remove the Program with the specified id from the list of Programs.
	 * 
	 * @param id The specified id of the Program to remove.
	 */
	public void removeProgram(int id)
	{
		for (int i = programs.size() - 1; i >= 0; i--)
		{
			if (programs.get(i).getId() == id)
			{
				programs.remove(i);
				
				break;
			}
		}
	}
	
	/**
	 * Implemented method that is called whenever a key is pressed
	 * in a TextField.
	 * 
	 * @param e The CodeFieldEvent that was passed.
	 */
	public void keyPressed(CodeFieldEvent e)
	{
		if (e.getSource() == codeField)
		{
			if (e.getStateMask() == (Integer)PROPERTIES.get("key.control"))
			{
				if (e.getKeyCode() == 's')
				{
					try
					{
						saveFile(fileLocation);
					}
					catch (IOException e1)
					{
						e1.printStackTrace();
					}
				}
			}
		}
	}
	
	/**
	 * Method that deletes the file at the specified location.
	 * 
	 * @param location The location of the file to be deleted.
	 * @return Whether the file was successfully deleted or not.
	 */
	public boolean deleteFile(String location)
	{
		int treeId = treeItemIds.get(location);
		
		treeItemLocations.remove(treeId);
		treeItemDirectories.remove(treeId);
		fileCache.remove(location);
		fileCacheSaved.remove(location);
		
		treeMenu.removeItem(treeId);
		
		boolean deleted = FileUtils.delete(new File(location));
		
		return deleted;
	}
	
	/**
	 * Method to synchronistically update the components of the main
	 * window.
	 */
	public void update()
	{
		if (mainProgram != null)
		{
			if (!consoleField.getText().equals(mainProgram.getText()))
			{
				consoleField.setText(mainProgram.getText());
			}
		}
		
		codeFieldToggled = false;
		
		if (toggleCodeField)
		{
			setCodeFieldExpanded(!isCodeFieldExpanded());
			
			toggleCodeField  = false;
			codeFieldToggled = true;
		}
	}

	/**
	 * @see net.foxycorndog.arrowide.event.ProgramListener#errorMessageReceived(java.lang.String)
	 */
	public void errorMessageReceived(String message)
	{
		
	}

	/**
	 * @see net.foxycorndog.arrowide.event.ProgramListener#messageReceived(java.lang.String)
	 */
	public void messageReceived(String message)
	{
		
	}

	/**
	 * @see net.foxycorndog.arrowide.event.ProgramListener#programStarted(net.foxycorndog.arrowide.Program)
	 */
	public void programStarted(Program program)
	{
		consoleField.setBackground(CONSOLE_RUNNING_COLOR);
	}

	/**
	 * @see net.foxycorndog.arrowide.event.ProgramListener#programTerminated(net.foxycorndog.arrowide.Program)
	 */
	public void programTerminated(Program program)
	{
		consoleField.setBackground(CONSOLE_TERMINATED_COLOR);
		
		if (!programs.contains(program))
		{
			return;
		}
		
		if (program.getText().length() <= 0)
		{
			consoleTabs.closeTab(program.getId());
			
			programs.remove(program);
			
			if (programs.size() <= 0)
			{
				resetMainProgram(program.getId());
			}
		}
	}
	
	/**
	 * Print an error telling that the file at the specified location
	 * could not be found.
	 * 
	 * @param location The location of the file that could not be found.
	 */
	private static void printFileNotFoundError(String location)
	{
		System.err.println("The specified file at '" + location + "' cannot be found.");
	}

	/**
	 * @see net.foxycorndog.arrowide.event.DropListener#itemDropped(net.foxycorndog.arrowide.event.DropEvent)
	 */
	public void itemDropped(DropEvent event)
	{
		int    type = event.getType();
		
		Object data = event.getData();
		
		if (type == DropEvent.FILES)
		{
			String strs[] = (String[])data;
			
			for (int i = 0; i < strs.length; i++)
			{
				try
				{
					openFile(strs[i]);
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
		else if (type == DropEvent.TEXT)
		{
			String str = (String)data;
			
			codeField.insert(str);
		}
		
		window.setFocus();
		window.setActive();
		
		window.getShell().forceFocus();
		window.getShell().forceActive();
	}
}