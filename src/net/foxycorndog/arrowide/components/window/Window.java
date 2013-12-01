package net.foxycorndog.arrowide.components.window;

import java.util.ArrayList;

import net.foxycorndog.arrowide.event.DropEvent;
import net.foxycorndog.arrowide.event.DropListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Tracker;

import static net.foxycorndog.arrowide.ArrowIDE.PROPERTIES;

public class Window
{
	private boolean						dragging, maximized, fullscreen;
	private boolean						resizable;
	private boolean						custom;
	
	private int							minimumWidth, minimumHeight;
	private int							borderSize;
	private int							startX, startY, width, height;
	private int							dragType;
	
	private Point						sizeBefore, locationBefore;
	
	private Cursor						defaultCursor, leftResizeCursor, topLeftResizeCursor, topRightResizeCursor, topResizeCursor;
	
	private Tracker						tracker;
	
	private Listener					moveListener;
	
	private ControlListener				trackListener;
	
	private Composite					content;
	
	private Shell						shell;
	
	private Display						display;
	
	private ArrayList<WindowListener>	windowListeners;
	private ArrayList<DropListener>		dropListeners;
	
	private static int					numberOpen;
	
	private static final int			BOTTOM = 1, TOP = 2, LEFT = 3, RIGHT = 4, BOTTOM_LEFT = 5, BOTTOM_RIGHT = 6, TOP_LEFT = 7, TOP_RIGHT = 8;
	
	public Window(Display display, boolean custom)
	{
		this(display, custom, custom ? 0 : -1);
	}
	
	public Window(Display display, boolean custom, int style)
	{
		this.display = display;
		
		if (custom)
		{
			shell = new Shell(display, SWT.NO_TRIM);
		}
		else if (style == -1)
		{
			shell = new Shell(display);
		}
		else
		{
			shell = new Shell(display, style);
		}
		
		windowListeners = new ArrayList<WindowListener>();
		dropListeners   = new ArrayList<DropListener>();
		
		Transfer[] types = new Transfer[] { FileTransfer.getInstance(), TextTransfer.getInstance() };
		
		DropTarget target = new DropTarget(shell, DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT);
	    target.setTransfer(types);
	    target.addDropListener(new DropTargetAdapter()
	    {
			public void dragEnter(DropTargetEvent event)
			{
				if (event.detail == DND.DROP_DEFAULT)
				{
					event.detail = (event.operations & DND.DROP_COPY) != 0 ? DND.DROP_COPY : DND.DROP_NONE;
				}
				
				for (int i = 0, n = event.dataTypes.length; i < n; i++)
				{
					if (TextTransfer.getInstance().isSupportedType(event.dataTypes[i]))
					{
						event.currentDataType = event.dataTypes[i];
					}
				}
			}
			
			public void dragOver(DropTargetEvent event)
			{
				event.feedback = DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL;
			}
			
			public void drop(DropTargetEvent event)
			{
				FileTransfer fileInstance = FileTransfer.getInstance();
				TextTransfer textInstance = TextTransfer.getInstance();
				
				TransferData fileTypes[]  = fileInstance.getSupportedTypes();
				TransferData textTypes[]  = textInstance.getSupportedTypes();
				
				boolean done = false;
				
				for (int i = 0; i < fileTypes.length && !done; i++)
				{
					if (fileTypes[i].type == event.currentDataType.type)
					{
						Object object = fileInstance.nativeToJava(event.currentDataType);
						
						if (object instanceof String[])
						{
							String strs[] = (String[])object;
							
							DropEvent e = new DropEvent(DropEvent.FILES, strs);
								
							for (int j = 0; j < dropListeners.size(); j++)
							{
								dropListeners.get(j).itemDropped(e);
							}
						}
						
						done = true;
					}
				}
				
				for (int i = 0; i < textTypes.length && !done; i++)
				{
					if (textTypes[i].type == event.currentDataType.type)
					{
						Object object = textInstance.nativeToJava(event.currentDataType);
						
						if (object instanceof String)
						{
							String str = (String)object;
							
							DropEvent e = new DropEvent(DropEvent.TEXT, str);
							
							for (int j = 0; j < dropListeners.size(); j++)
							{
								dropListeners.get(j).itemDropped(e);
							}
						}
						
						done = true;
					}
				}
			}
	    });
		
		content = new Composite(shell, SWT.NONE);
		
		setCustom(custom);
//		shell.setCapture(true);
//		shell.setLayoutDeferred(true);
//		shell.setTouchEnabled(false);
		
		sizeBefore     = new Point(0, 0);
		locationBefore = new Point(0, 0);
		
		setResizable(true);
		
		shell.addControlListener(new ControlListener()
		{
			public void controlResized(ControlEvent e)
			{
				setContentSize();
			}
			
			public void controlMoved(ControlEvent e)
			{
				
			}
		});
	}
	
