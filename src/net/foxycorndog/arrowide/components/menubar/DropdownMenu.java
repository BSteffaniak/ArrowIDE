package net.foxycorndog.arrowide.components.menubar;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import static net.foxycorndog.arrowide.ArrowIDE.DISPLAY;

public class DropdownMenu
{
	private int								textMargin;
	private int								separatorHeight;
	private int								leftMargin, rightMargin;
	private int 							minimumWidth;

	private GC								gc;
	
	private Color							selectionColor, defaultColor, separatorColor;
	
	private DropdownMenu					parent;

	private Composite						contentPanel;
	
	private Listener						selectionListener;
	private Listener						focusListener;
	
	private Shell							shell;

	private HashMap<Control, String>		controlIds;
	private HashMap<String, Control>		controls;
	private HashMap<Composite, String>		compositeIds;
	private HashMap<String, Composite>		composites;
	private ArrayList<Composite>			separators;
	private ArrayList<DropdownMenuListener>	listeners;
	private ArrayList<DropdownMenu>			children;

	private static int						staticId;
	
	private static final int				closeMenu = 12342349;
	
	private static final double				data;
	
	static
	{
		data = 32343.424234;
	}
	
	public DropdownMenu(final Composite comp, DropdownMenu parent)
	{
		this.parent = parent;
		
		final DropdownMenu thisMenu = this;
		
		shell = new Shell(comp.getShell(), SWT.NO_TRIM | SWT.NO_FOCUS | SWT.MODELESS);
		shell.setData(data);
		shell.setSize(0, 0);
		shell.forceActive();
		shell.forceFocus();
		
		selectionColor = new Color(Display.getDefault(), 200, 200, 200);
		defaultColor   = new Color(Display.getDefault(), 234, 234, 234);
		separatorColor = new Color(Display.getDefault(), 180, 180, 180);
		
		contentPanel = new Composite(shell, SWT.NONE);
		contentPanel.setSize(0, 0);
		contentPanel.setBackground(defaultColor);
		contentPanel.setBackgroundMode(SWT.INHERIT_DEFAULT);
		
		controlIds   = new HashMap<Control, String>();
		compositeIds = new HashMap<Composite, String>();
		controls     = new HashMap<String, Control>();
		composites   = new HashMap<String, Composite>();
		separators   = new ArrayList<Composite>();
		listeners    = new ArrayList<DropdownMenuListener>();
		children     = new ArrayList<DropdownMenu>();
		
		gc = new GC(shell);
		
		textMargin = 2;
		
		separatorHeight = 5;
		
		leftMargin   = 25;
		rightMargin  = 100;
		
		minimumWidth = 100;
		
		contentPanel.addControlListener(new ControlListener()
		{
			public void controlResized(ControlEvent e)
			{
				shell.setSize(contentPanel.getSize());
			}
			
			public void controlMoved(ControlEvent e)
			{
				
			}
		});
		
		selectionListener = new Listener()
		{
			public void handleEvent(Event event)
			{
				String text = null;
				
				Control control   = null;
				Control composite = null;
				
				if (controlIds.containsKey(event.widget))
				{
					text      = controlIds.get(event.widget);
					control   = controls.get(text);
					composite = composites.get(text);
				}
				else if (compositeIds.containsKey(event.widget))
				{
					text      = compositeIds.get(event.widget);
					control   = controls.get(text);
					composite = composites.get(text);
				}
				
				if (text == null)
				{
					return;
				}
				
				if (event.type == SWT.MouseUp && event.button == 1)
				{
					closeMenu();
					
					DropdownMenu parent = thisMenu;
					
					while (parent.getParent() != null)
					{
						parent = parent.getParent();
					}
					
					parent.close();
					
					for (int i = listeners.size() - 1; i >= 0; i--)
					{
						listeners.get(i).itemSelected(text);
					}
				}
				else if (event.type == SWT.MouseEnter)
				{
					for (int i = listeners.size() - 1; i >= 0; i--)
					{
						listeners.get(i).itemHovered(text);
					}
					
					resetColors();
				
					control.setBackground(selectionColor);
					composite.setBackground(selectionColor);
				}
			}
		};
		
		focusListener = new Listener()
		{
			int times = 0;
			
			public void handleEvent(Event event)
			{
				if (event.type == SWT.MouseDown)
				{
					++times;
					
					boolean dontClose = false;
					
					if (times > 1)
					{
						if (isOpen())
						{
							if (event.widget instanceof Control)
							{
								Control cont  = (Control)event.widget;
								
								Shell   shell = cont.getShell();
								
								if (shell.getData() instanceof Double)
								{
									if ((Double)shell.getData() == data)
									{
										dontClose = true;
									}
								}
							}
							
							if (!dontClose)
							{
								close();
								
								times = 0;
							}
						}
					}
				}
				else if (event.type == closeMenu)
				{
					close();
					
					times = 0;
				}
			}
		};
		
//		DISPLAY.addFilter(SWT.MouseUp, focusListener);
		DISPLAY.addFilter(SWT.MouseDown, focusListener);
		
		comp.getShell().addControlListener(new ControlListener()
		{
			@Override
			public void controlResized(ControlEvent e)
			{
				closeMenu();
			}
			
			@Override
			public void controlMoved(ControlEvent e)
			{
				closeMenu();
			}
		});
		
		if (parent != null)
		{
			parent.notifyParent(this);
		}
	}
	
