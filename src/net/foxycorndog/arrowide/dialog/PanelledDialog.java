package net.foxycorndog.arrowide.dialog;

import static net.foxycorndog.arrowide.ArrowIDE.CONFIG_DATA;

import java.util.HashMap;

import net.foxycorndog.arrowide.ArrowIDE;
import net.foxycorndog.arrowide.components.TitleBar;
import net.foxycorndog.arrowide.components.treemenu.TreeMenu;
import net.foxycorndog.arrowide.components.treemenu.TreeMenuListener;
import net.foxycorndog.arrowide.components.window.Window;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public abstract class PanelledDialog implements Dialog
{
	private int								currentPanelId;

	private Composite						contentPanel;
	private Composite						parent;

	private Button							apply, ok, cancel;

	private TitleBar						titleBar;

	private Window							window;

	private TreeMenu						treeMenu;

	private HashMap<Integer, DialogPanel>	panels;
	private HashMap<DialogPanel, Integer>	ids;

	private static PanelledDialog			thisDialog;
	
	public PanelledDialog(Composite parent)
	{
		this.parent = parent;
		
		panels      = new HashMap<Integer, DialogPanel>();
		ids         = new HashMap<DialogPanel, Integer>();
		
		createWindow();
		
		currentPanelId = -1;
		
		if (thisDialog == null)
		{
			thisDialog = this;
		}
	}
	
	public void addDialogPanel(DialogPanel panel)
	{
		int id = treeMenu.addItem(panel.getTitle());
		
		panels.put(id, panel);
		ids.put(panel, id);
	}
	
	public void removeDialogPanel(DialogPanel panel)
	{
		int id = ids.get(panel);
		
		treeMenu.removeItem(id);
		
		panels.remove(id);
		ids.remove(panel);
	}
	
	public void setVisible(DialogPanel panel, boolean visible)
	{
		int id = ids.get(panel);
		
		if (visible)
		{
			treeMenu.addItem(panel.getTitle());
		}
		else
		{
			treeMenu.removeItem(id);
		}
	}
	
	public static PanelledDialog getDefault()
	{
		return thisDialog;
	}
	
	private Rectangle getParentBounds()
	{
		Rectangle bounds = parent.getBounds();
		
		bounds.x += parent.getShell().getLocation().x;
		bounds.y += parent.getShell().getLocation().y;
		bounds.x += parent.getLocation().x;
		bounds.y += parent.getLocation().y;
		
		return bounds;
	}
	
	private void centerWindow()
	{
		Rectangle bounds = getParentBounds();
		
		window.setLocation(bounds.width / 2 - window.getSize().x / 2 + bounds.x, bounds.height / 2 - window.getSize().y / 2 + bounds.y);
	}
	
	private void createWindow()
	{
		boolean custom = false;
		
		if (CONFIG_DATA.containsKey("window.custom"))
		{
			custom = Boolean.valueOf(CONFIG_DATA.get("window.custom"));
		}
		
		if (custom)
		{
			window = new Window(Display.getDefault(), true);
		}
		else
		{
			window = new Window(Display.getDefault(), false, SWT.MIN | SWT.CLOSE);
		}
		
		window.setSize(750, 540);
		centerWindow();
		
		int titleBarHeight = 0;
		
		if (custom)
		{
			titleBar = new TitleBar(window, 28, SWT.MIN | SWT.CLOSE);
			titleBar.setBackground(ArrowIDE.TITLE_BAR_BACKGROUND);
			titleBar.setForeground(ArrowIDE.TITLE_BAR_FOREGROUND);
			
			titleBarHeight = titleBar.getHeight();
		}
		
		contentPanel = new Composite(window.getContentPanel(), SWT.NONE);
		contentPanel.setSize(window.getClientArea().width, window.getClientArea().height - titleBarHeight);
		contentPanel.setLocation(0, titleBarHeight);
		
		int width  = contentPanel.getSize().x;
		int height = contentPanel.getSize().y;
		
		window.addListener(SWT.Close, new Listener()
		{
			public void handleEvent(Event event)
			{
				window.setVisible(false);
				
				event.doit = false;
			}
		});
		
		treeMenu = new TreeMenu(contentPanel);
		treeMenu.setSize(150, 440 - titleBarHeight);
		
		treeMenu.addListener(new TreeMenuListener()
		{
			public void treeItemRightClicked(int id)
			{
				
			}
			
			public void treeItemDoubleClicked(int id)
			{
				
			}

			public void treeItemSelected(int id)
			{
				if (panels.containsKey(id))
				{
					panels.get(id).update();
					setActivePanel(id);
				}
			}
		});
		
		Listener buttonListener = new Listener()
		{
			public void handleEvent(Event event)
			{
				if (event.widget == apply)
				{
					DialogPanel panels[] = getPanels();
					
					for (int i = 0; i < panels.length; i ++)
					{
						panels[i].apply();
					}
				}
				else if (event.widget == ok)
				{
					DialogPanel panels[] = getPanels();
					
					for (int i = 0; i < panels.length; i ++)
					{
						panels[i].apply();
					}
					
					window.setVisible(false);
				}
				else if (event.widget == cancel)
				{
					DialogPanel panels[] = getPanels();
					
					for (int i = 0; i < panels.length; i ++)
					{
						panels[i].revert();
					}
					
					window.setVisible(false);
				}
			}
		};
		
		cancel = new Button(contentPanel, SWT.PUSH);
		cancel.setText("Cancel");
		cancel.setSize(100, 25);
		cancel.setLocation(width - cancel.getSize().x - 10, height - 50);
		cancel.addListener(SWT.Selection, buttonListener);
		
		ok = new Button(contentPanel, SWT.PUSH);
		ok.setText("OK");
		ok.setSize(100, 25);
		ok.setLocation(cancel.getLocation().x - ok.getSize().x - 10, height - 50);
		ok.addListener(SWT.Selection, buttonListener);
		
		apply = new Button(contentPanel, SWT.PUSH);
		apply.setText("Apply");
		apply.setSize(100, 25);
		apply.setLocation(ok.getLocation().x - ok.getSize().x - 10, height - 50);
		apply.addListener(SWT.Selection, buttonListener);
	}
	
	public void openToPanel(Class panel)
	{
		DialogPanel panels[] = this.panels.values().toArray(new DialogPanel[0]);
		
		for (int i = 0; i < panels.length; i++)
		{
			if (panels[i].getClass() == panel)
			{
				setActivePanel(ids.get(panels[i]));
				
				break;
			}
		}
		
		open();
	}
	
	private void setActivePanel(int id)
	{
		DialogPanel panels[] = getPanels();
		
		for (int i = 0; i < panels.length; i++)
		{
			panels[i].setVisible(false);
		}
		
		DialogPanel panel = this.panels.get(id);
		
		panel.update();
		panel.open();
		panel.setVisible(true);
		
		currentPanelId = id;
		
		treeMenu.setSelection(id);
	}
	
	private DialogPanel[] getPanels()
	{
		return this.panels.values().toArray(new DialogPanel[0]);
	}

	public String open()
	{
		window.setSize(750, 540);

		if (currentPanelId != -1)
		{
			setActivePanel(currentPanelId);
		}
		
		centerWindow();
		
		window.open();
		
		return null;
	}

	public void setText(String text)
	{

	}

	public Composite getContentPanel()
	{
		return contentPanel;
	}
	
	public Window getWindow()
	{
		return window;
	}
}