	private int getDragType(int x, int y)
	{
		if (x >= borderSize)
		{
			if (x < shell.getSize().x - borderSize)
			{
				if (y >= borderSize)
				{
					return BOTTOM;
				}
				else
				{
					return TOP;
				}
			}
			else
			{
				if (y >= borderSize)
				{
					if (y < shell.getSize().y - borderSize)
					{
						return RIGHT;
					}
					else
					{
						return BOTTOM_RIGHT;
					}
				}
				else
				{
					return TOP_RIGHT;
				}
			}
		}
		else
		{
			if (y >= borderSize)
			{
				if (y < shell.getSize().y - borderSize)
				{
					return LEFT;
				}
				else
				{
					return BOTTOM_LEFT;
				}
			}
			else
			{
				return TOP_LEFT;
			}
		}
	}
	
	private void setContentSize()
	{
		int bSize = borderSize * 2;
		
		if (isMaximized() || isFullscreen())
		{
			bSize = 0;
		}
		
		content.setSize(shell.getClientArea().width - bSize, shell.getClientArea().height - bSize);
		content.setLocation(bSize / 2, bSize / 2);
		
		if (width > minimumWidth)
		{
			width  = content.getSize().x;
		}

		if (height > minimumHeight)
		{
			height = content.getSize().y;
		}
	}
	
	public boolean isResizable()
	{
		return resizable;
	}
	
	public void setResizable(boolean resizable)
	{
		this.resizable = resizable;
	}
	
