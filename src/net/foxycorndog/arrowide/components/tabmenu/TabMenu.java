package net.foxycorndog.arrowide.components.tabmenu;

//import java.awt.Toolkit;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Listener;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import static net.foxycorndog.arrowide.ArrowIDE.PROPERTIES;

public class TabMenu
{
	private int							maxCharacters, maxWidth;
	
	private long						oldTime;

	private Composite					composite;

	private CTabFolder					tabFolder, widthFolder;
	
	private CTabItem					oldItem;

	private TabMenu						thisObject;

	private HashMap<Integer, CTabItem>	tabs;
	private HashMap<CTabItem, Integer>	tabIds;
	private HashMap<Integer, String>	tabsText;

	private ArrayList<TabMenuListener>	listeners;

	private static int					staticId;
	
	private static final int			doubleClickDelay = getDoubleClickDelay();
	
	private static final int getDoubleClickDelay()
	{
		String osName = (String)PROPERTIES.get("os.name");
		
		if (osName.equals("windows") || osName.equals("linux"))
		{
			return (Integer)Toolkit.getDefaultToolkit().getDesktopProperty("awt.multiClickInterval");
		}
		else if (osName.equals("macosx"))
		{
			return 200;
		}
		
		return 0;
	}
	
	public TabMenu(Composite composite)
	{
		this.composite = composite;
		
		listeners = new ArrayList<TabMenuListener>();
		
		tabFolder = new CTabMenu(composite, SWT.NONE);
		tabFolder.setBackground(new Color(Display.getCurrent(), 199, 238, 255));
		tabFolder.setForeground(new Color(Display.getCurrent(), 0, 0, 0));
		tabFolder.setTabHeight(20);
		
		widthFolder = new CTabMenu(composite, SWT.NONE);
		widthFolder.setTabHeight(20);
		widthFolder.setSize(99999, 999);
		widthFolder.setVisible(false);
//		widthFolder.setSimple(false);
		
		tabFolder.setSize(0, tabFolder.getTabHeight() + 3);
//		tabFolder.setSimple(false);
		
		tabs          = new HashMap<Integer, CTabItem>();
		tabIds        = new HashMap<CTabItem, Integer>();
		tabsText      = new HashMap<Integer, String>();
		
		maxCharacters = 20;
		
		thisObject    = this;
		
		tabFolder.addListener(SWT.MouseDown, new Listener()
		{
			public void handleEvent(Event event)
			{
				tabSelected(event, true);
			}
		});
		
		tabFolder.addListener(SWT.MouseUp, new Listener()
		{
			public void handleEvent(Event event)
			{
				tabSelected(event, false);
			}
		});
		
		tabFolder.addSelectionListener(new SelectionListener()
		{
			public void widgetSelected(SelectionEvent e)
			{
//				CTabItem item = (CTabItem)e.item;
//				
//				if (item == oldItem)
//				{
//					int id = tabIds.get(item);
//					
//					TabMenuEvent event = new TabMenuEvent(thisObject, id, 1);
//					
//					for (int i = listeners.size() - 1; i >= 0; i--)
//					{
//						listeners.get(i).tabDoubleClicked(event);
//					}
//				}
//				
//				oldItem = item;
			}

			public void widgetDefaultSelected(SelectionEvent e)
			{
				widgetSelected(e);
			}
		});
		
		final TabMenu thisMenu = this;
		
		tabFolder.addCTabFolder2Listener(new CTabFolder2Listener()
		{
			public void close(CTabFolderEvent e)
			{
				int id = tabIds.get(e.item);
				
				TabMenuEvent event = new TabMenuEvent(thisObject, new Point(e.x, e.y), id, 1, false, true);
				
				for (int i = listeners.size() - 1; i >= 0; i--)
				{
					if (!listeners.get(i).tabClosing(event))
					{
						e.doit = false;
						
						break;
					}
				}
				
				if (e.doit)
				{
					closeTab((CTabItem)e.item);
					
					// Prevent it from thinking it double clicked the tab.
					oldItem = null;
				}
			}

			public void minimize(CTabFolderEvent e)
			{
				
			}

			public void maximize(CTabFolderEvent e)
			{
				
			}

			public void restore(CTabFolderEvent e)
			{
				
			}

			public void showList(CTabFolderEvent e)
			{
				
			}
		});
		
		tabFolder.redraw();
	}
	
	private void tabSelected(Event event, boolean down)
	{
		Point point = new Point(event.x, event.y);
		CTabItem item = tabFolder.getItem(point);
		
		if (item != null)
		{
			int id = tabIds.get(item);
			
			boolean clicked = oldItem == item;
			
			TabMenuEvent e = new TabMenuEvent(thisObject, new Point(event.x, event.y), id, event.button, down, clicked);
			
			for (int i = listeners.size() - 1; i >= 0; i--)
			{
				listeners.get(i).tabSelected(e);
			}
			
			if (down)
			{
				long time = System.currentTimeMillis();
				
				if (item == oldItem)
				{
					if (time - oldTime < doubleClickDelay)
					{
						e = new TabMenuEvent(thisObject, new Point(event.x, event.y), id, event.button, down, clicked);
						
						for (int i = listeners.size() - 1; i >= 0; i--)
						{
							listeners.get(i).tabDoubleClicked(e);
						}
					}
				}
				
				oldTime = time;
			}
		}
			
		oldItem = item;
	}
	
//	private void tabSelected(CTabItem item)
//	{
//		int id = tabIds.get(item);
//
//		TabMenuEvent event = new TabMenuEvent(thisObject, id, 1);
//		
//		for (int i = listeners.size() - 1; i >= 0; i--)
//		{
//			listeners.get(i).tabSelected(event);
//		}
//	}
	
