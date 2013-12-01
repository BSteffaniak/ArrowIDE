package net.foxycorndog.arrowide.components.toolbar;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public class Toolbar
{
	private Composite									composite;

	private ToolBar										toolbar;

	private Composite									parent;

	private ArrayList<ToolbarListener>					listeners;

	private HashMap<String, ToolItem>					toolItems;
	private HashMap<String, HashMap<String, ToolItem>>	subItems;
	
	public Toolbar(Composite parent)
	{
		this.parent   = parent;
		
		composite    = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(4, false));
		composite.setSize(parent.getClientArea().width, 25);
		
		toolbar      = new ToolBar(composite, SWT.HORIZONTAL);
		toolbar.setSize(100, 250);
		
		toolItems    = new HashMap<String, ToolItem>();
		subItems     = new HashMap<String, HashMap<String, ToolItem>>();
		
		listeners    = new ArrayList<ToolbarListener>();
	}
	
	public void addToolItem(String name)
	{
		addToolItem(name, null);
	}
	
	public void addToolItem(final String name, Image image)
	{
		ToolItem item = new ToolItem(toolbar, SWT.PUSH);
		
		if (image == null)
		{
			item.setText(name);
		}
		else
		{
			item.setImage(image);
		}
		
		toolItems.put(name, item);
		subItems.put(name, new HashMap<String, ToolItem>());
		
		item.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(SelectionEvent e)
			{
				for (int i = listeners.size() - 1; i >= 0; i --)
				{
					listeners.get(i).toolItemPressed(name);
				}
			}

			public void widgetSelected(SelectionEvent e)
			{
				widgetDefaultSelected(e);
			}
		});
	}
	
	public void addSeparator()
	{
		new ToolItem(toolbar, SWT.SEPARATOR);
	}
	
//	public void addMenuSubItem(String subItemName, String headerName)
//	{
//		Menu headerMenu = getHeaderMenu(headerName);
//		
//		MenuItem item   = new MenuItem(headerMenu, SWT.PUSH);
//		item.setText(subItemName);
//		
//		subItems.get(headerName).put(subItemName, item);
//	}
	
	public void setToolItemImage(String itemName, Image image)
	{
//		Image i = new Image
		
		getToolItem(itemName).setImage(image);
	}
	
	public ToolItem getToolItem(String itemName)
	{
		return toolItems.get(itemName);
	}
	
	public void addSelectionListener(String itemName, SelectionListener listener)
	{
		getToolItem(itemName).addSelectionListener(listener);
	}
	
	public int getX()
	{
		return composite.getLocation().x;
	}
	
	public int getY()
	{
		return composite.getLocation().y;
	}
	
	public void setSize(int width, int height)
	{
		composite.setSize(width, height);
	}
	
	public int getWidth()
	{
		return composite.getSize().x;
	}
	
	public void setWidth(int width)
	{
		composite.setSize(width, getHeight());
	}

	public int getHeight()
	{
		return composite.getSize().y;
	}
	
	public void setHeight(int height)
	{
		composite.setSize(getWidth(), height);
	}
	
	public void setLocation(int x, int y)
	{
		composite.setLocation(x, y);
	}
	
	public void addListener(ToolbarListener listener)
	{
		listeners.add(listener);
	}
	
	public void setBackground(Color color)
	{
		composite.setBackground(color);
		toolbar.setBackground(color);
	}
}