	public void setCustom(boolean custom)
	{
		if (this.custom != custom)
		{
			if (custom)
			{
				defaultCursor = shell.getCursor();
				
				Image image = new Image(display, PROPERTIES.get("resources.location") + "res/images/leftresizecursor.png");
				leftResizeCursor = new Cursor(display, image.getImageData(), image.getBounds().width / 2, image.getBounds().height / 2);
				
				image = new Image(display, PROPERTIES.get("resources.location") + "res/images/topresizecursor.png");
				topResizeCursor = new Cursor(display, image.getImageData(), image.getBounds().width / 2, image.getBounds().height / 2);
				
				image = new Image(display, PROPERTIES.get("resources.location") + "res/images/topleftresizecursor.png");
				topLeftResizeCursor = new Cursor(display, image.getImageData(), image.getBounds().width / 2, image.getBounds().height / 2);
				
				image = new Image(display, PROPERTIES.get("resources.location") + "res/images/toprightresizecursor.png");
				topRightResizeCursor = new Cursor(display, image.getImageData(), image.getBounds().width / 2, image.getBounds().height / 2);
				
				Color bg = shell.getBackground();
				
				int offset = 26;
				RGB rgb    = bg.getRGB();
				rgb.red   -= offset;
				rgb.green -= offset;
				rgb.blue  -= offset;
				
				if (rgb.red < 0)
				{
					rgb.red += offset * 2;
				}
				if (rgb.green < 0)
				{
					rgb.green += offset * 2;
				}
				if (rgb.blue < 0)
				{
					rgb.blue += offset * 2;
				}
				
				Color color = new Color(Display.getDefault(), rgb);
				
				setBorderColor(color);
				
				setBorderSize(3);
				
				minimumWidth  = borderSize * 2;
				minimumHeight = borderSize * 2;
				
				width   = minimumWidth  + 1;
				height  = minimumHeight + 1;
				
				tracker = new Tracker(shell, SWT.NONE);
				
				trackListener = new ControlListener()
				{
					public void controlResized(ControlEvent e)
					{
						
					}
					
					public void controlMoved(ControlEvent e)
					{
						if (!isResizable())
						{
							return;
						}
						
						Point loc = new Point(tracker.getRectangles()[0].x + 99999, tracker.getRectangles()[0].y + 99999);
						
						int dx = loc.x - startX;
						int dy = loc.y - startY;
						int x2 = shell.getLocation().x;
						int y2 = shell.getLocation().y;
						
						if (dragType == LEFT || dragType == BOTTOM_LEFT || dragType == TOP_LEFT)
						{
							dx *= -1;
						}
						
						if (dragType == TOP || dragType == TOP_LEFT || dragType == TOP_RIGHT)
						{
							dy *= -1;
						}
						
						width  += dx;
						height += dy;
						
						if ((width > 0 || height > 0))
						{
							int newWidth  = shell.getSize().x;
							int newHeight = shell.getSize().y;
							
							if (width > 0 || dx < 0)
							{
								if (dragType != TOP && dragType != BOTTOM)
								{
									newWidth += dx;
									
									if (newWidth < minimumWidth)
									{
										newWidth = minimumWidth;
									}
								}
							}
							if (height > 0 || dy < 0)
							{
								if (dragType != LEFT && dragType != RIGHT)
								{
									newHeight += dy;
									
									if (newHeight < minimumHeight)
									{
										newHeight = minimumHeight;
									}
								}
							}
							
							if (newWidth != shell.getSize().x || newHeight != shell.getSize().y)
							{
								
								if (dragType == TOP_LEFT || dragType == LEFT || dragType == BOTTOM_LEFT)
								{
									x2 -= dx;
								}
								
								if (dragType == TOP_LEFT || dragType == TOP || dragType == TOP_RIGHT)
								{
									y2 -= dy;
								}
						
								shell.setBounds(x2, y2, newWidth, newHeight);
							}
						}
						
						startX = loc.x;
						startY = loc.y;
					}
				};
				
				tracker.addControlListener(trackListener);
				
				moveListener = new Listener()
				{
					public void handleEvent(Event event)
					{
						if (event.type == SWT.MouseDown)
						{
							dragType = getDragType(event.x, event.y);
							dragging = true;
							
							startX = 0;
							startY = 0;
							
							tracker.setRectangles(new Rectangle[] { new Rectangle(-99999, -99999, 0, 0) });
							tracker.open();
							
							tracker.close();
							dragging = false;
							dragType = 0;
						}
						else if (event.type == SWT.MouseMove)
						{
							if (isResizable())
							{
								int type = getDragType(event.x, event.y);
								
								if (type == TOP_LEFT || type == BOTTOM_RIGHT)
								{
									shell.setCursor(topLeftResizeCursor);
								}
								else if (type == TOP || type == BOTTOM)
								{
									shell.setCursor(topResizeCursor);
								}
								else if (type == LEFT || type == RIGHT)
								{
									shell.setCursor(leftResizeCursor);
								}
								else if (type == TOP_RIGHT || type == BOTTOM_LEFT)
								{
									shell.setCursor(topRightResizeCursor);
								}
							}
						}
						else if (event.type == SWT.MouseExit)
						{
							shell.setCursor(defaultCursor);
						}
					}
				};
				
				shell.addListener(SWT.MouseDown, moveListener);
				shell.addListener(SWT.MouseMove, moveListener);
				shell.addListener(SWT.MouseExit, moveListener);
			}
			else
			{
				tracker.removeControlListener(trackListener);
				
				shell.removeListener(SWT.MouseDown, moveListener);
				shell.removeListener(SWT.MouseMove, moveListener);
				shell.removeListener(SWT.MouseExit, moveListener);
				
				tracker      = null;
				moveListener = null;
			}
		}
		
		this.custom = custom;
	}
	