	public void setMaxWidth(int maxWidth)
	{
		this.maxWidth = maxWidth;
		
//		setWidth(maxWidth);
	}
	
	public String getTabText(int id)
	{
		return tabsText.get(id);
	}
	
	public String getTabShortenedText(int id)
	{
		return tabs.get(id).getText();
	}
	
	public void setTabText(int id, String text)
	{
		tabsText.put(id, text);
		
		String origText = text;
		
		if (text.length() > maxCharacters)
		{
			text = text.substring(0, maxCharacters - 3) + "...";
		}
		
		CTabItem wid = new CTabItem(widthFolder, SWT.CLOSE);
		wid.setText(text);
		
		CTabItem item = tabs.get(id);
		
		addWidth(wid.getBounds().width - item.getBounds().width);
		
		wid.dispose();
		
		item.setText(text);
		item.setToolTipText(origText);
		
//		tabFolder.redraw();
	}
	
	public int addTab(String text)
	{
		int id = ++staticId;
		
		tabsText.put(id, text);
		
		String origText = text;
		
		if (text.length() > maxCharacters)
		{
			text = text.substring(0, maxCharacters - 3) + "...";
		}
		
		CTabItem wid = new CTabItem(widthFolder, SWT.CLOSE);
		wid.setText(text);
		
		CTabItem item = new CTabItem(tabFolder, SWT.CLOSE);
		item.setText(text);
		item.setToolTipText(origText);
		
		item.addListener(SWT.MouseDown, new Listener()
		{
			public void handleEvent(Event event)
			{
				System.out.println("ASDf");
			}
		});
		
		addWidth(wid.getBounds().width);
		
		wid.dispose();
		
		tabs.put(id, item);
		tabIds.put(item, id);
		
		tabFolder.setSelection(item);
		
//		tabFolder.redraw();
		
//		setWidth(maxWidth);
		
		return id;
	}
	
	public void closeTab(int id)
	{
		closeTab(tabs.get(id));
	}
	
	public void closeTab(CTabItem item)
	{
		int id = tabIds.get(item);
		
		tabs.remove(id);
		tabIds.remove(item);
		tabsText.remove(id);
		
		item.dispose();
		
		subtractWidth(item.getBounds().width);
		
		TabMenuEvent e = new TabMenuEvent(thisObject, new Point(0, 0), id, 0, false, false);
		
		for (int i = listeners.size() - 1; i >= 0; i--)
		{
			listeners.get(i).tabClosed(e);
		}
	}
	
	public int getSelection()
	{
		CTabItem item = tabFolder.getSelection();
		
		if (tabIds.containsKey(item))
		{
			return tabIds.get(item);
		}
		
		return -1;
	}
	
	public void setSelection(int id)
	{
		CTabItem tab = tabs.get(id);
		
		tabFolder.setSelection(tab);
		
		TabMenuEvent e = new TabMenuEvent(thisObject, new Point(0, 0), id, 0, false, false);
		
		for (int i = listeners.size() - 1; i >= 0; i--)
		{
			listeners.get(i).tabSelected(e);
		}
	}
	
	public Control getControl()
	{
		return tabFolder;
	}
	
	public Color getBackground()
	{
		return tabFolder.getBackground();
	}
	
	public void setBackground(Color color)
	{
		tabFolder.setBackground(color);
	}
	
	public int getHeight()
	{
		return tabFolder.getSize().y;
	}
	
	private void setWidth(int width)
	{
		tabFolder.setSize(width, tabFolder.getSize().y);
	}
	
	private void addWidth(int width)
	{
		int wid = tabFolder.getSize().x + width;
		
		wid = wid > maxWidth ? maxWidth : wid;
		
		tabFolder.setSize(wid, tabFolder.getSize().y);
	}
	
	private void subtractWidth(int width)
	{
		int wid = tabFolder.getSize().x - width;
		
		tabFolder.setSize(wid, tabFolder.getSize().y);
	}
	
	public void setLocation(int x, int y)
	{
		tabFolder.setLocation(x, y);
	}
	
	public int getX()
	{
		return tabFolder.getBounds().x;
	}
	
	public int getY()
	{
		return tabFolder.getBounds().y;
	}
	
	public void setMaxCharacters(int maxCharacters)
	{
		this.maxCharacters = maxCharacters;
	}
	
	public void addListener(TabMenuListener listener)
	{
		listeners.add(listener);
	}
	
	public int getNumTabs()
	{
		return tabs.size();
	}
	
	private class CTabMenu extends CTabFolder
	{
		public CTabMenu(Composite parent, int style)
		{
			super(parent, style);
			
//			super.
		}
		
		
	}
}