	private void notifyParent(DropdownMenu menu)
	{
		children.add(menu);
	}
	
	private void closeMenu()
	{
		Event e2 = new Event();
		
		e2.type = closeMenu;
		
		focusListener.handleEvent(e2);
	}
	
	public void addMenuItem(String text, String id)
	{
		contentPanel.setSize(contentPanel.getSize().x, contentPanel.getSize().y + textMargin);
		
		Point extent = gc.stringExtent(text);
		
		int textWidth  = extent.x;
		int textHeight = extent.y;
		
		Composite comp = new Composite(contentPanel, SWT.NONE);
		comp.setSize(Math.max(Math.max(textWidth, minimumWidth), shell.getSize().x) + leftMargin + rightMargin, textHeight);
		comp.setLocation(0, contentPanel.getSize().y);
		comp.addListener(SWT.MouseUp, selectionListener);
		comp.addListener(SWT.MouseEnter, selectionListener);
		comp.setBackground(defaultColor);
		comp.setBackgroundMode(SWT.INHERIT_DEFAULT);
		
		Label label = new Label(comp, SWT.NONE);
		label.setSize(textWidth, textHeight);
		label.setLocation(leftMargin, 0);
		label.setText(text);
		label.addListener(SWT.MouseUp, selectionListener);
		label.addListener(SWT.MouseEnter, selectionListener);
		label.setBackground(defaultColor);
		
		if (textWidth > contentPanel.getSize().x - leftMargin - rightMargin)
		{
			int newWidth = comp.getSize().x;
			
			contentPanel.setSize(newWidth, contentPanel.getSize().y);
			
			Control values[] = compositeIds.keySet().toArray(new Control[0]);
			
			for (int i = 0; i < values.length; i++)
			{
				values[i].setSize(newWidth, values[i].getSize().y);
			}
		}
		
		controlIds.put(label, id);
		compositeIds.put(comp, id);
		composites.put(id, comp);
		controls.put(id, label);
		
		contentPanel.setSize(contentPanel.getSize().x, contentPanel.getSize().y + textHeight);
	}
	
	public void addSeparator()
	{
		contentPanel.setSize(contentPanel.getSize().x, contentPanel.getSize().y + separatorHeight);
		
		int height = separatorHeight % 2 == 0 ? 2 : 1;
		
		Composite sep = new Composite(contentPanel, SWT.NONE);
		sep.setLocation(0, contentPanel.getSize().y - separatorHeight / 2);
		sep.setBackground(separatorColor);
		sep.setBackgroundMode(SWT.INHERIT_DEFAULT);
		
		separators.add(sep);
		
		for (int i = separators.size() - 1; i >= 0; i--)
		{
			separators.get(i).setSize(contentPanel.getSize().x - 2, height);
		}
	}
	
	private void resetColors()
	{
		Control composites[] = compositeIds.keySet().toArray(new Control[0]);
		Control controls[]   = controlIds.keySet().toArray(new Control[0]);
		
		for (int i = 0; i < composites.length; i++)
		{
			composites[i].setBackground(defaultColor);
			controls[i].setBackground(defaultColor);
		}
	}
	
	public boolean isAncestor(DropdownMenu menu)
	{
		if (menu == this)
		{
			return true;
		}
		
		DropdownMenu parent = menu.getParent();
		
		while (parent != null)
		{
			if (parent == this)
			{
				return true;
			}
			else
			{
				parent = parent.getParent();
			}
		}
		
		return false;
	}
	
	public Point getLocation()
	{
		return shell.getLocation();
	}
	
	public Point getLocation(String id)
	{
		return controls.get(id).getLocation();
	}
	
	public void setLocation(int x, int y)
	{
		shell.setLocation(x, y);
	}
	
	public Point getSize()
	{
		return shell.getSize();
	}
	
	public boolean isOpen()
	{
		return shell.isVisible();
	}
	
	public void open()
	{
		shell.open();
	}
	
	public void close()
	{
		for (int i = 0; i < children.size(); i++)
		{
			children.get(i).closeMenu();
		}
		
		resetColors();
		
		shell.setVisible(false);
	}
	
	public void dispose()
	{
		shell.dispose();
	}
	
	public void addDropdownMenuListener(DropdownMenuListener listener)
	{
		listeners.add(listener);
	}
	
	public DropdownMenu getParent()
	{
		return parent;
	}
}