	public Color getBackground()
	{
		return content.getBackground();
	}
	
	public void setBackground(Color color)
	{
		content.setBackground(color);
	}
	
	public boolean isDisposed()
	{
		return shell.isDisposed();
	}
	
	public void dispose()
	{
		close();
		shell.dispose();
	}
	
	public void open()
	{
		shell.open();
		shell.setMinimized(false);
		shell.setActive();
		
		numberOpen++;
	}
	
	public void close()
	{
		numberOpen--;
		
		shell.close();
		
//		if (numberOpen <= 0)
//		{
//			System.exit(0);
//		}
	}
	
	public void setFocus()
	{
		shell.setFocus();
	}
	
	public void forceFocus()
	{
		shell.forceFocus();
	}
	
	public void setActive()
	{
		shell.setActive();
	}
	
	public void forceActive()
	{
		shell.forceActive();
	}
	
	public Composite getContentPanel()
	{
		return content;
	}
	
	public Shell getShell()
	{
		return shell;
	}
	
	public Rectangle getBounds()
	{
		return shell.getBounds();
	}
	
	public int getX()
	{
		return shell.getLocation().x;
	}
	
	public int getY()
	{
		return shell.getLocation().y;
	}
	
	public Point getLocation()
	{
		return shell.getLocation();
	}
	
	public void setLocation(int x, int y)
	{
		shell.setLocation(x, y);
		locationBefore = shell.getLocation();
	}
	
	public Point getSize()
	{
		return shell.getSize();
	}
	
	public void setSize(int width, int height)
	{
		shell.setSize(width, height);
		setContentSize();
		sizeBefore = shell.getSize();
	}
	
	public int getWidth()
	{
		return shell.getSize().x;
	}
	
	public int getHeight()
	{
		return shell.getSize().y;
	}
	
	public Image getIcon()
	{
		return shell.getImage();
	}
	
	public void setIcon(Image icon)
	{
		boolean doit = true;
		
		for (int i = windowListeners.size() - 1; i >= 0; i--)
		{
			if (!windowListeners.get(i).iconChanged(icon))	
			{
				doit = false;
			}
		}
		
		if (doit)
		{
			shell.setImage(icon);
		}
	}
	
	public String getTitle()
	{
		return shell.getText();
	}
	
	public void setTitle(String title)
	{
		boolean doit = true;
		
		for (int i = windowListeners.size() - 1; i >= 0; i--)
		{
			if (!windowListeners.get(i).titleChanged(title))	
			{
				doit = false;
			}
		}
		
		if (doit)
		{
			shell.setText(title);
		}
	}
	
	public void addControlListener(ControlListener listener)
	{
		shell.addControlListener(listener);
	}

	public void addPaintListener(PaintListener listener)
	{
		content.addPaintListener(listener);
	}
	
	public void addListener(int eventType, Listener listener)
	{
		shell.addListener(eventType, listener);
	}
	
	public boolean isMaximized()
	{
		return maximized;
	}
	
	public void setMaximized(boolean maximized)
	{
		if (!isResizable() || isFullscreen())
		{
			return;
		}
		
		this.maximized = maximized;
		
		if (custom)
		{
			if (maximized)
			{
				sizeBefore     = shell.getSize();
				locationBefore = shell.getLocation();
				
//				Dimension screenSize  = Toolkit.getDefaultToolkit().getScreenSize();
//				java.awt.Rectangle useableSize = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
				
//				System.out.println(taskbarHeight);
				
				Monitor monitor = getCurrentMonitor();
				
//				int taskbarHeight = monitor.getBounds().height - monitor.getClientArea().height;
				
				shell.setSize(monitor.getClientArea().width, monitor.getClientArea().height);
				shell.setLocation(monitor.getClientArea().x, monitor.getClientArea().y);
			}
			else
			{
				shell.setSize(sizeBefore);
				shell.setLocation(locationBefore);
			}
		}
		else
		{
			shell.setMaximized(maximized);
		}
	}
	
	public Monitor getCurrentMonitor()
	{
		Monitor monitors[] = Display.getDefault().getMonitors();
		
		Monitor monitor = null;
		
		int     maxSize = 0;
		
		for (int i = 0; i < monitors.length; i++)
		{
			Monitor m = monitors[i];
			
			int size = intersectionArea(m.getBounds().x, m.getBounds().y, m.getBounds().width, m.getBounds().height, shell.getBounds().x, shell.getBounds().y, shell.getBounds().width, shell.getBounds().height);
			
			if (size > maxSize)
			{
				monitor = m;
				
				maxSize = size;
			}
		}
		
		return monitor;
	}
	
	public boolean isMovable()
	{
		return !isFullscreen();
	}
	
	public boolean isMinimized()
	{
		return shell.getMinimized();
	}
	
	public boolean isFullscreen()
	{
		return fullscreen;
	}
	
	public void setFullscreen(boolean fullscreen)
	{
		if (!isResizable())
		{
			return;
		}
		
		this.fullscreen = fullscreen;
		
//		if (fullscreen)
//		{
//			sizeBefore = shell.getSize();
//			locationBefore = shell.getLocation();
//			
//			Dimension screenSize  = Toolkit.getDefaultToolkit().getScreenSize();
//			
//			shell.setSize(screenSize.width, screenSize.height);
//			shell.setLocation(0, 0);
//		}
//		else
//		{
//			shell.setSize(sizeBefore);
//			shell.setLocation(locationBefore);
//		}
		
		shell.setFullScreen(fullscreen);
	}
	
	public void setMinimized(boolean minimized)
	{
		shell.setMinimized(minimized);
	}
	
	public Rectangle getClientArea()
	{
		return new Rectangle(0, 0, content.getSize().x, content.getSize().y);
	}
	
	public boolean isVisible()
	{
		return shell.isVisible();
	}
	
	public void setVisible(boolean visible)
	{
		shell.setVisible(visible);
	}
	
	public int getBorderSize()
	{
		return borderSize;
	}
	
	public void setBorderSize(int borderSize)
	{
		this.borderSize = borderSize;
		
		setContentSize();
	}
	
	public void setMinimumSize(int width, int height)
	{
		this.minimumWidth  = width;
		this.minimumHeight = height;
	}
	
	public int getMinimumWidth()
	{
		return minimumWidth;
	}
	
	public void setMinimumWidth(int width)
	{
		this.minimumWidth  = width;
	}
	
	public int getMinimumHeight()
	{
		return minimumHeight;
	}
	
	public void setMinimumHeight(int height)
	{
		this.minimumHeight = height;
	}
	
	public Color getBorderColor()
	{
		return shell.getBackground();
	}
	
	public void setBorderColor(Color color)
	{
		shell.setBackground(color);
	}
	
	public void redraw()
	{
		shell.redraw();
		content.redraw();
	}
	
	public void addWindowListener(WindowListener listener)
	{
		windowListeners.add(listener);
	}
	
	public void removeWindowListener(WindowListener listener)
	{
		windowListeners.remove(listener);
	}
	
	public void addDropListener(DropListener listener)
	{
		dropListeners.add(listener);
	}
	
	public void removeDropListener(DropListener listener)
	{
		dropListeners.remove(listener);
	}
	
	private int intersectionArea(int x1, int y1, int width1, int height1, int x2, int y2, int width2, int height2)
	{
        int newX = Math.max(x1, x2);
        int newY = Math.max(y1, y2);

        int newWidth = Math.min(x1 + width1, x2 + width2) - newX;
        int newHeight = Math.min(y1 + height1, y2 + height2) - newY;

        if (newWidth <= 0 || newHeight <= 0) return 0;

        return newWidth * newHeight;
	}
}